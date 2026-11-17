package com.javaweb.nnland.controller.web;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.service.*;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PublicController {
  private final Support s;
  private final CatalogService catalog;
  private final AccountService accounts;
  private final ResidentService residents;

  public PublicController(
      Support s, CatalogService catalog, AccountService accounts, ResidentService residents) {
    this.s = s;
    this.catalog = catalog;
    this.accounts = accounts;
    this.residents = residents;
  }

  @GetMapping("/")
  public String home(Model m) {
    m.addAttribute("activeNav", "home");
    m.addAttribute("featured", catalog.featured());
    m.addAttribute(
        "buildings", catalog.buildings("").subList(0, Math.min(3, catalog.buildings("").size())));
    m.addAttribute("cityStats", catalog.cities());
    m.addAttribute("unitCount", s.db.count("select count(*) from nn_units where published=true"));
    return "home";
  }

  @GetMapping("/can-ho")
  public String apartments(@RequestParam Map<String, String> filters, Model m) {
    m.addAttribute("activeNav", "apartments");
    m.addAttribute("pageTitle", "Căn hộ cho thuê");
    m.addAttribute("result", catalog.search(filters));
    m.addAttribute("districts", catalog.districts(filters.get("city")));
    return "apartments";
  }

  @GetMapping("/can-ho/{id}")
  public String apartment(@PathVariable long id, Model m) {
    Row u = catalog.unit(id);
    m.addAttribute("activeNav", "apartments");
    m.addAttribute("pageTitle", u.str("name"));
    m.addAttribute("unit", u);
    m.addAttribute("building", catalog.building(u.num("building_id")));
    return "apartment-detail";
  }

  @GetMapping("/toa-nha")
  public String buildings(@RequestParam(defaultValue = "") String city, Model m) {
    m.addAttribute("activeNav", "buildings");
    m.addAttribute("pageTitle", "Hệ thống tòa nhà");
    m.addAttribute("buildings", catalog.buildings(city));
    m.addAttribute("selectedCity", city);
    return "buildings";
  }

  @GetMapping("/toa-nha/{id}")
  public String building(@PathVariable long id, Model m) {
    Row b = catalog.building(id);
    m.addAttribute("building", b);
    m.addAttribute("pageTitle", b.str("name"));
    m.addAttribute("activeNav", "buildings");
    return "building-detail";
  }

  @GetMapping("/gioi-thieu")
  public String about(Model m) {
    m.addAttribute("activeNav", "about");
    m.addAttribute("pageTitle", "Về NNland");
    return "about";
  }

  @GetMapping("/dieu-khoan")
  public String terms(Model m) {
    m.addAttribute("pageTitle", "Điều khoản thuê căn hộ");
    m.addAttribute("terms", s.terms());
    return "terms";
  }

  @GetMapping("/lien-he")
  public String contact(@RequestParam(required = false) Long unit, Model m) {
    if (unit != null) m.addAttribute("unit", catalog.unit(unit));
    m.addAttribute("activeNav", "contact");
    m.addAttribute("pageTitle", "Liên hệ NNland");
    return "contact";
  }

  @PostMapping("/lien-he")
  public String sendContact(@RequestParam Map<String, String> f, RedirectAttributes flash) {
    if (f.getOrDefault("website", "").isEmpty()) residents.contact(s.user(), f);
    flash.addFlashAttribute(
        "success", "Đã tiếp nhận thông tin. NNland sẽ liên hệ với bạn để xác nhận lịch hẹn.");
    return "redirect:/lien-he";
  }

  @GetMapping("/dang-nhap")
  public String login(Model m) {
    m.addAttribute("pageTitle", "Đăng nhập");
    return "auth/login";
  }

  @GetMapping("/dang-ky")
  public String register(Model m) {
    m.addAttribute("pageTitle", "Tạo tài khoản");
    return "auth/register";
  }

  @PostMapping("/dang-ky")
  public String register(@RequestParam Map<String, String> f, RedirectAttributes flash) {
    com.javaweb.nnland.utils.Rules.check(
        Objects.equals(f.get("password"), f.get("password_confirm")),
        "Hai mật khẩu chưa trùng khớp.");
    com.javaweb.nnland.utils.Rules.check(
        "on".equals(f.get("agree")), "Bạn cần đồng ý điều khoản sử dụng.");
    accounts.register(
        f.get("email"),
        f.get("username"),
        f.get("full_name"),
        f.get("birth_year"),
        f.get("phone"),
        f.getOrDefault("gender", "OTHER"),
        f.get("password"));
    flash.addFlashAttribute(
        "success", "Tài khoản đã được tạo. Bạn có thể đăng nhập bằng email hoặc tên đăng nhập.");
    return "redirect:/dang-nhap";
  }

  @GetMapping("/quen-mat-khau")
  public String forgot(Model m) {
    m.addAttribute("pageTitle", "Quên mật khẩu");
    return "auth/forgot";
  }

  @PostMapping("/quen-mat-khau")
  public String forgot(@RequestParam String email, RedirectAttributes flash) {
    accounts.requestReset(email);
    flash.addFlashAttribute(
        "success",
        "Nếu email thuộc tài khoản đang hoạt động, yêu cầu đặt lại mật khẩu đã được tiếp nhận. Vui"
            + " lòng kiểm tra hộp thư.");
    return "redirect:/quen-mat-khau";
  }

  @GetMapping("/dat-lai-mat-khau")
  public String reset(@RequestParam String token, Model m) {
    m.addAttribute("token", token);
    m.addAttribute("pageTitle", "Đặt lại mật khẩu");
    return "auth/reset";
  }

  @PostMapping("/dat-lai-mat-khau")
  public String reset(
      @RequestParam String token,
      @RequestParam String password,
      @RequestParam String password_confirm,
      RedirectAttributes flash) {
    com.javaweb.nnland.utils.Rules.check(
        password.equals(password_confirm), "Hai mật khẩu chưa trùng khớp.");
    accounts.reset(token, password);
    flash.addFlashAttribute("success", "Mật khẩu đã được cập nhật. Hãy đăng nhập lại.");
    return "redirect:/dang-nhap";
  }

  @GetMapping("/trang-chu")
  public String oldHome() {
    return "redirect:/";
  }

  @GetMapping("/san-pham")
  public String oldList() {
    return "redirect:/can-ho";
  }

  @GetMapping("/login")
  public String oldLogin() {
    return "redirect:/dang-nhap";
  }
}
