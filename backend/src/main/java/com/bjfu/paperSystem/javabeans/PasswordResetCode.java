package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "password_reset_code",
        uniqueConstraints = @UniqueConstraint(name="uk_prc_user", columnNames = "user_id")
)
public class PasswordResetCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Integer userId;

    @Column(nullable=false, length=10)
    private String code;

    @Column(name="expires_at", nullable=false)
    private LocalDateTime expiresAt;

    @Column(nullable=false)
    private Boolean used = false;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // setter and getter

    public void setId(Long id) {this.id = id;}
    public Long getId() {return this.id;}

    public void setUserId(Integer userId) {this.userId = userId;}
    public Integer getUserId() {return this.userId;}

    public void setCode(String code) {this.code = code;}
    public String getCode() {return this.code;}

    public void setExpiresAt(LocalDateTime time) {this.expiresAt = time;}
    public LocalDateTime getExpiresAt() {return this.expiresAt;}

    public void setUsed(Boolean used) {this.used = used;}
    public Boolean getUsed() {return this.used;}

    public void setCreatedAt(LocalDateTime time) {this.createdAt = time;}
    public LocalDateTime getCreatedAt() {return this.createdAt;}
}
