<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><main id="main-content"><section class="auth-section"><div class="auth-shell"><aside class="auth-visual"><div class="brand light"><span class="brand-mark">N<i></i></span><span class="brand-word">NN<span>land</span><small>APARTMENT LIVING</small></span></div>
<div><span class="eyebrow light">WELCOME HOME</span><h2>Chào mừng bạn<br>đến với <em>NNland.</em></h2>
<p>Không gian sống phù hợp bắt đầu từ đây.</p>
</div>
<span class="auth-visual-foot">LIVE YOUR WAY · NNLAND</span></aside>
<div class="auth-form"><div class="auth-switch"><a href="${ctx}/dang-nhap">Đăng nhập</a><a class="active" href="${ctx}/dang-ky">Đăng ký</a></div>
<h1>Tạo tài khoản NNland</h1>
<p>Một tài khoản cho hành trình tìm và thuê căn hộ.</p>
<form method="post" action="${ctx}/dang-ky"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><div class="form-grid two"><label>Họ và tên<input required name="full_name" maxlength="150" autocomplete="name"></label>
<label>Tên đăng nhập<input required name="username" minlength="3" maxlength="60" autocomplete="username" placeholder="Ví dụ: minhan"></label>
</div>
<label>Email<input required type="email" name="email" maxlength="190" autocomplete="email"></label>
<div class="form-grid two"><label>Năm sinh<input required type="number" name="birth_year" min="1900" max="${today.year-18}" placeholder="1998"></label>
<label>Số điện thoại<input required name="phone" maxlength="25" autocomplete="tel"></label>
</div>
<label>Giới tính<select name="gender"><option value="OTHER">Không muốn cung cấp</option><option value="FEMALE">Nữ</option><option value="MALE">Nam</option></select></label>
<label>Mật khẩu<div class="password-wrap"><input required type="password" name="password" minlength="8" maxlength="72" autocomplete="new-password"><button type="button" data-password-toggle aria-pressed="false">Hiện</button></div>
</label>
<label>Nhập lại mật khẩu<input required type="password" name="password_confirm" minlength="8" maxlength="72" autocomplete="new-password"></label>
<label class="check-label"><input type="checkbox" name="agree" required><span>Tôi đồng ý với <a class="text-link" href="${ctx}/dieu-khoan" target="_blank" rel="noopener">điều khoản sử dụng NNland</a>.</span></label>
<button class="button primary full" type="submit">Tạo tài khoản <span>→</span></button></form>
<p class="field-help">Mật khẩu tối thiểu 8 ký tự. Tài khoản thuê căn hộ dành cho người từ 18 tuổi.</p>
</div>
</div>
</section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>