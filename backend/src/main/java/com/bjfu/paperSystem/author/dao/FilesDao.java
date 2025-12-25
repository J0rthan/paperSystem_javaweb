package com.bjfu.paperSystem.author.dao;
import com.bjfu.paperSystem.javabeans.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FilesDao extends JpaRepository<Files, Integer> {
    List<Files> findByManuscriptId(int manuscriptId);
}