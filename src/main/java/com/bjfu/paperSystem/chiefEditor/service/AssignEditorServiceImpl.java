package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.author.service.logService;
import com.bjfu.paperSystem.chiefEditor.dao.ChiefEditorEditorial_BoardDao;
import com.bjfu.paperSystem.chiefEditor.dao.RecordAllocationDao;
import com.bjfu.paperSystem.javabeans.Editorial_Board;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Record_Allocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignEditorServiceImpl implements AssignEditorService {

    @Autowired
    private ManuscriptDao manuscriptDao;

    @Autowired
    private ChiefEditorEditorial_BoardDao editorialBoardDao;

    @Autowired
    private RecordAllocationDao recordAllocationDao; // 3. 注入新DAO

    @Autowired
    private logService logService;

    @Override
    public List<Manuscript> getToAssignManuscripts() {
        List<Manuscript> result = manuscriptDao.findByStatus("Pending Allocation II");
        return result != null ? result : List.of();
    }

    @Override
    public List<Editorial_Board> getAvailableBoardEditors() {
        List<Editorial_Board> all = editorialBoardDao.findByPosition("editor");
        if (all == null) {
            return List.of();
        }
        return all.stream()
                .filter(eb -> eb != null && eb.getUser() != null)
                .toList();
    }

    @Override
    @Transactional
    public void assignEditor(int manuscriptId, int editorId, String reason) {
        // 获取稿件
        Manuscript paper = manuscriptDao.findById(manuscriptId).orElse(null);

        // editorId 是当前登录用户的 User ID（来自 Controller）
        // 现在总是将稿件分配给当前登录用户

        if (paper != null) {
            // --- 1. 更新 Manuscript 表 (维护当前状态) ---
            // 必须保留！因为 EditorProcessService 里的 getManuscriptDetail 方法是根据
            // m.getEditorId() == editorId 来判断权限的。
            paper.setEditorId(editorId);
            paper.setStatus("With Editor");

            // 注意：不再调用 paper.setAssignReason(...) 和 paper.setAssignTime(...)
            // 如果你已经在数据库删除了这两列，调用会报错。如果没删，只是不再更新它。

            manuscriptDao.save(paper);

            // --- 2. 插入 Record_Allocation 表 (记录分配详情) ---
            Record_Allocation record = new Record_Allocation();
            record.setManuscriptId(manuscriptId);
            record.setEditorId(editorId); // 记录指派给了谁（当前登录用户）
            record.setAssignReason(reason);
            record.setAssignTime(LocalDateTime.now());

            recordAllocationDao.save(record); // 保存到新表

            // --- 3. 记录系统日志 ---
            String logDescription = "assign editor";
            logService.record(editorId, logDescription, manuscriptId); // 使用当前用户ID记录日志
        }
    }
}