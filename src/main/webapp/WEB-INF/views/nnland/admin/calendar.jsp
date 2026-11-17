<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><section class="panel"><div class="panel-header"><div><h2><c:out value="${unit.name}"/></h2>
<p>Mã căn <c:out value="${unit.code}"/> · Lịch thuê và giữ chỗ được quản lý riêng với trạng thái vận hành.</p>
</div>
<a class="button outline small" href="${ctx}/admin/can-ho">← Danh sách</a></div>
<span class="badge ${unit.operational_status}"><c:out value="${nn:label(unit.operational_status)}"/></span><div class="notice">Kỳ thuê sử dụng khoảng ngày [ngày nhận, ngày trả). Hợp đồng quá hạn nhưng chưa trả phòng vẫn cần được quyết toán trước khi bàn giao khách mới.</div>
<h3>Hợp đồng</h3>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Mã hợp đồng</th><th>Khách thuê</th><th>Bắt đầu</th><th>Kết thúc</th><th>Trạng thái</th></tr>
</thead>
<tbody><c:forEach var="r" items="${leases}"><tr><td><a href="${ctx}/admin/hop-dong/${r.id}"><c:out value="${r.code}"/></a></td><td><c:out value="${r.full_name}"/></td><td><c:out value="${nn:date(r.start_date)}"/></td><td><c:out value="${nn:date(r.end_date)}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td></tr>
</c:forEach><c:if test="${empty leases}"><tr><td colspan="5" class="muted">Chưa có hợp đồng.</td></tr>
</c:if></tbody>
</table>
</div>
<h3 style="margin-top:25px">Đơn giữ chỗ đang theo dõi</h3>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Đơn thuê</th><th>Khách</th><th>Kỳ thuê</th><th>Hạn giữ chỗ</th><th>Trạng thái</th></tr>
</thead>
<tbody><c:forEach var="r" items="${holds}"><tr><td><c:out value="${r.code}"/></td><td><c:out value="${r.full_name}"/></td><td><c:out value="${nn:date(r.start_date)}"/> → <c:out value="${nn:date(r.end_date)}"/></td><td><c:out value="${nn:datetime(r.hold_until)}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td></tr>
</c:forEach><c:if test="${empty holds}"><tr><td colspan="5" class="muted">Không có đơn giữ chỗ.</td></tr>
</c:if></tbody>
</table>
</div>
</section>
</main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>