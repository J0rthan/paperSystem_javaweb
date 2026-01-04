package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.author.service.logService;
import com.bjfu.paperSystem.chiefEditor.dao.ChiefEditorEditorial_BoardDao;
import com.bjfu.paperSystem.chiefEditor.dao.RecordAllocationDao; // 1. 引入新DAO
import com.bjfu.paperSystem.javabeans.Editorial_Board;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Record_Allocation; // 2. 引入新Bean
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
        List<Manuscript> result = manuscriptDao.findByStatus("Pending Asllocation II");
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

        // 获取编委 (注意：这里用的是 editorId，实际上是 User 表的 ID，我们需要确认前端传的是 User ID 还是 Editorial_Board 的 member_id)
        // 根据 Controller 代码：name="editorId" value="${e.user.userId}"
        // 所以这里的 editorId 是 User 的 userId。
        // 但如果前端改成了传 member_id，这里需要对应调整。
        // 假设这里 editorId 指的是具体的 User ID (即编辑本人)。

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
            record.setEditorId(editorId); // 记录指派给了谁
            record.setAssignReason(reason);
            record.setAssignTime(LocalDateTime.now());

            recordAllocationDao.save(record); // 保存到新表

            // --- 3. 记录系统日志 ---
            String logDescription = "分配编辑 (编辑ID: " + editorId + ", 理由: " + reason + ")";
            logService.record(0, logDescription, manuscriptId);
        }
    }
}