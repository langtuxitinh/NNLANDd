package com.javaweb.nnland;

import static org.assertj.core.api.Assertions.assertThat;

import com.javaweb.SpringBootWebApplication;
import com.javaweb.nnland.model.Row;
import com.javaweb.nnland.service.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.regex.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/** HTTP/Jasper integration: real server-rendered JSP, no browser preview or simulated DOM. */
@SpringBootTest(
    classes = SpringBootWebApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RenderedPagesTest {
  @Autowired TestRestTemplate http;
  @LocalServerPort int port;
  @Autowired Support s;
  @Autowired BookingService bookings;
  @Autowired PaymentService payments;
  @Autowired InvoiceService invoices;
  @Autowired ResidentService residents;
  @Autowired CatalogService catalog;

  @org.junit.jupiter.api.BeforeEach
  void clearBusinessFixtures() {
    for (String table :
        new String[] {
          "nn_ticket_events",
          "nn_reset_tokens",
          "nn_audit_log",
          "nn_notifications",
          "nn_mail_outbox",
          "nn_favorites",
          "nn_receipts",
          "nn_refunds",
          "nn_bank_references",
          "nn_lease_requests",
          "nn_tickets",
          "nn_contacts",
          "nn_payments",
          "nn_invoices",
          "nn_bookings",
          "nn_leases"
        }) s.db.update("delete from " + table);
    s.db.update("update nn_units set electricity_meter=0,water_meter=0,operational_status='READY'");
  }

  @Test
  void publicPagesRenderWithLocalAssets() {
    ResponseEntity<String> home = http.getForEntity("/", String.class);
    assertThat(home.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(home.getBody()).contains("Tìm nơi ở lý tưởng", "NNland", "/nnland/images/hero.webp");
    for (String path :
        new String[] {
          "/can-ho",
          "/toa-nha",
          "/can-ho/1",
          "/toa-nha/1",
          "/gioi-thieu",
          "/lien-he",
          "/dang-nhap",
          "/dang-ky",
          "/quen-mat-khau",
          "/dieu-khoan"
        }) {
      ResponseEntity<String> response = http.getForEntity(path, String.class);
      assertThat(response.getStatusCode()).as(path).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).as(path).contains("NNland");
    }
    assertThat(http.getForEntity("/nnland/images/hero.webp", byte[].class).getBody())
        .hasSizeGreaterThan(1000);
  }

  static class NoRedirect extends SimpleClientHttpRequestFactory {
    @Override
    protected void prepareConnection(HttpURLConnection c, String method) throws IOException {
      super.prepareConnection(c, method);
      c.setInstanceFollowRedirects(false);
    }
  }

  class Session {
    final RestTemplate client = new RestTemplate(new NoRedirect());
    String cookie = "";
    String csrf = "";

    ResponseEntity<String> request(
        String path, HttpMethod method, MultiValueMap<String, String> data) {
      HttpHeaders h = new HttpHeaders();
      if (!cookie.isEmpty()) h.set(HttpHeaders.COOKIE, cookie);
      if (data != null) {
        h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        data.add("_csrf", csrf);
      }
      ResponseEntity<String> response =
          client.exchange(
              "http://localhost:" + port + path, method, new HttpEntity<>(data, h), String.class);
      List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
      if (cookies != null)
        for (String c : cookies) if (c.startsWith("JSESSIONID=")) cookie = c.split(";", 2)[0];
      if (response.getBody() != null) {
        Matcher m =
            Pattern.compile("name=\"csrf-token\" content=\"([^\"]+)\"").matcher(response.getBody());
        if (m.find()) csrf = m.group(1);
      }
      return response;
    }

    void login(String email) {
      assertThat(request("/dang-nhap", HttpMethod.GET, null).getStatusCode())
          .isEqualTo(HttpStatus.OK);
      MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
      form.add("email", email);
      form.add("password", "NNland@2026!");
      assertThat(request("/dang-nhap", HttpMethod.POST, form).getStatusCode())
          .isEqualTo(HttpStatus.FOUND);
    }

    void renders(String path) {
      ResponseEntity<String> r = request(path, HttpMethod.GET, null);
      assertThat(r.getStatusCode()).as(path).isEqualTo(HttpStatus.OK);
      assertThat(r.getBody()).as(path).contains("NNland").doesNotContain("Whitelabel Error Page");
    }
  }

  @Test
  void authenticatedResidentAndAdminJspPagesActuallyRender() {
    Row customer = s.db.one("select * from nn_accounts where email='customer@nnland.vn'"),
        admin = s.db.one("select * from nn_accounts where email='admin@nnland.vn'");
    Row unit = s.db.one("select * from nn_units order by id limit 1");
    Row quote = bookings.quote(customer, unit.id(), null, s.today().toString(), "1");
    long payment =
        bookings.create(
            customer,
            unit.id(),
            null,
            s.today().toString(),
            "1",
            quote.num("monthly"),
            quote.num("deposit"),
            quote.str("terms_version"),
            quote.str("fee_signature"),
            true);
    payments.receive(
        admin,
        payment,
        String.valueOf(s.payment(payment).num("due_amount")),
        "RENDER-FULL-" + payment,
        "",
        "Kiểm thử giao diện");
    long lease =
        s.db
            .one(
                "select lease_id from nn_bookings where id=?", s.payment(payment).num("booking_id"))
            .num("lease_id");
    bookings.checkIn(admin, lease, "0", "0", true);
    Map<String, String> f = new HashMap<>();
    f.put("period", java.time.YearMonth.from(s.today()).toString());
    f.put("reading_date", s.today().toString());
    f.put("due_date", s.today().plusDays(7).toString());
    f.put("electricity_end", "10");
    f.put("water_end", "2");
    f.put("service_fee", "350000");
    f.put("parking_fee", "150000");
    f.put("adjustment", "0");
    f.put("note", "");
    long invoice = invoices.create(admin, lease, f);
    long ticket =
        residents.ticket(
            customer, lease, "WATER", "Kiểm tra vòi nước", "Mô tả kiểm thử hiển thị", "NORMAL");
    Map<String, String> contact = new HashMap<>();
    contact.put("full_name", customer.str("full_name"));
    contact.put("email", customer.str("email"));
    contact.put("phone", customer.str("phone"));
    contact.put("unit_id", String.valueOf(unit.id()));
    contact.put("message", "Tôi muốn được tư vấn thêm.");
    long appointment = residents.contact(customer, contact);
    residents.request(
        customer, lease, null, "CHECKOUT", s.today().toString(), "Kiểm thử màn hình quyết toán");
    long request = s.db.one("select id from nn_lease_requests where lease_id=?", lease).id();
    catalog.favorite(customer, unit.id());
    Row another = s.db.one("select * from nn_units where id<>? order by id limit 1", unit.id());
    Row q = bookings.quote(customer, another.id(), null, s.today().plusDays(1).toString(), "1");
    long waiting =
        bookings.create(
            customer,
            another.id(),
            null,
            s.today().plusDays(1).toString(),
            "1",
            q.num("monthly"),
            q.num("deposit"),
            q.str("terms_version"),
            q.str("fee_signature"),
            true);
    Session resident = new Session();
    resident.login("customer@nnland.vn");
    for (String path :
        new String[] {
          "/tai-khoan",
          "/tai-khoan/thong-tin",
          "/tai-khoan/dat-thue",
          "/tai-khoan/luu-tru",
          "/tai-khoan/hoa-don",
          "/tai-khoan/ho-tro",
          "/tai-khoan/yeu-cau",
          "/tai-khoan/lich-hen",
          "/tai-khoan/yeu-thich",
          "/tai-khoan/thong-bao",
          "/tai-khoan/hop-dong/" + lease,
          "/hoa-don/" + invoice,
          "/tai-khoan/ho-tro/" + ticket,
          "/thanh-toan/" + waiting,
          "/dat-thue/" + another.id()
        }) resident.renders(path);
    Session staff = new Session();
    staff.login("admin@nnland.vn");
    for (String path :
        new String[] {
          "/admin",
          "/admin/toa-nha",
          "/admin/can-ho",
          "/admin/don-thue",
          "/admin/thanh-toan",
          "/admin/hop-dong",
          "/admin/hoa-don",
          "/admin/ho-tro",
          "/admin/lich-hen",
          "/admin/yeu-cau",
          "/admin/hoan-tien",
          "/admin/tai-khoan",
          "/admin/bao-cao",
          "/admin/cai-dat",
          "/admin/thu-di",
          "/admin/nhat-ky",
          "/admin/toa-nha/moi",
          "/admin/toa-nha/" + unit.num("building_id") + "/sua",
          "/admin/can-ho/moi",
          "/admin/can-ho/" + unit.id() + "/sua",
          "/admin/can-ho/" + unit.id() + "/lich",
          "/admin/thanh-toan/" + waiting,
          "/admin/thanh-toan/" + payment,
          "/admin/hop-dong/" + lease,
          "/admin/hoa-don/moi?lease=" + lease,
          "/admin/hoa-don/" + invoice,
          "/admin/ho-tro/" + ticket,
          "/admin/lich-hen/" + appointment,
          "/admin/yeu-cau/" + request
        }) staff.renders(path);
  }
}
