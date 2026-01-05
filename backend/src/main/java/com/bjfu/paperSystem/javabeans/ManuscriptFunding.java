package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;

@Entity
@Table(name = "manuscript_funding")
public class ManuscriptFunding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "manuscript_id")
    private int manuscriptId;

    @Column(name = "grant_name")
    private String grantName; // 资助项目名称

    @Column(name = "grant_number")
    private String grantNumber; // 资助编号

    @Column(name = "organization")
    private String organization; // 资助机构

    // --- Getter and Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getManuscriptId() { return manuscriptId; }
    public void setManuscriptId(int manuscriptId) { this.manuscriptId = manuscriptId; }
    public String getGrantName() { return grantName; }
    public void setGrantName(String grantName) { this.grantName = grantName; }
    public String getGrantNumber() { return grantNumber; }
    public void setGrantNumber(String grantNumber) { this.grantNumber = grantNumber; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
}