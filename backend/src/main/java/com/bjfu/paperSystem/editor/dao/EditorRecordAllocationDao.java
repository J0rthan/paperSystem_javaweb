package com.bjfu.paperSystem.editor.dao;

import com.bjfu.paperSystem.javabeans.Record_Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List; // 记得导入 List

@Repository
public interface EditorRecordAllocationDao extends JpaRepository<Record_Allocation, Integer> {

    // 原有的方法保留
    @Query(value = "SELECT * FROM record_allocation WHERE manuscript_id = ?1 ORDER BY assign_time DESC LIMIT 1", nativeQuery = true)
    Record_Allocation findLatestByManuscriptId(int manuscriptId);

    // === 【新增】根据编辑ID查找他被分配的所有记录 ===
    // Spring Data JPA 会自动根据方法名生成 SQL，不需要手动写 Query
    List<Record_Allocation> findByEditorId(Integer editorId);
}