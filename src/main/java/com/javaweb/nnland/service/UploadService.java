package com.javaweb.nnland.service;

import com.javaweb.nnland.model.BusinessException;
import com.javaweb.nnland.utils.Rules;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {
  private final Path directory;

  public UploadService(@Value("${nnland.upload-dir:./uploads}") String dir) throws IOException {
    directory = Paths.get(dir).toAbsolutePath().normalize();
    Files.createDirectories(directory);
  }

  public Path directory() {
    return directory;
  }

  public String image(MultipartFile file) {
    if (file == null || file.isEmpty()) return null;
    Rules.check(file.getSize() <= 5 * 1024 * 1024, "Ảnh tối đa 5 MB.");
    try (ImageInputStream input = ImageIO.createImageInputStream(file.getInputStream())) {
      Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
      Rules.check(readers.hasNext(), "Chỉ nhận ảnh JPG hoặc PNG hợp lệ.");
      ImageReader reader = readers.next();
      try {
        String format = reader.getFormatName().toLowerCase(Locale.ROOT);
        Rules.check(
            Arrays.asList("jpeg", "jpg", "png").contains(format), "Chỉ nhận ảnh JPG hoặc PNG.");
        reader.setInput(input, true, true);
        int width = reader.getWidth(0), height = reader.getHeight(0);
        Rules.check(
            width >= 100 && height >= 100 && (long) width * height <= 30000000,
            "Ảnh cần tối thiểu 100×100 và tối đa 30 triệu điểm ảnh.");
        BufferedImage original = reader.read(0);
        double scale = Math.min(1, 1600.0 / Math.max(width, height));
        BufferedImage normalized =
            new BufferedImage(
                (int) (width * scale), (int) (height * scale), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = normalized.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, normalized.getWidth(), normalized.getHeight());
        g.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(original, 0, 0, normalized.getWidth(), normalized.getHeight(), null);
        g.dispose();
        String name = UUID.randomUUID().toString() + ".jpg";
        ImageIO.write(normalized, "jpg", directory.resolve(name).toFile());
        return "/uploads/" + name;
      } finally {
        reader.dispose();
      }
    } catch (BusinessException e) {
      throw e;
    } catch (IOException e) {
      throw new BusinessException("Không đọc được ảnh. Vui lòng thử tệp JPG/PNG khác.");
    }
  }
}
