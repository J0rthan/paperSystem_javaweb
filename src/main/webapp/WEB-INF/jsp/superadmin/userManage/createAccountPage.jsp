<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>创建账号页</title>

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
            margin-top: 20px;
        }

        form {
            width: 420px;
            margin: 20px auto;
            padding: 20px;
            background-color: #ffffff;
            border-radius: 8px;
            border: 1px solid #ddd;
            text-align: left;
        }

        form div {
            margin-bottom: 12px;
        }

        label {
            display: inline-block;
            width: 90px;
        }

        input, select {
            width: 240px;
            padding: 6px;
        }

        button {
            padding: 6px 16px;
            margin-right: 10px;
            border-radius: 4px;
            border: 1px solid #ccc;
            background-color: #fff;
            cursor: pointer;
        }

        button:hover {
            background-color: #eaeaea;
        }
    </style>
</head>

<body>

<h2>创建账号</h2>

<form action="<%= request.getContextPath() %>/superadmin/userManage/createAccount"
      method="post">

    <div>
        <label for="userName">账号名：</label>
        <input id="userName" name="userName" type="text" placeholder="请输入账号名" required>
    </div>

    <div>
        <label for="password">初始密码：</label>
        <input id="password" name="password" type="password" placeholder="请输入初始密码" required>
    </div>

    <div>
        <label for="role">角色：</label>
        <select id="role" name="userType" required>
            <option value="sys_admin" selected>系统管理员</option>
            <option value="option_admin">编辑部管理员</option>
            <option value="chief_editor">主编</option>
            <option value="editor">编辑</option>
            <option value="reviewer">审稿人</option>
            <option value="author">作者</option>
        </select>
    </div>

    <div>
        <label for="email">邮箱</label>
        <input type="email"
               id="email"
               name="email"
               placeholder="example@domain.com"
               pattern="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"
               required>
    </div>

    <div>
        <label for="fullName">全名</label>
        <input type="text"
               id="fullName"
               name="fullName"
               placeholder="请输入您的全名"
               required>
    </div>

    <div>
        <label for="company">公司</label>
        <input type="text"
               id="company"
               name="company"
               placeholder="请输入您的公司名"
               required>
    </div>

    <div>
        <label for="investigationDirection">就业方向</label>
        <input type="text"
               id="investigationDirection"
               name="investigationDirection"
               placeholder="请输入您的就业方向"
               required>
    </div>

    <div style="margin-top: 10px; text-align: center;">
        <button type="submit">创建用户</button>
        <button type="reset">重置</button>
    </div>
</form>

</body>
</html>