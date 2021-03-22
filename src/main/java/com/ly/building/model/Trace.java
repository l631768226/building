package com.ly.building.model;

public class Trace {

    private int id;

    private int userId;

    private Integer serialNum;

    private double lon;

    private double lat;

    private String dateS;

    private String timeS;

    private String timeStr;

    private int truth;

    private int pred;

    public Integer getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(Integer serialNum) {
        this.serialNum = serialNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
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

    public int getTruth() {
        return truth;
    }

    public void setTruth(int truth) {
        this.truth = truth;
    }

    public int getPred() {
        return pred;
    }

    public void setPred(int pred) {
        this.pred = pred;
    }
}
