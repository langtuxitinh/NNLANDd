<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><main id="main-content"><section class="container booking-page"><div class="breadcrumb"><a href="${ctx}/tai-khoan/dat-thue">← Đơn đặt thuê của bạn</a><span>/</span><c:out value="${payment.code}"/></div>
<div class="payment-heading action-row spread"><div><span class="eyebrow">NNLAND PAYMENT</span><h1>Thông tin thanh toán</h1>
</div>
<span class="badge ${payment.status}"><c:out value="${nn:label(payment.status)}"/></span></div>
<div class="payment-layout"><div class="stack"><section class="panel"><div class="payment-amount"><span>Số tiền còn cần thanh toán</span><strong><c:out value="${nn:money(payment.remaining)}"/></strong><small>Mã thanh toán: <c:out value="${payment.code}"/></small></div>
<c:if test="${not empty deadline and canTransfer}"><div class="countdown"><span>Thời gian giữ chỗ còn lại</span><strong data-countdown="${deadline}">—</strong></div>
</c:if><c:choose><c:when test="${canTransfer and bankReady}"><div class="bank-layout"><img class="qr-image" src="${ctx}/thanh-toan/${payment.id}/qr" alt="Mã VietQR của tài khoản nhận tiền NNland"><div class="bank-details"><div><span>Ngân hàng</span><strong><c:out value="${meta.bank_name}"/></strong></div>
<div><span>Chủ tài khoản</span><strong><c:out value="${meta.bank_owner}"/></strong></div>
<div><span>Số tài khoản</span><strong><c:out value="${meta.bank_account}"/></strong><button class="copy-button" type="button" data-copy="<c:out value='${meta.bank_account}'/>">Sao chép</button></div>
<div><span>Nội dung chuyển khoản</span><strong><c:out value="${payment.code}"/></strong><button class="copy-button" type="button" data-copy="<c:out value='${payment.code}'/>">Sao chép</button></div>
</div>
</div>
<div class="notice">Kiểm tra tên chủ tài khoản, số tiền và nội dung chuyển khoản trên ứng dụng ngân hàng trước khi xác nhận.</div>
</c:when><c:when test="${canTransfer and not bankReady}"><div class="notice warning">Thông tin tài khoản nhận tiền đang được NNland cập nhật. Vui lòng liên hệ <strong><c:out value="${meta.hotline}"/></strong> trước khi chuyển khoản.</div>
<c:if test="${isAdmin}"><a class="button outline" href="${ctx}/admin/cai-dat">Thiết lập ngân hàng →</a></c:if></c:when><c:when test="${payment.status eq 'PAID'}"><div class="notice">✓ NNland đã xác nhận đủ tiền. Bạn có thể xem hợp đồng hoặc hóa đơn trong tài khoản.</div>
<a class="button primary" href="${ctx}/tai-khoan/luu-tru">Xem thông tin lưu trú →</a></c:when><c:otherwise><div class="notice warning">Yêu cầu hiện không ở bước chuyển tiền. Nếu bạn đã chuyển khoản hoặc cần hoàn tiền, hãy liên hệ quản lý để đối soát. Mọi khoản đã nhận và đã hoàn được liệt kê bên dưới.</div>
</c:otherwise></c:choose></section>
<c:if test="${payment.remaining gt 0 and currentUser.id eq payment.account_id and payment.status ne 'CANCELLED' and payment.status ne 'REFUNDED' and payment.status ne 'REFUND_PENDING'}"><form class="panel" method="post" action="${ctx}/thanh-toan/${payment.id}/bao-chuyen-khoan"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><h2>Bạn đã chuyển khoản?</h2>
<p class="field-help">Thông báo giúp quản lý tìm giao dịch. Nút này không tự xác nhận thanh toán.</p>
<label>Thông tin chuyển khoản<textarea required name="note" maxlength="1000" rows="3" placeholder="Tên người chuyển, số tiền, thời điểm và mã giao dịch ngân hàng..."><c:out value="${payment.customer_note}"/></textarea></label>
<button class="button primary" type="submit">Báo quản lý kiểm tra</button><c:if test="${not empty payment.claimed_at}"><p class="field-help">Đã báo lần đầu lúc <c:out value="${nn:datetime(payment.claimed_at)}"/>. Gửi thêm thông tin không gia hạn thời gian giữ chỗ.</p>
</c:if></form>
</c:if><section class="panel"><div class="panel-header"><div><h2>Giao dịch đã đối chiếu</h2>
<p>Chỉ hiển thị các khoản được quản lý xác nhận thực nhận.</p>
</div>
<a class="button outline small" href="${ctx}/thanh-toan/${payment.id}/phieu-thu">Tải phiếu thu PDF</a></div>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Phiếu thu</th><th>Thời điểm nhận</th><th>Mã ngân hàng</th><th>Số tiền</th></tr>
</thead>
<tbody><c:forEach var="r" items="${receipts}"><tr><td><c:out value="${r.code}"/></td><td><c:out value="${nn:datetime(r.received_at)}"/></td><td><c:out value="${r.bank_reference}"/></td><td class="money"><c:out value="${nn:money(r.amount)}"/></td></tr>
</c:forEach><c:if test="${empty receipts}"><tr><td colspan="4" class="muted">Chưa có giao dịch được xác nhận.</td></tr>
</c:if></tbody>
</table>
</div>
</section>
<c:if test="${not empty refunds}"><section class="panel"><h2>Thông tin hoàn tiền</h2>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Khoản hoàn</th><th>Số tiền</th><th>Trạng thái</th><th>Mã ngân hàng</th></tr>
</thead>
<tbody><c:forEach var="r" items="${refunds}"><tr><td><c:out value="${nn:label(r.category)}"/><small><c:out value="${r.reason}"/></small></td><td><c:out value="${nn:money(r.amount)}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><c:out value="${r.bank_reference}"/></td></tr>
</c:forEach></tbody>
</table>
</div>
</section>
</c:if></div>
<aside class="panel"><h2>Tóm tắt thanh toán</h2>
<div class="info-grid"><div><span>Người thuê</span><strong><c:out value="${payment.full_name}"/></strong></div>
<div><span>Khoản thu</span><strong><c:out value="${nn:label(payment.type)}"/></strong></div>
</div>
<p class="field-help"><c:out value="${payment.unit_name}"/></p>
<div class="summary-rows"><c:if test="${not empty payment.start_date}"><div><span>Kỳ thuê</span><strong><c:out value="${nn:date(payment.start_date)}"/><br>→ <c:out value="${nn:date(payment.end_date)}"/></strong></div>
</c:if><div><span>Tổng cần thanh toán</span><strong><c:out value="${nn:money(payment.due_amount)}"/></strong></div>
<div><span>Đã nhận</span><strong><c:out value="${nn:money(payment.received)}"/></strong></div>
<div><span>Đã chuyển hoàn</span><strong><c:out value="${nn:money(payment.refunded)}"/></strong></div>
<div class="total"><span>Còn thiếu</span><strong><c:out value="${nn:money(payment.remaining)}"/></strong></div>
</div>
<p class="field-help">Tiền thuê trả trước không được thu lại trong hóa đơn điện nước hàng tháng.</p>
<a class="button outline full" href="${ctx}/tai-khoan">Về tài khoản của bạn</a></aside>
</div>
</section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>