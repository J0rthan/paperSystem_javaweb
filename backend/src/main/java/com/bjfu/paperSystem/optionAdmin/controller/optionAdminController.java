package com.bjfu.paperSystem.optionAdmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/optionadmin")
public class optionAdminController {
    @GetMapping
    public String optionAdmin() {
        return "optionadmin";
    }
}
