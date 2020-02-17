package com.example.projectname;

public class InviteClass {

    String projectName;
    String company;

    public InviteClass(String projectName, String company) {
        this.projectName = projectName;
        this.company = company;
    }

    public InviteClass() {
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
}
