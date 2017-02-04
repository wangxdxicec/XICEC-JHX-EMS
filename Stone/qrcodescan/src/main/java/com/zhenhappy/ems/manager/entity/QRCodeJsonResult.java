package com.zhenhappy.ems.manager.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/19.
 */
public class QRCodeJsonResult implements Serializable {
    private String result;
    private String msg;
    private String Name;
    private String Design;
    private String Company;
    private String Tel;
    private String Email;

    public QRCodeJsonResult() {
    }

    public QRCodeJsonResult(String result, String msg, String name, String design,
                            String company, String tel,String email) {
        this.result = result;
        this.msg = msg;
        this.Name = name;
        this.Design = design;
        this.Company = company;
        this.Tel = tel;
        this.Email = email;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDesign() {
        return Design;
    }

    public void setDesign(String design) {
        Design = design;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }
}
