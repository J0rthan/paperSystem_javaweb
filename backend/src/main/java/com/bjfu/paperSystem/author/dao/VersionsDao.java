package com.bjfu.paperSystem.author.dao;

import com.bjfu.paperSystem.javabeans.Versions;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VersionsDao extends JpaRepository<Versions, Integer> {
    List<Versions> findByManuscriptId(int manuscriptId);
    int countByManuscriptId(int manuscriptId);
    List<Versions> findByManuscriptIdOrderByVersionNumberDesc(int manuscriptId);
}