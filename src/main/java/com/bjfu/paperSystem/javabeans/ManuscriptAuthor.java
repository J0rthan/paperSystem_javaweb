package com.bjfu.paperSystem.javabeans;

import jakarta.persistence.*;

@Entity
@Table(name = "manuscript_authors")
public class ManuscriptAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_info_id")
    private int id;

    @Column(name = "manuscript_id")
    private int manuscriptId;

    @Column(name = "full_name")
    private String name;

    private String institution;
    private String email;
    private String degree;

    @Column(name = "professional_title")
    private String professionalTitle;

    private String position;

    @Column(name = "is_corresponding")
    private Boolean corresponding = false; // 改为包装类并赋初值

    public ManuscriptAuthor() {}

    // --- Getter and Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getManuscriptId() { return manuscriptId; }
    public void setManuscriptId(int manuscriptId) { this.manuscriptId = manuscriptId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getProfessionalTitle() { return professionalTitle; }
    public void setProfessionalTitle(String professionalTitle) { this.professionalTitle = professionalTitle; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    // 提供标准的 get/is/set 方法，确保 Thymeleaf 怎么都能读到
    public Boolean getCorresponding() { return corresponding; }
    public boolean isCorresponding() { return corresponding != null && corresponding; }
    public void setCorresponding(Boolean corresponding) { this.corresponding = corresponding; }
}