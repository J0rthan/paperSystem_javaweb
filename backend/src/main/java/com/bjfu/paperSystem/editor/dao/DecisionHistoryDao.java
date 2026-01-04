package com.bjfu.paperSystem.editor.dao;

import com.bjfu.paperSystem.javabeans.DecisionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DecisionHistoryDao extends JpaRepository<DecisionHistory, Long> {
}