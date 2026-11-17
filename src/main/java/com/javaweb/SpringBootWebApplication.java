package com.javaweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Keeps the original NetBeans/Maven entry point. Legacy modules are preserved but not activated.
 */
@SpringBootApplication(
    scanBasePackages = "com.javaweb.nnland",
    exclude = {HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
public class SpringBootWebApplication extends SpringBootServletInitializer {
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder app) {
    return app.sources(SpringBootWebApplication.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(SpringBootWebApplication.class, args);
  }
}
