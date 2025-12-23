package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.javabeans.Manuscript;
import java.util.List;

public interface chiefEditorService {
    // 1. 返回所有稿件（最简单版本）
    List<Manuscript> getAllManuscripts();

    // 2. （选做，后面可以用）按状态筛选，比如只看“待分配”的稿件
    List<Manuscript> getManuscriptsByStatus(String status);
}
