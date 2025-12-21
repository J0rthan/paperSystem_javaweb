package com.bjfu.paperSystem.sysAdmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sysadmin")
public class sysAdminController {
    @GetMapping
    public String sysAdmin() {
        return "/sysadmin";
    }
}
