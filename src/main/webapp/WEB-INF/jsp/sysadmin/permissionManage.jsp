<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>权限管理</title>
    <meta charset="UTF-8">
    <style>
        :root{
            --bg:#f6f7fb; --card:#fff; --text:#1f2937; --muted:#6b7280;
            --border:#e5e7eb; --header:#f3f4f6; --hover:#f9fafb;
            --primary:#2563eb; --primary-weak: rgba(37,99,235,.10);
            --shadow:0 10px 30px rgba(17,24,39,.08); --radius:12px;
        }
        *{box-sizing:border-box;}
        body{
            margin:0; padding:16px;
            font-family:"PingFang SC","Microsoft YaHei",Arial,sans-serif;
            background:var(--bg); color:var(--text);
        }
        h2{margin:0 0 18px; font-size:22px; font-weight:700; color:#111827;}

        table{
            width:100%;
            border-collapse:separate;
            border-spacing:0;
            background:var(--card);
            border:1px solid var(--border);
            border-radius:var(--radius);
            overflow:hidden;
            box-shadow:var(--shadow);
        }
        th,td{
            padding:12px 14px;
            text-align:center;
            font-size:14px;
            border-bottom:1px solid var(--border);
            border-right:1px solid var(--border);
            white-space:nowrap;
        }
        tr td:last-child, tr th:last-child{border-right:none;}
        th{background:var(--header); font-weight:700; color:#374151; font-size:13px;}
        tbody tr:last-child td{border-bottom:none;}
        tbody tr:hover td{background:var(--hover);}

        a.action{
            display:inline-flex; align-items:center; justify-content:center;
            height:32px; padding:0 12px;
            text-decoration:none; color:var(--primary);
            border:1px solid rgba(37,99,235,.25);
            background:var(--primary-weak);
            border-radius:999px; font-weight:600; font-size:13px;
            transition:transform .12s ease, background .12s ease, border-color .12s ease;
        }
        a.action:hover{
            background:rgba(37,99,235,.16);
            border-color:rgba(37,99,235,.35);
            transform:translateY(-1px);
        }

        #pageInfo{color:var(--muted); font-size:13px; padding:0 6px;}
        button,select,input[type="number"]{font:inherit;}
        button{
            height:34px; padding:0 14px;
            border-radius:10px;
            border:1px solid var(--border);
            background:#fff; color:#111827;
            cursor:pointer;
            box-shadow:0 2px 10px rgba(17,24,39,.06);
            transition:background .12s ease, transform .12s ease, box-shadow .12s ease;
        }
        button:hover{background:#f9fafb; transform:translateY(-1px); box-shadow:0 10px 22px rgba(17,24,39,.08);}
        button:disabled{opacity:.45; cursor:not-allowed; transform:none; box-shadow:none;}
        select{
            height:34px; border-radius:10px;
            border:1px solid var(--border);
            padding:0 10px; background:#fff; color:#111827;
        }
        input[type="number"]{
            height:34px; border-radius:10px;
            border:1px solid var(--border);
            padding:0 10px; background:#fff; color:#111827;
            outline:none; width:80px;
        }
        button:focus-visible, select:focus-visible, input[type="number"]:focus-visible, a.action:focus-visible{
            outline:none;
            box-shadow:0 0 0 4px rgba(37,99,235,.18);
            border-color:rgba(37,99,235,.45);
        }
    </style>
</head>

<body>

<h2>权限管理</h2>

<table>
    <thead>
    <tr>
        <th>user_id</th>
        <th>username</th>
        <th>用户类型</th>
        <th>具体权限</th>
    </tr>
    </thead>

    <tbody id="userTbody">
    <c:if test="${empty userList}">
        <tr>
            <td colspan="3" style="text-align:center; padding:16px; color:#6b7280; font-weight:600;">
                暂无用户数据
            </td>
        </tr>
    </c:if>

    <c:forEach var="user" items="${userList}">
        <tr class="user-row">
            <td>${user.userId}</td>
            <td>${user.userName}</td>
            <td>${user.userType}</td>
            <td>
                <a class="action"
                   href="${pageContext.request.contextPath}/sysadmin/permissionManage/detail?userId=${user.userId}">
                    查看 / 编辑
                </a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<!-- 分页区（按你模板） -->
<div style="margin-top:14px; display:flex; gap:10px; align-items:center; justify-content:flex-start; flex-wrap:wrap;">
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
    <input id="gotoInput" type="number" min="1" placeholder="页码">
    <button type="button" id="gotoBtn">GO</button>
</div>

<script>
    (function () {
        const rows = Array.from(document.querySelectorAll("#userTbody tr.user-row"));

        const prevBtn = document.getElementById("prevBtn");
        const nextBtn = document.getElementById("nextBtn");
        const pageInfo = document.getElementById("pageInfo");
        const pageSizeSelect = document.getElementById("pageSizeSelect");
        const gotoInput = document.getElementById("gotoInput");
        const gotoBtn = document.getElementById("gotoBtn");

        if (!prevBtn || !nextBtn || !pageInfo || !pageSizeSelect || !gotoInput || !gotoBtn) {
            console.error("分页组件缺失：请检查 prevBtn/nextBtn/pageInfo/pageSizeSelect/gotoInput/gotoBtn 是否存在");
            return;
        }

        let pageSize = parseInt(pageSizeSelect.value, 10) || 10;
        let currentPage = 1;

        function getTotalPages() {
            return Math.max(1, Math.ceil(rows.length / pageSize));
        }

        function clampPage(p) {
            const tp = getTotalPages();
            if (!Number.isFinite(p)) return 1;
            if (p < 1) return 1;
            if (p > tp) return tp;
            return p;
        }

        function render() {
            const tp = getTotalPages();
            currentPage = clampPage(currentPage);

            const start = (currentPage - 1) * pageSize;
            const end = start + pageSize;

            rows.forEach((tr, idx) => {
                tr.style.display = (idx >= start && idx < end) ? "" : "none";
            });

            pageInfo.textContent = "共 " + rows.length + " 条，第 " + currentPage + " / " + tp + " 页";

            const noData = (rows.length === 0);
            prevBtn.disabled = noData ? true : (currentPage === 1);
            nextBtn.disabled = noData ? true : (currentPage === tp);
            gotoBtn.disabled = noData;
            gotoInput.disabled = noData;
            pageSizeSelect.disabled = noData;
        }

        prevBtn.addEventListener("click", () => {
            currentPage = clampPage(currentPage - 1);
            render();
        });

        nextBtn.addEventListener("click", () => {
            currentPage = clampPage(currentPage + 1);
            render();
        });

        pageSizeSelect.addEventListener("change", () => {
            pageSize = parseInt(pageSizeSelect.value, 10) || 10;
            currentPage = 1;
            render();
        });

        function gotoPage() {
            const p = parseInt(gotoInput.value, 10);
            if (!Number.isFinite(p)) {
                gotoInput.value = "";
                return;
            }
            currentPage = clampPage(p);
            render();
        }

        gotoBtn.addEventListener("click", gotoPage);

        gotoInput.addEventListener("keydown", (e) => {
            if (e.key === "Enter") {
                e.preventDefault();
                gotoPage();
            }
        });

        render();
    })();
</script>

</body>
</html>