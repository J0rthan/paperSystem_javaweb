package com.bjfu.paperSystem.Login.javabeans;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")   // 如果表名不是 user，请改成真实表名
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "user_name", length = 50, nullable = false)
    private String userName;

    @Column(name = "password", length = 20, nullable = false)
    private String password;

    @Column(name = "user_type", length = 50)
    private String userType;

    @Column(name = "email", length = 30)
    private String email;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "company", length = 50)
    private String company;

    @Column(name = "investigation_direction", length = 50)
    private String investigationDirection;

    @Column(name = "register_time")
    private LocalDateTime registerTime;

    @Column(name = "status", length = 30)
    private String status;

    public User() {
    }

    // ===== getter / setter =====

    public int getUserId() {
        return userId;
    }

    public void setUserId(int user_id) {
        this.userId = user_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String user_name) {
        this.userName = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String user_type) {
        this.userType = user_type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String full_name) {
        this.fullName = full_name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getInvestigationDirection() {
        return investigationDirection;
    }

    public void setInvestigationDirection(String investigation_direction) {
        this.investigationDirection = investigation_direction;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(LocalDateTime register_time) {
        this.registerTime = register_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}