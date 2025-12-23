package com.bjfu.paperSystem.chiefEditor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chiefeditor")
public class chiefEditorController {
    @GetMapping
    public String chiefEditor() { return "chiefeditor"; }
}
