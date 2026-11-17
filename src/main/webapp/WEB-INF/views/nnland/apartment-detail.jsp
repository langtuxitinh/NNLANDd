<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><main id="main-content"><section class="container detail-page"><div class="breadcrumb"><a href="${ctx}/can-ho">← Quay lại danh sách</a><span>/</span><c:out value="${unit.name}"/></div>
<div class="detail-title-row"><div><span class="available-label"><i></i><c:out value="${nn:label(unit.operational_status)}"/></span><h1><c:out value="${unit.name}"/></h1>
<p class="icon-line muted"><svg class="icon"><use href="${ctx}/nnland/icons.svg#pin"/></svg><c:out value="${unit.building_name}"/> · <c:out value="${unit.district}"/>, <c:out value="${unit.city}"/></p>
</div>
<div class="detail-price"><small>Giá thuê mỗi tháng</small><strong><c:out value="${nn:money(unit.monthly_rent)}"/></strong><small>Chưa bao gồm các phí sinh hoạt bên dưới</small></div>
</div>
<div class="detail-gallery"><button class="gallery-main" type="button" data-gallery="${ctx}<c:out value='${unit.image}'/>"><img src="${ctx}<c:out value='${unit.image}'/>" alt="<c:out value='${unit.name}'/>"><span>Xem ảnh căn hộ ↗</span></button><div class="gallery-side"><button type="button" data-gallery="${ctx}<c:out value='${unit.image2}'/>"><img src="${ctx}<c:out value='${unit.image2}'/>" alt="Không gian căn hộ"></button><button type="button" data-gallery="${ctx}<c:out value='${unit.image3}'/>"><img src="${ctx}<c:out value='${unit.image3}'/>" alt="Nội thất căn hộ"></button></div>
</div>
<div class="detail-layout"><div><div class="detail-specs"><div><strong>${unit.bedrooms eq 0 ? 'Studio' : unit.bedrooms}</strong><span>Phòng ngủ</span></div>
<div><strong>${unit.bathrooms}</strong><span>Phòng tắm</span></div>
<div><strong>${unit.area} m²</strong><span>Diện tích</span></div>
<div><strong>Tầng ${unit.floor_no}</strong><span>Mã căn <c:out value="${unit.code}"/></span></div>
</div>
<section class="detail-section"><h2>Về căn hộ này</h2>
<p><c:out value="${unit.description}"/></p>
<span class="badge soft"><c:out value="${unit.type_label}"/></span></section>
<section class="detail-section"><h2>Tiện nghi trong căn hộ</h2>
<div class="amenities-grid"><c:forEach var="a" items="${unit.amenity_list}"><span><b>✓</b><c:out value="${a}"/></span></c:forEach></div>
</section>
<section class="detail-section"><h2>Chi phí rõ ràng từ đầu</h2>
<div class="fee-grid"><div><span>Tiền thuê / tháng</span><strong><c:out value="${nn:money(unit.monthly_rent)}"/></strong></div>
<div><span>Tiền cọc ban đầu</span><strong><c:out value="${nn:money(unit.deposit_amount)}"/></strong></div>
<div><span>Điện theo công tơ</span><strong><c:out value="${nn:money(unit.electricity_rate)}"/> / kWh</strong></div>
<div><span>Nước theo công tơ</span><strong><c:out value="${nn:money(unit.water_rate)}"/> / m³</strong></div>
<div><span>Dịch vụ / tháng</span><strong><c:out value="${nn:money(unit.service_fee)}"/></strong></div>
<div><span>Gửi xe / tháng</span><strong><c:out value="${nn:money(unit.parking_fee)}"/></strong></div>
</div>
<p class="field-help">Tiền thuê được thanh toán trước 100% theo số tháng bạn chọn. Điện nước và phí sinh hoạt được lập hóa đơn riêng. Lịch trống được kiểm tra khi bạn chọn kỳ thuê.</p>
</section>
<section class="detail-section"><h2>Thông tin tòa nhà</h2>
<a class="building-mini" href="${ctx}/toa-nha/${building.id}"><img src="${ctx}<c:out value='${building.image}'/>" alt="<c:out value='${building.name}'/>"><div><h3><c:out value="${building.name}"/></h3>
<p><c:out value="${building.address}"/></p>
<span class="text-link">Khám phá tòa nhà →</span></div>
</a></section>
</div>
<aside class="booking-aside"><span class="eyebrow">MỘT KHÔNG GIAN DÀNH CHO BẠN</span><div class="aside-price"><strong><c:out value="${nn:shortMoney(unit.monthly_rent)}"/></strong><small>/ tháng</small></div>
<p>Trao đổi với quản lý để xác nhận lịch xem thực tế trước khi chọn thuê.</p>
<div class="aside-benefits"><span>✓ Thông tin và chi phí minh bạch</span><span>✓ Quản lý và hỗ trợ trực tiếp</span><span>✓ Theo dõi kỳ thuê trong tài khoản</span></div>
<a class="button primary full" href="${ctx}/dat-thue/${unit.id}">Chọn kỳ thuê <span>→</span></a><a class="button outline full" href="${ctx}/lien-he?unit=${unit.id}">Đặt lịch xem căn hộ</a><a class="small-link" href="${ctx}/dieu-khoan">Xem điều khoản thuê căn hộ</a><c:if test="${not empty unit.manager}"><div class="manager-mini"><span class="avatar">N</span><div><strong><c:out value="${unit.manager.full_name}"/></strong><small>Quản lý phụ trách</small></div>
</div>
</c:if></aside>
</div>
</section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>