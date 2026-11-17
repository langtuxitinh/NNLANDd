<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><section class="panel"><div class="panel-header"><div><h2><c:out value="${pageTitle}"/></h2>
<p>${result.total} bản ghi · chỉ hiển thị dữ liệu thuộc phạm vi được phân công.</p>
</div>
<div class="action-row"><c:if test="${section eq 'toa-nha' and isAdmin}"><a class="button primary small" href="${ctx}/admin/toa-nha/moi">+ Thêm tòa nhà</a></c:if><c:if test="${section eq 'can-ho'}"><a class="button primary small" href="${ctx}/admin/can-ho/moi">+ Thêm căn hộ</a></c:if><c:if test="${section eq 'hoa-don'}"><a class="button primary small" href="${ctx}/admin/hoa-don/moi">+ Lập hóa đơn</a></c:if></div>
</div>
<c:if test="${section eq 'thu-di' and not mailEnabled}"><div class="notice warning">Gửi email đang tắt. Thư được lưu trong hàng đợi và chưa được gửi ra ngoài. Thiết lập SMTP theo hướng dẫn trong dự án rồi bật MAIL_ENABLED để gửi thật.</div>
</c:if><form class="filter-form" method="get" action="${ctx}/admin/${section}"><label>Tìm kiếm<input name="q" maxlength="100" value="<c:out value='${filters.q}'/>" placeholder="Mã, tên hoặc thông tin liên quan..."></label>
<c:choose><c:when test="${section eq 'can-ho'}"><label>Trạng thái<select name="status"><option value="">Tất cả</option><c:forTokens var="state" items="READY,MAINTENANCE,INACTIVE" delims=","><option value="${state}" ${filters.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
</c:when><c:when test="${section eq 'don-thue'}"><label>Trạng thái<select name="status"><option value="">Tất cả</option><c:forTokens var="state" items="HOLD,PAYMENT_REVIEW,CONFIRMED,EXPIRED,CANCELLED" delims=","><option value="${state}" ${filters.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
</c:when><c:when test="${section eq 'thanh-toan'}"><label>Trạng thái<select name="status"><option value="">Tất cả</option><c:forTokens var="state" items="PENDING,PARTIAL,PAID,RECONCILE,EXPIRED,CANCELLED,REFUND_PENDING,REFUNDED" delims=","><option value="${state}" ${filters.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
</c:when><c:when test="${section eq 'hop-dong'}"><label>Trạng thái<select name="status"><option value="">Tất cả</option><c:forTokens var="state" items="RESERVED,ACTIVE,ENDING,CLOSED,CANCELLED" delims=","><option value="${state}" ${filters.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
</c:when><c:when test="${section eq 'hoa-don'}"><label>Trạng thái<select name="status"><option value="">Tất cả</option><c:forTokens var="state" items="UNPAID,PAID" delims=","><option value="${state}" ${filters.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
</c:when><c:when test="${section eq 'ho-tro'}"><label>Trạng thái<select name="status"><option value="">Tất cả</option><c:forTokens var="state" items="OPEN,IN_PROGRESS,WAITING,RESOLVED,CLOSED" delims=","><option value="${state}" ${filters.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
</c:when><c:when test="${section eq 'lich-hen'}"><label>Trạng thái<select name="status"><option value="">Tất cả</option><c:forTokens var="state" items="NEW,CONFIRMED,COMPLETED,CANCELLED" delims=","><option value="${state}" ${filters.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
</c:when><c:when test="${section eq 'yeu-cau'}"><label>Trạng thái<select name="status"><option value="">Tất cả</option><c:forTokens var="state" items="OPEN,APPROVED,REJECTED" delims=","><option value="${state}" ${filters.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
</c:when><c:when test="${section eq 'hoan-tien'}"><label>Trạng thái<select name="status"><option value="">Tất cả</option><c:forTokens var="state" items="PENDING,COMPLETED" delims=","><option value="${state}" ${filters.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
</c:when><c:when test="${section eq 'tai-khoan'}"><label>Vai trò<select name="status"><option value="">Tất cả</option><c:forTokens var="state" items="USER,MANAGER,ADMIN" delims=","><option value="${state}" ${filters.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
</c:when><c:when test="${section eq 'thu-di'}"><label>Trạng thái<select name="status"><option value="">Tất cả</option><c:forTokens var="state" items="PENDING,SENDING,SENT,FAILED" delims=","><option value="${state}" ${filters.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
</c:when></c:choose><c:if test="${section ne 'tai-khoan' and section ne 'thu-di' and section ne 'nhat-ky'}"><label>Tòa nhà<select name="building"><option value="">Tất cả tòa được quản lý</option><c:forEach var="b" items="${staffBuildings}"><option value="${b.id}" ${filters.building eq b.id ? 'selected' : ''}><c:out value="${b.name}"/></option></c:forEach></select></label>
</c:if><button class="button primary" type="submit">Lọc dữ liệu</button><a class="button outline" href="${ctx}/admin/${section}">Xóa lọc</a></form>
<c:choose><c:when test="${section eq 'toa-nha'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Tòa nhà</th><th>Vị trí</th><th>Số căn</th><th>Hiển thị</th><th>Thao tác</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><strong><c:out value="${r.name}"/></strong><small><c:out value="${r.code}"/></small></td><td><c:out value="${r.city}"/><small><c:out value="${r.district}"/> · <c:out value="${r.address}"/></small></td><td><c:out value="${r.unit_count}"/></td><td><span class="badge ${r.published ? 'READY' : 'INACTIVE'}">${r.published ? 'Đang hiển thị' : 'Đã ẩn'}</span></td><td><a class="button outline small" href="${ctx}/admin/toa-nha/${r.id}/sua">Chỉnh sửa</a></td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="5" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when><c:when test="${section eq 'can-ho'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Mã / Căn hộ</th><th>Tòa nhà</th><th>Giá thuê / Cọc</th><th>Thông số</th><th>Vận hành</th><th>Thao tác</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><strong><c:out value="${r.code}"/></strong><small><c:out value="${r.name}"/></small></td><td><c:out value="${r.building_name}"/><small><c:out value="${r.city}"/></small></td><td><c:out value="${nn:money(r.monthly_rent)}"/><small>Cọc: <c:out value="${nn:money(r.deposit_amount)}"/></small></td><td><c:out value="${r.bedrooms}"/> PN · <c:out value="${r.area}"/> m²<small>Tầng <c:out value="${r.floor_no}"/></small></td><td><span class="badge ${r.operational_status}"><c:out value="${nn:label(r.operational_status)}"/></span><small>${r.published ? 'Đang hiển thị' : 'Đã ẩn'}</small></td><td><div class="action-row"><a class="button outline small" href="${ctx}/admin/can-ho/${r.id}/sua">Sửa</a><a class="button outline small" href="${ctx}/admin/can-ho/${r.id}/lich">Lịch thuê</a></div>
</td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="6" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when><c:when test="${section eq 'don-thue'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Đơn / Khách hàng</th><th>Căn hộ</th><th>Kỳ thuê</th><th>Tiền thuê & cọc</th><th>Tình trạng</th><th>Thao tác</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><strong><c:out value="${r.code}"/></strong><small><c:out value="${r.full_name}"/></small></td><td><c:out value="${r.unit_code}"/><small><c:out value="${r.building_name}"/></small></td><td><c:out value="${nn:date(r.start_date)}"/><br>→ <c:out value="${nn:date(r.end_date)}"/><small><c:out value="${r.months}"/> tháng</small></td><td><c:out value="${nn:money(r.rent_total+r.deposit_total)}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span><small><c:out value="${nn:label(r.payment_status)}"/></small></td><td><a class="button outline small" href="${ctx}/admin/thanh-toan/${r.payment_id}">Đối soát</a></td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="6" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when><c:when test="${section eq 'thanh-toan'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Thanh toán / Khách</th><th>Khoản thu</th><th>Căn hộ</th><th>Cần thu / Đã nhận</th><th>Tình trạng</th><th>Thao tác</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><strong><c:out value="${r.code}"/></strong><small><c:out value="${r.full_name}"/></small></td><td><c:out value="${nn:label(r.type)}"/></td><td><c:out value="${r.unit_code}"/><small><c:out value="${r.building_name}"/></small></td><td><c:out value="${nn:money(r.due_amount)}"/><small>Đã nhận: <c:out value="${nn:money(r.received)}"/></small></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span><small>Báo chuyển: <c:out value="${nn:datetime(r.claimed_at)}"/></small></td><td><a class="button outline small" href="${ctx}/admin/thanh-toan/${r.id}">Xem & đối soát</a></td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="6" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when><c:when test="${section eq 'hop-dong'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Hợp đồng / Khách</th><th>Căn hộ</th><th>Kỳ thuê</th><th>Giá thuê / tháng</th><th>Tình trạng</th><th>Thao tác</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><strong><c:out value="${r.code}"/></strong><small><c:out value="${r.full_name}"/></small></td><td><c:out value="${r.unit_code}"/><small><c:out value="${r.building_name}"/></small></td><td><c:out value="${nn:date(r.start_date)}"/><br>→ <c:out value="${nn:date(r.end_date)}"/></td><td><c:out value="${nn:money(r.monthly_rent)}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><a class="button outline small" href="${ctx}/admin/hop-dong/${r.id}">Chi tiết & bàn giao</a></td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="6" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when><c:when test="${section eq 'hoa-don'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Kỳ / Hóa đơn</th><th>Khách hàng</th><th>Căn hộ</th><th>Ngày chốt / Hạn trả</th><th>Tổng cộng</th><th>Thanh toán</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><strong><c:out value="${r.period}"/></strong><small><c:out value="${r.code}"/></small></td><td><c:out value="${r.full_name}"/></td><td><c:out value="${r.unit_code}"/><small><c:out value="${r.building_name}"/></small></td><td><c:out value="${nn:date(r.reading_date)}"/><small>Hạn: <c:out value="${nn:date(r.due_date)}"/></small></td><td><c:out value="${nn:money(r.total)}"/><br><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><a class="button outline small" href="${ctx}/admin/hoa-don/${r.id}">Chi tiết</a></td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="6" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when><c:when test="${section eq 'ho-tro'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Yêu cầu</th><th>Khách / Căn hộ</th><th>Mức ưu tiên</th><th>Tiến độ</th><th>Thời điểm</th><th>Thao tác</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><strong><c:out value="${r.subject}"/></strong><small><c:out value="${r.code}"/></small></td><td><c:out value="${r.full_name}"/><small><c:out value="${r.unit_code}"/> · <c:out value="${r.building_name}"/></small></td><td><c:out value="${nn:label(r.priority)}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><c:out value="${nn:datetime(r.created_at)}"/></td><td><a class="button outline small" href="${ctx}/admin/ho-tro/${r.id}">Xử lý</a></td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="6" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when><c:when test="${section eq 'lich-hen'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Người liên hệ</th><th>Căn hộ</th><th>Lịch mong muốn</th><th>Nội dung</th><th>Trạng thái</th><th>Thao tác</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><strong><c:out value="${r.full_name}"/></strong><small><c:out value="${r.email}"/><br><c:out value="${r.phone}"/></small></td><td><c:out value="${r.unit_name}"/><small><c:out value="${r.building_name}"/></small></td><td><c:out value="${nn:date(r.preferred_date)}"/><small><c:out value="${r.preferred_time}"/></small></td><td><c:out value="${fn:substring(r.message,0,90)}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><a class="button outline small" href="${ctx}/admin/lich-hen/${r.id}">Phản hồi</a></td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="6" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when><c:when test="${section eq 'yeu-cau'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Loại yêu cầu</th><th>Khách / Căn hộ</th><th>Ngày đề nghị</th><th>Tình trạng</th><th>Ngày gửi</th><th>Thao tác</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><c:out value="${nn:label(r.type)}"/></td><td><strong><c:out value="${r.full_name}"/></strong><small><c:out value="${r.unit_code}"/> · <c:out value="${r.building_name}"/></small></td><td><c:out value="${nn:date(r.requested_date)}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><c:out value="${nn:datetime(r.created_at)}"/></td><td><a class="button outline small" href="${ctx}/admin/yeu-cau/${r.id}">Kiểm tra & xử lý</a></td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="6" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when><c:when test="${section eq 'hoan-tien'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Thanh toán / Khách</th><th>Khoản hoàn</th><th>Số tiền</th><th>Tình trạng</th><th>Xác nhận chuyển hoàn</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><a href="${ctx}/admin/thanh-toan/${r.payment_id}"><c:out value="${r.payment_code}"/></a><small><c:out value="${r.full_name}"/> · <c:out value="${r.unit_code}"/></small></td><td><c:out value="${nn:label(r.category)}"/><small><c:out value="${r.reason}"/></small></td><td><c:out value="${nn:money(r.amount)}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><c:choose><c:when test="${r.status eq 'PENDING'}"><form action="${ctx}/admin/hoan-tien/${r.id}/xac-nhan" method="post"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><label>Mã giao dịch hoàn<input required name="bank_reference" maxlength="100"></label>
<label class="check-label"><input required name="verified" type="checkbox"><span>Đã chuyển tiền hoàn thực tế.</span></label>
<button class="button primary small" type="submit">Xác nhận</button></form>
</c:when><c:otherwise><c:out value="${r.bank_reference}"/><small><c:out value="${nn:datetime(r.processed_at)}"/></small></c:otherwise></c:choose></td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="5" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when><c:when test="${section eq 'tai-khoan'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Tài khoản</th><th>Liên hệ</th><th>Vai trò</th><th>Trạng thái</th><th>Cập nhật quyền</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><strong><c:out value="${r.full_name}"/></strong><small><c:out value="${r.username}"/></small></td><td><c:out value="${r.email}"/><small><c:out value="${r.phone}"/></small></td><td><span class="badge ${r.role}"><c:out value="${nn:label(r.role)}"/></span></td><td><span class="badge ${r.active ? 'READY' : 'INACTIVE'}">${r.active ? 'Đang hoạt động' : 'Đã khóa'}</span></td><td><form class="row-form" action="${ctx}/admin/tai-khoan/${r.id}" method="post"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><select name="role" aria-label="Vai trò"><option value="USER" ${r.role eq 'USER' ? 'selected' : ''}>Khách hàng</option><option value="MANAGER" ${r.role eq 'MANAGER' ? 'selected' : ''}>Quản lý</option><option value="ADMIN" ${r.role eq 'ADMIN' ? 'selected' : ''}>Quản trị viên</option></select><label><input type="checkbox" name="active" ${r.active ? 'checked' : ''}>Mở</label>
<button class="button outline small" type="submit">Lưu</button></form>
</td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="5" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when><c:when test="${section eq 'thu-di'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Người nhận</th><th>Nội dung</th><th>Tình trạng</th><th>Lần thử</th><th>Thời điểm</th><th>Thao tác</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><c:out value="${r.recipient}"/></td><td><details class="mail-details"><summary><c:out value="${r.subject}"/></summary><pre class="mail-body"><c:out value="${r.body}"/></pre><c:if test="${not empty r.last_error}"><p class="field-help"><c:out value="${r.last_error}"/></p>
</c:if></details>
</td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><c:out value="${r.attempts}"/></td><td><c:out value="${nn:datetime(r.created_at)}"/><small>Gửi: <c:out value="${nn:datetime(r.sent_at)}"/></small></td><td><c:if test="${r.status eq 'FAILED'}"><form action="${ctx}/admin/thu-di/${r.id}/thu-lai" method="post"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><button class="button outline small" type="submit">Thử lại</button></form>
</c:if></td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="6" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when><c:when test="${section eq 'nhat-ky'}"><div class="table-wrap"><table class="data-table"><thead><tr><th>Thời điểm</th><th>Người thực hiện</th><th>Hành động</th><th>Đối tượng</th><th>Chi tiết</th></tr>
</thead>
<tbody><c:forEach var="r" items="${result.items}"><tr><td><c:out value="${nn:datetime(r.created_at)}"/></td><td><c:out value="${r.actor_name}"/></td><td><c:out value="${r.action}"/></td><td><c:out value="${r.entity_type}"/> #<c:out value="${r.entity_id}"/></td><td><c:out value="${r.details}"/></td></tr>
</c:forEach><c:if test="${empty result.items}"><tr><td colspan="5" class="muted">Chưa có dữ liệu phù hợp với bộ lọc.</td></tr>
</c:if></tbody>
</table>
</div>
</c:when></c:choose><div class="table-caption"><span>Trang ${result.page} / ${result.pages} · ${result.total} bản ghi</span><span>Các thay đổi tài chính được lưu vào nhật ký thao tác.</span></div>
<nav class="pagination" aria-label="Phân trang quản lý"><c:forEach var="p" begin="1" end="${result.pages}"><c:url var="pageUrl" value="/admin/${section}"><c:forEach var="f" items="${filters}"><c:if test="${f.key ne 'page'}"><c:param name="${f.key}" value="${f.value}"/></c:if></c:forEach><c:param name="page" value="${p}"/></c:url><a href="${pageUrl}" class="${p eq result.page ? 'active' : ''}">${p}</a></c:forEach></nav>
</section>
<c:if test="${section eq 'tai-khoan'}"><div class="settings-grid" style="margin-top:20px"><section class="panel"><h2>Phân công quản lý tòa nhà</h2>
<p class="field-help">Chỉ tài khoản vai trò Quản lý đang hoạt động được phân công. Quản trị viên có quyền xem toàn hệ thống.</p>
<form method="post" action="${ctx}/admin/phan-cong"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><label>Tòa nhà<select name="building_id" required><c:forEach var="b" items="${staffBuildings}"><option value="${b.id}"><c:out value="${b.name}"/></option></c:forEach></select></label>
<label>Quản lý<select name="account_id" required><option value="">Chọn quản lý</option><c:forEach var="a" items="${managers}"><option value="${a.id}"><c:out value="${a.full_name}"/> · <c:out value="${a.email}"/></option></c:forEach></select></label>
<button class="button primary" type="submit">Phân công</button></form>
<div class="table-wrap"><table class="data-table"><tbody><c:forEach var="r" items="${assignments}"><tr><td><c:out value="${r.building_name}"/><small><c:out value="${r.full_name}"/></small></td><td><form method="post" action="${ctx}/admin/phan-cong"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><input type="hidden" name="building_id" value="${r.building_id}"><input type="hidden" name="account_id" value="${r.account_id}"><input type="hidden" name="assign" value="false"><button class="button danger small" type="submit">Gỡ</button></form>
</td></tr>
</c:forEach></tbody>
</table>
</div>
</section>
<section class="panel"><h2>Thêm tài khoản</h2>
<p class="field-help">Thông tin đăng nhập ban đầu được quản trị viên bàn giao riêng cho người sử dụng.</p>
<form method="post" action="${ctx}/admin/tai-khoan/tao"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><div class="form-grid two"><label>Họ tên<input required name="full_name" maxlength="150"></label>
<label>Tên đăng nhập<input required name="username" minlength="3" maxlength="60"></label>
</div>
<label>Email<input required name="email" type="email" maxlength="190"></label>
<div class="form-grid two"><label>Năm sinh<input required name="birth_year" type="number" min="1900" max="${today.year-18}"></label>
<label>Điện thoại<input required name="phone" maxlength="25"></label>
</div>
<input type="hidden" name="gender" value="OTHER"><label>Vai trò<select name="role"><option value="USER">Khách hàng</option><option value="MANAGER">Quản lý</option><option value="ADMIN">Quản trị viên</option></select></label>
<label>Mật khẩu ban đầu<input required type="password" name="password" minlength="8" maxlength="72" autocomplete="new-password"></label>
<button class="button primary" type="submit">Tạo tài khoản</button></form>
</section>
</div>
</c:if></main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>