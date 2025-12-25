package com.bjfu.paperSystem.chiefEditor.dao;

import com.bjfu.paperSystem.javabeans.Editorial_Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChiefEditorEditorial_BoardDao extends JpaRepository<Editorial_Board, Integer> {
    // 查找所有职位是 "Editor" 的编委
    List<Editorial_Board> findByPosition(String position);
}
