package com.bjfu.paperSystem.chiefEditor.dao;

import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiefEditorManuscriptDao extends JpaRepository<Manuscript, Integer> {
    List<Manuscript> findByAuthorId(int authorId);

    List<Manuscript> findByStatus(String status);
}