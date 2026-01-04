package com.bjfu.paperSystem.editor.dao;

import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EditorManuscriptDao extends JpaRepository<Manuscript, Integer> {
    // 查找分配给特定编辑的稿件
    List<Manuscript> findByEditorId(Integer editorId);
}