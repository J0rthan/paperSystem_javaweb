package com.bjfu.paperSystem.editor.controller;

import com.bjfu.paperSystem.javabeans.*;
import com.bjfu.paperSystem.editor.service.EditorProcessService;
import com.bjfu.paperSystem.editor.dao.*;
import com.bjfu.paperSystem.javabeans.ClientMessage;
import com.bjfu.paperSystem.clientMessageUtils.Service.clientMessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/editor")
public class EditorDashboardController {

    @Autowired
    private EditorManuscriptDao manuscriptDao;
    @Autowired
    private EditorRecordAllocationDao recordAllocationDao;
    @Autowired
    private EditorProcessService processService;
    @Autowired
    private EditorUserDao userDao;
    @Autowired
    private EditorReviewDao reviewDao;
    @Autowired
    private clientMessageService clientMsgService;

    /**
     * 主工作台页面（类似author/list）
     */
    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/Login.html";

        // 获取分配给该编辑的所有稿件
        List<Record_Allocation> allocations = recordAllocationDao.findByEditorId(loginUser.getUserId());
        List<Integer> manuIds = allocations.stream()
                .map(Record_Allocation::getManuscriptId)
                .toList();

        List<Manuscript> allManuscripts = manuIds.isEmpty()
                ? new ArrayList<>()
                : manuscriptDao.findAllById(manuIds);

        // 按状态分类稿件
        Map<String, List<Manuscript>> categorized = categorizeManuscripts(allManuscripts);

