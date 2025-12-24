package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.javabeans.Manuscript;

import java.util.List;

public interface FinalDecisionService {

    // 列出需要终审决策的稿件（比如所有审稿完成的稿件）
    List<Manuscript> listPendingFinalDecisions();

    // 查看某篇稿件详情（终审页用）
    Manuscript getManuscriptDetail(int manuscriptId);

    // 做出终审决策：ACCEPT / REJECT / NEED_REVISION
    void makeFinalDecision(int manuscriptId, String decision, String comment);
}