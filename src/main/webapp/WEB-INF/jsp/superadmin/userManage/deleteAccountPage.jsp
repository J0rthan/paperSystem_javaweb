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

        .tab-button.active {
            background-color: #eaeaea;
            font-weight: bold;
        }

        h2 {
            margin: 20px 0;
        }

        table {
            margin: 0 auto 30px;
            border-collapse: collapse;
            background-color: #fff;
            min-width: 600px;
        }

        th, td {
            padding: 10px 16px;
            border: 1px solid #ddd;
            text-align: center;
        }

        th {
            background-color: #f0f2f5;
            font-weight: bold;
        }

        input[type="submit"] {
            padding: 4px 12px;
            border: 1px solid #ccc;
            border-radius: 4px;
            background-color: #fff;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #f2f2f2;
        }
    </style>
</head>

<body>

<h2>用户列表</h2>

<table>
    <tr>
        <th>账号</th>
        <th>姓名</th>
        <th>账号类型</th>
        <th>操作</th>
    </tr>

    <c:forEach var="user" items="${userList}">
        <tr>
            <td>${user.userName}</td>
            <td>${user.fullName}</td>
            <td>${user.userType}</td>
            <td>
                <form action="${pageContext.request.contextPath}/superadmin/userManage/deleteAccount"
                      method="post"
                      onsubmit="return confirm('确认删除该用户？此操作不可恢复');">
                    <input type="hidden" name="userId" value="${user.userId}">
                    <input type="submit" value="删除">
                </form>
            </td>
        </tr>
    </c:forEach>

</table>

</body>
</html>