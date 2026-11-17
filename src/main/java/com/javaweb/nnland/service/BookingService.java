package com.javaweb.nnland.service;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.utils.Rules;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
  private final Support s;
  private final ScheduleService schedule;

  public BookingService(Support s, ScheduleService schedule) {
    this.s = s;
    this.schedule = schedule;
  }

  public Row quote(Row actor, long unitId, Long leaseId, String startText, String monthText) {
    Row u = s.unit(unitId, false);
    int months =
        Rules.integer(
            monthText == null || monthText.isEmpty() ? "1" : monthText, 1, 24, "Số tháng thuê");
    LocalDate start =
        startText == null || startText.isEmpty() ? s.today().plusDays(1) : Rules.date(startText);
    Row lease = null;
    if (leaseId != null) {
      lease = s.lease(leaseId);
      Rules.check(
          actor != null && actor.id() == lease.num("account_id"),
          "Hợp đồng không thuộc tài khoản này.");
      Rules.check(
          lease.num("unit_id") == unitId
              && Arrays.asList("ACTIVE", "RESERVED").contains(lease.str("status")),
          "Hợp đồng không thể gia hạn.");
      start = lease.date("end_date");
      Rules.check(
          !start.isBefore(s.today()),
          "Hợp đồng đã quá hạn. Vui lòng liên hệ quản lý để quyết toán hoặc gia hạn.");
    }
    Rules.check(
        !start.isBefore(s.today()) && !start.isAfter(s.today().plusYears(1)),
        "Ngày nhận căn hộ phải trong 12 tháng tới.");
    long monthly = lease == null ? u.num("monthly_rent") : lease.num("monthly_rent");
    long deposit = lease == null ? u.num("deposit_amount") : 0;
    Row fees = lease == null ? u : lease;
    Row policy = s.termsSnapshot();
    Row quote =
        new Row()
            .with("unit", u)
            .with("lease", lease)
            .with("start", start)
            .with("end", start.plusMonths(months))
            .with("months", months)
            .with("monthly", monthly)
            .with("rent", Rules.multiply(monthly, months))
            .with("deposit", deposit)
            .with("total", Math.addExact(Rules.multiply(monthly, months), deposit))
            .with(
                "available",
                u.str("operational_status").equals("READY")
                    && schedule.available(
                        unitId, start, start.plusMonths(months), 0, leaseId == null ? 0 : leaseId))
            .with("terms", policy.str("terms"))
            .with("terms_version", policy.str("terms_version"));
    for (String fee : Arrays.asList("electricity_rate", "water_rate", "service_fee", "parking_fee"))
      quote.put(fee, fees.num(fee));
    String fingerprint =
        monthly
            + ":"
            + deposit
            + ":"
            + quote.num("electricity_rate")
            + ":"
            + quote.num("water_rate")
            + ":"
            + quote.num("service_fee")
            + ":"
            + quote.num("parking_fee")
            + ":"
            + quote.str("terms_version")
            + ":"
            + quote.str("terms");
    quote.put("fee_signature", Rules.fingerprint(fingerprint));
    return quote;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public long create(
      Row actor,
      long unitId,
      Long leaseId,
      String start,
      String months,
      long quotedMonthly,
      long quotedDeposit,
      String termsVersion,
      String feeSignature,
      boolean accepted) {
    Rules.check(accepted, "Bạn cần đọc và đồng ý điều khoản thuê.");
    Row u = s.unit(unitId, true);
    Rules.check(
        leaseId != null
            || (u.bool("published")
                && s.db.count(
                        "select count(*) from nn_buildings where id=? and published=true",
                        u.num("building_id"))
                    == 1),
        "Căn hộ đang tạm ngừng nhận đơn mới.");
    Row q = quote(actor, unitId, leaseId, start, months);
    Rules.check(
        q.bool("available"),
        "Căn hộ không còn trống trong khoảng ngày đã chọn. Vui lòng chọn kỳ thuê khác.");
    Rules.check(
        q.num("monthly") == quotedMonthly
            && q.num("deposit") == quotedDeposit
            && q.str("terms_version").equals(termsVersion)
            && q.str("fee_signature").equals(feeSignature),
        "Giá hoặc điều khoản đã thay đổi. Hãy xem lại thông tin trước khi xác nhận.");
    Rules.check(
        s.db.count(
                "select count(*) from nn_bookings where account_id=? and status in"
                    + " ('HOLD','PAYMENT_REVIEW') and hold_until>?",
                actor.id(),
                s.now())
            < 3,
        "Bạn đang có 3 đơn giữ chỗ. Hãy hoàn tất hoặc hủy đơn trước khi đặt thêm.");
    if (leaseId != null)
      Rules.check(
          s.db.count(
                  "select count(*) from nn_lease_requests where lease_id=? and status='OPEN'",
                  leaseId)
              == 0,
          "Hợp đồng đang có yêu cầu trả phòng/hủy cần được xử lý trước.");
    long booking =
        s.db.insert(
            "insert into"
                + " nn_bookings(code,account_id,unit_id,lease_id,kind,start_date,end_date,months,monthly_rent,rent_total,deposit_total,electricity_rate,water_rate,service_fee,parking_fee,status,hold_until,terms_version,terms_text,accepted_at,created_at)"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'HOLD',?,?,?,?,?)",
            Rules.code("DK"),
            actor.id(),
            unitId,
            leaseId,
            leaseId == null ? "NEW" : "EXTENSION",
            q.get("start"),
            q.get("end"),
            q.get("months"),
            q.num("monthly"),
            q.num("rent"),
            q.num("deposit"),
            q.num("electricity_rate"),
            q.num("water_rate"),
            q.num("service_fee"),
            q.num("parking_fee"),
            s.now().plusMinutes(s.settingNumber("hold_minutes", 30)),
            termsVersion,
            q.str("terms"),
            s.now(),
            s.now());
    long payment =
        s.db.insert(
            "insert into nn_payments(code,account_id,booking_id,type,due_amount,created_at)"
                + " values(?,?,?,?,?,?)",
            Rules.code("TT"),
            actor.id(),
            booking,
            leaseId == null ? "RENT" : "EXTENSION",
            q.num("total"),
            s.now());
    s.audit(
        actor,
        "CREATE_HOLD",
        "BOOKING",
        booking,
        "Giữ chỗ " + q.get("start") + " → " + q.get("end"));
    return payment;
  }

  /** Called inside the receipt transaction while the unit and booking rows are locked. */
  boolean activate(Row booking, Row payment, Row actor) {
    if ("CONFIRMED".equals(booking.str("status"))) return true;
    if (booking.get("lease_id") != null
        && s.db.count(
                "select count(*) from nn_lease_requests where lease_id=? and status='OPEN'",
                booking.num("lease_id"))
            > 0) return false;
    if (Arrays.asList("CANCELLED", "CANCEL_REQUESTED").contains(booking.str("status")))
      return false;
    Row unit = s.unit(booking.num("unit_id"), false);
    if (booking.date("end_date").isBefore(s.today().plusDays(1))
        || ("NEW".equals(booking.str("kind")) && booking.date("start_date").isBefore(s.today())))
      return false;
    if (!unit.str("operational_status").equals("READY")
        || !schedule.available(
            unit.id(),
            booking.date("start_date"),
            booking.date("end_date"),
            booking.id(),
            "EXTENSION".equals(booking.str("kind")) ? booking.num("lease_id") : 0)) return false;
    long leaseId;
    if ("EXTENSION".equals(booking.str("kind"))) {
      Row lease =
          s.db.one("select * from nn_leases where id=? for update", booking.num("lease_id"));
      if (lease == null
          || !Arrays.asList("ACTIVE", "RESERVED").contains(lease.str("status"))
          || !lease.date("end_date").equals(booking.date("start_date"))) return false;
      s.db.update(
          "update nn_leases set end_date=?,reminder_sent_at=null where id=?",
          booking.date("end_date"),
          lease.id());
      leaseId = lease.id();
    } else {
      leaseId =
          s.db.insert(
              "insert into"
                  + " nn_leases(code,account_id,unit_id,initial_booking_id,start_date,end_date,original_end_date,monthly_rent,deposit_held,electricity_rate,water_rate,service_fee,parking_fee,terms_text,created_at)"
                  + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
              Rules.code("HD"),
              booking.num("account_id"),
              unit.id(),
              booking.id(),
              booking.date("start_date"),
              booking.date("end_date"),
              booking.date("end_date"),
              booking.num("monthly_rent"),
              booking.num("deposit_total"),
              booking.num("electricity_rate"),
              booking.num("water_rate"),
              booking.num("service_fee"),
              booking.num("parking_fee"),
              booking.str("terms_text"),
              s.now());
    }
    if ("NEW".equals(booking.str("kind"))) {
      Row customer = s.db.one("select * from nn_accounts where id=?", booking.num("account_id"));
      Row property =
          s.db.one(
              "select u.name unit_label,b.name building_label,b.address from nn_units u join"
                  + " nn_buildings b on b.id=u.building_id where u.id=?",
              unit.id());
      s.db.update(
          "update nn_leases set"
              + " customer_name=?,customer_email=?,customer_phone=?,unit_label=?,building_label=?,property_address=?"
              + " where id=?",
          customer.str("full_name"),
          customer.str("email"),
          customer.str("phone"),
          property.str("unit_label"),
          property.str("building_label"),
          property.str("address"),
          leaseId);
    }
    s.db.update(
        "update nn_bookings set status='CONFIRMED',confirmed_at=?,lease_id=? where id=?",
        s.now(),
        leaseId,
        booking.id());
    s.db.update("update nn_payments set status='PAID' where id=?", payment.id());
    Row customer = s.db.one("select * from nn_accounts where id=?", booking.num("account_id"));
    String message =
        "Đã xác nhận đủ tiền cho đơn "
            + booking.str("code")
            + ". "
            + ("EXTENSION".equals(booking.str("kind"))
                ? "Kỳ thuê được gia hạn đến " + booking.date("end_date") + "."
                : "Căn hộ đã được đặt trước. Hợp đồng chờ ký trực tiếp khi bàn giao.");
    s.notify(customer.id(), "Thanh toán được xác nhận", message, "/tai-khoan/hop-dong/" + leaseId);
    s.mail(customer.str("email"), "NNland · Xác nhận thanh toán và hợp đồng", message, leaseId);
    s.audit(actor, "CONFIRM_BOOKING", "BOOKING", booking.id(), "Tạo/cập nhật hợp đồng " + leaseId);
    return true;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void checkIn(Row actor, long leaseId, String electricity, String water, boolean signed) {
    Row hint = s.lease(leaseId);
    s.buildingAccess(actor, hint.num("building_id"));
    Row unit = s.unit(hint.num("unit_id"), true);
    Row lease = s.db.one("select * from nn_leases where id=? for update", leaseId);
    Rules.check(
        lease.str("status").equals("RESERVED"), "Chỉ hợp đồng chờ nhận phòng mới được bàn giao.");
    Rules.check(
        s.db.count(
                "select count(*) from nn_lease_requests where lease_id=? and status='OPEN'",
                leaseId)
            == 0,
        "Cần xử lý yêu cầu hủy đang chờ trước khi bàn giao căn hộ.");
    Rules.check(signed, "Cần xác nhận khách đã ký hợp đồng giấy.");
    Rules.check(
        !s.today().isBefore(lease.date("start_date")) && s.today().isBefore(lease.date("end_date")),
        "Chưa đến ngày nhận phòng hoặc kỳ thuê đã hết hạn.");
    Rules.check(
        unit.str("operational_status").equals("READY"), "Căn hộ đang bảo trì, chưa thể bàn giao.");
    Rules.check(
        s.db.count(
                "select count(*) from nn_leases where unit_id=? and status in ('ACTIVE','ENDING')"
                    + " and id<>?",
                unit.id(),
                leaseId)
            == 0,
        "Căn hộ còn hợp đồng chưa hoàn tất trả phòng.");
    java.math.BigDecimal e = Rules.meter(electricity), w = Rules.meter(water);
    Rules.check(
        e.compareTo(unit.decimal("electricity_meter")) >= 0
            && w.compareTo(unit.decimal("water_meter")) >= 0,
        "Chỉ số bàn giao không được nhỏ hơn chỉ số đã ghi nhận của căn hộ.");
    s.db.update(
        "update nn_leases set"
            + " status='ACTIVE',signed_at=?,checked_in_at=?,electricity_start=?,water_start=? where"
            + " id=?",
        s.now(),
        s.now(),
        e,
        w,
        leaseId);
    s.db.update(
        "update nn_units set electricity_meter=?,water_meter=? where id=?", e, w, unit.id());
    s.notify(
        lease.num("account_id"),
        "Đã bàn giao căn hộ",
        "Hợp đồng đã được ký và căn hộ đã được bàn giao. Chúc bạn có thời gian sống dễ chịu tại"
            + " NNland.",
        "/tai-khoan/hop-dong/" + leaseId);
    s.audit(
        actor, "CHECK_IN", "LEASE", leaseId, "Ký hợp đồng giấy; chỉ số điện " + e + ", nước " + w);
  }
}
