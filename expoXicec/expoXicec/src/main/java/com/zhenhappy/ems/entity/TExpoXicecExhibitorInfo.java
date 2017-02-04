package com.zhenhappy.ems.entity;

/**
 * Created by wangxd on 2017-01-16.
 */
public class TExpoXicecExhibitorInfo {
    private Integer eid;
    private String username;
    private String password;
    private String country;
    private String exhibitor_area;
    private String company;
    private String booth_number;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getExhibitor_area() {
        return exhibitor_area;
    }

    public void setExhibitor_area(String exhibitor_area) {
        this.exhibitor_area = exhibitor_area;
    }

    public String getBooth_number() {
        return booth_number;
    }

    public void setBooth_number(String booth_number) {
        this.booth_number = booth_number;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getEid() {
        return eid;
    }

    public void setEid(Integer eid) {
        this.eid = eid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

