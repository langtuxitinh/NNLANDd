<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<footer class="footer"><div class="container footer-grid"><div class="footer-brand"><a href="${ctx}/" class="brand light"><span class="brand-mark">N<i></i></span><span class="brand-word">NN<span>land</span><small>APARTMENT LIVING</small></span></a><p>Một nơi ở phù hợp, một cuộc sống dễ chịu. NNland đồng hành cùng bạn từ lúc tìm kiếm đến khi an cư.</p>
<span class="footer-tag">YOUR SPACE · YOUR STORY</span></div>
<div><h3>Khám phá</h3>
<a href="${ctx}/">Trang chủ</a><a href="${ctx}/toa-nha">Hệ thống tòa nhà</a><a href="${ctx}/can-ho">Căn hộ cho thuê</a><a href="${ctx}/gioi-thieu">Về NNland</a></div>
<div><h3>Dành cho bạn</h3>
<a href="${ctx}/tai-khoan">Tài khoản khách hàng</a><a href="${ctx}/dieu-khoan">Điều khoản & thanh toán</a><a href="${ctx}/lien-he">Đặt lịch xem căn hộ</a><a href="${ctx}/tai-khoan/ho-tro">Hỗ trợ cư dân</a></div>
<div><h3>Liên hệ NNland</h3>
<p><svg class="icon"><use href="${ctx}/nnland/icons.svg#pin"/></svg><c:out value="${meta.office_address}"/></p>
<p><svg class="icon"><use href="${ctx}/nnland/icons.svg#phone"/></svg><c:out value="${meta.hotline}"/></p>
<p><svg class="icon"><use href="${ctx}/nnland/icons.svg#mail"/></svg><c:out value="${meta.contact_email}"/></p>
<small>Thứ 2 – Chủ nhật: 08:00 – 21:00</small></div>
</div>
<div class="container footer-bottom"><span>© ${today.year} NNland. Không gian sống của bạn.</span><span>Thông tin rõ ràng. Đồng hành dài lâu.</span></div>
</footer>
