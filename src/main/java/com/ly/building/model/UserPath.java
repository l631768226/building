package com.ly.building.model;

import java.util.List;

public class UserPath {

    private String userId;

    private List<Integer> pathIds;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Integer> getPathIds() {
        return pathIds;
    }

    public void setPathIds(List<Integer> pathIds) {
        this.pathIds = pathIds;
    }
}
