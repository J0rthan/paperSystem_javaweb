package com.bjfu.paperSystem.superAdmin.controller;

import com.bjfu.paperSystem.superAdmin.service.superAdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;

@WebServlet("/superadmin/permissionManage")
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

        // 目前页面逻辑未完成，这里只做页面跳转
        request.getRequestDispatcher("/WEB-INF/jsp/superadmin/permissionManage.jsp")
                .forward(request, response);
    }
}