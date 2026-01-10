<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <title>系统管理 - 操作日志</title>

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

        *{ box-sizing: border-box; }
        html, body{ height: 100%; }

        /* ✅ 右侧内容区：铺满父容器，不居中 */
        body{
            margin: 0;
            padding: 16px;
            font-family: "PingFang SC","Microsoft YaHei", Arial, sans-serif;
            background: var(--bg);
            color: var(--text);
            text-align: left;
        }

        /* ✅ container：占满右侧 div（去掉 max-width 和 auto 居中） */
        .container{
            width: 100%;
            max-width: none;
            margin: 0;
        }

        /* 卡片统一风格 */
        .card,
        .table-card{
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
        }

        /* 查询卡片 */
        .card{
            padding: 18px 18px 14px;
            text-align: left;
        }

        fieldset{
            border: 1px solid var(--border);
            border-radius: 10px;
            padding: 14px 14px 10px;
            margin: 0;
        }

        legend{
            padding: 0 8px;
            font-weight: 700;
            color: #111827;
            font-size: 14px;
        }

        .form-grid{
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px 18px;
            margin-top: 10px;
        }

        .form-item{
            display: grid;
            grid-template-columns: 100px 1fr;
            align-items: center;
            gap: 10px;
        }

        .form-item label{
            width: auto;
            font-weight: 600;
            color: #374151;
            text-align: right;
            white-space: nowrap;
        }

        input[type="text"],
        input[type="datetime-local"]{
            width: 100%;
            height: 36px;
            padding: 0 10px;
            border: 1px solid var(--border);
            border-radius: 10px;
            background: #fff;
            color: #111827;
            outline: none;
            transition: box-shadow .12s ease, border-color .12s ease;
        }

        input[type="text"]:focus,
        input[type="datetime-local"]:focus{
            border-color: rgba(37,99,235,.45);
            box-shadow: 0 0 0 4px rgba(37,99,235,.18);
        }

        /* ✅ 查询/重置按钮：相对居中 */
        .form-actions{
            margin-top: 14px;
            display: flex;
            justify-content: center; /* 你要求的“相对居中” */
            gap: 12px;
        }

        button{
            height: 36px;
            padding: 0 16px;
            border-radius: 10px;
            border: 1px solid var(--border);
            background: #fff;
            color: #111827;
            cursor: pointer;
            font-weight: 700;
            box-shadow: 0 2px 10px rgba(17,24,39,.06);
            transition: transform .12s ease, background .12s ease, box-shadow .12s ease, border-color .12s ease;
        }

        button:hover{
            background: var(--hover);
            transform: translateY(-1px);
            box-shadow: 0 10px 22px rgba(17,24,39,.08);
        }

        button[type="submit"]{
            border-color: rgba(37,99,235,.25);
            background: var(--primary-weak);
            color: var(--primary);
        }

        button[type="submit"]:hover{
            background: rgba(37,99,235,.16);
            border-color: rgba(37,99,235,.35);
        }

        /* 表格卡片 */
        .table-card{
            margin-top: 16px;
            padding: 14px 14px 10px;
        }

        table{
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            background: #fff;
            border: 1px solid var(--border);
            border-radius: 10px;
            overflow: hidden;
        }

        th, td{
            padding: 12px 14px;
            border-bottom: 1px solid var(--border);
            text-align: center;
            font-size: 14px;
            white-space: nowrap;
        }

        thead th{
            background: var(--header);
            font-weight: 700;
            color: #374151;
            font-size: 13px;
            border-bottom: 1px solid var(--border);
        }

        tbody tr:last-child td{
            border-bottom: none;
        }

        tbody tr:hover td{
            background: var(--hover);
        }

        /* 分页控件 */
        .pager{
            margin-top: 12px;
            display: flex;
            gap: 10px;
            align-items: center;
            justify-content: flex-start; /* 与铺满布局更匹配 */
            flex-wrap: wrap;
        }

        .pager button{
            height: 34px;
            padding: 0 14px;
            border-radius: 10px;
            border: 1px solid var(--border);
            background: #fff;
            cursor: pointer;
            font-weight: 600;
            box-shadow: 0 2px 10px rgba(17,24,39,.06);
        }

        .pager button:disabled{
            opacity: .5;
            cursor: not-allowed;
            box-shadow: none;
            transform: none;
        }

        .pager select, .pager input{
            height: 34px;
            padding: 0 10px;
            border: 1px solid var(--border);
            border-radius: 10px;
            background: #fff;
            color: #111827;
        }

        #pageInfo{
            color: var(--muted);
            font-size: 13px;
            padding: 0 6px;
        }

        @media (max-width: 900px){
            .form-grid{ grid-template-columns: 1fr; }
            .form-item{ grid-template-columns: 1fr; }
            .form-item label{ text-align: left; }
            table{ display: block; overflow-x: auto; -webkit-overflow-scrolling: touch; }
            th, td{ text-align: left; }
        }
    </style>
</head>

<body>

<div class="container">

    <!-- 查询表单 -->
    <div class="card">
        <form action="${pageContext.request.contextPath}/superadmin/systemManage/queryLogs" method="get">
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
        const tbody = document.getElementById("logTbody");

        function parseTime(text){
            const m = text.match(
                /^(\d{4})-(\d{2})-(\d{2})[ T](\d{2}):(\d{2})(?::(\d{2}))?/
            );
            if(!m) return 0;
            return new Date(
                m[1], m[2]-1, m[3],
                m[4], m[5], m[6]||0
            ).getTime();
        }

        // ✅ 时间倒序
        rows.sort((a,b)=>{
            return parseTime(b.cells[0].innerText) -
                parseTime(a.cells[0].innerText);
        });
        rows.forEach(r=>tbody.appendChild(r));
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