package com.bjfu.paperSystem.author.service;
import com.bjfu.paperSystem.author.dao.ManuscriptDao;
import com.bjfu.paperSystem.author.dao.authorDao;
import com.bjfu.paperSystem.author.service.authorService;
import com.bjfu.paperSystem.author.service.logService;
import com.bjfu.paperSystem.javabeans.Manuscript;
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
    private logService logService;
    @Autowired
    private authorDao authorDao;
    @Override
    public Map<String, List<Manuscript>> getCategorizedManuscripts(int authorId) {
        List<Manuscript> all = manuscriptDao.findByAuthorId(authorId);
        Map<String, List<Manuscript>> map = new HashMap<>();
        // 1. 未完成
        map.put("incompletePapers", all.stream()
                .filter(p -> Arrays.asList("Started Submission", "Incomplete Submission").contains(p.getStatus()))
                .collect(Collectors.toList()));
        // 2. 处理中 - 细化为4个状态
        map.put("pendingReviewList", all.stream().filter(p -> "Pending Review".equals(p.getStatus())).collect(Collectors.toList()));
        map.put("pendingAllocationList", all.stream().filter(p -> "Pending Allocation".equals(p.getStatus())).collect(Collectors.toList()));
        map.put("withEditorList", all.stream().filter(p -> "With Editor".equals(p.getStatus())).collect(Collectors.toList()));
        map.put("underReviewList", all.stream().filter(p -> "Under Review".equals(p.getStatus())).collect(Collectors.toList()));
        // 3. 需修改
        map.put("revisionPapers", all.stream().filter(p -> "Need Revision".equals(p.getStatus())).collect(Collectors.toList()));
        // 4. 已裁决
        map.put("decidedPapers", all.stream()
                .filter(p -> Arrays.asList("Rejected", "Accepted", "With A Decision").contains(p.getStatus()))
                .collect(Collectors.toList()));
        return map;
    }
    @Override
    @Transactional
    public void handleSubmission(Manuscript manuscript, String action, User user) {
        manuscript.setAuthorId(user.getUserId());
        String logDesc;
        if ("save".equals(action)) {
            boolean isEmpty = (manuscript.getTitle() == null || manuscript.getTitle().trim().isEmpty()) &&
                    (manuscript.getAbstractText() == null || manuscript.getAbstractText().trim().isEmpty());

            manuscript.setStatus(isEmpty ? "Started Submission" : "Incomplete Submission");
            logDesc = isEmpty ? "保存了初始空稿件" : "保存了稿件草稿";
        } else {
            manuscript.setStatus("Pending Review");
            manuscript.setSubmitTime(LocalDateTime.now().withNano(0));
            logDesc = "正式提交了稿件";
        }
        manuscriptDao.save(manuscript);
        logService.record(user.getUserId(), logDesc, manuscript.getManuscriptId());
    }
    @Override
    public Manuscript getManuscriptByIdAndAuthor(int manuscriptId, int authorId) {
        return manuscriptDao.findById(manuscriptId)
                .filter(m -> m.getAuthorId() == authorId)
                .orElse(null);
    }
    @Override
    public User getUserById(int userId) {
        return authorDao.findById(userId).orElse(null);
    }
    @Override
    @Transactional
    public String updateProfile(User user, int loginUserId) {
        User existingUser = authorDao.findByUserName(user.getUserName());
        if (existingUser != null && !Objects.equals(existingUser.getUserId(), loginUserId)) {
            return "该用户名已存在";
        }
        User dbUser = authorDao.findById(loginUserId).orElse(null);
        if (dbUser == null) return "用户不存在";
        dbUser.setUserName(user.getUserName());
        dbUser.setFullName(user.getFullName());
        dbUser.setEmail(user.getEmail());
        dbUser.setCompany(user.getCompany());
        dbUser.setInvestigationDirection(user.getInvestigationDirection());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            dbUser.setPassword(user.getPassword());
        }
        authorDao.save(dbUser);
        logService.record(dbUser.getUserId(), "修改个人信息", 0);
        return null;
    }
}