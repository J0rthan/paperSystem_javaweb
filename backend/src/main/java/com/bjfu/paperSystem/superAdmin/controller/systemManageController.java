package com.bjfu.paperSystem.superAdmin.controller;

import com.bjfu.paperSystem.javabeans.Logs;
import com.bjfu.paperSystem.superAdmin.service.superAdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/superadmin/systemManage")
public class systemManageController {
    @Autowired
    private superAdminService adminService;

    @GetMapping()
    public String systemManage(HttpSession session) {
        List<Logs> logList = adminService.findAllLogs();
        session.setAttribute("logList", logList);
        return "/superadmin/systemManage";
    }

    @GetMapping("queryLogs")
    public String queryLogs(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime opTime,

            @RequestParam(required = false) Integer oporId,
            @RequestParam(required = false) String opType,
            @RequestParam(required = false) Integer paperId,

            HttpSession session) {
        opType = opType.trim();
        List<Logs> logList = adminService.queryLogs(
                opTime, oporId, opType, paperId
        );

        session.setAttribute("logList", logList);
        return "/superadmin/systemManage";
    }
}
