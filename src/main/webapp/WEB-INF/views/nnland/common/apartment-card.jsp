<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<article class="apartment-card"><a class="card-photo" href="${ctx}/can-ho/${unitCard.id}"><img src="${ctx}<c:out value='${unitCard.image}'/>" alt="<c:out value='${unitCard.name}'/>" loading="lazy"><c:if test="${unitCard.featured}"><span class="featured-tag">✧ Nổi bật</span></c:if><span class="availability"><i></i><c:out value="${unitCard.operational_status eq 'READY' ? 'Đang nhận đặt thuê' : nn:label(unitCard.operational_status)}"/></span></a><c:choose><c:when test="${not empty currentUser}"><form class="card-favorite" action="${ctx}/yeu-thich/${unitCard.id}" method="post"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><button class="icon-button ${favoriteIds.contains(unitCard.id) ? 'liked' : ''}" type="submit" aria-label="Lưu hoặc bỏ lưu căn hộ"><svg class="icon"><use href="${ctx}/nnland/icons.svg#heart"/></svg></button></form>
</c:when><c:otherwise><a class="icon-button card-favorite" href="${ctx}/dang-nhap" aria-label="Đăng nhập để lưu căn hộ"><svg class="icon"><use href="${ctx}/nnland/icons.svg#heart"/></svg></a></c:otherwise></c:choose><div class="card-content"><span class="card-kicker"><c:out value="${unitCard.building_name}"/></span><h3><a href="${ctx}/can-ho/${unitCard.id}"><c:out value="${unitCard.name}"/></a></h3>
<p class="muted icon-line"><svg class="icon"><use href="${ctx}/nnland/icons.svg#pin"/></svg><c:out value="${unitCard.district}"/>, <c:out value="${unitCard.city}"/></p>
<div class="unit-specs"><span><svg class="icon"><use href="${ctx}/nnland/icons.svg#bed"/></svg>${unitCard.bedrooms eq 0 ? 'Studio' : unitCard.bedrooms} ${unitCard.bedrooms eq 0 ? '' : 'PN'}</span><span><svg class="icon"><use href="${ctx}/nnland/icons.svg#bath"/></svg>${unitCard.bathrooms} WC</span><span><svg class="icon"><use href="${ctx}/nnland/icons.svg#area"/></svg>${unitCard.area} m²</span></div>
<div class="card-price-row"><div><strong><c:out value="${nn:shortMoney(unitCard.monthly_rent)}"/></strong><small>/ tháng</small></div>
<a href="${ctx}/can-ho/${unitCard.id}">Chi tiết <span>→</span></a></div>
</div>
<a class="card-book-link" href="${ctx}/dat-thue/${unitCard.id}">Đặt thuê căn hộ</a></article>
