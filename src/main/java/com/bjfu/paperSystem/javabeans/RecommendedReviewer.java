package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;

@Entity
@Table(name = "recommended_reviewers")
public class RecommendedReviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewer_id")
    private int id;

    @Column(name = "manuscript_id")
    private int manuscriptId;

    @Column(name = "full_name", length = 100)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    // --- Getter and Setter ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getManuscriptId() {
        return manuscriptId;
    }

    public void setManuscriptId(int manuscriptId) {
        this.manuscriptId = manuscriptId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}