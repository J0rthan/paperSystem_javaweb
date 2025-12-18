package com.bjfu.paperSystem.superAdmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/superadmin")
public class superAdminController {
    @GetMapping
    public String superAdmin() {
        return "superadmin";
    }
}
