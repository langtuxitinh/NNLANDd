package com.javaweb.nnland.service;

import com.javaweb.nnland.model.Row;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.*;

@Component
public class LeaseReminders {
  private final Support s;

  public LeaseReminders(Support s) {
    this.s = s;
  }

  @Scheduled(fixedDelay = 1800000, initialDelay = 60000)
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void remind() {
    for (Row hint :
        s.db.list(
            "select id,unit_id from nn_leases where status in ('ACTIVE','RESERVED') and end_date<=?"
                + " order by unit_id,id",
            s.today().plusDays(7))) {
      s.unit(hint.num("unit_id"), true);
      Row lease = s.db.one("select * from nn_leases where id=? for update", hint.id());
      if (!java.util.Arrays.asList("ACTIVE", "RESERVED").contains(lease.str("status"))) continue;
      boolean overdue = !lease.date("end_date").isAfter(s.today());
      if (lease.get("reminder_sent_at") != null
          && (!overdue
              || !lease.time("reminder_sent_at").toLocalDate().isBefore(lease.date("end_date"))))
        continue;
      String body =
          "Hợp đồng "
              + lease.str("code")
              + (overdue
                  ? " đã đến hạn. Vui lòng liên hệ quản lý để kiểm tra gia hạn hoặc hoàn tất bàn"
                        + " giao."
                  : " sẽ kết thúc ngày "
                      + lease.date("end_date")
                      + ". Bạn có thể kiểm tra lịch gia hạn hoặc gửi yêu cầu trả phòng trong tài"
                      + " khoản.");
      Row user = s.db.one("select email from nn_accounts where id=?", lease.num("account_id"));
      s.notify(
          lease.num("account_id"),
          overdue ? "Kỳ thuê đã đến hạn" : "Kỳ thuê sắp kết thúc",
          body,
          "/tai-khoan/hop-dong/" + lease.id());
      s.mail(user.str("email"), "NNland · Nhắc thời hạn thuê", body, null);
      s.db.update("update nn_leases set reminder_sent_at=? where id=?", s.now(), lease.id());
    }
  }
}
