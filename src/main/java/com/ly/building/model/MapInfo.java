package com.ly.building.model;

public class MapInfo {

    private int id;

    private int yearTime;

    private int dayTime;

    private int monthTime;

    private int weekTime;

    private double lng;

    private double lat;

    private String timeStr;

    private String actReal;

    private String actPred;

    private int period;

    private int tag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYearTime() {
        return yearTime;
    }

    public void setYearTime(int yearTime) {
        this.yearTime = yearTime;
    }

    public int getDayTime() {
        return dayTime;
    }

    public void setDayTime(int dayTime) {
        this.dayTime = dayTime;
    }

    public int getMonthTime() {
        return monthTime;
    }

    public void setMonthTime(int monthTime) {
        this.monthTime = monthTime;
    }

    public int getWeekTime() {
        return weekTime;
    }

    public void setWeekTime(int weekTime) {
        this.weekTime = weekTime;
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

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public String getActReal() {
        return actReal;
    }

    public void setActReal(String actReal) {
        this.actReal = actReal;
    }

    public String getActPred() {
        return actPred;
    }

    public void setActPred(String actPred) {
        this.actPred = actPred;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
