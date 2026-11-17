<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><div class="action-row spread" style="margin-bottom:20px"><span class="badge ${invoice.status}"><c:out value="${nn:label(invoice.status)}"/></span><div class="action-row"><a class="button outline small" href="${ctx}/hoa-don/${invoice.id}/pdf">Tải hóa đơn PDF</a><a class="button outline small" href="${ctx}/${adminView ? 'admin/hoa-don' : 'tai-khoan/hoa-don'}">← Danh sách hóa đơn</a></div>
</div>
<section class="panel"><div class="panel-header border"><div><span class="eyebrow">NNLAND RESIDENT SERVICES</span><h2>Điện nước & phí sinh hoạt</h2>
<p><c:out value="${invoice.code}"/> · Kỳ <c:out value="${invoice.period}"/></p>
</div>
<div><strong><c:out value="${invoice.full_name}"/></strong><p class="field-help"><c:out value="${invoice.unit_code}"/> · <c:out value="${invoice.building_name}"/></p>
</div>
</div>
<div class="info-grid"><div><span>Ngày chốt chỉ số</span><strong><c:out value="${nn:date(invoice.reading_date)}"/></strong></div>
<div><span>Hạn thanh toán</span><strong><c:out value="${nn:date(invoice.due_date)}"/></strong></div>
<div><span>Hợp đồng</span><strong><c:out value="${invoice.lease_code}"/></strong></div>
<div><span>Liên hệ</span><strong><c:out value="${invoice.phone}"/></strong></div>
</div>
<div class="table-wrap" style="margin-top:25px"><table class="data-table"><thead><tr><th>Khoản mục</th><th>Chỉ số đầu</th><th>Chỉ số cuối</th><th>Sử dụng / Đơn giá</th><th>Thành tiền</th></tr>
</thead>
<tbody><tr><td><strong>Điện</strong></td><td>${invoice.electricity_start}</td><td>${invoice.electricity_end}</td><td>${invoice.electricity_end-invoice.electricity_start} kWh<small><c:out value="${nn:money(invoice.electricity_rate)}"/> / kWh</small></td><td class="money"><c:out value="${nn:money(invoice.electricity_amount)}"/></td></tr>
<tr><td><strong>Nước</strong></td><td>${invoice.water_start}</td><td>${invoice.water_end}</td><td>${invoice.water_end-invoice.water_start} m³<small><c:out value="${nn:money(invoice.water_rate)}"/> / m³</small></td><td class="money"><c:out value="${nn:money(invoice.water_amount)}"/></td></tr>
<tr><td>Phí dịch vụ</td><td colspan="3">Theo kỳ hóa đơn</td><td><c:out value="${nn:money(invoice.service_fee)}"/></td></tr>
<tr><td>Phí gửi xe</td><td colspan="3">Theo kỳ hóa đơn</td><td><c:out value="${nn:money(invoice.parking_fee)}"/></td></tr>
<tr><td>Điều chỉnh</td><td colspan="3"><c:out value="${invoice.note}"/></td><td><c:out value="${nn:money(invoice.adjustment)}"/></td></tr>
</tbody>
</table>
</div>
<div class="invoice-total"><span>Tổng cộng</span><strong><c:out value="${nn:money(invoice.total)}"/></strong><p class="field-help">Đã nhận: <c:out value="${nn:money(invoice.received)}"/></p>
</div>
<div class="notice">Hóa đơn này không thu lại tiền thuê căn hộ đã được thanh toán trước.</div>
<a class="button primary" href="${ctx}/${adminView ? 'admin/thanh-toan' : 'thanh-toan'}/${invoice.payment_id}">${adminView ? 'Đối chiếu thanh toán' : invoice.status eq 'PAID' ? 'Xem phiếu thu' : 'Thanh toán hóa đơn'} →</a></section>
<c:if test="${adminView and canCorrect}"><details class="panel" style="margin-top:20px"><summary class="text-link">Điều chỉnh hóa đơn chưa có tiền thực nhận</summary><form method="post" action="${ctx}/admin/hoa-don/${invoice.id}/dieu-chinh"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><p class="field-help">Chỉ sửa hóa đơn mới nhất khi chưa thu tiền. Chỉ số đầu và đơn giá đã chốt được giữ nguyên.</p>
<div class="form-grid three"><label>Điện cuối<input required type="number" step="0.001" name="electricity_end" value="${invoice.electricity_end}"></label>
<label>Nước cuối<input required type="number" step="0.001" name="water_end" value="${invoice.water_end}"></label>
<label>Hạn thanh toán<input required type="date" name="due_date" value="${invoice.due_date}"></label>
<label>Phí dịch vụ<input required type="number" min="0" name="service_fee" value="${invoice.service_fee}"></label>
<label>Phí gửi xe<input required type="number" min="0" name="parking_fee" value="${invoice.parking_fee}"></label>
<label>Điều chỉnh<input required type="number" name="adjustment" value="${invoice.adjustment}"></label>
</div>
<label>Diễn giải<textarea name="note" maxlength="2000"><c:out value="${invoice.note}"/></textarea></label>
<label>Lý do sửa hóa đơn<textarea required name="correction_reason" maxlength="1000"></textarea></label>
<button class="button outline" type="submit">Lưu hóa đơn điều chỉnh</button></form>
</details>
</c:if></main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>