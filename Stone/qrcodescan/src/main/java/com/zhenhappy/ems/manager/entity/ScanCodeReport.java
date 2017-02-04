package com.zhenhappy.ems.manager.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/21.
 */
public class ScanCodeReport implements Serializable {
    private String phoneType;
    private Integer phoneValue;
    private String netType;
    private Integer netValue;

    public ScanCodeReport() {}

    public ScanCodeReport(String phoneType, Integer value, String netType, Integer netValue) {
        this.phoneType = phoneType;
        this.phoneValue = value;
        this.netType = netType;
        this.netValue = netValue;
    }
    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public Integer getPhoneValue() {
        return phoneValue;
    }

    public void setPhoneValue(Integer phoneValue) {
        this.phoneValue = phoneValue;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public Integer getNetValue() {
        return netValue;
    }

    public void setNetValue(Integer netValue) {
        this.netValue = netValue;
    }
}
