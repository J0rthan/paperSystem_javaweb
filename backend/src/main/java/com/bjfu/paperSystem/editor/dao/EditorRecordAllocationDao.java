package com.bjfu.paperSystem.editor.dao;

import com.bjfu.paperSystem.javabeans.Record_Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EditorRecordAllocationDao extends JpaRepository<Record_Allocation, Integer> {
    // 获取某稿件最新的分配记录
    @Query(value = "SELECT * FROM record_allocation WHERE manuscript_id = ?1 ORDER BY assign_time DESC LIMIT 1", nativeQuery = true)
    Record_Allocation findLatestByManuscriptId(int manuscriptId);
}