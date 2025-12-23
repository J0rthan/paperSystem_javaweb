package com.bjfu.paperSystem.author.service;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.User;
import java.util.Map;
import java.util.List;
public interface authorService {
    // 获取分类后的稿件数据
    Map<String, List<Manuscript>> getCategorizedManuscripts(int authorId);
    // 处理提交/保存逻辑
    void handleSubmission(Manuscript manuscript, String action, User user);
    // 获取用于编辑的稿件（带权限校验）
    Manuscript getManuscriptByIdAndAuthor(int manuscriptId, int authorId);
    // 根据ID获取用户信息
    User getUserById(int userId);
    // 更新个人信息（返回错误消息，若成功返回null）
    String updateProfile(User user, int loginUserId);
}