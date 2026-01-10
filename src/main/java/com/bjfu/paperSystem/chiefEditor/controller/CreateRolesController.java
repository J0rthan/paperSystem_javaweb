package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.chiefEditor.service.CreateRoles;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/chief_editor")
public class CreateRolesController {

    @Autowired
    private CreateRoles chiefEditorService;

    // 1. 展示“新建成员”的页面
    @GetMapping("/create_user")
    public String showCreateUserPage(HttpSession session, Model model) {
        // 这里可以加一个权限判断，确保只有主编能进
        // User user = (User) session.getAttribute("loginUser");
        // if (user == null || !"CHIEF_EDITOR".equals(user.getUserType())) return "redirect:/login";

        return "chiefeditor/create_user"; // 对应 templates/chief_editor/create_user.html
    }

    // 2. 处理“新建成员”的表单提交
    @PostMapping("/create_user")
    public String handleCreateUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam(value = "specialty", required = false) String specialty,
            @RequestParam(value = "profile", required = false) String profile,
            Model model) {

        try {
            // 调用 Service 执行具体的数据库操作
            chiefEditorService.createRole(username, password, fullName, email, role, specialty, profile);

            model.addAttribute("msg", "创建成功！用户 " + username + " 已添加为 " + role);
            // 创建成功后，可以清空表单，也可以跳转
            return "chiefeditor/create_user";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", "创建失败：" + e.getMessage());
            return "chiefeditor/create_user";
        }
    }
}