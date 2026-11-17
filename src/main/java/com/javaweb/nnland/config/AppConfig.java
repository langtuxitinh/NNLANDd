package com.javaweb.nnland.config;

import java.time.*;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {
  @Bean
  public Clock clock() {
    return Clock.system(ZoneId.of("Asia/Ho_Chi_Minh"));
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(11);
  }
}
