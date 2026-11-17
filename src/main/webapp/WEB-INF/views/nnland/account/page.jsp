<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><c:choose><c:when test="${section eq 'tong-quan'}"><div class="stats-grid"><div class="kpi accent"><span class="kpi-label">Chào mừng bạn về nhà</span><strong>NNland</strong><small>Không gian riêng để theo dõi mọi thông tin.</small></div>
<div class="kpi"><span class="kpi-label">Hợp đồng đã tạo</span><strong>${fn:length(leases)}</strong><small>Bao gồm lịch sử và kỳ đang thuê.</small></div>
<div class="kpi"><span class="kpi-label">Thanh toán cần theo dõi</span><strong>${pending}</strong><small>Đang chờ nhận tiền hoặc đối soát.</small></div>
<div class="kpi"><span class="kpi-label">Yêu cầu hỗ trợ đang mở</span><strong>${openTickets}</strong><small>Quản lý đồng hành cùng bạn.</small></div>
</div>
<div class="panel"><div class="panel-header"><div><h2>Nơi ở của bạn</h2>
<p>Thông tin kỳ thuê và các hợp đồng đã xác nhận.</p>
</div>
<a class="text-link" href="${ctx}/can-ho">Khám phá căn hộ →</a></div>
<c:choose><c:when test="${empty leases}"><div class="empty-state"><span class="empty-icon">⌂</span><h3>Câu chuyện mới bắt đầu từ đây.</h3>
<p>Chọn căn hộ phù hợp, đặt lịch xem và bắt đầu kỳ thuê của bạn.</p>
<a class="button primary" href="${ctx}/can-ho">Tìm căn hộ</a></div>
</c:when><c:otherwise><div class="data-card-grid"><c:forEach var="l" items="${leases}"><article class="lease-card"><img src="${ctx}<c:out value='${l.image}'/>" alt="<c:out value='${l.unit_name}'/>"><div class="lease-card-body"><span class="badge ${l.status}"><c:out value="${nn:label(l.status)}"/></span><h3><c:out value="${l.unit_name}"/></h3>
<p><c:out value="${l.building_name}"/> · <c:out value="${l.unit_code}"/></p>
<div class="lease-dates"><div><span>Ngày bắt đầu</span><strong><c:out value="${nn:date(l.start_date)}"/></strong></div>
<div><span>Ngày trả phòng</span><strong><c:out value="${nn:date(l.end_date)}"/></strong></div>
</div>
<div class="action-row spread"><a class="text-link" href="${ctx}/tai-khoan/hop-dong/${l.id}">Xem hợp đồng →</a><strong class="muted"><c:out value="${nn:shortMoney(l.monthly_rent)}"/> / tháng</strong></div>
</div>
</article>
</c:forEach></div>
</c:otherwise></c:choose></div>
<section class="panel"><div class="panel-header"><h2>Thông báo gần đây</h2>
<a class="text-link" href="${ctx}/tai-khoan/thong-bao">Xem tất cả →</a></div>
<div class="notifications"><c:forEach var="n" items="${notifications}"><a class="notification" href="${ctx}<c:out value='${n.link}'/>"><span class="feature-icon"><svg class="icon"><use href="${ctx}/nnland/icons.svg#bell"/></svg></span><div><strong><c:out value="${n.title}"/></strong><p><c:out value="${n.body}"/></p>
<time><c:out value="${nn:datetime(n.created_at)}"/></time></div>
</a></c:forEach></div>
</section>
</c:when>
<c:when test="${section eq 'thong-tin'}"><div class="detail-grid"><form class="panel" action="${ctx}/tai-khoan/thong-tin" method="post"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><div class="panel-header"><div><h2>Hồ sơ cá nhân</h2>
<p>Cập nhật thông tin để NNland liên hệ thuận tiện hơn.</p>
</div>
</div>
<div class="form-grid two"><label>Họ và tên<input required name="full_name" maxlength="150" value="<c:out value='${currentUser.full_name}'/>"></label>
<label>Năm sinh<input required name="birth_year" type="number" min="1900" max="${today.year-18}" value="${currentUser.birth_year}"></label>
</div>
<div class="form-grid two"><label>Số điện thoại<input required name="phone" maxlength="25" value="<c:out value='${currentUser.phone}'/>"></label>
<label>Giới tính<select name="gender"><option value="OTHER" ${currentUser.gender eq 'OTHER' ? 'selected' : ''}>Không muốn cung cấp</option><option value="FEMALE" ${currentUser.gender eq 'FEMALE' ? 'selected' : ''}>Nữ</option><option value="MALE" ${currentUser.gender eq 'MALE' ? 'selected' : ''}>Nam</option></select></label>
</div>
<label>Email<input readonly value="<c:out value='${currentUser.email}'/>"></label>
<label>Tên đăng nhập<input readonly value="<c:out value='${currentUser.username}'/>"></label>
<button class="button primary" type="submit">Lưu thông tin</button><p class="field-help">Thông tin liên hệ trên hợp đồng đã xác nhận được lưu tại thời điểm tạo hợp đồng.</p>
</form>
<form class="panel" action="${ctx}/tai-khoan/mat-khau" method="post"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><h2>Đổi mật khẩu</h2>
<p class="field-help">Sau khi đổi mật khẩu, bạn cần đăng nhập lại.</p>
<label>Mật khẩu hiện tại<input required type="password" name="current_password" autocomplete="current-password"></label>
<label>Mật khẩu mới<input required type="password" name="password" minlength="8" maxlength="72" autocomplete="new-password"></label>
<label>Nhập lại mật khẩu mới<input required type="password" name="password_confirm" minlength="8" maxlength="72" autocomplete="new-password"></label>
<button class="button outline full" type="submit">Cập nhật mật khẩu</button></form>
</div>
</c:when>
<c:when test="${section eq 'luu-tru'}"><c:choose><c:when test="${empty leases}"><div class="empty-state"><h3>Bạn chưa có hợp đồng thuê.</h3>
<p>Hợp đồng xuất hiện sau khi quản lý xác nhận đủ tiền thuê.</p>
<a class="button primary" href="${ctx}/can-ho">Khám phá căn hộ →</a></div>
</c:when><c:otherwise><div class="data-card-grid"><c:forEach var="l" items="${leases}"><article class="lease-card"><img src="${ctx}<c:out value='${l.image}'/>" alt="<c:out value='${l.unit_name}'/>"><div class="lease-card-body"><span class="badge ${l.status}"><c:out value="${nn:label(l.status)}"/></span><h3><c:out value="${l.unit_name}"/></h3>
<p><c:out value="${l.building_name}"/> · <c:out value="${l.unit_code}"/></p>
<div class="lease-dates"><div><span>Ngày bắt đầu</span><strong><c:out value="${nn:date(l.start_date)}"/></strong></div>
<div><span>Ngày trả phòng</span><strong><c:out value="${nn:date(l.end_date)}"/></strong></div>
</div>
<div class="action-row spread"><a class="text-link" href="${ctx}/tai-khoan/hop-dong/${l.id}">Xem hợp đồng →</a><strong class="muted"><c:out value="${nn:shortMoney(l.monthly_rent)}"/> / tháng</strong></div>
</div>
</article>
</c:forEach></div>
</c:otherwise></c:choose></c:when>
<c:when test="${section eq 'dat-thue'}"><section class="panel"><div class="panel-header"><div><h2>Lịch sử đặt thuê & gia hạn</h2>
<p>Theo dõi thời gian giữ chỗ, thanh toán và kết quả xác nhận.</p>
</div>
<a class="button primary small" href="${ctx}/can-ho">Tìm căn hộ →</a></div>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Đơn thuê / Căn hộ</th><th>Kỳ thuê</th><th>Tiền thuê & cọc</th><th>Tình trạng</th><th>Thao tác</th></tr>
</thead>
<tbody><c:forEach var="r" items="${rows}"><tr><td><strong><c:out value="${r.code}"/></strong><small><c:out value="${r.unit_name}"/><br><c:out value="${r.building_name}"/> · <c:out value="${r.unit_code}"/></small><span class="badge soft">${r.kind eq 'EXTENSION' ? 'Gia hạn' : 'Thuê mới'}</span></td><td><c:out value="${nn:date(r.start_date)}"/><br>→ <c:out value="${nn:date(r.end_date)}"/><small>${r.months} tháng</small></td><td class="money"><c:out value="${nn:money(r.rent_total+r.deposit_total)}"/><small>Đã nhận: <c:out value="${nn:money(r.received)}"/></small></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span><small>Thanh toán: <c:out value="${nn:label(r.payment_status)}"/></small><c:if test="${r.status eq 'HOLD' or r.status eq 'PAYMENT_REVIEW'}"><small>Giữ đến <c:out value="${nn:datetime(r.hold_until)}"/></small></c:if></td><td><a class="button outline small" href="${ctx}/thanh-toan/${r.payment_id}">Xem thanh toán</a><c:if test="${r.status eq 'CONFIRMED'}"><a class="small-link" href="${ctx}/tai-khoan/hop-dong/${r.lease_id}">Xem hợp đồng</a></c:if><c:if test="${r.status ne 'CONFIRMED' and r.status ne 'CANCELLED'}"><details class="mail-details"><summary>Hủy đơn thuê</summary><form method="post" action="${ctx}/tai-khoan/yeu-cau"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><input type="hidden" name="booking_id" value="${r.id}"><input type="hidden" name="type" value="CANCEL"><label>Lý do<textarea required name="note" maxlength="3000" rows="2"></textarea></label>
<button class="button danger small" type="submit">Gửi yêu cầu hủy</button></form>
</details>
</c:if></td></tr>
</c:forEach><c:if test="${empty rows}"><tr><td colspan="5" class="muted">Chưa có đơn đặt thuê. Hãy chọn căn hộ bạn yêu thích để bắt đầu.</td></tr>
</c:if></tbody>
</table>
</div>
</section>
</c:when>
<c:when test="${section eq 'hoa-don'}"><section class="panel"><div class="panel-header"><div><h2>Điện nước & phí sinh hoạt</h2>
<p>Tiền thuê đã thu trước được theo dõi trong đơn thuê, không thu lặp tại đây.</p>
</div>
</div>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Kỳ / Mã hóa đơn</th><th>Căn hộ</th><th>Ngày chốt / Hạn trả</th><th>Tổng cộng</th><th>Thanh toán</th><th></th></tr>
</thead>
<tbody><c:forEach var="r" items="${rows}"><tr><td><strong><c:out value="${r.period}"/></strong><small><c:out value="${r.code}"/></small></td><td><c:out value="${r.unit_code}"/><small><c:out value="${r.building_name}"/></small></td><td><c:out value="${nn:date(r.reading_date)}"/><small>Hạn: <c:out value="${nn:date(r.due_date)}"/></small></td><td class="money"><c:out value="${nn:money(r.total)}"/><small>Đã nhận: <c:out value="${nn:money(r.received)}"/></small></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><a class="button outline small" href="${ctx}/hoa-don/${r.id}">Chi tiết</a></td></tr>
</c:forEach><c:if test="${empty rows}"><tr><td colspan="6" class="muted">Chưa có hóa đơn được phát hành cho tài khoản này.</td></tr>
</c:if></tbody>
</table>
</div>
</section>
</c:when>
<c:when test="${section eq 'ho-tro'}"><div class="detail-grid"><section class="panel"><div class="panel-header"><div><h2>Yêu cầu đã gửi</h2>
<p>Trao đổi và theo dõi tiến độ xử lý cùng quản lý.</p>
</div>
</div>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Yêu cầu</th><th>Căn hộ</th><th>Trạng thái</th><th></th></tr>
</thead>
<tbody><c:forEach var="r" items="${rows}"><tr><td><strong><c:out value="${r.subject}"/></strong><small><c:out value="${r.code}"/> · <c:out value="${nn:datetime(r.created_at)}"/></small></td><td><c:out value="${r.unit_code}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><a href="${ctx}/tai-khoan/ho-tro/${r.id}" class="text-link">Xem →</a></td></tr>
</c:forEach><c:if test="${empty rows}"><tr><td colspan="4" class="muted">Chưa có yêu cầu hỗ trợ.</td></tr>
</c:if></tbody>
</table>
</div>
</section>
<form class="panel" method="post" action="${ctx}/tai-khoan/ho-tro"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><h2>Gửi yêu cầu mới</h2>
<p class="field-help">Chọn hợp đồng đang lưu trú để báo sự cố tại căn hộ.</p>
<label>Căn hộ<select required name="lease_id"><option value="">Chọn căn hộ đang ở</option><c:forEach var="l" items="${leases}"><c:if test="${l.status eq 'ACTIVE' or l.status eq 'ENDING'}"><option value="${l.id}"><c:out value="${l.unit_code}"/> · <c:out value="${l.building_name}"/></option></c:if></c:forEach></select></label>
<div class="form-grid two"><label>Nhóm sự cố<select name="category"><option value="ELECTRIC">Điện</option><option value="WATER">Nước</option><option value="FURNITURE">Nội thất</option><option value="INTERNET">Internet</option><option value="OTHER">Khác</option></select></label>
<label>Mức ưu tiên<select name="priority"><option value="NORMAL">Thông thường</option><option value="URGENT">Khẩn cấp</option></select></label>
</div>
<label>Tiêu đề<input required name="subject" maxlength="180"></label>
<label>Mô tả<textarea required rows="5" name="description" maxlength="4000" placeholder="Vị trí, tình trạng và thời điểm thuận tiện để kiểm tra..."></textarea></label>
<button class="button primary full" type="submit">Gửi yêu cầu hỗ trợ</button><p class="field-help">Sự cố cần xử lý ngay: gọi <c:out value="${meta.hotline}"/>.</p>
</form>
</div>
</c:when>
<c:when test="${section eq 'yeu-cau'}"><section class="panel"><div class="panel-header"><div><h2>Hủy đặt thuê & trả phòng</h2>
<p>Gửi yêu cầu từ chi tiết hợp đồng hoặc đơn đặt thuê tương ứng.</p>
</div>
<a class="text-link" href="${ctx}/tai-khoan/luu-tru">Xem hợp đồng →</a></div>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Yêu cầu</th><th>Căn hộ</th><th>Ngày mong muốn</th><th>Tình trạng</th><th>Phản hồi quản lý</th></tr>
</thead>
<tbody><c:forEach var="r" items="${rows}"><tr><td><strong><c:out value="${nn:label(r.type)}"/></strong><small><c:out value="${nn:datetime(r.created_at)}"/></small><p><c:out value="${r.note}"/></p>
</td><td><c:out value="${r.unit_code}"/><small><c:out value="${r.building_name}"/></small></td><td><c:out value="${nn:date(r.requested_date)}"/></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><c:out value="${r.manager_note}"/></td></tr>
</c:forEach><c:if test="${empty rows}"><tr><td colspan="5" class="muted">Chưa có yêu cầu cần quản lý xử lý. Đơn giữ chỗ chưa báo chuyển khoản có thể được hủy ngay trong mục Đơn đặt thuê.</td></tr>
</c:if></tbody>
</table>
</div>
</section>
</c:when>
<c:when test="${section eq 'lich-hen'}"><section class="panel"><div class="panel-header"><div><h2>Lịch hẹn & liên hệ</h2>
<p>Các yêu cầu được gửi trong khi bạn đăng nhập.</p>
</div>
<a class="button outline small" href="${ctx}/lien-he">Gửi liên hệ mới</a></div>
<div class="table-wrap"><table class="data-table"><thead><tr><th>Nội dung / Căn hộ</th><th>Ngày & khung giờ</th><th>Tình trạng</th><th>Phản hồi</th></tr>
</thead>
<tbody><c:forEach var="r" items="${rows}"><tr><td><strong><c:out value="${r.unit_name}"/></strong><p><c:out value="${r.message}"/></p>
</td><td><c:out value="${nn:date(r.preferred_date)}"/><small><c:out value="${r.preferred_time}"/></small></td><td><span class="badge ${r.status}"><c:out value="${nn:label(r.status)}"/></span></td><td><c:out value="${r.manager_note}"/></td></tr>
</c:forEach><c:if test="${empty rows}"><tr><td colspan="4" class="muted">Bạn chưa gửi yêu cầu xem căn hộ hoặc liên hệ.</td></tr>
</c:if></tbody>
</table>
</div>
</section>
</c:when>
<c:when test="${section eq 'yeu-thich'}"><c:choose><c:when test="${empty favorites}"><div class="empty-state"><span class="empty-icon">♡</span><h3>Lưu những căn hộ bạn yêu thích.</h3>
<p>Nhấn biểu tượng trái tim trên thẻ căn hộ để dễ dàng tìm lại.</p>
<a href="${ctx}/can-ho" class="button primary">Khám phá căn hộ →</a></div>
</c:when><c:otherwise><div class="apartment-grid"><c:forEach var="item" items="${favorites}"><c:set var="unitCard" value="${item}" scope="request"/><jsp:include page="/WEB-INF/views/nnland/common/apartment-card.jsp"/></c:forEach></div>
</c:otherwise></c:choose></c:when>
<c:when test="${section eq 'thong-bao'}"><section class="panel"><div class="panel-header"><h2>Cập nhật dành cho bạn</h2>
<form method="post" action="${ctx}/tai-khoan/thong-bao/doc"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><button class="button outline small" type="submit">Đánh dấu đã đọc tất cả</button></form>
</div>
<div class="notifications"><c:forEach var="r" items="${rows}"><a class="notification ${r.is_read ? '' : 'unread'}" href="${ctx}<c:out value='${r.link}'/>"><span class="feature-icon"><svg class="icon"><use href="${ctx}/nnland/icons.svg#bell"/></svg></span><div><strong><c:out value="${r.title}"/></strong><p><c:out value="${r.body}"/></p>
<time><c:out value="${nn:datetime(r.created_at)}"/></time></div>
</a></c:forEach><c:if test="${empty rows}"><p class="muted">Chưa có thông báo.</p>
</c:if></div>
</section>
</c:when></c:choose></main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>