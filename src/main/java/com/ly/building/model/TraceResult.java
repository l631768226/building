package com.ly.building.model;

import java.util.List;

public class TraceResult {

    private List<List<GeoPoint>> points;

    private List<TracePoint> markers;

    public List<List<GeoPoint>> getPoints() {
        return points;
    }

    public void setPoints(List<List<GeoPoint>> points) {
        this.points = points;
    }

    public List<TracePoint> getMarkers() {
        return markers;
    }

    public void setMarkers(List<TracePoint> markers) {
        this.markers = markers;
    }
}
