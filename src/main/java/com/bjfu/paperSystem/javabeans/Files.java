package com.bjfu.paperSystem.javabeans;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "files")
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private int fileId;
    @Column(name = "file_name", length = 100)
    private String fileName;
    @Column(name = "file_path", length = 255, nullable = false)
    private String filePath;
    @Column(name = "upload_time")
    private LocalDateTime uploadTime;
    @Column(name = "manuscript_id", nullable = false)
    private int manuscriptId;
    public Files() {
        this.uploadTime = LocalDateTime.now();
    }
    public int getFileId() {
        return fileId;
    }
    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public LocalDateTime getUploadTime() {
        return uploadTime;
    }
    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }
    public int getManuscriptId() {
        return manuscriptId;
    }
    public void setManuscriptId(int manuscriptId) {
        this.manuscriptId = manuscriptId;
    }
}