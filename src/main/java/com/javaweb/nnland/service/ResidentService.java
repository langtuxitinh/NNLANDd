package com.javaweb.nnland.service;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.utils.Rules;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
public class ResidentService {
  private final Support s;
  private final PaymentService payments;

  public ResidentService(Support s, PaymentService payments) {
    this.s = s;
    this.payments = payments;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public long contact(Row actor, Map<String, String> form) {
    String name = Rules.text(form.get("full_name"), 150, "Họ tên"),
        email = Rules.email(form.get("email")),
        phone = Rules.phone(form.get("phone")),
        message = Rules.text(form.get("message"), 3000, "Nội dung");
    Long unit = null;
    if (form.get("unit_id") != null && !form.get("unit_id").isEmpty()) {
      unit = Rules.amount(form.get("unit_id"), "Căn hộ");
      s.unit(unit, false);
    }
    LocalDate date = null;
    if (form.get("preferred_date") != null && !form.get("preferred_date").isEmpty()) {
      date = Rules.date(form.get("preferred_date"));
      Rules.check(
          !date.isBefore(s.today()) && !date.isAfter(s.today().plusDays(90)),
          "Ngày hẹn phải trong 90 ngày tới.");
    }
    Rules.check(
        s.db.count(
                "select count(*) from nn_contacts where email=? and created_at>?",
                email,
                s.now().minusHours(1))
            < 5,
        "Bạn đã gửi nhiều yêu cầu trong giờ qua. Vui lòng chờ quản lý phản hồi.");
    long id =
        s.db.insert(
            "insert into"
                + " nn_contacts(account_id,unit_id,full_name,email,phone,preferred_date,preferred_time,message,created_at)"
                + " values(?,?,?,?,?,?,?,?,?)",
            actor == null ? null : actor.id(),
            unit,
            name,
            email,
            phone,
            date,
            Rules.optional(form.get("preferred_time"), 40),
            message,
            s.now());
    if (actor != null)
      s.notify(
          actor.id(),
          "Đã tiếp nhận liên hệ",
          "NNland đã nhận thông tin tư vấn/xem căn hộ của bạn.",
          "/tai-khoan/lich-hen");
    return id;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void handleContact(Row actor, long id, String status, String note) {
    Row row = s.db.one("select * from nn_contacts where id=? for update", id);
    Rules.check(row != null, "Không tìm thấy liên hệ.");
    if (row.get("unit_id") == null) s.requireAdmin(actor);
    else s.buildingAccess(actor, s.unit(row.num("unit_id"), false).num("building_id"));
    Rules.choice(status, "NEW", "CONFIRMED", "COMPLETED", "CANCELLED");
    note = Rules.text(note, 2000, "Nội dung phản hồi");
    s.db.update(
        "update nn_contacts set status=?,manager_note=?,handled_by=? where id=?",
        status,
        note,
        actor.id(),
        id);
    s.mail(row.str("email"), "NNland · Phản hồi lịch hẹn/liên hệ", note, null);
    if (row.get("account_id") != null)
      s.notify(row.num("account_id"), "Lịch hẹn đã được cập nhật", note, "/tai-khoan/lich-hen");
    s.audit(actor, "HANDLE_CONTACT", "CONTACT", id, status);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public long ticket(
      Row actor,
      long leaseId,
      String category,
      String subject,
      String description,
      String priority) {
    Row lease = s.lease(leaseId);
    Rules.check(lease.num("account_id") == actor.id(), "Hợp đồng không thuộc tài khoản này.");
    Rules.check(
        Arrays.asList("ACTIVE", "ENDING").contains(lease.str("status")),
        "Chỉ tạo yêu cầu hỗ trợ trong thời gian lưu trú.");
    Rules.choice(category, "ELECTRIC", "WATER", "FURNITURE", "INTERNET", "OTHER");
    Rules.choice(priority, "NORMAL", "URGENT");
    long id =
        s.db.insert(
            "insert into"
                + " nn_tickets(code,account_id,lease_id,unit_id,category,subject,description,priority,created_at,updated_at)"
                + " values(?,?,?,?,?,?,?,?,?,?)",
            Rules.code("HT"),
            actor.id(),
            leaseId,
            lease.num("unit_id"),
            category,
            Rules.text(subject, 180, "Tiêu đề"),
            Rules.text(description, 4000, "Mô tả"),
            priority,
            s.now(),
            s.now());
    event(id, actor, "OPEN", description);
    s.audit(actor, "CREATE_TICKET", "TICKET", id, category);
    return id;
  }

  private void event(long ticket, Row actor, String status, String body) {
    s.db.insert(
        "insert into nn_ticket_events(ticket_id,actor_id,status,body,created_at) values(?,?,?,?,?)",
        ticket,
        actor.id(),
        status,
        Rules.text(body, 4000, "Nội dung phản hồi"),
        s.now());
  }

  public Row ticket(long id) {
    Row t =
        s.db.one(
            "select t.*,u.building_id,u.code unit_code,u.name unit_name,b.name"
                + " building_name,a.full_name from nn_tickets t join nn_units u on u.id=t.unit_id"
                + " join nn_buildings b on b.id=u.building_id join nn_accounts a on"
                + " a.id=t.account_id where t.id=?",
            id);
    Rules.check(t != null, "Không tìm thấy yêu cầu hỗ trợ.");
    return t;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void updateTicket(
      Row actor, long id, String status, String assignee, String technician, String note) {
    Row t = ticket(id);
    s.buildingAccess(actor, t.num("building_id"));
    Rules.choice(status, "OPEN", "IN_PROGRESS", "WAITING", "RESOLVED", "CLOSED");
    Long assigned = null;
    if (assignee != null && !assignee.isEmpty()) {
      assigned = Rules.amount(assignee, "Người phụ trách");
      Row a = s.db.one("select * from nn_accounts where id=? and active=true", assigned);
      Rules.check(a != null && s.staff(a), "Người phụ trách không hợp lệ.");
      s.buildingAccess(a, t.num("building_id"));
    }
    String tech = Rules.optional(technician, 150);
    note = Rules.text(note, 4000, "Nội dung cập nhật");
    s.db.update(
        "update nn_tickets set"
            + " status=?,assignee_id=?,assigned_to=?,manager_note=?,resolved_at=?,updated_at=?"
            + " where id=?",
        status,
        assigned,
        tech,
        note,
        Arrays.asList("RESOLVED", "CLOSED").contains(status) ? s.now() : null,
        s.now(),
        id);
    event(id, actor, status, note);
    s.notify(
        t.num("account_id"),
        "Yêu cầu hỗ trợ được cập nhật",
        t.str("code") + ": " + note,
        "/tai-khoan/ho-tro/" + id);
    s.audit(actor, "UPDATE_TICKET", "TICKET", id, status);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void replyTicket(Row actor, long id, String body, boolean reopen) {
    Row t = ticket(id);
    Rules.check(t.num("account_id") == actor.id(), "Yêu cầu không thuộc tài khoản này.");
    Rules.check(!"CLOSED".equals(t.str("status")), "Yêu cầu đã đóng. Vui lòng tạo yêu cầu mới.");
    String status = reopen ? "OPEN" : t.str("status");
    if (reopen)
      s.db.update(
          "update nn_tickets set status='OPEN',resolved_at=null,updated_at=? where id=?",
          s.now(),
          id);
    event(id, actor, status, body);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void request(
      Row actor, Long leaseId, Long bookingId, String type, String dateText, String note) {
    Rules.choice(type, "CANCEL", "CHECKOUT");
    Rules.check(
        (leaseId == null) != (bookingId == null), "Chỉ chọn một hợp đồng hoặc một đơn thuê.");
    note = Rules.text(note, 3000, "Lý do/yêu cầu");
    Row lease = leaseId == null ? null : s.lease(leaseId);
    Row booking =
        bookingId == null ? null : s.db.one("select * from nn_bookings where id=?", bookingId);
    Rules.check(lease != null || booking != null, "Thiếu thông tin hợp đồng/đơn thuê.");
    long unit = lease == null ? booking.num("unit_id") : lease.num("unit_id");
    s.unit(unit, true);
    if (leaseId != null) lease = s.lease(leaseId);
    if (bookingId != null)
      booking = s.db.one("select * from nn_bookings where id=? for update", bookingId);
    if (lease != null) {
      Rules.check(lease.num("account_id") == actor.id(), "Hợp đồng không thuộc tài khoản này.");
      Rules.check(
          "CANCEL".equals(type)
              ? "RESERVED".equals(lease.str("status"))
              : "ACTIVE".equals(lease.str("status")),
          "Trạng thái hợp đồng không phù hợp với yêu cầu.");
      Rules.check(
          s.db.count(
                  "select count(*) from nn_lease_requests where lease_id=? and status='OPEN'",
                  lease.id())
              == 0,
          "Hợp đồng đã có yêu cầu đang chờ xử lý.");
    }
    if (booking != null) {
      Rules.check(booking.num("account_id") == actor.id(), "Đơn thuê không thuộc tài khoản này.");
      Rules.check(
          "CANCEL".equals(type) && !"CANCELLED".equals(booking.str("status")),
          "Đơn thuê không thể gửi yêu cầu này.");
      if ("CONFIRMED".equals(booking.str("status"))) {
        lease = s.lease(booking.num("lease_id"));
        leaseId = lease.id();
        Rules.check(
            s.db.count(
                    "select count(*) from nn_lease_requests where lease_id=? and status='OPEN'",
                    leaseId)
                == 0,
            "Hợp đồng đã có yêu cầu đang chờ xử lý.");
        Rules.check(
            "RESERVED".equals(lease.str("status")),
            "Hợp đồng đã nhận phòng. Hãy gửi yêu cầu trả phòng.");
      }
      Rules.check(
          s.db.count(
                  "select count(*) from nn_lease_requests where booking_id=? and status='OPEN'",
                  booking.id())
              == 0,
          "Đơn đã có yêu cầu hủy đang chờ xử lý.");
      Row p = s.db.one("select * from nn_payments where booking_id=?", booking.id());
      if (!"CONFIRMED".equals(booking.str("status"))
          && s.received(p.id()) == 0
          && p.get("claimed_at") == null) {
        s.db.update("update nn_bookings set status='CANCELLED' where id=?", booking.id());
        s.db.update("update nn_payments set status='CANCELLED' where id=?", p.id());
        s.audit(actor, "CANCEL_UNPAID_HOLD", "BOOKING", booking.id(), note);
        return;
      }
    }
    LocalDate date = null;
    if ("CHECKOUT".equals(type)) {
      date = Rules.date(dateText);
      Rules.check(
          !date.isBefore(s.today()) && !date.isAfter(s.today().plusDays(90)),
          "Ngày dự kiến trả phòng phải trong 90 ngày tới.");
      s.db.update("update nn_leases set status='ENDING' where id=?", leaseId);
    }
    long id =
        s.db.insert(
            "insert into"
                + " nn_lease_requests(account_id,lease_id,booking_id,type,requested_date,note,created_at)"
                + " values(?,?,?,?,?,?,?)",
            actor.id(),
            leaseId,
            bookingId,
            type,
            date,
            note,
            s.now());
    s.notify(
        actor.id(),
        "Đã tiếp nhận yêu cầu",
        ("CHECKOUT".equals(type) ? "Yêu cầu trả phòng" : "Yêu cầu hủy")
            + " đang chờ quản lý kiểm tra.",
        "/tai-khoan/yeu-cau");
    s.audit(actor, "CREATE_LEASE_REQUEST", "REQUEST", id, type);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void handleRequest(Row actor, long id, boolean approve, Map<String, String> form) {
    Row hint =
        s.db.one(
            "select r.*,coalesce(l.unit_id,b.unit_id) unit_id from nn_lease_requests r left join"
                + " nn_leases l on l.id=r.lease_id left join nn_bookings b on b.id=r.booking_id"
                + " where r.id=?",
            id);
    Rules.check(hint != null, "Không tìm thấy yêu cầu.");
    Row unit = s.unit(hint.num("unit_id"), true);
    s.buildingAccess(actor, unit.num("building_id"));
    Row r = s.db.one("select * from nn_lease_requests where id=? for update", id);
    Rules.check("OPEN".equals(r.str("status")), "Yêu cầu này đã được xử lý.");
    String note = Rules.text(form.get("manager_note"), 3000, "Nội dung xử lý");
    Row lease =
        r.get("lease_id") == null
            ? null
            : s.db.one("select * from nn_leases where id=? for update", r.num("lease_id"));
    if (approve) {
      if ("CANCEL".equals(r.str("type"))) {
        if (lease != null) {
          Rules.check("RESERVED".equals(lease.str("status")), "Chỉ hủy hợp đồng chưa nhận phòng.");
          s.db.update("update nn_leases set status='CANCELLED' where id=?", lease.id());
          for (Row b : s.db.list("select * from nn_bookings where lease_id=?", lease.id())) {
            s.db.update("update nn_bookings set status='CANCELLED' where id=?", b.id());
            payments.refundCancelledBooking(actor, b);
          }
        } else {
          Row b = s.db.one("select * from nn_bookings where id=? for update", r.num("booking_id"));
          s.db.update("update nn_bookings set status='CANCELLED' where id=?", b.id());
          payments.refundCancelledBooking(actor, b);
        }
      } else {
        Rules.check(
            lease != null && Arrays.asList("ACTIVE", "ENDING").contains(lease.str("status")),
            "Hợp đồng không ở trạng thái lưu trú.");
        Rules.check(
            s.db.count(
                    "select count(*) from nn_invoices where lease_id=? and status='UNPAID'",
                    lease.id())
                == 0,
            "Còn hóa đơn chưa thanh toán. Hãy quyết toán trước khi xác nhận trả phòng.");
        Rules.check(
            Rules.meter(form.get("electricity_final")).compareTo(unit.decimal("electricity_meter"))
                    == 0
                && Rules.meter(form.get("water_final")).compareTo(unit.decimal("water_meter")) == 0,
            "Chỉ số trả phòng khác chỉ số đã lập hóa đơn. Hãy lập và thu hóa đơn cuối kỳ trước.");
        LocalDate actual = Rules.date(form.get("actual_date"));
        Rules.check(
            !actual.isBefore(lease.date("start_date")) && !actual.isAfter(s.today()),
            "Ngày bàn giao thực tế không hợp lệ.");
        Row closingBill =
            s.db.one(
                "select * from nn_invoices where lease_id=? order by id desc limit 1", lease.id());
        Rules.check(
            closingBill != null
                && "PAID".equals(closingBill.str("status"))
                && actual.equals(closingBill.date("reading_date"))
                && closingBill
                    .str("period")
                    .startsWith(java.time.YearMonth.from(actual).toString() + "-FINAL"),
            "Hóa đơn mới nhất phải là FINAL đúng ngày bàn giao thực tế và đã thanh toán trước khi"
                + " quyết toán.");
        long deduction = Rules.amount(form.getOrDefault("deposit_deduction", "0"), "Khấu trừ cọc"),
            rentRefund =
                Rules.amount(form.getOrDefault("rent_refund", "0"), "Hoàn tiền thuê thỏa thuận");
        Rules.check(deduction <= lease.num("deposit_held"), "Khấu trừ vượt tiền cọc đã thu.");
        Rules.choice(form.get("readiness"), "READY", "MAINTENANCE");
        s.db.update(
            "update nn_leases set"
                + " status='CLOSED',checked_out_at=?,checkout_date=?,deposit_deduction=?,checkout_note=?"
                + " where id=?",
            s.now(),
            actual,
            deduction,
            note,
            lease.id());
        s.db.update(
            "update nn_units set operational_status=? where id=?",
            form.get("readiness"),
            unit.id());
        List<Row> confirmed =
            s.db.list(
                "select b.*,p.id payment_id from nn_bookings b join nn_payments p on"
                    + " p.booking_id=b.id where b.lease_id=? and b.status='CONFIRMED' order by b.id"
                    + " desc",
                lease.id());
        long left = rentRefund;
        for (Row b : confirmed) {
          Row pay =
              s.db.one("select * from nn_payments where id=? for update", b.num("payment_id"));
          long used =
              s.db.count(
                  "select coalesce(sum(amount),0) from nn_refunds where payment_id=? and"
                      + " category='RENT' and status in ('PENDING','COMPLETED')",
                  pay.id());
          long part = Math.min(left, Math.max(0, b.num("rent_total") - used));
          if (part > 0) {
            payments.refundInternal(
                actor, pay, part, "RENT", "Hoàn tiền thuê theo biên bản quyết toán: " + note);
            left -= part;
          }
          if (b.num("deposit_total") > 0) {
            long refunded =
                s.db.count(
                    "select coalesce(sum(amount),0) from nn_refunds where payment_id=? and"
                        + " category='DEPOSIT' and status in ('PENDING','COMPLETED')",
                    pay.id());
            long dep = Math.max(0, b.num("deposit_total") - deduction - refunded);
            if (dep > 0)
              payments.refundInternal(
                  actor,
                  pay,
                  dep,
                  "DEPOSIT",
                  "Hoàn cọc sau bàn giao; khấu trừ " + deduction + " đồng. " + note);
          }
        }
        Rules.check(left == 0, "Tiền thuê thỏa thuận hoàn vượt phần tiền thuê có thể hoàn.");
        for (Row b :
            s.db.list(
                "select * from nn_bookings where lease_id=? and status in"
                    + " ('HOLD','PAYMENT_REVIEW','EXPIRED')",
                lease.id())) {
          s.db.update("update nn_bookings set status='CANCELLED' where id=?", b.id());
          payments.refundCancelledBooking(actor, b);
        }
      }
    } else if (lease != null
        && "CHECKOUT".equals(r.str("type"))
        && "ENDING".equals(lease.str("status")))
      s.db.update("update nn_leases set status='ACTIVE' where id=?", lease.id());
    s.db.update(
        "update nn_lease_requests set status=?,manager_note=?,handled_by=?,handled_at=? where id=?",
        approve ? "APPROVED" : "REJECTED",
        note,
        actor.id(),
        s.now(),
        id);
    s.notify(r.num("account_id"), "Yêu cầu đã được xử lý", note, "/tai-khoan/yeu-cau");
    s.audit(actor, approve ? "APPROVE_REQUEST" : "REJECT_REQUEST", "REQUEST", id, note);
  }
}
