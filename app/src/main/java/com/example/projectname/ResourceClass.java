package com.example.projectname;

import java.io.Serializable;

public class ResourceClass implements Serializable {

    String idOfUploader;
    String fileName;
    String fileUrl;
    String time;
    String mimeType;

    public ResourceClass(String idOfUploader, String fileName, String fileUrl, String time, String mimeType) {
        this.idOfUploader = idOfUploader;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.time = time;
        this.mimeType = mimeType;
    }

    public ResourceClass() {
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getIdOfUploader() {
        return idOfUploader;
    }

    public void setIdOfUploader(String idOfUploader) {
        this.idOfUploader = idOfUploader;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
