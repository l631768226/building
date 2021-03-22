package com.ly.building.model;

import java.util.List;

public class SFPointRst {

    public List<MapResult> getMapResultList() {
        return mapResultList;
    }

    public void setMapResultList(List<MapResult> mapResultList) {
        this.mapResultList = mapResultList;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    private List<MapResult> mapResultList;

    private List<String> imgList;

}
