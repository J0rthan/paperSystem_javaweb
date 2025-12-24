package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.chiefEditor.dao.ChiefEditorUserDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignEditorServiceImpl implements AssignEditorService {

    @Autowired
    private ManuscriptDao manuscriptDao;

    @Autowired
    private ChiefEditorUserDao userDao; // 查编辑列表

    @Override
    public List<Manuscript> getToAssignManuscripts() {
        // 按你实际定义的“待分配”状态来筛选；若目前用 SUBMITTED 表示待分配，就用这个
        return manuscriptDao.findByStatus("SUBMITTED");
    }

    @Override
    public List<User> getAvailableEditors() {
        // 这里简单用 userType = editor 过滤；需要时可加 status=exist
        return userDao.findAll().stream()
                .filter(u -> "editor".equalsIgnoreCase(u.getUserType()))
                .collect(Collectors.toList());
    }

    @Override
    public void assignEditor(int manuscriptId, int editorId) {
        Manuscript m = manuscriptDao.findById(manuscriptId).orElse(null);
        if (m == null) return;
        m.setEditorId(editorId);
        m.setStatus("UNDER_REVIEW"); // 分配后进入审稿中，按需调整
        manuscriptDao.save(m);
    }
}