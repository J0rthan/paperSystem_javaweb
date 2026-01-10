<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>删除账号页</title>
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

            --danger: #dc2626;
            --danger-weak: rgba(220,38,38,.10);

            --shadow: 0 10px 30px rgba(17,24,39,.08);
            --radius: 12px;
        }

        *{ box-sizing: border-box; }
        html, body{ height: 100%; }

        /* ✅ iframe/右侧内容区：不要居中，铺满父容器 */
        body{
            margin: 0;
            font-family: "PingFang SC","Microsoft YaHei", Arial, sans-serif;
            background: var(--bg);
            color: var(--text);
            text-align: left;
            padding: 16px;
        }

        /* ✅ 表格：铺满，不再 min-width，不再 auto 居中 */
        #userTable{
            width: 100% !important;
            margin: 0 !important;
            border-collapse: separate !important;
            border-spacing: 0 !important;
            background: var(--card) !important;
            border: 1px solid var(--border) !important;
            border-radius: var(--radius);
            overflow: hidden;
            box-shadow: var(--shadow);
        }

        /* ✅ 覆盖你 th/td 上的 inline 样式：统一细线与间距 */
        #userTable th,
        #userTable td{
            border: none !important;
            border-bottom: 1px solid var(--border) !important;
            padding: 12px 14px !important;
            text-align: center;
            font-size: 14px;
            white-space: nowrap;
        }

        #userTable thead th{
            background: var(--header) !important;
            color: #374151;
            font-weight: 700;
            font-size: 13px;
        }

        #userTable tbody tr:last-child td{
            border-bottom: none !important;
        }

        #userTable tbody tr:hover td{
            background: var(--hover);
        }

        /* 删除按钮：危险按钮风格 */
        #userTable button[type="submit"]{
            height: 32px;
            padding: 0 12px;
            border-radius: 999px;
            border: 1px solid rgba(220,38,38,.25);
            background: var(--danger-weak);
            color: var(--danger);
            font-weight: 700;
            cursor: pointer;
            transition: transform .12s ease, background .12s ease, border-color .12s ease;
        }

        #userTable button[type="submit"]:hover{
            background: rgba(220,38,38,.16);
            border-color: rgba(220,38,38,.35);
            transform: translateY(-1px);
        }

        #userTable button[type="submit"]:active{
            transform: translateY(0);
        }

        /* 分页区控件统一 */
        #pageInfo{
            color: var(--muted);
            font-size: 13px;
            padding: 0 6px;
        }

        button, select, input[type="number"]{ font: inherit; }

        /* 只影响分页区的普通按钮 */
        #prevBtn, #nextBtn, #gotoBtn{
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

        #prevBtn:hover, #nextBtn:hover, #gotoBtn:hover{
            background: #f9fafb;
            transform: translateY(-1px);
            box-shadow: 0 10px 22px rgba(17,24,39,.08);
        }

        #prevBtn:disabled, #nextBtn:disabled{
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

        /* focus 更清晰 */
        #prevBtn:focus-visible, #nextBtn:focus-visible, #gotoBtn:focus-visible,
        select:focus-visible, input[type="number"]:focus-visible,
        #userTable button[type="submit"]:focus-visible{
            outline: none;
            box-shadow: 0 0 0 4px rgba(37,99,235,.18);
            border-color: rgba(37,99,235,.45);
        }

        /* 小屏：表格可横向滚动避免挤坏 */
        @media (max-width: 900px){
            body{ padding: 12px; }
            #userTable{
                display: block;
                overflow-x: auto;
                -webkit-overflow-scrolling: touch;
            }
            #userTable th, #userTable td{
                text-align: left;
            }
        }
    </style>
</head>

<body>

<table id="userTable" style="width:100%;border-collapse:collapse;background:#fff;">
    <thead>
    <tr>
        <th style="border:1px solid #ddd;padding:8px;">ID</th>
        <th style="border:1px solid #ddd;padding:8px;">账号名</th>
        <th style="border:1px solid #ddd;padding:8px;">角色</th>
        <th style="border:1px solid #ddd;padding:8px;">邮箱</th>
        <th style="border:1px solid #ddd;padding:8px;">全名</th>
        <th style="border:1px solid #ddd;padding:8px;">公司</th>
        <th style="border:1px solid #ddd;padding:8px;">就业方向</th>
        <th style="border:1px solid #ddd;padding:8px;">操作</th>
    </tr>
    </thead>

    <tbody id="userTbody">
    <c:forEach items="${userList}" var="u">
        <tr class="user-row">
            <td style="border:1px solid #ddd;padding:8px;">${u.userId}</td>
            <td style="border:1px solid #ddd;padding:8px;">${u.userName}</td>
            <td style="border:1px solid #ddd;padding:8px;">${u.userType}</td>
            <td style="border:1px solid #ddd;padding:8px;">${u.email}</td>
            <td style="border:1px solid #ddd;padding:8px;">${u.fullName}</td>
            <td style="border:1px solid #ddd;padding:8px;">${u.company}</td>
            <td style="border:1px solid #ddd;padding:8px;">${u.investigationDirection}</td>
            <td style="border:1px solid #ddd;padding:8px;">
                <!-- delete 页面保留删除按钮；modify 页面保留编辑链接 -->
                <form action="<%=request.getContextPath()%>/superadmin/userManage/deleteAccount" method="post" style="display:inline;">
                    <input type="hidden" name="userId" value="${u.userId}">
                    <button type="submit" onclick="return confirm('确认删除？');">删除</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<!-- 分页控制区 -->
<div style="margin-top:14px;display:flex;gap:10px;align-items:center;justify-content:flex-start;flex-wrap:wrap;">
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

</body>

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

            // 关键：不要出现
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
</html>