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

    @Column(name = "author_id")
    private int authorId;

    @Column(name = "editor_id")
    private Integer editorId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String abstractText;

    private String keywords;

    @Column(name = "subject_area")
    private String subjectArea; // 研究主题

    @Column(name = "funding_info", columnDefinition = "TEXT")
    private String fundingInfo; // 项目资助情况

    private String status;
    private LocalDateTime submitTime;
    private String decision;

    private String manuscriptPath;
    private String coverLetterPath;

    @Column(name = "assign_reason")
    private String assignReason; // 选择当前编辑原因
    @Transient
    private List<ManuscriptAuthor> authors = new ArrayList<>();

    @Transient
    private List<RecommendedReviewer> reviewers = new ArrayList<>();

    // --- Getter and Setter ---
    public int getManuscriptId() { return manuscriptId; }
    public void setManuscriptId(int manuscriptId) { this.manuscriptId = manuscriptId; }

    public String getAssignReason() { return assignReason; }
    public void setAssignReason(String assignReason) { this.assignReason = assignReason; }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public Integer getEditorId() { return editorId; }
    public void setEditorId(Integer editorId) { this.editorId = editorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public String getSubjectArea() { return subjectArea; }
    public void setSubjectArea(String subjectArea) { this.subjectArea = subjectArea; }

    public String getFundingInfo() { return fundingInfo; }
    public void setFundingInfo(String fundingInfo) { this.fundingInfo = fundingInfo; }

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
}