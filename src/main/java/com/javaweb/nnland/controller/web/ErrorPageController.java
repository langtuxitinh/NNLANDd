package com.javaweb.nnland.controller.web;

import javax.servlet.http.*;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController implements ErrorController {
  @RequestMapping("/error")
  public String error(HttpServletRequest request, HttpServletResponse response, Model m) {
    Object status = request.getAttribute("javax.servlet.error.status_code");
    int code = status instanceof Number ? ((Number) status).intValue() : 500;
    response.setStatus(code);
    m.addAttribute(
        "errorTitle", code == 404 ? "Trang bạn tìm chưa có ở đây" : "Không thể hoàn tất yêu cầu");
    m.addAttribute(
        "errorMessage",
        code == 404
            ? "Đường dẫn có thể đã thay đổi. Hãy quay lại danh sách căn hộ."
            : "Vui lòng tải lại trang. Nếu vẫn gặp vấn đề, hãy liên hệ NNland để được hỗ trợ.");
    return "error";
  }

  @RequestMapping("/khong-co-quyen")
  public String denied(HttpServletResponse response, Model m) {
    response.setStatus(403);
    m.addAttribute("errorTitle", "Bạn chưa có quyền truy cập");
    m.addAttribute(
        "errorMessage",
        "Vui lòng sử dụng tài khoản được phân công quản lý hoặc trở về trang cá nhân.");
    return "error";
  }
}
