package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.chiefEditor.service.AssignEditorService;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;
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

    // 列表页
    @GetMapping("/assign-editor")
    public String assignEditorPage(Model model) {
        List<Manuscript> manuscripts = assignEditorService.getToAssignManuscripts();
        List<User> editors = assignEditorService.getAvailableEditors();
        model.addAttribute("manuscripts", manuscripts);
        model.addAttribute("editors", editors);
        return "chiefeditor/assign-editor";
    }

    // 提交指派
    @PostMapping("/assign-editor/do")
    public String doAssign(@RequestParam int manuscriptId,
                           @RequestParam int editorId) {
        assignEditorService.assignEditor(manuscriptId, editorId);
        return "redirect:/chiefeditor/assign-editor";
    }
}