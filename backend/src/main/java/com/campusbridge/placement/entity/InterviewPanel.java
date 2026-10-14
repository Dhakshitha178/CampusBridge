package com.campusbridge.placement.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "interview_panels")
public class InterviewPanel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "panel_name", nullable = false)
    private String panelName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "interview_panel_members",
        joinColumns = @JoinColumn(name = "panel_id"),
        inverseJoinColumns = @JoinColumn(name = "interviewer_id")
    )
    private List<Interviewer> members = new ArrayList<>();

    public InterviewPanel() {}

    public InterviewPanel(Company company, String panelName, String description) {
        this.company = company;
        this.panelName = panelName;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public String getPanelName() { return panelName; }
    public void setPanelName(String panelName) { this.panelName = panelName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Interviewer> getMembers() { return members; }
    public void setMembers(List<Interviewer> members) { this.members = members; }
}
