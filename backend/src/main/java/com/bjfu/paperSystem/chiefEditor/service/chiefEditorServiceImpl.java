package com.bjfu.paperSystem.chiefEditor.service;

import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class chiefEditorServiceImpl implements chiefEditorService {

    @Autowired
    private ManuscriptDao manuscriptDao; // 直接复用作者那边的 ManuscriptDao

    @Override
    public List<Manuscript> getAllManuscripts() {
        // JpaRepository 已经有 findAll() 方法
        return manuscriptDao.findAll();
    }

    @Override
    public List<Manuscript> getManuscriptsByStatus(String status) {
        // 这里先简单写“全部查出来在内存里过滤”，后面你可以再优化成数据库查询
        List<Manuscript> all = manuscriptDao.findAll();
        return all.stream()
                .filter(m -> status.equalsIgnoreCase(m.getStatus()))
                .toList();
    }
}