package com.bjfu.paperSystem.sysAdmin.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = "/sysadmin")
public class sysAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 转发到 JSP（建议放在 WEB-INF 下）
        request.getRequestDispatcher("/WEB-INF/jsp/sysadmin.jsp")
                .forward(request, response);
    }
}