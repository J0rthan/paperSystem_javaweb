package com.bjfu.paperSystem.author.dao;
import com.bjfu.paperSystem.javabeans.ManuscriptAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ManuscriptAuthorDao extends JpaRepository<ManuscriptAuthor, Integer> {
    List<ManuscriptAuthor> findByManuscriptId(Integer manuscriptId);
    @Modifying
    @Query("delete from ManuscriptAuthor a where a.manuscriptId = ?1")
    void deleteByManuscriptId(Integer manuscriptId);
    List<ManuscriptAuthor> findByManuscriptId(int manuscriptId);
}