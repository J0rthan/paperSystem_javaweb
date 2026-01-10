package com.bjfu.paperSystem.javabeans;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "manuscript")
public class Manuscript {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manuscript_id")
    private int manuscriptId;

    @Column(name = "author_id", nullable = false)
    private int authorId;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "abstract_text", columnDefinition = "TEXT")
    private String abstractText;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "subject_area")
    private String subjectArea; // 研究主题

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    @Column(name = "decision", length = 100)
    private String decision;

    @Column(name = "manuscript_path", length = 255)
    private String manuscriptPath;

    @Column(name = "cover_letter_path", length = 255)
    private String coverLetterPath;

    @Column(name = "assign_reason", length = 500)
    private String assignReason;

    @Column(name = "assign_time")
    private LocalDateTime assignTime;

    @Column(name = "editor_id")
    private Integer editorId;

    // 当前审稿轮次（默认为1）
    @Column(columnDefinition = "integer default 1")
    private Integer round = 1;

    // 编辑给主编的建议 (Accept/Reject/Revise) - 临时存放
    private String editorRecommendation;

    // 编辑给主编的备注 - 临时存放
    @Column(columnDefinition = "TEXT")
    private String editorComment;

    @Transient
    private List<ManuscriptAuthor> authors = new ArrayList<>();

    @Transient
    private List<RecommendedReviewer> reviewers = new ArrayList<>();

    @Transient
    private List<ManuscriptFunding> fundings = new ArrayList<>();

    // 字数统计相关字段（临时存储，不持久化）
    @Transient
    private int wordCount; // 稿件字数
    
    @Transient
    private boolean isWordCountExceeded; // 是否字数超限（超过8000字）
    
    // 查重相关字段（临时存储，不持久化）
    @Transient
    private double plagiarismRate; // 查重率（百分比）
    
    @Transient
    private boolean isHighSimilarity; // 是否高相似度（超过20%）

    // --- Getter and Setter ---
    public int getManuscriptId() { return manuscriptId; }
    public void setManuscriptId(int manuscriptId) { this.manuscriptId = manuscriptId; }

    public String getAssignReason() { return assignReason; }
    public void setAssignReason(String assignReason) { this.assignReason = assignReason; }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public String getSubjectArea() { return subjectArea; }
    public void setSubjectArea(String subjectArea) { this.subjectArea = subjectArea; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getManuscriptPath() { return manuscriptPath; }
    public void setManuscriptPath(String manuscriptPath) { this.manuscriptPath = manuscriptPath; }

    public String getCoverLetterPath() { return coverLetterPath; }
    public void setCoverLetterPath(String coverLetterPath) { this.coverLetterPath = coverLetterPath; }

    public List<ManuscriptAuthor> getAuthors() { return authors; }
    public void setAuthors(List<ManuscriptAuthor> authors) { this.authors = authors; }

    public List<RecommendedReviewer> getReviewers() { return reviewers; }
    public void setReviewers(List<RecommendedReviewer> reviewers) { this.reviewers = reviewers; }

    public LocalDateTime getAssignTime() {return assignTime;}
    public void setAssignTime(LocalDateTime assignTime) {this.assignTime = assignTime;}

    public Integer getEditorId() { return editorId; }
    public void setEditorId(Integer editorId) { this.editorId = editorId; }

    public Integer getRound() { return round; }
    public void setRound(Integer round) { this.round = round; }

    public String getEditorRecommendation() { return editorRecommendation; }
    public void setEditorRecommendation(String editorRecommendation) { this.editorRecommendation = editorRecommendation; }

    public String getEditorComment() { return editorComment; }
    public void setEditorComment(String editorComment) { this.editorComment = editorComment; }

    public List<ManuscriptFunding> getFundings() { return fundings; }
    public void setFundings(List<ManuscriptFunding> fundings) { this.fundings = fundings; }

    // 字数统计相关的getter和setter
    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }
    
    public boolean isWordCountExceeded() { return isWordCountExceeded; }
    public void setWordCountExceeded(boolean isWordCountExceeded) { this.isWordCountExceeded = isWordCountExceeded; }
    
    // 查重相关的getter和setter
    public double getPlagiarismRate() { return plagiarismRate; }
    public void setPlagiarismRate(double plagiarismRate) { this.plagiarismRate = plagiarismRate; }
    
    public boolean isHighSimilarity() { return isHighSimilarity; }
    public void setHighSimilarity(boolean isHighSimilarity) { this.isHighSimilarity = isHighSimilarity; }
}