package com.javaweb.config;

import com.javaweb.utils.ReadFileUtils;
import com.opensymphony.module.sitemesh.filter.PageFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Khi chay bang Tomcat nhung (spring-boot:run) file WEB-INF/web.xml bi bo qua,
 * nen filter SiteMesh (khung giao dien) va servlet doc anh /repository/* khong duoc dang ky.
 * Lop nay dang ky lai bang code, tuong duong voi web.xml.
 * Luu y: chi dung khi chay bang spring-boot:run. Neu deploy file WAR len Tomcat ngoai
 * (web.xml co hieu luc) thi nen xoa file nay de tranh dang ky trung.
 */
@Configuration
public class SiteMeshConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<PageFilter> sitemeshFilter() {
        FilterRegistrationBean<PageFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PageFilter());
        registration.addUrlPatterns("/*");
        registration.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
        // chay sau Spring Security
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public ServletRegistrationBean<ReadFileUtils> displayImageServlet() {
        return new ServletRegistrationBean<>(new ReadFileUtils(), "/repository/*");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // vao "/" thi chuyen sang trang chu (thay cho index.jsp trong web.xml)
        registry.addRedirectViewController("/", "/trang-chu");
    }
}