package com.bjfu.paperSystem.author.dao;
import com.bjfu.paperSystem.javabeans.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LogsDao extends JpaRepository<Logs, Integer>, JpaSpecificationExecutor<Logs> {
    List<Logs> findByPaperIdOrderByOpTimeDesc(Integer paperId);
}