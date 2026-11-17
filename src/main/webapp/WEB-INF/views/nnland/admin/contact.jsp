<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><div class="detail-grid"><section class="panel"><span class="badge ${contact.status}"><c:out value="${nn:label(contact.status)}"/></span><h2 style="margin-top:20px"><c:out value="${contact.full_name}"/></h2>
<div class="info-grid"><div><span>Email</span><strong><c:out value="${contact.email}"/></strong></div>
<div><span>Điện thoại</span><strong><c:out value="${contact.phone}"/></strong></div>
<div><span>Căn hộ</span><strong><c:out value="${contact.unit_name}"/></strong></div>
<div><span>Tòa nhà</span><strong><c:out value="${contact.building_name}"/></strong></div>
<div><span>Ngày hẹn mong muốn</span><strong><c:out value="${nn:date(contact.preferred_date)}"/></strong></div>
<div><span>Khung giờ</span><strong><c:out value="${contact.preferred_time}"/></strong></div>
</div>
<h3 style="margin-top:25px">Nội dung</h3>
<p class="description-text"><c:out value="${contact.message}"/></p>
<c:if test="${not empty contact.manager_note}"><div class="notice"><c:out value="${contact.manager_note}"/></div>
</c:if></section>
<form class="panel" method="post" action="${ctx}/admin/lich-hen/${contact.id}"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><h2>Phản hồi khách hàng</h2>
<p class="field-help">Ghi rõ lịch hẹn được xác nhận, người liên hệ hoặc thông tin cần bổ sung.</p>
<label>Trạng thái<select name="status"><c:forTokens var="state" items="NEW,CONFIRMED,COMPLETED,CANCELLED" delims=","><option value="${state}" ${contact.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
<label>Nội dung phản hồi<textarea required name="note" maxlength="2000" rows="6"><c:out value="${contact.manager_note}"/></textarea></label>
<button class="button primary full" type="submit">Lưu & tạo phản hồi</button><a class="small-link" href="${ctx}/admin/lich-hen">← Quay lại danh sách</a></form>
</div>
</main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>