package com.bjfu.paperSystem.author.controller;
import com.bjfu.paperSystem.author.service.*;
import com.bjfu.paperSystem.javabeans.*;
import com.bjfu.paperSystem.author.dao.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.time.LocalDateTime;
@Controller
@RequestMapping("/author/manuscript")
public class manuscriptSystemController {
    @Autowired private authorService authorService;
    @Autowired private logService logService;
    @Autowired private LogsDao logsDao;
    @Autowired private ManuscriptDao manuscriptDao;
    @Autowired private ManuscriptAuthorDao manuscriptAuthorDao;
    @Autowired private RecommendedReviewerDao recommendedReviewerDao;
    @Autowired private ManuscriptFundingDao manuscriptFundingDao;
    @Autowired private PermissionDao permissionDao;
    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/index";
        Permission perm = permissionDao.findByUser_UserId(loginUser.getUserId())
                .orElse(new Permission());
        model.addAttribute("permission", perm);
        Map<String, List<Manuscript>> data = authorService.getCategorizedManuscripts(loginUser.getUserId());
        Map<Integer, LocalDateTime> lastUpdateTimes = new HashMap<>();
        data.values().forEach(list -> {
            if (list != null) {
                for (Manuscript m : list) {
                    Optional<Logs> latestLog = logsDao.findTopByPaperIdOrderByOpTimeDesc(m.getManuscriptId());
                    LocalDateTime lastTime = latestLog.map(Logs::getOpTime).orElse(m.getSubmitTime());
                    lastUpdateTimes.put(m.getManuscriptId(), lastTime);
                }
            }
        });
        model.addAllAttributes(data);
        model.addAttribute("lastUpdateTimes", lastUpdateTimes);
        return "author/list";
    }
    @GetMapping("/submit")
    public String toSubmit(Model model, HttpSession session, RedirectAttributes ra) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/index";
        Permission p = permissionDao.findByUser_UserId(loginUser.getUserId()).orElse(new Permission());
        if (!p.isSubmitManuscript()) {
            ra.addFlashAttribute("error", "您当前没有投稿权限，请联系管理员开通。");
            return "redirect:/author/manuscript/list";
        }
        Manuscript manuscript = new Manuscript();
        manuscript.setManuscriptId(0);
        manuscript.setAuthors(new ArrayList<>(Collections.singletonList(new ManuscriptAuthor())));
        manuscript.setReviewers(new ArrayList<>(Collections.singletonList(new RecommendedReviewer())));
        manuscript.setFundings(new ArrayList<>(Collections.singletonList(new ManuscriptFunding())));

        model.addAttribute("manuscript", manuscript);
        return "author/submit";
    }
    @PostMapping("/doSubmit")
    public String doSubmit(@ModelAttribute Manuscript manuscript,
                           @RequestParam("action") String action,
                           @RequestParam(value = "msFile", required = false) MultipartFile msFile,
                           @RequestParam(value = "clFile", required = false) MultipartFile clFile,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/index";
        Permission p = permissionDao.findByUser_UserId(loginUser.getUserId()).orElse(new Permission());
        if (!p.isSubmitManuscript()) {
            redirectAttributes.addFlashAttribute("error", "权限不足");
            return "redirect:/author/manuscript/list";
        }
        try {
            if (manuscript.getManuscriptId() > 0) {
                Manuscript oldManuscript = manuscriptDao.findById(manuscript.getManuscriptId()).orElse(null);
                if (oldManuscript != null) {
                    if (msFile != null && !msFile.isEmpty()) {
                        manuscript.setManuscriptPath(saveFile(msFile, "manuscripts"));
                    } else {
                        manuscript.setManuscriptPath(oldManuscript.getManuscriptPath());
                    }
                    if (clFile != null && !clFile.isEmpty()) {
                        manuscript.setCoverLetterPath(saveFile(clFile, "covers"));
                    } else {
                        manuscript.setCoverLetterPath(oldManuscript.getCoverLetterPath());
                    }
                }
            } else {
                if (msFile != null && !msFile.isEmpty()) {
                    manuscript.setManuscriptPath(saveFile(msFile, "manuscripts"));
                }
                if (clFile != null && !clFile.isEmpty()) {
                    manuscript.setCoverLetterPath(saveFile(clFile, "covers"));
                }
            }
            authorService.fullSubmit(manuscript, action, loginUser);
            String msg = "save".equals(action) ? "草稿保存成功" : "稿件提交成功";
            redirectAttributes.addFlashAttribute("message", msg);
            return "redirect:list";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "操作失败：" + e.getMessage());
            return "redirect:/author/manuscript/submit";
        }
    }
    @GetMapping("/edit")
    public String toEdit(@RequestParam("id") int id, Model model, HttpSession session, RedirectAttributes ra) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/index";
        Permission p = permissionDao.findByUser_UserId(loginUser.getUserId()).orElse(new Permission());
        if (!p.isSubmitManuscript()) {
            ra.addFlashAttribute("error", "权限不足，无法编辑。");
            return "redirect:/author/manuscript/list";
        }
        Manuscript manuscript = authorService.getManuscriptByIdAndAuthor(id, loginUser.getUserId());
        if (manuscript == null) return "redirect:/author/manuscript/list";
        manuscript.setAuthors(manuscriptAuthorDao.findByManuscriptId(id));
        manuscript.setReviewers(recommendedReviewerDao.findByManuscriptId(id));
        manuscript.setFundings(manuscriptFundingDao.findByManuscriptId(id));

        model.addAttribute("manuscript", manuscript);
        return "author/submit";
    }
    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id, HttpSession session, RedirectAttributes ra) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        Manuscript m = authorService.getManuscriptByIdAndAuthor(id, loginUser.getUserId());
        if (m != null) {
            authorService.deleteManuscript(id);
            ra.addFlashAttribute("message", "稿件草稿已成功删除");
        }
        return "redirect:/author/manuscript/list";
    }
    @GetMapping("/track")
    public String track(@RequestParam("id") int id, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";
        Manuscript manuscript = authorService.getManuscriptByIdAndAuthor(id, loginUser.getUserId());
        if (manuscript == null) return "redirect:/author/manuscript/list";
        List<Logs> logs = logService.getLogsByManuscriptId(id);
        List<Versions> versions = authorService.getVersionsByManuscriptId(id);
        java.util.Map<Integer, String> userNames = new java.util.HashMap<>();
        for (Logs log : logs) {
            Integer oporId = log.getOporId();
            if (oporId == null) {
                userNames.put(oporId, "系统记录");
                continue;
            }
            User opUser = authorService.findUserById(oporId);
            if (opUser != null) {
                String name;
                if ("Reviewer".equalsIgnoreCase(opUser.getUserType())) {
                    name = "匿名审稿人";
                } else {
                    name = (opUser.getFullName() != null && !opUser.getFullName().isEmpty())
                            ? opUser.getFullName() : opUser.getUserName();
                }
                userNames.put(oporId, name);
            } else {
                userNames.put(oporId, "系统用户");
            }
        }
        model.addAttribute("manuscript", manuscript);
        model.addAttribute("logs", logs);
        model.addAttribute("versions", versions);
        model.addAttribute("userNames", userNames);
        return "author/track";
    }
    @Autowired private AuthorRecordAllocationDao recordAllocationDao;

    @GetMapping("/revise")
    public String toRevise(@RequestParam("id") int id, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/index";
        Manuscript manuscript = authorService.getManuscriptByIdAndAuthor(id, loginUser.getUserId());
        if (manuscript == null || !"Need Revision".equals(manuscript.getStatus())) {
            return "redirect:/author/manuscript/list";
        }
        List<Record_Allocation> allocations = recordAllocationDao.findByManuscriptId(id);
        List<User> assignedEditors = new ArrayList<>();
        for (Record_Allocation allocation : allocations) {
            if (allocation.getEditorId() != null) {
                User editor = authorService.findUserById(allocation.getEditorId());
                if (editor != null) assignedEditors.add(editor);
            }
        }
        model.addAttribute("manuscript", manuscript);
        model.addAttribute("assignedEditors", assignedEditors);
        return "author/revise";
    }
    @PostMapping("/doRevise")
    public String doRevise(@RequestParam("manuscriptId") int manuscriptId,
                           @RequestParam("cleanFile") MultipartFile cleanFile,
                           @RequestParam("markedFile") MultipartFile markedFile,
                           @RequestParam("replyFile") MultipartFile replyFile,
                           @RequestParam("responseText") String responseText,
                           HttpSession session, RedirectAttributes ra) {

        User loginUser = (User) session.getAttribute("loginUser");
        try {
            authorService.submitRevision(manuscriptId, cleanFile, markedFile, replyFile, responseText, loginUser);
            ra.addFlashAttribute("message", "修回已提交，进入下一轮审稿");
            return "redirect:/author/manuscript/list";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "提交失败：" + e.getMessage());
            return "redirect:/author/manuscript/revise?id=" + manuscriptId;
        }
    }
    @GetMapping("/download")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> downloadFile(
            @RequestParam("path") String filePath,
            HttpSession session) {
        if (session.getAttribute("loginUser") == null) {
            return org.springframework.http.ResponseEntity.status(403).build();
        }
        try {
            String projectPath = System.getProperty("user.dir");
            File file = new File(projectPath + "/backend/src/main/resources/static" + filePath);
            if (!file.exists()) {
                String classPath = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("").getPath(), "UTF-8");
                if (System.getProperty("os.name").toLowerCase().contains("win") && classPath.startsWith("/")) {
                    classPath = classPath.substring(1);
                }
                file = new File(classPath + "static" + filePath);
            }
            if (!file.exists()) return org.springframework.http.ResponseEntity.notFound().build();

            org.springframework.core.io.Resource resource = new org.springframework.core.io.FileSystemResource(file);
            String fileName = file.getName();
            String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            return org.springframework.http.ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") int id, Model model) {
        Manuscript manuscript = manuscriptDao.findById(id).orElse(null);
        if (manuscript == null) return "redirect:/author/manuscript/list";

        manuscript.setAuthors(manuscriptAuthorDao.findByManuscriptId(id));
        manuscript.setReviewers(recommendedReviewerDao.findByManuscriptId(id));
        manuscript.setFundings(manuscriptFundingDao.findByManuscriptId(id)); // 加载资助列表

        model.addAttribute("manuscript", manuscript);
        return "author/detailedInformation";
    }
    private String saveFile(MultipartFile file, String subDir) {
        try {
            String originalFileName = file.getOriginalFilename();
            String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;
            String projectPath = System.getProperty("user.dir");
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
}