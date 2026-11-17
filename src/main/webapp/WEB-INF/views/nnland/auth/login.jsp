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
<div class="auth-form"><div class="auth-switch"><a class="active" href="${ctx}/dang-nhap">Đăng nhập</a><a href="${ctx}/dang-ky">Đăng ký</a></div>
<h1>Chào mừng trở lại!</h1>
<p>Đăng nhập để theo dõi thông tin thuê căn hộ của bạn.</p>
<c:if test="${not empty param.error}"><div class="notice danger" role="alert">Email/tên đăng nhập hoặc mật khẩu chưa đúng.</div>
</c:if><c:if test="${not empty param.expired}"><div class="notice">Phiên đăng nhập đã hết hạn hoặc quyền truy cập đã thay đổi. Hãy đăng nhập lại.</div>
</c:if><form method="post" action="${ctx}/dang-nhap"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><label>Email hoặc tên đăng nhập<input required name="email" maxlength="190" autocomplete="username" placeholder="Email hoặc tên đăng nhập"></label>
<label>Mật khẩu<div class="password-wrap"><input required type="password" name="password" minlength="8" maxlength="72" autocomplete="current-password"><button type="button" data-password-toggle aria-pressed="false">Hiện</button></div>
</label>
<div class="auth-tools"><span>Kết nối an toàn với tài khoản của bạn.</span><a href="${ctx}/quen-mat-khau">Quên mật khẩu?</a></div>
<button class="button primary full" type="submit">Đăng nhập <span>→</span></button></form>
<p class="field-help">Bạn chưa có tài khoản? <a class="text-link" href="${ctx}/dang-ky">Đăng ký tại đây</a></p>
</div>
</div>
</section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>