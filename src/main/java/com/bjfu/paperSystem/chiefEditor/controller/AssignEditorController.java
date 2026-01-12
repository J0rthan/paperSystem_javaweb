package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.chiefEditor.dao.ChiefEditorUserDao;
import com.bjfu.paperSystem.chiefEditor.service.AssignEditorService;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/chiefeditor")
public class AssignEditorController {

    @Autowired
    private AssignEditorService assignEditorService;
    
    @Autowired
    private ChiefEditorUserDao chiefEditorUserDao;

    // 列表页
    @GetMapping("/assign-editor")
    public String assignEditorPage(Model model) {
            List<Manuscript> manuscripts = assignEditorService.getToAssignManuscripts();
            // 直接查询所有user_type为editor的用户
            List<User> editors = chiefEditorUserDao.findByUserTypeIgnoreCase("editor");
            model.addAttribute("manuscripts", manuscripts != null ? manuscripts : List.of());
            model.addAttribute("editors", editors != null ? editors : List.of());

        return "chiefeditor/assign-editor";
    }

    // 提交指派
    @PostMapping("/assign-editor/do")
    public String doAssign(@RequestParam int manuscriptId,
                           @RequestParam int editorId, // 表单中选中的编辑ID
                           @RequestParam String reason,
                           HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/Login.html";
        }
        int userId = loginUser.getUserId();
        
        // 传递selectedEditorId(表单选中的编辑ID)和userId(当前登录用户ID)
        assignEditorService.assignEditor(manuscriptId, editorId, userId, reason);
        return "redirect:/chiefeditor/assign-editor";
    }
}