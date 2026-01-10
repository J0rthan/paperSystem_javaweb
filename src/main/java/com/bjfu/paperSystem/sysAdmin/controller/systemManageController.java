package com.bjfu.paperSystem.sysAdmin.controller;

import com.bjfu.paperSystem.javabeans.Logs;
import com.bjfu.paperSystem.superAdmin.service.superAdminService;
import com.bjfu.paperSystem.sysAdmin.service.sysAdminService;
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

@Controller("sysAdminSystemManageController")
@RequestMapping("/sysadmin/systemManage")
public class systemManageController {
    @Autowired
    private sysAdminService sysService;

    @GetMapping()
    public String systemManage(HttpSession session) {
        List<Logs> logList = sysService.findAllLogs();
        session.setAttribute("logList", logList);
        return "/sysadmin/systemManage";
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
        List<Logs> logList = sysService.queryLogs(
                opTime, oporId, opType, paperId
        );

        session.setAttribute("logList", logList);
        return "/sysadmin/systemManage";
    }
}
