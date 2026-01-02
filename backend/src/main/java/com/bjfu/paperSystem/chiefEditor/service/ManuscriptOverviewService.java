package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.javabeans.Manuscript;
import java.util.List;

public interface ManuscriptOverviewService {
    // 1. 返回所有稿件（最简单版本）
    List<Manuscript> getAllManuscripts();

    // tabType: all, pending, under_review, decision, revision
    // keyword: 搜索标题或作者
    List<Manuscript> getManuscriptsByTabAndKeyword(String tabType, String keyword);
}
