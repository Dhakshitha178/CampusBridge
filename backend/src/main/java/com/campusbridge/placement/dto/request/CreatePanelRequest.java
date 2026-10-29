package com.campusbridge.placement.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.ArrayList;
import java.util.List;

public class CreatePanelRequest {

    @JsonAlias({"name", "title", "panelTitle"})
    private String panelName;

    private String description;

    private List<Long> interviewerIds = new ArrayList<>();

    public CreatePanelRequest() {}

    public CreatePanelRequest(String panelName, String description, List<Long> interviewerIds) {
        this.panelName = panelName;
        this.description = description;
        this.interviewerIds = interviewerIds != null ? interviewerIds : new ArrayList<>();
    }

    public String getPanelName() {
        return panelName;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

    public String getName() {
        return panelName;
    }

    public void setName(String name) {
        this.panelName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getInterviewerIds() {
        return interviewerIds;
    }

    public void setInterviewerIds(List<Long> interviewerIds) {
        this.interviewerIds = interviewerIds;
    }
}
