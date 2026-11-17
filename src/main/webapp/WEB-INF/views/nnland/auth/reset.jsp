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
<div class="auth-form"><span class="eyebrow">WELCOME BACK</span><h1>Đặt mật khẩu mới</h1>
<p>Chọn mật khẩu dễ nhớ với bạn và khó đoán với người khác.</p>
<form method="post" action="${ctx}/dat-lai-mat-khau"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><input type="hidden" name="token" value="<c:out value='${token}'/>"><label>Mật khẩu<div class="password-wrap"><input required type="password" name="password" minlength="8" maxlength="72" autocomplete="new-password"><button type="button" data-password-toggle aria-pressed="false">Hiện</button></div>
</label>
<label>Nhập lại mật khẩu<input required type="password" name="password_confirm" minlength="8" maxlength="72" autocomplete="new-password"></label>
<button class="button primary full" type="submit">Lưu mật khẩu mới →</button></form>
</div>
</div>
</section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>