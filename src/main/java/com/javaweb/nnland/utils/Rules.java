package com.javaweb.nnland.utils;

import com.javaweb.nnland.model.BusinessException;
import java.math.*;
import java.time.*;
import java.util.*;

public final class Rules {
  private Rules() {}

  public static void check(boolean condition, String message) {
    if (!condition) throw new BusinessException(message);
  }

  public static String text(String value, int max, String label) {
    String s = value == null ? "" : value.trim();
    check(!s.isEmpty() && s.length() <= max, label + " không hợp lệ (tối đa " + max + " ký tự).");
    return s;
  }

  public static String optional(String value, int max) {
    String s = value == null ? "" : value.trim();
    check(s.length() <= max, "Nội dung quá dài.");
    return s;
  }

  public static String email(String s) {
    s = text(s, 190, "Email").toLowerCase(Locale.ROOT);
    check(s.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"), "Địa chỉ email không hợp lệ.");
    return s;
  }

  public static String phone(String s) {
    s = text(s, 25, "Số điện thoại");
    check(s.matches("[+0-9() .-]{8,25}"), "Số điện thoại không hợp lệ.");
    return s;
  }

  public static void password(String s) {
    check(
        s != null
            && s.length() >= 8
            && s.length() <= 72
            && s.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72,
        "Mật khẩu cần có từ 8 đến 72 ký tự.");
  }

  public static long amount(String s, String label) {
    try {
      long n = Long.parseLong(s);
      check(n >= 0 && n <= 100000000000L, label + " phải từ 0 đến 100 tỷ đồng.");
      return n;
    } catch (NumberFormatException e) {
      throw new BusinessException(label + " phải là số nguyên đồng.");
    }
  }

  public static BigDecimal meter(String s) {
    try {
      BigDecimal n = new BigDecimal(s);
      check(
          n.signum() >= 0 && n.compareTo(new BigDecimal("999999999")) <= 0 && n.scale() <= 3,
          "Chỉ số công tơ không hợp lệ.");
      return n;
    } catch (Exception e) {
      throw new BusinessException("Chỉ số công tơ phải là số không âm, tối đa 3 chữ số thập phân.");
    }
  }

  public static int integer(String s, int min, int max, String label) {
    try {
      int n = Integer.parseInt(s);
      check(n >= min && n <= max, label + " phải từ " + min + " đến " + max + ".");
      return n;
    } catch (NumberFormatException e) {
      throw new BusinessException(label + " phải là số nguyên.");
    }
  }

  public static LocalDate date(String s) {
    try {
      return LocalDate.parse(s);
    } catch (Exception e) {
      throw new BusinessException("Ngày không hợp lệ.");
    }
  }

  public static long multiply(long amount, int months) {
    try {
      return Math.multiplyExact(amount, (long) months);
    } catch (ArithmeticException e) {
      throw new BusinessException("Số tiền vượt giới hạn.");
    }
  }

  public static String code(String prefix) {
    return prefix
        + "-"
        + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT);
  }

  public static String fingerprint(String text) {
    try {
      return Base64.getUrlEncoder()
          .withoutPadding()
          .encodeToString(
              java.security.MessageDigest.getInstance("SHA-256")
                  .digest(text.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 is required by Java", e);
    }
  }

  public static String choice(String value, String... allowed) {
    check(Arrays.asList(allowed).contains(value), "Lựa chọn không hợp lệ.");
    return value;
  }

  public static String image(String value) {
    String s = text(value, 255, "Đường dẫn ảnh");
    check(
        s.matches(
            "(/nnland/images/[A-Za-z0-9._-]+\\.(webp|jpg|jpeg|png)|/uploads/[a-f0-9-]{36}\\.jpg)"),
        "Chọn ảnh trong thư viện NNland.");
    return s;
  }
}
