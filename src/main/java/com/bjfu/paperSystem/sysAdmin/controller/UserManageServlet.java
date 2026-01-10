package com.bjfu.paperSystem.sysAdmin.controller;

import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.sysAdmin.service.sysAdminService;
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

@WebServlet(urlPatterns = "/sysadmin/userManage/*")
public class UserManageServlet extends HttpServlet {

    private sysAdminService sysService;

    /**
     * 按你的 JSP 实际位置修改：
     * 例如：/WEB-INF/jsp/sysadmin/userManage.jsp
     */
    private static final String JSP_BASE = "/WEB-INF/jsp";

    @Override
    public void init() throws ServletException {
        WebApplicationContext ctx =
                WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if (ctx == null) {
            throw new ServletException("Spring WebApplicationContext not found. " +
                    "请确认 Spring 已正确启动并初始化到 ServletContext。");
        }
        this.sysService = ctx.getBean(sysAdminService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = getAction(request);

        switch (action) {
            case "/createAccountPage": {
                // 等价于 @GetMapping("createAccountPage")
                forward(request, response, JSP_BASE + "/sysadmin/userManage/createAccountPage.jsp");
                break;
            }

            case "/deleteAccountPage": {
                // 等价于 @GetMapping("deleteAccountPage")
                List<User> users = sysService.findAllExistUsers();
                users.removeIf(u -> "super_admin".equals(u.getUserType()));
                users.removeIf(u -> "sys_admin".equals(u.getUserType()));

                request.setAttribute("userList", users);
//                response.sendRedirect(request.getContextPath() + "/sysadmin/userManage/deleteAccountPage.jsp");
                forward(request, response, JSP_BASE + "/sysadmin/userManage/deleteAccountPage.jsp");
                break;
            }

            case "/modifyAccountPage": {
                // 等价于 @GetMapping("modifyAccountPage")
                List<User> users = sysService.findAllUsers();
                users.removeIf(u -> "super_admin".equals(u.getUserType()));
                users.removeIf(u -> "sys_admin".equals(u.getUserType()));

                request.setAttribute("userList", users);
                forward(request, response, JSP_BASE + "/sysadmin/userManage/modifyAccountPage.jsp");
                break;
            }

            case "/edit": {
                // 等价于 @GetMapping("edit")：通过 userId 查用户详情
                Integer userId = parseInt(request.getParameter("userId"));
                if (userId == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少参数 userId");
                    return;
                }

                User temp = sysService.findUserById(userId);
                request.setAttribute("userInfo", temp);

                forward(request, response, JSP_BASE + "/sysadmin/userManage/edit.jsp");
                break;
            }

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = getAction(request);

        switch (action) {
            case "/createAccount": {
                // 等价于 @PostMapping("createAccount")
                User user = bindUser(request);
                sysService.createAccount(user);

                // 原 Controller：return "sysadmin/userManage/createAccountPage";
                forward(request, response, JSP_BASE + "/sysadmin/userManage/createAccountPage.jsp");
                break;
            }

            case "/deleteAccount": {
                // 等价于 @PostMapping("deleteAccount")
                Integer userId = parseInt(request.getParameter("userId"));
                if (userId == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少参数 userId");
                    return;
                }
                sysService.deleteUser(userId);

                // 原 Controller：redirect:/sysadmin/userManage/deleteAccountPage
                response.sendRedirect(request.getContextPath() + "/sysadmin/userManage/deleteAccountPage");
                break;
            }

            case "/modifyAccount": {
                // 等价于 @PostMapping("modifyAccount")
                User user = bindUserForModify(request);
                sysService.modifyUser(user);

                List<User> users = sysService.findAllUsers();
                request.setAttribute("userList", users);

                forward(request, response, JSP_BASE + "/sysadmin/userManage/modifyAccountPage.jsp");
                break;
            }

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * 取得 /sysadmin/userManage 后面的 pathInfo 作为 action
     * 例如：/sysadmin/userManage/deleteAccountPage -> "/deleteAccountPage"
     */
    private String getAction(HttpServletRequest request) {
        String pathInfo = request.getPathInfo(); // 只取 * 后面的部分
        if (pathInfo == null) return "";
        if ("/".equals(pathInfo)) return "";
        return pathInfo;
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String jspPath)
            throws ServletException, IOException {
        request.getRequestDispatcher(jspPath).forward(request, response);
    }

    private Integer parseInt(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 将表单参数绑定到 User（按你项目 User 字段自行补充）
     * 这里给出常见字段示例：userId / userName / password / userType / email
     * 你表单里叫什么 name，就用同名参数取值。
     */
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
}