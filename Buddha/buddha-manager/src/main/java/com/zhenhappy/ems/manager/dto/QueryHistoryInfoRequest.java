package com.zhenhappy.ems.manager.dto;

/**
 * Created by wangxd on 2016-05-24.
 */
public class QueryHistoryInfoRequest extends EasyuiRequest {
	private Integer id;
	private String address;   	//地址
	private String company;     //公司名
	private String contact;     //联系人
	private String mobile;		//手机
	private String telphone;    //电话
	private String fax;         //传真
	private String email;       //邮箱
	private String tel_remark;  //电话记录
	private Integer type;		//类别
	private Integer inlandOrOutland;//0：表示境内；1：表示境外

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

	public Integer getInlandOrOutland() {
		return inlandOrOutland;
	}

	public void setInlandOrOutland(Integer inlandOrOutland) {
		this.inlandOrOutland = inlandOrOutland;
	}
}
