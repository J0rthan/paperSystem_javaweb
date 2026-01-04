package com.bjfu.paperSystem.chiefEditor.dao;

import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiefEditorManuscriptDao extends JpaRepository<Manuscript, Integer> {
    List<Manuscript> findByAuthorId(int authorId);

    List<Manuscript> findByStatus(String status);
}