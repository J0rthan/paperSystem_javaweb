package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;

@Entity
@Table(name = "ClientMessage")
public class ClientMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ClientMes_id")
    private Integer clientMesId;

    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    @Column(name = "receiver_id", nullable = false)
    private Integer receiverId;

    @Column(name = "message_body", nullable = false, length = 200)
    private String messageBody;

    /**
     * 外键字段（数据库真实存在的列）
     */
    @Column(name = "manu_id", nullable = false)
    private Integer manuId;

    /**
     * JPA 关联对象（不直接写入列）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "manu_id",
            referencedColumnName = "manuscript_id",
            insertable = false,
            updatable = false
    )
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
}