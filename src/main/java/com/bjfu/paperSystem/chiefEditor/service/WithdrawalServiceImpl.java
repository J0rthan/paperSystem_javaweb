package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    @Autowired
    private ManuscriptDao manuscriptDao;

    @Override
    public List<Manuscript> listWithdrawalCandidates() {
        // 简化版：允许对所有非已撤稿的稿件执行撤稿
        // 你也可以只允许对 ACCEPTED / UNDER_REVIEW 的稿件撤稿
        List<Manuscript> all = manuscriptDao.findAll();
        return all.stream()
                .filter(m -> !"WITHDRAWN".equalsIgnoreCase(m.getStatus()))
                .toList();
    }

    @Override
    public void withdrawManuscript(int manuscriptId, String reason) {
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
    }
}