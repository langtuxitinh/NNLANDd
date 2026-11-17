<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><main id="main-content"><section class="page-banner"><img src="${ctx}/nnland/images/building-riverside.webp" alt="Tòa nhà NNland"><div class="container"><span class="eyebrow light">WE ARE HERE TO HELP</span><h1>Liên hệ NNland</h1>
<p><a href="${ctx}/">Trang chủ</a><span>›</span>Liên hệ</p>
</div>
</section>
<section class="container section-space contact-layout"><div><span class="eyebrow">LUÔN SẴN SÀNG LẮNG NGHE</span><h2>Chúng tôi luôn<br><em>ở đây cùng bạn.</em></h2>
<p class="lead">Để lại thông tin, đội ngũ NNland sẽ hỗ trợ bạn tìm căn hộ phù hợp hoặc xác nhận lịch xem nhà.</p>
<div class="contact-item"><span class="feature-icon"><svg class="icon"><use href="${ctx}/nnland/icons.svg#phone"/></svg></span><div><strong>Hotline tư vấn</strong><p><c:out value="${meta.hotline}"/></p>
</div>
</div>
<div class="contact-item"><span class="feature-icon"><svg class="icon"><use href="${ctx}/nnland/icons.svg#mail"/></svg></span><div><strong>Email</strong><p><c:out value="${meta.contact_email}"/></p>
</div>
</div>
<div class="contact-item"><span class="feature-icon"><svg class="icon"><use href="${ctx}/nnland/icons.svg#pin"/></svg></span><div><strong>Văn phòng</strong><p><c:out value="${meta.office_address}"/></p>
</div>
</div>
</div>
<form class="panel contact-form" action="${ctx}/lien-he" method="post"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><h2>${empty unit ? 'Gửi lời nhắn cho NNland' : 'Đặt lịch xem căn hộ'}</h2>
<p class="muted">Lịch hẹn chỉ được xác nhận sau khi quản lý phản hồi.</p>
<c:if test="${not empty unit}"><input type="hidden" name="unit_id" value="${unit.id}"><div class="mini-property"><img src="${ctx}<c:out value='${unit.image}'/>" alt="<c:out value='${unit.name}'/>"><div><strong><c:out value="${unit.name}"/></strong><small><c:out value="${unit.building_name}"/></small></div>
</div>
</c:if><div class="form-grid two"><label>Họ và tên<input required name="full_name" maxlength="150" value="<c:out value='${currentUser.full_name}'/>" autocomplete="name"></label>
<label>Số điện thoại<input required name="phone" maxlength="25" value="<c:out value='${currentUser.phone}'/>" autocomplete="tel"></label>
</div>
<label>Email<input required name="email" type="email" maxlength="190" value="<c:out value='${currentUser.email}'/>" autocomplete="email"></label>
<div class="form-grid two"><label>Ngày mong muốn<input type="date" name="preferred_date" min="${today}"></label>
<label>Khung giờ<select name="preferred_time"><option value="">Chọn khung giờ</option><option>08:00 – 11:00</option><option>13:00 – 17:00</option><option>18:00 – 20:00</option></select></label>
</div>
<label>Nội dung<textarea required name="message" rows="5" maxlength="3000" placeholder="Chia sẻ nhu cầu của bạn..."></textarea></label>
<label class="honeypot" aria-hidden="true">Website<input name="website" tabindex="-1" autocomplete="off"></label>
<button class="button primary full" type="submit">Gửi thông tin <span>→</span></button></form>
</section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>