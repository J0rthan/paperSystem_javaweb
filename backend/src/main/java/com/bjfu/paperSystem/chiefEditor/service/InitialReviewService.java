package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.javabeans.Manuscript;
import java.util.List;

public interface InitialReviewService {
    // 返回所有新提交、待初审的稿件
    List<Manuscript> getSubmittedManuscripts();

    // 初审决策：分配编辑或拒稿（后续可扩展）
    void initialDecision(int manuscriptId, String decision, Integer editorId);

    // 之前的全量查询（如果你需要继续保留）
    List<Manuscript> getAllManuscripts();
    List<Manuscript> getManuscriptsByStatus(String status);
}