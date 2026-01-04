package com.bjfu.paperSystem.chiefEditor.dao;

import com.bjfu.paperSystem.javabeans.DecisionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DecisionHistoryDao extends JpaRepository<DecisionHistory, Long>{
    List<DecisionHistory> findByManuscriptIdOrderByRoundAsc(Long manuscriptId);
}
