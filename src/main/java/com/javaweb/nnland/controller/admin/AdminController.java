package com.javaweb.nnland.controller.admin;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.service.*;
import com.javaweb.nnland.utils.Rules;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
  private final Support s;
  private final PortalQueryService query;
  private final AdminService admin;
  private final BookingService bookings;
  private final PaymentService payments;
  private final ResidentService residents;
  private final InvoiceService invoices;
  private final FinanceService finance;
  private final DocumentService documents;
  private final UploadService uploads;
  public static final Map<String, String> TITLES = new LinkedHashMap<>();

  static {
    String[][] pairs = {
      {"toa-nha", "Quản lý tòa nhà"},
      {"can-ho", "Quản lý căn hộ"},
      {"don-thue", "Đơn đặt thuê"},
      {"thanh-toan", "Thanh toán & đối soát"},
      {"hop-dong", "Hợp đồng & bàn giao"},
      {"hoa-don", "Hóa đơn điện nước"},
      {"ho-tro", "Yêu cầu hỗ trợ"},
      {"lich-hen", "Lịch hẹn & liên hệ"},
      {"yeu-cau", "Hủy & trả phòng"},
      {"hoan-tien", "Hoàn tiền"},
      {"tai-khoan", "Tài khoản & phân quyền"},
      {"bao-cao", "Doanh thu & báo cáo"},
      {"cai-dat", "Cài đặt hệ thống"},
      {"thu-di", "Email thông báo"},
      {"nhat-ky", "Nhật ký thao tác"}
    };
    for (String[] p : pairs) TITLES.put(p[0], p[1]);
  }

  public AdminController(
      Support s,
      PortalQueryService query,
      AdminService admin,
      BookingService bookings,
      PaymentService payments,
      ResidentService residents,
      InvoiceService invoices,
      FinanceService finance,
      DocumentService documents,
      UploadService uploads) {
    this.s = s;
    this.query = query;
    this.admin = admin;
    this.bookings = bookings;
    this.payments = payments;
    this.residents = residents;
    this.invoices = invoices;
    this.finance = finance;
    this.documents = documents;
    this.uploads = uploads;
  }

  private Row actor() {
    Row a = s.requireUser();
    Rules.check(s.staff(a), "Tài khoản chưa có quyền quản lý.");
    return a;
  }

  private void layout(Model m, String section) {
    m.addAttribute("workspace", true);
    m.addAttribute("adminView", true);
    m.addAttribute("section", section);
    m.addAttribute("pageTitle", TITLES.getOrDefault(section, "Tổng quan vận hành"));
    m.addAttribute("adminTitles", TITLES);
  }

  @GetMapping({"", "/", "/home"})
  public String dashboard(Model m) {
    Row a = actor();
    layout(m, "tong-quan");
    LocalDate first = s.today().withDayOfMonth(1);
    m.addAttribute("report", finance.report(a, first, s.today(), null));
    m.addAttribute("yearReport", finance.report(a, s.today().withDayOfYear(1), s.today(), null));
    m.addAttribute("chart", finance.chart(a, null));
    String scope = s.scope(a, "b.id");
    m.addAttribute(
        "unitCount",
        s.db.count(
            "select count(*) from nn_units u join nn_buildings b on b.id=u.building_id where "
                + scope));
    m.addAttribute(
        "activeCount",
        s.db.count(
            "select count(*) from nn_leases l join nn_units u on u.id=l.unit_id join nn_buildings b"
                + " on b.id=u.building_id where "
                + scope
                + " and l.status in ('ACTIVE','ENDING')"));
    m.addAttribute(
        "reviewCount",
        s.db.count(
            "select count(*) from nn_payments p join nn_bookings bo on bo.id=p.booking_id join"
                + " nn_units u on u.id=bo.unit_id join nn_buildings b on b.id=u.building_id where "
                + scope
                + " and p.status in ('PENDING','PARTIAL','RECONCILE') and (p.claimed_at is not null"
                + " or p.status<>'PENDING')"));
    m.addAttribute(
        "ticketsCount",
        s.db.count(
            "select count(*) from nn_tickets t join nn_units u on u.id=t.unit_id join nn_buildings"
                + " b on b.id=u.building_id where "
                + scope
                + " and t.status in ('OPEN','IN_PROGRESS','WAITING')"));
    m.addAttribute("recent", query.admin(a, "thanh-toan", Collections.emptyMap()).get("items"));
    return "admin/dashboard";
  }

  @GetMapping("/{section}")
  public String list(@PathVariable String section, @RequestParam Map<String, String> f, Model m) {
    Row a = actor();
    Rules.check(TITLES.containsKey(section), "Không tìm thấy mục quản lý.");
    layout(m, section);
    m.addAttribute("filters", f);
    if ("bao-cao".equals(section)) return report(f, m);
    if ("cai-dat".equals(section)) {
      s.requireAdmin(a);
      m.addAttribute("terms", s.terms());
      return "admin/settings";
    }
    m.addAttribute("result", query.admin(a, section, f));
    if ("tai-khoan".equals(section)) {
      m.addAttribute(
          "managers",
          s.db.list(
              "select id,full_name,email from nn_accounts where role='MANAGER' and active=true"
                  + " order by full_name"));
      m.addAttribute(
          "assignments",
          s.db.list(
              "select bs.*,b.name building_name,a.full_name,a.email from nn_building_staff bs join"
                  + " nn_buildings b on b.id=bs.building_id join nn_accounts a on"
                  + " a.id=bs.account_id order by b.id,a.id"));
    }
    return "admin/list";
  }

  @GetMapping({"/toa-nha/moi", "/toa-nha/{id}/sua"})
  public String buildingForm(@PathVariable(required = false) Long id, Model m) {
    Row a = actor();
    layout(m, "toa-nha");
    Row b = new Row();
    if (id == null) {
      s.requireAdmin(a);
      b.with("image", "/nnland/images/building-riverside.webp").with("published", true);
    } else {
      s.buildingAccess(a, id);
      b = s.db.one("select * from nn_buildings where id=?", id);
      Rules.check(b != null, "Tòa nhà không tồn tại.");
    }
    m.addAttribute("form", b);
    return "admin/building-form";
  }

  @PostMapping("/toa-nha/luu")
  public String saveBuilding(
      @RequestParam Map<String, String> f,
      @RequestParam(required = false) MultipartFile photo,
      RedirectAttributes flash) {
    String image = uploads.image(photo);
    if (image != null) f.put("image", image);
    Long id = f.getOrDefault("id", "").isEmpty() ? null : Rules.amount(f.get("id"), "Tòa nhà");
    admin.building(actor(), id, f);
    flash.addFlashAttribute("success", "Đã lưu tòa nhà.");
    return "redirect:/admin/toa-nha";
  }

  @GetMapping({"/can-ho/moi", "/can-ho/{id}/sua"})
  public String unitForm(@PathVariable(required = false) Long id, Model m) {
    Row a = actor();
    layout(m, "can-ho");
    Row u = new Row();
    if (id != null) {
      u = s.unit(id, false);
      s.buildingAccess(a, u.num("building_id"));
    } else {
      u.with("type", "STANDARD")
          .with("bedrooms", 1)
          .with("bathrooms", 1)
          .with("area", 52)
          .with("floor_no", 5)
          .with("monthly_rent", 9800000)
          .with("deposit_amount", 0)
          .with("service_fee", 350000)
          .with("electricity_rate", 3500)
          .with("water_rate", 18000)
          .with("parking_fee", 150000)
          .with("image", "/nnland/images/apartment-interior.webp")
          .with("image2", "/nnland/images/apartment-living.webp")
          .with("image3", "/nnland/images/apartment-room.webp")
          .with("operational_status", "READY")
          .with("published", true);
    }
    m.addAttribute("form", u);
    return "admin/unit-form";
  }

  @PostMapping("/can-ho/luu")
  public String saveUnit(
      @RequestParam Map<String, String> f,
      @RequestParam(required = false) MultipartFile photo,
      @RequestParam(required = false) MultipartFile photo2,
      @RequestParam(required = false) MultipartFile photo3,
      RedirectAttributes flash) {
    Row a = actor();
    String image = uploads.image(photo);
    if (image != null) f.put("image", image);
    String second = uploads.image(photo2), third = uploads.image(photo3);
    if (second != null) f.put("image2", second);
    if (third != null) f.put("image3", third);
    Long id = f.getOrDefault("id", "").isEmpty() ? null : Rules.amount(f.get("id"), "Căn hộ");
    admin.unit(a, id, f);
    flash.addFlashAttribute(
        "success", "Đã lưu căn hộ. Các đơn/hợp đồng đã xác nhận vẫn giữ giá và phí đã chốt.");
    return "redirect:/admin/can-ho";
  }

  @GetMapping("/can-ho/{id}/lich")
  public String calendar(@PathVariable long id, Model m) {
    Row a = actor(), u = s.unit(id, false);
    s.buildingAccess(a, u.num("building_id"));
    layout(m, "can-ho");
    m.addAttribute("unit", u);
    m.addAttribute(
        "leases",
        s.db.list(
            "select l.*,a.full_name from nn_leases l join nn_accounts a on a.id=l.account_id where"
                + " l.unit_id=? order by l.start_date",
            id));
    m.addAttribute(
        "holds",
        s.db.list(
            "select bo.*,a.full_name from nn_bookings bo join nn_accounts a on a.id=bo.account_id"
                + " where bo.unit_id=? and bo.status in ('HOLD','PAYMENT_REVIEW') order by"
                + " bo.start_date",
            id));
    return "admin/calendar";
  }

  @GetMapping("/thanh-toan/{id}")
  public String payment(@PathVariable long id, Model m) {
    Row a = actor(), p = s.payment(id);
    s.buildingAccess(a, p.num("building_id"));
    layout(m, "thanh-toan");
    m.addAttribute("payment", p);
    m.addAttribute(
        "receipts",
        s.db.list(
            "select r.*,a.full_name confirmed_name from nn_receipts r join nn_accounts a on"
                + " a.id=r.confirmed_by where r.payment_id=? order by r.id desc",
            id));
    m.addAttribute(
        "refunds", s.db.list("select * from nn_refunds where payment_id=? order by id desc", id));
    m.addAttribute(
        "booking",
        p.get("booking_id") == null
            ? null
            : s.db.one("select * from nn_bookings where id=?", p.num("booking_id")));
    return "admin/payment";
  }

  @PostMapping("/thanh-toan/{id}/nhan-tien")
  public String receive(
      @PathVariable long id, @RequestParam Map<String, String> f, RedirectAttributes flash) {
    Rules.check(
        "on".equals(f.get("verified")),
        "Cần xác nhận đã đối chiếu giao dịch thực tế tại ngân hàng.");
    payments.receive(
        actor(), id, f.get("amount"), f.get("bank_reference"), f.get("received_at"), f.get("note"));
    flash.addFlashAttribute("success", "Đã ghi nhận phiếu thu và cập nhật các dữ liệu liên quan.");
    return "redirect:/admin/thanh-toan/" + id;
  }

  @PostMapping("/thanh-toan/{id}/doi-soat")
  public String reconcile(@PathVariable long id, RedirectAttributes flash) {
    payments.reconcile(actor(), id);
    flash.addFlashAttribute("success", "Đã xác nhận sau đối soát.");
    return "redirect:/admin/thanh-toan/" + id;
  }

  @PostMapping("/thanh-toan/{id}/hoan-tien")
  public String refund(
      @PathVariable long id,
      @RequestParam String amount,
      @RequestParam String category,
      @RequestParam String reason,
      RedirectAttributes flash) {
    payments.requestRefund(actor(), id, amount, category, reason);
    flash.addFlashAttribute("success", "Đã lập khoản hoàn chờ chuyển tiền.");
    return "redirect:/admin/thanh-toan/" + id;
  }

  @PostMapping("/hoan-tien/{id}/xac-nhan")
  public String completeRefund(
      @PathVariable long id,
      @RequestParam String bank_reference,
      @RequestParam(defaultValue = "") String verified,
      RedirectAttributes flash) {
    Rules.check("on".equals(verified), "Cần xác nhận đã chuyển tiền hoàn thực tế.");
    payments.completeRefund(actor(), id, bank_reference);
    flash.addFlashAttribute("success", "Đã xác nhận chuyển khoản hoàn.");
    return "redirect:/admin/hoan-tien";
  }

  @GetMapping("/hop-dong/{id}")
  public String lease(@PathVariable long id, Model m) {
    Row a = actor(), l = s.lease(id);
    s.buildingAccess(a, l.num("building_id"));
    layout(m, "hop-dong");
    m.addAttribute("lease", l);
    m.addAttribute(
        "periods",
        s.db.list(
            "select bo.*,p.id payment_id,p.status payment_status from nn_bookings bo join"
                + " nn_payments p on p.booking_id=bo.id where bo.lease_id=? order by bo.id",
            id));
    m.addAttribute(
        "invoices", s.db.list("select * from nn_invoices where lease_id=? order by id desc", id));
    m.addAttribute("unit", s.unit(l.num("unit_id"), false));
    return "account/lease";
  }

  @PostMapping("/hop-dong/{id}/ban-giao")
  public String checkIn(
      @PathVariable long id,
      @RequestParam String electricity,
      @RequestParam String water,
      @RequestParam(defaultValue = "") String signed,
      RedirectAttributes flash) {
    bookings.checkIn(actor(), id, electricity, water, "on".equals(signed));
    flash.addFlashAttribute("success", "Đã xác nhận ký hợp đồng và bàn giao căn hộ.");
    return "redirect:/admin/hop-dong/" + id;
  }

  @GetMapping("/hoa-don/moi")
  public String invoiceForm(
      @RequestParam(required = false) Long lease,
      @RequestParam(defaultValue = "false") boolean final_bill,
      Model m) {
    Row a = actor();
    layout(m, "hoa-don");
    List<Row> leases =
        s.db.list(
            "select l.*,u.code unit_code,b.name building_name,a.full_name from nn_leases l join"
                + " nn_units u on u.id=l.unit_id join nn_buildings b on b.id=u.building_id join"
                + " nn_accounts a on a.id=l.account_id where "
                + s.scope(a, "b.id")
                + " and l.status in ('ACTIVE','ENDING') order by l.id desc");
    m.addAttribute("leases", leases);
    if (lease == null && !leases.isEmpty()) lease = leases.get(0).id();
    if (lease != null) {
      Row l = s.lease(lease);
      s.buildingAccess(a, l.num("building_id"));
      Rules.check(
          Arrays.asList("ACTIVE", "ENDING").contains(l.str("status")),
          "Hợp đồng chưa nhận phòng hoặc đã kết thúc.");
      m.addAttribute("lease", l);
      m.addAttribute("previous", invoices.last(lease));
      m.addAttribute("suggestedFinalPeriod", invoices.nextFinalPeriod(lease));
      m.addAttribute("monthFeesCharged", invoices.monthFeesCharged(lease));
    }
    m.addAttribute(
        "period",
        final_bill && lease != null
            ? invoices.nextFinalPeriod(lease)
            : YearMonth.from(s.today()).toString());
    m.addAttribute("due", s.today().plusDays(7));
    return "admin/invoice-form";
  }

  @PostMapping("/hoa-don/tao")
  public String createInvoice(
      @RequestParam long lease_id, @RequestParam Map<String, String> f, RedirectAttributes flash) {
    long id = invoices.create(actor(), lease_id, f);
    flash.addFlashAttribute("success", "Đã lập hóa đơn và thông báo cho khách hàng.");
    return "redirect:/admin/hoa-don/" + id;
  }

  @GetMapping("/hoa-don/{id}")
  public String invoice(@PathVariable long id, Model m) {
    Row a = actor(), i = invoices.invoice(id);
    s.buildingAccess(a, i.num("building_id"));
    layout(m, "hoa-don");
    Row l = s.lease(i.num("lease_id"));
    m.addAttribute(
        "canCorrect",
        i.num("received") == 0
            && Arrays.asList("ACTIVE", "ENDING").contains(l.str("status"))
            && invoices.last(l.id()).id() == id);
    m.addAttribute("invoice", i);
    return "account/invoice";
  }

  @PostMapping("/hoa-don/{id}/dieu-chinh")
  public String correctInvoice(
      @PathVariable long id, @RequestParam Map<String, String> f, RedirectAttributes flash) {
    invoices.correct(actor(), id, f);
    flash.addFlashAttribute(
        "success", "Đã điều chỉnh hóa đơn chưa thu, cập nhật chỉ số và thông báo cho khách hàng.");
    return "redirect:/admin/hoa-don/" + id;
  }

  @GetMapping("/ho-tro/{id}")
  public String ticket(@PathVariable long id, Model m) {
    Row a = actor(), t = residents.ticket(id);
    s.buildingAccess(a, t.num("building_id"));
    layout(m, "ho-tro");
    m.addAttribute("ticket", t);
    m.addAttribute(
        "events",
        s.db.list(
            "select e.*,a.full_name from nn_ticket_events e join nn_accounts a on a.id=e.actor_id"
                + " where e.ticket_id=? order by e.id",
            id));
    m.addAttribute(
        "staff",
        s.db.list(
            "select distinct a.id,a.full_name from nn_accounts a left join nn_building_staff bs on"
                + " bs.account_id=a.id where a.active=true and (a.role='ADMIN' or (a.role='MANAGER'"
                + " and bs.building_id=?))",
            t.num("building_id")));
    return "account/ticket";
  }

  @PostMapping("/ho-tro/{id}")
  public String updateTicket(
      @PathVariable long id, @RequestParam Map<String, String> f, RedirectAttributes flash) {
    residents.updateTicket(
        actor(), id, f.get("status"), f.get("assignee_id"), f.get("assigned_to"), f.get("note"));
    flash.addFlashAttribute("success", "Đã cập nhật tiến độ hỗ trợ.");
    return "redirect:/admin/ho-tro/" + id;
  }

  @GetMapping("/lich-hen/{id}")
  public String contact(@PathVariable long id, Model m) {
    Row a = actor(),
        c =
            s.db.one(
                "select c.*,u.name unit_name,b.name building_name from nn_contacts c left join"
                    + " nn_units u on u.id=c.unit_id left join nn_buildings b on b.id=u.building_id"
                    + " where c.id=?",
                id);
    Rules.check(c != null, "Không tìm thấy liên hệ.");
    if (c.get("unit_id") == null) s.requireAdmin(a);
    else s.buildingAccess(a, s.unit(c.num("unit_id"), false).num("building_id"));
    layout(m, "lich-hen");
    m.addAttribute("contact", c);
    return "admin/contact";
  }

  @PostMapping("/lich-hen/{id}")
  public String contact(
      @PathVariable long id,
      @RequestParam String status,
      @RequestParam String note,
      RedirectAttributes flash) {
    residents.handleContact(actor(), id, status, note);
    flash.addFlashAttribute("success", "Đã cập nhật liên hệ và tạo thông báo phản hồi.");
    return "redirect:/admin/lich-hen/" + id;
  }

  @GetMapping("/yeu-cau/{id}")
  public String request(@PathVariable long id, Model m) {
    Row a = actor(),
        r =
            s.db.one(
                "select r.*,a.full_name,coalesce(l.unit_id,bo.unit_id) unit_id from"
                    + " nn_lease_requests r join nn_accounts a on a.id=r.account_id left join"
                    + " nn_leases l on l.id=r.lease_id left join nn_bookings bo on"
                    + " bo.id=r.booking_id where r.id=?",
                id);
    Rules.check(r != null, "Không tìm thấy yêu cầu.");
    Row unit = s.unit(r.num("unit_id"), false);
    s.buildingAccess(a, unit.num("building_id"));
    layout(m, "yeu-cau");
    m.addAttribute("request", r);
    m.addAttribute("unit", unit);
    if (r.get("lease_id") != null) m.addAttribute("lease", s.lease(r.num("lease_id")));
    return "admin/request";
  }

  @PostMapping("/yeu-cau/{id}")
  public String handleRequest(
      @PathVariable long id, @RequestParam Map<String, String> f, RedirectAttributes flash) {
    boolean approve = "approve".equals(f.get("decision"));
    if (approve
        && "CHECKOUT"
            .equals(s.db.one("select type from nn_lease_requests where id=?", id).str("type")))
      Rules.check(
          "on".equals(f.get("inspected")), "Cần xác nhận đã kiểm tra căn hộ và chốt công nợ.");
    residents.handleRequest(actor(), id, approve, f);
    flash.addFlashAttribute("success", "Đã xử lý yêu cầu.");
    return "redirect:/admin/yeu-cau/" + id;
  }

  @PostMapping("/tai-khoan/tao")
  public String createAccount(@RequestParam Map<String, String> f, RedirectAttributes flash) {
    admin.createAccount(actor(), f);
    flash.addFlashAttribute(
        "success",
        "Đã tạo tài khoản. Hãy bàn giao mật khẩu ban đầu và đề nghị đổi sau lần đăng nhập đầu.");
    return "redirect:/admin/tai-khoan";
  }

  @PostMapping("/tai-khoan/{id}")
  public String account(
      @PathVariable long id,
      @RequestParam String role,
      @RequestParam(defaultValue = "") String active,
      RedirectAttributes flash) {
    admin.account(actor(), id, role, "on".equals(active));
    flash.addFlashAttribute("success", "Đã cập nhật quyền truy cập; tài khoản cần đăng nhập lại.");
    return "redirect:/admin/tai-khoan";
  }

  @PostMapping("/phan-cong")
  public String assignment(
      @RequestParam long building_id,
      @RequestParam long account_id,
      @RequestParam(defaultValue = "true") boolean assign,
      RedirectAttributes flash) {
    admin.assignment(actor(), building_id, account_id, assign);
    flash.addFlashAttribute("success", "Đã cập nhật phân công quản lý.");
    return "redirect:/admin/tai-khoan";
  }

  @PostMapping("/cai-dat")
  public String settings(@RequestParam Map<String, String> f, RedirectAttributes flash) {
    admin.settings(actor(), f);
    flash.addFlashAttribute("success", "Đã cập nhật cài đặt.");
    return "redirect:/admin/cai-dat";
  }

  @PostMapping("/thu-di/{id}/thu-lai")
  public String retryMail(@PathVariable long id, RedirectAttributes flash) {
    Row a = actor();
    s.requireAdmin(a);
    s.db.update(
        "update nn_mail_outbox set status='PENDING',attempts=0,next_attempt=null where id=? and"
            + " status='FAILED'",
        id);
    s.audit(a, "RETRY_EMAIL", "MAIL", id, "Yêu cầu thử gửi lại email lỗi");
    flash.addFlashAttribute("success", "Đã đặt lại email lỗi vào hàng đợi.");
    return "redirect:/admin/thu-di";
  }

  private String report(Map<String, String> f, Model m) {
    Row a = actor();
    LocalDate from =
        f.getOrDefault("from", "").isEmpty()
            ? s.today().withDayOfMonth(1)
            : Rules.date(f.get("from"));
    LocalDate to = f.getOrDefault("to", "").isEmpty() ? s.today() : Rules.date(f.get("to"));
    Long building =
        f.getOrDefault("building", "").isEmpty()
            ? null
            : Rules.amount(f.get("building"), "Tòa nhà");
    Long unit = f.getOrDefault("unit", "").isEmpty() ? null : Rules.amount(f.get("unit"), "Căn hộ");
    String type = f.getOrDefault("type", "");
    m.addAttribute("report", finance.report(a, from, to, building, unit, type));
    m.addAttribute("chart", finance.chart(a, building, unit, type));
    m.addAttribute(
        "reportUnits",
        s.db.list(
            "select u.id,u.code,b.name building_name from nn_units u join nn_buildings b on"
                + " b.id=u.building_id where "
                + s.scope(a, "b.id")
                + " order by b.name,u.code"));
    m.addAttribute("selectedUnit", unit);
    m.addAttribute("selectedType", type);
    m.addAttribute("selectedBuilding", building);
    return "admin/reports";
  }

  @GetMapping("/bao-cao/xuat")
  public ResponseEntity<byte[]> export(
      @RequestParam String from,
      @RequestParam String to,
      @RequestParam(defaultValue = "") String building,
      @RequestParam(defaultValue = "") String unit,
      @RequestParam(defaultValue = "") String type,
      @RequestParam(defaultValue = "xlsx") String format) {
    Row a = actor();
    Row r =
        finance.report(
            a,
            Rules.date(from),
            Rules.date(to),
            building.isEmpty() ? null : Rules.amount(building, "Tòa nhà"),
            unit.isEmpty() ? null : Rules.amount(unit, "Căn hộ"),
            type);
    boolean pdf = "pdf".equals(format);
    Rules.choice(format, "pdf", "xlsx");
    s.audit(a, "EXPORT_REPORT", "REPORT", null, from + " → " + to);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"NNland-" + from + "-" + to + "." + format + "\"")
        .header(HttpHeaders.CACHE_CONTROL, "no-store")
        .contentType(
            MediaType.parseMediaType(
                pdf
                    ? "application/pdf"
                    : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(pdf ? documents.reportPdf(r) : documents.reportExcel(r));
  }
}
