<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>删除账号页</title>
    <meta charset="UTF-8">
    <style>
        body {
            margin: 0;
            font-family: "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
            background-color: #f6f7fb;
            color: #333;
            text-align: center;
        }

        .tab-button {
            display: inline-block;
            margin: 20px 10px;
            padding: 8px 18px;
            text-decoration: none;
            border: 1px solid #ccc;
            border-radius: 6px;
            color: #333;
            background-color: #fff;
        }

        .tab-button:hover { background-color: #f2f2f2; }

        .tab-button.active {
            background-color: #eaeaea;
            font-weight: bold;
        }

        h2 { margin: 20px 0; }

        table {
            margin: 0 auto 30px;
            border-collapse: collapse;
            background-color: #fff;
            min-width: 900px;
            box-shadow: 0 6px 18px rgba(0,0,0,.06);
        }

        th, td {
            padding: 10px 16px;
            border: 1px solid #ddd;
            text-align: center;
            font-size: 14px;
        }

        th {
            background-color: #f0f2f5;
            font-weight: bold;
        }

        tr:hover td { background-color: #fafafa; }

        button[type="submit"] {
            padding: 4px 12px;
            border: 1px solid #ccc;
            border-radius: 4px;
            background-color: #fff;
            cursor: pointer;
        }

        button[type="submit"]:hover {
            background-color: #f2f2f2;
        }

        .pager button {
            padding: 6px 12px;
            border: 1px solid #ccc;
            background: #fff;
            border-radius: 4px;
            cursor: pointer;
        }

        .pager button:disabled {
            opacity: .5;
            cursor: not-allowed;
        }

        .pager select, .pager input {
            padding: 6px 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            background: #fff;
        }
    </style>
</head>

<body>

<h2>用户列表</h2>

<table id="userTable">
    <thead>
    <tr>
        <th>ID</th>
        <th>账号名</th>
        <th>角色</th>
        <th>邮箱</th>
        <th>全名</th>
        <th>公司</th>
        <th>就业方向</th>
        <th>操作</th>
    </tr>
    </thead>

    <tbody id="userTbody">
    <c:forEach items="${userList}" var="u">
        <tr class="user-row">
            <td><c:out value="${u.userId}"/></td>
            <td><c:out value="${u.userName}"/></td>
            <td><c:out value="${u.userType}"/></td>
            <td><c:out value="${u.email}"/></td>
            <td><c:out value="${u.fullName}"/></td>
            <td><c:out value="${u.company}"/></td>
            <td><c:out value="${u.investigationDirection}"/></td>
            <td>
                <form action="${pageContext.request.contextPath}/sysadmin/userManage/deleteAccount"
                      method="post"
                      style="display:inline;"
                      onsubmit="return confirm('确认删除该用户？此操作不可恢复');">
                    <input type="hidden" name="userId" value="${u.userId}">
                    <button type="submit">删除</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<!-- 分页控制区 -->
<div class="pager"
     style="margin-top:14px;display:flex;gap:10px;align-items:center;justify-content:center;flex-wrap:wrap;">
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

        // 可选：回车触发跳转
        gotoInput.addEventListener("keydown", function (e) {
            if (e.key === "Enter") gotoBtn.click();
        });

        render();
    })();
</script>

</body>
</html>