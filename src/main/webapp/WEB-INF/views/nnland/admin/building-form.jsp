<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/workspace-start.jsp"/><form class="panel" method="post" enctype="multipart/form-data" action="${ctx}/admin/toa-nha/luu"><jsp:include page="/WEB-INF/views/nnland/common/csrf.jsp"/><input type="hidden" name="id" value="${form.id}"><div class="panel-header"><h2>${empty form.id ? 'Thêm tòa nhà' : 'Chỉnh sửa tòa nhà'}</h2>
<a class="button outline small" href="${ctx}/admin/toa-nha">← Danh sách</a></div>
<div class="form-grid two"><label>Mã tòa nhà<input required name="code" maxlength="32" value="<c:out value='${form.code}'/>"></label>
<label>Tên tòa nhà<input required name="name" maxlength="150" value="<c:out value='${form.name}'/>"></label>
<label>Thành phố<input required name="city" maxlength="80" value="<c:out value='${form.city}'/>" list="city-list"></label>
<label>Quận/huyện<input required name="district" maxlength="80" value="<c:out value='${form.district}'/>"></label>
</div>
<datalist id="city-list"><c:forEach var="c" items="${cities}"><option value="<c:out value='${c.city}'/>"></option></c:forEach></datalist><label>Địa chỉ<input required name="address" maxlength="255" value="<c:out value='${form.address}'/>"></label>
<label>Giới thiệu<textarea required name="description" maxlength="5000" rows="5"><c:out value="${form.description}"/></textarea></label>
<label>Tiện ích, ngăn cách bằng dấu |<input name="amenities" maxlength="1000" value="<c:out value='${form.amenities}'/>" placeholder="Hồ bơi|Phòng gym|An ninh 24/7"></label>
<div class="form-grid two"><div class="image-field"><label>Ảnh trong thư viện<select name="image" data-image-select data-context="${ctx}"><c:if test="${fn:startsWith(form.image,'/uploads/')}"><option value="<c:out value='${form.image}'/>" selected>Ảnh đã tải lên</option></c:if><c:forEach var="image" items="${images}"><c:set var="imagePath" value="/nnland/images/${image}"/><option value="${imagePath}" ${form.image eq imagePath ? 'selected' : ''}><c:out value="${image}"/></option></c:forEach></select></label>
<img class="library-preview" src="${ctx}<c:out value='${form.image}'/>" alt="Ảnh xem trước"></div>
<label>Hoặc tải ảnh riêng<input type="file" name="photo" accept="image/jpeg,image/png"><span class="field-help">JPG/PNG, tối đa 5 MB. Ảnh được tối ưu khi lưu.</span></label>
</div>
<label class="check-label"><input type="checkbox" name="published" ${form.published ? 'checked' : ''}><span>Hiển thị tòa nhà trên trang khách hàng.</span></label>
<button class="button primary" type="submit">Lưu tòa nhà</button></form>
</main>
</div>
<jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>