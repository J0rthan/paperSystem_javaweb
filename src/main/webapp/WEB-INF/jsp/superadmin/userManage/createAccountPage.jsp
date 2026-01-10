<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>创建账号页</title>

    <style>
        :root{
            --bg: #f6f7fb;
            --card: #ffffff;
            --text: #1f2937;
            --muted: #6b7280;
            --border: #e5e7eb;
            --hover: #f9fafb;

            --primary: #2563eb;
            --primary-weak: rgba(37,99,235,.10);

            --shadow: 0 10px 30px rgba(17,24,39,.08);
            --radius: 12px;
        }

        *{ box-sizing: border-box; }
        html, body{ height: 100%; }

        body{
            margin: 0;
            padding: 16px;
            font-family: "PingFang SC","Microsoft YaHei", Arial, sans-serif;
            background: var(--bg);
            color: var(--text);
            text-align: left;
        }

        /* 标题（如果你保留 h2） */
        h2{
            margin: 0 0 14px;
            font-size: 18px;
            font-weight: 700;
            color: #111827;
        }

        .form-card{
            width: 100%;
            margin: 0;
            padding: 20px;
            background: #fff;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(17,24,39,.08);
        }

        /* 用网格排版，让表单看起来更“后台” */
        .form-card > div{
            display: grid;
            grid-template-columns: 110px 1fr;
            gap: 10px;
            margin-bottom: 14px;
            align-items: center;
        }


        label{
            width: auto;
            color: #374151;
            font-size: 14px;
            font-weight: 600;
            text-align: right;
        }

        input, select{
            width: 100%;
            height: 38px;
            padding: 0 12px;
            border: 1px solid var(--border);
            border-radius: 10px;
            background: #fff;
            color: #111827;
            outline: none;
            transition: box-shadow .12s ease, border-color .12s ease, background .12s ease;
        }

        input::placeholder{
            color: #9ca3af;
        }

        input:focus, select:focus{
            border-color: rgba(37,99,235,.45);
            box-shadow: 0 0 0 4px rgba(37,99,235,.18);
        }

        /* 最后一行按钮：单独覆盖你原来的 inline style */
        .form-card .actions{
            margin-top: 16px;
            display: flex;
            justify-content: center;  /* ✅ 你要的 */
            gap: 16px;
        }

        button{
            height: 38px;
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

        /* 主按钮（创建用户） */
        button[type="submit"]{
            border-color: rgba(37,99,235,.25);
            background: var(--primary-weak);
            color: var(--primary);
        }

        button[type="submit"]:hover{
            background: rgba(37,99,235,.16);
            border-color: rgba(37,99,235,.35);
        }
    </style>
</head>

<body>

<form class="form-card"
      action="<%= request.getContextPath() %>/superadmin/userManage/createAccount"
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

    <div class="actions">
        <button type="submit">创建用户</button>
        <button type="reset">重置</button>
    </div>
</form>

</body>
</html>