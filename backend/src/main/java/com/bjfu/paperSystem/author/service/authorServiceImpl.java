package com.bjfu.paperSystem.author.service;
import com.bjfu.paperSystem.author.dao.*;
import com.bjfu.paperSystem.javabeans.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
@Service
public class authorServiceImpl implements authorService {
    @Autowired private ManuscriptDao manuscriptDao;
    @Autowired private ManuscriptAuthorDao authorRepository;
    @Autowired private RecommendedReviewerDao reviewerRepository;
    @Autowired private logService logService;
    @Autowired private authorDao authorDao;
    @Autowired private VersionsDao versionsDao;
    @Autowired private FilesDao filesDao;
    @Autowired
    private LogsDao logsDao;

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
        if ("submit".equals(action)) {
            Versions ver = new Versions();
            ver.setManuscriptId(mid);
            ver.setVersionNumber(versionsDao.countByManuscriptId(mid) + 1);
            ver.setFilePathOriginal(manuscript.getManuscriptPath());
            ver.setCoverLetterPath(manuscript.getCoverLetterPath());
            versionsDao.save(ver);
            if (manuscript.getManuscriptPath() != null) {
                Files f = new Files();
                f.setManuscriptId(mid);
                f.setFilePath(manuscript.getManuscriptPath());
                f.setFileName("V" + ver.getVersionNumber() + "_Main_Manuscript");
                filesDao.save(f);
            }
        }
        String logAction = "submit".equals(action) ? "submit" : "save";
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
    public User findUserById(int userId) {
        // 使用 JPA 默认的 findById 方法
        return authorDao.findById(userId).orElse(null);
    }
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
    @Override
    @Transactional
    public void submitRevision(int manuscriptId, MultipartFile cleanFile, MultipartFile markedFile,
                               MultipartFile replyFile, String responseText, User user) throws IOException {
        String cleanPath = saveFile(cleanFile, "manuscripts");
        String markedPath = saveFile(markedFile, "marked");
        String replyPath = saveFile(replyFile, "replies");
        Integer maxVersion = versionsDao.findMaxVersionNumberByManuscriptId(manuscriptId);
        int nextVersion = (maxVersion == null) ? 1 : maxVersion + 1;
        Versions newV = new Versions();
        newV.setManuscriptId(manuscriptId);
        newV.setVersionNumber(nextVersion);
        newV.setFilePathOriginal(cleanPath);
        newV.setFilePathAnon(markedPath);
        newV.setResponseLetterPath(replyPath);
        newV.setResponseText(responseText);
        versionsDao.save(newV);
        // 4. 更新稿件状态
        Manuscript ms = manuscriptDao.findById(manuscriptId).get();
        ms.setStatus("Under Review");
        manuscriptDao.save(ms);
        Logs log = new Logs();
        log.setPaperId(manuscriptId);
        log.setOporId(user.getUserId());
        log.setOpType("Submit Revision");
        log.setOpTime(LocalDateTime.now());
        logsDao.save(log);
    }
    private String saveFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) return null;
        try {
            String originalFileName = file.getOriginalFilename();
            String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;
            String projectPath = System.getProperty("user.dir");
            // 1. 源码目录路径（为了让 IDEA 里的资源同步）
            String srcPath = projectPath + "/backend/src/main/resources/static/uploads/" + subDir + "/";
            File srcDir = new File(srcPath);
            if (!srcDir.exists()) srcDir.mkdirs();
            File srcFile = new File(srcDir, fileName);
            String classPath = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("").getPath(), "UTF-8");
            if (System.getProperty("os.name").toLowerCase().contains("win") && classPath.startsWith("/")) {
                classPath = classPath.substring(1);
            }
            String targetPath = classPath + "static/uploads/" + subDir + "/";
            File targetDir = new File(targetPath);
            if (!targetDir.exists()) targetDir.mkdirs();
            File targetFile = new File(targetDir, fileName);
            file.transferTo(srcFile);
            org.springframework.util.FileCopyUtils.copy(srcFile, targetFile);
            return "/uploads/" + subDir + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public List<Versions> getVersionsByManuscriptId(int manuscriptId) {
        return versionsDao.findByManuscriptIdOrderByVersionNumberDesc(manuscriptId);
    }
}