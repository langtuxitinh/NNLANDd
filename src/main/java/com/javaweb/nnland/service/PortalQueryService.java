package com.javaweb.nnland.service;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.utils.Rules;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class PortalQueryService {
  private final Support s;

  public PortalQueryService(Support s) {
    this.s = s;
  }

  public List<Row> leases(Row actor) {
    return s.db.list(
        "select l.*,u.name unit_name,u.code unit_code,b.name building_name,u.image from nn_leases l"
            + " join nn_units u on u.id=l.unit_id join nn_buildings b on b.id=u.building_id where"
            + " l.account_id=? order by l.id desc",
        actor.id());
  }

  public List<Row> own(Row actor, String section) {
    switch (section) {
      case "dat-thue":
        return s.db.list(
            "select bo.*,u.name unit_name,u.code unit_code,b.name building_name,u.image,p.id"
                + " payment_id,p.status payment_status,p.due_amount,(select coalesce(sum(amount),0)"
                + " from nn_receipts where payment_id=p.id) received from nn_bookings bo join"
                + " nn_units u on u.id=bo.unit_id join nn_buildings b on b.id=u.building_id join"
                + " nn_payments p on p.booking_id=bo.id where bo.account_id=? order by bo.id desc",
            actor.id());
      case "hoa-don":
        return s.db.list(
            "select i.*,u.code unit_code,b.name building_name,p.id payment_id,(select"
                + " coalesce(sum(amount),0) from nn_receipts where payment_id=p.id) received from"
                + " nn_invoices i join nn_units u on u.id=i.unit_id join nn_buildings b on"
                + " b.id=u.building_id join nn_payments p on p.invoice_id=i.id where i.account_id=?"
                + " order by i.id desc",
            actor.id());
      case "ho-tro":
        return s.db.list(
            "select t.*,u.code unit_code,b.name building_name from nn_tickets t join nn_units u on"
                + " u.id=t.unit_id join nn_buildings b on b.id=u.building_id where t.account_id=?"
                + " order by t.id desc",
            actor.id());
      case "yeu-cau":
        return s.db.list(
            "select r.*,u.code unit_code,b.name building_name from nn_lease_requests r left join"
                + " nn_leases l on l.id=r.lease_id left join nn_bookings bo on bo.id=r.booking_id"
                + " join nn_units u on u.id=coalesce(l.unit_id,bo.unit_id) join nn_buildings b on"
                + " b.id=u.building_id where r.account_id=? order by r.id desc",
            actor.id());
      case "lich-hen":
        return s.db.list(
            "select c.*,u.name unit_name from nn_contacts c left join nn_units u on u.id=c.unit_id"
                + " where c.account_id=? order by c.id desc",
            actor.id());
      case "thong-bao":
        return s.db.list(
            "select * from nn_notifications where account_id=? order by id desc limit 200",
            actor.id());
      default:
        return new ArrayList<>();
    }
  }

  public Row admin(Row actor, String section, Map<String, String> filters) {
    String select, from, scope, search, statusCol = null;
    switch (section) {
      case "toa-nha":
        select = "select b.*,(select count(*) from nn_units where building_id=b.id) unit_count";
        from = " from nn_buildings b";
        scope = s.scope(actor, "b.id");
        search = "concat(b.code,' ',b.name,' ',b.city)";
        break;
      case "can-ho":
        select = "select u.*,b.name building_name,b.city";
        from = " from nn_units u join nn_buildings b on b.id=u.building_id";
        scope = s.scope(actor, "b.id");
        search = "concat(u.code,' ',u.name,' ',b.name)";
        statusCol = "u.operational_status";
        break;
      case "don-thue":
        select =
            "select bo.*,a.full_name,u.code unit_code,b.name building_name,p.id payment_id,p.status"
                + " payment_status";
        from =
            " from nn_bookings bo join nn_accounts a on a.id=bo.account_id join nn_units u on"
                + " u.id=bo.unit_id join nn_buildings b on b.id=u.building_id join nn_payments p on"
                + " p.booking_id=bo.id";
        scope = s.scope(actor, "b.id");
        search = "concat(bo.code,' ',a.full_name,' ',a.email,' ',u.code)";
        statusCol = "bo.status";
        break;
      case "thanh-toan":
        select =
            "select p.*,a.full_name,u.code unit_code,b.name building_name,(select"
                + " coalesce(sum(amount),0) from nn_receipts where payment_id=p.id) received";
        from =
            " from nn_payments p join nn_accounts a on a.id=p.account_id left join nn_bookings bo"
                + " on bo.id=p.booking_id left join nn_invoices i on i.id=p.invoice_id join"
                + " nn_units u on u.id=coalesce(bo.unit_id,i.unit_id) join nn_buildings b on"
                + " b.id=u.building_id";
        scope = s.scope(actor, "b.id");
        search = "concat(p.code,' ',a.full_name,' ',a.email,' ',u.code)";
        statusCol = "p.status";
        break;
      case "hop-dong":
        select = "select l.*,a.full_name,u.code unit_code,b.name building_name";
        from =
            " from nn_leases l join nn_accounts a on a.id=l.account_id join nn_units u on"
                + " u.id=l.unit_id join nn_buildings b on b.id=u.building_id";
        scope = s.scope(actor, "b.id");
        search = "concat(l.code,' ',a.full_name,' ',u.code)";
        statusCol = "l.status";
        break;
      case "hoa-don":
        select = "select i.*,a.full_name,u.code unit_code,b.name building_name,p.id payment_id";
        from =
            " from nn_invoices i join nn_accounts a on a.id=i.account_id join nn_units u on"
                + " u.id=i.unit_id join nn_buildings b on b.id=u.building_id join nn_payments p on"
                + " p.invoice_id=i.id";
        scope = s.scope(actor, "b.id");
        search = "concat(i.code,' ',a.full_name,' ',u.code,' ',i.period)";
        statusCol = "i.status";
        break;
      case "ho-tro":
        select = "select t.*,a.full_name,u.code unit_code,b.name building_name";
        from =
            " from nn_tickets t join nn_accounts a on a.id=t.account_id join nn_units u on"
                + " u.id=t.unit_id join nn_buildings b on b.id=u.building_id";
        scope = s.scope(actor, "b.id");
        search = "concat(t.code,' ',t.subject,' ',a.full_name)";
        statusCol = "t.status";
        break;
      case "lich-hen":
        select = "select c.*,u.name unit_name,b.name building_name";
        from =
            " from nn_contacts c left join nn_units u on u.id=c.unit_id left join nn_buildings b on"
                + " b.id=u.building_id";
        scope = s.admin(actor) ? "1=1" : s.scope(actor, "b.id");
        search = "concat(c.full_name,' ',c.email,' ',c.phone)";
        statusCol = "c.status";
        break;
      case "yeu-cau":
        select = "select r.*,a.full_name,u.code unit_code,b.name building_name";
        from =
            " from nn_lease_requests r join nn_accounts a on a.id=r.account_id left join nn_leases"
                + " l on l.id=r.lease_id left join nn_bookings bo on bo.id=r.booking_id join"
                + " nn_units u on u.id=coalesce(l.unit_id,bo.unit_id) join nn_buildings b on"
                + " b.id=u.building_id";
        scope = s.scope(actor, "b.id");
        search = "concat(a.full_name,' ',u.code,' ',r.type)";
        statusCol = "r.status";
        break;
      case "hoan-tien":
        select = "select f.*,p.code payment_code,a.full_name,u.code unit_code,b.name building_name";
        from =
            " from nn_refunds f join nn_payments p on p.id=f.payment_id join nn_accounts a on"
                + " a.id=p.account_id left join nn_bookings bo on bo.id=p.booking_id left join"
                + " nn_invoices i on i.id=p.invoice_id join nn_units u on"
                + " u.id=coalesce(bo.unit_id,i.unit_id) join nn_buildings b on b.id=u.building_id";
        scope = s.scope(actor, "b.id");
        search = "concat(p.code,' ',a.full_name,' ',f.reason)";
        statusCol = "f.status";
        break;
      case "tai-khoan":
        s.requireAdmin(actor);
        select =
            "select"
                + " a.id,a.email,a.username,a.full_name,a.birth_year,a.phone,a.role,a.active,a.created_at";
        from = " from nn_accounts a";
        scope = "1=1";
        search = "concat(a.email,' ',a.username,' ',a.full_name)";
        statusCol = "a.role";
        break;
      case "thu-di":
        s.requireAdmin(actor);
        select = "select m.*";
        from = " from nn_mail_outbox m";
        scope = "1=1";
        search = "concat(m.recipient,' ',m.subject)";
        statusCol = "m.status";
        break;
      case "nhat-ky":
        s.requireAdmin(actor);
        select = "select e.*,a.full_name actor_name";
        from = " from nn_audit_log e left join nn_accounts a on a.id=e.actor_id";
        scope = "1=1";
        search = "concat(e.action,' ',e.entity_type,' ',coalesce(e.details,''))";
        break;
      default:
        throw new BusinessException("Mục quản lý không tồn tại.");
    }
    List<Object> args = new ArrayList<>();
    String where = " where " + scope;
    String q = Rules.optional(filters.get("q"), 100),
        status = Rules.optional(filters.get("status"), 40);
    if (!q.isEmpty()) {
      where += " and " + search + " like ?";
      args.add("%" + q + "%");
    }
    if (!status.isEmpty() && statusCol != null) {
      where += " and " + statusCol + "=?";
      args.add(status);
    }
    if (!filters.getOrDefault("building", "").isEmpty()
        && !Arrays.asList("tai-khoan", "thu-di", "nhat-ky").contains(section)) {
      long id = Rules.amount(filters.get("building"), "Tòa nhà");
      s.buildingAccess(actor, id);
      where += " and b.id=?";
      args.add(id);
    }
    long total = s.db.count("select count(*)" + from + where, args.toArray());
    int pages = (int) Math.max(1, (total + 24) / 25);
    int page =
        Math.min(pages, Rules.integer(filters.getOrDefault("page", "1"), 1, 1000000, "Trang"));
    String alias =
        Arrays.asList("toa-nha").contains(section)
            ? "b"
            : "can-ho".equals(section)
                ? "u"
                : "don-thue".equals(section)
                    ? "bo"
                    : "thanh-toan".equals(section)
                        ? "p"
                        : "hop-dong".equals(section)
                            ? "l"
                            : "hoa-don".equals(section)
                                ? "i"
                                : "ho-tro".equals(section)
                                    ? "t"
                                    : "lich-hen".equals(section)
                                        ? "c"
                                        : "yeu-cau".equals(section)
                                            ? "r"
                                            : "hoan-tien".equals(section)
                                                ? "f"
                                                : "tai-khoan".equals(section)
                                                    ? "a"
                                                    : "thu-di".equals(section) ? "m" : "e";
    args.add(25);
    args.add((page - 1) * 25);
    return new Row()
        .with(
            "items",
            s.db.list(
                select + from + where + " order by " + alias + ".id desc limit ? offset ?",
                args.toArray()))
        .with("total", total)
        .with("page", page)
        .with("pages", pages);
  }
}
