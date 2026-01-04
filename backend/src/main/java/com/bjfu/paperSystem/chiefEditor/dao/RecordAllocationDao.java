package com.bjfu.paperSystem.chiefEditor.dao;

import com.bjfu.paperSystem.javabeans.Record_Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordAllocationDao extends JpaRepository<Record_Allocation, Integer> {
    List<Record_Allocation> findByManuscriptId(int manuscriptId);
}