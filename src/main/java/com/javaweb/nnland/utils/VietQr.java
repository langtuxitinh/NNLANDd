package com.javaweb.nnland.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.javaweb.nnland.model.BusinessException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;

public final class VietQr {
  private VietQr() {}

  private static String field(String id, String value) {
    Rules.check(value.length() <= 99, "Dữ liệu QR quá dài.");
    return id + String.format("%02d", value.length()) + value;
  }

  public static String payload(String bin, String account, long amount, String reference) {
    Rules.check(
        bin.matches("[0-9]{6}") && account.matches("[0-9]{6,34}") && amount > 0,
        "Chưa cấu hình đầy đủ tài khoản nhận tiền hoặc không còn tiền cần trả.");
    String content =
        Normalizer.normalize(reference, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .replaceAll("[^A-Za-z0-9 -]", "");
    if (content.length() > 25) content = content.substring(0, 25);
    String merchant =
        field("00", "A000000727")
            + field("01", field("00", bin) + field("01", account))
            + field("02", "QRIBFTTA");
    String raw =
        field("00", "01")
            + field("01", "12")
            + field("38", merchant)
            + field("53", "704")
            + field("54", String.valueOf(amount))
            + field("58", "VN")
            + field("62", field("08", content))
            + "6304";
    int crc = 0xffff;
    for (byte b : raw.getBytes(StandardCharsets.UTF_8)) {
      crc ^= (b & 255) << 8;
      for (int k = 0; k < 8; k++) crc = (crc & 0x8000) != 0 ? (crc << 1) ^ 0x1021 : crc << 1;
      crc &= 0xffff;
    }
    return raw + String.format("%04X", crc);
  }

  public static byte[] png(String bin, String account, long amount, String reference) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(
          new QRCodeWriter()
              .encode(payload(bin, account, amount, reference), BarcodeFormat.QR_CODE, 360, 360),
          "PNG",
          out);
      return out.toByteArray();
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException("Không thể tạo QR", e);
    }
  }
}
