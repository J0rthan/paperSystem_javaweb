package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.javabeans.Editorial_Board;
import com.bjfu.paperSystem.javabeans.Manuscript;

import java.util.List;

public interface AssignEditorService {

    // 待分配的稿件
    List<Manuscript> getToAssignManuscripts();

    // 可选的编辑列表
    List<Editorial_Board> getAvailableBoardEditors();

    // 执行指派
    void assignEditor(int manuscriptId, int selectedEditorId, int userId, String reason);
}