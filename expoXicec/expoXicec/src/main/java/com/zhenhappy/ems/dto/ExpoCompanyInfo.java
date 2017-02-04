package com.zhenhappy.ems.dto;

public class ExpoCompanyInfo {
	private String boothNumber;
	private String company;
	private String areaNumber;

	public ExpoCompanyInfo(String areaNumber, String boothNumber, String company) {
		this.areaNumber = areaNumber;
		this.boothNumber = boothNumber;
		this.company = company;
	}

	public String getAreaNumber() {
		return areaNumber;
	}

	public void setAreaNumber(String areaNumber) {
		this.areaNumber = areaNumber;
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
}
