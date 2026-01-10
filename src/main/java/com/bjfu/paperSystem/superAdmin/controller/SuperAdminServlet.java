package com.bjfu.paperSystem.superAdmin.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/superadmin")
public class SuperAdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 转发到 superadmin.jsp
        request.getRequestDispatcher("/WEB-INF/jsp/superadmin.jsp")
                .forward(request, response);
    }
}