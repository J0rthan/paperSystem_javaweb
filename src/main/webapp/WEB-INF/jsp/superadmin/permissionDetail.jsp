<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>权限详情</title>
    <meta charset="UTF-8">
    <style>
        :root{
            --bg:#f6f7fb; --card:#fff; --text:#1f2937; --muted:#6b7280;
            --border:#e5e7eb; --header:#f3f4f6; --hover:#f9fafb;
            --primary:#2563eb; --primary-weak: rgba(37,99,235,.10);
            --shadow:0 10px 30px rgba(17,24,39,.08); --radius:12px;
        }
        *{box-sizing:border-box;}
        body{
            margin:0; padding:16px;
            font-family:"PingFang SC","Microsoft YaHei",Arial,sans-serif;
            background:var(--bg); color:var(--text);
        }
        .topbar{
            display:flex; align-items:flex-end; justify-content:space-between;
            gap:12px; flex-wrap:wrap; margin-bottom:14px;
        }
        .title h2{margin:0 0 6px; font-size:22px; font-weight:700; color:#111827;}
        .title .sub{margin:0; color:var(--muted); font-size:13px;}
        a.back{
            display:inline-flex; align-items:center; justify-content:center;
            height:34px; padding:0 14px;
            text-decoration:none; color:#111827;
            border:1px solid var(--border); background:#fff;
            border-radius:10px; box-shadow:0 2px 10px rgba(17,24,39,.06);
        }
        a.back:hover{background:#f9fafb;}

        table{
            width:100%;
            border-collapse:separate;
            border-spacing:0;
            background:var(--card);
            border:1px solid var(--border);
            border-radius:var(--radius);
            overflow:hidden;
            box-shadow:var(--shadow);
        }
        th,td{
            padding:12px 10px;
            text-align:center;
            font-size:14px;
            border-bottom:1px solid var(--border);
            border-right:1px solid var(--border);
            white-space:nowrap;
        }
        tr td:last-child, tr th:last-child{border-right:none;}
        th{background:var(--header); font-weight:700; color:#374151; font-size:13px;}
        tbody tr:last-child td{border-bottom:none;}
        tbody tr:hover td{background:var(--hover);}
        td.func{font-weight:700; color:#111827;}

        select{
            height:34px; border-radius:10px;
            border:1px solid var(--border);
            padding:0 10px; background:#fff; color:#111827;
            font:inherit;
            min-width:130px;
        }
        .actions{
            margin-top:14px;
            display:flex; gap:10px; align-items:center; justify-content:flex-start; flex-wrap:wrap;
        }
        button{
            height:34px; padding:0 14px;
            border-radius:10px;
            border:1px solid var(--border);
            background:#fff; color:#111827;
            cursor:pointer;
            box-shadow:0 2px 10px rgba(17,24,39,.06);
        }
        button.primary{
            border-color: rgba(37,99,235,.35);
            background: var(--primary-weak);
            color: var(--primary);
            font-weight:700;
        }
        button:hover{background:#f9fafb;}
        button.primary:hover{background: rgba(37,99,235,.16);}
        .hint{color:var(--muted); font-size:13px;}
    </style>
</head>

<body>

<div class="topbar">
    <div class="title">
        <h2>权限详情</h2>
        <p class="sub">
            用户：<strong>${user.userName}</strong>（ID：${user.userId}）
        </p>
    </div>

    <a class="back" href="${pageContext.request.contextPath}/superadmin/permissionManage">返回列表</a>
</div>

<form action="${pageContext.request.contextPath}/superadmin/permissionManage/save" method="post">
    <input type="hidden" name="userId" value="${user.userId}"/>

    <table>
        <thead>
        <tr>
            <th>功能模块</th>
            <th>作者</th>
            <th>审稿人</th>
            <th>编辑</th>
            <th>主编（EIC）</th>
            <th>管理员</th>
        </tr>
        </thead>

        <tbody>
        <!-- 允许值约定：DENY / ALLOW / SUGGEST -->
        <tr>
            <td class="func">提交新稿件</td>
            <td>
                <select name="SUBMIT_AUTHOR">
                    <option value="DENY">❌ 禁止</option>
                    <option value="ALLOW" selected>✅ 允许</option>
                    <option value="SUGGEST">⚠️ 建议</option>
                </select>
            </td>
            <td><select name="SUBMIT_REVIEWER">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="SUBMIT_EDITOR">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="SUBMIT_EIC">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="SUBMIT_ADMIN">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
        </tr>

        <tr>
            <td class="func">查看所有稿件</td>
            <td><select name="VIEWALL_AUTHOR">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="VIEWALL_REVIEWER">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="VIEWALL_EDITOR">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="VIEWALL_EIC">
                <option value="DENY">❌ 禁止</option><option value="ALLOW" selected>✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="VIEWALL_ADMIN">
                <option value="DENY">❌ 禁止</option><option value="ALLOW" selected>✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
        </tr>

        <tr>
            <td class="func">邀请/指派人员</td>
            <td><select name="ASSIGN_AUTHOR">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="ASSIGN_REVIEWER">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="ASSIGN_EDITOR">
                <option value="DENY">❌ 禁止</option><option value="ALLOW" selected>✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="ASSIGN_EIC">
                <option value="DENY">❌ 禁止</option><option value="ALLOW" selected>✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="ASSIGN_ADMIN">
                <option value="DENY">❌ 禁止</option><option value="ALLOW" selected>✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
        </tr>

        <tr>
            <td class="func">查看审稿人身份</td>
            <td><select name="VIEW_REVIEWER_ID_AUTHOR">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="VIEW_REVIEWER_ID_REVIEWER">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="VIEW_REVIEWER_ID_EDITOR">
                <option value="DENY">❌ 禁止</option><option value="ALLOW" selected>✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="VIEW_REVIEWER_ID_EIC">
                <option value="DENY">❌ 禁止</option><option value="ALLOW" selected>✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="VIEW_REVIEWER_ID_ADMIN">
                <option value="DENY">❌ 禁止</option><option value="ALLOW" selected>✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
        </tr>

        <tr>
            <td class="func">填写审稿意见</td>
            <td><select name="REVIEW_AUTHOR">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="REVIEW_REVIEWER">
                <option value="DENY">❌ 禁止</option><option value="ALLOW" selected>✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="REVIEW_EDITOR">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="REVIEW_EIC">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="REVIEW_ADMIN">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
        </tr>

        <tr>
            <td class="func">做出录用/拒稿决定</td>
            <td><select name="DECIDE_AUTHOR">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="DECIDE_REVIEWER">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="DECIDE_EDITOR">
                <option value="DENY">❌ 禁止</option>
                <option value="ALLOW">✅ 允许</option>
                <option value="SUGGEST" selected>⚠️ 建议（仅建议）</option>
            </select></td>
            <td><select name="DECIDE_EIC">
                <option value="DENY">❌ 禁止</option><option value="ALLOW" selected>✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="DECIDE_ADMIN">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
        </tr>

        <tr>
            <td class="func">修改系统配置</td>
            <td><select name="CONFIG_AUTHOR">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="CONFIG_REVIEWER">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="CONFIG_EDITOR">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="CONFIG_EIC">
                <option value="DENY" selected>❌ 禁止</option><option value="ALLOW">✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
            <td><select name="CONFIG_ADMIN">
                <option value="DENY">❌ 禁止</option><option value="ALLOW" selected>✅ 允许</option><option value="SUGGEST">⚠️ 建议</option>
            </select></td>
        </tr>

        </tbody>
    </table>

    <div class="actions">
        <button type="submit" class="primary">保存修改</button>
        <button type="reset">恢复默认</button>
        <span class="hint">说明：编辑在“做出录用/拒稿决定”默认是“⚠️ 建议（仅建议）”，主编（EIC）为“✅ 允许”。</span>
    </div>
</form>

</body>
</html>