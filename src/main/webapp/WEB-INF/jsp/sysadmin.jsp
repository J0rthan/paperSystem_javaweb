<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8" />
    <title>系统管理员后台</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <style>
        * { box-sizing: border-box; }
        html, body { height: 100%; }

        body {
            margin: 0;
            font-family: "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
            background-color: #f6f7fb;
            color: #222;
        }

        .layout {
            height: 100vh;
            display: flex;
        }

        /* 左侧侧栏 */
        .sidebar {
            width: 240px;
            background: #111827;
            color: #e5e7eb;
            padding: 18px 14px;
            display: flex;
            flex-direction: column;
            gap: 14px;
        }

        .brand { padding: 10px 10px 6px; }

        .brand h1 {
            margin: 0;
            font-size: 16px;
            font-weight: 700;
        }

        .brand p {
            margin: 6px 0 0;
            font-size: 12px;
            color: #9ca3af;
            line-height: 1.4;
        }

        /* 菜单树 */
        .menu {
            display: flex;
            flex-direction: column;
            gap: 8px;
            padding: 0 6px;
        }

        .menu details {
            border: 1px solid rgba(255,255,255,0.08);
            border-radius: 10px;
            background: rgba(255,255,255,0.04);
            overflow: hidden;
        }

        .menu summary {
            list-style: none;
            cursor: pointer;
            padding: 10px 12px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            user-select: none;
        }
        .menu summary::-webkit-details-marker { display: none; }

        .menu summary .title {
            display: flex;
            gap: 10px;
            align-items: center;
            font-size: 14px;
            font-weight: 600;
        }

        .chev {
            width: 10px;
            height: 10px;
            border-right: 2px solid #9ca3af;
            border-bottom: 2px solid #9ca3af;
            transform: rotate(-45deg);
            transition: transform .15s ease;
            margin-left: 10px;
        }
        details[open] .chev { transform: rotate(45deg); }

        .submenu {
            display: flex;
            flex-direction: column;
            padding: 6px 8px 10px;
            gap: 6px;
        }

        .submenu a {
            display: block;
            text-decoration: none;
            color: #e5e7eb;
            padding: 8px 10px;
            border-radius: 8px;
            font-size: 13px;
            background: rgba(255,255,255,0.03);
        }
        .submenu a:hover { background: rgba(255,255,255,0.08); }

        .submenu a.active {
            background: rgba(255,255,255,0.14);
            outline: 1px solid rgba(255,255,255,0.12);
        }

        /* 右侧内容区 */
        .main {
            flex: 1;
            display: flex;
            flex-direction: column;
            min-width: 0;
        }

        .topbar {
            height: 56px;
            background: #ffffff;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 18px;
            border-bottom: 1px solid #e5e7eb;
        }

        .topbar .path {
            font-size: 14px;
            color: #374151;
        }

        .content { flex: 1; padding: 16px; }

        .frame {
            width: 100%;
            height: calc(100vh - 56px - 32px);
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            background: #fff;
        }

        @media (max-width: 760px) {
            .sidebar { width: 200px; }
            .brand h1 { font-size: 14px; }
        }

        .logout {
            font-size: 14px;
            color: #374151;
            text-decoration: none;
            padding: 6px 12px;
            border: 1px solid #d1d5db;
            border-radius: 6px;
            background-color: #f9fafb;
        }

        .logout:hover {
            background-color: #e5e7eb;
        }
    </style>
</head>

<body>
<div class="layout">

    <!-- 左侧链接树 -->
    <aside class="sidebar">
        <div class="brand">
            <h1>系统管理员后台</h1>
            <p>用户管理 / 权限管理 / 系统管理</p>
        </div>

        <nav class="menu">

            <!-- 用户管理 -->
            <details open>
                <summary>
                    <span class="title">用户管理</span>
                    <span class="chev"></span>
                </summary>
                <div class="submenu">
                    <a class="active"
                       data-breadcrumb="系统管理员后台 / 用户管理 / 修改账号"
                       href="${pageContext.request.contextPath}/sysadmin/userManage/modifyAccountPage"
                       target="mainFrame">修改账号</a>

                    <a data-breadcrumb="系统管理员后台 / 用户管理 / 创建用户"
                       href="${pageContext.request.contextPath}/sysadmin/userManage/createAccountPage"
                       target="mainFrame">创建用户</a>

                    <a data-breadcrumb="系统管理员后台 / 用户管理 / 删除账号"
                       href="${pageContext.request.contextPath}/sysadmin/userManage/deleteAccountPage"
                       target="mainFrame">删除账号</a>
                </div>
            </details>

            <!-- 权限管理 -->
            <details>
                <summary>
                    <span class="title">权限管理</span>
                    <span class="chev"></span>
                </summary>
                <div class="submenu">
                    <a data-breadcrumb="系统管理员后台 / 权限管理 / 权限配置"
                       href="${pageContext.request.contextPath}/sysadmin/permissionManage"
                       target="mainFrame">权限配置</a>
                </div>
            </details>

            <!-- 系统管理 -->
            <details>
                <summary>
                    <span class="title">系统管理</span>
                    <span class="chev"></span>
                </summary>
                <div class="submenu">
                    <a data-breadcrumb="系统管理员后台 / 系统管理 / 系统日志"
                       href="${pageContext.request.contextPath}/sysadmin/systemManage/queryLogs"
                       target="mainFrame">系统日志</a>
                </div>
            </details>

        </nav>
    </aside>

    <!-- 右侧内容 -->
    <main class="main">
        <div class="topbar">
            <div class="path" id="breadcrumb">
                当前位置：系统管理员后台 / 用户管理 / 用户列表
            </div>

            <a href="${pageContext.request.contextPath}/author/logout"
               class="logout"
               onclick="return confirm('确认退出登录？');">
                退出登录
            </a>
        </div>

        <div class="content">
            <!-- 默认加载一个子页面 -->
            <iframe class="frame"
                    name="mainFrame"
                    src="${pageContext.request.contextPath}/sysadmin/userManage/modifyAccountPage">
            </iframe>
        </div>
    </main>

</div>

<script>
    // 1) 点击子菜单：高亮 active
    // 2) 更新“当前位置”面包屑
    (function () {
        const links = document.querySelectorAll('.submenu a');
        const breadcrumbEl = document.getElementById('breadcrumb');

        links.forEach(a => {
            a.addEventListener('click', function () {
                links.forEach(x => x.classList.remove('active'));
                this.classList.add('active');

                const bc = this.getAttribute('data-breadcrumb');
                if (bc) breadcrumbEl.textContent = '当前位置：' + bc;
            });
        });
    })();
</script>

</body>
</html>