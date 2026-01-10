package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.LogsDao;
import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.editor.dao.DecisionHistoryDao; // 引入历史记录DAO
import com.bjfu.paperSystem.javabeans.DecisionHistory;
import com.bjfu.paperSystem.javabeans.Logs;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User; // 引入User
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 引入事务

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class FinalDecisionServiceImpl implements FinalDecisionService {

    @Autowired
    private ManuscriptDao manuscriptDao;

    @Autowired
    private DecisionHistoryDao decisionHistoryDao;
    
    @Autowired
    private LogsDao logsDao; // 注入历史记录DAO

    @Override
    public List<Manuscript> listPendingFinalDecisions() {
        // 【关键修改】查找状态为 PENDING_DECISION 的稿件
        return manuscriptDao.findByStatus("With Editor II");
    }

    @Override
    public Manuscript getManuscriptDetail(int manuscriptId) {
        return manuscriptDao.findById(manuscriptId).orElse(null);
    }

    @Override
    @Transactional
    public void makeFinalDecision(int manuscriptId, String decision, String comment, int userId) {
        Manuscript m = manuscriptDao.findById(manuscriptId).orElse(null);
        if (m == null) return;

        // 1. 更新稿件状态
        // decision 对应前端的值: ACCEPT, REJECT, NEED_REVISION
        switch (decision) {
            case "Accepted" -> m.setStatus("Accepted"); // 录用
            case "Rejected" -> m.setStatus("Rejected"); // 拒稿
            case "Need Revision" -> m.setStatus("Need Revision"); // 需返修
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

        decisionHistoryDao.save(history);
        
        // 3. 记录操作日志到 logs 表
        Logs log = new Logs();
        log.setOporId(userId);
        log.setPaperId(manuscriptId);
        log.setOpTime(LocalDateTime.now());
        
        // 根据决策类型设置操作类型
        switch (decision) {
            case "Accepted" -> log.setOpType("final accepted");
            case "Need Revision" -> log.setOpType("need revision");
            case "Rejected" -> log.setOpType("final rejected");
            default -> log.setOpType("final decision");
        }
        
        logsDao.save(log);
    }
}