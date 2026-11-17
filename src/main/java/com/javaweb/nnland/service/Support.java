package com.javaweb.nnland.service;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.repository.Store;
import com.javaweb.nnland.utils.Rules;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class Support {
  public final Store db;
  public final Clock clock;

  public Support(Store db, Clock clock) {
    this.db = db;
    this.clock = clock;
  }

  public LocalDate today() {
    return LocalDate.now(clock);
  }

  public LocalDateTime now() {
    return LocalDateTime.now(clock);
  }

  public Row user() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    if (a == null || !a.isAuthenticated() || "anonymousUser".equals(a.getName())) return null;
    return db.one("select * from nn_accounts where email=? and active=true", a.getName());
  }

  public Row requireUser() {
    Row u = user();
    if (u == null) throw new AccessDeniedException("Vui lòng đăng nhập.");
    return u;
  }

  public boolean staff(Row u) {
    return u != null && Arrays.asList("ADMIN", "MANAGER").contains(u.str("role"));
  }

  public boolean admin(Row u) {
    return u != null && "ADMIN".equals(u.str("role"));
  }

  public void requireAdmin(Row u) {
    if (!admin(u))
      throw new AccessDeniedException("Chỉ quản trị viên được thực hiện thao tác này.");
  }

  public String scope(Row actor, String field) {
    if (!staff(actor)) throw new AccessDeniedException("Không có quyền quản lý.");
    if (admin(actor)) return "1=1";
    List<Row> rows =
        db.list("select building_id from nn_building_staff where account_id=?", actor.id());
    return field
        + " in (-1"
        + rows.stream().map(r -> "," + r.num("building_id")).collect(Collectors.joining())
        + ")";
  }

  public void buildingAccess(Row actor, long building) {
    if (!staff(actor)
        || (!admin(actor)
            && db.count(
                    "select count(*) from nn_building_staff where account_id=? and building_id=?",
                    actor.id(),
                    building)
                == 0))
      throw new AccessDeniedException("Bạn không được phân công quản lý tòa nhà này.");
  }

  public Row unit(long id, boolean lock) {
    Row row = db.one("select * from nn_units where id=?" + (lock ? " for update" : ""), id);
    Rules.check(row != null, "Không tìm thấy căn hộ.");
    return row;
  }

  public Row lease(long id) {
    Row row =
        db.one(
            "select l.*,u.building_id,u.code unit_code,u.name unit_name,b.name"
                + " building_name,b.city,b.address,a.full_name,a.email,a.phone from nn_leases l"
                + " join nn_units u on u.id=l.unit_id join nn_buildings b on b.id=u.building_id"
                + " join nn_accounts a on a.id=l.account_id where l.id=?",
            id);
    Rules.check(row != null, "Không tìm thấy hợp đồng.");
    return row;
  }

  public void leaseAccess(Row actor, Row lease) {
    if (actor != null && actor.id() == lease.num("account_id")) return;
    buildingAccess(actor, lease.num("building_id"));
  }

  public Row payment(long id) {
    Row p =
        db.one(
            "select p.*,coalesce(b.unit_id,i.unit_id)"
                + " unit_id,coalesce(u.building_id,iu.building_id)"
                + " building_id,coalesce(u.name,iu.name)"
                + " unit_name,a.full_name,a.email,a.phone,b.kind"
                + " booking_kind,b.start_date,b.end_date,b.status booking_status,b.hold_until from"
                + " nn_payments p join nn_accounts a on a.id=p.account_id left join nn_bookings b"
                + " on b.id=p.booking_id left join nn_units u on u.id=b.unit_id left join"
                + " nn_invoices i on i.id=p.invoice_id left join nn_units iu on iu.id=i.unit_id"
                + " where p.id=?",
            id);
    Rules.check(p != null, "Không tìm thấy yêu cầu thanh toán.");
    p.put("received", received(id));
    p.put("refunded", refunded(id, false));
    p.put("remaining", Math.max(0, p.num("due_amount") - p.num("received")));
    return p;
  }

  public void paymentAccess(Row actor, Row p) {
    if (actor != null && actor.id() == p.num("account_id")) return;
    buildingAccess(actor, p.num("building_id"));
  }

  public long received(long payment) {
    return db.count("select coalesce(sum(amount),0) from nn_receipts where payment_id=?", payment);
  }

  public long refunded(long payment, boolean includePending) {
    return db.count(
        "select coalesce(sum(amount),0) from nn_refunds where payment_id=? and status "
            + (includePending ? "in ('PENDING','COMPLETED')" : "='COMPLETED'"),
        payment);
  }

  public String setting(String key, String fallback) {
    Row r = db.one("select setting_value from nn_settings where setting_key=?", key);
    return r == null ? fallback : r.str("setting_value");
  }

  public long settingNumber(String key, long fallback) {
    try {
      return Long.parseLong(setting(key, String.valueOf(fallback)));
    } catch (Exception e) {
      return fallback;
    }
  }

  public Map<String, String> settings() {
    Map<String, String> map = new LinkedHashMap<>();
    for (Row r : db.list("select * from nn_settings"))
      map.put(r.str("setting_key"), r.str("setting_value"));
    return map;
  }

  public void set(String key, String value) {
    if (db.update(
            "update nn_settings set setting_value=?,updated_at=? where setting_key=?",
            value,
            now(),
            key)
        == 0)
      db.update(
          "insert into nn_settings(setting_key,setting_value,updated_at) values(?,?,?)",
          key,
          value,
          now());
  }

  public void audit(Row actor, String action, String entity, Long id, String details) {
    db.insert(
        "insert into nn_audit_log(actor_id,action,entity_type,entity_id,details,created_at)"
            + " values(?,?,?,?,?,?)",
        actor == null ? null : actor.id(),
        action,
        entity,
        id,
        details,
        now());
  }

  public void notify(long account, String title, String body, String link) {
    db.insert(
        "insert into nn_notifications(account_id,title,body,link,created_at) values(?,?,?,?,?)",
        account,
        title,
        body,
        link,
        now());
  }

  public void mail(String recipient, String subject, String body, Long lease) {
    db.insert(
        "insert into nn_mail_outbox(recipient,subject,body,attachment_lease_id,created_at)"
            + " values(?,?,?,?,?)",
        recipient,
        subject,
        body,
        lease,
        now());
  }

  public String terms() {
    return termsSnapshot().str("terms");
  }

  /** Read text and version together so the customer accepts one coherent policy snapshot. */
  public Row termsSnapshot() {
    Row policy =
        db.one(
            "select max(case when setting_key='terms_text' then setting_value end) terms, "
                + "max(case when setting_key='terms_version' then setting_value end) terms_version "
                + "from nn_settings where setting_key in ('terms_text','terms_version')");
    if (policy.get("terms") == null)
      policy.put(
          "terms",
          "NNland cho thuê nguyên căn. Khách thanh toán trước toàn bộ tiền thuê theo số tháng đã"
              + " chọn. Điện, nước và phí dịch vụ thanh toán theo hóa đơn riêng. Việc giữ chỗ có"
              + " thời hạn; đơn thuê chỉ được xác nhận sau khi quản lý đối chiếu tiền thực nhận."
              + " Hợp đồng được ký trực tiếp khi bàn giao. Gia hạn cần kiểm tra lịch trống và thanh"
              + " toán trước. Hủy, trả sớm và hoàn tiền được hai bên xác nhận bằng văn bản; tiền"
              + " cọc được quyết toán khi trả phòng.");
    if (policy.get("terms_version") == null) policy.put("terms_version", "NN-2026-01");
    return policy;
  }
}
