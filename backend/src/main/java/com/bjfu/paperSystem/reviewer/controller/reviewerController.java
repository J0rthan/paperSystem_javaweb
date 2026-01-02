package com.bjfu.paperSystem.reviewer.controller;

import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.author.service.authorService;
import com.bjfu.paperSystem.javabeans.Review;
import com.bjfu.paperSystem.reviewer.service.reviewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reviewer")
public class reviewerController {
    @Autowired
    private reviewerService revService;

    @Autowired
    private authorService autService;

    @GetMapping
    public String toReviewerPage() {
        return "/reviewer";
    }

    @GetMapping("pendingView")
    public String toPendingViewPage(Model model) {
        List<Review> list = revService.filterByTime(null, null);
        model.addAttribute("reviewList", list);
        return "/reviewer/pendingView";
    }

    @GetMapping("reviewJobs")
    public String toReviewJobsPage(Model model) {
        List<Review> list = revService.filterByStatus("accepted");
        model.addAttribute("jobList", list);

        return "/reviewer/jobPage";
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
        return "/reviewer/pendingView";
    }

    @GetMapping("getManuSummary/{manuId}")
    @ResponseBody
    public String getManuSummary(@PathVariable int manuId) {
        return revService.findByManuId(manuId).getAbstractText();
    }

    @PostMapping("invitations/accept")
    @ResponseBody
    public String acceptManu(@RequestParam("manuId") int manuId, @RequestParam("reviewId") int reviewId) {
        return revService.acceptManu(reviewId, manuId);
    }

    @PostMapping("invitations/reject")
    @ResponseBody
    public String rejectManu(@RequestParam("manuId") int manuId, @RequestParam("reviewId") int reviewId) {
        return revService.rejectManu(reviewId, manuId);
    }

    @GetMapping("submitOpinionPage/{reviewId}")
    public String tpSubmitOpinionPage(@PathVariable int reviewId, Model model) {
        System.out.println(reviewId);
        Review review = revService.findByRevId(reviewId);
        if (review == null) {
            System.out.println("null");
        }
        else {
            System.out.println("not null");
        }

        System.out.println(review.getManuScript().getTitle());
        model.addAttribute("review", review);
        return "/reviewer/submitOpinionPage";
    }

    @PostMapping("submitOpinion")
    public String handleSubmitOpinion() {
        return "";
    }
}
