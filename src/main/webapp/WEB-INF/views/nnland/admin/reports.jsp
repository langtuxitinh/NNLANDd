<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><section class="panel" style="margin-bottom:20px"><form method="get" action="${ctx}/admin/bao-cao" class="filter-form"><label>Từ ngày<input required type="date" name="from" value="${report.from}"></label>
<label>Đến ngày<input required type="date" name="to" value="${report.through}"></label>
<label>Tòa nhà<select name="building"><option value="">Tất cả tòa được quản lý</option><c:forEach var="b" items="${staffBuildings}"><option value="${b.id}" ${selectedBuilding eq b.id ? 'selected' : ''}><c:out value="${b.name}"/></option></c:forEach></select></label>
<label>Căn hộ<select name="unit"><option value="">Tất cả căn hộ</option><c:forEach var="u" items="${reportUnits}"><option value="${u.id}" ${selectedUnit eq u.id ? 'selected' : ''}><c:out value="${u.building_name}"/> · <c:out value="${u.code}"/></option></c:forEach></select></label>
<label>Loại thanh toán<select name="type"><option value="">Tất cả khoản thu</option><option value="RENT" ${selectedType eq 'RENT' ? 'selected' : ''}>Đặt thuê ban đầu</option><option value="EXTENSION" ${selectedType eq 'EXTENSION' ? 'selected' : ''}>Gia hạn</option><option value="UTILITY" ${selectedType eq 'UTILITY' ? 'selected' : ''}>Điện nước & dịch vụ</option></select></label>
<button class="button primary" type="submit">Xem báo cáo</button></form>
<c:url var="excelUrl" value="/admin/bao-cao/xuat"><c:param name="from" value="${report.from}"/><c:param name="to" value="${report.through}"/><c:param name="building" value="${selectedBuilding}"/><c:param name="unit" value="${selectedUnit}"/><c:param name="type" value="${selectedType}"/><c:param name="format" value="xlsx"/></c:url><c:url var="pdfUrl" value="/admin/bao-cao/xuat"><c:param name="from" value="${report.from}"/><c:param name="to" value="${report.through}"/><c:param name="building" value="${selectedBuilding}"/><c:param name="unit" value="${selectedUnit}"/><c:param name="type" value="${selectedType}"/><c:param name="format" value="pdf"/></c:url><div class="action-row"><a class="button outline small" href="${excelUrl}">Tải Excel (.xlsx)</a><a class="button outline small" href="${pdfUrl}">Tải báo cáo PDF</a></div>
</section>
<div class="stats-grid"><article class="kpi accent"><span class="kpi-label">Tổng thực nhận</span><strong><c:out value="${nn:money(report.gross)}"/></strong><small>Từ phiếu thu đã đối chiếu.</small></article>
<article class="kpi"><span class="kpi-label">Đã chuyển hoàn</span><strong><c:out value="${nn:money(report.refunded)}"/></strong><small>Chỉ khoản hoàn đã chuyển thực tế.</small></article>
<article class="kpi"><span class="kpi-label">Dòng tiền ròng</span><strong><c:out value="${nn:money(report.net)}"/></strong><small>Thực nhận trừ khoản đã hoàn.</small></article>
<article class="kpi"><span class="kpi-label">Doanh thu phân bổ</span><strong><c:out value="${nn:money(report.earned_total)}"/></strong><small>Tiền thuê theo kỳ, phí và bồi thường.</small></article>
</div>
<section class="panel income-panel"><div class="panel-header"><div><h2>Cơ cấu doanh thu theo khoản mục</h2>
<p>Trong khoảng ngày và phạm vi đang lọc. Không bao gồm tiền cọc giữ hộ hay chuyển thừa.</p>
</div>
</div>
<div class="income-layout"><div class="income-ring"><svg viewBox="0 0 42 42" role="img" aria-label="Cơ cấu doanh thu: tiền thuê, điện, nước, dịch vụ, gửi xe và khấu trừ cọc"><circle cx="21" cy="21" r="15.9155" fill="none" stroke="#edf1ec" stroke-width="5"/><c:forEach var="part" items="${report.income_parts}"><circle cx="21" cy="21" r="15.9155" fill="none" stroke="${part.color}" stroke-width="5" stroke-dasharray="${part.percent} 100" stroke-dashoffset="${part.offset}" transform="rotate(-90 21 21)"/></c:forEach></svg><div><small>DOANH THU</small><strong><c:out value="${nn:money(report.earned_total)}"/></strong></div>
</div>
<div class="income-legend"><c:forEach var="part" items="${report.income_parts}"><div><span><i style="background:${part.color}"></i><c:out value="${part.name}"/></span><strong><c:out value="${nn:money(part.amount)}"/></strong></div>
</c:forEach><div><span>Điều chỉnh hóa đơn (+/−)</span><strong><c:out value="${nn:money(report.adjustment)}"/></strong></div>
</div>
</div>
<p class="field-help">Biểu đồ dùng các khoản dương; điều chỉnh hóa đơn hiển thị riêng và đã được tính vào tổng. Doanh thu chỉ phân bổ đến ngày hiện tại.</p>
</section>
<div class="dashboard-grid"><section class="panel"><div class="panel-header"><div><h2>Xu hướng 12 tháng</h2>
<p>Theo căn hộ, loại thanh toán và tòa nhà đang lọc.</p>
</div>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/chart.jsp"/></section>
<section class="panel"><h2>Cơ cấu dòng tiền ròng</h2>
<div class="summary-rows"><div><span>Tiền thuê</span><strong><c:out value="${nn:money(report.rent_cash)}"/></strong></div>
<div><span>Điện nước & dịch vụ</span><strong><c:out value="${nn:money(report.utility_cash)}"/></strong></div>
<div><span>Tiền cọc</span><strong><c:out value="${nn:money(report.deposit_cash)}"/></strong></div>
<div><span>Chuyển thừa chưa hoàn</span><strong><c:out value="${nn:money(report.overpay_cash)}"/></strong></div>
</div>
<p class="field-help">Tiền cọc và chuyển thừa được tách riêng, không cộng vào doanh thu phân bổ.</p>
<h3 style="margin-top:24px">So sánh tòa nhà</h3>
<c:forEach var="entry" items="${report.building_comparison}"><div class="report-building-row"><span><c:out value="${entry.name}"/></span><strong><c:out value="${nn:money(entry.amount)}"/></strong></div>
<div class="building-bar ${entry.negative ? 'negative' : ''}"><span style="width:${entry.width}%"></span></div>
</c:forEach><c:if test="${empty report.building_cash}"><p class="field-help">Chưa phát sinh dòng tiền trong khoảng ngày này.</p>
</c:if></section>
</div>
<section class="panel"><div class="panel-header"><div><h2>Chi tiết giao dịch thực nhận</h2>
<p>Mỗi giao dịch ngân hàng chỉ được thống kê một lần.</p>
</div>
</div>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Ngày nhận</th><th>Phiếu thu / Thanh toán</th><th>Khách hàng</th><th>Tòa nhà / Căn</th><th>Mã ngân hàng</th><th>Thực nhận</th></tr>
</thead>
<tbody><c:forEach var="r" items="${report.receipts}"><tr><td><c:out value="${nn:datetime(r.received_at)}"/></td><td><c:out value="${r.code}"/><small><a href="${ctx}/admin/thanh-toan/${r.payment_id}"><c:out value="${r.payment_code}"/></a></small></td><td><c:out value="${r.full_name}"/></td><td><c:out value="${r.building_name}"/><small><c:out value="${r.unit_code}"/></small></td><td><c:out value="${r.bank_reference}"/></td><td class="money"><c:out value="${nn:money(r.amount)}"/></td></tr>
</c:forEach><c:if test="${empty report.receipts}"><tr><td colspan="6" class="muted">Chưa có phiếu thu trong khoảng ngày được chọn.</td></tr>
</c:if></tbody>
</table>
</div>
<details style="margin-top:20px"><summary class="text-link">Cách tính các chỉ số</summary><p class="field-help">Dòng tiền ròng = thực nhận − hoàn đã chuyển. Tiền thuê được phân bổ theo từng kỳ tháng của hợp đồng và phần ngày nằm trong khoảng báo cáo, sau khi trừ khoản hoàn tiền thuê đã chuyển. Điện nước và dịch vụ đã thanh toán ghi nhận theo ngày chốt hóa đơn; khấu trừ cọc theo ngày bàn giao. Đây là báo cáo quản trị, không thay thế sổ kế toán theo chuẩn áp dụng của đơn vị.</p>
</details>
</section>
<section class="panel"><h2>Bảng số liệu 12 tháng</h2>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Tháng</th><th>Thực nhận</th><th>Đã hoàn</th><th>Dòng tiền ròng</th><th>Doanh thu phân bổ</th></tr>
</thead>
<tbody><c:forEach var="r" items="${chart}"><tr><td>${r.month_label}</td><td><c:out value="${nn:money(r.gross)}"/></td><td><c:out value="${nn:money(r.refunded)}"/></td><td><c:out value="${nn:money(r.net)}"/></td><td><c:out value="${nn:money(r.earned_total)}"/></td></tr>
</c:forEach></tbody>
</table>
</div>
</section>
</main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>