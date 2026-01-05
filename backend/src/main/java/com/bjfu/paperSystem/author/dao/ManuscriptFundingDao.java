package com.bjfu.paperSystem.author.dao;
import com.bjfu.paperSystem.javabeans.ManuscriptFunding;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ManuscriptFundingDao extends JpaRepository<ManuscriptFunding, Integer> {
    List<ManuscriptFunding> findByManuscriptId(int manuscriptId);
    void deleteByManuscriptId(int manuscriptId);
}