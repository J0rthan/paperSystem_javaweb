package com.bjfu.paperSystem.optionAdmin.service;

import com.bjfu.paperSystem.javabeans.Journal;
import com.bjfu.paperSystem.optionAdmin.dao.JournalDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JournalServiceImpl implements JournalService {

    @Autowired
    private JournalDao journalDao;

    @Override
    public void saveJournal(Journal journal) {
        journal.setCreateTime(LocalDateTime.now());
        journal.setUpdateTime(LocalDateTime.now());
        journal.setStatus("published");
        journalDao.save(journal);
    }

    @Override
    public void updateJournal(Journal journal) {
        // 确保createTime不为空
        if (journal.getCreateTime() == null) {
            journal.setCreateTime(LocalDateTime.now());
        }
        journal.setUpdateTime(LocalDateTime.now());
        journalDao.save(journal);
    }

    @Override
    public void deleteJournal(Integer journalId) {
        journalDao.deleteById(journalId);
    }

    @Override
    public Journal getJournalById(Integer journalId) {
        return journalDao.findById(journalId).orElse(null);
    }

    @Override
    public List<Journal> getAllJournal() {
        return journalDao.findAllByOrderByUpdateTimeDesc();
    }

    @Override
    public List<Journal> getJournalByStatus(String status) {
        return journalDao.findByStatusOrderByUpdateTimeDesc(status);
    }

    @Override
    public List<Journal> searchJournal(String keyword) {
        return journalDao.findByKeyword(keyword);
    }

    @Override
    public void publishJournal(Integer journalId) {
        Journal journal = journalDao.findById(journalId).orElse(null);
        if (journal != null) {
            journal.setStatus("published");
            // 确保createTime不为空
            if (journal.getCreateTime() == null) {
                journal.setCreateTime(LocalDateTime.now());
            }
            journal.setUpdateTime(LocalDateTime.now());
            journalDao.save(journal);
        }
    }

    @Override
    public void saveDraft(Journal journal) {
        journal.setCreateTime(LocalDateTime.now());
        journal.setUpdateTime(LocalDateTime.now());
        journal.setStatus("draft");
        journalDao.save(journal);
    }

    @Override
    public List<Journal> getPublishedJournal() {
        return journalDao.findPublishedJournal();
    }
}