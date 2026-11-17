<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><form class="panel" method="post" enctype="multipart/form-data" action="${ctx}/admin/can-ho/luu"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><input type="hidden" name="id" value="${form.id}"><div class="panel-header"><div><h2>${empty form.id ? 'Thêm căn hộ' : 'Chỉnh sửa căn hộ'}</h2>
<p>Giá và phí mới áp dụng cho các đơn mới; hợp đồng đã chốt giữ nguyên dữ liệu.</p>
</div>
<a class="button outline small" href="${ctx}/admin/can-ho">← Danh sách</a></div>
<div class="form-grid three"><label>Tòa nhà<select required name="building_id"><option value="">Chọn tòa nhà</option><c:forEach var="b" items="${staffBuildings}"><option value="${b.id}" ${form.building_id eq b.id ? 'selected' : ''}><c:out value="${b.name}"/></option></c:forEach></select></label>
<label>Mã căn<input required name="code" maxlength="32" value="<c:out value='${form.code}'/>"></label>
<label>Loại căn hộ<select name="type"><c:forEach var="t" items="${types}"><option value="${t.key}" ${form.type eq t.key ? 'selected' : ''}><c:out value="${t.value}"/></option></c:forEach></select></label>
</div>
<label>Tên căn hộ<input required name="name" maxlength="180" value="<c:out value='${form.name}'/>"></label>
<div class="form-grid four"><label>Phòng ngủ (Studio: 0)<input required type="number" name="bedrooms" min="0" max="10" value="${form.bedrooms}"></label>
<label>Phòng tắm<input required type="number" name="bathrooms" min="1" max="10" value="${form.bathrooms}"></label>
<label>Diện tích m²<input required type="number" name="area" min="10" max="2000" step="0.01" value="${form.area}"></label>
<label>Tầng<input required type="number" name="floor_no" min="0" max="150" value="${form.floor_no}"></label>
</div>
<div class="form-grid three"><label>Tiền thuê / tháng (đồng)<input required type="number" name="monthly_rent" min="100000" max="1000000000" value="${form.monthly_rent}"></label>
<label>Tiền cọc ban đầu (đồng)<input required type="number" name="deposit_amount" min="0" value="${form.deposit_amount}"></label>
<label>Phí dịch vụ / tháng<input required type="number" name="service_fee" min="0" value="${form.service_fee}"></label>
<label>Điện / kWh<input required type="number" name="electricity_rate" min="0" max="1000000" value="${form.electricity_rate}"></label>
<label>Nước / m³<input required type="number" name="water_rate" min="0" max="1000000" value="${form.water_rate}"></label>
<label>Gửi xe / tháng<input required type="number" name="parking_fee" min="0" value="${form.parking_fee}"></label>
</div>
<label>Mô tả<textarea required name="description" rows="5" maxlength="5000"><c:out value="${form.description}"/></textarea></label>
<label>Tiện nghi, ngăn cách bằng dấu |<input name="amenities" maxlength="2000" value="<c:out value='${form.amenities}'/>" placeholder="Điều hòa|Bếp|Tủ lạnh|Máy giặt"></label>
<div class="form-grid three"><div class="image-field"><label>Ảnh chính<select name="image" data-image-select data-context="${ctx}"><c:if test="${fn:startsWith(form.image,'/uploads/')}"><option value="<c:out value='${form.image}'/>" selected>Ảnh đã tải lên</option></c:if><c:forEach var="image" items="${images}"><c:set var="imagePath" value="/nnland/images/${image}"/><option value="${imagePath}" ${form.image eq imagePath ? 'selected' : ''}><c:out value="${image}"/></option></c:forEach></select></label>
<img class="library-preview" src="${ctx}<c:out value='${form.image}'/>" alt="Ảnh xem trước"></div>
<div class="image-field"><label>Ảnh thứ hai<select name="image2" data-image-select data-context="${ctx}"><c:if test="${fn:startsWith(form.image2,'/uploads/')}"><option value="<c:out value='${form.image2}'/>" selected>Ảnh đã tải lên</option></c:if><c:forEach var="image" items="${images}"><c:set var="imagePath" value="/nnland/images/${image}"/><option value="${imagePath}" ${form.image2 eq imagePath ? 'selected' : ''}><c:out value="${image}"/></option></c:forEach></select></label>
<img class="library-preview" src="${ctx}<c:out value='${form.image2}'/>" alt="Ảnh xem trước"></div>
<div class="image-field"><label>Ảnh thứ ba<select name="image3" data-image-select data-context="${ctx}"><c:if test="${fn:startsWith(form.image3,'/uploads/')}"><option value="<c:out value='${form.image3}'/>" selected>Ảnh đã tải lên</option></c:if><c:forEach var="image" items="${images}"><c:set var="imagePath" value="/nnland/images/${image}"/><option value="${imagePath}" ${form.image3 eq imagePath ? 'selected' : ''}><c:out value="${image}"/></option></c:forEach></select></label>
<img class="library-preview" src="${ctx}<c:out value='${form.image3}'/>" alt="Ảnh xem trước"></div>
</div>
<div class="form-grid three"><label>Tải ảnh chính riêng<input type="file" name="photo" accept="image/jpeg,image/png"></label>
<label>Tải ảnh thứ hai<input type="file" name="photo2" accept="image/jpeg,image/png"></label>
<label>Tải ảnh thứ ba<input type="file" name="photo3" accept="image/jpeg,image/png"></label>
</div>
<p class="field-help">Mỗi ảnh JPG/PNG tối đa 5 MB; ảnh tải lên thay ảnh thư viện tương ứng và được tối ưu trước khi lưu.</p>
<div class="form-grid three"><label>Trạng thái vận hành<select name="operational_status"><option value="READY" ${form.operational_status eq 'READY' ? 'selected' : ''}>Sẵn sàng</option><option value="MAINTENANCE" ${form.operational_status eq 'MAINTENANCE' ? 'selected' : ''}>Đang bảo trì</option><option value="INACTIVE" ${form.operational_status eq 'INACTIVE' ? 'selected' : ''}>Tạm ngừng</option></select></label>
<label class="check-label"><input type="checkbox" name="published" ${form.published ? 'checked' : ''}><span>Hiển thị trên trang khách hàng.</span></label>
<label class="check-label"><input type="checkbox" name="featured" ${form.featured ? 'checked' : ''}><span>Đánh dấu căn hộ nổi bật.</span></label>
</div>
<div class="notice">Lịch trống được tính từ đơn giữ chỗ và hợp đồng, độc lập với trạng thái vận hành. Hãy hoàn tất quy trình trả phòng trước khi bàn giao cho khách mới.</div>
<button class="button primary" type="submit">Lưu căn hộ</button></form>
</main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>