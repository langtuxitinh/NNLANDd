package com.javaweb.nnland.config;

import com.javaweb.nnland.service.UploadService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  private final UploadService uploads;

  public WebConfig(UploadService uploads) {
    this.uploads = uploads;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/uploads/**")
        .addResourceLocations(uploads.directory().toUri().toString())
        .setCachePeriod(86400);
  }
}
