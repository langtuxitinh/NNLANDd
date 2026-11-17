<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><main id="main-content"><section class="hero"><img class="hero-photo" src="${ctx}/nnland/images/hero.webp" alt="Không gian sống hiện đại, ngập ánh sáng"><div class="hero-shade"></div>
<div class="container hero-inner"><div class="hero-copy"><span class="hero-pill"><i></i> HỆ THỐNG CĂN HỘ NNLAND</span><h1>Tìm nơi ở lý tưởng.<br><em>Sống theo cách</em> của bạn.</h1>
<p>Khám phá căn hộ tại TP. Hồ Chí Minh, Hà Nội và Đà Nẵng. Thông tin rõ ràng, một lựa chọn an tâm và trải nghiệm thuê nhà trọn vẹn.</p>
<div class="hero-actions"><a class="button lime" href="${ctx}/can-ho">Khám phá căn hộ <span>→</span></a><a class="hero-play" href="${ctx}/gioi-thieu"><span>▶</span>Tìm hiểu NNland</a></div>
<div class="hero-trust"><div class="trust-avatars"><span>N</span><span>N</span><span>+</span></div>
<div><strong>Được quản lý trực tiếp</strong><small>Đồng hành cùng bạn trong từng kỳ thuê.</small></div>
</div>
</div>
<div class="hero-float"><span class="feature-icon"><svg class="icon"><use href="${ctx}/nnland/icons.svg#key"/></svg></span><div><strong>Một nơi bạn gọi là nhà.</strong><small>Chọn căn hộ, bắt đầu câu chuyện mới.</small></div>
<span>↗</span></div>
</div>
<div class="hero-bottom"><div class="container"><span>YOUR SPACE, YOUR WAY</span><i></i><span>Không gian sống dành cho bạn</span></div>
</div>
</section>
<section class="container search-section"><jsp:include page="/WEB-INF/views/nnland/common/search.jsp"/></section>
<section class="container section-space intro-row"><div><span class="eyebrow">NNLAND NETWORK</span><h2>Một hệ thống.<br><em>Nhiều lựa chọn</em> sống.</h2>
</div>
<p>Từ nhịp sống năng động giữa thành phố đến không gian thoáng đãng gần biển, tìm một căn hộ phù hợp với cách bạn sống.</p>
<div class="stat"><strong>${fn:length(cities)}</strong><span>Thành phố<br>trong hệ thống</span></div>
<div class="stat"><strong>${unitCount}</strong><span>Căn hộ<br>để bạn khám phá</span></div>
</section>
<section class="soft-section"><div class="container"><div class="section-heading"><div><span class="eyebrow">ĐIỂM ĐẾN CỦA BẠN</span><h2>Chọn thành phố bạn yêu thích</h2>
<p>Những khu vực năng động, những không gian đáng sống.</p>
</div>
<a class="text-link" href="${ctx}/can-ho">Xem tất cả căn hộ →</a></div>
<div class="city-grid"><c:forEach var="city" items="${cityStats}" varStatus="vs"><c:url var="cityUrl" value="/can-ho"><c:param name="city" value="${city.city}"/></c:url><a class="city-card" href="${cityUrl}"><img src="${ctx}/nnland/images/${city.city eq 'Hà Nội' ? 'city-hanoi.webp' : city.city eq 'Đà Nẵng' ? 'city-danang.webp' : 'city-hcm.webp'}" alt="<c:out value='${city.city}'/>" loading="lazy"><div><span>${city.unit_count} CĂN HỘ TRONG HỆ THỐNG</span><h3><c:out value="${city.city}"/></h3>
<p>Khám phá nơi ở của bạn <b>→</b></p>
</div>
</a></c:forEach></div>
</div>
</section>
<section class="container section-space"><div class="section-heading"><div><span class="eyebrow">CĂN HỘ ĐƯỢC QUAN TÂM</span><h2>Không gian sống nổi bật</h2>
<p>Những căn hộ được chọn lọc trong hệ thống NNland.</p>
</div>
<a class="text-link" href="${ctx}/can-ho">Xem tất cả căn hộ →</a></div>
<div class="apartment-grid"><c:forEach var="item" items="${featured}"><c:set var="unitCard" value="${item}" scope="request"/><jsp:include page="/WEB-INF/views/nnland/common/apartment-card.jsp"/></c:forEach></div>
</section>
<section class="soft-section"><div class="container"><div class="section-heading"><div><span class="eyebrow">NNLAND RESIDENCE</span><h2>Khám phá hệ thống tòa nhà</h2>
<p>Mỗi tòa nhà một phong cách sống, cùng một tiêu chuẩn chăm sóc.</p>
</div>
<a class="text-link" href="${ctx}/toa-nha">Tất cả tòa nhà →</a></div>
<div class="building-grid"><c:forEach var="item" items="${buildings}"><c:set var="buildingCard" value="${item}" scope="request"/><jsp:include page="/WEB-INF/views/nnland/common/building-card.jsp"/></c:forEach></div>
</div>
</section>
<section class="container why-section"><div class="why-visual"><img src="${ctx}/nnland/images/apartment-living.webp" alt="Không gian phòng khách NNland" loading="lazy"><span class="round-stamp"><small>YOUR SPACE</small><b>NN</b><small>YOUR STORY</small></span><div class="why-note"><span>✓</span><div><strong>Minh bạch từ đầu</strong><small>Thông tin và chi phí rõ ràng.</small></div>
</div>
</div>
<div><span class="eyebrow">VÌ SAO CHỌN NNLAND</span><h2>Thuê nhà nhẹ nhàng.<br><em>An tâm mỗi ngày.</em></h2>
<p class="lead">Một nơi ở mới nên bắt đầu bằng cảm giác dễ chịu. NNland cùng bạn từ lần xem căn hộ đến những ngày sống tại đây.</p>
<div class="benefit"><span class="feature-icon"><svg class="icon"><use href="${ctx}/nnland/icons.svg#shield"/></svg></span><div><h3>Thông tin minh bạch</h3>
<p>Giá thuê, tiện nghi và các khoản phí được trình bày rõ ràng.</p>
</div>
</div>
<div class="benefit"><span class="feature-icon"><svg class="icon"><use href="${ctx}/nnland/icons.svg#key"/></svg></span><div><h3>Đặt thuê thuận tiện</h3>
<p>Xem căn hộ, chọn kỳ thuê và theo dõi thông tin ngay trong tài khoản.</p>
</div>
</div>
<div class="benefit"><span class="feature-icon"><svg class="icon"><use href="${ctx}/nnland/icons.svg#phone"/></svg></span><div><h3>Luôn có người đồng hành</h3>
<p>Đội ngũ quản lý tiếp nhận và theo dõi các yêu cầu của bạn.</p>
</div>
</div>
<a class="button outline" href="${ctx}/gioi-thieu">Tìm hiểu về NNland →</a></div>
</section>
<section class="cta-band"><div class="container"><div><span class="eyebrow light">START YOUR STORY</span><h2>Căn hộ phù hợp có thể<br><em>đang chờ bạn.</em></h2>
<p>Bắt đầu hành trình tìm một nơi ở mới.</p>
</div>
<a class="button lime" href="${ctx}/can-ho">Tìm căn hộ của bạn →</a></div>
</section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>