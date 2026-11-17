package com.javaweb.nnland.service;

import com.javaweb.nnland.model.Row;
import com.javaweb.nnland.utils.Rules;
import java.math.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
public class FinanceService {
  private final Support s;

  public FinanceService(Support s) {
    this.s = s;
  }

  private String join() {
    return " from nn_payments p left join nn_bookings bo on bo.id=p.booking_id left join"
        + " nn_invoices i on i.id=p.invoice_id join nn_units u on"
        + " u.id=coalesce(bo.unit_id,i.unit_id) join nn_buildings b on b.id=u.building_id ";
  }

  public Row report(Row actor, LocalDate from, LocalDate through, Long building) {
    return report(actor, from, through, building, null, "");
  }

  public Row report(
      Row actor, LocalDate from, LocalDate through, Long building, Long unit, String type) {
    Rules.check(
        !through.isBefore(from) && ChronoUnit.DAYS.between(from, through) <= 366,
        "Khoảng thống kê tối đa 367 ngày và ngày kết thúc không được trước ngày bắt đầu.");
    if (building != null) s.buildingAccess(actor, building);
    if (unit != null) {
      Row selected = s.unit(unit, false);
      s.buildingAccess(actor, selected.num("building_id"));
      Rules.check(
          building == null || building == selected.num("building_id"),
          "Căn hộ không thuộc tòa nhà đã chọn.");
    }
    Rules.choice(type, "", "RENT", "EXTENSION", "UTILITY");
    String scope = s.scope(actor, "b.id") + (building == null ? "" : " and b.id=" + building);
    if (unit != null) scope += " and u.id=" + unit;
    String paymentScope = scope + (type.isEmpty() ? "" : " and p.type='" + type + "'");
    LocalDate end = through.plusDays(1);
    if (end.isAfter(s.today().plusDays(1))) end = s.today().plusDays(1);
    List<Row> receipts =
        s.db.list(
            "select r.*,p.type,p.code"
                + " payment_code,p.account_id,bo.rent_total,bo.deposit_total,i.total"
                + " invoice_total,u.code unit_code,b.name building_name,a.full_name,(select"
                + " coalesce(sum(pr.amount),0) from nn_receipts pr where pr.payment_id=p.id and"
                + " pr.id<r.id) prior_received from nn_receipts r join nn_payments p on"
                + " p.id=r.payment_id left join nn_bookings bo on bo.id=p.booking_id left join"
                + " nn_invoices i on i.id=p.invoice_id join nn_units u on"
                + " u.id=coalesce(bo.unit_id,i.unit_id) join nn_buildings b on b.id=u.building_id"
                + " join nn_accounts a on a.id=p.account_id where "
                + paymentScope
                + " and r.received_at>=? and r.received_at<? order by r.received_at desc,r.id desc",
            from.atStartOfDay(),
            end.atStartOfDay());
    List<Row> refunds =
        s.db.list(
            "select f.*,p.code payment_code,u.code unit_code,b.name building_name from nn_refunds f"
                + " join nn_payments p on p.id=f.payment_id left join nn_bookings bo on"
                + " bo.id=p.booking_id left join nn_invoices i on i.id=p.invoice_id join nn_units u"
                + " on u.id=coalesce(bo.unit_id,i.unit_id) join nn_buildings b on"
                + " b.id=u.building_id where "
                + paymentScope
                + " and f.status='COMPLETED' and f.processed_at>=? and f.processed_at<? order by"
                + " f.processed_at desc",
            from.atStartOfDay(),
            end.atStartOfDay());
    long gross = 0, returned = 0, rentCash = 0, utilityCash = 0, depositCash = 0, excessCash = 0;
    Map<String, Long> buildingCash = new LinkedHashMap<>();
    for (Row r : receipts) {
      long amount = r.num("amount"), prior = r.num("prior_received");
      gross += amount;
      buildingCash.merge(r.str("building_name"), amount, Long::sum);
      if ("UTILITY".equals(r.str("type"))) {
        long utility =
            Math.min(prior + amount, r.num("invoice_total"))
                - Math.min(prior, r.num("invoice_total"));
        utilityCash += utility;
        excessCash += amount - utility;
      } else {
        long rent =
            Math.min(prior + amount, r.num("rent_total")) - Math.min(prior, r.num("rent_total"));
        long deposit =
            Math.min(Math.max(0, prior + amount - r.num("rent_total")), r.num("deposit_total"))
                - Math.min(Math.max(0, prior - r.num("rent_total")), r.num("deposit_total"));
        rentCash += rent;
        depositCash += deposit;
        excessCash += amount - rent - deposit;
      }
    }
    for (Row r : refunds) {
      long amount = r.num("amount");
      returned += amount;
      buildingCash.merge(r.str("building_name"), -amount, Long::sum);
      switch (r.str("category")) {
        case "RENT":
          rentCash -= amount;
          break;
        case "DEPOSIT":
          depositCash -= amount;
          break;
        case "OVERPAY":
          excessCash -= amount;
          break;
        default:
          utilityCash -= amount;
      }
    }
    BigDecimal earned = BigDecimal.ZERO;
    String rentScope =
        scope
            + (type.isEmpty()
                ? ""
                : "UTILITY".equals(type)
                    ? " and 1=0"
                    : " and bo.kind='" + ("RENT".equals(type) ? "NEW" : "EXTENSION") + "'");
    List<Row> periods =
        s.db.list(
            "select bo.*,(select coalesce(sum(f.amount),0) from nn_refunds f join nn_payments pp on"
                + " pp.id=f.payment_id where pp.booking_id=bo.id and f.category='RENT' and"
                + " f.status='COMPLETED') refunded_rent from nn_bookings bo join nn_leases l on"
                + " l.id=bo.lease_id join nn_units u on u.id=bo.unit_id join nn_buildings b on"
                + " b.id=u.building_id where "
                + rentScope
                + " and bo.status='CONFIRMED' and l.status<>'CANCELLED' and bo.start_date<? and"
                + " bo.end_date>?",
            end,
            from);
    for (Row b : periods) {
      BigDecimal ratio =
          BigDecimal.valueOf(Math.max(0, b.num("rent_total") - b.num("refunded_rent")))
              .divide(
                  BigDecimal.valueOf(Math.max(1, b.num("rent_total"))), 12, RoundingMode.HALF_UP);
      for (int k = 0; k < b.num("months"); k++) {
        LocalDate start = b.date("start_date").plusMonths(k),
            stop = b.date("start_date").plusMonths(k + 1),
            a = start.isAfter(from) ? start : from,
            z = stop.isBefore(end) ? stop : end;
        if (z.isAfter(a))
          earned =
              earned.add(
                  BigDecimal.valueOf(b.num("monthly_rent"))
                      .multiply(ratio)
                      .multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(a, z)))
                      .divide(
                          BigDecimal.valueOf(ChronoUnit.DAYS.between(start, stop)),
                          10,
                          RoundingMode.HALF_UP));
      }
    }
    String utilityScope = scope + (type.isEmpty() || "UTILITY".equals(type) ? "" : " and 1=0");
    Row utilityParts =
        s.db.one(
            "select coalesce(sum(i.electricity_amount),0)"
                + " electricity,coalesce(sum(i.water_amount),0)"
                + " water,coalesce(sum(i.service_fee),0) service,coalesce(sum(i.parking_fee),0)"
                + " parking,coalesce(sum(i.adjustment),0) adjustment from nn_invoices i join"
                + " nn_units u on u.id=i.unit_id join nn_buildings b on b.id=u.building_id where "
                + utilityScope
                + " and i.status='PAID' and i.reading_date>=? and i.reading_date<?",
            from,
            end);
    long utilities =
        s.db.count(
            "select coalesce(sum(i.total),0) from nn_invoices i join nn_units u on u.id=i.unit_id"
                + " join nn_buildings b on b.id=u.building_id where "
                + utilityScope
                + " and i.status='PAID' and i.reading_date>=? and i.reading_date<?",
            from,
            end);
    long deductions =
        s.db.count(
            "select coalesce(sum(l.deposit_deduction),0) from nn_leases l join nn_units u on"
                + " u.id=l.unit_id join nn_buildings b on b.id=u.building_id where "
                + scope
                + (type.isEmpty() || "RENT".equals(type) ? "" : " and 1=0")
                + " and l.status='CLOSED' and l.checkout_date>=? and l.checkout_date<?",
            from,
            end);
    long rentEarned = earned.setScale(0, RoundingMode.HALF_UP).longValue();
    List<Row> incomeParts = new ArrayList<>();
    String[] partNames = {
      "Tiền thuê", "Tiền điện", "Tiền nước", "Phí dịch vụ", "Phí gửi xe", "Khấu trừ cọc"
    };
    String[] partColors = {"#29795e", "#9abf49", "#64a6b1", "#c1a06a", "#8b81b0", "#e0bf63"};
    long[] partAmounts = {
      rentEarned,
      utilityParts.num("electricity"),
      utilityParts.num("water"),
      utilityParts.num("service"),
      utilityParts.num("parking"),
      deductions
    };
    long partTotal = 0;
    for (long amount : partAmounts) partTotal += amount;
    double offset = 0;
    for (int k = 0; k < partAmounts.length; k++) {
      double percent = partTotal == 0 ? 0 : partAmounts[k] * 100.0 / partTotal;
      incomeParts.add(
          new Row()
              .with("name", partNames[k])
              .with("amount", partAmounts[k])
              .with("color", partColors[k])
              .with("percent", String.format(Locale.ROOT, "%.4f", percent))
              .with("offset", String.format(Locale.ROOT, "%.4f", -offset)));
      offset += percent;
    }
    long buildingMax = 1;
    for (long amount : buildingCash.values()) buildingMax = Math.max(buildingMax, Math.abs(amount));
    List<Row> comparisons = new ArrayList<>();
    for (Map.Entry<String, Long> entry : buildingCash.entrySet())
      comparisons.add(
          new Row()
              .with("name", entry.getKey())
              .with("amount", entry.getValue())
              .with("negative", entry.getValue() < 0)
              .with("width", Math.abs(entry.getValue()) * 100 / buildingMax));
    return new Row()
        .with("from", from)
        .with("through", through)
        .with("gross", gross)
        .with("refunded", returned)
        .with("net", gross - returned)
        .with("rent_cash", rentCash)
        .with("utility_cash", utilityCash)
        .with("deposit_cash", depositCash)
        .with("overpay_cash", excessCash)
        .with("earned_rent", earned.setScale(0, RoundingMode.HALF_UP).longValue())
        .with("earned_utility", utilities)
        .with("income_parts", incomeParts)
        .with("income_base", partTotal)
        .with("adjustment", utilityParts.num("adjustment"))
        .with("building_comparison", comparisons)
        .with("deposit_deduction", deductions)
        .with(
            "earned_total",
            earned.setScale(0, RoundingMode.HALF_UP).longValue() + utilities + deductions)
        .with("receipts", receipts)
        .with("refunds", refunds)
        .with("building_cash", buildingCash);
  }

  public List<Row> chart(Row actor, Long building) {
    return chart(actor, building, null, "");
  }

  public List<Row> chart(Row actor, Long building, Long unit, String type) {
    List<Row> months = new ArrayList<>();
    long max = 1;
    for (int i = 11; i >= 0; i--) {
      YearMonth ym = YearMonth.from(s.today()).minusMonths(i);
      Row r = report(actor, ym.atDay(1), ym.atEndOfMonth(), building, unit, type);
      r.put("month", ym.toString());
      r.put("month_label", String.format("%02d/%d", ym.getMonthValue(), ym.getYear()));
      max = Math.max(max, Math.max(Math.abs(r.num("net")), r.num("earned_total")));
      months.add(r);
    }
    for (Row r : months) {
      r.put("cash_height", r.num("net") == 0 ? 0 : Math.max(1, Math.abs(r.num("net")) * 45 / max));
      r.put("cash_negative", r.num("net") < 0);
      r.put(
          "earned_height",
          r.num("earned_total") == 0 ? 0 : Math.max(1, r.num("earned_total") * 45 / max));
    }
    return months;
  }
}
