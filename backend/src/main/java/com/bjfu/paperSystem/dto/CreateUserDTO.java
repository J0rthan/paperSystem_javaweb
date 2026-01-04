package com.bjfu.paperSystem.dto;

// 用于接收前端“新建用户”表单的数据
public class CreateUserDTO {
    private String username;
    private String fullName;
    private String email;
    private String password; // 默认可以是123456
    private String role;     // 选项: EDITOR, REVIEWER, EDITORIAL_ADMIN
    private String specialty; // 专长/研究方向 (用于编辑和审稿人)
    private String profile;   // 简介 (用于编辑)

    // Getters and Setters...
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }
}