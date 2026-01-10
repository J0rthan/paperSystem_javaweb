<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>修改账号</title>
    <meta charset="UTF-8">
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

        /* 右侧内容区：不强制居中 */
        body{
            margin: 0;
            padding: 16px;
            font-family: "PingFang SC","Microsoft YaHei", Arial, sans-serif;
            background: var(--bg);
            color: var(--text);
            text-align: left;
        }

        h2{
            margin: 0 0 14px;
            font-size: 18px;
            font-weight: 800;
            color: #111827;
        }

        /* 表单卡片：更像后台面板 */
        form{
            width: 100%;
            margin: 0;
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            padding: 20px;
        }

        /* 每一行：label + 控件 */
        form > div{
            display: grid;
            grid-template-columns: 110px 1fr;
            gap: 10px;
            align-items: center;
            margin-bottom: 14px;
        }

        label{
            color: #374151;
            font-size: 14px;
            font-weight: 700;
            text-align: right;
            white-space: nowrap;
        }

        input[type="text"],
        input[type="password"],
        select{
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

        input:focus,
        select:focus{
            border-color: rgba(37,99,235,.45);
            box-shadow: 0 0 0 4px rgba(37,99,235,.18);
        }

        /* 分割线更柔和 */
        hr{
            margin: 18px 0;
            border: none;
            border-top: 1px solid var(--border);
        }

        /* 最后一行按钮区：居中 + 间距 */
        form > div:last-of-type{
            grid-template-columns: 1fr;  /* 让按钮区单独成行 */
            justify-items: center;
            margin-top: 6px;
        }

        input[type="submit"]{
            height: 38px;
            padding: 0 16px;
            border-radius: 10px;
            border: 1px solid rgba(37,99,235,.25);
            background: var(--primary-weak);
            color: var(--primary);
            font-weight: 800;
            cursor: pointer;
            box-shadow: 0 2px 10px rgba(17,24,39,.06);
            transition: transform .12s ease, background .12s ease, box-shadow .12s ease, border-color .12s ease;
        }

        input[type="submit"]:hover{
            background: rgba(37,99,235,.16);
            border-color: rgba(37,99,235,.35);
            transform: translateY(-1px);
            box-shadow: 0 10px 22px rgba(17,24,39,.08);
        }

        a{
            margin-left: 12px;
            text-decoration: none;
            color: #374151;
            padding: 6px 10px;
            border-radius: 10px;
            border: 1px solid var(--border);
            background: #fff;
            transition: background .12s ease, transform .12s ease, box-shadow .12s ease;
        }

        a:hover{
            background: var(--hover);
            transform: translateY(-1px);
            box-shadow: 0 10px 22px rgba(17,24,39,.08);
        }

        /* 小屏：label 上移，变上下布局 */
        @media (max-width: 640px){
            form{ padding: 14px; }
            form > div{
                grid-template-columns: 1fr;
                gap: 6px;
            }
            label{ text-align: left; }
        }

        /* ✅ 用户类型对齐：按你的网格规则走（方案A改完HTML后这条可省略） */

        /* ✅ 按钮对齐：让 a 像按钮一样，并且与 submit 同高同宽感 */
        .actions{
            margin-top: 16px;
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 14px;
        }

        .actions input[type="submit"]{
            height: 38px;
            padding: 0 16px;
            border-radius: 10px;
        }

        .actions .btn-secondary{
            display: inline-flex;
            align-items: center;
            justify-content: center;
            height: 38px;
            padding: 0 16px;
            border-radius: 10px;
            border: 1px solid var(--border);
            background: #fff;
            color: #374151;
            text-decoration: none;
            font-weight: 700;
            box-shadow: 0 2px 10px rgba(17,24,39,.06);
            transition: transform .12s ease, background .12s ease, box-shadow .12s ease;
        }

        .actions .btn-secondary:hover{
            background: var(--hover);
            transform: translateY(-1px);
            box-shadow: 0 10px 22px rgba(17,24,39,.08);
        }
    </style>
</head>

<body>

<h2>修改账号</h2>

<form action="${pageContext.request.contextPath}/superadmin/userManage/modifyAccount"
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
        <label>用户类型：</label>
        <select name="userType" required>
            <option value="sys_admin" <c:if test="${userInfo.userType eq 'sys_admin'}">selected</c:if>>系统管理员</option>
            <option value="option_admin" <c:if test="${userInfo.userType eq 'option_admin'}">selected</c:if>>编辑部管理员</option>
            <option value="chief_editor" <c:if test="${userInfo.userType eq 'chief_editor'}">selected</c:if>>主编</option>
            <option value="editor" <c:if test="${userInfo.userType eq 'editor'}">selected</c:if>>编辑</option>
            <option value="reviewer" <c:if test="${userInfo.userType eq 'reviewer'}">selected</c:if>>审稿人</option>
            <option value="author" <c:if test="${userInfo.userType eq 'author'}">selected</c:if>>作者</option>
        </select>
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

    <div class="actions">
        <input type="submit" value="保存修改"/>
        <a class="btn-secondary" href="${pageContext.request.contextPath}/superadmin/userManage/modifyAccountPage">返回列表</a>
    </div>

</form>

</body>
</html>