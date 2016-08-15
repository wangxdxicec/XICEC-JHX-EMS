package com.zhenhappy.ems.manager.dto;

/**
 * Created by wangxd on 2016-05-24.
 */
public class QueryHistoryInfoRequest extends EasyuiRequest {
	private Integer id;
	private String booth_number;   //展位号
	private String company_zh;     //公司中文名
	private String company_en;     //公司英文名
	private String telphone;       //电话
	private String fax;            //传真
	private String email;          //邮箱
	private String website;        //网址
	private String address_zh;     //中文地址
	private String address_en;     //英文地址
	private String zipcode;        //邮编
	private String product_type;   //产品分类
	private String main_product_zh;//主营产品_中文
	private String main_product_en;//主营产品_英文
	private String company_profile;//公司简介
	private String invoice_head;   //发票抬头
	private String local_tax;      //地税编号
	private String joiner_name;    //参展人员名单
	private String joiner_telphone;//参展人员电话
	private String joiner_email;   //参展人员邮箱
	private String fair_year;      //展会时间
	private String field_back1;    //备用字段1
	private String field_back2;    //备用字段2
	private String field_back3;    //备用字段3
	private String field_back4;    //备用字段4

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBooth_number() {
		return booth_number;
	}

	public void setBooth_number(String booth_number) {
		this.booth_number = booth_number;
	}

	public String getCompany_zh() {
		return company_zh;
	}

	public void setCompany_zh(String company_zh) {
		this.company_zh = company_zh;
	}

	public String getCompany_en() {
		return company_en;
	}

	public void setCompany_en(String company_en) {
		this.company_en = company_en;
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

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getAddress_zh() {
		return address_zh;
	}

	public void setAddress_zh(String address_zh) {
		this.address_zh = address_zh;
	}

	public String getAddress_en() {
		return address_en;
	}

	public void setAddress_en(String address_en) {
		this.address_en = address_en;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getProduct_type() {
		return product_type;
	}

	public void setProduct_type(String product_type) {
		this.product_type = product_type;
	}

	public String getMain_product_zh() {
		return main_product_zh;
	}

	public void setMain_product_zh(String main_product_zh) {
		this.main_product_zh = main_product_zh;
	}

	public String getMain_product_en() {
		return main_product_en;
	}

	public void setMain_product_en(String main_product_en) {
		this.main_product_en = main_product_en;
	}

	public String getCompany_profile() {
		return company_profile;
	}

	public void setCompany_profile(String company_profile) {
		this.company_profile = company_profile;
	}

	public String getInvoice_head() {
		return invoice_head;
	}

	public void setInvoice_head(String invoice_head) {
		this.invoice_head = invoice_head;
	}

	public String getLocal_tax() {
		return local_tax;
	}

	public void setLocal_tax(String local_tax) {
		this.local_tax = local_tax;
	}

	public String getJoiner_name() {
		return joiner_name;
	}

	public void setJoiner_name(String joiner_name) {
		this.joiner_name = joiner_name;
	}

	public String getJoiner_telphone() {
		return joiner_telphone;
	}

	public void setJoiner_telphone(String joiner_telphone) {
		this.joiner_telphone = joiner_telphone;
	}

	public String getJoiner_email() {
		return joiner_email;
	}

	public void setJoiner_email(String joiner_email) {
		this.joiner_email = joiner_email;
	}

	public String getFair_year() {
		return fair_year;
	}

	public void setFair_year(String fair_year) {
		this.fair_year = fair_year;
	}

	public String getField_back1() {
		return field_back1;
	}

	public void setField_back1(String field_back1) {
		this.field_back1 = field_back1;
	}

	public String getField_back2() {
		return field_back2;
	}

	public void setField_back2(String field_back2) {
		this.field_back2 = field_back2;
	}

	public String getField_back3() {
		return field_back3;
	}

	public void setField_back3(String field_back3) {
		this.field_back3 = field_back3;
	}

	public String getField_back4() {
		return field_back4;
	}

	public void setField_back4(String field_back4) {
		this.field_back4 = field_back4;
	}
}
