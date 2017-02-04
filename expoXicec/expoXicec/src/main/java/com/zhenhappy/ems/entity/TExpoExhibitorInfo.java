package com.zhenhappy.ems.entity;

/**
 * Created by wangxd on 2017-01-19.
 */
public class TExpoExhibitorInfo {
    private Integer id;  //对应TExhibitor的eid;
    private Integer eid;
    private String boothnumber;
    private String company;
    private String areanumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEid() {
        return eid;
    }

    public void setEid(Integer eid) {
        this.eid = eid;
    }

    public String getAreanumber() {
        return areanumber;
    }

    public void setAreanumber(String areanumber) {
        this.areanumber = areanumber;
    }

    public String getBoothnumber() {
        return boothnumber;
    }

    public void setBoothnumber(String boothnumber) {
        this.boothnumber = boothnumber;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}

