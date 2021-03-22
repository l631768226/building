package com.ly.building.model;

import java.util.List;

public class PathInfo {

    private String md5;

    private List<UserPath> userPaths;

    private List<List<TravelPoint>> pathInfos;

    private List<TravelPoint> pathPoints;

    public List<TravelPoint> getPathPoints() {
        return pathPoints;
    }

    public void setPathPoints(List<TravelPoint> pathPoints) {
        this.pathPoints = pathPoints;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public List<UserPath> getUserPaths() {
        return userPaths;
    }

    public void setUserPaths(List<UserPath> userPaths) {
        this.userPaths = userPaths;
    }

    public List<List<TravelPoint>> getPathInfos() {
        return pathInfos;
    }

    public void setPathInfos(List<List<TravelPoint>> pathInfos) {
        this.pathInfos = pathInfos;
    }
}
