package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
public class Logs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private int logId;

    @Column(name = "op_time")
    private LocalDateTime opTime;

    @Column(name = "opor_id")
    private int oporId;

    @Column(name = "op_type", length = 50)
    private String op_type;

    @Column(name = "paper_id")
    private int paperId;

    // setter and getter methods
    public int getLogId() {
        return this.logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public LocalDateTime getOpTime() {
        return this.opTime;
    }

    public void setOpTime(LocalDateTime opTime) {
        this.opTime = opTime;
    }

    public int getOporId() {
        return this.oporId;
    }

    public void setOporId(int oporId) {
        this.oporId = oporId;
    }

    public String getOp_type() {
        return this.op_type;
    }

    public void setOp_type(String op_type) {
        this.op_type = op_type;
    }

    public int getPaperId() {
        return this.paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }
}
