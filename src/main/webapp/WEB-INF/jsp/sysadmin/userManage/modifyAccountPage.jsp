<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>修改账号</title>
    <meta charset="UTF-8">
    <style>
        :root{
            --bg: #f6f7fb;
            --card: #ffffff;
            --text: #1f2937;
            --muted: #6b7280;
            --border: #e5e7eb;
            --header: #f3f4f6;
            --hover: #f9fafb;
            --primary: #2563eb;
            --primary-weak: rgba(37,99,235,.10);
            --shadow: 0 10px 30px rgba(17,24,39,.08);
            --radius: 12px;
        }

        * { box-sizing: border-box; }
        html, body { height: 100%; }

        body {
            margin: 0;
            font-family: "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
            background-color: var(--bg);
            color: var(--text);
            text-align: left;        /* ← 关键 */
            padding: 16px;           /* 适合 iframe 内边距 */
        }

        h2 {
            margin: 0 0 18px;
            font-size: 22px;
            font-weight: 700;
            letter-spacing: .2px;
            color: #111827;
        }

        /* 表格整体更像“卡片” */
        table {
            width: 100%;                 /* ← 填满右侧 div */
            margin: 0;                   /* ← 不再居中 */
            border-collapse: separate;
            border-spacing: 0;
            background-color: var(--card);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            overflow: hidden;
            box-shadow: var(--shadow);
        }

        th, td {
            padding: 12px 14px;
            text-align: center;
            font-size: 14px;
            border-bottom: 1px solid var(--border);
            border-right: 1px solid var(--border);
            white-space: nowrap;
        }

        tr td:last-child,
        tr th:last-child {
            border-right: none;
        }

        th {
            background-color: var(--header);
            font-weight: 700;
            color: #374151;
            font-size: 13px;
        }

        /* 最后一行不要底边 */
        tbody tr:last-child td {
            border-bottom: none;
        }

        /* 行 hover 更柔和 */
        tbody tr:hover td {
            background-color: var(--hover);
        }

        /* 操作链接：做成按钮样式 */
        td a {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            height: 32px;
            padding: 0 12px;
            text-decoration: none;
            color: var(--primary);
            border: 1px solid rgba(37,99,235,.25);
            background: var(--primary-weak);
            border-radius: 999px;
            font-weight: 600;
            font-size: 13px;
            transition: transform .12s ease, background .12s ease, border-color .12s ease;
        }

        td a:hover {
            background: rgba(37,99,235,.16);
            border-color: rgba(37,99,235,.35);
            transform: translateY(-1px);
        }

        /* 分页区：统一控件风格（不改你HTML，只覆盖默认样式） */
        #pageInfo{
            color: var(--muted);
            font-size: 13px;
            padding: 0 6px;
        }

        button, select, input[type="number"]{
            font: inherit;
        }

        button{
            height: 34px;
            padding: 0 14px;
            border-radius: 10px;
            border: 1px solid var(--border);
            background: #fff;
            color: #111827;
            cursor: pointer;
            box-shadow: 0 2px 10px rgba(17,24,39,.06);
            transition: background .12s ease, transform .12s ease, box-shadow .12s ease;
        }

        button:hover{
            background: #f9fafb;
            transform: translateY(-1px);
            box-shadow: 0 10px 22px rgba(17,24,39,.08);
        }

        button:active{
            transform: translateY(0);
            box-shadow: 0 2px 10px rgba(17,24,39,.06);
        }

        button:disabled{
            opacity: .45;
            cursor: not-allowed;
            transform: none;
            box-shadow: none;
        }

        select{
            height: 34px;
            border-radius: 10px;
            border: 1px solid var(--border);
            padding: 0 10px;
            background: #fff;
            color: #111827;
        }

        input[type="number"]{
            height: 34px;
            border-radius: 10px;
            border: 1px solid var(--border);
            padding: 0 10px;
            background: #fff;
            color: #111827;
            outline: none;
        }

        /* 聚焦更明显一点 */
        button:focus-visible,
        select:focus-visible,
        input[type="number"]:focus-visible,
        td a:focus-visible{
            outline: none;
            box-shadow: 0 0 0 4px rgba(37,99,235,.18);
            border-color: rgba(37,99,235,.45);
        }
    </style>
</head>

<body>

<h2>用户列表</h2>

<table>
    <tr>
        <th>账号</th>
        <th>姓名</th>
        <th>邮箱</th>
        <th>账号类型</th>
        <th>状态</th>
        <th>操作</th>
    </tr>

    <c:forEach var="user" items="${userList}">
        <tr class="user-row">
            <td><c:out value="${user.userName}"/></td>
            <td><c:out value="${user.fullName}"/></td>
            <td><c:out value="${user.email}"/></td>
            <td><c:out value="${user.userType}"/></td>
            <td><c:out value="${user.status}"/></td>
            <td>
                <a href="${pageContext.request.contextPath}/sysadmin/userManage/edit?userId=${user.userId}">
                    编辑
                </a>
            </td>
        </tr>
    </c:forEach>
</table>

<!-- 前端分页控件（与你给的风格一致） -->
<div style="
    margin-top:14px;
    display:flex;
    gap:10px;
    align-items:center;
    justify-content:flex-start;   <!-- 原来是 center -->
    flex-wrap:wrap;
">
    <button type="button" id="prevBtn">上一页</button>
    <span id="pageInfo"></span>
    <button type="button" id="nextBtn">下一页</button>

    <span style="margin-left:10px;">每页</span>
    <select id="pageSizeSelect">
        <option value="5">5</option>
        <option value="10" selected>10</option>
        <option value="20">20</option>
        <option value="50">50</option>
    </select>
    <span>条</span>

    <span style="margin-left:10px;">跳转到</span>
    <input id="gotoInput" type="number" min="1" style="width:70px;">
    <button type="button" id="gotoBtn">GO</button>
</div>

<script>
    (function () {
        const rows = Array.from(document.querySelectorAll(".user-row"));
        const prevBtn = document.getElementById("prevBtn");
        const nextBtn = document.getElementById("nextBtn");
        const pageInfo = document.getElementById("pageInfo");
        const pageSizeSelect = document.getElementById("pageSizeSelect");
        const gotoInput = document.getElementById("gotoInput");
        const gotoBtn = document.getElementById("gotoBtn");

        let pageSize = parseInt(pageSizeSelect.value, 10);
        let currentPage = 1;

        function totalPages() {
            return Math.max(1, Math.ceil(rows.length / pageSize));
        }

        function render() {
            const tp = totalPages();
            if (currentPage > tp) currentPage = tp;
            if (currentPage < 1) currentPage = 1;

            const start = (currentPage - 1) * pageSize;
            const end = start + pageSize;

            rows.forEach((tr, idx) => {
                tr.style.display = (idx >= start && idx < end) ? "" : "none";
            });

            pageInfo.textContent = "共 " + rows.length + " 条，第 " + currentPage + " / " + tp + " 页";
            prevBtn.disabled = (currentPage === 1);
            nextBtn.disabled = (currentPage === tp);
        }

        prevBtn.addEventListener("click", function () {
            currentPage--;
            render();
        });

        nextBtn.addEventListener("click", function () {
            currentPage++;
            render();
        });

        pageSizeSelect.addEventListener("change", function () {
            pageSize = parseInt(pageSizeSelect.value, 10);
            currentPage = 1;
            render();
        });

        gotoBtn.addEventListener("click", function () {
            const p = parseInt(gotoInput.value, 10);
            if (!isNaN(p)) {
                currentPage = p;
                render();
            }
        });

        render();
    })();
</script>

</body>
</html>