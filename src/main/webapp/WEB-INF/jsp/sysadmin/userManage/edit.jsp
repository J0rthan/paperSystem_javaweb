<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>修改账号</title>
    <meta charset="UTF-8">
    <style>
        body {
            margin: 0;
            font-family: "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
            background-color: #f6f7fb;
            color: #333;
            text-align: center;
        }

        h2 {
            margin: 30px 0 20px;
        }

        form {
            display: inline-block;
            text-align: left;
            background-color: #fff;
            padding: 24px 32px;
            border-radius: 8px;
            box-shadow: 0 6px 18px rgba(0,0,0,.06);
            min-width: 420px;
        }

        form > div {
            margin-bottom: 12px;
        }

        label {
            display: inline-block;
            width: 90px;
            font-weight: bold;
        }

        input[type="text"],
        input[type="password"],
        select {
            width: 240px;
            padding: 6px 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        /* 可选：注册时间通常不建议编辑 */
        input[readonly] {
            background-color: #f9fafb;
            color: #6b7280;
        }

        hr {
            margin: 20px 0;
            border: none;
            border-top: 1px solid #ddd;
        }

        input[type="submit"] {
            padding: 6px 18px;
            border: 1px solid #ccc;
            border-radius: 4px;
            background-color: #fff;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #f2f2f2;
        }

        a {
            margin-left: 12px;
            text-decoration: none;
            color: #333;
        }

        a:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<h2>修改账号</h2>

<form action="${pageContext.request.contextPath}/sysadmin/userManage/modifyAccount"
      method="post"
      onsubmit="return confirm('确认保存修改？');">

    <!-- 主键必须带回去 -->
    <input type="hidden" name="userId" value="${userInfo.userId}" />

    <div>
        <label>账号名称：</label>
        <input type="text" name="userName" value="${userInfo.userName}" />
    </div>

    <div>
        <label>用户全名：</label>
        <input type="text" name="fullName" value="${userInfo.fullName}" />
    </div>

    <div>
        <label>邮箱：</label>
        <input type="text" name="email" value="${userInfo.email}" />
    </div>

    <div>
        <div>
            <label>用户类型：</label>
            <select name="userType" required>
                <option value="option_admin" <c:if test="${userInfo.userType eq 'option_admin'}">selected</c:if>>编辑部管理员</option>
                <option value="chief_editor" <c:if test="${userInfo.userType eq 'chief_editor'}">selected</c:if>>主编</option>
                <option value="editor" <c:if test="${userInfo.userType eq 'editor'}">selected</c:if>>编辑</option>
                <option value="reviewer" <c:if test="${userInfo.userType eq 'reviewer'}">selected</c:if>>审稿人</option>
                <option value="author" <c:if test="${userInfo.userType eq 'author'}">selected</c:if>>作者</option>
            </select>
        </div>
    </div>

    <div>
        <label>单位：</label>
        <input type="text" name="company" value="${userInfo.company}" />
    </div>

    <div>
        <label>研究方向：</label>
        <input type="text" name="investigationDirection" value="${userInfo.investigationDirection}" />
    </div>

    <hr/>

    <div>
        <label>密码：</label>
        <input type="password" name="password" placeholder="不修改则留空" />
    </div>

    <div>
        <label>账号状态：</label>
        <select name="status">
            <option value="exist"
                    <c:if test="${userInfo.status == 'exist'}">selected</c:if>>
                启用账号
            </option>

            <option value="not_exist"
                    <c:if test="${userInfo.status == 'not_exist'}">selected</c:if>>
                禁用账号
            </option>
        </select>
    </div>

    <div style="margin-top: 16px; text-align: center;">
        <input type="submit" value="保存修改"/>
        <a href="${pageContext.request.contextPath}/sysadmin/userManage/modifyAccountPage">返回列表</a>
    </div>

</form>

</body>
</html>