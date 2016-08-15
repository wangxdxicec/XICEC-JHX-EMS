package com.zhenhappy.ems.manager.dto;

import java.util.Date;

/**
 * Created by wujianbin on 2014-07-14.
 */
public class QueryVisitorInfo {
	private Integer id;
	private String email;
	private String checkingNo;
	private String password;
	private String firstName;
	private String lastName;
	private String sex;
	private String company;
	private String position;
	private Integer country;
	private String province;
	private String city;
	private String address;
	private String backupEmail;
	private String mobilePhoneCode;
	private String mobilePhone;
	private String telephoneCode;
	private String telephone;
	private String telephoneCode2;
	private String faxCode;
	private String fax;
	private String faxCode2;
	private String website;
	private String industry;
	private String industryOther;
	private String remark;
	private String createIp;
	private Date createTime;
	private String updateIp;
	private Date updateTime;
	private Integer sendEmailNum;
	private Date sendEmailTime;
	private Integer sendMsgNum;
	private Date sendMsgTime;
	private String langFlag;
	private String visitDate;
	private String beenToFair;
	private String beenToRole;
	private Boolean isRecieveEmail;
	private Boolean isRecieveDoc;
	private Boolean isMobile;
	private Boolean isjudged;
	private Integer isProfessional;
	private Boolean isAccommodation;
	private Boolean isDisabled;
	private Boolean isReaded;
	private String tmp_Country;
	private String tmp_Postcode;
	private String tmp_Interest;
	private String tmp_InterestOthers;
	private String tmp_Knowfrom;
	private String tmp_KnowfromOthers;
	private String tmp_V_name1;
	private String tmp_V_title1;
	private String tmp_V_position1;
	private String tmp_V_contact1;
	private String tmp_V_name2;
	private String tmp_V_title2;
	private String tmp_V_position2;
	private String tmp_V_contact2;
	private String tmp_V_name3;
	private String tmp_V_title3;
	private String tmp_V_position3;
	private String tmp_V_contact3;
	private String guid;
	private Integer govement;
	private String rabbi;
	private Integer sendEmailFlag;
	private Integer sendMsgFlag;
	private Integer customerType;

	/** default constructor */
	public QueryVisitorInfo() {
	}

