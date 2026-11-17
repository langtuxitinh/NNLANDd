package com.javaweb.nnland.service;

import com.javaweb.nnland.model.Row;
import com.javaweb.nnland.utils.ViewFormat;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class DocumentService {
  private final Support s;
  private final InvoiceService invoices;
  private final BaseFont font;

  public DocumentService(Support s, InvoiceService invoices) throws IOException, DocumentException {
    this.s = s;
    this.invoices = invoices;
    byte[] bytes =
        StreamUtils.copyToByteArray(
            new ClassPathResource("static/nnland/fonts/DejaVuSans.ttf").getInputStream());
    font =
        BaseFont.createFont(
            "DejaVuSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, bytes, null);
  }

  private com.lowagie.text.Font text(float size, boolean bold) {
    return new com.lowagie.text.Font(
        font,
        size,
        bold ? com.lowagie.text.Font.BOLD : com.lowagie.text.Font.NORMAL,
        new Color(33, 54, 47));
  }

  private Document begin(ByteArrayOutputStream out, String title) throws DocumentException {
    Document d = new Document(PageSize.A4, 44, 44, 40, 45);
    PdfWriter.getInstance(d, out);
    d.open();
    Paragraph brand = new Paragraph("NNland", text(26, true));
    d.add(brand);
    Paragraph h = new Paragraph(title, text(17, true));
    h.setSpacingBefore(14);
    h.setSpacingAfter(16);
    d.add(h);
    return d;
  }

  private void line(Document d, String label, Object value) throws DocumentException {
    Paragraph p =
        new Paragraph(label + ": " + String.valueOf(value == null ? "—" : value), text(10, false));
    p.setLeading(16);
    p.setSpacingAfter(4);
    d.add(p);
  }

  private void section(Document d, String title) throws DocumentException {
    Paragraph p = new Paragraph(title, text(12, true));
    p.setSpacingBefore(15);
    p.setSpacingAfter(8);
    d.add(p);
  }

  private void note(Document d, String value) throws DocumentException {
    Paragraph p = new Paragraph(value, text(9, false));
    p.setLeading(15);
    p.setSpacingBefore(10);
    d.add(p);
  }

  private void table(Document d, String[] headings, java.util.List<String[]> rows)
      throws DocumentException {
    PdfPTable t = new PdfPTable(headings.length);
    t.setWidthPercentage(100);
    t.setSpacingBefore(10);
    t.setHeaderRows(1);
    for (String heading : headings) {
      PdfPCell c = new PdfPCell(new Phrase(heading, text(9, true)));
      c.setBackgroundColor(new Color(235, 242, 234));
      c.setPadding(7);
      c.setBorderColor(new Color(220, 230, 220));
      t.addCell(c);
    }
    for (String[] row : rows)
      for (String value : row) {
        PdfPCell c = new PdfPCell(new Phrase(value == null ? "" : value, text(9, false)));
        c.setPadding(7);
        c.setBorderColor(new Color(230, 235, 230));
        t.addCell(c);
      }
    d.add(t);
  }

  public byte[] contract(long id) {
    try {
      Row l = s.lease(id);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Document d = begin(out, "HỢP ĐỒNG THUÊ CĂN HỘ · " + l.str("code"));
      line(d, "Tình trạng", ViewFormat.label(l.str("status")));
      line(
          d,
          "Ký trực tiếp",
          l.get("signed_at") == null
              ? "Chờ ký khi bàn giao"
              : ViewFormat.datetime(l.get("signed_at")));
      section(d, "Các bên và căn hộ");
      line(d, "Bên cho thuê", "Hệ thống căn hộ NNland");
      line(d, "Liên hệ", s.setting("hotline", ""));
      line(d, "Khách thuê", l.getOrDefault("customer_name", l.get("full_name")));
      line(
          d,
          "Email / Điện thoại",
          l.getOrDefault("customer_email", l.get("email"))
              + " / "
              + l.getOrDefault("customer_phone", l.get("phone")));
      line(d, "Căn hộ", l.getOrDefault("unit_label", l.get("unit_name")));
      line(d, "Tòa nhà", l.getOrDefault("building_label", l.get("building_name")));
      line(d, "Địa chỉ", l.getOrDefault("property_address", l.get("address")));
      section(d, "Thời hạn và các khoản phí");
      line(d, "Nhận căn hộ", ViewFormat.date(l.get("start_date")));
      line(d, "Ngày trả phòng (không tính tiền)", ViewFormat.date(l.get("end_date")));
      line(d, "Giá thuê mỗi tháng", ViewFormat.money(l.get("monthly_rent")));
      line(d, "Tiền cọc", ViewFormat.money(l.get("deposit_held")));
      line(
          d,
          "Điện / Nước",
          ViewFormat.money(l.get("electricity_rate"))
              + "/kWh · "
              + ViewFormat.money(l.get("water_rate"))
              + "/m³");
      line(
          d,
          "Dịch vụ / Gửi xe mỗi tháng",
          ViewFormat.money(l.get("service_fee")) + " / " + ViewFormat.money(l.get("parking_fee")));
      java.util.List<String[]> rows = new ArrayList<>();
      for (Row b :
          s.db.list(
              "select * from nn_bookings where lease_id=? and status='CONFIRMED' order by id", id))
        rows.add(
            new String[] {
              b.str("code"),
              ViewFormat.date(b.get("start_date")) + " → " + ViewFormat.date(b.get("end_date")),
              ViewFormat.money(b.get("rent_total"))
            });
      table(d, new String[] {"Đơn thuê / Gia hạn", "Kỳ thuê", "Tiền thuê trả trước"}, rows);
      section(d, "Điều khoản đã chấp nhận");
      note(d, l.str("terms_text"));
      section(d, "Bàn giao và xác nhận");
      line(d, "Điện đầu kỳ", l.get("electricity_start"));
      line(d, "Nước đầu kỳ", l.get("water_start"));
      line(d, "Ngày nhận thực tế", ViewFormat.datetime(l.get("checked_in_at")));
      line(d, "Ngày trả thực tế", ViewFormat.date(l.get("checkout_date")));
      note(
          d,
          "Bản hợp đồng được tạo từ dữ liệu đơn thuê đã xác nhận. Hai bên kiểm tra thông tin, danh"
              + " mục bàn giao và ký trực tiếp; quản lý cập nhật việc ký và bàn giao trên hệ"
              + " thống.");
      section(d, "ĐẠI DIỆN NNLAND                                      KHÁCH THUÊ");
      note(
          d, "(Ký, ghi rõ họ tên)                                             (Ký, ghi rõ họ tên)");
      d.close();
      return out.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("Không thể tạo hợp đồng PDF", e);
    }
  }

  public byte[] invoice(long id) {
    try {
      Row i = invoices.invoice(id);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Document d = begin(out, "HÓA ĐƠN ĐIỆN NƯỚC & DỊCH VỤ");
      line(d, "Mã hóa đơn", i.str("code"));
      line(d, "Kỳ", i.str("period"));
      line(d, "Khách thuê", i.str("full_name"));
      line(d, "Căn hộ", i.str("unit_code") + " · " + i.str("building_name"));
      line(
          d,
          "Ngày chốt / Hạn thanh toán",
          ViewFormat.date(i.get("reading_date")) + " / " + ViewFormat.date(i.get("due_date")));
      line(d, "Tình trạng", ViewFormat.label(i.str("status")));
      java.util.List<String[]> rows = new ArrayList<>();
      rows.add(
          new String[] {
            "Điện",
            i.str("electricity_start") + " → " + i.str("electricity_end") + " kWh",
            ViewFormat.money(i.get("electricity_amount"))
          });
      rows.add(
          new String[] {
            "Nước",
            i.str("water_start") + " → " + i.str("water_end") + " m³",
            ViewFormat.money(i.get("water_amount"))
          });
      rows.add(new String[] {"Phí dịch vụ", "Theo kỳ", ViewFormat.money(i.get("service_fee"))});
      rows.add(new String[] {"Phí gửi xe", "Theo kỳ", ViewFormat.money(i.get("parking_fee"))});
      rows.add(new String[] {"Điều chỉnh", i.str("note"), ViewFormat.money(i.get("adjustment"))});
      table(d, new String[] {"Khoản mục", "Diễn giải", "Thành tiền"}, rows);
      section(d, "TỔNG CỘNG: " + ViewFormat.money(i.get("total")));
      line(d, "Đã nhận", ViewFormat.money(i.get("received")));
      line(d, "Còn thiếu", ViewFormat.money(Math.max(0, i.num("total") - i.num("received"))));
      note(
          d,
          "Tiền thuê căn hộ đã được thu trước theo đơn thuê; hóa đơn này chỉ bao gồm điện, nước và"
              + " các phí phát sinh được liệt kê.");
      d.close();
      return out.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("Không thể tạo hóa đơn PDF", e);
    }
  }

  public byte[] receipt(long id) {
    try {
      Row p = s.payment(id);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Document d = begin(out, "BẢNG ĐỐI CHIẾU THANH TOÁN · " + p.str("code"));
      line(d, "Khách hàng", p.str("full_name"));
      line(d, "Căn hộ", p.str("unit_name"));
      line(d, "Khoản thu", ViewFormat.label(p.str("type")));
      line(d, "Cần thanh toán", ViewFormat.money(p.get("due_amount")));
      java.util.List<String[]> rows = new ArrayList<>();
      for (Row r : s.db.list("select * from nn_receipts where payment_id=? order by id", id))
        rows.add(
            new String[] {
              r.str("code"),
              r.str("bank_reference"),
              ViewFormat.datetime(r.get("received_at")),
              ViewFormat.money(r.get("amount"))
            });
      table(d, new String[] {"Phiếu thu", "Mã ngân hàng", "Nhận lúc", "Số tiền"}, rows);
      line(d, "Tổng thực nhận", ViewFormat.money(p.get("received")));
      line(d, "Đã chuyển hoàn", ViewFormat.money(p.get("refunded")));
      line(d, "Tình trạng", ViewFormat.label(p.str("status")));
      d.close();
      return out.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("Không thể tạo phiếu thu PDF", e);
    }
  }

  public byte[] reportPdf(Row report) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Document d = begin(out, "BÁO CÁO THU CHI NNLAND");
      line(
          d,
          "Khoảng ngày",
          ViewFormat.date(report.get("from")) + " → " + ViewFormat.date(report.get("through")));
      for (String[] p :
          new String[][] {
            {"Tiền thực nhận", "gross"},
            {"Đã chuyển hoàn", "refunded"},
            {"Dòng tiền ròng", "net"},
            {"Tiền cọc ròng", "deposit_cash"},
            {"Doanh thu phân bổ", "earned_total"}
          }) line(d, p[0], ViewFormat.money(report.get(p[1])));
      java.util.List<String[]> rows = new ArrayList<>();
      for (Row r : (java.util.List<Row>) report.get("receipts"))
        rows.add(
            new String[] {
              ViewFormat.datetime(r.get("received_at")),
              r.str("payment_code"),
              r.str("building_name"),
              ViewFormat.money(r.get("amount"))
            });
      table(d, new String[] {"Thời điểm", "Thanh toán", "Tòa nhà", "Thực nhận"}, rows);
      note(
          d,
          "Dòng tiền ròng = thực nhận − khoản hoàn đã chuyển. Tiền cọc và tiền thừa tách khỏi doanh"
              + " thu. Tiền thuê phân bổ theo các kỳ tháng của hợp đồng; điện nước/phí đã thu ghi"
              + " nhận theo ngày chốt hóa đơn. Đây là báo cáo quản trị nội bộ.");
      d.close();
      return out.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("Không thể xuất báo cáo PDF", e);
    }
  }

  public byte[] reportExcel(Row report) {
    try (XSSFWorkbook wb = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      CellStyle header = wb.createCellStyle();
      org.apache.poi.ss.usermodel.Font f = wb.createFont();
      f.setBold(true);
      f.setColor(IndexedColors.WHITE.getIndex());
      header.setFont(f);
      header.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
      header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      Sheet summary = wb.createSheet("Tong quan");
      String[][] values = {
        {"NNland · Báo cáo quản trị", "Giá trị"},
        {"Từ ngày", report.get("from").toString()},
        {"Đến ngày", report.get("through").toString()},
        {"Thực nhận", String.valueOf(report.num("gross"))},
        {"Đã hoàn", String.valueOf(report.num("refunded"))},
        {"Dòng tiền ròng", String.valueOf(report.num("net"))},
        {"Tiền thuê phân bổ", String.valueOf(report.num("earned_rent"))},
        {"Điện nước và dịch vụ đã thu", String.valueOf(report.num("earned_utility"))},
        {"Khấu trừ cọc", String.valueOf(report.num("deposit_deduction"))},
        {"Tiền cọc ròng", String.valueOf(report.num("deposit_cash"))}
      };
      for (int n = 0; n < values.length; n++) {
        org.apache.poi.ss.usermodel.Row row = summary.createRow(n);
        row.createCell(0).setCellValue(values[n][0]);
        if (n >= 3) row.createCell(1).setCellValue(Long.parseLong(values[n][1]));
        else row.createCell(1).setCellValue(values[n][1]);
        if (n == 0) for (Cell c : row) c.setCellStyle(header);
      }
      summary.setColumnWidth(0, 11500);
      summary.setColumnWidth(1, 6500);
      Sheet sheet = wb.createSheet("Phieu thu");
      String[] headings = {
        "Phiếu thu",
        "Thanh toán",
        "Khách hàng",
        "Tòa nhà",
        "Căn hộ",
        "Ngày nhận",
        "Mã ngân hàng",
        "Số tiền"
      };
      org.apache.poi.ss.usermodel.Row h = sheet.createRow(0);
      for (int k = 0; k < headings.length; k++) {
        Cell c = h.createCell(k);
        c.setCellValue(headings[k]);
        c.setCellStyle(header);
        sheet.setColumnWidth(k, 6500);
      }
      int index = 1;
      for (Row r : (java.util.List<Row>) report.get("receipts")) {
        org.apache.poi.ss.usermodel.Row row = sheet.createRow(index++);
        String[] data = {
          r.str("code"),
          r.str("payment_code"),
          r.str("full_name"),
          r.str("building_name"),
          r.str("unit_code"),
          ViewFormat.datetime(r.get("received_at")),
          r.str("bank_reference")
        };
        for (int k = 0; k < data.length; k++) row.createCell(k).setCellValue(data[k]);
        row.createCell(7).setCellValue(r.num("amount"));
      }
      sheet.createFreezePane(0, 1);
      Sheet refunds = wb.createSheet("Hoan tien");
      String[] refundHeadings = {
        "Thanh toán", "Mã ngân hàng", "Số tiền", "Khoản mục", "Ngày chuyển", "Lý do", "Tòa nhà"
      };
      org.apache.poi.ss.usermodel.Row refundHeader = refunds.createRow(0);
      for (int k = 0; k < refundHeadings.length; k++) {
        Cell c = refundHeader.createCell(k);
        c.setCellValue(refundHeadings[k]);
        c.setCellStyle(header);
        refunds.setColumnWidth(k, k == 5 ? 13000 : 6500);
      }
      refunds.createFreezePane(0, 1);
      int n = 1;
      for (Row r : (java.util.List<Row>) report.get("refunds")) {
        org.apache.poi.ss.usermodel.Row row = refunds.createRow(n++);
        row.createCell(0).setCellValue(r.str("payment_code"));
        row.createCell(1).setCellValue(r.str("bank_reference"));
        row.createCell(2).setCellValue(r.num("amount"));
        row.createCell(3).setCellValue(ViewFormat.label(r.str("category")));
        row.createCell(4).setCellValue(ViewFormat.datetime(r.get("processed_at")));
        row.createCell(5).setCellValue(r.str("reason"));
        row.createCell(6).setCellValue(r.str("building_name"));
      }
      wb.write(out);
      return out.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("Không thể xuất Excel", e);
    }
  }
}
