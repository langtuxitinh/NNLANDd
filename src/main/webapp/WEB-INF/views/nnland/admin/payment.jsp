<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><div class="action-row spread" style="margin-bottom:20px"><span class="badge ${payment.status}"><c:out value="${nn:label(payment.status)}"/></span><div class="action-row"><a class="button outline small" href="${ctx}/thanh-toan/${payment.id}/phieu-thu">Phiếu thu PDF</a><a class="button outline small" href="${ctx}/admin/thanh-toan">← Danh sách</a></div>
</div>
<div class="detail-grid"><div class="stack"><section class="panel"><div class="panel-header"><div><span class="eyebrow">ĐỐI CHIẾU THANH TOÁN</span><h2><c:out value="${payment.code}"/></h2>
<p><c:out value="${payment.unit_name}"/></p>
</div>
<span class="badge soft"><c:out value="${nn:label(payment.type)}"/></span></div>
<div class="info-grid"><div><span>Khách hàng</span><strong><c:out value="${payment.full_name}"/></strong></div>
<div><span>Liên hệ</span><strong><c:out value="${payment.email}"/><br><c:out value="${payment.phone}"/></strong></div>
<div><span>Cần thanh toán</span><strong><c:out value="${nn:money(payment.due_amount)}"/></strong></div>
<div><span>Thực nhận / Đã hoàn</span><strong><c:out value="${nn:money(payment.received)}"/> / <c:out value="${nn:money(payment.refunded)}"/></strong></div>
</div>
<c:if test="${not empty booking}"><div class="notice">Đơn <c:out value="${booking.code}"/> · <c:out value="${nn:label(booking.status)}"/><br><c:out value="${nn:date(booking.start_date)}"/> → <c:out value="${nn:date(booking.end_date)}"/> · ${booking.months} tháng<br>Tiền thuê: <c:out value="${nn:money(booking.rent_total)}"/> · Cọc: <c:out value="${nn:money(booking.deposit_total)}"/></div>
<c:if test="${not empty booking.lease_id}"><a class="button outline small" href="${ctx}/admin/hop-dong/${booking.lease_id}">Xem hợp đồng →</a></c:if></c:if><c:if test="${not empty payment.customer_note}"><h3>Thông tin khách báo chuyển khoản</h3>
<p class="description-text"><c:out value="${payment.customer_note}"/></p>
<small class="muted">Báo lần đầu: <c:out value="${nn:datetime(payment.claimed_at)}"/></small></c:if></section>
<section class="panel"><h2>Lịch sử thực nhận</h2>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Phiếu thu</th><th>Mã ngân hàng</th><th>Số tiền / Thời điểm</th><th>Người xác nhận</th></tr>
</thead>
<tbody><c:forEach var="r" items="${receipts}"><tr><td><c:out value="${r.code}"/></td><td><c:out value="${r.bank_reference}"/><small><c:out value="${r.note}"/></small></td><td><strong><c:out value="${nn:money(r.amount)}"/></strong><small><c:out value="${nn:datetime(r.received_at)}"/></small></td><td><c:out value="${r.confirmed_name}"/></td></tr>
</c:forEach><c:if test="${empty receipts}"><tr><td colspan="4" class="muted">Chưa ghi nhận tiền thực nhận.</td></tr>
</c:if></tbody>
</table>
</div>
</section>
<section class="panel"><div class="panel-header"><h2>Các khoản hoàn tiền</h2>
<a class="text-link" href="${ctx}/admin/hoan-tien">Xử lý chuyển hoàn →</a></div>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Khoản mục</th><th>Số tiền</th><th>Trạng thái</th><th>Lý do / Mã ngân hàng</th></tr>
</thead>
<tbody><c:forEach var="r" items="${refunds}"><tr><td><c:out value="${nn:label(r.category)}"/></td><td><c:out value="${nn:money(r.amount)}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><c:out value="${r.reason}"/><small><c:out value="${r.bank_reference}"/></small></td></tr>
</c:forEach><c:if test="${empty refunds}"><tr><td colspan="4" class="muted">Chưa có khoản hoàn tiền.</td></tr>
</c:if></tbody>
</table>
</div>
</section>
</div>
<aside class="stack"><form class="panel" method="post" action="${ctx}/admin/thanh-toan/${payment.id}/nhan-tien"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><h2>Ghi nhận tiền thực nhận</h2>
<p class="field-help">Chỉ lập phiếu thu sau khi kiểm tra tài khoản ngân hàng. Mỗi mã ngân hàng chỉ được ghi một lần trong toàn hệ thống.</p>
<label>Số tiền nhận được (đồng)<input required type="number" name="amount" min="1" max="100000000000" value="${payment.remaining gt 0 ? payment.remaining : ''}"></label>
<label>Mã giao dịch ngân hàng<input required name="bank_reference" maxlength="100" placeholder="Mã tham chiếu từ ngân hàng"></label>
<label>Thời điểm nhận<input type="datetime-local" name="received_at" value="${now}" step="1"></label>
<label>Ghi chú<textarea name="note" maxlength="1000" rows="3"></textarea></label>
<label class="check-label"><input required type="checkbox" name="verified"><span>Tôi đã đối chiếu tiền thực nhận và mã giao dịch trên tài khoản ngân hàng.</span></label>
<button class="button primary full" type="submit">Lưu phiếu thu</button></form>
<c:if test="${payment.status eq 'RECONCILE'}"><form class="panel" method="post" action="${ctx}/admin/thanh-toan/${payment.id}/doi-soat"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><h3>Xác nhận sau đối soát</h3>
<p class="field-help">Chỉ xác nhận được nếu đã đủ tiền, kỳ thuê còn hợp lệ và lịch căn hộ còn trống. Không thu tiền thêm ở thao tác này.</p>
<button class="button outline full" type="submit">Kiểm tra & xác nhận lại</button></form>
</c:if><form class="panel" method="post" action="${ctx}/admin/thanh-toan/${payment.id}/hoan-tien"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><h3>Lập khoản hoàn</h3>
<p class="field-help">Tiền thuê/cọc chỉ hoàn sau khi hủy hoặc quyết toán. Chuyển thừa được hệ thống lập khoản hoàn riêng.</p>
<label>Khoản mục<select name="category"><option value="OVERPAY">Tiền chuyển thừa</option><option value="RENT">Tiền thuê</option><option value="DEPOSIT">Tiền cọc</option></select></label>
<label>Số tiền hoàn<input required type="number" name="amount" min="1"></label>
<label>Lý do<textarea required name="reason" maxlength="1000" rows="3"></textarea></label>
<button class="button outline full" type="submit">Lập khoản hoàn chờ chuyển</button></form>
</aside>
</div>
</main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>