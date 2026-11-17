package com.javaweb.nnland.service;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.utils.Rules;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
public class InvoiceService {
  private final Support s;

  public InvoiceService(Support s) {
    this.s = s;
  }

  public Row last(long lease) {
    return s.db.one(
        "select * from nn_invoices where lease_id=? and status<>'VOID' order by id desc limit 1",
        lease);
  }

  public String nextFinalPeriod(long lease) {
    String base = YearMonth.from(s.today()) + "-FINAL";
    Row latest =
        s.db.one(
            "select period from nn_invoices where lease_id=? and period like ? order by period desc"
                + " limit 1",
            lease,
            base + "%");
    if (latest == null) return base;
    String period = latest.str("period");
    int sequence =
        period.equals(base) ? 1 : Integer.parseInt(period.substring(base.length() + 1)) + 1;
    Rules.check(sequence <= 999, "Đã vượt số hóa đơn bổ sung trong tháng. Liên hệ quản trị.");
    return base + "-" + String.format(Locale.ROOT, "%03d", sequence);
  }

  public boolean monthFeesCharged(long lease) {
    return s.db.count(
            "select count(*) from nn_invoices where lease_id=? and period like ? and (service_fee>0"
                + " or parking_fee>0)",
            lease,
            YearMonth.from(s.today()) + "%")
        > 0;
  }

  public Row invoice(long id) {
    Row r =
        s.db.one(
            "select i.*,l.code lease_code,u.name unit_name,u.code unit_code,u.building_id,b.name"
                + " building_name,b.address,a.full_name,a.email,a.phone,p.id payment_id,p.status"
                + " payment_status,(select coalesce(sum(amount),0) from nn_receipts where"
                + " payment_id=p.id) received from nn_invoices i join nn_leases l on"
                + " l.id=i.lease_id join nn_units u on u.id=i.unit_id join nn_buildings b on"
                + " b.id=u.building_id join nn_accounts a on a.id=i.account_id join nn_payments p"
                + " on p.invoice_id=i.id where i.id=?",
            id);
    Rules.check(r != null, "Không tìm thấy hóa đơn.");
    return r;
  }

