package com.ly.building.model;

import java.util.List;

public class AnalysisRes {

    private List<String> imgList;

    private Integer tag = 0;

    private List<Integer> userIdList;

    private List<MapResult> mapResults;

    public List<MapResult> getMapResults() {
        return mapResults;
    }

    public void setMapResults(List<MapResult> mapResults) {
        this.mapResults = mapResults;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public List<Integer> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Integer> userIdList) {
        this.userIdList = userIdList;
    }
}
