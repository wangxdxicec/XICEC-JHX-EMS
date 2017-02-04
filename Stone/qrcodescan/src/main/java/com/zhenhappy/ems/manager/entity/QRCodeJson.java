package com.zhenhappy.ems.manager.entity;

/**
 * Created by Administrator on 2016/4/20.
 */
public class QRCodeJson {
    private String mobile;

    public QRCodeJson() {
    }

    public QRCodeJson(String mobile) {
        this.mobile = mobile;
    }
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
