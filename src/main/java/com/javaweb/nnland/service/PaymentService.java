package com.javaweb.nnland.service;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.utils.Rules;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
  private final Support s;
  private final BookingService bookings;

  public PaymentService(Support s, BookingService bookings) {
    this.s = s;
    this.bookings = bookings;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void claim(Row actor, long id, String note) {
    Row hint = s.payment(id);
    Rules.check(
        hint.num("account_id") == actor.id(), "Yêu cầu thanh toán không thuộc tài khoản này.");
    s.unit(hint.num("unit_id"), true);
    Row p = s.db.one("select * from nn_payments where id=? for update", id);
    Rules.check(
        !Arrays.asList("PAID", "REFUNDED", "CANCELLED", "REFUND_PENDING").contains(p.str("status")),
        "Yêu cầu này đã hoàn tất hoặc đã hủy.");
    if (p.get("claimed_at") == null) {
      s.db.update(
          "update nn_payments set claimed_at=?,customer_note=? where id=?",
          s.now(),
          Rules.text(note, 1000, "Thông tin chuyển khoản"),
          id);
      if (p.get("booking_id") != null) {
        Row b = s.db.one("select * from nn_bookings where id=? for update", p.num("booking_id"));
        if (Arrays.asList("HOLD", "PAYMENT_REVIEW").contains(b.str("status"))
            && b.time("hold_until").isAfter(s.now()))
          s.db.update(
              "update nn_bookings set status='PAYMENT_REVIEW',hold_until=? where id=?",
              s.now().plusHours(24),
              b.id());
        else {
          s.db.update(
              "update nn_bookings set status='EXPIRED' where id=? and status in"
                  + " ('HOLD','PAYMENT_REVIEW')",
              b.id());
          s.db.update("update nn_payments set status='RECONCILE' where id=?", id);
        }
      }
      s.audit(
          actor,
          "REPORT_TRANSFER",
          "PAYMENT",
          id,
          "Khách báo chuyển khoản; chưa xác nhận nhận tiền.");
    } else {
      s.db.update(
          "update nn_payments set customer_note=? where id=?",
          Rules.text(note, 1000, "Thông tin chuyển khoản"),
          id);
      s.audit(
          actor,
          "UPDATE_TRANSFER_NOTE",
          "PAYMENT",
          id,
          "Bổ sung thông tin; không kéo dài giữ chỗ.");
    }
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public long receive(
      Row actor,
      long id,
      String amountText,
      String bankReference,
      String receivedText,
      String note) {
    Row hint = s.payment(id);
    s.buildingAccess(actor, hint.num("building_id"));
    s.unit(hint.num("unit_id"), true);
    Row p = s.db.one("select * from nn_payments where id=? for update", id);
    long amount = Rules.amount(amountText, "Tiền thực nhận");
    Rules.check(amount > 0, "Tiền thực nhận phải lớn hơn 0.");
    String ref = Rules.text(bankReference, 100, "Mã giao dịch ngân hàng").toUpperCase(Locale.ROOT);
    Rules.check(
        ref.matches("[A-Z0-9._/-]{4,100}"),
        "Mã ngân hàng gồm 4–100 ký tự chữ, số, dấu chấm, gạch ngang hoặc gạch chéo.");
    Row duplicate = s.db.one("select * from nn_receipts where bank_reference=?", ref);
    if (duplicate != null) {
      Rules.check(
          duplicate.num("payment_id") == id && duplicate.num("amount") == amount,
          "Mã ngân hàng đã được ghi nhận cho giao dịch khác. Không thể ghi nhận hai lần.");
      return duplicate.id();
    }
    Rules.check(
        s.db.count("select count(*) from nn_refunds where bank_reference=?", ref) == 0,
        "Mã ngân hàng đã được dùng cho một khoản hoàn tiền.");
    LocalDateTime received = s.now();
    if (receivedText != null && !receivedText.trim().isEmpty())
      try {
        received = LocalDateTime.parse(receivedText);
      } catch (Exception e) {
        throw new BusinessException("Thời điểm nhận tiền không hợp lệ.");
      }
    Rules.check(
        !received.isAfter(s.now().plusMinutes(5))
            && !received.isBefore(p.time("created_at").minusDays(1)),
        "Thời điểm nhận tiền phải từ lúc lập yêu cầu (cho phép lệch 1 ngày) đến hiện tại.");
    s.db.update(
        "insert into nn_bank_references(bank_reference,event_type,created_at)"
            + " values(?,'RECEIPT',?)",
        ref,
        s.now());
    long receipt =
        s.db.insert(
            "insert into"
                + " nn_receipts(code,payment_id,amount,bank_reference,received_at,confirmed_by,note,created_at)"
                + " values(?,?,?,?,?,?,?,?)",
            Rules.code("PT"),
            id,
            amount,
            ref,
            received,
            actor.id(),
            Rules.optional(note, 1000),
            s.now());
    settle(p, actor);
    long excess = Math.max(0, s.received(id) - p.num("due_amount"));
    long allocated =
        s.db.count(
            "select coalesce(sum(amount),0) from nn_refunds where payment_id=? and"
                + " category='OVERPAY' and status in ('PENDING','COMPLETED')",
            id);
    if (excess > allocated)
      refundInternal(
          actor,
          p,
          excess - allocated,
          "OVERPAY",
          "Hoàn phần chuyển thừa so với số tiền cần thanh toán.");
    s.audit(
        actor,
        "RECORD_RECEIPT",
        "PAYMENT",
        id,
        "Phiếu thu " + receipt + "; mã ngân hàng " + ref + "; thực nhận " + amount);
    s.notify(
        p.num("account_id"),
        "Đã ghi nhận chuyển khoản",
        "NNland đã đối chiếu và ghi nhận "
            + amount
            + " đồng cho mã thanh toán "
            + p.str("code")
            + ".",
        "/thanh-toan/" + id);
    return receipt;
  }

  private void settle(Row payment, Row actor) {
    long paid = s.received(payment.id());
    if (payment.get("booking_id") != null) {
      Row b =
          s.db.one("select * from nn_bookings where id=? for update", payment.num("booking_id"));
      boolean cancelled = Arrays.asList("CANCELLED", "CANCEL_REQUESTED").contains(b.str("status"));
      boolean ended =
          b.get("lease_id") != null
              && s.db.count(
                      "select count(*) from nn_leases where id=? and status in"
                          + " ('CLOSED','CANCELLED')",
                      b.num("lease_id"))
                  > 0;
      boolean cancelRequested =
          s.db.count(
                  "select count(*) from nn_lease_requests where booking_id=? and type='CANCEL' and"
                      + " status='OPEN'",
                  b.id())
              > 0;
      if (cancelled || cancelRequested || ended) {
        s.db.update("update nn_payments set status='RECONCILE' where id=?", payment.id());
        return;
      }
      if (paid >= payment.num("due_amount")) {
        boolean valid = bookings.activate(b, payment, actor);
        if (!valid)
          s.db.update("update nn_payments set status='RECONCILE' where id=?", payment.id());
      } else {
        s.db.update("update nn_payments set status='PARTIAL' where id=?", payment.id());
        if (!b.date("start_date").isBefore(s.today())
            && Arrays.asList("HOLD", "PAYMENT_REVIEW").contains(b.str("status"))
            && b.time("hold_until").isAfter(s.now()))
          s.db.update(
              "update nn_bookings set status='PAYMENT_REVIEW',hold_until=? where id=?",
              s.now().plusHours(24),
              b.id());
        else s.db.update("update nn_payments set status='RECONCILE' where id=?", payment.id());
      }
    } else {
      Row invoice =
          s.db.one("select * from nn_invoices where id=? for update", payment.num("invoice_id"));
      if (invoice == null || "VOID".equals(invoice.str("status"))) {
        s.db.update("update nn_payments set status='RECONCILE' where id=?", payment.id());
        return;
      }
      boolean full = paid >= payment.num("due_amount");
      s.db.update(
          "update nn_payments set status=? where id=?", full ? "PAID" : "PARTIAL", payment.id());
      s.db.update(
          "update nn_invoices set status=? where id=?", full ? "PAID" : "UNPAID", invoice.id());
      if (full && !"PAID".equals(invoice.str("status"))) {
        Row customer =
            s.db.one("select email from nn_accounts where id=?", payment.num("account_id"));
        s.mail(
            customer.str("email"),
            "NNland · Xác nhận thanh toán hóa đơn",
            "Hóa đơn "
                + invoice.str("code")
                + " đã được thanh toán đầy đủ. Bạn có thể tải phiếu thu trong tài khoản NNland.",
            null);
      }
    }
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void reconcile(Row actor, long id) {
    Row hint = s.payment(id);
    s.buildingAccess(actor, hint.num("building_id"));
    s.unit(hint.num("unit_id"), true);
    Row p = s.db.one("select * from nn_payments where id=? for update", id);
    Rules.check(s.received(id) >= p.num("due_amount"), "Chưa nhận đủ tiền để xác nhận đơn.");
    settle(p, actor);
    Rules.check(
        "PAID".equals(s.db.one("select status from nn_payments where id=?", id).str("status")),
        "Chưa thể xác nhận: lịch căn hộ, ngày thuê hoặc yêu cầu hủy đang cần xử lý. Tiền đã nhận"
            + " vẫn được lưu để đối soát/hoàn.");
    s.audit(actor, "RECONCILE_PAYMENT", "PAYMENT", id, "Xác nhận sau đối soát");
  }

  long refundInternal(Row actor, Row payment, long amount, String category, String reason) {
    Rules.check(
        amount > 0 && amount <= s.received(payment.id()) - s.refunded(payment.id(), true),
        "Số tiền hoàn vượt số tiền thực nhận còn có thể hoàn.");
    return s.db.insert(
        "insert into nn_refunds(payment_id,amount,category,reason,created_by,created_at)"
            + " values(?,?,?,?,?,?)",
        payment.id(),
        amount,
        category,
        Rules.text(reason, 1000, "Lý do hoàn tiền"),
        actor.id(),
        s.now());
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public long requestRefund(
      Row actor, long paymentId, String amountText, String category, String reason) {
    Row hint = s.payment(paymentId);
    s.buildingAccess(actor, hint.num("building_id"));
    s.unit(hint.num("unit_id"), true);
    Row p = s.db.one("select * from nn_payments where id=? for update", paymentId);
    long amount = Rules.amount(amountText, "Tiền hoàn");
    Rules.choice(category, "RENT", "DEPOSIT", "OVERPAY");
    if ("OVERPAY".equals(category)) {
      long max =
          Math.max(0, s.received(paymentId) - p.num("due_amount"))
              - s.db.count(
                  "select coalesce(sum(amount),0) from nn_refunds where payment_id=? and"
                      + " category='OVERPAY' and status in ('PENDING','COMPLETED')",
                  paymentId);
      Rules.check(amount <= max, "Khoản hoàn chuyển thừa vượt phần tiền chuyển thừa còn lại.");
    } else {
      Rules.check(
          p.get("booking_id") != null, "Hóa đơn điện nước chỉ có thể hoàn phần chuyển thừa.");
      Row b = s.db.one("select * from nn_bookings where id=?", p.num("booking_id"));
      Row lease =
          b.get("lease_id") == null
              ? null
              : s.db.one("select * from nn_leases where id=?", b.num("lease_id"));
      Rules.check(
          "CANCELLED".equals(b.str("status"))
              || (lease != null
                  && Arrays.asList("CLOSED", "CANCELLED").contains(lease.str("status"))),
          "Chỉ hoàn tiền thuê/cọc sau khi hủy hoặc quyết toán hợp đồng.");
      long base =
          "RENT".equals(category)
              ? b.num("rent_total")
              : Math.max(
                  0, b.num("deposit_total") - (lease == null ? 0 : lease.num("deposit_deduction")));
      long previous =
          s.db.count(
              "select coalesce(sum(amount),0) from nn_refunds where payment_id=? and category=? and"
                  + " status in ('PENDING','COMPLETED')",
              paymentId,
              category);
      Rules.check(amount <= base - previous, "Số tiền hoàn vượt khoản mục còn lại.");
    }
    long id = refundInternal(actor, p, amount, category, reason);
    s.audit(actor, "REQUEST_REFUND", "REFUND", id, "Khoản mục " + category + "; số tiền " + amount);
    return id;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void completeRefund(Row actor, long id, String bankReference) {
    Row hint = s.db.one("select * from nn_refunds where id=?", id);
    Rules.check(hint != null, "Không tìm thấy khoản hoàn.");
    Row pay = s.payment(hint.num("payment_id"));
    s.buildingAccess(actor, pay.num("building_id"));
    s.unit(pay.num("unit_id"), true);
    Row p = s.db.one("select * from nn_payments where id=? for update", pay.id());
    Row refund = s.db.one("select * from nn_refunds where id=? for update", id);
    String ref = Rules.text(bankReference, 100, "Mã chuyển khoản hoàn").toUpperCase(Locale.ROOT);
    Rules.check(ref.matches("[A-Z0-9._/-]{4,100}"), "Mã ngân hàng không hợp lệ.");
    if ("COMPLETED".equals(refund.str("status"))) {
      Rules.check(
          ref.equals(refund.str("bank_reference")), "Khoản hoàn này đã được xác nhận trước đó.");
      return;
    }
    Rules.check(
        s.db.count("select count(*) from nn_bank_references where bank_reference=?", ref) == 0,
        "Mã ngân hàng đã được sử dụng.");
    Rules.check(
        s.refunded(p.id(), false) + refund.num("amount") <= s.received(p.id()),
        "Tổng hoàn vượt tiền thực nhận.");
    s.db.update(
        "insert into nn_bank_references(bank_reference,event_type,created_at) values(?,'REFUND',?)",
        ref,
        s.now());
    s.db.update(
        "update nn_refunds set status='COMPLETED',bank_reference=?,processed_by=?,processed_at=?"
            + " where id=?",
        ref,
        actor.id(),
        s.now(),
        id);
    if (s.refunded(p.id(), false) == s.received(p.id()))
      s.db.update("update nn_payments set status='REFUNDED' where id=?", p.id());
    s.notify(
        p.num("account_id"),
        "Đã chuyển tiền hoàn",
        "NNland đã chuyển hoàn " + refund.num("amount") + " đồng. Mã giao dịch: " + ref,
        "/thanh-toan/" + p.id());
    s.audit(actor, "COMPLETE_REFUND", "REFUND", id, "Đã chuyển hoàn; mã ngân hàng " + ref);
  }

  /** All refunds are queued, never marked transferred without a bank reference. */
  void refundCancelledBooking(Row actor, Row booking) {
    Row p = s.db.one("select * from nn_payments where booking_id=? for update", booking.id());
    if (p == null) return;
    long left = s.received(p.id()) - s.refunded(p.id(), true);
    for (String category : Arrays.asList("RENT", "DEPOSIT", "OVERPAY")) {
      if (left <= 0) break;
      long base =
          "RENT".equals(category)
              ? booking.num("rent_total")
              : "DEPOSIT".equals(category)
                  ? booking.num("deposit_total")
                  : Math.max(0, s.received(p.id()) - p.num("due_amount"));
      long used =
          s.db.count(
              "select coalesce(sum(amount),0) from nn_refunds where payment_id=? and category=? and"
                  + " status in ('PENDING','COMPLETED')",
              p.id(),
              category);
      long part = Math.min(left, Math.max(0, base - used));
      if (part > 0) {
        refundInternal(
            actor, p, part, category, "Hoàn tiền theo yêu cầu hủy đơn đã được quản lý chấp thuận.");
        left -= part;
      }
    }
    s.db.update(
        "update nn_payments set status=? where id=?",
        s.received(p.id()) == 0
            ? "CANCELLED"
            : s.refunded(p.id(), false) == s.received(p.id()) ? "REFUNDED" : "REFUND_PENDING",
        p.id());
  }
}
