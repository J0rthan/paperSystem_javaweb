package com.bjfu.paperSystem.author.service;

import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.author.dao.authorDao;
import com.bjfu.paperSystem.author.dao.RecommendedReviewerDao;
import com.bjfu.paperSystem.author.dao.ManuscriptAuthorDao;
import com.bjfu.paperSystem.javabeans.Manuscript;
import com.bjfu.paperSystem.javabeans.ManuscriptAuthor;
import com.bjfu.paperSystem.javabeans.RecommendedReviewer;
import com.bjfu.paperSystem.javabeans.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class authorServiceImpl implements authorService {

    @Autowired
    private ManuscriptDao manuscriptDao;

    @Autowired
    private ManuscriptAuthorDao authorRepository;

    @Autowired
    private RecommendedReviewerDao reviewerRepository;

    @Autowired
    private logService logService;

    @Autowired
    private authorDao authorDao;

    @Override
    @Transactional
    public void fullSubmit(Manuscript manuscript, String action, User user) {
        // 设置当前提交者的ID
        manuscript.setAuthorId(user.getUserId());
        String logDesc;

        // 1. 根据任务书动作判断状态
        if ("submit".equals(action)) {
            manuscript.setStatus("Submissions Being Processed"); // 对应任务书：提交投稿
            manuscript.setSubmitTime(LocalDateTime.now());
            logDesc = "submitted";
        } else {
            manuscript.setStatus("Incomplete Submission"); // 对应任务书：保存草稿
            logDesc = "saved";
        }

        // 2. 如果是编辑旧稿件（ID已存在），先清理旧的作者和审稿人关联，实现覆盖更新
        if (manuscript.getManuscriptId() > 0) {
            authorRepository.deleteByManuscriptId(manuscript.getManuscriptId());
            reviewerRepository.deleteByManuscriptId(manuscript.getManuscriptId());
        }

        // 3. 保存稿件主表并获取持久化后的对象（包含生成的自增ID）
        Manuscript savedManuscript = manuscriptDao.save(manuscript);
        int mid = savedManuscript.getManuscriptId();

        // 4. 保存作者列表 (从 Manuscript 对象的 Transient 集合中获取)
        if (manuscript.getAuthors() != null && !manuscript.getAuthors().isEmpty()) {
            for (ManuscriptAuthor author : manuscript.getAuthors()) {
                // 仅保存姓名不为空的有效记录
                if (author.getName() != null && !author.getName().trim().isEmpty()) {
                    author.setManuscriptId(mid);
                    authorRepository.save(author);
                }
            }
        }

        // 5. 保存推荐审稿人列表
        if (manuscript.getReviewers() != null && !manuscript.getReviewers().isEmpty()) {
            for (RecommendedReviewer reviewer : manuscript.getReviewers()) {
                if (reviewer.getName() != null && !reviewer.getName().trim().isEmpty()) {
                    reviewer.setManuscriptId(mid);
                    reviewerRepository.save(reviewer);
                }
            }
        }

        // 6. 记录日志
        logService.record(user.getUserId(), logDesc, mid);
    }

    @Override
    public Map<String, List<Manuscript>> getCategorizedManuscripts(int authorId) {
        List<Manuscript> all = manuscriptDao.findByAuthorId(authorId);
        Map<String, List<Manuscript>> map = new HashMap<>();

        // 对应任务书的不同工作流状态
        map.put("incompletePapers", all.stream()
                .filter(p -> Arrays.asList("Incomplete Submission", "Started Submission").contains(p.getStatus()))
                .collect(Collectors.toList()));

        map.put("pendingReviewList", all.stream()
                .filter(p -> Arrays.asList("Submissions Being Processed", "Pending Review").contains(p.getStatus()))
                .collect(Collectors.toList()));

        map.put("underReviewList", all.stream().filter(p -> "Under Review".equals(p.getStatus())).collect(Collectors.toList()));

        map.put("decidedPapers", all.stream()
                .filter(p -> Arrays.asList("Rejected", "Accepted").contains(p.getStatus()))
                .collect(Collectors.toList()));

        return map;
    }

    @Override
    public Manuscript getManuscriptByIdAndAuthor(int manuscriptId, int authorId) {
        Optional<Manuscript> optional = manuscriptDao.findById(manuscriptId);
        if (optional.isPresent() && optional.get().getAuthorId() == authorId) {
            Manuscript m = optional.get();
            // 关键：手动加载关联的作者和审稿人，以便前端回显
            m.setAuthors(authorRepository.findByManuscriptId(manuscriptId));
            m.setReviewers(reviewerRepository.findByManuscriptId(manuscriptId));
            return m;
        }
        return null;
    }
    @Override
    public User getUserById(int userId) {
        return authorDao.findById(userId).orElse(null);
    }
    @Override
    @Transactional
    public String updateProfile(User user, int loginUserId) {
        User dbUser = authorDao.findById(loginUserId).orElse(null);
        if (dbUser == null) return "用户不存在";
        User existingUser = authorDao.findByUserName(user.getUserName());
        if (existingUser != null && existingUser.getUserId() != loginUserId) {
            return "该用户名已存在";
        }
        dbUser.setUserName(user.getUserName());
        dbUser.setFullName(user.getFullName());
        dbUser.setEmail(user.getEmail());
        dbUser.setCompany(user.getCompany());
        dbUser.setInvestigationDirection(user.getInvestigationDirection());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            dbUser.setPassword(user.getPassword());
        }
        authorDao.save(dbUser);
        logService.record(dbUser.getUserId(), "modify information", 0);
        return null;
    }
}