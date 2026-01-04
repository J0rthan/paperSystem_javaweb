package com.bjfu.paperSystem.dto;

import java.time.LocalDateTime;

/**
 * 专门用于编辑端列表展示的传输对象
 * 包含：稿件基本信息、分配时间、匿名作者信息、是否紧急
 */
public class EditorManuscriptDTO {
    private int manuscriptId;
    private String title;
    private String status;       // 显示状态 (With Editor / Under Review)
    private LocalDateTime assignTime; // 指派时间
    private String authorInfo;   // 处理后的作者信息 (如 "Affiliation: BJP U")
    private boolean isUrgent;    // 是否有即将逾期/已逾期的审稿

    public EditorManuscriptDTO(int manuscriptId, String title, String status,
                               LocalDateTime assignTime, String authorInfo, boolean isUrgent) {
        this.manuscriptId = manuscriptId;
        this.title = title;
        this.status = status;
        this.assignTime = assignTime;
        this.authorInfo = authorInfo;
        this.isUrgent = isUrgent;
    }

    // Getters
    public int getManuscriptId() { return manuscriptId; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public LocalDateTime getAssignTime() { return assignTime; }
    public String getAuthorInfo() { return authorInfo; }
    public boolean isUrgent() { return isUrgent; }
}