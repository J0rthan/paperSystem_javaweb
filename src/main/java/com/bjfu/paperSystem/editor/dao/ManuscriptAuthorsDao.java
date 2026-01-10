package com.bjfu.paperSystem.editor.dao;

import com.bjfu.paperSystem.javabeans.ManuscriptAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManuscriptAuthorsDao extends JpaRepository<ManuscriptAuthor, Integer> {

    /**
     * 方法 1: 根据稿件 ID 获取所有作者列表 (标准 JPA 写法)
     * 如果以后需要显示所有作者详情，可以用这个
     */
    List<ManuscriptAuthor> findByManuscriptId(int manuscriptId);

    /**
     * 方法 2: 专门为编辑列表页优化的查询 (自定义 SQL)
     * 作用：只获取该稿件 第一位作者 的 单位(institution)
     * * 修改点：
     * 1. 表名改为 manuscript_authors
     * 2. 字段改为 institution (对应实体类中的字段)
     * 3. 加了 LIMIT 1，因为列表页只需要显示一个单位即可，不用把所有作者都查出来
     */
    @Query(value = "SELECT institution FROM manuscript_authors WHERE manuscript_id = ?1 LIMIT 1", nativeQuery = true)
    String findAuthorAffiliationByManuscriptId(int manuscriptId);
}