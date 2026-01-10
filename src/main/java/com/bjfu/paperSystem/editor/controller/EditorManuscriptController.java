package com.bjfu.paperSystem.editor.controller;

import com.bjfu.paperSystem.dto.EditorManuscriptDTO;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Record_Allocation;
import com.bjfu.paperSystem.javabeans.Review;
import com.bjfu.paperSystem.javabeans.User;
import com.bjfu.paperSystem.editor.dao.EditorManuscriptDao;
import com.bjfu.paperSystem.editor.dao.EditorReviewDao;
import com.bjfu.paperSystem.editor.dao.ManuscriptAuthorsDao;
import com.bjfu.paperSystem.editor.dao.EditorRecordAllocationDao; // 使用您现有的 DAO
import com.bjfu.paperSystem.editor.service.EditorProcessService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/editor")
public class EditorManuscriptController {

    @Autowired private EditorManuscriptDao manuscriptDao;
    @Autowired private EditorProcessService editorProcessService;
    @Autowired private ManuscriptAuthorsDao manuscriptAuthorsDao;
    @Autowired private EditorReviewDao reviewDao;

    @Autowired
    private EditorRecordAllocationDao editorRecordAllocationDao;

    // 注意：主工作台路由已由 EditorDashboardController 处理
    // 此Controller可以删除或用于其他功能
}