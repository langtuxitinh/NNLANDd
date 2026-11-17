package com.javaweb.nnland.service;

import com.javaweb.nnland.model.Row;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements ApplicationRunner {
  private final Support s;
  private final BCryptPasswordEncoder encoder;

  @Value("${nnland.seed-data:true}")
  private boolean seed;

  @Value("${nnland.demo-accounts:true}")
  private boolean demo;

  @Value("${NNLAND_ADMIN_EMAIL:admin@nnland.vn}")
  private String bootstrapEmail;

  @Value("${NNLAND_ADMIN_PASSWORD:}")
  private String bootstrapPassword;

  public DataSeeder(Support s, BCryptPasswordEncoder encoder) {
    this.s = s;
    this.encoder = encoder;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (s.db.count("select count(*) from nn_settings where setting_key='terms_text'") == 0) {
      s.set("terms_text", s.terms());
      s.set("terms_version", "NN-2026-01");
      s.set("hold_minutes", "30");
      s.set("bank_bin", "");
      s.set("bank_account", "");
      s.set("bank_owner", "");
      s.set("bank_name", "");
      s.set("hotline", "1900 6868");
      s.set("contact_email", "hello@nnland.vn");
      s.set("office_address", "46 Man Thiện, TP. Hồ Chí Minh");
    }
    long admin = 0, manager = 0, customer = 0;
    if (demo) {
      admin = account("admin@nnland.vn", "admin", "Quản trị NNland", "ADMIN");
      manager = account("manager@nnland.vn", "manager", "Quản lý NNland", "MANAGER");
      customer = account("customer@nnland.vn", "customer", "Nguyễn Minh An", "USER");
    }
    if (!demo
        && s.db.count("select count(*) from nn_accounts where role='ADMIN' and active=true") == 0) {
      com.javaweb.nnland.utils.Rules.password(bootstrapPassword);
      s.db.insert(
          "insert into"
              + " nn_accounts(email,username,full_name,birth_year,phone,gender,password_hash,role,created_at)"
              + " values(?,?,?,?,?,?,?,?,?)",
          com.javaweb.nnland.utils.Rules.email(bootstrapEmail),
          uniqueUsername("admin"),
          "Quản trị NNland",
          1990,
          "0900000000",
          "OTHER",
          encoder.encode(bootstrapPassword),
          "ADMIN",
          s.now());
    }
    if (!seed) return;
    if (s.db.count("select count(*) from nn_buildings") > 0) return;
    String[][] buildings = {
      {
        "RIVERSIDE",
        "NNland Riverside",
        "TP. Hồ Chí Minh",
        "TP. Thủ Đức",
        "Đường Mai Chí Thọ, khu đô thị Thủ Thiêm",
        "building-riverside.webp",
        "Không gian sống hiện đại bên sông, kết nối nhanh đến trung tâm và các tiện ích đô thị.",
        "Hồ bơi|Phòng gym|Vườn trên cao|An ninh 24/7"
      },
      {
        "CENTRAL",
        "NNland Central Park",
        "TP. Hồ Chí Minh",
        "Bình Thạnh",
        "Khu vực Tân Cảng, Bình Thạnh",
        "building-central.webp",
        "Căn hộ tiện nghi cạnh công viên, dành cho nhịp sống năng động giữa thành phố.",
        "Công viên|Phòng gym|Siêu thị|Bãi đỗ xe"
      },
      {
        "WESTLAKE",
        "NNland West Lake",
        "Hà Nội",
        "Tây Hồ",
        "Đường Võ Chí Công, Tây Hồ",
        "building-westlake.webp",
        "Nhịp sống yên bình gần hồ Tây với không gian rộng và nhiều ánh sáng tự nhiên.",
        "Hồ bơi|Café|Khu vui chơi|An ninh 24/7"
      },
      {
        "OCEAN",
        "NNland Ocean View",
        "Đà Nẵng",
        "Sơn Trà",
        "Khu vực đường Võ Nguyên Giáp, Sơn Trà",
        "amenity-pool.webp",
        "Không gian sống thoáng sáng gần biển, phù hợp với gia đình và chuyên gia làm việc tại Đà"
            + " Nẵng.",
        "Gần biển|Hồ bơi|Bãi đỗ xe|Sảnh lễ tân"
      },
      {
        "GARDEN",
        "NNland Garden",
        "TP. Hồ Chí Minh",
        "Quận 7",
        "Khu đô thị Phú Mỹ Hưng, Quận 7",
        "lobby.webp",
        "Môi trường sống xanh với tiện ích nội khu và không gian riêng tư cho gia đình trẻ.",
        "Vườn nội khu|Phòng gym|An ninh 24/7|Khu vui chơi"
      },
      {
        "BADINH",
        "NNland Ba Đình",
        "Hà Nội",
        "Ba Đình",
        "Khu vực Giảng Võ, Ba Đình",
        "building-westlake.webp",
        "Tòa nhà ngay khu vực trung tâm, thuận tiện di chuyển đến các khu văn phòng và điểm đến văn"
            + " hóa.",
        "Sảnh lễ tân|Thang máy|Bãi đỗ xe|Café"
      }
    };
    String[] kinds = {"STUDIO", "STANDARD", "STANDARD", "DUPLEX", "PENTHOUSE"};
    String[] names = {
      "Studio tràn ánh sáng",
      "Căn hộ 1 phòng ngủ tiện nghi",
      "Căn hộ 2 phòng ngủ có ban công",
      "Duplex không gian mở",
      "Penthouse tầm nhìn rộng"
    };
    String[] photos = {
      "apartment-room.webp",
      "apartment-interior.webp",
      "apartment-living.webp",
      "residence-exterior.webp",
      "hero.webp"
    };
    int[] beds = {0, 1, 2, 3, 3};
    int[] areas = {35, 52, 78, 112, 165};
    long[] prices = {6500000, 9800000, 14500000, 22000000, 36000000};
    for (int i = 0; i < buildings.length; i++) {
      String[] b = buildings[i];
      long id =
          s.db.insert(
              "insert into"
                  + " nn_buildings(code,name,city,district,address,image,description,amenities,created_at)"
                  + " values(?,?,?,?,?,?,?,?,?)",
              b[0],
              b[1],
              b[2],
              b[3],
              b[4],
              "/nnland/images/" + b[5],
              b[6],
              b[7],
              s.now());
      if (manager > 0 && i < 4)
        s.db.update(
            "insert into nn_building_staff(building_id,account_id) values(?,?)", id, manager);
      for (int j = 0; j < 5; j++) {
        long rent = prices[j] + (i == 2 ? 1000000 : i == 3 ? -500000 : 0);
        s.db.insert(
            "insert into"
                + " nn_units(building_id,code,name,type,bedrooms,bathrooms,area,floor_no,monthly_rent,deposit_amount,service_fee,electricity_rate,water_rate,parking_fee,description,amenities,image,image2,image3,featured,created_at)"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            id,
            "A" + (j + 5) + "0" + (j + 1),
            names[j],
            kinds[j],
            beds[j],
            j > 1 ? 2 : 1,
            new BigDecimal(areas[j]),
            j + 5,
            rent,
            0,
            350000,
            3500,
            18000,
            150000,
            "Một không gian được bố trí chỉn chu, đón ánh sáng tự nhiên và đáp ứng nhu cầu sống"
                + " hàng ngày. Căn hộ có khu bếp riêng, nội thất đồng bộ và nằm trong hệ thống do"
                + " NNland quản lý trực tiếp. Hình ảnh minh họa phong cách; lịch xem thực tế được"
                + " xác nhận cùng quản lý.",
            "Nội thất đầy đủ|Máy giặt|Điều hòa|Bếp|Tủ lạnh|Internet",
            "/nnland/images/" + photos[j],
            "/nnland/images/apartment-living.webp",
            "/nnland/images/apartment-room.webp",
            (i < 3 && j == 2) || (i == 0 && j == 1),
            s.now());
      }
    }
    s.audit(
        null,
        "SEED_DATA",
        "SYSTEM",
        null,
        "Tạo 6 tòa nhà, 30 căn hộ và tài khoản thử nghiệm nếu được bật.");
  }

  private long account(String email, String username, String name, String role) {
    Row old = s.db.one("select * from nn_accounts where email=?", email);
    if (old != null) return old.id();
    return s.db.insert(
        "insert into"
            + " nn_accounts(email,username,full_name,birth_year,phone,gender,password_hash,role,created_at)"
            + " values(?,?,?,?,?,?,?,?,?)",
        email,
        uniqueUsername(username),
        name,
        1995,
        "0900000000",
        "OTHER",
        encoder.encode("NNland@2026!"),
        role,
        s.now());
  }

  private String uniqueUsername(String preferred) {
    String candidate = preferred;
    int suffix = 1;
    while (s.db.count("select count(*) from nn_accounts where username=?", candidate) > 0)
      candidate = preferred + "-" + suffix++;
    return candidate;
  }
}
