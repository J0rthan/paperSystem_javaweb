package com.bjfu.paperSystem.author.service;
import com.bjfu.paperSystem.author.dao.*;
import com.bjfu.paperSystem.javabeans.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class authorServiceImpl implements authorService {
    @Autowired private ManuscriptDao manuscriptDao;
    @Autowired private ManuscriptAuthorDao authorRepository;
    @Autowired private RecommendedReviewerDao reviewerRepository;
    @Autowired private logService logService; // 注入你的 logService
    @Autowired private authorDao authorDao;
    private String translateStatus(String status) {
        if (status == null) return "未知状态";
        return switch (status) {
            case "Started Submission" -> "开始投稿";
            case "Incomplete Submission" -> "尚未提交的草稿";
            case "Pending Review" -> "待审查";
            case "Pending Allocation" -> "待分配";
            case "With Editor" -> "编辑处理中";
            case "Under Review" -> "审稿中";
            case "Rejected" -> "拒稿";
            case "Accepted" -> "录用";
            case "Need Revision" -> "需要返修";
            case "With A Decision" -> "已决议";
            default -> status;
        };
    }
    @Override
    @Transactional
    public void fullSubmit(Manuscript manuscript, String action, User user) {
        manuscript.setAuthorId(user.getUserId());
        if ("submit".equals(action)) {
            manuscript.setStatus("Pending Review");
            manuscript.setSubmitTime(LocalDateTime.now());
        } else {
            if (manuscript.getTitle() == null || manuscript.getTitle().trim().isEmpty()) {
                manuscript.setStatus("Started Submission");
            } else {
                manuscript.setStatus("Incomplete Submission");
            }
        }
        if (manuscript.getManuscriptId() > 0) {
            authorRepository.deleteByManuscriptId(manuscript.getManuscriptId());
            reviewerRepository.deleteByManuscriptId(manuscript.getManuscriptId());
        }
        Manuscript savedManuscript = manuscriptDao.save(manuscript);
        int mid = savedManuscript.getManuscriptId();
        String logAction = "submit".equals(action) ? "提交投稿" : "保存草稿";
        logService.record(user.getUserId(), logAction, mid);
        if (manuscript.getAuthors() != null) {
            for (ManuscriptAuthor author : manuscript.getAuthors()) {
                if (author.getName() != null && !author.getName().trim().isEmpty()) {
                    author.setManuscriptId(mid);
                    author.setId(0);
                    authorRepository.save(author);
                }
            }
        }
        if (manuscript.getReviewers() != null) {
            for (RecommendedReviewer reviewer : manuscript.getReviewers()) {
                if (reviewer.getName() != null && !reviewer.getName().trim().isEmpty()) {
                    reviewer.setManuscriptId(mid);
                    reviewer.setId(0);
                    reviewerRepository.save(reviewer);
                }
            }
        }
    }
    @Override
    @Transactional
    public void deleteManuscript(int manuscriptId) {
        authorRepository.deleteByManuscriptId(manuscriptId);
        reviewerRepository.deleteByManuscriptId(manuscriptId);
        manuscriptDao.deleteById(manuscriptId);
    }
    @Override
    public Map<String, List<Manuscript>> getCategorizedManuscripts(int authorId) {
        List<Manuscript> all = manuscriptDao.findByAuthorId(authorId);
        all.forEach(m -> m.setStatus(translateStatus(m.getStatus())));
        Map<String, List<Manuscript>> map = new HashMap<>();
        map.put("incompletePapers", all.stream().filter(m -> m.getStatus().equals("开始投稿") || m.getStatus().equals("尚未提交的草稿")).collect(Collectors.toList()));
        map.put("pendingReviewList", all.stream().filter(m -> m.getStatus().equals("待审查")).collect(Collectors.toList()));
        map.put("underReviewList", all.stream().filter(m -> m.getStatus().equals("审稿中")).collect(Collectors.toList()));
        map.put("decidedPapers", all.stream().filter(m -> Arrays.asList("录用", "拒稿", "已决议").contains(m.getStatus())).collect(Collectors.toList()));
        map.put("revisionPapers", all.stream().filter(m -> m.getStatus().equals("需要返修")).collect(Collectors.toList()));
        map.put("pendingAllocationList", all.stream().filter(m -> m.getStatus().equals("待分配")).collect(Collectors.toList()));
        map.put("withEditorList", all.stream().filter(m -> m.getStatus().equals("编辑处理中")).collect(Collectors.toList()));

        return map;
    }
    @Override
    public Manuscript getManuscriptByIdAndAuthor(int manuscriptId, int authorId) {
        Optional<Manuscript> optional = manuscriptDao.findById(manuscriptId);
        if (optional.isPresent() && optional.get().getAuthorId() == authorId) {
            Manuscript m = optional.get();
            m.setAuthors(authorRepository.findByManuscriptId(manuscriptId));
            m.setReviewers(reviewerRepository.findByManuscriptId(manuscriptId));
            return m;
        }
        return null;
    }
    @Override public User getUserById(int userId) { return authorDao.findById(userId).orElse(null); }
    @Override
    @Transactional
    public String updateProfile(User user, int loginUserId) {
        User dbUser = authorDao.findById(loginUserId).orElse(null);
        if (dbUser == null) return "用户不存在";
        dbUser.setUserName(user.getUserName());
        dbUser.setFullName(user.getFullName());
        dbUser.setEmail(user.getEmail());
        dbUser.setCompany(user.getCompany());
        dbUser.setInvestigationDirection(user.getInvestigationDirection());
        authorDao.save(dbUser);
        return null;
    }
}