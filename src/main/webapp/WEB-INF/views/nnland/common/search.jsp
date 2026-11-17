<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<form action="${ctx}/can-ho" method="get" class="search-panel"><div class="search-head"><div><span class="eyebrow">TÌM CĂN HỘ PHÙ HỢP</span><h2>Bạn đang tìm nơi ở nào?</h2>
</div>
<span class="muted">Một lựa chọn vừa vặn với bạn.</span></div>
<div class="search-fields"><label><span>Thành phố</span><select name="city" data-city-select><option value="">Tất cả thành phố</option><c:forEach var="city" items="${cities}"><option value="<c:out value='${city.city}'/>" ${param.city eq city.city ? 'selected' : ''}><c:out value="${city.city}"/></option></c:forEach></select></label>
<label><span>Khu vực</span><select name="district" data-district-select><option value="">Tất cả khu vực</option><c:forEach var="location" items="${locationOptions}"><c:if test="${param.city eq location.city}"><option value="<c:out value='${location.district}'/>" ${param.district eq location.district ? 'selected' : ''}><c:out value="${location.district}"/></option></c:if></c:forEach></select></label>
<label><span>Phòng ngủ</span><select name="beds"><option value="">Tất cả</option><option value="0" ${param.beds eq '0' ? 'selected' : ''}>Studio</option><option value="1" ${param.beds eq '1' ? 'selected' : ''}>1 phòng ngủ</option><option value="2" ${param.beds eq '2' ? 'selected' : ''}>2 phòng ngủ</option><option value="3" ${param.beds eq '3' ? 'selected' : ''}>3 phòng ngủ trở lên</option></select></label>
<label><span>Ngân sách / tháng</span><select name="budget"><option value="">Tất cả mức giá</option><option value="10000000" ${param.budget eq '10000000' ? 'selected' : ''}>Đến 10 triệu</option><option value="15000000" ${param.budget eq '15000000' ? 'selected' : ''}>Đến 15 triệu</option><option value="20000000" ${param.budget eq '20000000' ? 'selected' : ''}>Đến 20 triệu</option><option value="40000000" ${param.budget eq '40000000' ? 'selected' : ''}>Đến 40 triệu</option></select></label>
<button class="button primary search-button" type="submit"><svg class="icon"><use href="${ctx}/nnland/icons.svg#search"/></svg>Tìm căn hộ</button></div>
<c:if test="${activeNav eq 'apartments'}"><details class="advanced-search" ${not empty param.start or not empty param.type ? 'open' : ''}><summary>Chọn loại căn hộ & thời gian thuê <span>+</span></summary><div class="form-grid four"><label>Loại căn hộ<select name="type"><option value="">Tất cả loại</option><c:forEach var="type" items="${types}"><option value="${type.key}" ${param.type eq type.key ? 'selected' : ''}><c:out value="${type.value}"/></option></c:forEach></select></label>
<label>Ngày nhận căn hộ<input name="start" type="date" min="${today}" value="<c:out value='${empty param.start ? today : param.start}'/>"></label>
<label>Số tháng<input name="months" type="number" min="1" max="24" value="<c:out value='${empty param.months ? 1 : param.months}'/>"></label>
<label>Sắp xếp<select name="sort"><option value="recommended">Đề xuất cho bạn</option><option value="price-asc" ${param.sort eq 'price-asc' ? 'selected' : ''}>Giá thấp đến cao</option><option value="price-desc" ${param.sort eq 'price-desc' ? 'selected' : ''}>Giá cao đến thấp</option><option value="area-desc" ${param.sort eq 'area-desc' ? 'selected' : ''}>Diện tích lớn trước</option></select></label>
</div>
<p class="field-help">Chọn ngày nhận và số tháng để chỉ xem các căn còn trống trong cả kỳ thuê.</p>
</details>
</c:if></form>
