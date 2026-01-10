package com.bjfu.paperSystem.editor.service;

import com.bjfu.paperSystem.clientMessageUtils.Dao.clientMessageDao;
import com.bjfu.paperSystem.editor.dao.EditorManuscriptDao;
import com.bjfu.paperSystem.editor.dao.EditorRecordAllocationDao;
import com.bjfu.paperSystem.javabeans.ClientMessage;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.Record_Allocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EditorMessageServiceImpl implements EditorMessageService {

    @Autowired
    private clientMessageDao clientMessageDao;

    @Autowired
    private EditorManuscriptDao manuscriptDao;

    @Autowired
    private EditorRecordAllocationDao recordAllocationDao;

    @Override
    public List<ClientMessage> getCommunicationHistory(int manuscriptId, int editorId) {
        // 1. 权限验证：检查Editor是否被分配了该稿件
        Manuscript manuscript = manuscriptDao.findById(manuscriptId).orElse(null);
        if (manuscript == null) {
            return List.of(); // 稿件不存在，返回空列表
        }

        // 检查是否被分配（通过Record_Allocation表）
        List<Record_Allocation> allocations = recordAllocationDao.findByEditorId(editorId);
        boolean hasPermission = allocations.stream()
                .anyMatch(a -> a.getManuscriptId() == manuscriptId);

        // 或者也可以通过 manuscript.getEditorId() 检查（如果该字段有值）
        if (!hasPermission && (manuscript.getEditorId() == null || !manuscript.getEditorId().equals(editorId))) {
            return List.of(); // 没有权限，返回空列表
        }

        // 2. 查询该稿件的所有消息
        return clientMessageDao.findByManuIdWithUsers(manuscriptId);
    }
}