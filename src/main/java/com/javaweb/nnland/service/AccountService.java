package com.javaweb.nnland.service;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.utils.Rules;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
  private final Support s;
  private final BCryptPasswordEncoder encoder;
  private final SessionRegistry sessions;

  @Value("${nnland.base-url}")
  private String baseUrl;

  public AccountService(Support s, BCryptPasswordEncoder encoder, SessionRegistry sessions) {
    this.s = s;
    this.encoder = encoder;
    this.sessions = sessions;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public long register(
      String email,
      String username,
      String name,
      String birth,
      String phone,
      String gender,
      String password) {
    email = Rules.email(email);
    username = Rules.text(username, 60, "Tên đăng nhập").toLowerCase(Locale.ROOT);
    Rules.check(
        username.matches("[a-z0-9._-]{3,60}"),
        "Tên đăng nhập gồm 3–60 ký tự chữ thường, số, dấu chấm, gạch ngang hoặc gạch dưới.");
    Rules.password(password);
    int year = Rules.integer(birth, 1900, s.today().getYear() - 18, "Năm sinh");
    name = Rules.text(name, 150, "Họ tên");
    phone = Rules.phone(phone);
    gender = Rules.choice(gender, "MALE", "FEMALE", "OTHER");
    Rules.check(
        s.db.count("select count(*) from nn_accounts where email=? or username=?", email, username)
            == 0,
        "Email hoặc tên đăng nhập đã được sử dụng.");
    long id =
        s.db.insert(
            "insert into"
                + " nn_accounts(email,username,full_name,birth_year,phone,gender,password_hash,role,active,created_at)"
                + " values(?,?,?,?,?,?,?,'USER',true,?)",
            email,
            username,
            name,
            year,
            phone,
            gender,
            encoder.encode(password),
            s.now());
    s.notify(
        id,
        "Chào mừng đến NNland",
        "Tài khoản đã được tạo. Bạn có thể chọn căn hộ và đặt lịch xem trước khi thuê.",
        "/tai-khoan");
    s.mail(
        email,
        "Chào mừng bạn đến NNland",
        "Xin chào " + name + ", tài khoản NNland đã được tạo. Website: " + baseUrl,
        null);
    s.audit(null, "REGISTER", "ACCOUNT", id, "Khách hàng tự đăng ký");
    return id;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void profile(Row actor, String name, String birth, String phone, String gender) {
    int year = Rules.integer(birth, 1900, s.today().getYear() - 18, "Năm sinh");
    s.db.update(
        "update nn_accounts set full_name=?,birth_year=?,phone=?,gender=? where id=?",
        Rules.text(name, 150, "Họ tên"),
        year,
        Rules.phone(phone),
        Rules.choice(gender, "MALE", "FEMALE", "OTHER"),
        actor.id());
    s.audit(actor, "UPDATE_PROFILE", "ACCOUNT", actor.id(), "Cập nhật hồ sơ cá nhân");
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void changePassword(Row actor, String current, String next) {
    Rules.check(
        encoder.matches(current, actor.str("password_hash")), "Mật khẩu hiện tại chưa đúng.");
    Rules.password(next);
    s.db.update(
        "update nn_accounts set password_hash=? where id=?", encoder.encode(next), actor.id());
    s.audit(actor, "CHANGE_PASSWORD", "ACCOUNT", actor.id(), "Đổi mật khẩu");
    expireSessions(actor.str("email"));
  }

  private void expireSessions(String email) {
    for (Object p : sessions.getAllPrincipals())
      if (p instanceof UserDetails && ((UserDetails) p).getUsername().equals(email))
        sessions.getAllSessions(p, false).forEach(x -> x.expireNow());
  }

  private String hash(String value) {
    try {
      byte[] bytes =
          MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
      StringBuilder b = new StringBuilder();
      for (byte x : bytes) b.append(String.format("%02x", x));
      return b.toString();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void requestReset(String email) {
    email = Rules.email(email);
    Row account = s.db.one("select * from nn_accounts where email=? and active=true", email);
    if (account == null) return;
    if (s.db.count(
            "select count(*) from nn_reset_tokens where account_id=? and created_at>?",
            account.id(),
            s.now().minusMinutes(5))
        > 0) return;
    byte[] random = new byte[32];
    new SecureRandom().nextBytes(random);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(random);
    s.db.insert(
        "insert into nn_reset_tokens(account_id,token_hash,expires_at,created_at) values(?,?,?,?)",
        account.id(),
        hash(token),
        s.now().plusMinutes(30),
        s.now());
    s.mail(
        email,
        "NNland · Đặt lại mật khẩu",
        "Liên kết dùng một lần, hết hạn sau 30 phút: "
            + baseUrl
            + "/dat-lai-mat-khau?token="
            + token
            + "\nNếu không yêu cầu, bạn có thể bỏ qua email này.",
        null);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void reset(String token, String next) {
    Rules.password(next);
    Row t =
        s.db.one(
            "select * from nn_reset_tokens where token_hash=? for update",
            hash(Rules.text(token, 100, "Liên kết")));
    Rules.check(
        t != null && t.get("consumed_at") == null && t.time("expires_at").isAfter(s.now()),
        "Liên kết đã dùng hoặc hết hạn. Hãy yêu cầu liên kết mới.");
    s.db.update(
        "update nn_reset_tokens set consumed_at=? where account_id=? and consumed_at is null",
        s.now(),
        t.num("account_id"));
    s.db.update(
        "update nn_accounts set password_hash=? where id=?",
        encoder.encode(next),
        t.num("account_id"));
    Row account = s.db.one("select * from nn_accounts where id=?", t.num("account_id"));
    expireSessions(account.str("email"));
    s.audit(
        account,
        "RESET_PASSWORD",
        "ACCOUNT",
        account.id(),
        "Đặt lại mật khẩu qua liên kết một lần");
  }
}