  private long charge(BigDecimal quantity, long rate) {
    return quantity
        .multiply(BigDecimal.valueOf(rate))
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact();
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public long create(Row actor, long leaseId, Map<String, String> form) {
    Row hint = s.lease(leaseId);
    s.buildingAccess(actor, hint.num("building_id"));
    s.unit(hint.num("unit_id"), true);
    Row lease = s.db.one("select * from nn_leases where id=? for update", leaseId);
    Rules.check(
        Arrays.asList("ACTIVE", "ENDING").contains(lease.str("status")),
        "Chỉ lập hóa đơn cho hợp đồng đã nhận phòng và chưa quyết toán.");
    String period = Rules.text(form.get("period"), 20, "Kỳ hóa đơn");
    Rules.check(
        period.matches("[0-9]{4}-(0[1-9]|1[0-2])(-FINAL(-[0-9]{3})?)?"),
        "Kỳ hóa đơn có dạng YYYY-MM, YYYY-MM-FINAL hoặc YYYY-MM-FINAL-001 (bổ sung).");
    YearMonth month = YearMonth.parse(period.substring(0, 7));
    Rules.check(
        !month.isBefore(YearMonth.from(lease.date("start_date")))
            && !month.isAfter(YearMonth.from(s.today())),
        "Kỳ hóa đơn phải từ tháng nhận phòng đến tháng hiện tại.");
    Rules.check(
        s.db.count(
                "select count(*) from nn_invoices where lease_id=? and period=?", leaseId, period)
            == 0,
        "Kỳ này đã có hóa đơn. Hãy kiểm tra trước khi lập thêm.");
    LocalDate reading = Rules.date(form.get("reading_date")),
        due = Rules.date(form.get("due_date"));
    Rules.check(
        !reading.isAfter(s.today()) && !reading.isBefore(lease.date("start_date")),
        "Ngày chốt chỉ số không hợp lệ.");
    Rules.check(YearMonth.from(reading).equals(month), "Kỳ hóa đơn phải khớp tháng chốt chỉ số.");
    Rules.check(
        !due.isBefore(reading) && !due.isAfter(s.today().plusDays(60)),
        "Hạn thanh toán phải sau ngày chốt và không quá 60 ngày tới.");
    Row previous = last(leaseId);
    if (previous != null) {
      Rules.check(
          !reading.isBefore(previous.date("reading_date")),
          "Ngày chốt không được trước hóa đơn gần nhất.");
      Rules.check(
          period.compareTo(previous.str("period")) > 0,
          "Hãy lập hóa đơn theo thứ tự kỳ, hóa đơn FINAL đặt sau kỳ thông thường.");
    }
    BigDecimal
        es =
            previous == null
                ? lease.decimal("electricity_start")
                : previous.decimal("electricity_end"),
        ws = previous == null ? lease.decimal("water_start") : previous.decimal("water_end");
    BigDecimal ee = Rules.meter(form.get("electricity_end")),
        we = Rules.meter(form.get("water_end"));
    Rules.check(
        ee.compareTo(es) >= 0 && we.compareTo(ws) >= 0,
        "Chỉ số cuối phải lớn hơn hoặc bằng chỉ số đầu.");
    long service = Rules.amount(form.get("service_fee"), "Phí dịch vụ"),
        parking = Rules.amount(form.get("parking_fee"), "Phí gửi xe");
    String note = Rules.optional(form.get("note"), 2000);
    long adjustment;
    try {
      adjustment = Long.parseLong(form.getOrDefault("adjustment", "0"));
    } catch (Exception e) {
      throw new BusinessException("Điều chỉnh phải là số nguyên đồng.");
    }
    Rules.check(
        adjustment >= -1000000000L && adjustment <= 1000000000L, "Khoản điều chỉnh vượt giới hạn.");
    if (adjustment != 0) Rules.check(!note.isEmpty(), "Khoản điều chỉnh cần có diễn giải.");
    if (s.db.count(
            "select count(*) from nn_invoices where lease_id=? and period like ? and (service_fee>0"
                + " or parking_fee>0)",
            leaseId,
            period.substring(0, 7) + "%")
        > 0)
      Rules.check(
          service == 0 && parking == 0,
          "Phí dịch vụ/gửi xe đã được tính trong tháng này; hóa đơn bổ sung chỉ tính phần phát"
              + " sinh.");
    long electric = charge(ee.subtract(es), lease.num("electricity_rate")),
        water = charge(we.subtract(ws), lease.num("water_rate"));
    long total = electric + water + service + parking + adjustment;
    Rules.check(total >= 0 && total <= 100000000000L, "Tổng hóa đơn không hợp lệ.");
    long id =
        s.db.insert(
            "insert into"
                + " nn_invoices(code,lease_id,account_id,unit_id,period,reading_date,due_date,electricity_start,electricity_end,water_start,water_end,electricity_rate,water_rate,electricity_amount,water_amount,service_fee,parking_fee,adjustment,note,total,status,created_by,created_at)"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            Rules.code("DN"),
            leaseId,
            lease.num("account_id"),
            lease.num("unit_id"),
            period,
            reading,
            due,
            es,
            ee,
            ws,
            we,
            lease.num("electricity_rate"),
            lease.num("water_rate"),
            electric,
            water,
            service,
            parking,
            adjustment,
            note,
            total,
            total == 0 ? "PAID" : "UNPAID",
            actor.id(),
            s.now());
    s.db.insert(
        "insert into nn_payments(code,account_id,invoice_id,type,due_amount,status,created_at)"
            + " values(?,?,?,'UTILITY',?,?,?)",
        Rules.code("TT"),
        lease.num("account_id"),
        id,
        total,
        total == 0 ? "PAID" : "PENDING",
        s.now());
    s.db.update(
        "update nn_units set electricity_meter=?,water_meter=? where id=?",
        ee,
        we,
        lease.num("unit_id"));
    s.notify(
        lease.num("account_id"),
        "Hóa đơn điện nước mới",
        "Hóa đơn kỳ "
            + period
            + " đã được phát hành. Vui lòng xem chỉ số và chi tiết các khoản phí.",
        "/hoa-don/" + id);
    Row user = s.db.one("select email from nn_accounts where id=?", lease.num("account_id"));
    s.mail(
        user.str("email"),
        "NNland · Hóa đơn kỳ " + period,
        "Hóa đơn mới có tổng tiền "
            + total
            + " đồng, hạn thanh toán "
            + due
            + ". Vui lòng đăng nhập NNland để xem chi tiết.",
        null);
    s.audit(actor, "CREATE_INVOICE", "INVOICE", id, "Kỳ " + period + "; tổng " + total);
    return id;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void correct(Row actor, long id, Map<String, String> form) {
    Row hint = invoice(id);
    s.buildingAccess(actor, hint.num("building_id"));
    s.unit(hint.num("unit_id"), true);
    Row current = s.db.one("select * from nn_invoices where id=? for update", id),
        lease = s.db.one("select * from nn_leases where id=? for update", hint.num("lease_id"));
    Row payment = s.db.one("select * from nn_payments where invoice_id=? for update", id);
    Rules.check(
        Arrays.asList("ACTIVE", "ENDING").contains(lease.str("status")),
        "Hợp đồng đã kết thúc, không thể sửa hóa đơn.");
    Rules.check(last(lease.id()).id() == id, "Chỉ sửa hóa đơn mới nhất để giữ đúng chuỗi chỉ số.");
    Rules.check(
        s.received(payment.id()) == 0,
        "Hóa đơn đã có tiền thực nhận; không được sửa số tiền hoặc chỉ số.");
    String reason = Rules.text(form.get("correction_reason"), 1000, "Lý do điều chỉnh hóa đơn");
    BigDecimal ee = Rules.meter(form.get("electricity_end")),
        we = Rules.meter(form.get("water_end"));
    Rules.check(
        ee.compareTo(current.decimal("electricity_start")) >= 0
            && we.compareTo(current.decimal("water_start")) >= 0,
        "Chỉ số cuối phải lớn hơn hoặc bằng chỉ số đầu.");
    long service = Rules.amount(form.get("service_fee"), "Phí dịch vụ"),
        parking = Rules.amount(form.get("parking_fee"), "Phí gửi xe"),
        adjustment;
    try {
      adjustment = Long.parseLong(form.getOrDefault("adjustment", "0"));
    } catch (Exception e) {
      throw new BusinessException("Điều chỉnh phải là số nguyên đồng.");
    }
    Rules.check(
        adjustment >= -1000000000L && adjustment <= 1000000000L, "Điều chỉnh vượt giới hạn.");
    if (s.db.count(
            "select count(*) from nn_invoices where lease_id=? and id<>? and period like ? and"
                + " (service_fee>0 or parking_fee>0)",
            lease.id(),
            id,
            current.str("period").substring(0, 7) + "%")
        > 0)
      Rules.check(
          service == 0 && parking == 0, "Phí dịch vụ/gửi xe đã có trong hóa đơn khác của tháng.");
    long
        electric =
            charge(
                ee.subtract(current.decimal("electricity_start")), current.num("electricity_rate")),
        water = charge(we.subtract(current.decimal("water_start")), current.num("water_rate")),
        total = electric + water + service + parking + adjustment;
    Rules.check(total >= 0 && total <= 100000000000L, "Tổng hóa đơn không hợp lệ.");
    LocalDate due = Rules.date(form.get("due_date"));
    Rules.check(
        !due.isBefore(current.date("reading_date")) && !due.isAfter(s.today().plusDays(60)),
        "Hạn thanh toán không hợp lệ.");
    String note = Rules.optional(form.get("note"), 2000) + "\nĐiều chỉnh: " + reason;
    s.db.update(
        "update nn_invoices set"
            + " electricity_end=?,water_end=?,electricity_amount=?,water_amount=?,service_fee=?,parking_fee=?,adjustment=?,note=?,total=?,due_date=?,status=?"
            + " where id=?",
        ee,
        we,
        electric,
        water,
        service,
        parking,
        adjustment,
        note,
        total,
        due,
        total == 0 ? "PAID" : "UNPAID",
        id);
    s.db.update(
        "update nn_payments set due_amount=?,status=? where id=?",
        total,
        total == 0 ? "PAID" : "PENDING",
        payment.id());
    s.db.update(
        "update nn_units set electricity_meter=?,water_meter=? where id=?",
        ee,
        we,
        hint.num("unit_id"));
    s.notify(
        current.num("account_id"),
        "Hóa đơn đã được điều chỉnh",
        current.str("code") + ": " + reason,
        "/hoa-don/" + id);
    s.audit(
        actor,
        "CORRECT_UNPAID_INVOICE",
        "INVOICE",
        id,
        "Tổng cũ " + current.num("total") + " → tổng mới " + total + "; " + reason);
  }
}