	/** minimal constructor */
	public QueryVisitorInfo(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public QueryVisitorInfo(Integer id, String email, String checkingNo,
							String password, String firstName, String lastName, String sex,
							String company, String position, Integer country, String province,
							String city, String address, String backupEmail,
							String mobileCode, String mobile, String telCode,
							String tel, String telCode2, String faxCode,
							String fax, String faxCode2, String website, String remark,
							String createIp, Date createTime, String updateIp,
							Date updateTime, Integer sendEmailNum,Date sendEmailTime,
							Boolean isDisabled, String guid, Integer isGovement, String isRabbi) {
		this.id = id;
		this.email = email;
		this.checkingNo = checkingNo;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.sex = sex;
		this.company = company;
		this.position = position;
		this.country = country;
		this.province = province;
		this.city = city;
		this.address = address;
		this.backupEmail = backupEmail;
		this.mobilePhoneCode = mobileCode;
		this.mobilePhone = mobile;
		this.telephoneCode = telCode;
		this.telephone = tel;
		this.telephoneCode2 = telCode2;
		this.faxCode = faxCode;
		this.fax = fax;
		this.faxCode2 = faxCode2;
		this.website = website;
		this.remark = remark;
		this.createIp = createIp;
		this.createTime = createTime;
		this.updateIp = updateIp;
		this.updateTime = updateTime;
		this.sendEmailNum = sendEmailNum;
		this.sendEmailTime = sendEmailTime;
		this.isDisabled = isDisabled;
		this.guid = guid;
		this.govement = isGovement;
		this.rabbi = isRabbi;
	}

	public QueryVisitorInfo(Integer id, String firstName, String company, String city, String address,
							String mobile, String tel, String email, Date createTime, Integer isGovement, String isRabbi,
							boolean isMobile, Integer customertype) {
		this.id = id;
		this.firstName = firstName;
		this.company = company;
		this.city = city;
		this.address = address;
		this.mobilePhone = mobile;
		this.telephone = tel;
		this.email = email;
		this.createTime = createTime;
		this.govement = isGovement;
		this.rabbi = isRabbi;
		this.isMobile = isMobile;
		this.customerType = customertype;
	}

	public QueryVisitorInfo(Integer id, String firstName, String company, Integer country, String address,
							String mobile, String tel, String email, Date createTime, Integer isGovement, String isRabbi,
							boolean isMobile, Integer customertype) {
		this.id = id;
		this.firstName = firstName;
		this.company = company;
		this.country = country;
		this.address = address;
		this.mobilePhone = mobile;
		this.telephone = tel;
		this.email = email;
		this.createTime = createTime;
		this.govement = isGovement;
		this.rabbi = isRabbi;
		this.isMobile = isMobile;
		this.customerType = customertype;
	}

	public QueryVisitorInfo(Integer id, String email, String checkingNo,
						String password, String firstName, String lastName, String sex,
						String company, String position, Integer country, String province,
						String city, String address, String backupEmail,
						String mobilePhoneCode, String mobilePhone, String telephoneCode,
						String telephone, String telephoneCode2, String faxCode,
						String fax, String faxCode2, String website, String industry,String industryOther, String remark,
						String createIp, Date createTime, String updateIp,
						Date updateTime, Integer sendEmailNum,Date sendEmailTime, Integer sendMsgNum,Date sendMsgTime,
						String langFlag,String visitDate,String beenToFair,String beenToRole,Boolean isRecieveEmail,
						Boolean isRecieveDoc,Boolean isMobile,Boolean isjudged,Integer isProfessional,Boolean isAccommodation,
						Boolean isDisabled, Boolean isReaded,
						String tmp_Country,String tmp_Postcode,String tmp_Interest,String tmp_InterestOthers,
						String tmp_Knowfrom,String tmp_KnowfromOthers,String tmp_V_name1,String tmp_V_title1,
						String tmp_V_position1,String tmp_V_contact1,String tmp_V_name2,String tmp_V_title2,
						String tmp_V_position2,String tmp_V_contact2,String tmp_V_name3,String tmp_V_title3,
						String tmp_V_position3,String tmp_V_contact3,String guid, Integer isGovement, String isRabbi, Integer customertype) {
		this.id = id;
		this.email = email;
		this.checkingNo = checkingNo;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.sex = sex;
		this.company = company;
		this.position = position;
		this.country = country;
		this.province = province;
		this.city = city;
		this.address = address;
		this.backupEmail = backupEmail;
		this.mobilePhoneCode = mobilePhoneCode;
		this.mobilePhone = mobilePhone;
		this.telephoneCode = telephoneCode;
		this.telephone = telephone;
		this.telephoneCode2 = telephoneCode2;
		this.faxCode = faxCode;
		this.fax = fax;
		this.faxCode2 = faxCode2;
		this.website = website;
		this.industry = industry;
		this.industryOther = industryOther;
		this.remark = remark;
		this.createIp = createIp;
		this.createTime = createTime;
		this.updateIp = updateIp;
		this.updateTime = updateTime;
		this.sendEmailNum = sendEmailNum;
		this.sendEmailTime = sendEmailTime;
		this.sendMsgNum = sendMsgNum;
		this.sendMsgTime = sendMsgTime;
		this.langFlag = langFlag;
		this.visitDate = visitDate;
		this.beenToFair = beenToFair;
		this.beenToRole = beenToRole;
		this.isRecieveEmail = isRecieveEmail;
		this.isRecieveDoc = isRecieveDoc;
		this.isMobile = isMobile;
		this.isjudged = isjudged;
		this.isProfessional = isProfessional;
		this.isAccommodation = isAccommodation;
		this.isDisabled = isDisabled;
		this.isReaded = isReaded;
		this.tmp_Country = tmp_Country;
		this.tmp_Postcode = tmp_Postcode;
		this.tmp_Interest = tmp_Interest;
		this.tmp_InterestOthers = tmp_InterestOthers;
		this.tmp_Knowfrom = tmp_Knowfrom;
		this.tmp_KnowfromOthers = tmp_KnowfromOthers;
		this.tmp_V_name1 = tmp_V_name1;
		this.tmp_V_title1 = tmp_V_title1;
		this.tmp_V_position1 = tmp_V_position1;
		this.tmp_V_contact1 = tmp_V_contact1;
		this.tmp_V_name2 = tmp_V_name2;
		this.tmp_V_title2 = tmp_V_title2;
		this.tmp_V_position2 = tmp_V_position2;
		this.tmp_V_contact2 = tmp_V_contact2;
		this.tmp_V_name3 = tmp_V_name3;
		this.tmp_V_title3 = tmp_V_title3;
		this.tmp_V_position3 = tmp_V_position3;
		this.tmp_V_contact3 = tmp_V_contact3;
		this.guid = guid;
		this.govement = isGovement;
		this.rabbi = isRabbi;
		this.customerType = customertype;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCheckingNo() {
		return checkingNo;
	}

	public void setCheckingNo(String checkingNo) {
		this.checkingNo = checkingNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Integer getCountry() {
		return country;
	}

	public void setCountry(Integer country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBackupEmail() {
		return backupEmail;
	}

	public void setBackupEmail(String backupEmail) {
		this.backupEmail = backupEmail;
	}

	public String getMobilePhoneCode() {
		return mobilePhoneCode;
	}

	public void setMobilePhoneCode(String mobilePhoneCode) {
		this.mobilePhoneCode = mobilePhoneCode;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getTelephoneCode() {
		return telephoneCode;
	}

	public void setTelephoneCode(String telephoneCode) {
		this.telephoneCode = telephoneCode;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getTelephoneCode2() {
		return telephoneCode2;
	}

	public void setTelephoneCode2(String telephoneCode2) {
		this.telephoneCode2 = telephoneCode2;
	}

	public String getFaxCode() {
		return faxCode;
	}

	public void setFaxCode(String faxCode) {
		this.faxCode = faxCode;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getFaxCode2() {
		return faxCode2;
	}

	public void setFaxCode2(String faxCode2) {
		this.faxCode2 = faxCode2;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getIndustryOther() {
		return industryOther;
	}

	public void setIndustryOther(String industryOther) {
		this.industryOther = industryOther;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreateIp() {
		return createIp;
	}

	public void setCreateIp(String createIp) {
		this.createIp = createIp;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateIp() {
		return updateIp;
	}

	public void setUpdateIp(String updateIp) {
		this.updateIp = updateIp;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getSendEmailNum() {
		return sendEmailNum;
	}

	public void setSendEmailNum(Integer sendEmailNum) {
		this.sendEmailNum = sendEmailNum;
	}

	public Date getSendEmailTime() {
		return sendEmailTime;
	}

	public void setSendEmailTime(Date sendEmailDate) {
		this.sendEmailTime = sendEmailDate;
	}

	public Integer getSendMsgNum() {
		return sendMsgNum;
	}

	public void setSendMsgNum(Integer sendMsgNum) {
		this.sendMsgNum = sendMsgNum;
	}

	public Date getSendMsgDate() {
		return sendMsgTime;
	}

	public void setSendMsgDate(Date sendMsgDate) {
		this.sendMsgTime = sendMsgDate;
	}

	public String getLangFlag() {
		return langFlag;
	}

	public void setLangFlag(String langFlag) {
		this.langFlag = langFlag;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public String getBeenToFair() {
		return beenToFair;
	}

	public void setBeenToFair(String beenToFair) {
		this.beenToFair = beenToFair;
	}

	public String getBeenToRole() {
		return beenToRole;
	}

	public void setBeenToRole(String beenToRole) {
		this.beenToRole = beenToRole;
	}

	public Boolean getRecieveEmail() {
		return isRecieveEmail;
	}

	public void setRecieveEmail(Boolean recieveEmail) {
		isRecieveEmail = recieveEmail;
	}

	public Boolean getRecieveDoc() {
		return isRecieveDoc;
	}

	public void setRecieveDoc(Boolean recieveDoc) {
		isRecieveDoc = recieveDoc;
	}

	public Boolean getMobile() {
		return isMobile;
	}

	public void setMobile(Boolean mobile) {
		isMobile = mobile;
	}

	public Boolean getIsjudged() {
		return isjudged;
	}

	public void setIsjudged(Boolean isjudged) {
		this.isjudged = isjudged;
	}

	public Integer getProfessional() {
		return isProfessional;
	}

	public void setProfessional(Integer professional) {
		isProfessional = professional;
	}

	public Boolean getAccommodation() {
		return isAccommodation;
	}

	public void setAccommodation(Boolean accommodation) {
		isAccommodation = accommodation;
	}

	public Boolean getDisabled() {
		return isDisabled;
	}

	public void setDisabled(Boolean disabled) {
		isDisabled = disabled;
	}

	public Boolean getReaded() {
		return isReaded;
	}

	public void setReaded(Boolean readed) {
		isReaded = readed;
	}

	public String getTmp_Country() {
		return tmp_Country;
	}

	public void setTmp_Country(String tmp_Country) {
		this.tmp_Country = tmp_Country;
	}

	public String getTmp_Postcode() {
		return tmp_Postcode;
	}

	public void setTmp_Postcode(String tmp_Postcode) {
		this.tmp_Postcode = tmp_Postcode;
	}

	public String getTmp_Interest() {
		return tmp_Interest;
	}

	public void setTmp_Interest(String tmp_Interest) {
		this.tmp_Interest = tmp_Interest;
	}

	public String getTmp_InterestOthers() {
		return tmp_InterestOthers;
	}

	public void setTmp_InterestOthers(String tmp_InterestOthers) {
		this.tmp_InterestOthers = tmp_InterestOthers;
	}

	public String getTmp_Knowfrom() {
		return tmp_Knowfrom;
	}

	public void setTmp_Knowfrom(String tmp_Knowfrom) {
		this.tmp_Knowfrom = tmp_Knowfrom;
	}

	public String getTmp_KnowfromOthers() {
		return tmp_KnowfromOthers;
	}

	public void setTmp_KnowfromOthers(String tmp_KnowfromOthers) {
		this.tmp_KnowfromOthers = tmp_KnowfromOthers;
	}

	public String getTmp_V_name1() {
		return tmp_V_name1;
	}

	public void setTmp_V_name1(String tmp_V_name1) {
		this.tmp_V_name1 = tmp_V_name1;
	}

	public String getTmp_V_title1() {
		return tmp_V_title1;
	}

	public void setTmp_V_title1(String tmp_V_title1) {
		this.tmp_V_title1 = tmp_V_title1;
	}

	public String getTmp_V_position1() {
		return tmp_V_position1;
	}

	public void setTmp_V_position1(String tmp_V_position1) {
		this.tmp_V_position1 = tmp_V_position1;
	}

	public String getTmp_V_contact1() {
		return tmp_V_contact1;
	}

	public void setTmp_V_contact1(String tmp_V_contact1) {
		this.tmp_V_contact1 = tmp_V_contact1;
	}

	public String getTmp_V_name2() {
		return tmp_V_name2;
	}

	public void setTmp_V_name2(String tmp_V_name2) {
		this.tmp_V_name2 = tmp_V_name2;
	}

	public String getTmp_V_title2() {
		return tmp_V_title2;
	}

	public void setTmp_V_title2(String tmp_V_title2) {
		this.tmp_V_title2 = tmp_V_title2;
	}

	public String getTmp_V_position2() {
		return tmp_V_position2;
	}

	public void setTmp_V_position2(String tmp_V_position2) {
		this.tmp_V_position2 = tmp_V_position2;
	}

	public String getTmp_V_contact2() {
		return tmp_V_contact2;
	}

	public void setTmp_V_contact2(String tmp_V_contact2) {
		this.tmp_V_contact2 = tmp_V_contact2;
	}

	public String getTmp_V_name3() {
		return tmp_V_name3;
	}

	public void setTmp_V_name3(String tmp_V_name3) {
		this.tmp_V_name3 = tmp_V_name3;
	}

	public String getTmp_V_title3() {
		return tmp_V_title3;
	}

	public void setTmp_V_title3(String tmp_V_title3) {
		this.tmp_V_title3 = tmp_V_title3;
	}

	public String getTmp_V_position3() {
		return tmp_V_position3;
	}

	public void setTmp_V_position3(String tmp_V_position3) {
		this.tmp_V_position3 = tmp_V_position3;
	}

	public String getTmp_V_contact3() {
		return tmp_V_contact3;
	}

	public void setTmp_V_contact3(String tmp_V_contact3) {
		this.tmp_V_contact3 = tmp_V_contact3;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Integer getGovement() {
		return this.govement;
	}

	public void setGovement(Integer govement) {
		this.govement = govement;
	}

	public String getRabbi() {
		return rabbi;
	}

	public void setRabbi(String rabbi) {
		rabbi = rabbi;
	}

	public Integer getSendEmailFlag() {
		return sendEmailFlag;
	}

	public void setSendEmailFlag(Integer sendEmailFlag) {
		this.sendEmailFlag = sendEmailFlag;
	}

	public Integer getSendMsgFlag() {
		return sendMsgFlag;
	}

	public void setSendMsgFlag(Integer sendMsgFlag) {
		this.sendMsgFlag = sendMsgFlag;
	}

	public Integer getCustomerType() {
		return customerType;
	}

	public void setCustomerType(Integer customerType) {
		this.customerType = customerType;
	}
}
