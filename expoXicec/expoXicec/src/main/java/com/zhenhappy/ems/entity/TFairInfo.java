package com.zhenhappy.ems.entity;

import javax.persistence.*;

/**
 * Created by wangxd on 2017-01-16.
 */
@Entity
@Table(name = "t_fair_info")
public class TFairInfo {

    private Integer id;
    private String fair_name;      //展会名称--用于前台界面显示
    private String fair_name_alias;//展会别名
    private Integer fair_enable;   //是否激活该展会

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "fair_enable")
    public Integer getFair_enable() {
        return fair_enable;
    }

    public void setFair_enable(Integer fair_enable) {
        this.fair_enable = fair_enable;
    }

    @Column(name = "fair_name")
    public String getFair_name() {
        return fair_name;
    }

    public void setFair_name(String fair_name) {
        this.fair_name = fair_name;
    }

    @Column(name = "fair_name_alias")
    public String getFair_name_alias() {
        return fair_name_alias;
    }

    public void setFair_name_alias(String fair_name_alias) {
        this.fair_name_alias = fair_name_alias;
    }
}

