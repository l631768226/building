package com.ly.building.model;

import java.math.BigInteger;

public class Tsmc {

    private String id;

    private String userId;

    private String venueId;

    private String venueCateId;

    private String venueCateName;

    private double latitude;

    private double longtitude;

    private String timeZone;

    private String utc;

    private String loca;

    private BigInteger lat;

    private BigInteger lon;

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getVenueCateId() {
        return venueCateId;
    }

    public void setVenueCateId(String venueCateId) {
        this.venueCateId = venueCateId;
    }

    public String getVenueCateName() {
        return venueCateName;
    }

    public void setVenueCateName(String venueCateName) {
        this.venueCateName = venueCateName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getUtc() {
        return utc;
    }

    public void setUtc(String utc) {
        this.utc = utc;
    }

    public String getLoca() {
        return loca;
    }

    public void setLoca(String loca) {
        this.loca = loca;
    }

    public BigInteger getLat() {
        return lat;
    }

    public void setLat(BigInteger lat) {
        this.lat = lat;
    }

    public BigInteger getLon() {
        return lon;
    }

    public void setLon(BigInteger lon) {
        this.lon = lon;
    }
}
