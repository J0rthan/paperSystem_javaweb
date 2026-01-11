package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_message")
public class ClientMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ClientMes_id")
    private Integer clientMesId;

    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    // sender_id 外键 -> user.user_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id",
            insertable = false, updatable = false)
    private User sender;

    @Column(name = "receiver_id", nullable = false)
    private Integer receiverId;

    // receiver_id 外键 -> user.user_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id",
            insertable = false, updatable = false)
    private User receiver;

    @Column(name = "message_body", nullable = false, length = 200)
    private String messageBody;

    @Column(name = "sending_time", nullable = false)
    private LocalDateTime sendingTime;

    /**
     * 外键字段（数据库真实存在的列）
     */
    @Column(name = "manu_id", nullable = false)
    private Integer manuId;

    /**
     * JPA 关联对象（不直接写入列）
     * 使用@JsonIgnore避免JSON序列化时的循环引用问题
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "manu_id",
            referencedColumnName = "manuscript_id",
            insertable = false,
            updatable = false
    )
    @JsonIgnore
    private Manuscript manuscript;

    // ===== Getter / Setter =====

    public Integer getClientMesId() {
        return clientMesId;
    }

    public void setClientMesId(Integer clientMesId) {
        this.clientMesId = clientMesId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Integer getManuId() {
        return manuId;
    }

    public void setManuId(Integer manuId) {
        this.manuId = manuId;
    }

    public Manuscript getManuscript() {
        return manuscript;
    }

    public void setManuscript(Manuscript manuscript) {
        this.manuscript = manuscript;
    }

    public void setSendingTime(LocalDateTime sendingTime) {this.sendingTime = sendingTime;}

    public LocalDateTime getSendingTime() {return this.sendingTime;}

    public void setSender(User sender) {this.sender = sender;}

    public User getSender() {return this.sender;}

    public void setReceiver(User receiver) {this.receiver = receiver;}

    public User getReceiver() {return this.receiver;}
}