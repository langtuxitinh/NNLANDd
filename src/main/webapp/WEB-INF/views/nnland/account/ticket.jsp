<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><div class="action-row spread" style="margin-bottom:20px"><span class="badge ${ticket.status}"><c:out value="${nn:label(ticket.status)}"/></span><a class="button outline small" href="${ctx}/${adminView ? 'admin/ho-tro' : 'tai-khoan/ho-tro'}">← Danh sách yêu cầu</a></div>
<div class="detail-grid"><div class="stack"><section class="panel"><span class="eyebrow"><c:out value="${ticket.code}"/></span><h2><c:out value="${ticket.subject}"/></h2>
<p class="field-help"><c:out value="${ticket.building_name}"/> · <c:out value="${ticket.unit_code}"/> · <c:out value="${nn:label(ticket.category)}"/></p>
<p class="description-text"><c:out value="${ticket.description}"/></p>
<div class="info-grid"><div><span>Mức ưu tiên</span><strong><c:out value="${nn:label(ticket.priority)}"/></strong></div>
<div><span>Kỹ thuật viên / đơn vị xử lý</span><strong><c:out value="${ticket.assigned_to}"/></strong></div>
</div>
</section>
<section class="panel"><h2>Trao đổi & tiến độ</h2>
<div class="timeline"><c:forEach var="e" items="${events}"><article class="timeline-item"><h3><c:out value="${e.full_name}"/> <span class="badge ${e.status}"><c:out value="${nn:label(e.status)}"/></span></h3>
<p><c:out value="${e.body}"/></p>
<small><c:out value="${nn:datetime(e.created_at)}"/></small></article>
</c:forEach></div>
</section>
</div>
<aside><c:choose><c:when test="${adminView}"><form class="panel" method="post" action="${ctx}/admin/ho-tro/${ticket.id}"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><h2>Cập nhật xử lý</h2>
<p class="field-help">Nội dung cập nhật sẽ được thông báo cho khách hàng.</p>
<label>Trạng thái<select name="status"><c:forTokens var="state" items="OPEN,IN_PROGRESS,WAITING,RESOLVED,CLOSED" delims=","><option value="${state}" ${ticket.status eq state ? 'selected' : ''}><c:out value="${nn:label(state)}"/></option></c:forTokens></select></label>
<label>Quản lý phụ trách<select name="assignee_id"><option value="">Chưa phân công</option><c:forEach var="a" items="${staff}"><option value="${a.id}" ${ticket.assignee_id eq a.id ? 'selected' : ''}><c:out value="${a.full_name}"/></option></c:forEach></select></label>
<label>Kỹ thuật viên / đơn vị xử lý<input name="assigned_to" maxlength="150" value="<c:out value='${ticket.assigned_to}'/>"></label>
<label>Nội dung cập nhật<textarea required name="note" rows="5" maxlength="4000"></textarea></label>
<button class="button primary full" type="submit">Lưu tiến độ</button></form>
</c:when><c:otherwise><c:if test="${ticket.status ne 'CLOSED' and ticket.account_id eq currentUser.id}"><form class="panel" method="post" action="${ctx}/tai-khoan/ho-tro/${ticket.id}/phan-hoi"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><h2>Bổ sung thông tin</h2>
<label>Nội dung<textarea required name="body" rows="5" maxlength="4000"></textarea></label>
<c:if test="${ticket.status eq 'RESOLVED'}"><label class="check-label"><input type="checkbox" name="reopen" value="true"><span>Vấn đề chưa được giải quyết, cần tiếp tục hỗ trợ.</span></label>
</c:if><button class="button primary full" type="submit">Gửi phản hồi</button></form>
</c:if></c:otherwise></c:choose></aside>
</div>
</main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>