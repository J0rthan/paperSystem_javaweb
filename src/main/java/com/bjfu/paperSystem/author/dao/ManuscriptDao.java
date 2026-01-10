package com.bjfu.paperSystem.author.dao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ManuscriptDao extends JpaRepository<Manuscript, Integer> {
    List<Manuscript> findByAuthorId(int authorId);
    List<Manuscript> findByStatus(String submitted);
    // 按提交时间倒序排序获取所有稿件
    List<Manuscript> findAllByOrderBySubmitTimeDesc();
}