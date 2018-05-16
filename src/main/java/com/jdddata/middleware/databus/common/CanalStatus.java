package com.jdddata.middleware.databus.common;

public enum CanalStatus {

    RUNNING("running"),

    STOPPING("stopping");


    private String value;

    CanalStatus(String value) {
        this.value = value;

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
