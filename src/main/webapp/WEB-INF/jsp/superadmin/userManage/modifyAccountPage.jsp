<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>账号管理</title>
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
            margin: 20px 8px 10px;
            padding: 6px 18px;
            border: 1px solid #ccc;
            border-radius: 4px;
            text-decoration: none;
            color: #333;
            background-color: #fff;
        }

        .tab-button:hover {
            background-color: #f2f2f2;
        }

        .tab-button.active {
            font-weight: bold;
            border-color: #999;
        }

        h2 {
            margin: 20px 0;
        }

        table {
            margin: 0 auto 30px;
            border-collapse: collapse;
            background-color: #fff;
            min-width: 760px;
            box-shadow: 0 6px 18px rgba(0,0,0,.06);
        }

        th, td {
            border: 1px solid #ddd;
            padding: 10px 14px;
            text-align: center;
            font-size: 14px;
        }

        th {
            background-color: #f3f4f6;
            font-weight: bold;
        }

        tr:hover td {
            background-color: #fafafa;
        }

        td a {
            text-decoration: none;
            color: #333;
            padding: 4px 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        td a:hover {
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
        <th>邮箱</th>
        <th>账号类型</th>
        <th>状态</th>
        <th>操作</th>
    </tr>

    <c:forEach var="user" items="${userList}">
        <tr>
            <td>${user.userName}</td>
            <td>${user.fullName}</td>
            <td>${user.email}</td>
            <td>${user.userType}</td>
            <td>${user.status}</td>
            <td>
                <a href="${pageContext.request.contextPath}/superadmin/userManage/edit?userId=${user.userId}">
                    编辑
                </a>
            </td>
        </tr>
    </c:forEach>

</table>

</body>
</html>