package com.bjfu.paperSystem.sysAdmin.controller;

import com.bjfu.paperSystem.javabeans.User;
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

    @Override
    public void init() throws ServletException {
        // 从 Spring 容器中获取 Bean
        WebApplicationContext ctx =
                WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if (ctx == null) {
            throw new ServletException("Spring WebApplicationContext not found. " +
                    "请确认启动类包含 @ServletComponentScan");
        }
        this.suAdminService = ctx.getBean(superAdminService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();

        String base = ctx + "/sysadmin/permissionManage";
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
                request.getRequestDispatcher("/WEB-INF/jsp/superadmin/permissionManage.jsp")
                        .forward(request, response);

                break;
            }
            case "/detail": {
                System.out.println(1111111);

                String userIdStr = request.getParameter("userId");
                int userId = Integer.parseInt(userIdStr);

                User user = suAdminService.findUserById(userId);

                request.setAttribute("user", user);
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
}