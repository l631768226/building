package com.ly.building.model;

import java.util.List;

public class ActRst {

    private List<MapResult> allPoints;

    private List<MapResult> rightPoints;

    private List<MapResult> errorPoints;

    private int sum;

    private int errorCount;

    private int rightCount;

    public List<MapResult> getAllPoints() {
        return allPoints;
    }

    public void setAllPoints(List<MapResult> allPoints) {
        this.allPoints = allPoints;
    }

    public List<MapResult> getRightPoints() {
        return rightPoints;
    }

    public void setRightPoints(List<MapResult> rightPoints) {
        this.rightPoints = rightPoints;
    }

    public List<MapResult> getErrorPoints() {
        return errorPoints;
    }

    public void setErrorPoints(List<MapResult> errorPoints) {
        this.errorPoints = errorPoints;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getRightCount() {
        return rightCount;
    }

    public void setRightCount(int rightCount) {
        this.rightCount = rightCount;
    }
}
