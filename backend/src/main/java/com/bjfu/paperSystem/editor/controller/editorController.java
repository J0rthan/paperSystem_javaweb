package com.bjfu.paperSystem.editor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/editor")
public class editorController {
    @GetMapping
    public String editor() {
        return "editor";
    }
}
