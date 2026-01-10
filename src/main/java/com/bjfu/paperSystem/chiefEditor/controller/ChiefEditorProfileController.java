package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.chiefEditor.dao.ChiefEditorUserDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/chiefeditor/profile")
public class ChiefEditorProfileController {

    @Autowired
    private ChiefEditorUserDao chiefEditorUserDao;

    @GetMapping
    public String profile(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/index";
        }
        model.addAttribute("user", chiefEditorUserDao.findById(loginUser.getUserId()).orElse(null));
        return "chiefeditor/profile";
    }

    @PostMapping("/update")
    public String update(User user, HttpSession session, RedirectAttributes ra) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/index";
        }
        
        // 检查用户名是否被其他用户占用
        User duplicateUser = chiefEditorUserDao.findByUserName(user.getUserName());
        if (duplicateUser != null && duplicateUser.getUserId() != loginUser.getUserId()) {
            ra.addFlashAttribute("error", "用户名已被占用，请更换其他账号名");
            return "redirect:/chiefeditor/profile";
        }
        
        // 获取数据库中的用户并更新信息
        User dbUser = chiefEditorUserDao.findById(loginUser.getUserId()).orElse(null);
        if (dbUser == null) {
            ra.addFlashAttribute("error", "用户不存在");
            return "redirect:/chiefeditor/profile";
        }
        
        // 更新用户信息
        dbUser.setUserName(user.getUserName());
        dbUser.setFullName(user.getFullName());
        dbUser.setEmail(user.getEmail());
        dbUser.setCompany(user.getCompany());
        dbUser.setInvestigationDirection(user.getInvestigationDirection());
        
        // 保存更新
        chiefEditorUserDao.save(dbUser);
        
        // 更新session中的用户信息
        session.setAttribute("loginUser", dbUser);
        
        ra.addFlashAttribute("msg", "信息修改成功！");
        return "redirect:/chiefeditor/profile";
    }
}
