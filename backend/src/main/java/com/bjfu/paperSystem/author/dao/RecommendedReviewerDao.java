package com.bjfu.paperSystem.author.dao;
import com.bjfu.paperSystem.javabeans.RecommendedReviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface RecommendedReviewerDao extends JpaRepository<RecommendedReviewer, Integer> {
    List<RecommendedReviewer> findByManuscriptId(Integer manuscriptId);
    @Modifying
    @Query("delete from RecommendedReviewer r where r.manuscriptId = ?1")
    void deleteByManuscriptId(Integer manuscriptId);
    List<RecommendedReviewer> findByManuscriptId(int manuscriptId);
}