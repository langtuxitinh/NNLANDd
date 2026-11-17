<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><div class="detail-grid"><section class="panel"><div class="panel-header"><h2><c:out value="${nn:label(request.type)}"/></h2>
<span class="badge ${request.status}"><c:out value="${nn:label(request.status)}"/></span></div>
<div class="info-grid"><div><span>Khách hàng</span><strong><c:out value="${request.full_name}"/></strong></div>
<div><span>Căn hộ</span><strong><c:out value="${unit.code}"/> · <c:out value="${unit.name}"/></strong></div>
<div><span>Ngày gửi</span><strong><c:out value="${nn:datetime(request.created_at)}"/></strong></div>
<div><span>Ngày đề nghị trả</span><strong><c:out value="${nn:date(request.requested_date)}"/></strong></div>
</div>
<p class="description-text"><c:out value="${request.note}"/></p>
<c:if test="${not empty lease}"><a class="button outline small" href="${ctx}/admin/hop-dong/${lease.id}">Xem hợp đồng & công nợ →</a><div class="notice">Tiền cọc ban đầu: <c:out value="${nn:money(lease.deposit_held)}"/>. Khoản khấu trừ và hoàn tiền cần có thỏa thuận khi quyết toán.</div>
</c:if><c:if test="${not empty request.manager_note}"><h3>Kết quả xử lý</h3>
<p class="description-text"><c:out value="${request.manager_note}"/></p>
</c:if></section>
<c:if test="${request.status eq 'OPEN'}"><form class="panel" method="post" action="${ctx}/admin/yeu-cau/${request.id}"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><h2>Kiểm tra & xử lý</h2>
<c:choose><c:when test="${request.type eq 'CHECKOUT'}"><div class="notice warning">Chỉ chấp thuận sau khi đã thu đủ hóa đơn và lập hóa đơn cuối kỳ cho các chỉ số phát sinh. Không tự giải phóng căn chỉ vì hết hạn hợp đồng.</div>
<a class="button outline small" href="${ctx}/admin/hoa-don/moi?lease=${lease.id}&amp;final_bill=true">Lập hóa đơn chốt trả phòng →</a><label>Ngày bàn giao thực tế<input required type="date" name="actual_date" value="${today}" max="${today}"></label>
<div class="form-grid two"><label>Điện chốt trả phòng<input required type="number" step="0.001" name="electricity_final" value="${unit.electricity_meter}"></label>
<label>Nước chốt trả phòng<input required type="number" step="0.001" name="water_final" value="${unit.water_meter}"></label>
</div>
<label>Khấu trừ cọc theo biên bản (đồng)<input required type="number" name="deposit_deduction" min="0" value="0"></label>
<label>Tiền thuê thỏa thuận hoàn (đồng)<input required type="number" name="rent_refund" min="0" value="0"></label>
<label>Tình trạng căn hộ sau bàn giao<select name="readiness"><option value="READY">Sẵn sàng đón khách mới</option><option value="MAINTENANCE">Cần vệ sinh/sửa chữa, đang bảo trì</option></select></label>
<label class="check-label"><input type="checkbox" name="inspected"><span>Đã kiểm tra căn hộ, chốt chỉ số và đối chiếu công nợ.</span></label>
</c:when><c:otherwise><div class="notice">Chấp thuận hủy sẽ giải phóng lịch thuê và lập khoản hoàn cho tiền thực nhận còn lại. Tiền chỉ được đánh dấu đã hoàn sau khi có mã chuyển khoản thực tế.</div>
</c:otherwise></c:choose><label>Nội dung phản hồi / biên bản<textarea required name="manager_note" maxlength="3000" rows="5"></textarea></label>
<div class="action-row"><button class="button primary" type="submit" name="decision" value="approve">Chấp thuận</button><button class="button danger" type="submit" name="decision" value="reject" formnovalidate>Từ chối</button></div>
<p class="field-help">Từ chối cũng cần ghi rõ lý do.</p>
</form>
</c:if></div>
</main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>