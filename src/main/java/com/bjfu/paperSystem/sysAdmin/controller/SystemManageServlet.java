package com.bjfu.paperSystem.sysAdmin.controller;

import com.bjfu.paperSystem.javabeans.Logs;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet(urlPatterns = "/sysadmin/systemManage/*")
public class SystemManageServlet extends HttpServlet {

    private superAdminService adminService;

    @Override
    public void init() throws ServletException {
        WebApplicationContext ctx =
                WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        if (ctx == null) {
            throw new ServletException("Spring WebApplicationContext not found. " +
                    "请确认项目已集成 Spring 并正确初始化 WebApplicationContext。");
        }
        this.adminService = ctx.getBean(superAdminService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();

        String ctxPath = request.getContextPath();
        String uri = request.getRequestURI();

        String base = ctxPath + "/sysadmin/systemManage";
        String action = uri.length() > base.length() ? uri.substring(base.length()) : "";

        switch (action) {
            case "/queryLogs": {
                // 等价于 @GetMapping("queryLogs") queryLogs(...)
                LocalDateTime opTime = parseLocalDateTime(request.getParameter("opTime"));
                Integer oporId = parseInteger(request.getParameter("oporId"));
                Integer paperId = parseInteger(request.getParameter("paperId"));

                String opType = trimToNull(request.getParameter("opType"));

                List<Logs> logList = adminService.queryLogs(opTime, oporId, opType, paperId);
                session.setAttribute("logList", logList);

                forward(request, response, "/WEB-INF/jsp/superadmin/systemManage.jsp");
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

    private String trimToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private Integer parseInteger(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null; // 容错：非法数字当作未填写
        }
    }

    /**
     * 兼容两种常见格式：
     * 1) 2026-01-10T15:40:30   (ISO_DATE_TIME)
     * 2) 2026-01-10 15:40:30   (常见展示格式)
     */
    private LocalDateTime parseLocalDateTime(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;

        try {
            return LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException ignored) {}

        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(s, fmt);
        } catch (DateTimeParseException ignored) {}

        return null;
    }
}