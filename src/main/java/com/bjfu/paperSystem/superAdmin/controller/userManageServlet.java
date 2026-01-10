package com.bjfu.paperSystem.superAdmin.controller;

import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.superAdmin.service.superAdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(urlPatterns = {
        "/superadmin/userManage/*"
})
public class userManageServlet extends HttpServlet {

    private superAdminService suAdminService;

    @Override
    public void init() throws ServletException {
        WebApplicationContext ctx =
                WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if (ctx == null) {
            throw new ServletException("Spring WebApplicationContext not found. " +
                    "请确认已使用 Spring Boot 启动且启动类包含 @ServletComponentScan。");
        }
        this.suAdminService = ctx.getBean(superAdminService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();

        // 取出 /superadmin/userManage 后面的“子路径”
        String base = ctx + "/superadmin/userManage";
        String action = uri.length() > base.length() ? uri.substring(base.length()) : "";

        switch (action) {
            case "/createAccountPage":
                // 原：return "superadmin/userManage/createAccountPage";
                forward(request, response, "/WEB-INF/jsp/superadmin/userManage/createAccountPage.jsp");
                break;

            case "/deleteAccountPage": {
                // 原：model.addAttribute("userList", users)
                List<User> users = suAdminService.findAllExistUsers();
                request.setAttribute("userList", users);
                forward(request, response, "/WEB-INF/jsp/superadmin/userManage/deleteAccountPage.jsp");
                break;
            }

            case "/modifyAccountPage": {
                List<User> users = suAdminService.findAllUsers();
                request.setAttribute("userList", users);
                forward(request, response, "/WEB-INF/jsp/superadmin/userManage/modifyAccountPage.jsp");
                break;
            }

            case "/edit": {
                // 原：toEditPage(User user, Model model)
                Integer userId = Integer.valueOf(request.getParameter("userId"));
                User temp = suAdminService.findUserById(userId);
                request.setAttribute("userInfo", temp);
                forward(request, response, "/WEB-INF/jsp/superadmin/userManage/edit.jsp");
                break;
            }

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // 你后面如果要补 createAccount/deleteAccount/modifyAccount（POST），就写在这里
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();

        String base = ctx + "/superadmin/userManage";
        String action = uri.length() > base.length() ? uri.substring(base.length()) : "";

        switch (action) {
             case "/deleteAccount": {
                 Integer userId = Integer.valueOf(request.getParameter("userId"));
                 suAdminService.deleteUser(userId);
                 response.sendRedirect(request.getContextPath() + "/superadmin/userManage/deleteAccountPage");
                 break;
             }
             case "/createAccount": {
                 User user = bindUser(request);          // 1) 取参封装
                 suAdminService.createAccount(user);     // 2) 调用业务

                 // 3) 重定向回创建页（可以带提示参数）
                 response.sendRedirect(request.getContextPath()
                         + "/superadmin/userManage/createAccountPage?success=1");
                 break;
             }
            case "/modifyAccount": {
                User user = bindUserForModify(request);

                String pwd = request.getParameter("password");
                if (pwd == null || pwd.trim().isEmpty()) {
                    user.setPassword(null);          // 明确：不改密码
                } else {
                    user.setPassword(pwd.trim());    // 要改密码
                }

                suAdminService.modifyUser(user);

                response.sendRedirect(request.getContextPath()
                        + "/superadmin/userManage/modifyAccountPage");
                break;
            }
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String jspPath)
            throws ServletException, IOException {
        request.getRequestDispatcher(jspPath).forward(request, response);
    }

    private User bindUser(HttpServletRequest request) {
        User u = new User();
        u.setUserName(request.getParameter("userName"));
        u.setPassword(request.getParameter("password"));
        u.setUserType(request.getParameter("userType"));
        u.setEmail(request.getParameter("email"));
        u.setFullName(request.getParameter("fullName"));
        u.setCompany(request.getParameter("company"));
        u.setInvestigationDirection(request.getParameter("investigationDirection"));
        u.setRegisterTime(LocalDateTime.now());
        return u;
    }

    private User bindUserForModify(HttpServletRequest request) {
        User u = new User();
        u.setUserId(Integer.parseInt(request.getParameter("userId")));
        u.setUserName(request.getParameter("userName"));
        u.setFullName(request.getParameter("fullName"));
        u.setEmail(request.getParameter("email"));
        u.setUserType(request.getParameter("userType"));
        u.setCompany(request.getParameter("company"));
        u.setInvestigationDirection(request.getParameter("investigationDirection"));
        u.setStatus(request.getParameter("status"));
        // password 在 case 里单独处理（留空不改）
        return u;
    }
}