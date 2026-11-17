package com.javaweb.nnland.config;

import com.javaweb.nnland.model.Row;
import com.javaweb.nnland.repository.Store;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@SuppressWarnings("deprecation")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private final Store db;
  private final BCryptPasswordEncoder encoder;

  public SecurityConfig(Store db, BCryptPasswordEncoder encoder) {
    this.db = db;
    this.encoder = encoder;
  }

  @Bean
  @Override
  public UserDetailsService userDetailsService() {
    return login -> {
      Row a =
          db.one(
              "select * from nn_accounts where (email=? or username=?) and active=true",
              login.toLowerCase(java.util.Locale.ROOT),
              login.toLowerCase(java.util.Locale.ROOT));
      if (a == null) throw new UsernameNotFoundException("Tài khoản không hợp lệ");
      return User.withUsername(a.str("email"))
          .password(a.str("password_hash"))
          .roles(a.str("role"))
          .build();
    };
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public static HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Override
  protected void configure(
      org.springframework.security.config.annotation.authentication.builders
              .AuthenticationManagerBuilder
          auth)
      throws Exception {
    auth.userDetailsService(userDetailsService()).passwordEncoder(encoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/admin/**")
        .hasAnyRole("ADMIN", "MANAGER")
        .antMatchers(
            "/tai-khoan/**",
            "/dat-thue/**",
            "/thanh-toan/**",
            "/yeu-thich/**",
            "/hop-dong/**",
            "/hoa-don/**")
        .authenticated()
        .antMatchers("/api/**", "/repository/**", "/common/**", "/decorators/**")
        .denyAll()
        .anyRequest()
        .permitAll();
    http.formLogin()
        .loginPage("/dang-nhap")
        .loginProcessingUrl("/dang-nhap")
        .usernameParameter("email")
        .passwordParameter("password")
        .successHandler(
            (req, res, auth) -> {
              boolean staff =
                  auth.getAuthorities().stream()
                      .anyMatch(
                          x ->
                              x.getAuthority().equals("ROLE_ADMIN")
                                  || x.getAuthority().equals("ROLE_MANAGER"));
              res.sendRedirect(req.getContextPath() + (staff ? "/admin" : "/tai-khoan"));
            })
        .failureUrl("/dang-nhap?error")
        .permitAll();
    http.logout().logoutUrl("/dang-xuat").logoutSuccessUrl("/?logout").deleteCookies("JSESSIONID");
    http.exceptionHandling().accessDeniedPage("/khong-co-quyen");
    http.headers().contentTypeOptions();
    http.headers().frameOptions().sameOrigin();
    http.headers()
        .referrerPolicy(
            org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
                .ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN);
    http.sessionManagement()
        .maximumSessions(5)
        .sessionRegistry(sessionRegistry())
        .expiredUrl("/dang-nhap?expired");
  }
}
