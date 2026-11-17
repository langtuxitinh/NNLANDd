<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><div class="action-row spread" style="margin-bottom:20px"><span class="badge ${lease.status}"><c:out value="${nn:label(lease.status)}"/></span><div class="action-row"><a class="button outline small" href="${ctx}/hop-dong/${lease.id}/pdf">Tải hợp đồng PDF</a><a class="button outline small" href="${ctx}/${adminView ? 'admin/hop-dong' : 'tai-khoan/luu-tru'}">← Danh sách hợp đồng</a></div>
</div>
<div class="detail-grid"><div class="stack"><section class="panel"><div class="panel-header"><div><h2><c:out value="${lease.unit_name}"/></h2>
<p><c:out value="${lease.building_name}"/> · Mã căn <c:out value="${lease.unit_code}"/></p>
</div>
<span class="badge soft"><c:out value="${lease.code}"/></span></div>
<div class="info-grid"><div><span>Khách thuê</span><strong><c:out value="${lease.full_name}"/></strong></div>
<div><span>Email / Điện thoại</span><strong><c:out value="${lease.email}"/><br><c:out value="${lease.phone}"/></strong></div>
<div><span>Ngày bắt đầu</span><strong><c:out value="${nn:date(lease.start_date)}"/></strong></div>
<div><span>Ngày trả phòng (không tính tiền)</span><strong><c:out value="${nn:date(lease.end_date)}"/></strong></div>
<div><span>Tiền thuê / tháng</span><strong><c:out value="${nn:money(lease.monthly_rent)}"/></strong></div>
<div><span>Tiền cọc ban đầu</span><strong><c:out value="${nn:money(lease.deposit_held)}"/></strong></div>
<div><span>Ngày ký & nhận phòng</span><strong><c:out value="${nn:datetime(lease.checked_in_at)}"/></strong></div>
<div><span>Ngày trả thực tế</span><strong><c:out value="${nn:date(lease.checkout_date)}"/></strong></div>
<div><span>Điện đầu kỳ</span><strong>${lease.electricity_start} kWh</strong></div>
<div><span>Nước đầu kỳ</span><strong>${lease.water_start} m³</strong></div>
</div>
<c:if test="${lease.status eq 'RESERVED'}"><div class="notice">Căn hộ đã được đặt trước. Hợp đồng được ký trực tiếp và xác nhận bàn giao vào ngày nhận phòng.</div>
</c:if><c:if test="${not empty lease.checkout_note}"><div class="notice"><strong>Quyết toán bàn giao:</strong> <c:out value="${lease.checkout_note}"/><br>Khấu trừ cọc: <c:out value="${nn:money(lease.deposit_deduction)}"/></div>
</c:if></section>
<section class="panel"><h2>Các kỳ thuê đã đăng ký</h2>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Mã đơn</th><th>Kỳ thuê</th><th>Tiền thuê</th><th>Tình trạng</th></tr>
</thead>
<tbody><c:forEach var="p" items="${periods}"><tr><td><a href="${ctx}/${adminView ? 'admin/thanh-toan' : 'thanh-toan'}/${p.payment_id}"><c:out value="${p.code}"/></a><small><c:out value="${nn:label(p.kind)}"/></small></td><td><c:out value="${nn:date(p.start_date)}"/> → <c:out value="${nn:date(p.end_date)}"/></td><td class="money"><c:out value="${nn:money(p.rent_total)}"/></td><td><span class="badge ${p.status}"><c:out value="${nn:label(p.status)}"/></span></td></tr>
</c:forEach></tbody>
</table>
</div>
</section>
<section class="panel"><div class="panel-header"><h2>Hóa đơn của hợp đồng</h2>
<c:if test="${adminView and (lease.status eq 'ACTIVE' or lease.status eq 'ENDING')}"><a class="button outline small" href="${ctx}/admin/hoa-don/moi?lease=${lease.id}">Lập hóa đơn</a></c:if></div>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Kỳ hóa đơn</th><th>Tổng cộng</th><th>Trạng thái</th><th></th></tr>
</thead>
<tbody><c:forEach var="i" items="${invoices}"><tr><td><c:out value="${i.period}"/></td><td><c:out value="${nn:money(i.total)}"/></td><td><span class="badge ${i.status}"><c:out value="${nn:label(i.status)}"/></span></td><td><a href="${ctx}/${adminView ? 'admin/hoa-don' : 'hoa-don'}/${i.id}">Chi tiết →</a></td></tr>
</c:forEach><c:if test="${empty invoices}"><tr><td colspan="4" class="muted">Chưa có hóa đơn sinh hoạt.</td></tr>
</c:if></tbody>
</table>
</div>
</section>
</div>
<aside class="stack"><section class="panel"><h2>Chi phí sinh hoạt</h2>
<div class="summary-rows"><div><span>Điện / kWh</span><strong><c:out value="${nn:money(lease.electricity_rate)}"/></strong></div>
<div><span>Nước / m³</span><strong><c:out value="${nn:money(lease.water_rate)}"/></strong></div>
<div><span>Dịch vụ / tháng</span><strong><c:out value="${nn:money(lease.service_fee)}"/></strong></div>
<div><span>Gửi xe / tháng</span><strong><c:out value="${nn:money(lease.parking_fee)}"/></strong></div>
</div>
</section>
<c:if test="${not adminView and currentUser.id eq lease.account_id and (lease.status eq 'ACTIVE' or lease.status eq 'RESERVED')}"><section class="panel"><h2>Tiếp tục kỳ thuê</h2>
<p class="field-help">Kỳ gia hạn nối tiếp ngày kết thúc hiện tại và cần còn lịch trống.</p>
<a class="button primary full" href="${ctx}/dat-thue/${lease.unit_id}?lease=${lease.id}">Đăng ký gia hạn →</a></section>
<form class="panel" method="post" action="${ctx}/tai-khoan/yeu-cau"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><input type="hidden" name="lease_id" value="${lease.id}"><input type="hidden" name="type" value="${lease.status eq 'ACTIVE' ? 'CHECKOUT' : 'CANCEL'}"><h2>${lease.status eq 'ACTIVE' ? 'Yêu cầu trả phòng' : 'Yêu cầu hủy đặt thuê'}</h2>
<c:if test="${lease.status eq 'ACTIVE'}"><label>Ngày dự kiến trả phòng<input required type="date" name="requested_date" min="${today}" value="${today}"></label>
</c:if><label>Nội dung yêu cầu<textarea required name="note" rows="4" maxlength="3000"></textarea></label>
<button class="button outline full" type="submit">Gửi quản lý xem xét</button><p class="field-help">Quản lý kiểm tra công nợ, tình trạng căn hộ và khoản hoàn theo thỏa thuận trước khi kết thúc.</p>
</form>
</c:if><c:if test="${adminView and lease.status eq 'RESERVED'}"><form class="panel" method="post" action="${ctx}/admin/hop-dong/${lease.id}/ban-giao"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><h2>Ký & bàn giao căn hộ</h2>
<p class="field-help">Thực hiện vào ngày nhận phòng, sau khi khách đã ký hợp đồng giấy.</p>
<label>Chỉ số điện bàn giao<input required name="electricity" type="number" min="${unit.electricity_meter}" step="0.001" value="${unit.electricity_meter}"></label>
<label>Chỉ số nước bàn giao<input required name="water" type="number" min="${unit.water_meter}" step="0.001" value="${unit.water_meter}"></label>
<label class="check-label"><input required type="checkbox" name="signed"><span>Khách đã ký hợp đồng giấy và hai bên đã kiểm tra bàn giao.</span></label>
<button class="button primary full" type="submit">Xác nhận nhận phòng</button></form>
</c:if><section class="panel"><h3>Điều khoản hợp đồng</h3>
<details class="mail-details"><summary>Xem nội dung đã chấp nhận</summary><p class="description-text"><c:out value="${lease.terms_text}"/></p>
</details>
</section>
</aside>
</div>
</main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>