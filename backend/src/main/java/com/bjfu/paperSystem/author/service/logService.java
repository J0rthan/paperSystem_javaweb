package com.bjfu.paperSystem.author.service;
import com.bjfu.paperSystem.javabeans.Logs;
import java.util.List;
public interface logService {
    void record(Integer userId, String action, Integer manuscriptId);
    List<Logs> getLogsByManuscriptId(Integer manuscriptId);
}