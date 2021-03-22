package com.ly.building.model;

public class RstPointData<T, V> {

    private int code;

    private String msg;

    private T data;

    private V points;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public V getPoints() {
        return points;
    }

    public void setPoints(V points) {
        this.points = points;
    }
}
