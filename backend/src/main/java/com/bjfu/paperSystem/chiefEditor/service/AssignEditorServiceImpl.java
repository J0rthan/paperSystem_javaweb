package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.author.service.logService;
import com.bjfu.paperSystem.chiefEditor.dao.ChiefEditorEditorial_BoardDao;
import com.bjfu.paperSystem.javabeans.Editorial_Board;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssignEditorServiceImpl implements AssignEditorService {

    @Autowired
    private ManuscriptDao manuscriptDao;

    @Autowired
    private ChiefEditorEditorial_BoardDao editorialBoardDao;

    @Autowired
    private logService logService; // 引用日志服务

    @Override
    public List<Manuscript> getToAssignManuscripts() {
        // 根据现有项目规范，待分配状态为 "Pending Allocation"
        List<Manuscript> result = manuscriptDao.findByStatus("Pending Allocation");
        return result != null ? result : List.of();
    }

    @Override
    public List<Editorial_Board> getAvailableBoardEditors() {
        // 查找职位为 "editor" 的编委成员，以便获取其专长信息
        // 过滤掉 user 为 null 的记录，避免页面访问时出错
        List<Editorial_Board> all = editorialBoardDao.findByPosition("editor");
        if (all == null) {
            return List.of();
        }
        return all.stream()
                .filter(eb -> eb != null && eb.getUser() != null)
                .toList();
    }

    @Override
    @Transactional // 开启事务，保证更新稿件和插入日志同时成功
    public void assignEditor(int manuscriptId, int editorId, String reason) {
        Manuscript paper = manuscriptDao.findById(manuscriptId).orElse(null);

        if (paper != null) {
            // 1. 设置负责编辑的ID
            paper.setEditorId(editorId);

            // 2. 更新状态：从 "Pending Allocation" -> "With Editor"
            paper.setStatus("With Editor");

            // 3. 保存分配理由 (需要在 Manuscript 实体中添加此字段)
            paper.setAssignReason(reason);

            // 4. 执行更新
            manuscriptDao.save(paper);

            // 5. 记录操作日志 (任务书要求系统审计)
            // 参数说明：操作人ID(暂填0或从Session获取), 操作描述, 关联稿件ID
            String logDescription = "分配编辑 (编辑ID: " + editorId + ", 理由: " + reason + ")";
            logService.record(0, logDescription, manuscriptId);
        }
    }
}