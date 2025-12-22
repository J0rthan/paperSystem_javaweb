package com.bjfu.paperSystem.reviewer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reviewer")
public class reviewerController {
    @GetMapping
    public String reviewer() {
        return "reviewer";
    }
}
