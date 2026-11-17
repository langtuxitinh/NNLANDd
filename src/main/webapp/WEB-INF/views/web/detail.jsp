<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><c:out value="${building.name}"/></title>
</head>
<body>
<%-- Khung giao dien (decorators/web.jsp) khong co <dec:head/> nen CSS phai nam trong <body> --%>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800&display=swap" rel="stylesheet">
<style>
    .bd {
        --bd-ink: #17302a;
        --bd-green: #1f8a5b;
        --bd-bright: #35BF76;
        --bd-mute: #5d7068;
        --bd-line: #dbe4df;
        --bd-wash: #eef4f0;
        font-family: "Be Vietnam Pro", "Segoe UI", Roboto, sans-serif;
        color: var(--bd-ink);
        line-height: 1.6;
        padding: 28px 0 72px;
    }
    .bd *, .bd *::before, .bd *::after { box-sizing: border-box; }
    .bd a { color: var(--bd-green); }
    .bd a:hover { color: var(--bd-ink); }
    .bd a:focus-visible, .bd .bd-btn:focus-visible { outline: 3px solid var(--bd-bright); outline-offset: 2px; }

    .bd-wrap { width: 100%; max-width: 1120px; margin: 0 auto; padding: 0 20px; }

    .bd-crumb { font-size: 14px; color: var(--bd-mute); margin-bottom: 28px; }
    .bd-crumb a { color: var(--bd-mute); text-decoration: none; }
    .bd-crumb a:hover { color: var(--bd-ink); text-decoration: underline; }
    .bd-crumb span[aria-current] { color: var(--bd-ink); }

    /* ---- Hero ---- */
    .bd-hero {
        display: grid;
        grid-template-columns: minmax(0, 1.15fr) minmax(0, 1fr);
        gap: 48px;
        align-items: center;
        padding-bottom: 44px;
        border-bottom: 1px solid var(--bd-line);
    }
    .bd-tag {
        display: inline-block;
        font-size: 14px; font-weight: 600;
        color: var(--bd-green);
        background: var(--bd-wash);
        padding: 4px 12px;
        border-radius: 4px;
        margin-bottom: 16px;
    }
    .bd-title {
        font-size: clamp(32px, 5vw, 52px);
        line-height: 1.1;
        font-weight: 800;
        letter-spacing: -0.02em;
        margin: 0 0 18px;
        color: var(--bd-ink);
    }
    .bd-addr { display: flex; gap: 10px; align-items: flex-start; font-size: 18px; color: var(--bd-mute); margin: 0; max-width: 34em; }
    .bd-addr svg { flex: none; margin-top: 5px; }
    .bd-lead { display: flex; flex-wrap: wrap; gap: 28px; margin-top: 30px; }
    .bd-lead div { min-width: 0; }
    .bd-lead dt { font-size: 14px; color: var(--bd-mute); font-weight: 500; }
    .bd-lead dd { margin: 2px 0 0; font-size: 20px; font-weight: 700; }

    .bd-figure { margin: 0; }
    .bd-figure svg, .bd-figure img { display: block; width: 100%; height: auto; }
    .bd-figure img { aspect-ratio: 4 / 3; object-fit: cover; border-radius: 4px; }
    .bd-figure figcaption { margin-top: 10px; font-size: 14px; color: var(--bd-mute); }

    /* ---- Noi dung ---- */
    .bd-body {
        display: grid;
        grid-template-columns: minmax(0, 1fr) 340px;
        gap: 56px;
        padding-top: 44px;
        align-items: start;
    }
    .bd-sec { margin-bottom: 44px; }
    .bd-sec:last-child { margin-bottom: 0; }
    .bd-sec h2 { font-size: 22px; font-weight: 700; margin: 0 0 6px; letter-spacing: -0.01em; }
    .bd-sec .bd-sub { margin: 0 0 14px; color: var(--bd-mute); font-size: 15px; }

    .bd-dl { margin: 0; border-top: 2px solid var(--bd-ink); }
    .bd-dl .bd-row {
        display: grid; grid-template-columns: 200px minmax(0, 1fr); gap: 16px;
        padding: 13px 0; border-bottom: 1px solid var(--bd-line);
    }
    .bd-dl dt { color: var(--bd-mute); font-weight: 500; }
    .bd-dl dd { margin: 0; font-weight: 600; overflow-wrap: anywhere; }

    .bd-chips { display: flex; flex-wrap: wrap; gap: 8px; margin: 0; padding: 0; list-style: none; }
    .bd-chips li {
        padding: 6px 14px; border: 1.5px solid var(--bd-green); color: var(--bd-green);
        border-radius: 4px; font-weight: 700;
    }
    .bd-note { margin: 0; padding-left: 18px; border-left: 4px solid var(--bd-bright); font-size: 17px; max-width: 40em; }

    /* ---- Cot lien he ---- */
    .bd-aside { position: sticky; top: 96px; background: var(--bd-wash); border-radius: 4px; padding: 28px; }
    .bd-price-label { font-size: 14px; color: var(--bd-mute); font-weight: 500; }
    .bd-price { font-size: 40px; font-weight: 800; line-height: 1.1; letter-spacing: -0.02em; margin: 2px 0 0; }
    .bd-price small { font-size: 16px; font-weight: 600; color: var(--bd-mute); letter-spacing: 0; margin-left: 4px; }
    .bd-price-desc { margin: 6px 0 0; font-size: 14px; color: var(--bd-mute); }
    .bd-hr { border: 0; border-top: 1px solid var(--bd-line); margin: 22px 0; }
    .bd-manager { margin: 0 0 16px; }
    .bd-manager b { display: block; font-size: 18px; }
    .bd-manager span { color: var(--bd-mute); font-size: 14px; }
    .bd-btn {
        display: block; width: 100%; text-align: center; text-decoration: none;
        padding: 13px 16px; border-radius: 4px; font-weight: 700; font-size: 16px;
        border: 2px solid var(--bd-green); transition: background-color .15s, color .15s;
    }
    .bd .bd-btn-main { background: var(--bd-green); color: #fff; }
    .bd .bd-btn-main:hover { background: var(--bd-ink); border-color: var(--bd-ink); color: #fff; }
    .bd .bd-btn-ghost { background: transparent; color: var(--bd-green); margin-top: 10px; }
    .bd .bd-btn-ghost:hover { background: var(--bd-green); color: #fff; }
    .bd-back { display: inline-block; margin-top: 18px; font-size: 15px; }

    @media (max-width: 900px) {
        .bd-hero { grid-template-columns: 1fr; gap: 28px; }
        .bd-body { grid-template-columns: 1fr; gap: 36px; }
        .bd-aside { position: static; }
    }
    @media (max-width: 560px) {
        .bd-dl .bd-row { grid-template-columns: 1fr; gap: 2px; }
        .bd-addr { font-size: 16px; }
    }
</style>

<%-- So tang ham: gioi han 0..5 de ve so do --%>
<c:set var="nb" value="${empty building.numberOfBasement ? 0 : building.numberOfBasement}"/>
<c:set var="nbDraw" value="${nb > 5 ? 5 : nb}"/>

<main class="bd">
<div class="bd-wrap">

    <nav class="bd-crumb" aria-label="Điều hướng">
        <a href="<c:url value='/trang-chu'/>">Trang chủ</a> /
        <a href="<c:url value='/san-pham'/>">Sản phẩm</a> /
        <span aria-current="page"><c:out value="${building.name}"/></span>
    </nav>

    <%-- ===== Hero ===== --%>
    <header class="bd-hero">
        <div>
            <c:if test="${not empty typeName or not empty building.typeCode}">
                <span class="bd-tag"><c:out value="${empty typeName ? building.typeCode : typeName}"/></span>
            </c:if>
            <h1 class="bd-title"><c:out value="${building.name}"/></h1>
            <p class="bd-addr">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 21s-7-6.2-7-11.5A7 7 0 0 1 19 9.5C19 14.8 12 21 12 21z"/><circle cx="12" cy="9.5" r="2.5"/></svg>
                <span>
                    <c:out value="${building.street}"/><c:if test="${not empty building.ward}">, <c:out value="${building.ward}"/></c:if><c:if test="${not empty building.district}">, <c:out value="${empty districtName ? building.district : districtName}"/></c:if>
                </span>
            </p>

            <dl class="bd-lead">
                <c:if test="${not empty building.floorArea}">
                    <div><dt>Diện tích sàn</dt><dd><c:out value="${building.floorArea}"/> m²</dd></div>
                </c:if>
                <c:if test="${not empty building.level}">
                    <div><dt>Hạng</dt><dd><c:out value="${building.level}"/></dd></div>
                </c:if>
                <c:if test="${not empty building.direction}">
                    <div><dt>Hướng</dt><dd><c:out value="${building.direction}"/></dd></div>
                </c:if>
            </dl>
        </div>

        <figure class="bd-figure">
            <c:choose>
                <c:when test="${not empty building.image}">
                    <img src="<c:url value='/repository${building.image}'/>" alt="Hình ảnh <c:out value='${building.name}'/>">
                    <figcaption><c:out value="${building.name}"/></figcaption>
                </c:when>
                <c:otherwise>
                    <%-- So do mat cat minh hoa: tang hem ve theo du lieu that (numberofbasement) --%>
                    <svg viewBox="0 0 360 ${274 + nbDraw * 24}" role="img" aria-label="Sơ đồ mặt cắt minh họa tòa nhà có ${nb} tầng hầm">
                        <defs>
                            <pattern id="bdWin" width="14" height="12" patternUnits="userSpaceOnUse">
                                <rect x="3" y="3" width="8" height="6" fill="#9fe3bf" opacity=".85"/>
                            </pattern>
                            <pattern id="bdHatch" width="8" height="8" patternUnits="userSpaceOnUse" patternTransform="rotate(45)">
                                <line x1="0" y1="0" x2="0" y2="8" stroke="#9db3a8" stroke-width="2"/>
                            </pattern>
                        </defs>
                        <rect x="0" y="0" width="360" height="250" fill="#eef4f0"/>
                        <line x1="180" y1="50" x2="180" y2="22" stroke="#17302a" stroke-width="3"/>
                        <rect x="110" y="50" width="140" height="200" fill="#17302a"/>
                        <rect x="110" y="50" width="140" height="200" fill="url(#bdWin)"/>
                        <rect x="62" y="150" width="48" height="100" fill="#2f5d49"/>
                        <rect x="62" y="150" width="48" height="100" fill="url(#bdWin)"/>
                        <rect x="250" y="182" width="46" height="68" fill="#2f5d49"/>
                        <rect x="250" y="182" width="46" height="68" fill="url(#bdWin)"/>
                        <rect x="166" y="222" width="28" height="28" fill="#35BF76"/>
                        <c:if test="${nbDraw > 0}">
                            <c:forEach begin="1" end="${nbDraw}" var="i">
                                <rect x="62" y="${250 + (i - 1) * 24}" width="234" height="24" fill="url(#bdHatch)" stroke="#9db3a8"/>
                                <text x="74" y="${250 + (i - 1) * 24 + 16}" font-size="12" font-weight="700" fill="#17302a" font-family="Be Vietnam Pro, sans-serif">B${i}</text>
                            </c:forEach>
                        </c:if>
                        <line x1="20" y1="250" x2="340" y2="250" stroke="#17302a" stroke-width="3"/>
                    </svg>
                    <figcaption>
                        Sơ đồ minh họa.
                        <c:choose>
                            <c:when test="${nb > 0}">Tòa nhà có <c:out value="${nb}"/> tầng hầm.</c:when>
                            <c:otherwise>Tòa nhà không có tầng hầm.</c:otherwise>
                        </c:choose>
                    </figcaption>
                </c:otherwise>
            </c:choose>
        </figure>
    </header>

    <%-- ===== Noi dung + cot lien he ===== --%>
    <div class="bd-body">
        <div>
            <section class="bd-sec" aria-labelledby="bd-h-info">
                <h2 id="bd-h-info">Thông tin tòa nhà</h2>
                <dl class="bd-dl">
                    <c:if test="${not empty building.structure}">
                        <div class="bd-row"><dt>Kết cấu</dt><dd><c:out value="${building.structure}"/></dd></div>
                    </c:if>
                    <c:if test="${not empty building.level}">
                        <div class="bd-row"><dt>Hạng</dt><dd><c:out value="${building.level}"/></dd></div>
                    </c:if>
                    <c:if test="${not empty building.direction}">
                        <div class="bd-row"><dt>Hướng</dt><dd><c:out value="${building.direction}"/></dd></div>
                    </c:if>
                    <div class="bd-row"><dt>Tầng hầm</dt><dd><c:out value="${nb}"/></dd></div>
                    <c:if test="${not empty building.floorArea}">
                        <div class="bd-row"><dt>Diện tích sàn</dt><dd><c:out value="${building.floorArea}"/> m²</dd></div>
                    </c:if>
                </dl>
            </section>

            <c:if test="${not empty building.rentArea}">
                <section class="bd-sec" aria-labelledby="bd-h-area">
                    <h2 id="bd-h-area">Diện tích có thể thuê</h2>
                    <p class="bd-sub">Các mặt bằng đang cho thuê, tính theo m².</p>
                    <ul class="bd-chips">
                        <c:forEach items="${fn:split(building.rentArea, ',')}" var="area">
                            <li><c:out value="${fn:trim(area)}"/> m²</li>
                        </c:forEach>
                    </ul>
                </section>
            </c:if>

            <section class="bd-sec" aria-labelledby="bd-h-cost">
                <h2 id="bd-h-cost">Chi phí</h2>
                <dl class="bd-dl">
                    <c:if test="${not empty building.rentPrice}">
                        <div class="bd-row"><dt>Giá thuê</dt><dd><c:out value="${building.rentPrice}"/> USD/m²</dd></div>
                    </c:if>
                    <c:if test="${not empty building.serviceFee}">
                        <div class="bd-row"><dt>Phí dịch vụ</dt><dd><c:out value="${building.serviceFee}"/></dd></div>
                    </c:if>
                    <c:if test="${not empty building.carFee}">
                        <div class="bd-row"><dt>Phí gửi ô tô</dt><dd><c:out value="${building.carFee}"/></dd></div>
                    </c:if>
                    <c:if test="${not empty building.motoFee}">
                        <div class="bd-row"><dt>Phí gửi xe máy</dt><dd><c:out value="${building.motoFee}"/></dd></div>
                    </c:if>
                    <c:if test="${not empty building.overtimeFee}">
                        <div class="bd-row"><dt>Phí ngoài giờ</dt><dd><c:out value="${building.overtimeFee}"/></dd></div>
                    </c:if>
                    <c:if test="${not empty building.electricityFee}">
                        <div class="bd-row"><dt>Tiền điện</dt><dd><c:out value="${building.electricityFee}"/></dd></div>
                    </c:if>
                    <c:if test="${building.brokerageFee > 0}">
                        <div class="bd-row"><dt>Phí môi giới</dt><dd><fmt:formatNumber value="${building.brokerageFee}" maxFractionDigits="1"/></dd></div>
                    </c:if>
                </dl>
            </section>

            <section class="bd-sec" aria-labelledby="bd-h-term">
                <h2 id="bd-h-term">Điều khoản thuê</h2>
                <dl class="bd-dl">
                    <c:if test="${not empty building.deposit}">
                        <div class="bd-row"><dt>Đặt cọc</dt><dd><c:out value="${building.deposit}"/></dd></div>
                    </c:if>
                    <c:if test="${not empty building.payment}">
                        <div class="bd-row"><dt>Thanh toán</dt><dd><c:out value="${building.payment}"/></dd></div>
                    </c:if>
                    <c:if test="${not empty building.rentTime}">
                        <div class="bd-row"><dt>Thời hạn thuê</dt><dd><c:out value="${building.rentTime}"/></dd></div>
                    </c:if>
                    <c:if test="${not empty building.decorationTime}">
                        <div class="bd-row"><dt>Thời gian trang trí</dt><dd><c:out value="${building.decorationTime}"/></dd></div>
                    </c:if>
                </dl>
            </section>

            <c:if test="${not empty building.note}">
                <section class="bd-sec" aria-labelledby="bd-h-note">
                    <h2 id="bd-h-note">Ghi chú</h2>
                    <p class="bd-note"><c:out value="${building.note}"/></p>
                </section>
            </c:if>
        </div>

        <aside class="bd-aside" aria-label="Giá thuê và liên hệ">
            <c:if test="${not empty building.rentPrice}">
                <div class="bd-price-label">Giá thuê</div>
                <p class="bd-price"><c:out value="${building.rentPrice}"/><small>USD/m²</small></p>
                <c:if test="${not empty building.rentPriceDescription}">
                    <p class="bd-price-desc"><c:out value="${building.rentPriceDescription}"/></p>
                </c:if>
                <hr class="bd-hr">
            </c:if>

            <c:if test="${not empty building.managerName or not empty building.managerPhone}">
                <p class="bd-manager">
                    <span>Người quản lý tòa nhà</span>
                    <b><c:out value="${building.managerName}"/></b>
                </p>
            </c:if>
            <c:if test="${not empty building.managerPhone}">
                <a class="bd-btn bd-btn-main" href="tel:<c:out value='${building.managerPhone}'/>">Gọi <c:out value="${building.managerPhone}"/></a>
            </c:if>
            <a class="bd-btn bd-btn-ghost" href="<c:url value='/lien-he'/>">Gửi yêu cầu tư vấn</a>
            <a class="bd-back" href="<c:url value='/san-pham'/>">Xem các tòa nhà khác</a>
        </aside>
    </div>

</div>
</main>
</body>
</html>
