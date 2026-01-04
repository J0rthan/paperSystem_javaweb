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

    // === 修改点：注入您提供的 EditorRecordAllocationDao ===
    @Autowired
    private EditorRecordAllocationDao editorRecordAllocationDao;

    // 首页
    @GetMapping
    public String index(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/Login.html";
        model.addAttribute("username", loginUser.getFullName());
        return "editor";
    }

    @GetMapping("/manuscripts")
    public String getManuscriptList(Model model, HttpSession session) {
        // 1. 登录检查
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/Login.html";

        // ================== 【基于分配表获取稿件】 ==================
        // 2.1 调用 EditorRecordAllocationDao 查该编辑被分配了哪些记录
        //
        List<Record_Allocation> allocations = editorRecordAllocationDao.findByuserId(loginUser.getUserId());

        // 2.2 提取出所有的 manuscriptId
        List<Integer> manuIds = allocations.stream()
                .map(Record_Allocation::getManuscriptId)
                .collect(Collectors.toList());

        // 2.3 判空处理：如果没有分配任务，不查数据库
        List<Manuscript> manuscripts;
        if (manuIds.isEmpty()) {
            manuscripts = new ArrayList<>();
        } else {
            // 2.4 根据 ID 列表去查具体的稿件信息
            manuscripts = manuscriptDao.findAllById(manuIds);
        }
        // ==========================================================

        // 3. 【状态检查逻辑】(针对查出来的这些稿件进行检查)
        for (Manuscript m : manuscripts) {
            // 只有 With Editor 才检查，提升性能
            if ("With Editor".equalsIgnoreCase(m.getStatus())) {
                editorProcessService.checkAndUpdateManuscriptStatus(m.getManuscriptId());
            }
        }

        // 4. 【再次查询】刷新数据状态
        if (!manuIds.isEmpty()) {
            manuscripts = manuscriptDao.findAllById(manuIds);
        }

        // 5. 【转换为 DTO】(适配前端显示)
        List<EditorManuscriptDTO> dtoList = new ArrayList<>();
        for (Manuscript m : manuscripts) {

            // (1) 获取作者单位信息
            String authorInfo = manuscriptAuthorsDao.findAuthorAffiliationByManuscriptId(m.getManuscriptId());
            if (authorInfo == null) authorInfo = "暂无单位信息";

            // (2) 计算是否紧急
            boolean isUrgent = false;
            List<Review> reviews = reviewDao.findByManuId(m.getManuscriptId());
            for (Review r : reviews) {
                if (r.getDeadline() != null
                        && !"COMPLETED".equalsIgnoreCase(r.getStatus())
                        && !"REJECTED".equalsIgnoreCase(r.getStatus())
                        && LocalDateTime.now().isAfter(r.getDeadline())) {
                    isUrgent = true;
                    break;
                }
            }

            // (3) 构建 DTO
            EditorManuscriptDTO dto = new EditorManuscriptDTO(
                    m.getManuscriptId(),
                    m.getTitle(),
                    m.getStatus(),
                    m.getAssignTime(),
                    authorInfo,
                    isUrgent
            );
            dtoList.add(dto);
        }

        model.addAttribute("manuscripts", dtoList);
        model.addAttribute("username", loginUser.getFullName());

        return "editor/manuscripts";
    }
}