package com.zhenhappy.ems.manager.entity;

import javax.persistence.*;

import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by wangxd on 2016-07-14.
 */
@Entity
@Table(name = "t_history_visitor_info", schema = "dbo")
public class THistoryVisitorInfo {
    private Integer id;
    private String address;     //地址
    private String company;     //公司名
    private String contact;     //联系人
    private String mobile;      //手机
    private String telphone;    //电话
    private String fax;         //传真
    private String email;       //邮箱
    private String tel_remark;  //电话记录
    private Integer type;       //类别
    private Date create_time;   //导入时间
    private Date update_time;   //更新时间
    private String fair_time;   //展会时间
    private Integer inlandOrOutland;  //0：表示境内；1：表示境外
    private String field_bak2;  //备用字段2
    private String field_bak3;  //备用字段3

    public THistoryVisitorInfo() {
    }

    public THistoryVisitorInfo(Integer id) {
        this.id = id;
    }

    public THistoryVisitorInfo(Integer id, String address, String company, String contact, String mobile, String telphone, String fax,
                               String email, String tel_remark, Integer type, Date create_time, Date update_time, String fair_time,
                               Integer inlandOrOutland, String field_bak2, String field_bak3) {
        this.id = id;
        this.address = address;
        this.company = company;
        this.contact = contact;
        this.mobile = mobile;
        this.telphone = telphone;
        this.fax = fax;
        this.email = email;
        this.tel_remark = tel_remark;
        this.type = type;
        this.create_time = create_time;
        this.update_time = update_time;
        this.fair_time = fair_time;
        this.inlandOrOutland = inlandOrOutland;
        this.field_bak2 = field_bak2;
        this.field_bak3 = field_bak3;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic
    @Column(name = "company")
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Basic
    @Column(name = "contact")
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Basic
    @Column(name = "mobile")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Basic
    @Column(name = "telphone")
    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    @Basic
    @Column(name = "fax")
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "tel_remark")
    public String getTel_remark() {
        return tel_remark;
    }

    public void setTel_remark(String tel_remark) {
        this.tel_remark = tel_remark;
    }

    @Basic
    @Column(name = "type")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Basic
    @Column(name = "create_time")
    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    @Basic
    @Column(name = "update_time")
    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    @Basic
    @Column(name = "fair_time")
    public String getFair_time() {
        return fair_time;
    }

    public void setFair_time(String fair_time) {
        this.fair_time = fair_time;
    }

    @Basic
    @Column(name = "inlandOrOutland")
    public Integer getInlandOrOutland() {
        return inlandOrOutland;
    }

    public void setInlandOrOutland(Integer inlandOrOutland) {
        this.inlandOrOutland = inlandOrOutland;
    }

    @Basic
    @Column(name = "field_bak2")
    public String getField_bak2() {
        return field_bak2;
    }

    public void setField_bak2(String field_bak2) {
        this.field_bak2 = field_bak2;
    }

    @Basic
    @Column(name = "field_bak3")
    public String getField_bak3() {
        return field_bak3;
    }

    public void setField_bak3(String field_bak3) {
        this.field_bak3 = field_bak3;
    }
}
