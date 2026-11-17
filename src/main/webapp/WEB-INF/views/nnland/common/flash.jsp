<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<c:if test="${not empty success}"><div class="flash success" role="status"><svg class="icon"><use href="${ctx}/nnland/icons.svg#check"/></svg><span><c:out value="${success}"/></span><button type="button" data-dismiss aria-label="Đóng thông báo">×</button></div>
</c:if><c:if test="${not empty error}"><div class="flash error" role="alert"><c:out value="${error}"/></div>
</c:if>