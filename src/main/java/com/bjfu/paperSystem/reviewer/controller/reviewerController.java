package com.bjfu.paperSystem.reviewer.controller;

import com.bjfu.paperSystem.clientMessageUtils.Service.clientMessageService;
import com.bjfu.paperSystem.javabeans.*;
import com.bjfu.paperSystem.author.service.authorService;
import com.bjfu.paperSystem.mailUtils.Service.mailService;
import com.bjfu.paperSystem.mailUtils.MailUtil;
import com.bjfu.paperSystem.reviewer.service.reviewerService;
import com.bjfu.paperSystem.superAdmin.service.superAdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
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

    @Autowired
    private superAdminService suService;

    @Autowired
    private mailService mSerivice;

    @Autowired
    private clientMessageService cService;

    private final MailUtil mailUtil1;

    public reviewerController(MailUtil mailUtil) {
        this.mailUtil1 = mailUtil;
    }

    @GetMapping
    public String toReviewerPage() {
        return "/reviewer";
    }

    @GetMapping("pendingView")
    public String toPendingViewPage(Model model, HttpSession session) {
        int reviewer_id = (int) session.getAttribute("user_id");
        List<Review> list = revService.filterByTime(null, null, reviewer_id);
        model.addAttribute("reviewList", list);
        return "/reviewer/pendingView";
    }

    @GetMapping("reviewJobs")
    public String toReviewJobsPage(Model model, HttpSession session) {
        int reviewer_id = (int) session.getAttribute("user_id");
        List<Review> list = revService.filterByStatus("accepted", reviewer_id);
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
                         Model model, HttpSession session) {
        int reviewer_id = (int) session.getAttribute("user_id");
        List<Review> list = revService.filterByTime(null, null, reviewer_id);
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
        model.addAttribute("editors",
                suService.findUserByType("editor"));
        if (suService.findUserByType("editor").isEmpty()) {
            System.out.println("it's empty");
        }
        else {
            System.out.println("it's not empty");
        }
        return "/reviewer/submitOpinionPage";
    }

    @PostMapping("submitOpinion")
    @ResponseBody
    public String handleSubmitOpinion(
            @RequestParam Integer reviewId, // 当前reviewId 可以查到编辑和作者id
            @RequestParam Integer scoreNovelty, // 创新性打分1-5
            @RequestParam Integer scoreMethod, // 方法论打分1-5
            @RequestParam Integer scoreQuality, // 整体质量打分1-5
            @RequestParam String keyComments, // 关键评价
            @RequestParam String recommendation, // 总体建议
            @RequestParam String commentsToAuthor // 给作者的具体修改建议
    ) {
        // 先查到编辑和作者id
        Review review = revService.findByRevId(reviewId);
        Integer reviewerId = review.getReviewerId();
        User reviewer = suService.findUserById(reviewerId);
        String reviewerEmail = reviewer.getEmail();
        Manuscript manu = review.getManuScript();
        int editor_id = manu.getEditorId();
        int author_id = manu.getAuthorId();

        // 再根据user表查具体的邮箱地址
        String editor_email = suService.findUserById(editor_id).getEmail();
        System.out.println("editor_email: " + editor_email);
        String author_email = suService.findUserById(author_id).getEmail();
        System.out.println("author_email: " + author_email);

        // 开始发送邮件信息
        String toEditorMessage = String.format("""
                创新性打分:%d,
                方法论打分:%d,
                整体质量打分: %d,
                关键评价: %s,
                整体建议: %s
                """, scoreNovelty, scoreMethod, scoreQuality, keyComments, recommendation);
        String toAuthorMessage = String.format("""
                具体修改建议: %s
                """, commentsToAuthor);

        mailUtil1.sendTextMail(editor_email, "给编辑的意见", toEditorMessage);
        mailUtil1.sendTextMail(author_email, "给作者的建议", toAuthorMessage);
        // 发送邮件的同时写入邮件信息表
        mSerivice.insertRecord(reviewerEmail, author_email, toAuthorMessage, manu.getManuscriptId(), manu);
        mSerivice.insertRecord(reviewerEmail, editor_email, toEditorMessage, manu.getManuscriptId(), manu);

        String exitCode = revService.updateFinish(reviewId);

        return "意见已提交";
    }

    // 下载功能
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

    // 查看自己的消息列表
    @GetMapping("ClientMessagesPage")
    public String toClientMessagesPage(Model model, HttpSession session) {
        int reviewer_id = (int) session.getAttribute("user_id");
        List<ClientMessage> sentMessages = cService.findMessageBySender(reviewer_id);
        List<ClientMessage> receivedMessages = cService.findMessageByReceiver(reviewer_id);
        model.addAttribute("sentMessages", sentMessages);
        model.addAttribute("receivedMessage", receivedMessages);

        return "/reviewer/MessageList";
    }

    // 查看自己的邮件箱
    @GetMapping("EmailMessagesPage")
    public String toEmailMessagesPage(Model model, HttpSession session) {
        int reviewer_id = (int) session.getAttribute("user_id");
        User reviewer = suService.findUserById(reviewer_id);
        String reviewer_email = reviewer.getEmail();
        List<EmailMessage> sentMessages = mSerivice.findMessagesBySenderMail(reviewer_email);
        List<EmailMessage> receivedMessages = mSerivice.findMessagesByReceiverMail(reviewer_email);
        model.addAttribute("sentMessages", sentMessages);
        model.addAttribute("receivedMessage", receivedMessages);

        return "/reviewer/EmailMessageList";
    }

    // 退出登录
    @GetMapping("logout")
    public String toLoginPage() {
        return "redirect:/Login.html";
    }
}
