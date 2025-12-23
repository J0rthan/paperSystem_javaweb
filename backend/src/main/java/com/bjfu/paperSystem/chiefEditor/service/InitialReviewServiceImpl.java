package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InitialReviewServiceImpl implements InitialReviewService {

    @Autowired
    private ManuscriptDao manuscriptDao;

    @Override
    public List<Manuscript> getSubmittedManuscripts() {
        // 使用自定义查询，查状态为 SUBMITTED 的稿件
        return manuscriptDao.findByStatus("SUBMITTED");
    }

    @Override
    public void initialDecision(int manuscriptId, String decision, Integer editorId) {
        Manuscript m = manuscriptDao.findById(manuscriptId).orElse(null);
        if (m == null) return;

        if ("ASSIGN_EDITOR".equalsIgnoreCase(decision) && editorId != null) {
            m.setEditorId(editorId);
            m.setStatus("UNDER_REVIEW"); // 分配编辑后进入审稿中
        } else if ("REJECT".equalsIgnoreCase(decision)) {
            m.setStatus("REJECTED");
        }
        // 可以在这里顺便写 m.setDecision(...) 或记录日志，后续再扩展
        manuscriptDao.save(m);
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
