package com.javaweb.nnland.controller.web;

import com.javaweb.nnland.model.*;
import com.javaweb.nnland.service.*;
import com.javaweb.nnland.utils.*;
import java.time.ZoneId;
import java.util.*;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountController {
  private final Support s;
  private final PortalQueryService query;
  private final CatalogService catalog;
  private final BookingService bookings;
  private final AccountService accounts;
  private final PaymentService payments;
  private final InvoiceService invoices;
  private final ResidentService residents;
  private final DocumentService documents;

  public AccountController(
      Support s,
      PortalQueryService query,
      CatalogService catalog,
      BookingService bookings,
      AccountService accounts,
      PaymentService payments,
      InvoiceService invoices,
      ResidentService residents,
      DocumentService documents) {
    this.s = s;
    this.query = query;
    this.catalog = catalog;
    this.bookings = bookings;
    this.accounts = accounts;
    this.payments = payments;
    this.invoices = invoices;
    this.residents = residents;
    this.documents = documents;
  }

  private void layout(Model m, String section, String title) {
    m.addAttribute("section", section);
    m.addAttribute("pageTitle", title);
    m.addAttribute("workspace", true);
    m.addAttribute("adminView", false);
  }

  @GetMapping("/tai-khoan")
  public String home(Model m) {
    Row u = s.requireUser();
    layout(m, "tong-quan", "Không gian của bạn");
    m.addAttribute("leases", query.leases(u));
    m.addAttribute(
        "notifications",
        s.db.list(
            "select * from nn_notifications where account_id=? order by id desc limit 5", u.id()));
    m.addAttribute(
        "pending",
        s.db.count(
            "select count(*) from nn_payments where account_id=? and status in"
                + " ('PENDING','PARTIAL','RECONCILE')",
            u.id()));
    m.addAttribute(
        "openTickets",
        s.db.count(
            "select count(*) from nn_tickets where account_id=? and status in"
                + " ('OPEN','IN_PROGRESS','WAITING')",
            u.id()));
    return "account/page";
  }

  @GetMapping("/tai-khoan/{section}")
  public String page(@PathVariable String section, Model m) {
    Map<String, String> titles = new LinkedHashMap<>();
    titles.put("thong-tin", "Thông tin cá nhân");
    titles.put("dat-thue", "Đơn đặt thuê");
    titles.put("luu-tru", "Thông tin lưu trú");
    titles.put("hoa-don", "Hóa đơn & biên lai");
    titles.put("ho-tro", "Yêu cầu hỗ trợ");
    titles.put("yeu-cau", "Yêu cầu của bạn");
    titles.put("lich-hen", "Lịch hẹn & liên hệ");
    titles.put("yeu-thich", "Căn hộ yêu thích");
    titles.put("thong-bao", "Thông báo");
    Rules.check(titles.containsKey(section), "Không tìm thấy mục tài khoản.");
    Row u = s.requireUser();
    layout(m, section, titles.get(section));
    m.addAttribute("rows", query.own(u, section));
    m.addAttribute("leases", query.leases(u));
    if (section.equals("yeu-thich")) m.addAttribute("favorites", catalog.favorites(u));
    return "account/page";
  }

  @PostMapping("/tai-khoan/thong-tin")
  public String profile(@RequestParam Map<String, String> f, RedirectAttributes flash) {
    accounts.profile(
        s.requireUser(), f.get("full_name"), f.get("birth_year"), f.get("phone"), f.get("gender"));
    flash.addFlashAttribute("success", "Đã cập nhật thông tin cá nhân.");
    return "redirect:/tai-khoan/thong-tin";
  }

  @PostMapping("/tai-khoan/mat-khau")
  public String password(
      @RequestParam String current_password,
      @RequestParam String password,
      @RequestParam String password_confirm,
      RedirectAttributes flash) {
    Rules.check(password.equals(password_confirm), "Hai mật khẩu mới chưa trùng khớp.");
    accounts.changePassword(s.requireUser(), current_password, password);
    flash.addFlashAttribute("success", "Đã đổi mật khẩu. Vui lòng đăng nhập lại.");
    return "redirect:/dang-nhap";
  }

  @GetMapping("/tai-khoan/hop-dong/{id}")
  public String lease(@PathVariable long id, Model m) {
    Row lease = s.lease(id);
    s.leaseAccess(s.requireUser(), lease);
    layout(m, "luu-tru", lease.str("code"));
    m.addAttribute("lease", lease);
    m.addAttribute(
        "periods",
        s.db.list(
            "select bo.*,p.id payment_id,p.status payment_status from nn_bookings bo join"
                + " nn_payments p on p.booking_id=bo.id where bo.lease_id=? order by bo.id",
            id));
    m.addAttribute(
        "invoices", s.db.list("select * from nn_invoices where lease_id=? order by id desc", id));
    return "account/lease";
  }

  @GetMapping("/dat-thue/{id}")
  public String quote(
      @PathVariable long id,
      @RequestParam(required = false) Long lease,
      @RequestParam(required = false) String start,
      @RequestParam(defaultValue = "1") String months,
      Model m) {
    Row actor = s.requireUser();
    m.addAttribute("quote", bookings.quote(actor, id, lease, start, months));
    m.addAttribute("unit", lease == null ? catalog.unit(id) : catalog.leaseUnit(id));
    m.addAttribute("leaseId", lease);
    m.addAttribute("pageTitle", lease == null ? "Đăng ký thuê căn hộ" : "Gia hạn hợp đồng");
    return "booking";
  }

  @PostMapping("/dat-thue/{id}")
  public String book(@PathVariable long id, @RequestParam Map<String, String> f) {
    Long lease =
        f.getOrDefault("lease_id", "").isEmpty()
            ? null
            : Rules.amount(f.get("lease_id"), "Hợp đồng");
    long p =
        bookings.create(
            s.requireUser(),
            id,
            lease,
            f.get("start"),
            f.get("months"),
            Rules.amount(f.get("quoted_monthly"), "Giá đã báo"),
            Rules.amount(f.get("quoted_deposit"), "Cọc đã báo"),
            f.get("terms_version"),
            f.get("fee_signature"),
            "on".equals(f.get("accepted")));
    return "redirect:/thanh-toan/" + p;
  }

  @GetMapping("/thanh-toan/{id}")
  public String payment(@PathVariable long id, Model m) {
    Row p = s.payment(id);
    s.paymentAccess(s.requireUser(), p);
    m.addAttribute("payment", p);
    m.addAttribute(
        "receipts", s.db.list("select * from nn_receipts where payment_id=? order by id desc", id));
    m.addAttribute(
        "refunds", s.db.list("select * from nn_refunds where payment_id=? order by id desc", id));
    m.addAttribute(
        "bankReady",
        !s.setting("bank_bin", "").isEmpty() && !s.setting("bank_account", "").isEmpty());
    boolean open =
        p.get("booking_id") == null
            || ("HOLD".equals(p.str("booking_status"))
                    || "PAYMENT_REVIEW".equals(p.str("booking_status")))
                && p.time("hold_until").isAfter(s.now());
    m.addAttribute(
        "canTransfer",
        open
            && p.num("remaining") > 0
            && !Arrays.asList("CANCELLED", "REFUNDED", "REFUND_PENDING").contains(p.str("status")));
    if (p.get("hold_until") != null)
      m.addAttribute(
          "deadline",
          p.time("hold_until").atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant().toEpochMilli());
    m.addAttribute("pageTitle", "Thanh toán · " + p.str("code"));
    return "payment";
  }

  @PostMapping("/thanh-toan/{id}/bao-chuyen-khoan")
  public String claim(@PathVariable long id, @RequestParam String note, RedirectAttributes flash) {
    payments.claim(s.requireUser(), id, note);
    flash.addFlashAttribute(
        "success",
        "Đã báo quản lý kiểm tra giao dịch. Trạng thái chỉ được xác nhận sau khi NNland đối chiếu"
            + " tiền thực nhận.");
    return "redirect:/thanh-toan/" + id;
  }

  @GetMapping(value = "/thanh-toan/{id}/qr", produces = "image/png")
  @ResponseBody
  public byte[] qr(@PathVariable long id) {
    Row p = s.payment(id);
    s.paymentAccess(s.requireUser(), p);
    Rules.check(
        p.num("remaining") > 0
            && !Arrays.asList("CANCELLED", "EXPIRED", "REFUNDED", "REFUND_PENDING")
                .contains(p.str("status")),
        "Yêu cầu không còn ở bước thanh toán.");
    if (p.get("booking_id") != null)
      Rules.check(
          Arrays.asList("HOLD", "PAYMENT_REVIEW").contains(p.str("booking_status"))
              && p.time("hold_until").isAfter(s.now()),
          "Đơn đã hết thời gian giữ chỗ. Vui lòng liên hệ quản lý.");
    return VietQr.png(
        s.setting("bank_bin", ""),
        s.setting("bank_account", ""),
        p.num("remaining"),
        p.str("code"));
  }

  @GetMapping("/hoa-don/{id}")
  public String invoice(@PathVariable long id, Model m) {
    Row i = invoices.invoice(id);
    s.paymentAccess(s.requireUser(), s.payment(i.num("payment_id")));
    layout(m, "hoa-don", i.str("code"));
    m.addAttribute("invoice", i);
    return "account/invoice";
  }

  private ResponseEntity<byte[]> file(byte[] bytes, String name, String type) {
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(type))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
        .header(HttpHeaders.CACHE_CONTROL, "no-store")
        .body(bytes);
  }

  @GetMapping("/hop-dong/{id}/pdf")
  public ResponseEntity<byte[]> contractPdf(@PathVariable long id) {
    Row l = s.lease(id);
    s.leaseAccess(s.requireUser(), l);
    return file(documents.contract(id), l.str("code") + ".pdf", "application/pdf");
  }

  @GetMapping("/hoa-don/{id}/pdf")
  public ResponseEntity<byte[]> invoicePdf(@PathVariable long id) {
    Row i = invoices.invoice(id);
    s.paymentAccess(s.requireUser(), s.payment(i.num("payment_id")));
    return file(documents.invoice(id), i.str("code") + ".pdf", "application/pdf");
  }

  @GetMapping("/thanh-toan/{id}/phieu-thu")
  public ResponseEntity<byte[]> receiptPdf(@PathVariable long id) {
    Row p = s.payment(id);
    s.paymentAccess(s.requireUser(), p);
    return file(documents.receipt(id), p.str("code") + ".pdf", "application/pdf");
  }

  @PostMapping("/yeu-thich/{id}")
  public String favorite(@PathVariable long id, RedirectAttributes flash) {
    boolean saved = catalog.favorite(s.requireUser(), id);
    flash.addFlashAttribute("success", saved ? "Đã lưu căn hộ yêu thích." : "Đã bỏ lưu căn hộ.");
    return "redirect:/tai-khoan/yeu-thich";
  }

  @PostMapping("/tai-khoan/ho-tro")
  public String ticket(
      @RequestParam long lease_id,
      @RequestParam String category,
      @RequestParam String subject,
      @RequestParam String description,
      @RequestParam(defaultValue = "NORMAL") String priority,
      RedirectAttributes flash) {
    long id = residents.ticket(s.requireUser(), lease_id, category, subject, description, priority);
    flash.addFlashAttribute("success", "Đã gửi yêu cầu hỗ trợ.");
    return "redirect:/tai-khoan/ho-tro/" + id;
  }

  @GetMapping("/tai-khoan/ho-tro/{id}")
  public String ticket(@PathVariable long id, Model m) {
    Row t = residents.ticket(id);
    Row actor = s.requireUser();
    if (t.num("account_id") != actor.id()) s.buildingAccess(actor, t.num("building_id"));
    layout(m, "ho-tro", t.str("code"));
    m.addAttribute("ticket", t);
    m.addAttribute(
        "events",
        s.db.list(
            "select e.*,a.full_name from nn_ticket_events e join nn_accounts a on a.id=e.actor_id"
                + " where e.ticket_id=? order by e.id",
            id));
    return "account/ticket";
  }

  @PostMapping("/tai-khoan/ho-tro/{id}/phan-hoi")
  public String reply(
      @PathVariable long id,
      @RequestParam String body,
      @RequestParam(defaultValue = "false") boolean reopen) {
    residents.replyTicket(s.requireUser(), id, body, reopen);
    return "redirect:/tai-khoan/ho-tro/" + id;
  }

  @PostMapping("/tai-khoan/yeu-cau")
  public String request(
      @RequestParam(required = false) Long lease_id,
      @RequestParam(required = false) Long booking_id,
      @RequestParam String type,
      @RequestParam(defaultValue = "") String requested_date,
      @RequestParam String note,
      RedirectAttributes flash) {
    residents.request(s.requireUser(), lease_id, booking_id, type, requested_date, note);
    flash.addFlashAttribute("success", "Yêu cầu đã được ghi nhận.");
    return "redirect:/tai-khoan/yeu-cau";
  }

  @PostMapping("/tai-khoan/thong-bao/doc")
  public String read(@RequestParam(defaultValue = "0") long id) {
    Row actor = s.requireUser();
    if (id == 0)
      s.db.update("update nn_notifications set is_read=true where account_id=?", actor.id());
    else
      s.db.update(
          "update nn_notifications set is_read=true where account_id=? and id=?", actor.id(), id);
    return "redirect:/tai-khoan/thong-bao";
  }
}
