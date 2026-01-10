package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;

@Entity
@Table(name = "versions")
public class Versions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id")
    private int versionId;

    @Column(name = "manuscript_id", nullable = false)
    private int manuscriptId; // 关联的稿件ID

    @Column(name = "version_number")
    private int versionNumber; // 版本号，如 1, 2, 3

    @Column(name = "file_path_original", length = 255)
    private String filePathOriginal; // 原版文件路径（含作者信息）

    @Column(name = "file_path_anon", length = 255)
    private String filePathAnon; // 匿名版文件路径（用于双盲审稿）

    @Column(name = "cover_letter_path", length = 255)
    private String coverLetterPath; // 附信路径

    @Column(name = "response_letter_path", length = 255)
    private String responseLetterPath; // 回复信路径（修改稿时使用）

    @Column(name = "response_text", columnDefinition = "TEXT")
    private String responseText;
    // 无参构造函数
    public Versions() {
    }

    // ===== Getter 和 Setter =====

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public int getManuscriptId() {
        return manuscriptId;
    }

    public void setManuscriptId(int manuscriptId) {
        this.manuscriptId = manuscriptId;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getFilePathOriginal() {
        return filePathOriginal;
    }

    public void setFilePathOriginal(String filePathOriginal) {
        this.filePathOriginal = filePathOriginal;
    }

    public String getFilePathAnon() {
        return filePathAnon;
    }

    public void setFilePathAnon(String filePathAnon) {
        this.filePathAnon = filePathAnon;
    }

    public String getCoverLetterPath() {
        return coverLetterPath;
    }

    public void setCoverLetterPath(String coverLetterPath) {
        this.coverLetterPath = coverLetterPath;
    }

    public String getResponseLetterPath() {
        return responseLetterPath;
    }

    public void setResponseLetterPath(String responseLetterPath) {
        this.responseLetterPath = responseLetterPath;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
}