package com.bjfu.paperSystem.author.service;

import com.bjfu.paperSystem.author.dao.LogsDao;
import com.bjfu.paperSystem.javabeans.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class logServiceImpl implements logService {
    @Autowired
    private LogsDao logsDao;
    @Override
    public void record(Integer userId, String action, Integer manuscriptId) {
        Logs log = new Logs();
        log.setOporId(userId);
        log.setOpType(action);
        log.setPaperId(manuscriptId);
        log.setOpTime(LocalDateTime.now().withNano(0));
        logsDao.save(log);
    }
    @Override
    public List<Logs> getLogsByManuscriptId(Integer manuscriptId) {
        // 调用 Dao 层按时间降序排列
        return logsDao.findByPaperIdOrderByOpTimeDesc(manuscriptId);
    }
}