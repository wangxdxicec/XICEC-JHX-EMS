package com.zhenhappy.ems.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wangxd on 2017-01-16.
 */
@Entity
@Table(name = "t_expoXicec")
public class TExpoXicec {

    private Integer id;
    private String mobilephone;  //注册手机号--登录帐号
    private String username;
    private String password;
    private String company;
    private String email;
    private Date create_Time;
    private Date update_Time;
    private Integer fair_type;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "company")
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Column(name = "create_Time")
    public Date getCreate_Time() {
        return create_Time;
    }

    public void setCreate_Time(Date create_Time) {
        this.create_Time = create_Time;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "mobilephone")
    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "update_Time")
    public Date getUpdate_Time() {
        return update_Time;
    }

    public void setUpdate_Time(Date update_Time) {
        this.update_Time = update_Time;
    }

    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "fair_type")
    public Integer getFair_type() {
        return fair_type;
    }

    public void setFair_type(Integer fair_type) {
        this.fair_type = fair_type;
    }
}

