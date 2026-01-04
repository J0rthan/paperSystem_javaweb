package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;

@Entity
@Table(name = "EmailMessage")
public class EmailMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emailMes_id")
    private Integer emailMesId;

    @Column(name = "sender_email", nullable = false, length = 50)
    private String senderEmail;

    @Column(name = "receiver_email", nullable = false, length = 50)
    private String receiverEmail;

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

    /* ========= getter / setter ========= */

    public Integer getEmailMesId() {
        return emailMesId;
    }

    public void setEmailMesId(Integer emailMesId) {
        this.emailMesId = emailMesId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
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