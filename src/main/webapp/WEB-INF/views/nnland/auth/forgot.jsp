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
<div class="auth-form"><span class="eyebrow">ACCOUNT SUPPORT</span><h1>Quên mật khẩu?</h1>
<p>Nhập email đã đăng ký. Nếu tài khoản tồn tại, NNland sẽ tiếp nhận yêu cầu gửi liên kết đặt lại mật khẩu.</p>
<form method="post" action="${ctx}/quen-mat-khau"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><label>Email của bạn<input required type="email" name="email" maxlength="190" autocomplete="email"></label>
<button class="button primary full" type="submit">Yêu cầu đặt lại mật khẩu →</button></form>
<p class="field-help">Liên kết có hiệu lực trong 30 phút và chỉ dùng một lần. <a class="text-link" href="${ctx}/dang-nhap">Quay lại đăng nhập</a></p>
</div>
</div>
</section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>