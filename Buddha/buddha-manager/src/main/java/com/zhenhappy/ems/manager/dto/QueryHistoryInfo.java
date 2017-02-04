package com.zhenhappy.ems.manager.dto;

import java.util.Date;

/**
 * Created by wangxd on 2016-07-15.
 */
public class QueryHistoryInfo extends EasyuiRequest {
    private Integer id;
    private String address;    //地址
    private String company;     //公司名
    private String contact;     //联系人
    private String mobile;        //手机
    private String telphone;    //电话
    private String fax;         //传真
    private String email;       //邮箱
    private String tel_remark;  //电话记录
    private Integer type;        //类别
    private Date create_time;   //导入时间；
    private Date update_time;   //更新时间；
    private String fair_time;   //展会时间
    private Integer inlandOrOutland; //0：表示境内；1：表示境外
    private String field_bak2; //备用字段2
    private String field_bak3; //备用字段3

    public QueryHistoryInfo() {
    }

    public QueryHistoryInfo(Integer id, String address, String company, String contact, String mobile, String telphone, String fax,
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel_remark() {
        return tel_remark;
    }

    public void setTel_remark(String tel_remark) {
        this.tel_remark = tel_remark;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getFair_time() {
        return fair_time;
    }

    public void setFair_time(String fair_year) {
        this.fair_time = fair_year;
    }

    public Integer getInlandOrOutland() {
        return inlandOrOutland;
    }

    public void setInlandOrOutland(Integer inlandOrOutland) {
        this.inlandOrOutland = inlandOrOutland;
    }

    public String getField_bak2() {
        return field_bak2;
    }

    public void setField_bak2(String field_bak2) {
        this.field_bak2 = field_bak2;
    }

    public String getField_bak3() {
        return field_bak3;
    }

    public void setField_bak3(String field_bak3) {
        this.field_bak3 = field_bak3;
    }
}
