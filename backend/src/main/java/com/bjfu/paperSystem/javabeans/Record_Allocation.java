package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "record_allocation")
public class Record_Allocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private int recordId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "manuscript_id")
    private int manuscriptId;

    @Column(name = "assign_time")
    private LocalDateTime assignTime;

    @Column(name = "assign_reason", length = 500)
    private String assignReason;

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public int getManuscriptId() { return manuscriptId; }
    public void setManuscriptId(int manuscriptId) { this.manuscriptId = manuscriptId; }
    public LocalDateTime getAssignTime() {return assignTime;}
    public void setAssignTime(LocalDateTime assignTime) {this.assignTime = assignTime;}
    public String getAssignReason() { return assignReason; }
    public void setAssignReason(String assignReason) { this.assignReason = assignReason; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer editorId) { this.userId = editorId; }
}
