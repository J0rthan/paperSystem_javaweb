package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;

@Entity
@Table(name = "permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private int permissionId;

    /**
     * 一个用户通常对应一条权限记录：建议用 OneToOne，并给 user_id 加唯一约束。
     * 若你希望一个用户有多条权限记录（不常见），可改成 ManyToOne 并去掉 unique=true。
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 1) 提交新稿件
    @Column(name = "submit_manuscript", nullable = false)
    private boolean submitManuscript;

    // 2) 查看所有稿件
    @Column(name = "view_all_manuscripts", nullable = false)
    private boolean viewAllManuscripts;

    // 3) 邀请/指派人员
    @Column(name = "invite_assign_personnel", nullable = false)
    private boolean inviteOrAssignPersonnel;

    // 4) 查看审稿人身份
    @Column(name = "view_reviewer_identity", nullable = false)
    private boolean viewReviewerIdentity;

    // 5) 填写审稿意见
    @Column(name = "write_review_comment", nullable = false)
    private boolean writeReviewComment;

    // 6) 做出录用/拒稿决定
    @Column(name = "make_accept_reject_decision", nullable = false)
    private boolean makeAcceptRejectDecision;

    // 7) 修改系统配置
    @Column(name = "modify_system_config", nullable = false)
    private boolean modifySystemConfig;

    public Permission() {}

    // ===== getter / setter =====

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSubmitManuscript() {
        return submitManuscript;
    }

    public void setSubmitManuscript(boolean submitManuscript) {
        this.submitManuscript = submitManuscript;
    }

    public boolean isViewAllManuscripts() {
        return viewAllManuscripts;
    }

    public void setViewAllManuscripts(boolean viewAllManuscripts) {
        this.viewAllManuscripts = viewAllManuscripts;
    }

    public boolean isInviteOrAssignPersonnel() {
        return inviteOrAssignPersonnel;
    }

    public void setInviteOrAssignPersonnel(boolean inviteOrAssignPersonnel) {
        this.inviteOrAssignPersonnel = inviteOrAssignPersonnel;
    }

    public boolean isViewReviewerIdentity() {
        return viewReviewerIdentity;
    }

    public void setViewReviewerIdentity(boolean viewReviewerIdentity) {
        this.viewReviewerIdentity = viewReviewerIdentity;
    }

    public boolean isWriteReviewComment() {
        return writeReviewComment;
    }

    public void setWriteReviewComment(boolean writeReviewComment) {
        this.writeReviewComment = writeReviewComment;
    }

    public boolean isMakeAcceptRejectDecision() {
        return makeAcceptRejectDecision;
    }

    public void setMakeAcceptRejectDecision(boolean makeAcceptRejectDecision) {
        this.makeAcceptRejectDecision = makeAcceptRejectDecision;
    }

    public boolean isModifySystemConfig() {
        return modifySystemConfig;
    }

    public void setModifySystemConfig(boolean modifySystemConfig) {
        this.modifySystemConfig = modifySystemConfig;
    }
}