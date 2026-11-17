<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><c:choose><c:when test="${empty leases}"><div class="empty-state"><h3>Chưa có hợp đồng đang lưu trú.</h3>
<p>Hóa đơn sinh hoạt được lập sau khi căn hộ đã được ký nhận và bàn giao.</p>
<a class="button primary" href="${ctx}/admin/hop-dong">Xem hợp đồng →</a></div>
</c:when><c:otherwise><form class="panel" method="post" action="${ctx}/admin/hoa-don/tao"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><div class="panel-header"><div><h2>Lập hóa đơn điện nước</h2>
<p>Chỉ số đầu lấy từ lần bàn giao hoặc hóa đơn gần nhất.</p>
</div>
<a class="button outline small" href="${ctx}/admin/hoa-don">← Danh sách</a></div>
<label>Hợp đồng / Căn hộ<select required name="lease_id" data-lease-select="${ctx}/admin/hoa-don/moi"><c:forEach var="l" items="${leases}"><option value="${l.id}" ${lease.id eq l.id ? 'selected' : ''}><c:out value="${l.full_name}"/> · <c:out value="${l.unit_code}"/> · <c:out value="${l.building_name}"/></option></c:forEach></select></label>
<div class="action-row"><button class="button outline small" type="button" data-final-period="${suggestedFinalPeriod}">Dùng kỳ chốt trả phòng: ${suggestedFinalPeriod}</button></div>
<div class="form-grid three"><label>Kỳ hóa đơn<input required name="period" value="${period}" maxlength="20" placeholder="YYYY-MM hoặc YYYY-MM-FINAL"></label>
<label>Ngày chốt chỉ số<input required type="date" name="reading_date" value="${today}" max="${today}"></label>
<label>Hạn thanh toán<input required type="date" name="due_date" value="${due}"></label>
</div>
<div class="form-grid two"><section class="panel"><h3>Điện</h3>
<p class="field-help">Đơn giá: <c:out value="${nn:money(lease.electricity_rate)}"/> / kWh</p>
<label>Chỉ số đầu<input readonly value="${empty previous ? lease.electricity_start : previous.electricity_end}"></label>
<label>Chỉ số cuối<input required type="number" step="0.001" min="${empty previous ? lease.electricity_start : previous.electricity_end}" name="electricity_end" value="${empty previous ? lease.electricity_start : previous.electricity_end}"></label>
</section>
<section class="panel"><h3>Nước</h3>
<p class="field-help">Đơn giá: <c:out value="${nn:money(lease.water_rate)}"/> / m³</p>
<label>Chỉ số đầu<input readonly value="${empty previous ? lease.water_start : previous.water_end}"></label>
<label>Chỉ số cuối<input required type="number" step="0.001" min="${empty previous ? lease.water_start : previous.water_end}" name="water_end" value="${empty previous ? lease.water_start : previous.water_end}"></label>
</section>
</div>
<div class="form-grid three"><label>Phí dịch vụ<input required type="number" min="0" name="service_fee" value="${monthFeesCharged ? 0 : lease.service_fee}"></label>
<label>Phí gửi xe<input required type="number" min="0" name="parking_fee" value="${monthFeesCharged ? 0 : lease.parking_fee}"></label>
<label>Điều chỉnh (+/−)<input required type="number" name="adjustment" value="0"></label>
</div>
<label>Diễn giải<textarea name="note" maxlength="2000" rows="3" placeholder="Bắt buộc diễn giải nếu có điều chỉnh..."></textarea></label>
<div class="notice">Hóa đơn chốt trả phòng phải đúng ngày bàn giao. Nếu đổi ngày sau khi đã thanh toán, lập kỳ bổ sung -FINAL-001, -002… nối tiếp chỉ số cũ. Các phí dịch vụ/gửi xe đã tính trong tháng được mặc định bằng 0, tránh tính hai lần.</div>
<div class="action-row spread"><div><span class="muted">Tổng dự kiến</span><h2 data-invoice-estimate data-electricity-start="${empty previous ? lease.electricity_start : previous.electricity_end}" data-water-start="${empty previous ? lease.water_start : previous.water_end}" data-electricity-rate="${lease.electricity_rate}" data-water-rate="${lease.water_rate}">—</h2>
</div>
<button class="button primary" type="submit">Phát hành hóa đơn →</button></div>
</form>
</c:otherwise></c:choose></main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>