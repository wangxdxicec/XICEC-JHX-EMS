package com.zhenhappy.ems.manager.dto;

/**
 * Created by wujianbin on 2014-08-14.
 */
public class QueryCustomerRequest extends EasyuiRequest {
    private String firstName;
    private String company;
    private Integer country;
    private String address;
    private String city;
    private String mobile;
    private String tel;
    private String email;
    private String createTime;
    private Integer inlandOrForeign;  //1:表示国内客商  2：表示国外客商
    private Integer isRabbicFlag;  //0：表示非法师；1：表示法师
    private Integer sourceValue;  //0：表示网站；1：表示手机
    private Integer pre;  //1：表示本次的数据；
    private Integer customerType;

    public Integer getInlandOrForeign() {
        return inlandOrForeign;
    }

    public void setInlandOrForeign(Integer inlandOrForeign) {
        this.inlandOrForeign = inlandOrForeign;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobilePhone) {
        this.mobile = mobilePhone;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String telephone) {
        this.tel = telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createdTime) {
        this.createTime = createdTime;
    }

    public Integer getIsRabbicFlag() {
        return isRabbicFlag;
    }

    public void setIsRabbicFlag(Integer isRabbicFlag) {
        this.isRabbicFlag = isRabbicFlag;
    }

    public Integer getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(Integer sourceValue) {
        this.sourceValue = sourceValue;
    }

    public Integer getPre() {
        return pre;
    }

    public void setPre(Integer pre) {
        this.pre = pre;
    }

    public Integer getCustomerType() {
        return customerType;
    }

    public void setCustomerType(Integer customerType) {
        this.customerType = customerType;
    }
}