        model.addAllAttributes(categorized);
        model.addAttribute("username", loginUser.getFullName());
        return "editor/dashboard";
    }

    /**
     * AJAX接口：获取某个稿件的详情（用于弹窗或侧边栏展示）
     */
    @GetMapping("/api/manuscript/{id}")
    @ResponseBody
    public Map<String, Object> getManuscriptDetail(@PathVariable int id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        Manuscript manuscript = processService.getManuscriptDetail(id, loginUser.getUserId());
        if (manuscript == null) {
            result.put("success", false);
            result.put("message", "稿件不存在或无权限");
            return result;
        }

        // 获取作者信息
        User author = userDao.findById(manuscript.getAuthorId()).orElse(null);

        result.put("success", true);
        result.put("manuscript", manuscript);
        result.put("author", author);
        return result;
    }

    /**
     * AJAX接口：获取 Need Revision 状态的稿件列表（用于与作者沟通模块）
     */
    @GetMapping("/api/manuscripts/need-revision")
    @ResponseBody
    public Map<String, Object> getNeedRevisionManuscripts(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        try {
            // 获取分配给该编辑的所有稿件
            List<Record_Allocation> allocations = recordAllocationDao.findByEditorId(loginUser.getUserId());
            List<Integer> manuIds = allocations.stream()
                    .map(Record_Allocation::getManuscriptId)
                    .toList();

            List<Manuscript> allManuscripts = manuIds.isEmpty()
                    ? new ArrayList<>()
                    : manuscriptDao.findAllById(manuIds);

            // 筛选出状态为 "Need Revision" 且 editor_id 等于当前编辑的稿件
            List<Manuscript> needRevisionManuscripts = allManuscripts.stream()
                    .filter(m -> "Need Revision".equals(m.getStatus()))
                    .filter(m -> m.getEditorId() != null && m.getEditorId().equals(loginUser.getUserId()))
                    .collect(java.util.stream.Collectors.toList());

            // 转换为简单格式用于前端显示
            List<Map<String, Object>> manuscriptList = new ArrayList<>();
            for (Manuscript m : needRevisionManuscripts) {
                Map<String, Object> item = new HashMap<>();
                item.put("manuscriptId", m.getManuscriptId());
                item.put("title", m.getTitle() != null ? m.getTitle() : "");
                item.put("abstractText", m.getAbstractText() != null ? m.getAbstractText() : "");
                item.put("status", m.getStatus());
                item.put("submitTime", m.getSubmitTime());
                manuscriptList.add(item);
            }

            result.put("success", true);
            result.put("manuscripts", manuscriptList);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * AJAX接口：获取某个稿件的沟通历史
     */
    @GetMapping("/api/manuscript/{id}/messages")
    @ResponseBody
    public Map<String, Object> getCommunicationHistory(@PathVariable int id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            return result;
        }

        List<ClientMessage> messages = processService.getCommunicationHistory(id, loginUser.getUserId());
        result.put("success", true);
        result.put("messages", messages);
        return result;
    }

    /**
     * AJAX接口：发送消息给作者
     */
    @PostMapping("/api/message/send")
    @ResponseBody
    public Map<String, Object> sendMessage(
            @RequestParam("manuscriptId") int manuscriptId,
            @RequestParam("messageBody") String messageBody,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        try {
            Manuscript manuscript = processService.getManuscriptDetail(manuscriptId, loginUser.getUserId());
            if (manuscript == null) {
                result.put("success", false);
                result.put("message", "稿件不存在或无权限");
                return result;
            }

            processService.sendMessageToAuthor(manuscriptId, manuscript.getAuthorId(), messageBody, loginUser.getUserId());
            result.put("success", true);
            result.put("message", "消息发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "发送失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * AJAX接口：获取已逾期的审稿任务列表（只显示已逾期的，添加逾期天数字段）
     */
    @GetMapping("/api/reviews/pending")
    @ResponseBody
    public Map<String, Object> getPendingReviews(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            User loginUser = (User) session.getAttribute("loginUser");
            if (loginUser == null) {
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }

            // 获取该编辑的所有稿件
            List<Record_Allocation> allocations = recordAllocationDao.findByEditorId(loginUser.getUserId());
            
            if (allocations == null || allocations.isEmpty()) {
                result.put("success", true);
                result.put("pendingList", new ArrayList<>());
                result.put("message", "暂无分配的稿件");
                return result;
            }

            List<Integer> manuIds = allocations.stream()
                    .map(Record_Allocation::getManuscriptId)
                    .toList();

            List<Map<String, Object>> pendingList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            
            for (Integer manuId : manuIds) {
                try {
                    List<Review> reviews = reviewDao.findByManuId(manuId);
                    
                    for (Review review : reviews) {
                        try {
                            // 只显示已逾期的审稿人（状态为accepted或pending，且截止日期已过）
                            if (review.getDeadline() != null &&
                                    ("accepted".equalsIgnoreCase(review.getStatus()) || "pending".equalsIgnoreCase(review.getStatus()))) {
                                
                                boolean isOverdue = review.getDeadline().isBefore(now);
                                
                                if (isOverdue) {
                                    Manuscript manuscript = manuscriptDao.findById(manuId).orElse(null);
                                    User reviewer = userDao.findById(review.getReviewerId()).orElse(null);

                                    // 计算已逾期天数
                                    long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(
                                            review.getDeadline().toLocalDate(), 
                                            now.toLocalDate()
                                    );

                                    Map<String, Object> item = new HashMap<>();
                                    item.put("reviewId", review.getReviewId());
                                    item.put("manuscriptId", manuId);
                                    item.put("manuscriptTitle", manuscript != null ? manuscript.getTitle() : "");
                                    item.put("reviewerName", reviewer != null ? reviewer.getFullName() : "");
                                    item.put("reviewerEmail", reviewer != null ? reviewer.getEmail() : "");
                                    item.put("deadline", review.getDeadline() != null ? 
                                            review.getDeadline().toString() : null);
                                    item.put("overdueDays", overdueDays);
                                    pendingList.add(item);
                                }
                            }
                        } catch (Exception e) {
                            // 继续处理下一条记录，不中断整个流程
                        }
                    }
                } catch (Exception e) {
                    // 继续处理下一个稿件，不中断整个流程
                }
            }

            result.put("success", true);
            result.put("pendingList", pendingList);
            return result;
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
            result.put("pendingList", new ArrayList<>());
            return result;
        }
    }

    /**
     * AJAX接口：获取有逾期审稿人的稿件列表
     */
    @GetMapping("/api/reviews/overdue/manuscripts")
    @ResponseBody
    public Map<String, Object> getOverdueManuscripts(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        try {
            List<Map<String, Object>> manuscripts = processService.getOverdueManuscripts(loginUser.getUserId());
            result.put("success", true);
            result.put("manuscripts", manuscripts);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * AJAX接口：根据稿件ID获取逾期审稿人列表
     */
    @GetMapping("/api/reviews/overdue/reviewers")
    @ResponseBody
    public Map<String, Object> getOverdueReviewers(
            @RequestParam("manuscriptId") int manuscriptId,
            HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            User loginUser = (User) session.getAttribute("loginUser");
            if (loginUser == null) {
                result.put("success", false);
                result.put("message", "未登录");
                return result;
            }

            // 权限验证：检查Editor是否被分配了该稿件
            Manuscript manuscript = processService.getManuscriptDetail(manuscriptId, loginUser.getUserId());
            if (manuscript == null) {
                result.put("success", false);
                result.put("message", "稿件不存在或无权限");
                return result;
            }

            List<Map<String, Object>> reviewers = processService.getOverdueReviewers(manuscriptId);
            result.put("success", true);
            result.put("reviewers", reviewers);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取失败：" + e.getMessage());
            result.put("reviewers", new ArrayList<>());
        }
        return result;
    }

    /**
     * AJAX接口：发送催审提醒（支持自定义邮件内容）
     */
    @PostMapping("/api/review/remind")
    @ResponseBody
    public Map<String, Object> remindReviewer(
            @RequestParam("reviewId") int reviewId,
            @RequestParam(value = "customContent", required = false) String customContent,
            @RequestParam(value = "subject", required = false) String subject,
            HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        try {
            // 如果提供了自定义内容，使用自定义内容发送；否则使用默认模板
            if (customContent != null && !customContent.trim().isEmpty()) {
                processService.sendCustomReminder(reviewId, subject, customContent, loginUser.getUserId());
            } else {
                processService.sendManualReminder(reviewId, loginUser.getUserId());
            }
            result.put("success", true);
            result.put("message", "催审邮件已发送");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "发送失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 下载文件接口（参考 author 的实现）
     */
    @GetMapping("/download")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(
            @RequestParam("path") String filePath,
            HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(403).build();
        }
        
        try {
            String projectPath = System.getProperty("user.dir");
            File file = new File(projectPath + "/src/main/resources/static" + filePath);
            if (!file.exists()) {
                String classPath = java.net.URLDecoder.decode(
                    this.getClass().getClassLoader().getResource("").getPath(), "UTF-8");
                if (System.getProperty("os.name").toLowerCase().contains("win") && classPath.startsWith("/")) {
                    classPath = classPath.substring(1);
                }
                file = new File(classPath + "static" + filePath);
            }
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            org.springframework.core.io.Resource resource = new FileSystemResource(file);
            String fileName = file.getName();
            String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + encodedFileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * AJAX接口：检查新消息数量
     * 统计该编辑收到的、属于其被分配稿件的消息数量（只统计接收到的消息，不统计发送的）
     */
    @GetMapping("/api/message/unreadCount")
    @ResponseBody
    public Map<String, Object> getUnreadMessageCount(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            return result;
        }

        try {
            // 1. 获取分配给该编辑的所有稿件ID
            List<Record_Allocation> allocations = recordAllocationDao.findByEditorId(loginUser.getUserId());
            List<Integer> manuIds = allocations.stream()
                    .map(Record_Allocation::getManuscriptId)
                    .collect(java.util.stream.Collectors.toList());

            if (manuIds.isEmpty()) {
                result.put("success", true);
                result.put("unreadCount", 0);
                return result;
            }

            // 2. 获取该编辑收到的所有消息
            List<ClientMessage> receivedMsgs = clientMsgService.findMessageByReceiver(loginUser.getUserId());

            // 3. 过滤出属于被分配稿件的消息（只统计接收到的，不统计自己发送的）
            long unreadCount = receivedMsgs.stream()
                    .filter(msg -> msg.getManuId() != null && manuIds.contains(msg.getManuId()))
                    .count();

            // 可选：进一步筛选最近24小时内的消息
            // LocalDateTime yesterday = LocalDateTime.now().minusHours(24);
            // unreadCount = receivedMsgs.stream()
            //         .filter(msg -> msg.getManuId() != null && manuIds.contains(msg.getManuId()))
            //         .filter(msg -> msg.getSendingTime() != null && msg.getSendingTime().isAfter(yesterday))
            //         .count();

            result.put("success", true);
            result.put("unreadCount", unreadCount);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("unreadCount", 0);
        }

        return result;
    }

    // 辅助方法：按状态分类稿件
    // 只返回4种状态：编辑处理中(With Editor)、审稿中(Under Review)、待提交建议(With Editor II)、需要返修(Need Revision)
    private Map<String, List<Manuscript>> categorizeManuscripts(List<Manuscript> manuscripts) {
        Map<String, List<Manuscript>> result = new HashMap<>();
        result.put("withEditor", new ArrayList<>());      // 编辑处理中
        result.put("underReview", new ArrayList<>());     // 审稿中
        result.put("pendingRecommendation", new ArrayList<>()); // 待提交建议 (With Editor II)
        result.put("needRevision", new ArrayList<>());    // 需要返修

        for (Manuscript m : manuscripts) {
            String status = m.getStatus();
            if ("With Editor".equals(status)) {
                result.get("withEditor").add(m);
            } else if ("Under Review".equals(status)) {
                result.get("underReview").add(m);
            } else if ("With Editor II".equals(status)) {
                result.get("pendingRecommendation").add(m);
            } else if ("Need Revision".equals(status)) {
                result.get("needRevision").add(m);
            }
            // 注意：已决议的稿件(Accepted/Rejected)不在此列表显示
        }

        return result;
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index";
    }

    /**
     * 消息中心页面
     */
    @GetMapping("/message/list")
    public String messageList(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/Login.html";

        // 获取Editor参与的所有稿件的消息
        // 1. 获取分配给该编辑的所有稿件
        List<Record_Allocation> allocations = recordAllocationDao.findByEditorId(loginUser.getUserId());
        List<Integer> manuIds = allocations.stream()
                .map(Record_Allocation::getManuscriptId)
                .collect(java.util.stream.Collectors.toList());

        // 2. 获取所有发送和接收的消息
        List<ClientMessage> sentMsgs = clientMsgService.findMessageBySender(loginUser.getUserId());
        List<ClientMessage> receivedMsgs = clientMsgService.findMessageByReceiver(loginUser.getUserId());

        // 3. 过滤出只属于该编辑被分配稿件的消息
        List<ClientMessage> filteredMsgs = new ArrayList<>();
        for (ClientMessage msg : sentMsgs) {
            if (manuIds.contains(msg.getManuId())) {
                filteredMsgs.add(msg);
            }
        }
        for (ClientMessage msg : receivedMsgs) {
            if (manuIds.contains(msg.getManuId())) {
                filteredMsgs.add(msg);
            }
        }

        // 4. 按时间排序（最新在前）
        List<ClientMessage> sortedMsgs = filteredMsgs.stream()
                .filter(msg -> msg.getSendingTime() != null)
                .sorted((m1, m2) -> m2.getSendingTime().compareTo(m1.getSendingTime()))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("clientMsgs", sortedMsgs);
        model.addAttribute("username", loginUser.getFullName());
        return "editor/message_list";
    }

    /**
     * AJAX接口：搜索审稿人
     */
    @GetMapping("/api/reviewers/search")
    @ResponseBody
    public Map<String, Object> searchReviewers(@RequestParam(required = false) String keyword, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        try {
            List<User> reviewers = processService.searchReviewers(keyword);
            // 转换为简单格式用于前端显示
            List<Map<String, Object>> reviewerList = new ArrayList<>();
            for (User r : reviewers) {
                Map<String, Object> reviewer = new HashMap<>();
                reviewer.put("userId", r.getUserId());
                reviewer.put("fullName", r.getFullName());
                reviewer.put("email", r.getEmail());
                reviewer.put("investigationDirection", r.getInvestigationDirection());
                reviewerList.add(reviewer);
            }
            result.put("success", true);
            result.put("reviewers", reviewerList);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "搜索失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * AJAX接口：获取已邀请的审稿人列表
     */
    @GetMapping("/api/reviews/existing")
    @ResponseBody
    public Map<String, Object> getExistingReviews(@RequestParam("manuscriptId") int manuscriptId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        try {
            // 权限验证：检查Editor是否被分配了该稿件
            Manuscript manuscript = processService.getManuscriptDetail(manuscriptId, loginUser.getUserId());
            if (manuscript == null) {
                result.put("success", false);
                result.put("message", "稿件不存在或无权限");
                return result;
            }

            List<Review> reviews = processService.getCurrentReviews(manuscriptId);
            // 过滤掉已撤销的邀请
            List<Review> validReviews = reviews.stream()
                    .filter(r -> !"undo".equalsIgnoreCase(r.getStatus()))
                    .collect(java.util.stream.Collectors.toList());

            // 转换为前端格式
            List<Map<String, Object>> reviewList = new ArrayList<>();
            for (Review r : validReviews) {
                User reviewer = userDao.findById(r.getReviewerId()).orElse(null);
                Map<String, Object> reviewItem = new HashMap<>();
                reviewItem.put("reviewId", r.getReviewId());
                reviewItem.put("reviewerId", r.getReviewerId());
                reviewItem.put("reviewerName", reviewer != null ? reviewer.getFullName() : "Unknown");
                reviewItem.put("reviewerEmail", reviewer != null ? reviewer.getEmail() : "");
                reviewItem.put("status", r.getStatus());
                reviewItem.put("deadline", r.getDeadline());
                reviewItem.put("invitationTime", r.getInvitationTime());
                reviewList.add(reviewItem);
            }

            result.put("success", true);
            result.put("reviews", reviewList);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取失败：" + e.getMessage());
        }
        return result;
    }

}