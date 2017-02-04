package com.zhenhappy.ems.manager.entity;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by Administrator on 2016/4/19.
 */
@Entity
@Table(name = "code_scan_info", schema = "dbo")
public class QRCodeScanInfo implements Serializable {
    private int id;
    private String phonenumber;
    private Integer count;

    public QRCodeScanInfo() {
    }

    public QRCodeScanInfo(Integer id) {
        this.id = id;
    }

    public QRCodeScanInfo(Integer id, String phonenumber, Integer count) {
        this.id = id;
        this.phonenumber = phonenumber;
        this.count = count;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "count")
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Column(name = "phonenumber")
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
