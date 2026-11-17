<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<article class="building-card"><a class="card-photo" href="${ctx}/toa-nha/${buildingCard.id}"><img src="${ctx}<c:out value='${buildingCard.image}'/>" alt="<c:out value='${buildingCard.name}'/>" loading="lazy"><span class="city-chip"><svg class="icon"><use href="${ctx}/nnland/icons.svg#pin"/></svg><c:out value="${buildingCard.city}"/></span></a><div class="card-content"><div class="card-kicker-row"><span class="card-kicker">NNLAND RESIDENCE</span><span class="muted">${buildingCard.unit_count} căn hộ</span></div>
<h3><a href="${ctx}/toa-nha/${buildingCard.id}"><c:out value="${buildingCard.name}"/></a></h3>
<p class="muted icon-line"><svg class="icon"><use href="${ctx}/nnland/icons.svg#pin"/></svg><c:out value="${buildingCard.address}"/></p>
<p class="building-desc"><c:out value="${buildingCard.description}"/></p>
<div class="amenity-tags"><c:forEach var="amenity" items="${fn:split(buildingCard.amenities,'|')}" end="2"><span><c:out value="${amenity}"/></span></c:forEach></div>
<a class="building-link" href="${ctx}/toa-nha/${buildingCard.id}">Khám phá tòa nhà <span>→</span></a></div>
</article>
