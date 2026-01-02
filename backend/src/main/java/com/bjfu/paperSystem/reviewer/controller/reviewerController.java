package com.bjfu.paperSystem.reviewer.controller;

import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.author.service.authorService;
import com.bjfu.paperSystem.javabeans.Review;
import com.bjfu.paperSystem.reviewer.service.reviewerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
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

        for (Review r : list) {
            Manuscript m = r.getManuScript();
            System.out.println("reviewId=" + r.getReviewId()
                    + ", manuNull=" + (m == null)
                    + ", manuscriptPath=[" + (m == null ? null : m.getManuscriptPath()) + "]");
        }

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

    @GetMapping("download")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> downloadFile(
            @RequestParam("path") String filePath,
            HttpSession session) {
        if (session.getAttribute("loginUser") == null) {
            return org.springframework.http.ResponseEntity.status(403).build();
        }
        try {
            String projectPath = System.getProperty("user.dir");
            File file = new File(projectPath + "/backend/src/main/resources/static" + filePath);
            if (!file.exists()) {
                String classPath = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("").getPath(), "UTF-8");
                if (System.getProperty("os.name").toLowerCase().contains("win") && classPath.startsWith("/")) {
                    classPath = classPath.substring(1);
                }
                file = new File(classPath + "static" + filePath);
            }

            if (!file.exists()) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }

            org.springframework.core.io.Resource resource = new org.springframework.core.io.FileSystemResource(file);
            String fileName = file.getName();
            String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");

            return org.springframework.http.ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError().build();
        }
    }
}
