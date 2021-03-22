package com.ly.building.model;

public class TraceRes {

    private Integer userId;

    private Integer traceId;

    private String travelMode;

    private Integer traMode;

    private Double aveV;

    private Double aveA;

    private Double aveAA;

    private Double aveDV;

    private Double distance;

    private String startTime;

    private String endTime;

    private String duringTime;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTraceId() {
        return traceId;
    }

    public void setTraceId(Integer traceId) {
        this.traceId = traceId;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public Integer getTraMode() {
        return traMode;
    }

    public void setTraMode(Integer traMode) {
        this.traMode = traMode;
    }

    public Double getAveV() {
        return aveV;
    }

    public void setAveV(Double aveV) {
        this.aveV = aveV;
    }

    public Double getAveA() {
        return aveA;
    }

    public void setAveA(Double aveA) {
        this.aveA = aveA;
    }

    public Double getAveAA() {
        return aveAA;
    }

    public void setAveAA(Double aveAA) {
        this.aveAA = aveAA;
    }

    public Double getAveDV() {
        return aveDV;
    }

    public void setAveDV(Double aveDV) {
        this.aveDV = aveDV;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDuringTime() {
        return duringTime;
    }

    public void setDuringTime(String duringTime) {
        this.duringTime = duringTime;
    }
}
