<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><main id="main-content"><section class="page-banner"><img src="${ctx}/nnland/images/building-westlake.webp" alt="Không gian tòa nhà NNland"><div class="container"><span class="eyebrow light">NNLAND RESIDENCE</span><h1>Hệ thống tòa nhà</h1>
<p><a href="${ctx}/">Trang chủ</a><span>›</span>Tòa nhà NNland</p>
</div>
</section>
<section class="container section-space"><div class="section-heading"><div><span class="eyebrow">MỘT NƠI Ở TỐT</span><h2>Những không gian đáng sống</h2>
<p>Toàn bộ tòa nhà thuộc hệ thống NNland tại các thành phố lớn.</p>
</div>
<form method="get" action="${ctx}/toa-nha" class="inline-form"><label class="sr-only" for="building-city">Thành phố</label>
<select name="city" id="building-city" data-autosubmit><option value="">Tất cả thành phố</option><c:forEach var="city" items="${cities}"><option value="<c:out value='${city.city}'/>" ${selectedCity eq city.city ? 'selected' : ''}><c:out value="${city.city}"/></option></c:forEach></select><button class="button outline" type="submit">Lọc</button></form>
</div>
<div class="building-grid"><c:forEach var="item" items="${buildings}"><c:set var="buildingCard" value="${item}" scope="request"/><jsp:include page="/WEB-INF/views/nnland/common/building-card.jsp"/></c:forEach></div>
<c:if test="${empty buildings}"><div class="empty-state"><h3>Chưa có tòa nhà tại khu vực này.</h3>
<a href="${ctx}/toa-nha" class="text-link">Xem tất cả tòa nhà →</a></div>
</c:if></section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>