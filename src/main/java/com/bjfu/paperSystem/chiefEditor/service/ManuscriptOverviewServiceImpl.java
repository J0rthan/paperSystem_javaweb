package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManuscriptOverviewServiceImpl implements ManuscriptOverviewService {

    @Autowired
    private ManuscriptDao manuscriptDao;

    @Override
    public List<Manuscript> getAllManuscripts() {
        // JpaRepository 已经有 findAll() 方法
        return manuscriptDao.findAll();
    }

    @Override
    public List<Manuscript> getManuscriptsByTabAndKeyword(String tabType, String keyword) {
        List<Manuscript> all = manuscriptDao.findAll();

        // 1. 根据 Tab 类型过滤状态 (严格匹配9个状态)
        List<Manuscript> filteredByTab = all.stream().filter(m -> {
            String s = m.getStatus();
            if (s == null) return false;

            return switch (tabType) {
                // Tab: 待处理 (Pending)
                case "pending" ->
                        s.equals("Pending Review") ||    // ③ 待审查
                                s.equals("Pending Allocation");  // ④ 待分配

                // Tab: 审稿/处理中 (In Progress)
                case "review" ->
                        s.equals("With Editor") ||       // ⑤ 编辑处理中
                                s.equals("Under Review");        // ⑥ 审稿中

                // Tab: 需修回 (Revision)
                case "revision" ->
                        s.equals("Need Revision");       // ⑨ 需要返修

                // Tab: 已决议 (Decision)
                case "decision" ->
                        s.equals("Accepted") ||          // ⑧ 录用
                                s.equals("Rejected");            // ⑦ 拒稿

                // Tab: 全部 (All) - 包含草稿状态
                default -> true;
                // 这里会包含 ① Started Submission 和 ② Incomplete Submission
                // 主编在“全部”里能看到作者写了一半的草稿，但这通常不需要主编处理
            };
        }).collect(Collectors.toList());

        // 2. 关键字搜索逻辑不变
        if (keyword != null && !keyword.trim().isEmpty()) {
            String k = keyword.toLowerCase();
            return filteredByTab.stream()
                    .filter(m -> (m.getTitle() != null && m.getTitle().toLowerCase().contains(k)))
                    .collect(Collectors.toList());
        }

        return filteredByTab;
    }
    
    @Override
    public List<Manuscript> getManuscriptsByStatus(String status, String keyword) {
        List<Manuscript> filteredByStatus;
        
        // 1. 根据状态直接过滤稿件
        if (status == null || status.equals("all")) {
            filteredByStatus = manuscriptDao.findAll();
        } else {
            filteredByStatus = manuscriptDao.findByStatus(status);
        }

        // 2. 应用关键字搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            String k = keyword.toLowerCase();
            return filteredByStatus.stream()
                    .filter(m -> (m.getTitle() != null && m.getTitle().toLowerCase().contains(k)))
                    .collect(Collectors.toList());
        }

        return filteredByStatus;
    }
}