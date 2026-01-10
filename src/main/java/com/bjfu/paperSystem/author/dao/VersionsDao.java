package com.bjfu.paperSystem.author.dao;
import com.bjfu.paperSystem.javabeans.Versions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface VersionsDao extends JpaRepository<Versions, Integer> {
    List<Versions> findByManuscriptId(int manuscriptId);
    int countByManuscriptId(int manuscriptId);
    List<Versions> findByManuscriptIdOrderByVersionNumberDesc(int manuscriptId);
    @Query("SELECT MAX(v.versionNumber) FROM Versions v WHERE v.manuscriptId = ?1")
    Integer findMaxVersionNumberByManuscriptId(int manuscriptId);
}