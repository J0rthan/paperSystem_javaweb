<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <title>系统管理 - 操作日志</title>
</head>

<body>

<header>
    <h1>系统管理</h1>
    <p>查看操作日志，支持按时间、用户及操作关键字筛选，用于故障排查与审计追溯</p>
</header>

<div class="container">

    <!-- 查询表单 -->
    <form action="${pageContext.request.contextPath}/superadmin/systemManage/queryLogs"
          method="get">

        <fieldset>
            <legend>日志查询条件</legend>

            <div>
                <label>操作时间：</label>
                <!-- datetime-local 提交格式：yyyy-MM-ddTHH:mm[:ss] -->
                <input type="datetime-local" name="opTime" step="1">
            </div>

            <div>
                <label>操作人ID：</label>
                <input type="text" name="oporId" placeholder="输入操作人ID">
            </div>

            <div>
                <label>操作类型：</label>
                <input type="text" name="opType" placeholder="输入操作类型">
            </div>

            <div>
                <label>稿件ID：</label>
                <input type="text" name="paperId" placeholder="请输入稿件ID">
            </div>

            <div>
                <button type="submit">查询</button>
                <button type="reset">重置</button>
            </div>
        </fieldset>

    </form>

    <!-- 查询结果表格 -->
    <table border="1" cellspacing="0" cellpadding="6">
        <thead>
        <tr>
            <th>操作时间</th>
            <th>操作人ID</th>
            <th>操作类型</th>
            <th>稿件ID</th>
        </tr>
        </thead>
        <tbody>

        <c:if test="${empty sessionScope.logList}">
            <tr>
                <td colspan="4">暂无日志数据</td>
            </tr>
        </c:if>

        <c:forEach var="log" items="${sessionScope.logList}">
            <tr>
                <td>${log.opTime}</td>
                <td>${log.oporId}</td>
                <td>${log.opType}</td>
                <td>${log.paperId}</td>
            </tr>
        </c:forEach>

        </tbody>
    </table>

</div>

</body>
</html>