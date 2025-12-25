package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;

@Entity
@Table(name = "editorial_board")
public class Editorial_Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 关联到 User 表，方便获取姓名、邮箱
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "position") // 职位：editor 或 chief_editor
    private String position;

    @Column(name = "profile", columnDefinition = "TEXT") // 简介
    private String profile;

    @Column(name = "specialty") // 专长/所属栏目，用于匹配稿件
    private String specialty;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
}