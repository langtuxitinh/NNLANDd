<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><main id="main-content"><section class="container detail-page"><div class="breadcrumb"><a href="${ctx}/toa-nha">← Hệ thống tòa nhà</a><span>/</span><c:out value="${building.name}"/></div>
<div class="building-detail-hero"><img src="${ctx}<c:out value='${building.image}'/>" alt="<c:out value='${building.name}'/>"><div><span class="eyebrow">NNLAND RESIDENCE</span><h1><c:out value="${building.name}"/></h1>
<p class="icon-line muted"><svg class="icon"><use href="${ctx}/nnland/icons.svg#pin"/></svg><c:out value="${building.address}"/> · <c:out value="${building.city}"/></p>
<p class="lead"><c:out value="${building.description}"/></p>
<div class="amenity-tags"><c:forEach var="a" items="${building.amenity_list}"><span><c:out value="${a}"/></span></c:forEach></div>
<div class="building-stats"><div><strong>${fn:length(building.units)}</strong><span>Căn hộ đang giới thiệu</span></div>
<div><strong>24/7</strong><span>An ninh tòa nhà</span></div>
<div><strong>NN</strong><span>Quản lý trực tiếp</span></div>
</div>
</div>
</div>
<section class="section-space"><div class="section-heading"><div><span class="eyebrow">CHỌN CĂN HỘ</span><h2>Nơi ở của bạn tại đây</h2>
<p>Khám phá diện tích, tiện nghi và giá thuê từng căn.</p>
</div>
</div>
<div class="apartment-grid"><c:forEach var="item" items="${building.units}"><c:set var="unitCard" value="${item}" scope="request"/><jsp:include page="/WEB-INF/views/nnland/common/apartment-card.jsp"/></c:forEach></div>
<c:if test="${empty building.units}"><div class="empty-state"><h3>Danh sách căn hộ đang được cập nhật.</h3>
<a class="button outline" href="${ctx}/lien-he">Liên hệ tư vấn</a></div>
</c:if></section>
</section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>