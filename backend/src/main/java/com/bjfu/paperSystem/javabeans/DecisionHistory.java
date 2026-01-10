package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
@Table(name = "decision_history")
public class DecisionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联稿件
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manuscript_id", nullable = false)
    private Manuscript manuscript;

    // 这一条决策属于第几轮？(Round 1, Round 2...)
    private Integer round;

    // 编辑的建议 (存档)
    private String editorRecommendation; // e.g., "ACCEPT", "REVISE"
    @Column(columnDefinition = "TEXT")
    private String editorComment; // 编辑给主编的话

    // 主编的最终决策 (存档)
    private String finalDecision; // e.g., "REVISE", "REJECT"
    @Column(columnDefinition = "TEXT")
    private String finalDecisionComment; // 主编给作者的批注（决策邮件内容核心）

    // 决策时间
    private Date decisionDate;

    // 是谁做的决策 (关联 User)
    @ManyToOne
    @JoinColumn(name = "decider_id")
    private User decider;

    public Integer getRound() { return round; }
    public void setRound(Integer round) { this.round = round;}
    public String getEditorRecommendation() { return editorRecommendation; }
    public void setEditorRecommendation(String editorRecommendation) { this.editorRecommendation = editorRecommendation;}
    public String getEditorComment() { return editorComment; }
    public void setEditorComment(String editorComment) { this.editorComment = editorComment;}
    public String getFinalDecision() { return finalDecision; }
    public void setFinalDecision(String finalDecision) { this.finalDecision = finalDecision;}
    public String getFinalDecisionComment() { return finalDecisionComment; }
    public void setFinalDecisionComment(String finalDecisionComment) { this.finalDecisionComment = finalDecisionComment;}
    public Date getDecisionDate() { return decisionDate; }
    public void setDecisionDate(Date decisionDate) { this.decisionDate = decisionDate;}
    public User getDecider() { return decider; }
    public void setDecider(User decider) { this.decider = decider;}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public void setManuscript(Manuscript manu) {this.manuscript = manu;}
    public Manuscript getManuscript() {return this.manuscript;}
}