package com.javaweb.nnland.service;

import com.javaweb.nnland.model.Row;
import java.time.*;
import java.util.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduleService {
  private final Support s;

  public ScheduleService(Support s) {
    this.s = s;
  }

  public boolean available(long unit, LocalDate start, LocalDate end, long excludeBooking) {
    return available(unit, start, end, excludeBooking, 0);
  }

  public boolean available(
      long unit, LocalDate start, LocalDate end, long excludeBooking, long excludeLease) {
    if (s.db.count(
            "select count(*) from nn_leases where unit_id=? and id<>? and status in"
                + " ('RESERVED','ACTIVE','ENDING') and start_date<? and (end_date>? or (status in"
                + " ('ACTIVE','ENDING') and end_date<=?))",
            unit,
            excludeLease,
            end,
            start,
            s.today())
        > 0) return false;
    return s.db.count(
            "select count(*) from nn_bookings where unit_id=? and id<>? and status in"
                + " ('HOLD','PAYMENT_REVIEW') and hold_until>? and start_date<? and end_date>?",
            unit,
            excludeBooking,
            s.now(),
            end,
            start)
        == 0;
  }

  /** A hold is not an occupied apartment. Expiry never changes an active lease. */
  @Scheduled(fixedDelay = 60000)
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void expireHolds() {
    for (Row hint :
        s.db.list(
            "select id,unit_id from nn_bookings where status in ('HOLD','PAYMENT_REVIEW') and"
                + " hold_until<=? order by unit_id,id",
            s.now())) {
      s.unit(hint.num("unit_id"), true);
      Row b = s.db.one("select * from nn_bookings where id=? for update", hint.id());
      if (!Arrays.asList("HOLD", "PAYMENT_REVIEW").contains(b.str("status"))
          || b.time("hold_until").isAfter(s.now())) continue;
      s.db.update("update nn_bookings set status='EXPIRED' where id=?", b.id());
      Row p = s.db.one("select * from nn_payments where booking_id=?", b.id());
      if (p != null)
        s.db.update(
            "update nn_payments set status=? where id=?",
            s.received(p.id()) > 0 ? "RECONCILE" : "EXPIRED",
            p.id());
      s.notify(
          b.num("account_id"),
          "Thời gian giữ chỗ đã kết thúc",
          "Đơn "
              + b.str("code")
              + " đã hết thời gian giữ chỗ. Tiền đến muộn sẽ được quản lý đối soát trước khi xử"
              + " lý.",
          "/tai-khoan/dat-thue");
    }
  }
}
