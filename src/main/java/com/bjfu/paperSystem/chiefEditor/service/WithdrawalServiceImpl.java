package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.LogsDao;
import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.javabeans.Logs;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    @Autowired
    private ManuscriptDao manuscriptDao;
    
    @Autowired
    private LogsDao logsDao; // 注入日志DAO

    @Override
    public List<Manuscript> listWithdrawalCandidates() {
        // 直接使用DAO方法根据状态查询，提高性能
        return manuscriptDao.findByStatus("Accepted");
    }

    @Override
    @Transactional
    public void withdrawManuscript(int manuscriptId, String reason, int userId) {
        Manuscript m = manuscriptDao.findById(manuscriptId).orElse(null);
        if (m == null) return;
        m.setStatus("Incomplete Submission");
        String oldDecision = m.getDecision();
        String note = "[撤稿原因] " + reason;
        if (oldDecision != null && !oldDecision.isEmpty()) {
            m.setDecision(oldDecision + " | " + note);
        } else {
            m.setDecision(note);
        }
        manuscriptDao.save(m);
        
        // 记录撤稿日志
        Logs log = new Logs();
        log.setOporId(userId);
        log.setPaperId(manuscriptId);
        log.setOpType("retract");
        log.setOpTime(LocalDateTime.now());
        logsDao.save(log);
    }
}