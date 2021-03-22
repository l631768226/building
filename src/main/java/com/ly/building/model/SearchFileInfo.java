package com.ly.building.model;

import java.util.List;

public class SearchFileInfo {

    private String fileName;

    private String fileStr;

    private PlaceModel fileInfo;

    private List<TableData> tableData;

    public List<TableData> getTableData() {
        return tableData;
    }

    public void setTableData(List<TableData> tableData) {
        this.tableData = tableData;
    }

    public PlaceModel getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(PlaceModel fileInfo) {
        this.fileInfo = fileInfo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileStr() {
        return fileStr;
    }

    public void setFileStr(String fileStr) {
        this.fileStr = fileStr;
    }
}
