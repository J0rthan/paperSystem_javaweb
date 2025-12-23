package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.chiefEditor.service.ManuscriptOverviewService;
import com.bjfu.paperSystem.javabeans.Manuscript;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/chiefeditor")
public class ManuscriptOverviewController {
    @Autowired
    private ManuscriptOverviewService Service;

    // 新增：稿件总览
    @GetMapping("/manuscripts")
    public String manuscriptsOverview(HttpSession session, Model model) {
        // 简单写法：先不做复杂权限检查，以后可以再判断 userType 是否是 chief_editor

        List<Manuscript> list = Service.getAllManuscripts();
        model.addAttribute("manuscripts", list);

        return "chiefeditor/manuscripts"; // 对应 templates/chiefeditor/manuscripts.html
    }
}
