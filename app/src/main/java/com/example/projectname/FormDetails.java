package com.example.projectname;

import java.io.Serializable;
import java.util.List;

public class FormDetails implements Serializable {

    String projectName;
    String company;
    String projectDescription;
    String date;
    String projectImage;
    List<ResourceClass> resourceUris;
    List<String> members;

    public FormDetails(String projectName, String company, String projectDescription, String date, String projectImage,
                       List<ResourceClass> resourceUris, List<String> members) {
        this.projectName = projectName;
        this.company = company;
        this.projectDescription = projectDescription;
        this.date = date;
        this.projectImage = projectImage;
        this.resourceUris = resourceUris;
        this.members = members;
    }

    public FormDetails() {
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<ResourceClass> getResourceUris() {
        return resourceUris;
    }

    public void setResourceUris(List<ResourceClass> resourceUris) {
        this.resourceUris = resourceUris;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getProjectImage() {
        return projectImage;
    }

    public void setProjectImage(String projectImage) {
        this.projectImage = projectImage;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
