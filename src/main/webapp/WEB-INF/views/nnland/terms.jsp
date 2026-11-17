<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<!doctype html>
<html lang="vi"><head><jsp:include page="/WEB-INF/views/nnland/common/head.jsp"/></head><body><a class="skip-link" href="#main-content">Đến nội dung chính</a><jsp:include page="/WEB-INF/views/nnland/common/header.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/flash.jsp"/><main id="main-content"><section class="container narrow section-space"><span class="eyebrow">THÔNG TIN TRƯỚC KHI THUÊ</span><h1>Điều khoản thuê căn hộ</h1>
<p class="lead">Chúng tôi muốn mọi chi phí và trách nhiệm đều rõ ràng ngay từ đầu.</p>
<article class="panel policy-text"><c:out value="${terms}"/></article>
<div class="notice">Ngày kết thúc kỳ thuê là ngày bàn giao trả căn hộ và không tính thêm tiền thuê của ngày đó. Một tháng thuê được tính theo tháng lịch, ví dụ 31/01 đến 28/02.</div>
<a class="button primary" href="${ctx}/can-ho">Khám phá căn hộ →</a></section>
</main>
<jsp:include page="/WEB-INF/views/nnland/common/footer.jsp"/><jsp:include page="/WEB-INF/views/nnland/common/scripts.jsp"/></body></html>