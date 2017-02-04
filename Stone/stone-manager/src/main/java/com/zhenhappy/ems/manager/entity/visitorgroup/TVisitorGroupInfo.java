package com.zhenhappy.ems.manager.entity.visitorgroup;

import javax.persistence.*;

import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by wangxd on 2016-12-26.
 */
@Entity
@Table(name = "t_visitor_group_info", schema = "dbo")
public class TVisitorGroupInfo {
    private Integer id;
    private String group_registration_number;   //注册成功后生成的登记号
    private String group_name;                  //参观团名称
    private String group_password;              //参观团密码
    private String group_header_name;           //团长姓名
    private String group_header_telphone;       //团长手机号
    private String group_header_position;       //团长职位
    private String group_header_email;          //团长邮箱
    private String group_header_address;        //团长地址
    private Date group_header_create_time;      //参观团注册时间
    private Date group_header_update_time;      //参观团更新时间
    private Integer group_id_for_member;        //参观团成员对应的所在团ID
    private String group_member_company;        //参观团成员公司名称
    private String group_member_name;           //参观团成员名称
    private String group_member_position;       //参观团成员职位
    private String group_member_telphone;       //参观团成员电话
    private String group_member_email;          //参观团成员邮箱
    private String group_member_address;        //参观团成员地址
    private Date group_member_create_time;      //参观团成员注册时间
    private Date group_member_update_time;      //参观团成员更新时间
    private Integer group_header_or_member_flag;//团长还是团员标记

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "group_header_address")
    public String getGroup_header_address() {
        return group_header_address;
    }

    public void setGroup_header_address(String group_header_address) {
        this.group_header_address = group_header_address;
    }

    @Basic
    @Column(name = "group_header_create_time")
    public Date getGroup_header_create_time() {
        return group_header_create_time;
    }

    public void setGroup_header_create_time(Date group_header_create_time) {
        this.group_header_create_time = group_header_create_time;
    }

    @Basic
    @Column(name = "group_header_email")
    public String getGroup_header_email() {
        return group_header_email;
    }

    public void setGroup_header_email(String group_header_email) {
        this.group_header_email = group_header_email;
    }

    @Basic
    @Column(name = "group_header_name")
    public String getGroup_header_name() {
        return group_header_name;
    }

    public void setGroup_header_name(String group_header_name) {
        this.group_header_name = group_header_name;
    }

    @Basic
    @Column(name = "group_header_or_member_flag")
    public Integer getGroup_header_or_member_flag() {
        return group_header_or_member_flag;
    }

    public void setGroup_header_or_member_flag(Integer group_header_or_member_flag) {
        this.group_header_or_member_flag = group_header_or_member_flag;
    }

    @Basic
    @Column(name = "group_header_position")
    public String getGroup_header_position() {
        return group_header_position;
    }

    public void setGroup_header_position(String group_header_position) {
        this.group_header_position = group_header_position;
    }

    @Basic
    @Column(name = "group_header_telphone")
    public String getGroup_header_telphone() {
        return group_header_telphone;
    }

    public void setGroup_header_telphone(String group_header_telphone) {
        this.group_header_telphone = group_header_telphone;
    }

    @Basic
    @Column(name = "group_header_update_time")
    public Date getGroup_header_update_time() {
        return group_header_update_time;
    }

    public void setGroup_header_update_time(Date group_header_update_time) {
        this.group_header_update_time = group_header_update_time;
    }

    @Basic
    @Column(name = "group_id_for_member")
    public Integer getGroup_id_for_member() {
        return group_id_for_member;
    }

    public void setGroup_id_for_member(Integer group_id_for_member) {
        this.group_id_for_member = group_id_for_member;
    }

    @Basic
    @Column(name = "group_member_address")
    public String getGroup_member_address() {
        return group_member_address;
    }

    public void setGroup_member_address(String group_member_address) {
        this.group_member_address = group_member_address;
    }

    @Basic
    @Column(name = "group_member_company")
    public String getGroup_member_company() {
        return group_member_company;
    }

    public void setGroup_member_company(String group_member_company) {
        this.group_member_company = group_member_company;
    }

    @Basic
    @Column(name = "group_member_create_time")
    public Date getGroup_member_create_time() {
        return group_member_create_time;
    }

    public void setGroup_member_create_time(Date group_member_create_time) {
        this.group_member_create_time = group_member_create_time;
    }

    @Basic
    @Column(name = "group_member_email")
    public String getGroup_member_email() {
        return group_member_email;
    }

    public void setGroup_member_email(String group_member_email) {
        this.group_member_email = group_member_email;
    }

    @Basic
    @Column(name = "group_member_name")
    public String getGroup_member_name() {
        return group_member_name;
    }

    public void setGroup_member_name(String group_member_name) {
        this.group_member_name = group_member_name;
    }

    @Basic
    @Column(name = "group_member_position")
    public String getGroup_member_position() {
        return group_member_position;
    }

    public void setGroup_member_position(String group_member_position) {
        this.group_member_position = group_member_position;
    }

    @Basic
    @Column(name = "group_member_telphone")
    public String getGroup_member_telphone() {
        return group_member_telphone;
    }

    public void setGroup_member_telphone(String group_member_telphone) {
        this.group_member_telphone = group_member_telphone;
    }

    @Basic
    @Column(name = "group_member_update_time")
    public Date getGroup_member_update_time() {
        return group_member_update_time;
    }

    public void setGroup_member_update_time(Date group_member_update_time) {
        this.group_member_update_time = group_member_update_time;
    }

    @Basic
    @Column(name = "group_name")
    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    @Basic
    @Column(name = "group_password")
    public String getGroup_password() {
        return group_password;
    }

    public void setGroup_password(String group_password) {
        this.group_password = group_password;
    }

    @Basic
    @Column(name = "group_registration_number")
    public String getGroup_registration_number() {
        return group_registration_number;
    }

    public void setGroup_registration_number(String group_registration_number) {
        this.group_registration_number = group_registration_number;
    }
}
