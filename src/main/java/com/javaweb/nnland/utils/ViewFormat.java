package com.javaweb.nnland.utils;

import java.math.*;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class ViewFormat {
  private ViewFormat() {}

  public static String money(Object value) {
    if (value == null) return "0 ₫";
    return NumberFormat.getIntegerInstance(new Locale("vi", "VN"))
            .format(new BigDecimal(value.toString()))
        + " ₫";
  }

  public static String shortMoney(Object value) {
    if (value == null) return "0";
    BigDecimal n =
        new BigDecimal(value.toString()).divide(new BigDecimal("1000000")).stripTrailingZeros();
    return n.toPlainString().replace('.', ',') + " triệu";
  }

  public static String date(Object value) {
    if (value == null) return "—";
    LocalDate d;
    if (value instanceof java.sql.Date) d = ((java.sql.Date) value).toLocalDate();
    else if (value instanceof LocalDate) d = (LocalDate) value;
    else d = LocalDate.parse(value.toString().substring(0, 10));
    return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
  }

  public static String datetime(Object value) {
    if (value == null) return "—";
    LocalDateTime t;
    if (value instanceof Timestamp) t = ((Timestamp) value).toLocalDateTime();
    else if (value instanceof LocalDateTime) t = (LocalDateTime) value;
    else t = LocalDateTime.parse(value.toString().replace(' ', 'T'));
    return t.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
  }

  private static final Map<String, String> LABELS = new HashMap<>();

  static {
    String[][] pairs = {
      {"HOLD", "Đang giữ chỗ"},
      {"PAYMENT_REVIEW", "Chờ đối chiếu tiền"},
      {"CONFIRMED", "Đã xác nhận"},
      {"EXPIRED", "Hết giữ chỗ"},
      {"CANCELLED", "Đã hủy"},
      {"PENDING", "Đang chờ"},
      {"PARTIAL", "Đã nhận một phần"},
      {"PAID", "Đã thanh toán"},
      {"RECONCILE", "Cần đối soát"},
      {"REFUND_PENDING", "Chờ hoàn tiền"},
      {"REFUNDED", "Đã hoàn tiền"},
      {"RESERVED", "Chờ nhận căn hộ"},
      {"ACTIVE", "Đang lưu trú"},
      {"ENDING", "Chờ trả phòng"},
      {"CLOSED", "Đã kết thúc"},
      {"UNPAID", "Chưa thanh toán đủ"},
      {"NEW", "Mới"},
      {"EXTENSION", "Gia hạn"},
      {"RENT", "Tiền thuê"},
      {"UTILITY", "Điện nước & dịch vụ"},
      {"DEPOSIT", "Tiền cọc"},
      {"OVERPAY", "Tiền chuyển thừa"},
      {"READY", "Sẵn sàng"},
      {"MAINTENANCE", "Đang bảo trì"},
      {"INACTIVE", "Tạm ngừng"},
      {"OPEN", "Đã tiếp nhận"},
      {"IN_PROGRESS", "Đang xử lý"},
      {"WAITING", "Chờ bổ sung"},
      {"RESOLVED", "Đã giải quyết"},
      {"COMPLETED", "Hoàn tất"},
      {"APPROVED", "Đã chấp thuận"},
      {"REJECTED", "Không chấp thuận"},
      {"CHECKOUT", "Trả phòng"},
      {"CANCEL", "Hủy đặt thuê"},
      {"USER", "Khách hàng"},
      {"MANAGER", "Quản lý"},
      {"ADMIN", "Quản trị viên"},
      {"NORMAL", "Thông thường"},
      {"URGENT", "Khẩn cấp"},
      {"ELECTRIC", "Điện"},
      {"WATER", "Nước"},
      {"FURNITURE", "Nội thất"},
      {"INTERNET", "Internet"},
      {"OTHER", "Khác"},
      {"SENT", "Đã gửi"},
      {"FAILED", "Gửi chưa thành công"},
      {"SENDING", "Đang gửi"},
      {"STANDARD", "Căn hộ tiêu chuẩn"},
      {"STUDIO", "Studio"},
      {"OFFICETEL", "Officetel"},
      {"DUPLEX", "Duplex"},
      {"PENTHOUSE", "Penthouse"}
    };
    for (String[] p : pairs) LABELS.put(p[0], p[1]);
  }

  public static String label(String value) {
    return LABELS.getOrDefault(value, value == null ? "—" : value);
  }
}
