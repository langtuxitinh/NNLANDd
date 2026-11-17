<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><main id="main-content"><section class="container booking-page"><div class="breadcrumb"><a href="${ctx}/can-ho/${unit.id}">← Chi tiết căn hộ</a><span>/</span><c:out value="${pageTitle}"/></div>
<h1><c:out value="${pageTitle}"/></h1>
<div class="stepper"><span class="active"><b>✓</b>Chọn căn hộ</span><i></i><span class="active"><b>02</b>Thông tin & điều khoản</span><i></i><span><b>03</b>Thanh toán</span></div>
<div class="booking-layout"><div class="stack"><section class="panel"><div class="panel-header"><div><h2>Kỳ thuê của bạn</h2>
<p>Chọn số tháng để xem tổng chi phí trước khi giữ chỗ.</p>
</div>
</div>
<form method="get" action="${ctx}/dat-thue/${unit.id}"><c:if test="${not empty leaseId}"><input type="hidden" name="lease" value="${leaseId}"></c:if><div class="form-grid two"><label>${empty leaseId ? 'Ngày nhận căn hộ' : 'Bắt đầu kỳ gia hạn'}<input required type="date" name="start" value="${quote.start}" min="${today}" ${empty leaseId ? '' : 'readonly'}></label>
<label>Số tháng thuê<input required type="number" min="1" max="24" name="months" value="${quote.months}"></label>
</div>
<div class="action-row spread"><p class="field-help">Ngày trả phòng: <strong><c:out value="${nn:date(quote.end)}"/></strong> (không tính tiền ngày này).</p>
<button class="button outline small" type="submit">Kiểm tra kỳ thuê ↻</button></div>
</form>
<c:choose><c:when test="${quote.available}"><div class="notice">✓ Căn hộ còn trống trong kỳ đã chọn. Chỗ chỉ được giữ sau khi bạn xác nhận bên dưới.</div>
</c:when><c:otherwise><div class="notice danger">Căn hộ chưa sẵn sàng hoặc lịch thuê bị trùng. Vui lòng chọn kỳ khác hoặc liên hệ quản lý.</div>
</c:otherwise></c:choose></section>
<section class="panel"><div class="panel-header"><div><h2>Thông tin người thuê</h2>
<p>Thông tin liên hệ được lấy từ tài khoản của bạn.</p>
</div>
<a class="text-link" href="${ctx}/tai-khoan/thong-tin">Cập nhật →</a></div>
<div class="info-grid"><div><span>Họ tên</span><strong><c:out value="${currentUser.full_name}"/></strong></div>
<div><span>Số điện thoại</span><strong><c:out value="${currentUser.phone}"/></strong></div>
<div><span>Email</span><strong><c:out value="${currentUser.email}"/></strong></div>
<div><span>Năm sinh</span><strong>${currentUser.birth_year}</strong></div>
</div>
</section>
<section class="panel"><div class="panel-header"><div><h2>Điều khoản thuê căn hộ</h2>
<p>Phiên bản <c:out value="${quote.terms_version}"/> · Lưu cùng đơn đặt thuê.</p>
</div>
</div>
<div class="terms-box" tabindex="0"><c:out value="${quote.terms}"/></div>
<form method="post" action="${ctx}/dat-thue/${unit.id}"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><c:if test="${not empty leaseId}"><input type="hidden" name="lease_id" value="${leaseId}"></c:if><input type="hidden" name="start" value="${quote.start}"><input type="hidden" name="months" value="${quote.months}"><input type="hidden" name="quoted_monthly" value="${quote.monthly}"><input type="hidden" name="quoted_deposit" value="${quote.deposit}"><input type="hidden" name="terms_version" value="<c:out value='${quote.terms_version}'/>"><input type="hidden" name="fee_signature" value="<c:out value='${quote.fee_signature}'/>"><label class="check-label"><input required type="checkbox" name="accepted"><span>Tôi đã đọc và đồng ý các điều khoản, kỳ thuê và tổng tiền hiển thị. Tôi hiểu rằng đơn chỉ được xác nhận khi NNland đối chiếu đủ tiền thực nhận.</span></label>
<button class="button primary full" type="submit" ${quote.available ? '' : 'disabled'}>Xác nhận & tiếp tục thanh toán <span>→</span></button></form>
</section>
</div>
<aside class="panel booking-summary"><div class="mini-property"><img src="${ctx}<c:out value='${unit.image}'/>" alt="<c:out value='${unit.name}'/>"><div><strong><c:out value="${unit.name}"/></strong><small><c:out value="${unit.building_name}"/></small><b><c:out value="${nn:shortMoney(quote.monthly)}"/> / tháng</b></div>
</div>
<div class="summary-rows"><div><span>Ngày bắt đầu</span><strong><c:out value="${nn:date(quote.start)}"/></strong></div>
<div><span>Ngày trả phòng</span><strong><c:out value="${nn:date(quote.end)}"/></strong></div>
<div><span>Tiền thuê (${quote.months} tháng)</span><strong><c:out value="${nn:money(quote.rent)}"/></strong></div>
<div><span>Tiền cọc</span><strong><c:out value="${nn:money(quote.deposit)}"/></strong></div>
<div class="total"><span>Tổng thanh toán ban đầu</span><strong><c:out value="${nn:money(quote.total)}"/></strong></div>
</div>
<div class="notice">Thanh toán trước 100% tiền thuê của ${quote.months} tháng. Tiền cọc được theo dõi riêng.</div>
<h3>Phí sinh hoạt thu riêng</h3>
<div class="summary-rows"><div><span>Điện / kWh</span><strong><c:out value="${nn:money(quote.electricity_rate)}"/></strong></div>
<div><span>Nước / m³</span><strong><c:out value="${nn:money(quote.water_rate)}"/></strong></div>
<div><span>Dịch vụ / tháng</span><strong><c:out value="${nn:money(quote.service_fee)}"/></strong></div>
<div><span>Gửi xe / tháng</span><strong><c:out value="${nn:money(quote.parking_fee)}"/></strong></div>
</div>
<p class="field-help">Chỉ số điện nước được ghi khi bàn giao và trên từng hóa đơn.</p>
</aside>
</div>
</section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>