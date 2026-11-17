package com.javaweb.nnland.controller.web;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.service.*;
import java.util.*;
import javax.servlet.http.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class ViewAdvice {
  private final Support s;
  private final CatalogService catalog;
  private final Logger log = LoggerFactory.getLogger(ViewAdvice.class);

  @Value("${nnland.mail.enabled:false}")
  private boolean mailEnabled;

  public ViewAdvice(Support s, CatalogService catalog) {
    this.s = s;
    this.catalog = catalog;
  }

  @ModelAttribute
  public void common(Model m, HttpServletRequest request) {
    Row user = s.user();
    if (user != null) {
      Row safe = new Row();
      safe.putAll(user);
      safe.remove("password_hash");
      m.addAttribute("currentUser", safe);
      m.addAttribute(
          "unread",
          s.db.count(
              "select count(*) from nn_notifications where account_id=? and is_read=false",
              user.id()));
      if (s.staff(user))
        m.addAttribute(
            "staffBuildings",
            s.db.list(
                "select b.* from nn_buildings b where "
                    + s.scope(user, "b.id")
                    + " order by b.name"));
    }
    m.addAttribute("isStaff", s.staff(user));
    m.addAttribute("isAdmin", s.admin(user));
    m.addAttribute("meta", s.settings());
    m.addAttribute("cities", catalog.cities());
    m.addAttribute("types", CatalogService.TYPES);
    m.addAttribute("today", s.today());
    m.addAttribute("now", s.now().withNano(0));
    m.addAttribute("pageTitle", "NNland · Không gian sống của bạn");
    m.addAttribute("ctx", request.getContextPath());
    m.addAttribute("mailEnabled", mailEnabled);
    m.addAttribute(
        "locationOptions",
        s.db.list(
            "select distinct city,district from nn_buildings where published=true order by"
                + " city,district"));
    Set<Long> favorites = new HashSet<>();
    if (user != null)
      for (Row f : s.db.list("select unit_id from nn_favorites where account_id=?", user.id()))
        favorites.add(f.num("unit_id"));
    m.addAttribute("favoriteIds", favorites);
    m.addAttribute(
        "images",
        Arrays.asList(
            "hero.webp",
            "apartment-interior.webp",
            "apartment-living.webp",
            "apartment-room.webp",
            "residence-exterior.webp",
            "building-riverside.webp",
            "building-central.webp",
            "building-westlake.webp",
            "city-hcm.webp",
            "city-hanoi.webp",
            "city-danang.webp",
            "amenity-pool.webp",
            "lobby.webp"));
  }

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String business(BusinessException e, Model m) {
    m.addAttribute("errorTitle", "Thông tin cần được kiểm tra lại");
    m.addAttribute("errorMessage", e.getMessage());
    return "error";
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public String denied(Model m) {
    m.addAttribute("errorTitle", "Bạn chưa có quyền truy cập");
    m.addAttribute(
        "errorMessage", "Vui lòng đăng nhập đúng tài khoản hoặc liên hệ quản lý phụ trách.");
    return "error";
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public String conflict(Model m) {
    m.addAttribute("errorTitle", "Thông tin đã được ghi nhận trước đó");
    m.addAttribute(
        "errorMessage",
        "Mã căn, tài khoản, kỳ hóa đơn hoặc mã giao dịch đã tồn tại. Hãy tải lại trang để kiểm tra"
            + " kết quả trước khi gửi lại.");
    return "error";
  }

  @ExceptionHandler(TransientDataAccessException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public String concurrent(Model m) {
    m.addAttribute("errorTitle", "Dữ liệu đang được cập nhật");
    m.addAttribute(
        "errorMessage",
        "Có một thao tác khác đang được xử lý cùng lúc. Hãy tải lại trang và kiểm tra kết quả trước"
            + " khi gửi lại; không chuyển tiền thêm.");
    return "error";
  }
}
