package com.ly.building.model;

public class ListModel {

    private String dateStr;

    private Integer zoneType;

    private Integer userId;

    private String labelStr;

    public String getLabelStr() {
        return labelStr;
    }

    public void setLabelStr(String labelStr) {
        this.labelStr = labelStr;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public Integer getZoneType() {
        return zoneType;
    }

    public void setZoneType(Integer zoneType) {
        this.zoneType = zoneType;
    }
}
