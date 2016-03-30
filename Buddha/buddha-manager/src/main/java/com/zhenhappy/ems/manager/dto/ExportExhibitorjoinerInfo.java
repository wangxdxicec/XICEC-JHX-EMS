package com.zhenhappy.ems.manager.dto;

import com.zhenhappy.ems.entity.WCustomer;

/**
 * Created by wangxd9 on 2016-3-30.
 */
public class ExportExhibitorjoinerInfo extends WCustomer {
	private String name;
	private String position;
	private String email;
	private String telphone;

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getPosition() {
		return position;
	}

	@Override
	public void setPosition(String position) {
		this.position = position;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}
}
