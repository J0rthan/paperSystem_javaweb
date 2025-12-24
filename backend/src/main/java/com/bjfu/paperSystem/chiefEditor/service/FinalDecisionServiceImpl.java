package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinalDecisionServiceImpl implements FinalDecisionService {

    @Autowired
    private ManuscriptDao manuscriptDao;

    @Override
    public List<Manuscript> listPendingFinalDecisions() {
        // 简化版：先用状态筛选，比如所有 status = "REVIEW_COMPLETED" 的稿件
        return manuscriptDao.findByStatus("REVIEW_COMPLETED");
    }

    @Override
    public Manuscript getManuscriptDetail(int manuscriptId) {
        return manuscriptDao.findById(manuscriptId).orElse(null);
    }

    @Override
    public void makeFinalDecision(int manuscriptId, String decision, String comment) {
        Manuscript m = manuscriptDao.findById(manuscriptId).orElse(null);
        if (m == null) return;

        // decision: ACCEPT / REJECT / NEED_REVISION
        switch (decision) {
            case "ACCEPT" -> m.setStatus("ACCEPTED");
            case "REJECT" -> m.setStatus("REJECTED");
            case "NEED_REVISION" -> m.setStatus("NEED_REVISION");
            default -> { return; }
        }
        m.setDecision(comment); // 把终审意见简单记录到 decision 字段
        manuscriptDao.save(m);
    }
}