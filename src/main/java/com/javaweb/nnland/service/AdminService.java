package com.javaweb.nnland.service;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.utils.Rules;
import java.math.*;
import java.util.*;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
public class AdminService {
  private final Support s;
  private final SessionRegistry sessions;
  private final AccountService accounts;

  public AdminService(Support s, SessionRegistry sessions, AccountService accounts) {
    this.s = s;
    this.sessions = sessions;
    this.accounts = accounts;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public long building(Row actor, Long id, Map<String, String> f) {
    if (id == null) s.requireAdmin(actor);
    else s.buildingAccess(actor, id);
    String code = Rules.text(f.get("code"), 32, "Mã tòa nhà").toUpperCase(Locale.ROOT);
    Rules.check(code.matches("[A-Z0-9-]{2,32}"), "Mã tòa nhà không hợp lệ.");
    String name = Rules.text(f.get("name"), 150, "Tên tòa nhà"),
        city = Rules.text(f.get("city"), 80, "Thành phố"),
        district = Rules.text(f.get("district"), 80, "Quận/huyện"),
        address = Rules.text(f.get("address"), 255, "Địa chỉ"),
        description = Rules.text(f.get("description"), 5000, "Giới thiệu"),
        image = Rules.image(f.get("image")),
        amenities = Rules.optional(f.get("amenities"), 1000);
    boolean published = "on".equals(f.get("published"));
    Rules.check(
        s.db.count(
                "select count(*) from nn_buildings where code=? and id<>?",
                code,
                id == null ? 0 : id)
            == 0,
        "Mã tòa nhà đã được sử dụng.");
    if (id == null)
      id =
          s.db.insert(
              "insert into"
                  + " nn_buildings(code,name,city,district,address,description,image,amenities,published,created_at)"
                  + " values(?,?,?,?,?,?,?,?,?,?)",
              code,
              name,
              city,
              district,
              address,
              description,
              image,
              amenities,
              published,
              s.now());
    else
      s.db.update(
          "update nn_buildings set"
              + " code=?,name=?,city=?,district=?,address=?,description=?,image=?,amenities=?,published=?"
              + " where id=?",
          code,
          name,
          city,
          district,
          address,
          description,
          image,
          amenities,
          published,
          id);
    s.audit(actor, "SAVE_BUILDING", "BUILDING", id, name);
    return id;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public long unit(Row actor, Long id, Map<String, String> f) {
    long building = Rules.amount(f.get("building_id"), "Tòa nhà");
    s.buildingAccess(actor, building);
    Rules.check(
        s.db.count("select count(*) from nn_buildings where id=?", building) == 1,
        "Tòa nhà không tồn tại.");
    if (id != null) {
      Row current = s.unit(id, true);
      s.buildingAccess(actor, current.num("building_id"));
      Rules.check(
          current.num("building_id") == building,
          "Không chuyển căn hộ sang tòa khác khi đã tạo mã căn.");
    }
    String code = Rules.text(f.get("code"), 32, "Mã căn").toUpperCase(Locale.ROOT);
    Rules.check(code.matches("[A-Z0-9-]{1,32}"), "Mã căn không hợp lệ.");
    Rules.check(
        s.db.count(
                "select count(*) from nn_units where building_id=? and code=? and id<>?",
                building,
                code,
                id == null ? 0 : id)
            == 0,
        "Mã căn đã có trong tòa nhà này.");
    String type =
        Rules.choice(f.get("type"), "STANDARD", "STUDIO", "OFFICETEL", "DUPLEX", "PENTHOUSE");
    int beds = Rules.integer(f.get("bedrooms"), 0, 10, "Số phòng ngủ"),
        baths = Rules.integer(f.get("bathrooms"), 1, 10, "Số phòng tắm"),
        floor = Rules.integer(f.get("floor_no"), 0, 150, "Tầng");
    Rules.check(
        !type.equals("STUDIO") || beds == 0, "Studio dùng số phòng ngủ bằng 0 (không gian mở).");
    BigDecimal area = Rules.meter(f.get("area"));
    Rules.check(
        area.compareTo(new BigDecimal("10")) >= 0
            && area.compareTo(new BigDecimal("2000")) <= 0
            && area.scale() <= 2,
        "Diện tích phải từ 10 đến 2.000 m², tối đa 2 số thập phân.");
    long rent = Rules.amount(f.get("monthly_rent"), "Giá thuê"),
        deposit = Rules.amount(f.get("deposit_amount"), "Tiền cọc"),
        service = Rules.amount(f.get("service_fee"), "Phí dịch vụ"),
        electric = Rules.amount(f.get("electricity_rate"), "Đơn giá điện"),
        water = Rules.amount(f.get("water_rate"), "Đơn giá nước"),
        parking = Rules.amount(f.get("parking_fee"), "Phí gửi xe");
    Rules.check(
        rent >= 100000 && rent <= 1000000000L && deposit <= rent * 12,
        "Giá thuê phải từ 100 nghìn đến 1 tỷ; tiền cọc tối đa 12 tháng thuê.");
    Rules.check(
        electric <= 1000000 && water <= 1000000 && service <= 100000000 && parking <= 100000000,
        "Đơn giá điện/nước hoặc phí dịch vụ vượt giới hạn.");
    String status = Rules.choice(f.get("operational_status"), "READY", "MAINTENANCE", "INACTIVE");
    String name = Rules.text(f.get("name"), 180, "Tên căn hộ"),
        description = Rules.text(f.get("description"), 5000, "Mô tả"),
        amenities = Rules.optional(f.get("amenities"), 2000),
        image = Rules.image(f.get("image")),
        image2 = Rules.image(f.get("image2")),
        image3 = Rules.image(f.get("image3"));
    boolean published = "on".equals(f.get("published")), featured = "on".equals(f.get("featured"));
    if (id == null)
      id =
          s.db.insert(
              "insert into"
                  + " nn_units(building_id,code,name,type,bedrooms,bathrooms,area,floor_no,monthly_rent,deposit_amount,service_fee,electricity_rate,water_rate,parking_fee,description,amenities,image,image2,image3,published,featured,operational_status,created_at)"
                  + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
              building,
              code,
              name,
              type,
              beds,
              baths,
              area,
              floor,
              rent,
              deposit,
              service,
              electric,
              water,
              parking,
              description,
              amenities,
              image,
              image2,
              image3,
              published,
              featured,
              status,
              s.now());
    else
      s.db.update(
          "update nn_units set"
              + " code=?,name=?,type=?,bedrooms=?,bathrooms=?,area=?,floor_no=?,monthly_rent=?,deposit_amount=?,service_fee=?,electricity_rate=?,water_rate=?,parking_fee=?,description=?,amenities=?,image=?,image2=?,image3=?,published=?,featured=?,operational_status=?"
              + " where id=?",
          code,
          name,
          type,
          beds,
          baths,
          area,
          floor,
          rent,
          deposit,
          service,
          electric,
          water,
          parking,
          description,
          amenities,
          image,
          image2,
          image3,
          published,
          featured,
          status,
          id);
    s.audit(actor, "SAVE_UNIT", "UNIT", id, name + "; " + status);
    return id;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void assignment(Row actor, long building, long account, boolean assign) {
    s.requireAdmin(actor);
    Rules.check(
        s.db.count("select count(*) from nn_buildings where id=?", building) == 1,
        "Tòa nhà không tồn tại.");
    Rules.check(
        s.db.count(
                "select count(*) from nn_accounts where id=? and role='MANAGER' and active=true",
                account)
            == 1,
        "Chọn tài khoản quản lý đang hoạt động.");
    if (assign) {
      if (s.db.count(
              "select count(*) from nn_building_staff where building_id=? and account_id=?",
              building,
              account)
          == 0)
        s.db.update(
            "insert into nn_building_staff(building_id,account_id) values(?,?)", building, account);
    } else
      s.db.update(
          "delete from nn_building_staff where building_id=? and account_id=?", building, account);
    s.audit(
        actor,
        assign ? "ASSIGN_MANAGER" : "UNASSIGN_MANAGER",
        "BUILDING",
        building,
        "Tài khoản " + account);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void account(Row actor, long id, String role, boolean active) {
    s.requireAdmin(actor);
    s.db.list("select id from nn_accounts where role='ADMIN' order by id for update");
    Rules.choice(role, "USER", "MANAGER", "ADMIN");
    Row user = s.db.one("select * from nn_accounts where id=? for update", id);
    Rules.check(user != null, "Không tìm thấy tài khoản.");
    Rules.check(
        actor.id() != id || ("ADMIN".equals(role) && active),
        "Bạn không thể tự khóa hoặc tự hạ quyền tài khoản đang dùng.");
    if ("ADMIN".equals(user.str("role")) && (!active || !"ADMIN".equals(role)))
      Rules.check(
          s.db.count("select count(*) from nn_accounts where role='ADMIN' and active=true") > 1,
          "Cần giữ ít nhất một quản trị viên hoạt động.");
    if (!"MANAGER".equals(role) || !active)
      s.db.update("delete from nn_building_staff where account_id=?", id);
    s.db.update("update nn_accounts set role=?,active=? where id=?", role, active, id);
    for (Object p : sessions.getAllPrincipals())
      if (p instanceof UserDetails && ((UserDetails) p).getUsername().equals(user.str("email")))
        sessions.getAllSessions(p, false).forEach(x -> x.expireNow());
    s.audit(actor, "UPDATE_ACCOUNT_ACCESS", "ACCOUNT", id, role + "; active=" + active);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public long createAccount(Row actor, Map<String, String> f) {
    s.requireAdmin(actor);
    long id =
        accounts.register(
            f.get("email"),
            f.get("username"),
            f.get("full_name"),
            f.get("birth_year"),
            f.get("phone"),
            f.getOrDefault("gender", "OTHER"),
            f.get("password"));
    account(actor, id, f.get("role"), true);
    return id;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public void settings(Row actor, Map<String, String> f) {
    s.requireAdmin(actor);
    String bin = Rules.optional(f.get("bank_bin"), 6),
        account = Rules.optional(f.get("bank_account"), 34),
        owner = Rules.optional(f.get("bank_owner"), 150);
    boolean any = !bin.isEmpty() || !account.isEmpty() || !owner.isEmpty();
    Rules.check(
        !any || (bin.matches("[0-9]{6}") && account.matches("[0-9]{6,34}") && !owner.isEmpty()),
        "Cấu hình ngân hàng cần đủ BIN 6 số, số tài khoản và chủ tài khoản.");
    s.set("bank_bin", bin);
    s.set("bank_account", account);
    s.set("bank_owner", owner);
    s.set("bank_name", Rules.optional(f.get("bank_name"), 100));
    s.set("hotline", Rules.phone(f.get("hotline")));
    s.set("contact_email", Rules.email(f.get("contact_email")));
    s.set("office_address", Rules.text(f.get("office_address"), 255, "Địa chỉ văn phòng"));
    s.set(
        "hold_minutes",
        String.valueOf(Rules.integer(f.get("hold_minutes"), 10, 120, "Phút giữ chỗ")));
    String terms = Rules.text(f.get("terms_text"), 10000, "Điều khoản");
    Rules.check(
        terms.length() >= 100, "Điều khoản cần nêu rõ chính sách thuê, thanh toán và hoàn tiền.");
    if (!terms.equals(s.terms())) s.set("terms_version", Rules.code("DK"));
    s.set("terms_text", terms);
    s.audit(
        actor,
        "UPDATE_SETTINGS",
        "SETTINGS",
        null,
        "Cập nhật ngân hàng/thông tin liên hệ/chính sách");
  }
}
