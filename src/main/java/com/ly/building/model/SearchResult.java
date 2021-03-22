package com.ly.building.model;

import java.util.List;

public class SearchResult {

    private String json;

    private List<SearchFileInfo> fileList;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public List<SearchFileInfo> getFileList() {
        return fileList;
    }

    public void setFileList(List<SearchFileInfo> fileList) {
        this.fileList = fileList;
    }
}
