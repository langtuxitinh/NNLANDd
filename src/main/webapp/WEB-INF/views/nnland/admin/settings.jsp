<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><form method="post" action="${ctx}/admin/cai-dat"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><div class="settings-grid"><section class="panel"><div class="panel-header"><div><h2>Tài khoản nhận tiền</h2>
<p>Thông tin dùng trên trang thanh toán và mã VietQR.</p>
</div>
</div>
<div class="notice warning">Nhập tài khoản ngân hàng thuộc đơn vị vận hành NNland và kiểm tra lại tên người nhận trên ứng dụng ngân hàng trước khi sử dụng.</div>
<label>Tên ngân hàng<input name="bank_name" maxlength="100" value="<c:out value='${meta.bank_name}'/>" placeholder="Ví dụ: Vietcombank"></label>
<label>Mã BIN ngân hàng (6 chữ số)<input name="bank_bin" maxlength="6" inputmode="numeric" value="<c:out value='${meta.bank_bin}'/>" placeholder="Ví dụ: 970436"></label>
<label>Số tài khoản<input name="bank_account" maxlength="34" inputmode="numeric" value="<c:out value='${meta.bank_account}'/>"></label>
<label>Chủ tài khoản<input name="bank_owner" maxlength="150" value="<c:out value='${meta.bank_owner}'/>"></label>
<p class="field-help">Để trống cả BIN, số tài khoản và chủ tài khoản nếu chưa sẵn sàng nhận tiền. QR chỉ được tạo khi thông tin đầy đủ.</p>
</section>
<section class="panel"><h2>Liên hệ & giữ chỗ</h2>
<p class="field-help">Thông tin này hiển thị trên website và các trang hỗ trợ.</p>
<label>Hotline<input required name="hotline" maxlength="25" value="<c:out value='${meta.hotline}'/>"></label>
<label>Email liên hệ<input required type="email" name="contact_email" maxlength="190" value="<c:out value='${meta.contact_email}'/>"></label>
<label>Địa chỉ văn phòng<input required name="office_address" maxlength="255" value="<c:out value='${meta.office_address}'/>"></label>
<label>Thời gian giữ chỗ ban đầu (phút)<input required type="number" name="hold_minutes" min="10" max="120" value="<c:out value='${meta.hold_minutes}'/>"></label>
<div class="notice">Khách báo đã chuyển khoản trong thời gian giữ chỗ sẽ được chuyển sang chờ đối chiếu tối đa 24 giờ một lần. Việc báo lại không kéo dài thêm thời gian.</div>
</section>
</div>
<section class="panel" style="margin-top:20px"><h2>Điều khoản thuê</h2>
<p class="field-help">Nội dung thay đổi được cấp phiên bản mới và chỉ áp dụng cho đơn mới. Đơn/hợp đồng đã chốt giữ nguyên điều khoản đã chấp nhận.</p>
<label>Nội dung điều khoản<textarea required name="terms_text" maxlength="10000" rows="12"><c:out value="${terms}"/></textarea></label>
<button class="button primary" type="submit">Lưu cấu hình NNland</button></section>
</form>
</main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>