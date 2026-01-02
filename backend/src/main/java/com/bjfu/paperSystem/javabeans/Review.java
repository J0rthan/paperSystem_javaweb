package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private int reviewId;

    @Column(name = "manu_id")
    private int manuId;

    @Column(name = "reviewer_id")
    private int reviewerId;

    @Column(name = "opinion")
    private String opinion;

    @Column(name = "score")
    private int score;

    @Column(name = "comments")
    private String comments;

    @Column(name = "submission_time")
    private LocalDateTime submissionTime;

    @Column(name = "status")
    private String status;

    @Column(name = "invitation_time")
    private LocalDateTime invitationTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manu_id", referencedColumnName = "manuscript_id",
            insertable = false, updatable = false)
    private Manuscript manuscript;

    // getter and setter method
    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getManuId() {
        return manuId;
    }

    public void setManuId(int manuId) {
        this.manuId = manuId;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getInvitationTime() {
        return invitationTime;
    }

    public void setInvitationTime(LocalDateTime invitationTime) {
        this.invitationTime = invitationTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Manuscript getManuScript() {
        return this.manuscript;
    }

    public void setManuScript(Manuscript manu) {
        this.manuscript = manu;
    }
}
