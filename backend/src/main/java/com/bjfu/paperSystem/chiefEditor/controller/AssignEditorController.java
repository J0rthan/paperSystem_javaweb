package com.bjfu.paperSystem.chiefEditor.controller;

import com.bjfu.paperSystem.chiefEditor.service.AssignEditorService;
import com.bjfu.paperSystem.javabeans.Editorial_Board;
import com.bjfu.paperSystem.javabeans.Manuscript;
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
            List<Editorial_Board> editors = assignEditorService.getAvailableBoardEditors();
            model.addAttribute("manuscripts", manuscripts != null ? manuscripts : List.of());
            model.addAttribute("editors", editors != null ? editors : List.of());

        return "chiefeditor/assign-editor";
    }

    // 提交指派
    @PostMapping("/assign-editor/do")
    public String doAssign(@RequestParam int manuscriptId,
                           @RequestParam int editorId,
                           @RequestParam String reason) {
        assignEditorService.assignEditor(manuscriptId, editorId, reason);
        return "redirect:/chiefeditor/assign-editor";
    }
}