package com.bjfu.paperSystem.optionAdmin.dao;

import com.bjfu.paperSystem.javabeans.Journal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JournalDao extends JpaRepository<Journal, Integer>, JpaSpecificationExecutor<Journal> {

    // 根据状态查询期刊
    List<Journal> findByStatusOrderByUpdateTimeDesc(String status);

    // 根据名称或介绍模糊查询
    @Query("SELECT j FROM Journal j WHERE j.name LIKE %:keyword% OR j.introduction LIKE %:keyword%")
    List<Journal> findByKeyword(@Param("keyword") String keyword);

    // 查询所有期刊（按更新时间倒序）
    List<Journal> findAllByOrderByUpdateTimeDesc();

    // 查询已发布的期刊（按更新时间倒序）
    @Query("SELECT j FROM Journal j WHERE j.status = 'published' ORDER BY j.updateTime DESC")
    List<Journal> findPublishedJournal();

    // 分页查询已发布的期刊（按更新时间倒序）
    @Query("SELECT j FROM Journal j WHERE j.status = 'published' ORDER BY j.updateTime DESC")
    List<Journal> findPublishedJournal(Pageable pageable);
}