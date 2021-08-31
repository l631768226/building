package com.ly.building.model;

import java.util.List;

public class BioActiveRst {

    String total_cover;
    String total_gain;
    String gain;
    String cover;
    String weight;

    private String img2;

    private List<GeneModel> geneList;

    public String getTotal_cover() {
        return total_cover;
    }

    public void setTotal_cover(String total_cover) {
        this.total_cover = total_cover;
    }

    public String getTotal_gain() {
        return total_gain;
    }

    public void setTotal_gain(String total_gain) {
        this.total_gain = total_gain;
    }

    public String getGain() {
        return gain;
    }

    public void setGain(String gain) {
        this.gain = gain;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public List<GeneModel> getGeneList() {
        return geneList;
    }

    public void setGeneList(List<GeneModel> geneList) {
        this.geneList = geneList;
    }
}
