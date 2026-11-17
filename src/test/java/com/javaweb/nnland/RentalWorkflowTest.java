package com.javaweb.nnland;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.javaweb.SpringBootWebApplication;
import com.javaweb.nnland.model.*;
import com.javaweb.nnland.service.*;
import com.javaweb.nnland.utils.VietQr;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.*;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
    classes = {SpringBootWebApplication.class, RentalWorkflowTest.ClockConfig.class},
    properties = {
      "spring.datasource.url=${NNLAND_WORKFLOW_DATABASE_URL:jdbc:h2:mem:nnland_workflow;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE}",
      "nnland.seed-data=false",
      "nnland.demo-accounts=false",
      "NNLAND_ADMIN_EMAIL=workflow-bootstrap@test.local",
      "NNLAND_ADMIN_PASSWORD=Bootstrap@2026!"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RentalWorkflowTest {
  @TestConfiguration
  static class ClockConfig {
    @Bean
    @Primary
    MutableClock testClock() {
      return new MutableClock();
    }
  }

  static class MutableClock extends Clock {
    final AtomicReference<Instant> value = new AtomicReference<>();
    final ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");

    void set(LocalDateTime t) {
      value.set(t.atZone(zone).toInstant());
    }

    public ZoneId getZone() {
      return zone;
    }

    public Clock withZone(ZoneId z) {
      return this;
    }

    public Instant instant() {
      Instant t = value.get();
      return t == null ? Instant.parse("2026-01-31T03:00:00Z") : t;
    }
  }

  @Autowired Support s;
  @Autowired BookingService bookings;
  @Autowired PaymentService payments;
  @Autowired InvoiceService invoices;
  @Autowired ResidentService residents;
  @Autowired ScheduleService schedule;
  @Autowired CatalogService catalog;
  @Autowired FinanceService finance;
  @Autowired AccountService accounts;
  @Autowired DocumentService documents;
  @Autowired MailDispatcher mail;
  @Autowired AdminService adminService;
  @Autowired MutableClock clock;
  @Autowired MockMvc mvc;
  @Autowired PlatformTransactionManager transactionManager;
  Row admin, manager, alice, bob;
  long b1, b2, u1, u2, u3;
  static final String HASH = new BCryptPasswordEncoder(4).encode("Test@2026!");

  @BeforeEach
  void fixture() {
    clock.set(LocalDateTime.of(2026, 1, 31, 10, 0));
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
          "nn_leases",
          "nn_units",
          "nn_building_staff",
          "nn_buildings",
          "nn_accounts"
        }) s.db.update("delete from " + table);
    admin = account("admin@test.local", "ADMIN");
    manager = account("manager@test.local", "MANAGER");
    alice = account("alice@test.local", "USER");
    bob = account("bob@test.local", "USER");
    b1 = building("B1");
    b2 = building("B2");
    s.db.update(
        "insert into nn_building_staff(building_id,account_id) values(?,?)", b1, manager.id());
    u1 = unit(b1, "A1", 10000000, 2000000);
    u2 = unit(b1, "A2", 9000000, 0);
    u3 = unit(b2, "B1", 8000000, 0);
  }

  Row account(String email, String role) {
    long id =
        s.db.insert(
            "insert into"
                + " nn_accounts(email,username,full_name,birth_year,phone,gender,password_hash,role,created_at)"
                + " values(?,?,?,?,?,?,?,?,?)",
            email,
            email.split("@")[0],
            email,
            1995,
            "0900000000",
            "OTHER",
            HASH,
            role,
            s.now());
    return s.db.one("select * from nn_accounts where id=?", id);
  }

  long building(String code) {
    return s.db.insert(
        "insert into nn_buildings(code,name,city,district,address,image) values(?,?,?,?,?,?)",
        code,
        "NNland " + code,
        "TP. Hồ Chí Minh",
        "Quận 7",
        "Địa chỉ thử nghiệm",
        "/nnland/images/building-riverside.webp");
  }

  long unit(long building, String code, long rent, long deposit) {
    return s.db.insert(
        "insert into"
            + " nn_units(building_id,code,name,type,bedrooms,bathrooms,area,floor_no,monthly_rent,deposit_amount,service_fee,electricity_rate,water_rate,parking_fee,image,image2,image3)"
            + " values(?,?,?,'STANDARD',2,1,60,5,?,?,300000,3500,18000,100000,?,?,?)",
        building,
        code,
        "Căn hộ " + code,
        rent,
        deposit,
        "/nnland/images/apartment-interior.webp",
        "/nnland/images/apartment-living.webp",
        "/nnland/images/apartment-room.webp");
  }

  long book(Row who, long unit, LocalDate start, int months, Long lease) {
    Row q = bookings.quote(who, unit, lease, start.toString(), String.valueOf(months));
    return bookings.create(
        who,
        unit,
        lease,
        start.toString(),
        String.valueOf(months),
        q.num("monthly"),
        q.num("deposit"),
        q.str("terms_version"),
        q.str("fee_signature"),
        true);
  }

  void receive(long payment, long amount, String ref) {
    payments.receive(admin, payment, String.valueOf(amount), ref, "", "Đối chiếu trong kiểm thử");
  }

  long activate(Row who, long unit) {
    long p = book(who, unit, s.today(), 1, null);
    receive(p, s.payment(p).num("due_amount"), "FULL-" + p);
    return s.db
        .one("select * from nn_bookings where id=?", s.payment(p).num("booking_id"))
        .num("lease_id");
  }

  long activeLease() {
    long id = activate(alice, u1);
    bookings.checkIn(admin, id, "0", "0", true);
    return id;
  }

  Map<String, String> bill(String period, String e, String w) {
    Map<String, String> f = new HashMap<>();
    f.put("period", period);
    f.put("reading_date", s.today().toString());
    f.put("due_date", s.today().plusDays(7).toString());
    f.put("electricity_end", e);
    f.put("water_end", w);
    f.put("service_fee", "300000");
    f.put("parking_fee", "100000");
    f.put("adjustment", "0");
    f.put("note", "");
    return f;
  }

  long invoicePayment(long invoice) {
    return s.db.one("select * from nn_payments where invoice_id=?", invoice).id();
  }

  Map<String, String> checkout() {
    Map<String, String> f = new HashMap<>();
    f.put("manager_note", "Hai bên đã kiểm tra và đồng ý biên bản quyết toán.");
    f.put("actual_date", s.today().toString());
    f.put("electricity_final", "10");
    f.put("water_final", "2");
    f.put("deposit_deduction", "500000");
    f.put("rent_refund", "0");
    f.put("readiness", "READY");
    return f;
  }

  @Test
  void calendarMonthsAndUpfrontRentAreExact() {
    Row q = bookings.quote(alice, u1, null, "2026-01-31", "2");
    assertThat(q.date("end")).isEqualTo(LocalDate.of(2026, 3, 31));
    assertThat(q.num("rent")).isEqualTo(20000000);
    assertThat(q.num("total")).isEqualTo(22000000);
    assertThat(bookings.quote(alice, u1, null, "2026-01-31", "1").date("end"))
        .isEqualTo(LocalDate.of(2026, 2, 28));
  }

  @Test
  void acceptanceAndStaleFeeQuoteCannotBeBypassed() {
    Row q = bookings.quote(alice, u1, null, "2026-01-31", "1");
    assertThatThrownBy(
            () ->
                bookings.create(
                    alice,
                    u1,
                    null,
                    "2026-01-31",
                    "1",
                    q.num("monthly"),
                    q.num("deposit"),
                    q.str("terms_version"),
                    q.str("fee_signature"),
                    false))
        .isInstanceOf(BusinessException.class);
    s.db.update("update nn_units set water_rate=20000 where id=?", u1);
    assertThatThrownBy(
            () ->
                bookings.create(
                    alice,
                    u1,
                    null,
                    "2026-01-31",
                    "1",
                    q.num("monthly"),
                    q.num("deposit"),
                    q.str("terms_version"),
                    q.str("fee_signature"),
                    true))
        .isInstanceOf(BusinessException.class);
    assertThat(s.db.count("select count(*) from nn_bookings")).isZero();
  }

  @Test
  void feesAndIdentityAreFrozenWhenPaymentActivates() {
    long p = book(alice, u1, s.today(), 1, null);
    s.db.update(
        "update nn_units set electricity_rate=9999,water_rate=9999,service_fee=9999 where id=?",
        u1);
    receive(p, 12000000, "SNAPSHOT-1");
    Row l =
        s.lease(
            s.db
                .one("select lease_id from nn_bookings where id=?", s.payment(p).num("booking_id"))
                .num("lease_id"));
    assertThat(l.num("electricity_rate")).isEqualTo(3500);
    assertThat(l.num("service_fee")).isEqualTo(300000);
    s.db.update("update nn_accounts set full_name='Tên đã đổi' where id=?", alice.id());
    assertThat(s.lease(l.id()).str("customer_name")).isEqualTo(alice.str("full_name"));
  }

  @Test
  void concurrentHoldsCannotOverlap() throws Exception {
    ExecutorService pool = Executors.newFixedThreadPool(2);
    CountDownLatch start = new CountDownLatch(1);
    try {
      Callable<Boolean> a =
          () -> {
            start.await();
            try {
              book(alice, u1, s.today(), 1, null);
              return true;
            } catch (BusinessException e) {
              return false;
            }
          };
      Future<Boolean> x = pool.submit(a),
          y =
              pool.submit(
                  () -> {
                    start.await();
                    try {
                      book(bob, u1, s.today(), 1, null);
                      return true;
                    } catch (BusinessException e) {
                      return false;
                    }
                  });
      start.countDown();
      assertThat(Arrays.asList(x.get(10, TimeUnit.SECONDS), y.get(10, TimeUnit.SECONDS)))
          .containsExactlyInAnyOrder(true, false);
      assertThat(s.db.count("select count(*) from nn_bookings")).isEqualTo(1);
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void expiredHoldsFreeTheScheduleWithoutOccupyingApartment() {
    long p = book(alice, u1, s.today(), 1, null);
    clock.set(s.now().plusMinutes(31));
    schedule.expireHolds();
    assertThat(s.payment(p).str("booking_status")).isEqualTo("EXPIRED");
    assertThat(schedule.available(u1, s.today(), s.today().plusMonths(1), 0)).isTrue();
    assertThat(s.db.count("select count(*) from nn_leases")).isZero();
  }

  @Test
  void lateMoneyDoesNotStealAnotherCustomersReservation() {
    long old = book(alice, u1, s.today(), 1, null);
    clock.set(s.now().plusMinutes(31));
    schedule.expireHolds();
    long newer = book(bob, u1, s.today(), 1, null);
    receive(old, 12000000, "LATE-1");
    assertThat(s.payment(old).str("status")).isEqualTo("RECONCILE");
    assertThat(s.db.count("select count(*) from nn_leases")).isZero();
    assertThat(s.payment(newer).str("booking_status")).isEqualTo("HOLD");
    assertThat(s.received(old)).isEqualTo(12000000);
  }

  @Test
  void transactionSeesNewHoldCommittedAfterItsInitialRead() throws Exception {
    long old = book(alice, u1, s.today(), 1, null);
    clock.set(s.now().plusMinutes(31));
    schedule.expireHolds();
    CountDownLatch read = new CountDownLatch(1), committed = new CountDownLatch(1);
    ExecutorService pool = Executors.newSingleThreadExecutor();
    try {
      Future<?> receiver =
          pool.submit(
              () ->
                  new TransactionTemplate(transactionManager)
                      .execute(
                          status -> {
                            s.payment(old);
                            read.countDown();
                            try {
                              if (!committed.await(10, TimeUnit.SECONDS))
                                throw new IllegalStateException("Timed out");
                            } catch (InterruptedException e) {
                              throw new RuntimeException(e);
                            }
                            receive(old, 12000000, "SNAPSHOT-RACE");
                            return null;
                          }));
      assertThat(read.await(5, TimeUnit.SECONDS)).isTrue();
      book(bob, u1, s.today(), 1, null);
      committed.countDown();
      receiver.get(10, TimeUnit.SECONDS);
      assertThat(s.payment(old).str("status")).isEqualTo("RECONCILE");
      assertThat(s.db.count("select count(*) from nn_leases")).isZero();
    } finally {
      committed.countDown();
      pool.shutdownNow();
    }
  }

  @Test
  void oldPastPeriodMoneyIsRecordedButNotActivated() {
    long p = book(alice, u1, s.today(), 1, null);
    clock.set(s.now().plusDays(2));
    schedule.expireHolds();
    receive(p, 12000000, "PAST-PERIOD");
    assertThat(s.payment(p).str("status")).isEqualTo("RECONCILE");
    assertThat(s.db.count("select count(*) from nn_leases")).isZero();
  }

  @Test
  void partialFullOverpaidAndRefundAmountsReconcile() {
    long p = book(alice, u1, s.today(), 1, null);
    receive(p, 5000000, "PART-1");
    assertThat(s.payment(p).str("status")).isEqualTo("PARTIAL");
    assertThat(s.db.count("select count(*) from nn_leases")).isZero();
    receive(p, 7000000, "PART-2");
    assertThat(s.payment(p).str("status")).isEqualTo("PAID");
    receive(p, 1000000, "PART-3");
    receive(p, 1000000, "PART-3");
    assertThat(s.db.count("select count(*) from nn_receipts")).isEqualTo(3);
    Row refund = s.db.one("select * from nn_refunds where payment_id=?", p);
    assertThat(refund.num("amount")).isEqualTo(1000000);
    payments.completeRefund(admin, refund.id(), "REF-PART-3");
    Row report = finance.report(admin, s.today(), s.today(), null);
    assertThat(report.num("gross")).isEqualTo(13000000);
    assertThat(report.num("net")).isEqualTo(12000000);
    assertThat(report.num("deposit_cash")).isEqualTo(2000000);
    assertThat(report.num("overpay_cash")).isZero();
    assertThat(report.num("earned_rent")).isEqualTo(357143);
  }

  @Test
  void concurrentRepeatedBankConfirmationProducesOneReceiptAndLease() throws Exception {
    long p = book(alice, u1, s.today(), 1, null);
    ExecutorService pool = Executors.newFixedThreadPool(2);
    try {
      List<Future<?>> jobs = new ArrayList<>();
      for (int k = 0; k < 2; k++)
        jobs.add(pool.submit(() -> receive(p, 12000000, "DUPLICATE-BANK")));
      for (Future<?> job : jobs) job.get(10, TimeUnit.SECONDS);
      assertThat(s.db.count("select count(*) from nn_receipts")).isEqualTo(1);
      assertThat(s.db.count("select count(*) from nn_leases")).isEqualTo(1);
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void cannotReuseBankReferenceForDifferentPayment() {
    long a = book(alice, u1, s.today(), 1, null), b = book(bob, u2, s.today(), 1, null);
    receive(a, 12000000, "GLOBAL-REF");
    assertThatThrownBy(() -> receive(b, 9000000, "GLOBAL-REF"))
        .isInstanceOf(BusinessException.class);
    assertThat(s.received(b)).isZero();
  }

  @Test
  void extensionStartsAtCurrentEndAndCanRenewOnExpiryDay() {
    long lease = activeLease();
    clock.set(LocalDateTime.of(2026, 2, 28, 10, 0));
    assertThat(schedule.available(u1, s.today(), s.today().plusMonths(1), 0)).isFalse();
    long p = book(alice, u1, s.today(), 1, lease);
    receive(p, 10000000, "EXTENSION-1");
    assertThat(s.lease(lease).date("end_date")).isEqualTo(LocalDate.of(2026, 3, 28));
    receive(p, 10000000, "EXTENSION-1");
    assertThat(s.lease(lease).date("end_date")).isEqualTo(LocalDate.of(2026, 3, 28));
  }

  @Test
  void futureReservationBlocksExtension() {
    long lease = activeLease();
    long next = book(bob, u1, LocalDate.of(2026, 2, 28), 1, null);
    receive(next, 12000000, "NEXT-TENANT");
    assertThat(bookings.quote(alice, u1, lease, "2026-02-28", "1").bool("available")).isFalse();
    assertThatThrownBy(() -> book(alice, u1, LocalDate.of(2026, 2, 28), 1, lease))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  void openCancellationPreventsPreviouslyHeldExtensionActivating() {
    long lease = activate(alice, u1);
    long ext = book(alice, u1, s.lease(lease).date("end_date"), 1, lease);
    residents.request(alice, lease, null, "CANCEL", "", "Thay đổi kế hoạch");
    receive(ext, 10000000, "EXT-CANCELLED-LEASE");
    assertThat(s.payment(ext).str("status")).isEqualTo("RECONCILE");
    assertThat(s.lease(lease).date("end_date")).isEqualTo(LocalDate.of(2026, 2, 28));
  }

  @Test
  void checkInRequiresDateSignatureAndFreePhysicalApartment() {
    long p = book(alice, u1, s.today().plusDays(1), 1, null);
    receive(p, 12000000, "FUTURE-IN");
    long lease =
        s.db
            .one("select lease_id from nn_bookings where id=?", s.payment(p).num("booking_id"))
            .num("lease_id");
    assertThatThrownBy(() -> bookings.checkIn(admin, lease, "0", "0", true))
        .isInstanceOf(BusinessException.class);
    clock.set(s.now().plusDays(1));
    assertThatThrownBy(() -> bookings.checkIn(admin, lease, "0", "0", false))
        .isInstanceOf(BusinessException.class);
    bookings.checkIn(admin, lease, "0", "0", true);
    assertThat(s.lease(lease).str("status")).isEqualTo("ACTIVE");
  }

  @Test
  void pendingCancellationMustBeResolvedBeforeCheckIn() {
    long lease = activate(alice, u1);
    residents.request(alice, lease, null, "CANCEL", "", "Cần xem lại kế hoạch nhận phòng");
    long request = s.db.one("select id from nn_lease_requests where lease_id=?", lease).id();
    assertThatThrownBy(() -> bookings.checkIn(admin, lease, "0", "0", true))
        .isInstanceOf(BusinessException.class);
    assertThat(s.lease(lease).str("status")).isEqualTo("RESERVED");
    residents.handleRequest(admin, request, false, checkout());
    bookings.checkIn(admin, lease, "0", "0", true);
    assertThat(s.lease(lease).str("status")).isEqualTo("ACTIVE");
  }

  @Test
  void policyTextChangesInvalidateQuoteEvenWhenVersionIsUnchanged() {
    Row policy = s.termsSnapshot();
    Row q = bookings.quote(alice, u1, null, s.today().toString(), "1");
    try {
      s.set("terms_text", policy.str("terms") + " Bổ sung chính sách mới.");
      assertThatThrownBy(
              () ->
                  bookings.create(
                      alice,
                      u1,
                      null,
                      s.today().toString(),
                      "1",
                      q.num("monthly"),
                      q.num("deposit"),
                      q.str("terms_version"),
                      q.str("fee_signature"),
                      true))
          .isInstanceOf(BusinessException.class);
      long payment = book(alice, u1, s.today(), 1, null);
      String accepted = s.terms();
      s.set("terms_text", policy.str("terms"));
      assertThat(
              s.db
                  .one(
                      "select terms_text from nn_bookings where id=?",
                      s.payment(payment).num("booking_id"))
                  .str("terms_text"))
          .isEqualTo(accepted);
    } finally {
      s.set("terms_text", policy.str("terms"));
    }
  }

  @Test
  void paidFinalInvoiceCanBeSupplementedWhenCheckoutIsPostponedWithinMonth() {
    clock.set(LocalDateTime.of(2026, 1, 10, 10, 0));
    long lease = activeLease();
    long first = invoices.create(admin, lease, bill(invoices.nextFinalPeriod(lease), "10", "2"));
    receive(invoicePayment(first), 471000, "FINAL-FIRST");
    residents.request(
        alice, lease, null, "CHECKOUT", s.today().plusDays(1).toString(), "Lùi bàn giao một ngày");
    long request = s.db.one("select id from nn_lease_requests where lease_id=?", lease).id();
    clock.set(s.now().plusDays(1));
    assertThatThrownBy(() -> residents.handleRequest(admin, request, true, checkout()))
        .isInstanceOf(BusinessException.class);
    assertThat(invoices.nextFinalPeriod(lease)).isEqualTo("2026-01-FINAL-001");
    assertThat(invoices.monthFeesCharged(lease)).isTrue();
    Map<String, String> supplement = bill(invoices.nextFinalPeriod(lease), "12", "3");
    supplement.put("service_fee", "0");
    supplement.put("parking_fee", "0");
    long second = invoices.create(admin, lease, supplement);
    assertThat(invoices.invoice(second).num("total")).isEqualTo(25000);
    receive(invoicePayment(second), 25000, "FINAL-SUPPLEMENT");
    Map<String, String> handover = checkout();
    handover.put("electricity_final", "12");
    handover.put("water_final", "3");
    residents.handleRequest(admin, request, true, handover);
    assertThat(s.lease(lease).str("status")).isEqualTo("CLOSED");
    assertThat(invoices.invoice(first).num("total")).isEqualTo(471000);
  }

  @Test
  void moneyArrivingAfterFullRefundNeedsReconciliationAndAnotherActualRefund() {
    long lease = activeLease();
    long payment =
        s.db
            .one(
                "select id from nn_payments where booking_id=?",
                s.lease(lease).num("initial_booking_id"))
            .id();
    long invoice = invoices.create(admin, lease, bill("2026-01-FINAL", "10", "2"));
    receive(invoicePayment(invoice), 471000, "CLOSED-FINAL");
    residents.request(
        alice, lease, null, "CHECKOUT", s.today().toString(), "Quyết toán theo thỏa thuận");
    long request = s.db.one("select id from nn_lease_requests where lease_id=?", lease).id();
    Map<String, String> form = checkout();
    form.put("deposit_deduction", "0");
    form.put("rent_refund", "10000000");
    residents.handleRequest(admin, request, true, form);
    for (Row refund : s.db.list("select id from nn_refunds where payment_id=?", payment))
      payments.completeRefund(admin, refund.id(), "CLOSED-REFUND-" + refund.id());
    assertThat(s.payment(payment).str("status")).isEqualTo("REFUNDED");
    receive(payment, 1, "EXTRA-AFTER-CLOSED");
    assertThat(s.payment(payment).str("status")).isEqualTo("RECONCILE");
    Row extra =
        s.db.one("select * from nn_refunds where payment_id=? and status='PENDING'", payment);
    assertThat(extra.num("amount")).isEqualTo(1);
    payments.completeRefund(admin, extra.id(), "REFUND-EXTRA-CLOSED");
    assertThat(s.payment(payment).str("status")).isEqualTo("REFUNDED");
    assertThat(s.lease(lease).str("status")).isEqualTo("CLOSED");
  }

  @Test
  void invoiceUsesMeterDifferencesAndCannotDuplicatePeriod() {
    long lease = activeLease();
    long id = invoices.create(manager, lease, bill("2026-01", "10", "2"));
    Row i = invoices.invoice(id);
    assertThat(i.num("electricity_amount")).isEqualTo(35000);
    assertThat(i.num("water_amount")).isEqualTo(36000);
    assertThat(i.num("total")).isEqualTo(471000);
    assertThatThrownBy(() -> invoices.create(manager, lease, bill("2026-01", "10", "2")))
        .isInstanceOf(BusinessException.class);
    Map<String, String> wrong = bill("2026-01-FINAL", "9", "2");
    wrong.put("service_fee", "0");
    wrong.put("parking_fee", "0");
    assertThatThrownBy(() -> invoices.create(manager, lease, wrong))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  void checkoutRequiresPaidFinalBillAndProtectsDeductedDeposit() {
    long lease = activeLease();
    long invoice = invoices.create(manager, lease, bill("2026-01-FINAL", "10", "2"));
    residents.request(
        alice, lease, null, "CHECKOUT", s.today().toString(), "Trả phòng theo thỏa thuận");
    long request = s.db.one("select * from nn_lease_requests where lease_id=?", lease).id();
    assertThatThrownBy(() -> residents.handleRequest(admin, request, true, checkout()))
        .isInstanceOf(BusinessException.class);
    receive(invoicePayment(invoice), 471000, "FINAL-UTILITY");
    Map<String, String> wrong = checkout();
    wrong.put("electricity_final", "11");
    assertThatThrownBy(() -> residents.handleRequest(admin, request, true, wrong))
        .isInstanceOf(BusinessException.class);
    residents.handleRequest(admin, request, true, checkout());
    assertThat(s.lease(lease).str("status")).isEqualTo("CLOSED");
    assertThat(schedule.available(u1, s.today(), s.today().plusMonths(1), 0)).isTrue();
    Row dep = s.db.one("select * from nn_refunds where category='DEPOSIT'");
    assertThat(dep.num("amount")).isEqualTo(1500000);
    assertThatThrownBy(
            () ->
                payments.requestRefund(
                    admin,
                    dep.num("payment_id"),
                    "1",
                    "DEPOSIT",
                    "Không được vượt phần cọc còn lại"))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  void customerCannotSeeOthersAndManagerCannotSeeOtherBuilding() throws Exception {
    long lease = activeLease();
    long invoice = invoices.create(admin, lease, bill("2026-01", "10", "2"));
    long ticket = residents.ticket(alice, lease, "WATER", "Vòi nước rò", "Cần kiểm tra", "NORMAL");
    long p =
        s.db
            .one(
                "select p.id from nn_payments p join nn_bookings b on b.id=p.booking_id where"
                    + " b.lease_id=?",
                lease)
            .id();
    for (String path :
        new String[] {
          "/thanh-toan/" + p,
          "/thanh-toan/" + p + "/phieu-thu",
          "/hop-dong/" + lease + "/pdf",
          "/hoa-don/" + invoice,
          "/hoa-don/" + invoice + "/pdf",
          "/tai-khoan/ho-tro/" + ticket
        })
      mvc.perform(get(path).with(user(bob.str("email")).roles("USER")))
          .andExpect(status().isForbidden());
    long other = book(bob, u3, s.today(), 1, null);
    mvc.perform(get("/admin/thanh-toan/" + other).with(user(manager.str("email")).roles("MANAGER")))
        .andExpect(status().isForbidden());
    mvc.perform(
            get("/admin/bao-cao")
                .param("building", String.valueOf(b2))
                .with(user(manager.str("email")).roles("MANAGER")))
        .andExpect(status().isForbidden());
  }

  @Test
  void csrfAndRoleGuardsRejectUnauthorizedMutations() throws Exception {
    mvc.perform(post("/tai-khoan/thong-tin").with(user(alice.str("email")).roles("USER")))
        .andExpect(status().isForbidden());
    mvc.perform(post("/admin/cai-dat").with(csrf()).with(user(alice.str("email")).roles("USER")))
        .andExpect(status().isForbidden());
  }

  @Test
  void resetLinksAreHashedSingleUseAndMailNotFalselySent() {
    accounts.requestReset(alice.str("email"));
    Row queued =
        s.db.one(
            "select * from nn_mail_outbox where recipient=? order by id desc limit 1",
            alice.str("email"));
    String token = queued.str("body").split("token=")[1].split("\\s")[0];
    assertThat(
            s.db
                .one("select * from nn_reset_tokens where account_id=?", alice.id())
                .str("token_hash"))
        .doesNotContain(token);
    accounts.reset(token, "Changed@2026!");
    assertThatThrownBy(() -> accounts.reset(token, "ChangedAgain@2026!"))
        .isInstanceOf(BusinessException.class);
    mail.deliver();
    assertThat(s.db.count("select count(*) from nn_mail_outbox where status='SENT'")).isZero();
  }

  @Test
  void contractInvoiceAndExcelProduceRealDocuments() {
    long lease = activeLease();
    long invoice = invoices.create(admin, lease, bill("2026-01", "10", "2"));
    assertThat(
            new String(documents.contract(lease), 0, 4, java.nio.charset.StandardCharsets.US_ASCII))
        .isEqualTo("%PDF");
    assertThat(
            new String(
                documents.invoice(invoice), 0, 4, java.nio.charset.StandardCharsets.US_ASCII))
        .isEqualTo("%PDF");
    Row r = finance.report(admin, s.today(), s.today(), null);
    assertThat(documents.reportExcel(r)).startsWith((byte) 'P', (byte) 'K');
  }

  @Test
  void emailOutboxIncludesContractAndRetriesFailedDeliveryWithoutFalseSuccess() throws Exception {
    activeLease();
    class CapturingSender extends org.springframework.mail.javamail.JavaMailSenderImpl {
      boolean fail;
      final java.util.List<javax.mail.internet.MimeMessage> delivered = new ArrayList<>();

      @Override
      public void send(javax.mail.internet.MimeMessage message) {
        if (fail) throw new org.springframework.mail.MailSendException("Simulated SMTP failure");
        delivered.add(message);
      }
    }
    CapturingSender sender = new CapturingSender();
    MailDispatcher dispatcher = new MailDispatcher(s, sender, documents);
    org.springframework.test.util.ReflectionTestUtils.setField(dispatcher, "enabled", true);
    org.springframework.test.util.ReflectionTestUtils.setField(
        dispatcher, "from", "no-reply@test.local");
    dispatcher.deliver();
    assertThat(sender.delivered).hasSize(1);
    javax.mail.internet.MimeMessage message = sender.delivered.get(0);
    assertThat(message.getAllRecipients()[0].toString()).isEqualTo(alice.str("email"));
    assertThat(message.getSubject()).contains("NNland", "Xác nhận thanh toán");
    javax.mail.Multipart parts = (javax.mail.Multipart) message.getContent();
    javax.mail.BodyPart attachment = parts.getBodyPart(1);
    assertThat(attachment.getFileName()).endsWith(".pdf");
    byte[] pdf = org.springframework.util.StreamUtils.copyToByteArray(attachment.getInputStream());
    assertThat(new String(pdf, 0, 4, java.nio.charset.StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    assertThat(s.db.count("select count(*) from nn_mail_outbox where status='SENT'")).isEqualTo(1);
    s.mail(alice.str("email"), "Thử gửi lại", "Nội dung kiểm thử", null);
    sender.fail = true;
    dispatcher.deliver();
    Row failed = s.db.one("select * from nn_mail_outbox where status='FAILED'");
    assertThat(failed.num("attempts")).isEqualTo(1);
    assertThat(failed.get("sent_at")).isNull();
    sender.fail = false;
    dispatcher.deliver();
    assertThat(sender.delivered).hasSize(1);
    clock.set(s.now().plusMinutes(6));
    dispatcher.deliver();
    assertThat(sender.delivered).hasSize(2);
    assertThat(s.db.one("select * from nn_mail_outbox where id=?", failed.id()).str("status"))
        .isEqualTo("SENT");
  }

  @Test
  void qrEncodesPaymentAmountAndBankWithoutExternalService() {
    String data = VietQr.payload("970436", "0000000000", 12000000, "TT-TEST001");
    assertThat(data)
        .startsWith("000201010212")
        .contains("970436", "0000000000", "540812000000", "TT-TEST001");
    assertThat(VietQr.png("970436", "0000000000", 12000000, "TT-TEST001"))
        .startsWith((byte) 137, (byte) 80, (byte) 78, (byte) 71);
  }

  @Test
  void receiptAndRefundCannotUseSameBankReferenceConcurrently() throws Exception {
    long lease = activate(alice, u1);
    residents.request(alice, lease, null, "CANCEL", "", "Hủy trước ngày bàn giao");
    Row req = s.db.one("select * from nn_lease_requests where lease_id=?", lease);
    Map<String, String> decision = new HashMap<>();
    decision.put("manager_note", "Đồng ý hoàn tiền trước nhận phòng.");
    residents.handleRequest(admin, req.id(), true, decision);
    Row refund = s.db.one("select * from nn_refunds where category='RENT' order by id limit 1");
    long other = book(bob, u2, s.today(), 1, null);
    ExecutorService pool = Executors.newFixedThreadPool(2);
    CountDownLatch start = new CountDownLatch(1);
    try {
      Callable<Boolean> incoming =
          () -> {
            start.await();
            try {
              receive(other, 9000000, "TWO-DIRECTIONS");
              return true;
            } catch (RuntimeException e) {
              return false;
            }
          };
      Callable<Boolean> outgoing =
          () -> {
            start.await();
            try {
              payments.completeRefund(admin, refund.id(), "TWO-DIRECTIONS");
              return true;
            } catch (RuntimeException e) {
              return false;
            }
          };
      Future<Boolean> x = pool.submit(incoming), y = pool.submit(outgoing);
      start.countDown();
      assertThat(Arrays.asList(x.get(10, TimeUnit.SECONDS), y.get(10, TimeUnit.SECONDS)))
          .containsExactlyInAnyOrder(true, false);
      assertThat(
              s.db.count(
                  "select count(*) from nn_bank_references where bank_reference='TWO-DIRECTIONS'"))
          .isEqualTo(1);
      assertThat(
              s.db.count("select count(*) from nn_receipts where bank_reference='TWO-DIRECTIONS'")
                  + s.db.count(
                      "select count(*) from nn_refunds where bank_reference='TWO-DIRECTIONS'"))
          .isEqualTo(1);
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void unpaidInvoiceCanBeCorrectedButPaidOrEarlierReadingsCannot() {
    long lease = activeLease();
    long id = invoices.create(admin, lease, bill("2026-01", "10", "2"));
    Map<String, String> corrected = bill("2026-01", "12", "3");
    corrected.put("correction_reason", "Nhập lại theo ảnh công tơ đã kiểm tra");
    invoices.correct(manager, id, corrected);
    assertThat(invoices.invoice(id).num("total")).isEqualTo(496000);
    assertThat(s.unit(u1, false).decimal("electricity_meter")).isEqualByComparingTo("12");
    receive(invoicePayment(id), 496000, "CORRECTED-INVOICE");
    assertThatThrownBy(() -> invoices.correct(admin, id, corrected))
        .isInstanceOf(BusinessException.class);
    Map<String, String> finalBill = bill("2026-01-FINAL", "12", "3");
    finalBill.put("service_fee", "0");
    finalBill.put("parking_fee", "0");
    invoices.create(admin, lease, finalBill);
    assertThatThrownBy(() -> invoices.correct(admin, id, corrected))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  void defaultSearchExcludesOccupiedApartmentsAndRespectsFutureDates() {
    activeLease();
    Row now = catalog.search(Collections.emptyMap());
    assertThat(((java.util.List<Row>) now.get("items"))).extracting(Row::id).doesNotContain(u1);
    Map<String, String> future = new HashMap<>();
    future.put("start", "2026-03-01");
    future.put("months", "1");
    assertThat((java.util.List<Row>) catalog.search(future).get("items"))
        .extracting(Row::id)
        .contains(u1);
  }

  @Test
  void financialFiltersKeepCashAndEarnedComponentsInTheSelectedScope() {
    long lease = activeLease();
    activate(bob, u3);
    long bill = invoices.create(admin, lease, bill("2026-01", "10", "2"));
    receive(invoicePayment(bill), 471000, "FILTER-UTILITY");
    Row utility = finance.report(manager, s.today(), s.today(), b1, u1, "UTILITY");
    assertThat(utility.num("gross")).isEqualTo(471000);
    assertThat(utility.num("earned_rent")).isZero();
    assertThat(utility.num("earned_utility")).isEqualTo(471000);
    assertThat(utility.num("income_base")).isEqualTo(471000);
    Row rent =
        finance.report(
            manager, s.today().withDayOfMonth(1), s.today().plusDays(20), b1, u1, "RENT");
    assertThat(rent.num("gross")).isEqualTo(12000000);
    assertThat(rent.num("earned_rent")).isEqualTo(357143);
    assertThat(rent.num("earned_utility")).isZero();
    assertThatThrownBy(() -> finance.report(manager, s.today(), s.today(), null, u3, ""))
        .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
  }

  @Test
  void customerCannotPromoteSelfAndLastAdministratorStaysActive() throws Exception {
    mvc.perform(
            post("/dang-ky")
                .with(csrf())
                .param("email", "new@test.local")
                .param("username", "newcustomer")
                .param("full_name", "Khách đăng ký")
                .param("birth_year", "1995")
                .param("phone", "0900000000")
                .param("gender", "OTHER")
                .param("password", "Customer@2026!")
                .param("password_confirm", "Customer@2026!")
                .param("role", "ADMIN")
                .param("agree", "on"))
        .andExpect(status().is3xxRedirection());
    assertThat(s.db.one("select role from nn_accounts where email='new@test.local'").str("role"))
        .isEqualTo("USER");
    assertThatThrownBy(() -> adminService.account(admin, admin.id(), "USER", false))
        .isInstanceOf(BusinessException.class);
    assertThat(s.db.count("select count(*) from nn_accounts where role='ADMIN' and active=true"))
        .isEqualTo(1);
  }

  @Test
  void qrPngCanBeDecodedByAnIndependentQrReader() throws Exception {
    byte[] bytes = VietQr.png("970436", "0000000000", 12000000, "TT-TEST001");
    java.awt.image.BufferedImage image =
        javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(bytes));
    com.google.zxing.BinaryBitmap bitmap =
        new com.google.zxing.BinaryBitmap(
            new com.google.zxing.common.HybridBinarizer(
                new com.google.zxing.client.j2se.BufferedImageLuminanceSource(image)));
    String decoded = new com.google.zxing.MultiFormatReader().decode(bitmap).getText();
    assertThat(decoded).isEqualTo(VietQr.payload("970436", "0000000000", 12000000, "TT-TEST001"));
  }

  @Test
  void uploadedImagesAreNormalizedAndNonImagesRejected() {
    try {
      java.awt.image.BufferedImage image =
          new java.awt.image.BufferedImage(200, 200, java.awt.image.BufferedImage.TYPE_INT_RGB);
      java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
      javax.imageio.ImageIO.write(image, "png", out);
      UploadService upload =
          new UploadService(System.getProperty("java.io.tmpdir") + "/nnland-unit-upload-test");
      String path =
          upload.image(
              new org.springframework.mock.web.MockMultipartFile(
                  "photo", "room.png", "image/png", out.toByteArray()));
      assertThat(path).matches("/uploads/[a-f0-9-]{36}\\.jpg");
      assertThat(com.javaweb.nnland.utils.Rules.image(path)).isEqualTo(path);
      assertThatThrownBy(
              () ->
                  upload.image(
                      new org.springframework.mock.web.MockMultipartFile(
                          "photo",
                          "bad.png",
                          "image/png",
                          "<script>bad</script>"
                              .getBytes(java.nio.charset.StandardCharsets.UTF_8))))
          .isInstanceOf(BusinessException.class);
    } catch (java.io.IOException e) {
      throw new RuntimeException(e);
    }
  }
}
