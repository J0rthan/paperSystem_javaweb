package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.editor.dao.DecisionHistoryDao; // 引入历史记录DAO
import com.bjfu.paperSystem.javabeans.DecisionHistory;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User; // 引入User
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 引入事务

import java.util.Date;
import java.util.List;

@Service
public class FinalDecisionServiceImpl implements FinalDecisionService {

    @Autowired
    private ManuscriptDao manuscriptDao;

    @Autowired
    private DecisionHistoryDao decisionHistoryDao; // 注入历史记录DAO

    @Override
    public List<Manuscript> listPendingFinalDecisions() {
        // 【关键修改】查找状态为 PENDING_DECISION 的稿件
        return manuscriptDao.findByStatus("PENDING_DECISION");
    }

    @Override
    public Manuscript getManuscriptDetail(int manuscriptId) {
        return manuscriptDao.findById(manuscriptId).orElse(null);
    }

    @Override
    @Transactional // 确保数据一致性
    public void makeFinalDecision(int manuscriptId, String decision, String comment) {
        Manuscript m = manuscriptDao.findById(manuscriptId).orElse(null);
        if (m == null) return;

        // 1. 更新稿件状态
        // decision 对应前端的值: ACCEPT, REJECT, NEED_REVISION
        switch (decision) {
            case "ACCEPT" -> m.setStatus("ACCEPTED"); // 录用
            case "REJECT" -> m.setStatus("REJECTED"); // 拒稿
            case "NEED_REVISION" -> m.setStatus("NEED_REVISION"); // 需返修
            default -> { return; }
        }

        // 将主编的最终评语更新到稿件表 (或者你可以选择只存历史表)
        m.setDecision(comment);
        manuscriptDao.save(m);

        // 2. 【建议新增】保存决策历史到 DecisionHistory 表，方便审计
        DecisionHistory history = new DecisionHistory();
        history.setManuscript(m);
        history.setRound(m.getRound() == null ? 1 : m.getRound());
        history.setFinalDecision(decision);
        history.setFinalDecisionComment(comment);
        history.setDecisionDate(new Date());
        // 注意：这里没有传 chiefEditor 的 User 对象，如果需要记录是谁操作的，
        // Controller 层需要把 session 中的 user 传进来，或者暂时设为 null

        decisionHistoryDao.save(history);
    }
}