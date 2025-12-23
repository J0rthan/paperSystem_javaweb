package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "manuscript")
public class Manuscript {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manuscript_id")
    private int manuscriptId;

    @Column(name = "author_id")
    private int authorId; // 提交作者 ID

    @Column(name = "editor_id")
    private Integer editorId; // 当前编辑 ID (允许为空)

    @Column(name = "title", length = 200) //标题也可以为空！
    private String title;

    @Column(name = "abstract_text", columnDefinition = "TEXT")
    private String abstractText;

    @Column(name = "keywords", length = 100)
    private String keywords;

    @Column(name = "author_list", columnDefinition = "TEXT")
    private String authorList;

    @Column(name = "status", length = 50)
    private String status; // 如：SUBMITTED, UNDER_REVIEW, ACCEPTED, REJECTED

    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    @Column(name = "decision", columnDefinition = "TEXT")
    private String decision;

    public Manuscript() {}

    // Getter and Setter
    public int getManuscriptId() { return manuscriptId; }
    public void setManuscriptId(int manuscriptId) { this.manuscriptId = manuscriptId; }
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
    public String getAuthorList() { return authorList; }
    public void setAuthorList(String authorList) { this.authorList = authorList; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
}