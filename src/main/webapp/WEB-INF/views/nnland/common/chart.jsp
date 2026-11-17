<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nn" uri="https://nnland.local/functions" %>
<div class="chart" role="img" aria-label="Biểu đồ dòng tiền ròng và doanh thu phân bổ 12 tháng"><c:forEach var="m" items="${chart}"><div class="chart-column" title="${m.month_label}: Dòng tiền ${m.net}; Doanh thu phân bổ ${m.earned_total}"><div class="chart-bar ${m.cash_negative ? 'negative' : ''} ${m.cash_height eq 0 ? 'zero' : ''}" style="height:${m.cash_height}%"></div>
<div class="chart-bar secondary ${m.earned_height eq 0 ? 'zero' : ''}" style="height:${m.earned_height}%"></div>
</div>
</c:forEach></div>
<div class="chart-labels"><c:forEach var="m" items="${chart}"><span>${m.month_label}</span></c:forEach></div>
<div class="chart-legend"><span><i></i>Dòng tiền ròng</span><span><i class="secondary"></i>Doanh thu phân bổ</span></div>
