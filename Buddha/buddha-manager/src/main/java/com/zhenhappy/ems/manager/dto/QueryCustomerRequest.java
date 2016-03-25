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
    private String mobilePhone;
    private String telephone;
    private String email;
    private String createTime;
    private Integer inlandOrForeign;  //1:表示国内客商  2：表示国外客商

    public Integer getInlandOrForeign() {
        return inlandOrForeign;
    }

    public void setInlandOrForeign(Integer inlandOrForeign) {
        this.inlandOrForeign = inlandOrForeign;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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
}
