<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<c:if test="${not workspace}"><div class="topline"><div class="wide-container"><span><svg class="icon"><use href="${ctx}/nnland/icons.svg#shield"/></svg> Căn hộ chính chủ · Quản lý trực tiếp bởi NNland</span><span class="topline-right">Tư vấn: <strong><c:out value="${meta.hotline}"/></strong><i></i> 08:00 – 21:00 mỗi ngày</span></div>
</div>
</c:if>
<header class="site-header"><div class="wide-container header-inner"><a href="${ctx}/" class="brand" aria-label="NNland – Trang chủ"><span class="brand-mark">N<i></i></span><span class="brand-word">NN<span>land</span><small>APARTMENT LIVING</small></span></a>
<c:choose><c:when test="${adminView}"><div class="workspace-label">Quản trị &amp; vận hành <span class="badge soft">NNland</span></div>
</c:when><c:otherwise><nav class="main-nav" id="main-nav" aria-label="Điều hướng chính"><a class="${activeNav eq 'home' ? 'active' : ''}" href="${ctx}/">Trang chủ</a><a class="${activeNav eq 'buildings' ? 'active' : ''}" href="${ctx}/toa-nha">Tòa nhà</a><a class="${activeNav eq 'apartments' ? 'active' : ''}" href="${ctx}/can-ho">Căn hộ</a><a class="${activeNav eq 'about' ? 'active' : ''}" href="${ctx}/gioi-thieu">Về NNland</a><a class="${activeNav eq 'contact' ? 'active' : ''}" href="${ctx}/lien-he">Liên hệ</a></nav>
</c:otherwise></c:choose>
<div class="header-actions"><c:if test="${workspace}"><button class="icon-button sidebar-toggle" type="button" aria-label="Mở menu tài khoản" aria-expanded="false"><svg class="icon"><use href="${ctx}/nnland/icons.svg#menu"/></svg></button></c:if><a class="icon-button favorite-header" href="${ctx}/tai-khoan/yeu-thich" aria-label="Căn hộ yêu thích"><svg class="icon"><use href="${ctx}/nnland/icons.svg#heart"/></svg></a><c:choose><c:when test="${empty currentUser}"><a class="button outline login-link" href="${ctx}/dang-nhap"><svg class="icon"><use href="${ctx}/nnland/icons.svg#user"/></svg>Đăng nhập</a></c:when><c:otherwise><details class="account-dropdown"><summary><span class="avatar"><c:out value="${fn:substring(currentUser.full_name,0,1)}"/></span><span class="user-name"><c:out value="${currentUser.full_name}"/></span><c:if test="${unread gt 0}"><span class="notification-dot"></span></c:if></summary><div class="account-menu"><a href="${ctx}/tai-khoan">Không gian của bạn</a><a href="${ctx}/tai-khoan/thong-bao">Thông báo <span class="badge">${unread}</span></a><c:if test="${isStaff}"><a href="${ctx}/admin">Trang quản lý</a></c:if><form action="${ctx}/dang-xuat" method="post"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><button type="submit">Đăng xuất</button></form>
</div>
</details>
</c:otherwise></c:choose><c:if test="${not adminView}"><button class="icon-button mobile-nav-toggle" type="button" aria-controls="main-nav" aria-expanded="false" aria-label="Mở menu"><svg class="icon"><use href="${ctx}/nnland/icons.svg#menu"/></svg></button></c:if></div>
</div>
</header>
<template id="district-options"><c:forEach var="location" items="${locationOptions}"><option data-city="<c:out value='${location.city}'/>" value="<c:out value='${location.district}'/>"><c:out value="${location.district}"/></option></c:forEach></template>