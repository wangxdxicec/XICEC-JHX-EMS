package com.zhenhappy.ems.manager.dto;

/**
 * Created by wangxd on 2015-03-30.
 */
public class QueryExhibitorJoiner {
	private Integer eid;
	private String name;
	private String position;
	private String telphone;
	private String email;
	private String create_time;
	private String admin_update_time;

	public String getAdmin_update_time() {
		return admin_update_time;
	}

	public void setAdmin_update_time(String admin_update_time) {
		this.admin_update_time = admin_update_time;
	}

	public Integer getEid() {
		return eid;
	}

	public void setEid(Integer eid) {
		this.eid = eid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

}
