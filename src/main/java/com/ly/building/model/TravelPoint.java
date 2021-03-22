package com.ly.building.model;

public class TravelPoint {

    private double lng;

    private double lat;

    private int weight;

    private int userId;

    private String dateS;

    private String timeS;

    private String timeStr;

    private Integer fromTag = 0;

    private String lineTag = "";

    public Integer getFromTag() {
        return fromTag;
    }

    public void setFromTag(Integer fromTag) {
        this.fromTag = fromTag;
    }

    public String getLineTag() {
        return lineTag;
    }

    public void setLineTag(String lineTag) {
        this.lineTag = lineTag;
    }

    public String getDateS() {
        return dateS;
    }

    public void setDateS(String dateS) {
        this.dateS = dateS;
    }

    public String getTimeS() {
        return timeS;
    }

    public void setTimeS(String timeS) {
        this.timeS = timeS;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
