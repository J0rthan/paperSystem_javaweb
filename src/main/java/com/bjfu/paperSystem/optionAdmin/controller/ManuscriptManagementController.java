//查看所有稿件列表
//查看稿件详情
//通过稿件审核
//拒绝稿件审核
//拒绝稿件并发送反馈
package com.bjfu.paperSystem.optionAdmin.controller;

import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.javabeans.EmailMessage;
import com.bjfu.paperSystem.optionAdmin.service.optionAdminService;
import java.io.File;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/optionadmin")
public class ManuscriptManagementController {
    @Autowired
    private optionAdminService service;

    // 查看所有稿件
    @GetMapping("/manuscripts")
    public String getAllManuscripts(HttpSession session, Model model) {
        List<Manuscript> list = service.getAllManuscripts();
        model.addAttribute("manuscripts", list);
        return "optionadmin/manuscripts";
    }

    // 查看单个稿件详情
    @GetMapping("/manuscripts/{id}")
    public String getManuscriptDetails(@PathVariable("id") Integer manuscriptId, Model model) {
        Manuscript manuscript = service.getManuscriptById(manuscriptId);
        model.addAttribute("manuscript", manuscript);
        return "optionadmin/manuscript-details";
    }

    

    // 通过稿件审核
    @PostMapping("/manuscripts/approve")
    public String approveManuscript(@RequestParam("manuscriptId") Integer manuscriptId, HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        // 使用当前用户的ID作为操作ID
        Integer operatorId = loginUser.getUserId();
        service.approveManuscript(manuscriptId, operatorId);
        return "redirect:/optionadmin/manuscripts";
    }

    // 拒绝稿件审核
    @PostMapping("/manuscripts/reject")
    public String rejectManuscript(@RequestParam("manuscriptId") Integer manuscriptId, HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        // 使用当前用户的ID作为操作ID
        Integer operatorId = loginUser.getUserId();
        service.rejectManuscript(manuscriptId, operatorId);
        return "redirect:/optionadmin/manuscripts";
    }
    
    // 拒绝稿件并发送反馈
    @PostMapping("/manuscripts/rejectWithFeedback")
    public String rejectWithFeedback(@RequestParam("manuscriptId") Integer manuscriptId,
                                   @RequestParam("messageBody") String messageBody,
                                   HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        // 使用当前用户的ID作为操作ID
        Integer operatorId = loginUser.getUserId();
        
        // 调用服务层方法处理拒绝和发送反馈
        service.rejectManuscriptWithFeedback(manuscriptId, operatorId, messageBody, loginUser.getEmail());
        
        return "redirect:/optionadmin/manuscripts";
    }
    
    // 下载文件方法
    @GetMapping("/manuscripts/download")
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
            if (!file.exists()) return org.springframework.http.ResponseEntity.notFound().build();

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
