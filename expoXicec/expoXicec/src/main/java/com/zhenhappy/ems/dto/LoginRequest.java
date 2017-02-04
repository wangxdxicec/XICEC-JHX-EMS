package com.zhenhappy.ems.dto;

/**
 * Created by wangxd on 2017-01-17.
 */
public class LoginRequest extends BaseRequest{
    private String inputPhone;
    private String password;
    private Integer fair_type;

    public String getInputPhone() {
        return inputPhone;
    }

    public void setInputPhone(String inputPhone) {
        this.inputPhone = inputPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getFair_type() {
        return fair_type;
    }

    public void setFair_type(Integer fair_type) {
        this.fair_type = fair_type;
    }
}
