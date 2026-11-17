<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp" %>
<%-- The toa nha dung chung. Duoc include tinh ben trong c:forEach, can bien: b (BuildingCardResponse), cardCol (vd: col-md-4) --%>
<div class="col-12 ${cardCol}">
    <div class="product1">
        <div class="product1-image"<c:if test="${not empty b.imageUrl}"> style="background-image:url('<c:out value="${pageContext.request.contextPath}/repository${b.imageUrl}"/>')"</c:if>></div>
        <div class="product1-conntent">
            <div class="product1-conntent-header">
                <a href="${pageContext.request.contextPath}/chi-tiet-${b.id}"><c:out value="${b.name}"/></a>
            </div>
            <span class="product1-conntent-title"><c:out value="${b.description}"/></span>
            <ul class="product1-conntent-list">
                <li class="product1-conntent-item">
                    <i class="bi bi-geo-alt-fill"></i>
                    <span><c:out value="${b.address}"/></span>
                </li>
                <li class="product1-conntent-item">
                    <i class="bi bi-building"></i>
                    <span>Loại BĐS: <c:out value="${b.typeNames}"/></span>
                </li>
                <li class="product1-conntent-item">
                    <i class="bi bi-arrows-fullscreen"></i>
                    <span>Diện tích: <c:out value="${b.areaText}"/></span>
                </li>
            </ul>
        </div>
        <div class="product1-footer">
            <span class="product1-footer-cost"><c:out value="${b.priceText}"/></span>
            <button type="button" class="product1-footer-detail"><a href="${pageContext.request.contextPath}/chi-tiet-${b.id}"
                                                                      style="color:#fff">Xem chi tiết</a></button>
        </div>
    </div>
</div>
