package com.javaweb.nnland.service;

import com.javaweb.nnland.model.Row;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MailDispatcher {
  private final Support s;
  private final JavaMailSender sender;
  private final DocumentService documents;

  @Value("${nnland.mail.enabled:false}")
  private boolean enabled;

  @Value("${nnland.mail.from}")
  private String from;

  public MailDispatcher(Support s, JavaMailSender sender, DocumentService documents) {
    this.s = s;
    this.sender = sender;
    this.documents = documents;
  }

  @Scheduled(fixedDelay = 30000, initialDelay = 30000)
  public void deliver() {
    if (!enabled) return;
    s.db.update(
        "update nn_mail_outbox set status='FAILED',next_attempt=? where status='SENDING' and"
            + " next_attempt<?",
        s.now(),
        s.now().minusMinutes(10));
    for (Row m :
        s.db.list(
            "select * from nn_mail_outbox where status in ('PENDING','FAILED') and attempts<5 and"
                + " (next_attempt is null or next_attempt<=?) order by id limit 10",
            s.now())) {
      if (s.db.update(
              "update nn_mail_outbox set status='SENDING',attempts=attempts+1,next_attempt=? where"
                  + " id=? and status in ('PENDING','FAILED')",
              s.now(),
              m.id())
          != 1) continue;
      try {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper =
            new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setFrom(from);
        helper.setTo(m.str("recipient"));
        helper.setSubject(m.str("subject"));
        helper.setText(m.str("body"), false);
        if (m.get("attachment_lease_id") != null)
          helper.addAttachment(
              "Hop-dong-NNland.pdf",
              new ByteArrayResource(documents.contract(m.num("attachment_lease_id"))));
        sender.send(message);
        s.db.update(
            "update nn_mail_outbox set status='SENT',sent_at=?,last_error=null where id=?",
            s.now(),
            m.id());
      } catch (Exception e) {
        s.db.update(
            "update nn_mail_outbox set status='FAILED',last_error=?,next_attempt=? where id=?",
            e.getClass().getSimpleName() + ": Không gửi được; kiểm tra cấu hình SMTP và kết nối.",
            s.now().plusMinutes(Math.min(60, 5 * (m.num("attempts") + 1))),
            m.id());
      }
    }
  }
}
