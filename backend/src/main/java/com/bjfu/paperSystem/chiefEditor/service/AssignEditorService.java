package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;

import java.util.List;

public interface AssignEditorService {

    // 待分配的稿件
    List<Manuscript> getToAssignManuscripts();

    // 可选的编辑列表
    List<User> getAvailableEditors();

    // 执行指派
    void assignEditor(int manuscriptId, int editorId);
}