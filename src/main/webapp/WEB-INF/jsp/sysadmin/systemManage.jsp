<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <title>系统管理 - 操作日志</title>

    <style>
        body {
            margin: 0;
            font-family: "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
            background-color: #f6f7fb;
            color: #333;
            text-align: center;
        }

        header {
            padding: 22px 0 10px;
        }

        header h1 {
            margin: 0 0 6px;
            font-size: 22px;
        }

        header p {
            margin: 0;
            color: #666;
            font-size: 14px;
        }

        .container {
            width: 92%;
            max-width: 1100px;
            margin: 16px auto 30px;
        }

        /* 查询卡片 */
        .card {
            background: #fff;
            border: 1px solid #ddd;
            border-radius: 8px;
            box-shadow: 0 6px 18px rgba(0,0,0,.06);
            padding: 18px 18px 14px;
            text-align: left;
        }

        fieldset {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 14px 14px 10px;
            margin: 0;
        }

        legend {
            padding: 0 8px;
            font-weight: bold;
            color: #333;
        }

        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px 18px;
            margin-top: 10px;
        }

        .form-item label {
            display: inline-block;
            width: 90px;
            font-weight: bold;
        }

        input[type="text"],
        input[type="datetime-local"] {
            width: 240px;
            padding: 6px 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            background: #fff;
        }

        .form-actions {
            margin-top: 14px;
            text-align: center;
        }

        button {
            padding: 6px 16px;
            margin-right: 10px;
            border-radius: 4px;
            border: 1px solid #ccc;
            background-color: #fff;
            cursor: pointer;
        }

        button:hover { background-color: #eaeaea; }

        /* 表格卡片 */
        .table-card {
            margin-top: 16px;
            background: #fff;
            border-radius: 8px;
            border: 1px solid #ddd;
            box-shadow: 0 6px 18px rgba(0,0,0,.06);
            padding: 14px 14px 10px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: #fff;
        }

        th, td {
            padding: 10px 14px;
            border: 1px solid #ddd;
            text-align: center;
            font-size: 14px;
        }

        th {
            background-color: #f0f2f5;
            font-weight: bold;
        }

        tr:hover td { background-color: #fafafa; }

        /* 分页控件 */
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

        .pager {
            margin-top: 12px;
            display: flex;
            gap: 10px;
            align-items: center;
            justify-content: center;
            flex-wrap: wrap;
        }

        @media (max-width: 820px) {
            .form-grid { grid-template-columns: 1fr; }
            input[type="text"], input[type="datetime-local"] { width: 100%; }
            .form-item label { width: 90px; }
        }
    </style>
</head>

<body>

<header>
    <h1>系统管理</h1>
    <p>查看操作日志，支持按时间、用户及操作关键字筛选，用于故障排查与审计追溯</p>
</header>

<div class="container">

    <!-- 查询表单 -->
    <div class="card">
        <form action="${pageContext.request.contextPath}/sysadmin/systemManage/queryLogs" method="get">
            <fieldset>
                <legend>日志查询条件</legend>

                <div class="form-grid">
                    <div class="form-item">
                        <label>操作时间：</label>
                        <input type="datetime-local" name="opTime" step="1">
                    </div>

                    <div class="form-item">
                        <label>操作人ID：</label>
                        <input type="text" name="oporId" placeholder="输入操作人ID">
                    </div>

                    <div class="form-item">
                        <label>操作类型：</label>
                        <input type="text" name="opType" placeholder="输入操作类型">
                    </div>

                    <div class="form-item">
                        <label>稿件ID：</label>
                        <input type="text" name="paperId" placeholder="请输入稿件ID">
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit">查询</button>
                    <button type="reset">重置</button>
                </div>
            </fieldset>
        </form>
    </div>

    <!-- 查询结果表格 -->
    <div class="table-card">
        <table>
            <thead>
            <tr>
                <th>操作时间</th>
                <th>操作人ID</th>
                <th>操作类型</th>
                <th>稿件ID</th>
            </tr>
            </thead>

            <tbody id="logTbody">
            <!-- 兼容：优先 requestScope.logList，没有则使用 sessionScope.logList -->
            <c:set var="logs" value="${not empty requestScope.logList ? requestScope.logList : sessionScope.logList}" />

            <c:if test="${empty logs}">
                <tr>
                    <td colspan="4">暂无日志数据</td>
                </tr>
            </c:if>

            <c:forEach var="log" items="${logs}">
                <tr class="log-row">
                    <td><c:out value="${log.opTime}"/></td>
                    <td><c:out value="${log.oporId}"/></td>
                    <td><c:out value="${log.opType}"/></td>
                    <td><c:out value="${log.paperId}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <!-- 分页控制区 -->
        <div class="pager">
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
    </div>

</div>

<script>
    (function () {
        const rows = Array.from(document.querySelectorAll(".log-row"));
        const prevBtn = document.getElementById("prevBtn");
        const nextBtn = document.getElementById("nextBtn");
        const pageInfo = document.getElementById("pageInfo");
        const pageSizeSelect = document.getElementById("pageSizeSelect");
        const gotoInput = document.getElementById("gotoInput");
        const gotoBtn = document.getElementById("gotoBtn");

        // 如果没有数据行（只有“暂无日志数据”那一行），分页不工作，直接退出
        if (rows.length === 0) {
            pageInfo.textContent = "";
            prevBtn.disabled = true;
            nextBtn.disabled = true;
            gotoBtn.disabled = true;
            pageSizeSelect.disabled = true;
            gotoInput.disabled = true;
            return;
        }

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

        gotoInput.addEventListener("keydown", function (e) {
            if (e.key === "Enter") gotoBtn.click();
        });

        render();
    })();
</script>

</body>
</html>