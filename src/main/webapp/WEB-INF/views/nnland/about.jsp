<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><main id="main-content"><section class="page-banner"><img src="${ctx}/nnland/images/hero.webp" alt="Không gian NNland"><div class="container"><span class="eyebrow light">YOUR SPACE · YOUR STORY</span><h1>Về NNland</h1>
<p><a href="${ctx}/">Trang chủ</a><span>›</span>Giới thiệu</p>
</div>
</section>
<section class="container section-space"><div class="about-intro"><div><span class="eyebrow">CÂU CHUYỆN CỦA CHÚNG TÔI</span><h2>Một nơi ở tốt<br><em>tạo nên cuộc sống tốt.</em></h2>
</div>
<p class="lead">NNland phát triển hệ thống tòa nhà và căn hộ cho thuê tại nhiều thành phố. Chúng tôi hướng đến trải nghiệm thuê nhà thuận tiện, thông tin rõ ràng và sự chăm sóc tận tâm.</p>
</div>
<div class="about-mosaic"><img src="${ctx}/nnland/images/apartment-living.webp" alt="Phòng khách ấm cúng"><div><img src="${ctx}/nnland/images/amenity-pool.webp" alt="Tiện ích trong khu căn hộ"><blockquote>“Không chỉ là một căn hộ, đó là nơi bạn có thể gọi là nhà.”<small>NNLAND · LIVE YOUR WAY</small></blockquote></div>
</div>
<div class="values-grid"><article><span class="feature-icon">✓</span><h3>Minh bạch</h3>
<p>Thông tin căn hộ, chi phí và lịch sử thanh toán được trình bày rõ ràng.</p>
</article>
<article><span class="feature-icon">⌂</span><h3>Thuận tiện</h3>
<p>Tìm nơi ở, đặt lịch xem và theo dõi thông tin thuê trong cùng một không gian.</p>
</article>
<article><span class="feature-icon">♡</span><h3>Đồng hành</h3>
<p>Đội ngũ quản lý tiếp nhận yêu cầu và hỗ trợ bạn trong suốt kỳ thuê.</p>
</article>
</div>
<div class="cta-card"><div><span class="eyebrow light">START YOUR STORY</span><h2>Sẵn sàng tìm nơi ở mới?</h2>
</div>
<a class="button lime" href="${ctx}/can-ho">Khám phá căn hộ →</a></div>
</section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>