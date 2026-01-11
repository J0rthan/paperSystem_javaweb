package com.bjfu.paperSystem.sysAdmin.controller;

import com.bjfu.paperSystem.javabeans.Permission;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.superAdmin.dao.permissionManageDao;
import com.bjfu.paperSystem.superAdmin.service.permissionManageService;
import com.bjfu.paperSystem.superAdmin.service.superAdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/sysadmin/permissionManage/*")
public class PermissionManageServlet extends HttpServlet {

    private superAdminService suAdminService;

    private permissionManageService permissionService;

    private permissionManageDao permissionDao;

    @Override
    public void init() throws ServletException {
        WebApplicationContext ctx =
                WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if (ctx == null) {
            throw new ServletException("Spring WebApplicationContext not found. 请确认启动类包含 @ServletComponentScan");
        }

        this.suAdminService = ctx.getBean(superAdminService.class);
        this.permissionService = ctx.getBean(permissionManageService.class);
        this.permissionDao = ctx.getBean(permissionManageDao.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();

        String base = ctx + "/superadmin/permissionManage";
        String action = uri.length() > base.length() ? uri.substring(base.length()) : "";

        switch(action) {
            case "":
            case "/": {
                List<User> userList = suAdminService.findAllUsers();

                List<User> result = userList.stream()
                        .filter(u ->
                                ("editor".equals(u.getUserType()) || "reviewer".equals(u.getUserType()) || "author".equals(u.getUserType()) || "chief_editor".equals(u.getUserType()))
                                        && "exist".equals(u.getStatus())
                        )
                        .toList();

                request.setAttribute("userList", result);
                request.getRequestDispatcher("/WEB-INF/jsp/sysadmin/permissionManage.jsp")
                        .forward(request, response);

                break;
            }
            case "/detail": {
                String userIdStr = request.getParameter("userId");
                int userId = Integer.parseInt(userIdStr);

                User user = suAdminService.findUserById(userId);

                request.setAttribute("user", user);
                Permission permission = permissionService.findByUserId(userId);
                request.setAttribute("permission", permission);

                request.getRequestDispatcher(
                        "/WEB-INF/jsp/superadmin/permissionDetail.jsp"
                ).forward(request, response);

                break;
            }
        }



        /*
            <option value="sys_admin" selected>系统管理员</option>
            <option value="option_admin">编辑部管理员</option>
            <option value="chief_editor">主编</option>
            <option value="editor">编辑</option>
            <option value="reviewer">审稿人</option>
            <option value="author">作者</option>
         */
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();

        String base = ctx + "/superadmin/permissionManage";
        String action = uri.length() > base.length() ? uri.substring(base.length()) : "";

        switch(action) {
            case "/save": {
                request.setCharacterEncoding("UTF-8");
                response.setCharacterEncoding("UTF-8");

                // 1) 取 userId
                String userIdStr = request.getParameter("userId");
                if (userIdStr == null || userIdStr.isBlank()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少 userId");
                    return;
                }
                int userId = Integer.parseInt(userIdStr);

                // 2) 查用户，拿到 userType
                User user = suAdminService.findUserById(userId); // 你自己已有的查询方法
                if (user == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户不存在: " + userId);
                    return;
                }
                String userType = user.getUserType(); // author / reviewer / editor / chief_editor

                // 3) 查 Permission（建议一对一：通过 userId 查）
                Permission permission = permissionService.findByUserId(userId);
                if (permission == null) {
                    permission = new Permission();
                    // ★ 关键：必须把 user 关联设置上，否则会 not-null property ... Permission.user
                    permission.setUser(user);
                } else {
                    // 若你历史数据里存在 permission.user 为 null，也可以顺手修复
                    if (permission.getUser() == null) {
                        permission.setUser(user);
                    }
                }

                // 4) 按 userType 只更新本角色那一列（页面只提交这一列）
                if ("author".equals(userType)) {

                    applyIfNotNull(paramToBooleanOrNull(request, "SUBMIT_AUTHOR"), permission::setSubmitManuscript);
                    applyIfNotNull(paramToBooleanOrNull(request, "VIEWALL_AUTHOR"), permission::setViewAllManuscripts);
                    applyIfNotNull(paramToBooleanOrNull(request, "ASSIGN_AUTHOR"), permission::setInviteOrAssignPersonnel);
                    applyIfNotNull(paramToBooleanOrNull(request, "VIEW_REVIEWER_ID_AUTHOR"), permission::setViewReviewerIdentity);
                    applyIfNotNull(paramToBooleanOrNull(request, "REVIEW_AUTHOR"), permission::setWriteReviewComment);
                    applyIfNotNull(paramToBooleanOrNull(request, "DECIDE_AUTHOR"), permission::setMakeAcceptRejectDecision);
                    applyIfNotNull(paramToBooleanOrNull(request, "CONFIG_AUTHOR"), permission::setModifySystemConfig);

                } else if ("reviewer".equals(userType)) {

                    applyIfNotNull(paramToBooleanOrNull(request, "SUBMIT_REVIEWER"), permission::setSubmitManuscript);
                    applyIfNotNull(paramToBooleanOrNull(request, "VIEWALL_REVIEWER"), permission::setViewAllManuscripts);
                    applyIfNotNull(paramToBooleanOrNull(request, "ASSIGN_REVIEWER"), permission::setInviteOrAssignPersonnel);
                    applyIfNotNull(paramToBooleanOrNull(request, "VIEW_REVIEWER_ID_REVIEWER"), permission::setViewReviewerIdentity);
                    applyIfNotNull(paramToBooleanOrNull(request, "REVIEW_REVIEWER"), permission::setWriteReviewComment);
                    applyIfNotNull(paramToBooleanOrNull(request, "DECIDE_REVIEWER"), permission::setMakeAcceptRejectDecision);
                    applyIfNotNull(paramToBooleanOrNull(request, "CONFIG_REVIEWER"), permission::setModifySystemConfig);

                } else if ("editor".equals(userType)) {

                    applyIfNotNull(paramToBooleanOrNull(request, "SUBMIT_EDITOR"), permission::setSubmitManuscript);
                    applyIfNotNull(paramToBooleanOrNull(request, "VIEWALL_EDITOR"), permission::setViewAllManuscripts);
                    applyIfNotNull(paramToBooleanOrNull(request, "ASSIGN_EDITOR"), permission::setInviteOrAssignPersonnel);
                    applyIfNotNull(paramToBooleanOrNull(request, "VIEW_REVIEWER_ID_EDITOR"), permission::setViewReviewerIdentity);
                    applyIfNotNull(paramToBooleanOrNull(request, "REVIEW_EDITOR"), permission::setWriteReviewComment);
                    applyIfNotNull(paramToBooleanOrNull(request, "DECIDE_EDITOR"), permission::setMakeAcceptRejectDecision);
                    applyIfNotNull(paramToBooleanOrNull(request, "CONFIG_EDITOR"), permission::setModifySystemConfig);

                } else if ("chief_editor".equals(userType)) {

                    applyIfNotNull(paramToBooleanOrNull(request, "SUBMIT_EIC"), permission::setSubmitManuscript);
                    applyIfNotNull(paramToBooleanOrNull(request, "VIEWALL_EIC"), permission::setViewAllManuscripts);
                    applyIfNotNull(paramToBooleanOrNull(request, "ASSIGN_EIC"), permission::setInviteOrAssignPersonnel);
                    applyIfNotNull(paramToBooleanOrNull(request, "VIEW_REVIEWER_ID_EIC"), permission::setViewReviewerIdentity);
                    applyIfNotNull(paramToBooleanOrNull(request, "REVIEW_EIC"), permission::setWriteReviewComment);
                    applyIfNotNull(paramToBooleanOrNull(request, "DECIDE_EIC"), permission::setMakeAcceptRejectDecision);
                    applyIfNotNull(paramToBooleanOrNull(request, "CONFIG_EIC"), permission::setModifySystemConfig);

                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "未知用户类型: " + userType);
                    return;
                }

                // 5) 保存（JPA 的 save = insert 或 update）
                permissionDao.save(permission);

                // 6) 重定向回详情页（按你的路由改）
                response.sendRedirect(request.getContextPath() + "/superadmin/permissionManage/detail?userId=" + userId);
                return;
            }
        }
    }

    private static Boolean paramToBooleanOrNull(HttpServletRequest request, String name) {
        String v = request.getParameter(name);
        if (v == null) return null;
        v = v.trim();
        if (v.isEmpty()) return null;
        // 只接受 true/false；其它值当作 null，避免误覆盖
        if ("true".equalsIgnoreCase(v)) return Boolean.TRUE;
        if ("false".equalsIgnoreCase(v)) return Boolean.FALSE;
        return null;
    }

    private static void applyIfNotNull(Boolean value, java.util.function.Consumer<Boolean> setter) {
        if (value != null) setter.accept(value);
    }
}