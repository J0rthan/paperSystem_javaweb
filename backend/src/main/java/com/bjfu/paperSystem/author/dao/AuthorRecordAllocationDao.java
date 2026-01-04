package com.bjfu.paperSystem.author.dao;
import com.bjfu.paperSystem.javabeans.Record_Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AuthorRecordAllocationDao extends JpaRepository<Record_Allocation, Integer> {
    List<Record_Allocation> findByManuscriptId(int manuscriptId);
}