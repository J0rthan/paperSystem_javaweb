package com.bjfu.paperSystem.optionAdmin.controller;

import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.optionAdmin.service.optionAdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/optionadmin/profile")
public class OptionAdminProfileController {
    
    @Autowired
    private optionAdminService optionAdminService;
    
    @GetMapping
    public String profile(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/index";
        
        // 获取当前登录用户的详细信息
        User user = optionAdminService.getUserById(loginUser.getUserId());
        model.addAttribute("user", user);
        
        return "optionadmin/profile";
    }
    
    @PostMapping("/update")
    public String updateProfile(User user, HttpSession session, RedirectAttributes ra) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/index";
        
        // 确保只能修改当前登录用户的信息
        user.setUserId(loginUser.getUserId());
        
        // 更新用户信息
        String errorMsg = optionAdminService.updateProfile(user, loginUser.getUserId());
        
        if (errorMsg != null) {
            ra.addFlashAttribute("error", errorMsg);
        } else {
            // 更新session中的用户信息
            User updatedUser = optionAdminService.getUserById(loginUser.getUserId());
            session.setAttribute("loginUser", updatedUser);
            ra.addFlashAttribute("msg", "个人信息修改成功！");
        }
        
        return "redirect:/optionadmin/profile";
    }
}