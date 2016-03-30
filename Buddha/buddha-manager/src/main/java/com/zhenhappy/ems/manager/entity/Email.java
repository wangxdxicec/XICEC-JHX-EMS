package com.zhenhappy.ems.manager.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Email {
	private String name;
	private String boothNumber;
	private String company;
	private String receivers;
	private String subject;
    private int gender;
	private String fromAddress = "service@stonefair.org.cn";
	private int flag; //0:表示展会观众；1：表示专业采购商
	private int country; //0:表示中国；1：表示国外
	private String followName; //随行人员
	private String regID; //预登记编号

	public Email() {
		super();
	}

	public Email(String name, String boothNumber, String company,
				 String receivers, String subject, int flag, int country, String followName, String RegID) {
		super();
		this.name = name;
		this.boothNumber = boothNumber;
		this.company = company;
		this.receivers = receivers;
		this.subject = subject;
		this.fromAddress = "service@stonefair.org.cn";
		this.flag = flag;
		this.country = country;
		this.followName = followName;
		this.regID = RegID;
	}

	public String getFollowName() {
		return followName;
	}

	public void setFollowName(String followName) {
		this.followName = followName;
	}

	public String getRegID() {
		return regID;
	}

	public void setRegID(String regID) {
		this.regID = regID;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBoothNumber() {
		return boothNumber;
	}

	public void setBoothNumber(String boothNumber) {
		this.boothNumber = boothNumber;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getReceivers() {
		return receivers;
	}

	public void setReceivers(String receivers) {
		this.receivers = receivers;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getGenderStr(){
        return gender==1?"先生":"女士";
    }

    public String getGenderStrEn(){
        return gender==1?"先生":"女士";
    }

    public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(new Date());
    }

    public String getTimeEn(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

	public int getCountry() {
		return country;
	}

	public void setCountry(int country) {
		this.country = country;
	}
}
