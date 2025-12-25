package com.bjfu.paperSystem.reviewer.controller;

import com.bjfu.paperSystem.javabeans.Review;
import com.bjfu.paperSystem.reviewer.service.reviewerService;
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
@RequestMapping("/reviewer")
public class reviewerController {
    @Autowired
    private reviewerService revService;

    @GetMapping
    public String reviewer() {
        return "reviewer";
    }

    @GetMapping("pendingView")
    public String toPendingViewPage() {
        return "reviewer/pendingView";
    }

    @GetMapping("filter")
    public String filter(@RequestParam(required = false)
                             @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
                             LocalDateTime startTime,
                         @RequestParam(required = false)
                             @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
                             LocalDateTime endTime,
                         Model model) {
        List<Review> list = revService.filterByTime(startTime, endTime);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("reviewList", list);
        return "reviewer/pendingView";
    }
}
