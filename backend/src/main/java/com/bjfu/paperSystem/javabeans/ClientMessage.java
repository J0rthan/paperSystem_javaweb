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
}