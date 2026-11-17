<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><div class="year-summary"><span>Năm ${today.year} · đến hôm nay</span><strong>Thực nhận: <c:out value="${nn:money(yearReport.gross)}"/></strong><strong>Doanh thu phân bổ: <c:out value="${nn:money(yearReport.earned_total)}"/></strong><a href="${ctx}/admin/bao-cao?from=${today.year}-01-01&amp;to=${today}">Xem chi tiết năm →</a></div>
<c:if test="${isAdmin and empty meta.bank_account}"><div class="notice warning">Chưa thiết lập tài khoản nhận tiền. <a href="${ctx}/admin/cai-dat">Cập nhật ngân hàng</a> để khách hàng có thể xem QR và thông tin chuyển khoản.</div>
</c:if><div class="stats-grid"><article class="kpi accent"><span class="kpi-label">Thực nhận tháng này</span><svg class="icon kpi-icon"><use href="${ctx}/nnland/icons.svg#wallet"/></svg><strong><c:out value="${nn:money(report.gross)}"/></strong><small>Tiền đã được đối chiếu tại ngân hàng.</small></article>
<article class="kpi"><span class="kpi-label">Doanh thu phân bổ</span><svg class="icon kpi-icon"><use href="${ctx}/nnland/icons.svg#chart"/></svg><strong><c:out value="${nn:money(report.earned_total)}"/></strong><small>Theo kỳ thuê và phí đã thanh toán.</small></article>
<article class="kpi"><span class="kpi-label">Hợp đồng đang lưu trú</span><svg class="icon kpi-icon"><use href="${ctx}/nnland/icons.svg#key"/></svg><strong>${activeCount}</strong><small>Trên ${unitCount} căn hộ được quản lý.</small></article>
<article class="kpi"><span class="kpi-label">Thanh toán cần kiểm tra</span><svg class="icon kpi-icon"><use href="${ctx}/nnland/icons.svg#file"/></svg><strong>${reviewCount}</strong><small>Khách báo chuyển, nhận thiếu hoặc cần đối soát.</small></article>
</div>
<div class="dashboard-grid"><section class="panel"><div class="panel-header"><div><h2>Dòng tiền & nhịp vận hành</h2>
<p>12 tháng gần nhất · tiền cọc không tính vào doanh thu.</p>
</div>
<a class="text-link" href="${ctx}/admin/bao-cao">Báo cáo →</a></div>
<jsp:include page="/WEB-INF/views/nnland/common/chart.jsp"/></section>
<section class="panel"><div class="panel-header"><h2>Việc cần quan tâm</h2>
</div>
<div class="quick-actions"><a href="${ctx}/admin/thanh-toan"><svg class="icon"><use href="${ctx}/nnland/icons.svg#wallet"/></svg>Đối chiếu tiền nhận<span>→</span></a><a href="${ctx}/admin/hop-dong?status=RESERVED"><svg class="icon"><use href="${ctx}/nnland/icons.svg#key"/></svg>Hợp đồng chờ bàn giao<span>→</span></a><a href="${ctx}/admin/ho-tro"><svg class="icon"><use href="${ctx}/nnland/icons.svg#ticket"/></svg>${ticketsCount} yêu cầu hỗ trợ đang mở<span>→</span></a><a href="${ctx}/admin/yeu-cau?status=OPEN"><svg class="icon"><use href="${ctx}/nnland/icons.svg#logout"/></svg>Xử lý hủy & trả phòng<span>→</span></a></div>
</section>
</div>
<section class="panel"><div class="panel-header"><div><h2>Thanh toán gần đây</h2>
<p>Đơn thuê và hóa đơn được theo dõi cùng giao dịch thực tế.</p>
</div>
<a class="text-link" href="${ctx}/admin/thanh-toan">Xem tất cả →</a></div>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Mã thanh toán</th><th>Khách hàng</th><th>Căn hộ</th><th>Cần thu</th><th>Đã nhận</th><th>Tình trạng</th></tr>
</thead>
<tbody><c:forEach var="r" items="${recent}" end="5"><tr><td><a href="${ctx}/admin/thanh-toan/${r.id}"><c:out value="${r.code}"/></a></td><td><c:out value="${r.full_name}"/></td><td><c:out value="${r.unit_code}"/><small><c:out value="${r.building_name}"/></small></td><td><c:out value="${nn:money(r.due_amount)}"/></td><td><c:out value="${nn:money(r.received)}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td></tr>
</c:forEach><c:if test="${empty recent}"><tr><td colspan="6" class="muted">Chưa có yêu cầu thanh toán. Đơn mới từ khách hàng sẽ xuất hiện tại đây.</td></tr>
</c:if></tbody>
</table>
</div>
</section>
</main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>