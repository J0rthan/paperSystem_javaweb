package com.bjfu.paperSystem.optionAdmin.service;

import com.bjfu.paperSystem.javabeans.Journal;

import java.util.List;

public interface JournalService {
    // 新增期刊
    void saveJournal(Journal journal);

    // 更新期刊
    void updateJournal(Journal journal);

    // 删除期刊
    void deleteJournal(Integer journalId);

    // 根据ID获取期刊
    Journal getJournalById(Integer journalId);

    // 获取所有期刊
    List<Journal> getAllJournal();

    // 根据状态获取期刊
    List<Journal> getJournalByStatus(String status);

    // 搜索期刊（根据名称或介绍）
    List<Journal> searchJournal(String keyword);

    // 发布期刊
    void publishJournal(Integer journalId);

    // 保存为草稿
    void saveDraft(Journal journal);

    // 获取已发布的期刊
    List<Journal> getPublishedJournal();
}