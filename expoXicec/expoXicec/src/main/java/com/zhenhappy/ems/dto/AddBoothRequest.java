package com.zhenhappy.ems.dto;

/**
 * Created by wangxd on 2017-01-20.
 */
public class AddBoothRequest extends BaseRequest {
	private String Name;
	private String Company;
	private String Contact;
	private String Mobile;
	private String Type;

	public String getCompany() {
		return Company;
	}

	public void setCompany(String company) {
		Company = company;
	}

	public String getContact() {
		return Contact;
	}

	public void setContact(String contact) {
		Contact = contact;
	}

	public String getMobile() {
		return Mobile;
	}

	public void setMobile(String mobile) {
		Mobile = mobile;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}
}
