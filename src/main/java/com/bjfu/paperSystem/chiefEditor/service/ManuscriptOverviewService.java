package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.javabeans.Manuscript;
import java.util.List;

public interface ManuscriptOverviewService {
    // 1. 返回所有稿件（最简单版本）
    List<Manuscript> getAllManuscripts();

    // tabType: all, pending, under_review, decision, revision
    // keyword: 搜索标题或作者
    List<Manuscript> getManuscriptsByTabAndKeyword(String tabType, String keyword);
    
    // 直接根据状态过滤稿件
    List<Manuscript> getManuscriptsByStatus(String status, String keyword);
    
    // 根据ID获取稿件详情
    Manuscript getManuscriptDetail(int manuscriptId);
}
