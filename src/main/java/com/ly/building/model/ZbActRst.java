package com.ly.building.model;

import java.util.List;

public class ZbActRst {

    private List<MapResult> allPoints;

    private List<MapResult> errorPoints;

    private List<MapResult> rightPoints;

    private List<UserCount> userCounts;

    private Integer sum;

    private Integer errorCount;

    private Integer rightCount;

    private Integer tag = 1;

    public List<UserCount> getUserCounts() {
        return userCounts;
    }

    public void setUserCounts(List<UserCount> userCounts) {
        this.userCounts = userCounts;
    }

    public List<MapResult> getAllPoints() {
        return allPoints;
    }

    public void setAllPoints(List<MapResult> allPoints) {
        this.allPoints = allPoints;
    }

    public List<MapResult> getErrorPoints() {
        return errorPoints;
    }

    public void setErrorPoints(List<MapResult> errorPoints) {
        this.errorPoints = errorPoints;
    }

    public List<MapResult> getRightPoints() {
        return rightPoints;
    }

    public void setRightPoints(List<MapResult> rightPoints) {
        this.rightPoints = rightPoints;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public Integer getRightCount() {
        return rightCount;
    }

    public void setRightCount(Integer rightCount) {
        this.rightCount = rightCount;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }
}
