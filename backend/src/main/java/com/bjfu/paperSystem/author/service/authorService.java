package com.bjfu.paperSystem.author.service;

import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;
import java.util.List;
import java.util.Map;

public interface authorService {
    // 获取分类后的稿件列表
    Map<String, List<Manuscript>> getCategorizedManuscripts(int authorId);

    // 任务书核心：处理包含多作者、推荐审稿人和文件的完整投稿逻辑
    void fullSubmit(Manuscript manuscript, String action, User user);

    // 根据ID获取稿件（用于编辑草稿）
    Manuscript getManuscriptByIdAndAuthor(int manuscriptId, int authorId);
    void deleteManuscript(int manuscriptId);
    // 个人信息相关
    User getUserById(int userId);
    String updateProfile(User user, int loginUserId);
}