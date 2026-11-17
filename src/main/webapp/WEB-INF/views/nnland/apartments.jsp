<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><main id="main-content"><section class="page-banner"><img src="${ctx}/nnland/images/building-central.webp" alt="Kiến trúc tòa nhà"><div class="container"><span class="eyebrow light">NNLAND LIVING</span><h1>Căn hộ cho thuê</h1>
<p><a href="${ctx}/">Trang chủ</a><span>›</span>Danh sách căn hộ</p>
</div>
</section>
<div class="container listing-content"><jsp:include page="/WEB-INF/views/nnland/common/search.jsp"/><div class="section-heading"><div><span class="eyebrow">NNLAND APARTMENTS</span><h2>Căn hộ dành cho bạn</h2>
<p>${result.total} căn hộ phù hợp · trống từ <c:out value="${nn:date(result.search_start)}"/> đến <c:out value="${nn:date(result.search_end)}"/>.</p>
</div>
<a class="text-link" href="${ctx}/can-ho">Xóa bộ lọc ↻</a></div>
<c:choose><c:when test="${empty result.items}"><div class="empty-state"><span class="empty-icon">⌕</span><h3>Chưa tìm thấy căn hộ phù hợp</h3>
<p>Thử thay đổi khu vực, ngân sách hoặc khoảng thời gian thuê.</p>
<a class="button primary" href="${ctx}/can-ho">Xem tất cả căn hộ</a></div>
</c:when><c:otherwise><div class="apartment-grid"><c:forEach var="item" items="${result.items}"><c:set var="unitCard" value="${item}" scope="request"/><jsp:include page="/WEB-INF/views/nnland/common/apartment-card.jsp"/></c:forEach></div>
<nav class="pagination" aria-label="Phân trang"><c:forEach var="p" begin="1" end="${result.pages}"><c:url var="pageUrl" value="/can-ho"><c:forEach var="f" items="${result.filters}"><c:if test="${f.key ne 'page'}"><c:param name="${f.key}" value="${f.value}"/></c:if></c:forEach><c:param name="page" value="${p}"/></c:url><a href="${pageUrl}" class="${p eq result.page ? 'active' : ''}">${p}</a></c:forEach></nav>
</c:otherwise></c:choose></div>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>