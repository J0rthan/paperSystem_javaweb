package com.bjfu.paperSystem.editor.controller;

import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.editor.dao.EditorUserDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/editor/profile")
public class EditorProfileController {
    
    @Autowired
    private EditorUserDao userDao;

    @GetMapping
    public String profile(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        User user = userDao.findById(loginUser.getUserId()).orElse(null);
        if (user == null) return "redirect:/editor";
        model.addAttribute("user", user);
        return "editor/profile";
    }

    @PostMapping("/update")
    public String update(User user, HttpSession session, RedirectAttributes ra) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        
        String errorMsg = updateProfile(user, loginUser.getUserId());
        if (errorMsg != null) {
            ra.addFlashAttribute("error", errorMsg);
            return "redirect:/editor/profile";
        }
        
        // 更新 session 中的用户信息
        User updatedUser = userDao.findById(loginUser.getUserId()).orElse(null);
        if (updatedUser != null) {
            session.setAttribute("loginUser", updatedUser);
        }
        ra.addFlashAttribute("msg", "信息修改成功！");
        return "redirect:/editor/profile";
    }

    /**
     * 更新个人信息（参考 author 的实现）
     */
    private String updateProfile(User user, int loginUserId) {
        User duplicateUser = userDao.findByUserName(user.getUserName());
        if (duplicateUser != null && duplicateUser.getUserId() != loginUserId) {
            return "用户名已被占用，请更换其他账号名";
        }
        
        User dbUser = userDao.findById(loginUserId).orElse(null);
        if (dbUser == null) return "用户不存在";
        
        // 更新基本信息
        dbUser.setUserName(user.getUserName());
        dbUser.setFullName(user.getFullName());
        dbUser.setEmail(user.getEmail());
        dbUser.setCompany(user.getCompany());
        dbUser.setInvestigationDirection(user.getInvestigationDirection());
        
        // 如果提供了新密码，则更新密码
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            dbUser.setPassword(user.getPassword());
        }
        
        userDao.save(dbUser);
        return null;
    }
}
