package com.campusbridge.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    private String website;
    private String industry;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String city;
    private String state;

    @Column(name = "hr_contact_name")
    private String hrContactName;

    @Column(name = "hr_contact_email")
    private String hrContactEmail;

    @Column(name = "hr_contact_phone")
    private String hrContactPhone;

    private Boolean verified = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Company() {}

    public Company(String name, String website, String industry, String description, String city, String state, String hrContactName, String hrContactEmail, String hrContactPhone) {
        this.name = name;
        this.website = website;
        this.industry = industry;
        this.description = description;
        this.city = city;
        this.state = state;
        this.hrContactName = hrContactName;
        this.hrContactEmail = hrContactEmail;
        this.hrContactPhone = hrContactPhone;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getHrContactName() { return hrContactName; }
    public void setHrContactName(String hrContactName) { this.hrContactName = hrContactName; }
    public String getHrContactEmail() { return hrContactEmail; }
    public void setHrContactEmail(String hrContactEmail) { this.hrContactEmail = hrContactEmail; }
    public String getHrContactPhone() { return hrContactPhone; }
    public void setHrContactPhone(String hrContactPhone) { this.hrContactPhone = hrContactPhone; }
    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
