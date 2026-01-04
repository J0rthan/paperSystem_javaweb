package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.LogsDao;
import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.javabeans.Logs;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InitialReviewServiceImpl implements InitialReviewService {

    @Autowired
    private ManuscriptDao manuscriptDao;
    
    @Autowired
    private LogsDao logsDao;

    @Override
    public List<Manuscript> getSubmittedManuscripts() {
        // 使用自定义查询，查状态为 SUBMITTED 的稿件
        return manuscriptDao.findByStatus("SUBMITTED");
    }

    @Override
    public void initialDecision(int manuscriptId, String decision, int userId, String assignReason) {
        Manuscript m = manuscriptDao.findById(manuscriptId).orElse(null);
        if (m == null) return;

        // 设置assign_reason
        m.setAssignReason(assignReason);
        
        String logType = "";
        if ("ASSIGN_EDITOR".equalsIgnoreCase(decision)) {
            m.setStatus("Pending Allocation II"); // 送审后状态改为Pending Allocation II
            logType = "Desk Accept";
        } else if ("REJECT".equalsIgnoreCase(decision)) {
            m.setStatus("Rejected"); // 拒稿后状态改为Rejected
            logType = "Desk Reject";
        }
        
        // 保存稿件状态更新
        manuscriptDao.save(m);
        
        // 创建日志记录
        Logs log = new Logs();
        log.setOporId(userId);
        log.setOpType(logType);
        log.setPaperId(manuscriptId);
        log.setOpTime(LocalDateTime.now());
        logsDao.save(log);
    }

    @Override
    public List<Manuscript> getAllManuscripts() {
        return manuscriptDao.findAll();
    }

    @Override
    public List<Manuscript> getManuscriptsByStatus(String status) {
        return manuscriptDao.findByStatus(status);
    }
}
