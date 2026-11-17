package com.javaweb.nnland.service;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.utils.Rules;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {
  private final Support s;
  private final ScheduleService schedule;

  public CatalogService(Support s, ScheduleService schedule) {
    this.s = s;
    this.schedule = schedule;
  }

  public static final Map<String, String> TYPES = new LinkedHashMap<>();

  static {
    TYPES.put("STANDARD", "Căn hộ tiêu chuẩn");
    TYPES.put("STUDIO", "Studio");
    TYPES.put("OFFICETEL", "Officetel");
    TYPES.put("DUPLEX", "Duplex");
    TYPES.put("PENTHOUSE", "Penthouse");
  }

  private String unitSelect() {
    return "select u.*,b.name building_name,b.city,b.district,b.address building_address,b.code"
               + " building_code from nn_units u join nn_buildings b on b.id=u.building_id ";
  }

  public Row unit(long id) {
    Row u = s.db.one(unitSelect() + "where u.id=? and u.published=true and b.published=true", id);
    Rules.check(u != null, "Căn hộ không tồn tại hoặc đang tạm ngừng hiển thị.");
    u.put("type_label", TYPES.get(u.str("type")));
    u.put("amenity_list", u.str("amenities").split("\\|"));
    u.put(
        "manager",
        s.db.one(
            "select a.full_name,a.phone from nn_accounts a join nn_building_staff bs on"
                + " bs.account_id=a.id where bs.building_id=? and a.active=true order by a.id limit"
                + " 1",
            u.num("building_id")));
    return u;
  }

  public Row leaseUnit(long id) {
    Row u = s.db.one(unitSelect() + "where u.id=?", id);
    Rules.check(u != null, "Không tìm thấy căn hộ.");
    return u;
  }

  public List<Row> featured() {
    return s.db.list(
        unitSelect()
            + "where u.published=true and b.published=true and u.featured=true and"
            + " u.operational_status='READY' order by u.id limit 6");
  }

  public List<Row> buildings(String city) {
    return s.db.list(
        "select b.*,(select count(*) from nn_units u where u.building_id=b.id and u.published=true)"
            + " unit_count,(select min(monthly_rent) from nn_units u where u.building_id=b.id and"
            + " u.published=true) price_from from nn_buildings b where b.published=true "
            + (city == null || city.isEmpty() ? "" : "and b.city=? ")
            + "order by b.id",
        city == null || city.isEmpty() ? new Object[] {} : new Object[] {city});
  }

  public Row building(long id) {
    Row b = s.db.one("select * from nn_buildings where id=? and published=true", id);
    Rules.check(b != null, "Không tìm thấy tòa nhà.");
    b.put(
        "units",
        s.db.list(
            unitSelect() + "where u.building_id=? and u.published=true order by u.monthly_rent",
            id));
    b.put("amenity_list", b.str("amenities").split("\\|"));
    return b;
  }

  public List<Row> cities() {
    return s.db.list(
        "select b.city,count(distinct b.id) building_count,count(u.id) unit_count from nn_buildings"
            + " b left join nn_units u on u.building_id=b.id and u.published=true where"
            + " b.published=true group by b.city order by b.city");
  }

  public List<Row> districts(String city) {
    return s.db.list(
        "select distinct district from nn_buildings where published=true "
            + (city == null || city.isEmpty() ? "" : "and city=? ")
            + "order by district",
        city == null || city.isEmpty() ? new Object[] {} : new Object[] {city});
  }

  public Row search(Map<String, String> input) {
    Map<String, String> f = new LinkedHashMap<>();
    for (String k :
        Arrays.asList(
            "city",
            "district",
            "type",
            "beds",
            "budget",
            "start",
            "months",
            "sort",
            "page",
            "building")) f.put(k, Rules.optional(input.get(k), 100));
    List<Object> args = new ArrayList<>();
    StringBuilder where =
        new StringBuilder(
            "where u.published=true and b.published=true and u.operational_status='READY' ");
    for (String key : Arrays.asList("city", "district"))
      if (!f.get(key).isEmpty()) {
        where.append("and b.").append(key).append("=? ");
        args.add(f.get(key));
      }
    if (!f.get("type").isEmpty()) {
      Rules.check(TYPES.containsKey(f.get("type")), "Loại căn hộ không hợp lệ.");
      where.append("and u.type=? ");
      args.add(f.get("type"));
    }
    if (!f.get("beds").isEmpty()) {
      int beds = Rules.integer(f.get("beds"), 0, 5, "Số phòng ngủ");
      where.append(beds >= 3 ? "and u.bedrooms>=? " : "and u.bedrooms=? ");
      args.add(beds);
    }
    if (!f.get("budget").isEmpty()) {
      long max = Rules.amount(f.get("budget"), "Ngân sách");
      where.append("and u.monthly_rent<=? ");
      args.add(max);
    }
    if (!f.get("building").isEmpty()) {
      long id = Rules.amount(f.get("building"), "Tòa nhà");
      where.append("and b.id=? ");
      args.add(id);
    }
    String order = "u.featured desc,u.id desc";
    if ("price-asc".equals(f.get("sort"))) order = "u.monthly_rent,u.id";
    if ("price-desc".equals(f.get("sort"))) order = "u.monthly_rent desc,u.id";
    if ("area-desc".equals(f.get("sort"))) order = "u.area desc,u.id";
    List<Row> all = s.db.list(unitSelect() + where + "order by " + order, args.toArray());
    LocalDate start = f.get("start").isEmpty() ? s.today() : Rules.date(f.get("start"));
    Rules.check(
        !start.isBefore(s.today()) && !start.isAfter(s.today().plusYears(1)),
        "Ngày nhận căn hộ phải nằm trong 12 tháng tới.");
    int months =
        Rules.integer(f.get("months").isEmpty() ? "1" : f.get("months"), 1, 24, "Số tháng");
    LocalDate end = start.plusMonths(months);
    all.removeIf(u -> !schedule.available(u.id(), start, end, 0));
    int pages = Math.max(1, (all.size() + 8) / 9);
    int page =
        Math.min(
            pages,
            Rules.integer(f.get("page").isEmpty() ? "1" : f.get("page"), 1, 100000, "Trang"));
    int offset = (page - 1) * 9;
    return new Row()
        .with(
            "items",
            new ArrayList<>(
                all.subList(Math.min(offset, all.size()), Math.min(offset + 9, all.size()))))
        .with("total", all.size())
        .with("page", page)
        .with("pages", pages)
        .with("filters", f)
        .with("search_start", start)
        .with("search_end", end);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public boolean favorite(Row actor, long id) {
    unit(id);
    if (s.db.count(
            "select count(*) from nn_favorites where account_id=? and unit_id=?", actor.id(), id)
        > 0) {
      s.db.update("delete from nn_favorites where account_id=? and unit_id=?", actor.id(), id);
      return false;
    }
    s.db.update("insert into nn_favorites(account_id,unit_id) values(?,?)", actor.id(), id);
    return true;
  }

  public List<Row> favorites(Row actor) {
    return s.db.list(
        unitSelect()
            + "join nn_favorites f on f.unit_id=u.id where f.account_id=? and u.published=true and"
            + " b.published=true order by u.id desc",
        actor.id());
  }
}
