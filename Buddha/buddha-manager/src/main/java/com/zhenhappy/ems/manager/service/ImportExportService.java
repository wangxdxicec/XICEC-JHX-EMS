package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.dao.ContactDao;
import com.zhenhappy.ems.dao.ExhibitorDao;
import com.zhenhappy.ems.dao.ExhibitorInfoDao;
import com.zhenhappy.ems.entity.*;
import com.zhenhappy.ems.entity.managerrole.TUserInfo;
import com.zhenhappy.ems.manager.dto.*;
import com.zhenhappy.ems.manager.entity.TExhibitorBooth;
import com.zhenhappy.ems.manager.entity.THistoryVisitorInfo;
import com.zhenhappy.ems.manager.tag.StringUtil;
import com.zhenhappy.ems.service.CountryProvinceService;
import com.zhenhappy.ems.service.ExhibitorService;

import com.zhenhappy.ems.service.InvoiceService;
import com.zhenhappy.util.Page;
import net.sf.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wujianbin on 2014-08-25.
 */
@Service
public class ImportExportService extends ExhibitorService {

	private static Logger log = Logger.getLogger(ImportExportService.class);

	@Autowired
	private ExhibitorManagerService exhibitorManagerService;
	@Autowired
	private ExhibitorInfoDao exhibitorInfoDao;
	@Autowired
	private ContactManagerService contactService;
	@Autowired
	private InvoiceService invoiceService;
	@Autowired
	private CountryProvinceService countryProvinceService;
	@Autowired
	private ContactDao contactDao;
	@Autowired
	private ExhibitorDao exhibitorDao;

	List<ImportVegetarianExhibitorInfo> isExistCustomerList = new ArrayList<ImportVegetarianExhibitorInfo>();
	List<ImportVegetarianExhibitorInfo> willImportCustomerList = new ArrayList<ImportVegetarianExhibitorInfo>();

	public List<QueryExhibitorInfo> exportExhibitor(List<TExhibitor> exhibitors) {
		List<QueryExhibitorInfo> queryExhibitorInfos = new ArrayList<QueryExhibitorInfo>();
		if(exhibitors != null){
			for(TExhibitor exhibitor:exhibitors){
				TExhibitorInfo exhibitorInfo = exhibitorManagerService.loadExhibitorInfoByEid(exhibitor.getEid());
				if(exhibitorInfo != null){
					QueryExhibitorInfo queryExhibitorInfo = new QueryExhibitorInfo();
					queryExhibitorInfo.setUsername(exhibitor.getUsername());
					queryExhibitorInfo.setPassword(exhibitor.getPassword());
					queryExhibitorInfo.setBoothNumber(exhibitorManagerService.loadBoothNum(exhibitor.getEid()));
					queryExhibitorInfo.setCompany(exhibitorInfo.getCompany());
					queryExhibitorInfo.setCompanyEn(exhibitorInfo.getCompanyEn());
					queryExhibitorInfo.setPhone(exhibitorInfo.getPhone());
					queryExhibitorInfo.setFax(exhibitorInfo.getFax());
					queryExhibitorInfo.setEmail(exhibitorInfo.getEmail());
					queryExhibitorInfo.setWebsite(exhibitorInfo.getWebsite());
					queryExhibitorInfo.setAddress(exhibitorInfo.getAddress());
					queryExhibitorInfo.setAddressEn(exhibitorInfo.getAddressEn());
					queryExhibitorInfo.setZipcode(exhibitorInfo.getZipcode());
					queryExhibitorInfo.setProductType(exhibitorManagerService.queryExhibitorClassByEinfoid(exhibitorInfo.getEinfoid()));
					queryExhibitorInfo.setMainProduct(exhibitorInfo.getMainProduct());
					queryExhibitorInfo.setMainProductEn(exhibitorInfo.getMainProductEn());
					queryExhibitorInfo.setMark(exhibitorInfo.getMark());
					TInvoiceApply invoice = invoiceService.getByEid(exhibitorInfo.getEid());
					if(invoice != null){
						if(StringUtils.isNotEmpty(invoice.getInvoiceNo())) {
							queryExhibitorInfo.setInvoiceNo(invoice.getInvoiceNo());
						}else{
							queryExhibitorInfo.setInvoiceNo("");
						}
						if(StringUtils.isNotEmpty(invoice.getTitle())){
							queryExhibitorInfo.setInvoiceTitle(invoice.getTitle());
						}else{
							queryExhibitorInfo.setInvoiceTitle("");
						}
					}else{
						queryExhibitorInfo.setInvoiceNo("");
						queryExhibitorInfo.setInvoiceTitle("");
					}
					queryExhibitorInfos.add(queryExhibitorInfo);
				}else{
					QueryExhibitorInfo queryExhibitorInfo = new QueryExhibitorInfo();
					queryExhibitorInfo.setBoothNumber(exhibitorManagerService.loadBoothNum(exhibitor.getEid()));
					queryExhibitorInfos.add(queryExhibitorInfo);
				}
			}
		}
		return queryExhibitorInfos;
	}

	public List<String> importExhibitor(File importFile, ImportExhibitorsRequest request) {
		Integer count = 0;
		List<String> report = new ArrayList<String>();
		try {
			Workbook book = Workbook.getWorkbook(importFile);
			// 获得第一个工作表对象
			Sheet sheet = book.getSheet(0);
			// 得到单元格
			for (int j = 1; j < sheet.getRows(); j++) {
				// 展位号
				TExhibitorBooth booth = new TExhibitorBooth();
				Cell boothTmp = sheet.getCell(0, j);
				String boothNo = boothTmp.getContents().trim().replaceAll(" ", "");
				if(StringUtils.isEmpty(boothNo)) {
//					System.out.println("第" + (j+1) + "行有问题,原因:展位号为空");
					report.add("第" + (j+1) + "行有问题,原因:展位号为空");
					continue;
				}
				if(exhibitorManagerService.queryBoothByBoothNum(boothNo) != null) {
//					System.out.println("第" + (j+1) + "行有问题,原因:展位号=" + boothNo + "有重复");
					report.add("第" + (j+1) + "行有问题,原因:展位号=" + boothNo + "有重复");
					continue;
				}
				booth.setBoothNumber(boothNo);
				booth.setExhibitionArea(boothNo.substring(0,1) + "厅");

				TExhibitor exhibitor = new TExhibitor();
				TExhibitorInfo exhibitorInfo = new TExhibitorInfo();
				List<TContact> contacts = new ArrayList<TContact>();
				String company = null;
				String companye = null;
				for (int i = 1; i < 16; i++) {
					Cell cell = sheet.getCell(i, j);
					switch (i) {
						case 1:	//用户名
							exhibitor.setUsername(cell.getContents().trim().replaceAll(" ", ""));
							break;
						case 2:	//密码
							exhibitor.setPassword(cell.getContents().trim().replaceAll(" ", ""));
							break;
						case 3:	//组织机构代码
							String organizationCode = cell.getContents().trim().replaceAll(" ", "");
							if(organizationCode == null || "".equals(organizationCode)){
								exhibitorInfo.setOrganizationCode(organizationCode);
							}else{
								if(organizationCode.length() == 10){
									exhibitorInfo.setOrganizationCode(organizationCode);
								}else{
									exhibitorInfo.setOrganizationCode(organizationCode);
									report.add("第" + (j+1) + "行有问题,原因:组织机构代码=" + organizationCode + "的长度不是10,但不影响此展商账号添加,请手动修改");
									break;
								}
							}
							break;
						case 4:	//公司名称(中文)
							company = cell.getContents().trim();
							break;
						case 5:	//公司名称(英文)
							companye = cell.getContents().trim();
							break;
						case 6:	//电话
							exhibitorInfo.setPhone(cell.getContents().trim());
							break;
						case 7:	//传真
							exhibitorInfo.setFax(cell.getContents().trim());
							break;
						case 8:	//网址
							exhibitorInfo.setWebsite(cell.getContents().trim().replaceAll(" ", ""));
							break;
						case 9:	//公司地址(中文)
							exhibitorInfo.setAddress(cell.getContents().trim());
							break;
						case 10://公司地址(英文)
							exhibitorInfo.setAddressEn(cell.getContents().trim());
							break;
						case 11://联系人姓名
							String[] names = cell.getContents().trim().split("\n");
							for(String name:names){
								TContact contact = new TContact();
								contact.setName(name);
								contacts.add(contact);
							}
							break;
						case 12://联系人职务
							String[] position = cell.getContents().trim().split("\n");
							if(contacts.size() != position.length){
								report.add("第" + (j+1) + "行有问题,原因:联系人职务不能联系人姓名一一对应,但不影响此展商账号添加,多出的联系人职务将丢失");
								break;
							}
							if(contacts.size() > 0){
								for(int t = 0;t < contacts.size(); t ++){
									contacts.get(t).setPosition(position[t]);
								}
							}
							break;
						case 13://手机
							String[] phone = cell.getContents().trim().split("\n");
							if(contacts.size() != phone.length){
								report.add("第" + (j+1) + "行有问题,原因:联系人手机号不能联系人姓名一一对应,但不影响此展商账号添加,多出的联系人手机号将丢失");
								break;
							}
							if(contacts.size() > 0){
								for(int t = 0;t < contacts.size(); t ++){
									contacts.get(t).setPhone(phone[t]);
								}
							}
							break;
						case 14://邮箱
							String[] email = cell.getContents().trim().replaceAll(" ", "").split("\n");
							if(contacts.size() != email.length){
								report.add("第" + (j+1) + "行有问题,原因:联系人邮箱不能联系人姓名一一对应,但不影响此展商账号添加,多出的联系人邮箱将丢失");
								break;
							}
							if(contacts.size() > 0){
								for(int t = 0;t < contacts.size(); t ++){
									contacts.get(t).setEmail(email[t]);
								}
							}
							break;
						case 15:
							exhibitor.setExhibitionArea(cell.getContents().trim().replaceAll(" ", ""));
						default:
							break;
					}
				}
				if(StringUtils.isEmpty(company) && StringUtils.isEmpty(companye)){
//					System.out.println("第" + (j+1) + "行有问题,原因:公司中文名和英文名都为空");
					report.add("第" + (j+1) + "行有问题,原因:公司中文名和英文名都为空");
					continue;//公司中文名和英文名都为空
				}else if((exhibitorManagerService.loadAllExhibitorByCompany(company) != null) || (exhibitorManagerService.loadAllExhibitorByCompanye(companye) != null)){
//					System.out.println("第" + (j+1) + "行有问题,原因:公司中文名"+ company +"或英文名"+ companye +"存在重复");
					report.add("第" + (j+1) + "行有问题,原因:公司中文名"+ company +"或英文名"+ companye +"存在重复");
					continue;//公司中文名或英文名存在重复
				}
				//exhibitor.setCompany(company);
				exhibitorInfo.setCompany(company);
				//exhibitor.setCompanye(companye);
				exhibitorInfo.setCompanyEn(companye);
				//exhibitor.setCompanyt(JChineseConvertor.getInstance().s2t(company.trim()));
				exhibitorInfo.setCompanyT(JChineseConvertor.getInstance().s2t(company.trim()));
				if(request.getCountry() != null) exhibitor.setCountry(request.getCountry());
				if(request.getProvince() != null) exhibitor.setProvince(request.getProvince());
				if(request.getArea() != null) exhibitor.setArea(request.getArea());
				if(request.getGroup() != null) exhibitor.setGroup(request.getGroup());
				if(request.getTag() != null) exhibitor.setTag(request.getTag());
				exhibitor.setCreateTime(new Date());
				exhibitor.setCreateUser(1);
				exhibitor.setIsLogout(0);
				exhibitor.setContractId("");
				Integer eid = (Integer) getHibernateTemplate().save(exhibitor);

				if(eid != null){
					exhibitor.setEid(eid);
					booth.setEid(eid);
					booth.setMark("");
					booth.setCreateTime(new Date());
					booth.setCreateUser(1);
					exhibitorManagerService.bindBoothInfo(booth);

					exhibitorInfo.setEid(eid);
					exhibitorInfo.setPhone(contacts.get(0).getPhone());
					exhibitorInfo.setEmail(contacts.get(0).getEmail());
					exhibitorInfo.setCreateTime(new Date());
					exhibitorInfo.setAdminUser(1);
					exhibitorInfoDao.create(exhibitorInfo);

					for(TContact contact:contacts){
						contact.setEid(eid);
						contact.setIsDelete(0);
						contactService.addContact(contact);
					}
				}
				count ++;
			}
			report.add("共导入:" + count + "条数据");
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return report;
	}

	public void copyLogo(Integer[] eids, String destDir) throws IOException {
		List<TExhibitor> exhibitors = new ArrayList<TExhibitor>();
		if(eids == null){
			exhibitors = exhibitorManagerService.loadAllExhibitors();
		}else{
			exhibitors = exhibitorManagerService.loadSelectedExhibitors(eids);
		}
		for(TExhibitor exhibitor:exhibitors){
			TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(exhibitor.getEid());
			if(exhibitorInfo != null){
				if(StringUtils.isNotEmpty(exhibitorInfo.getLogo())){
					String boothNumber = loadBoothNum(exhibitor.getEid());
					File srcFile = new File(exhibitorInfo.getLogo().replaceAll("\\\\\\\\", "\\\\").replaceAll("/", "\\\\"));
					if (srcFile.exists() == false) continue;
					File destFile = null;
					if(StringUtils.isNotEmpty(exhibitorInfo.getCompany())){
						destFile = new File(destDir + "\\" + exhibitorInfo.getCompany().replaceAll("/", "") + boothNumber.replaceAll("/", "") + "." + FilenameUtils.getExtension(exhibitorInfo.getLogo().replaceAll("/", "\\\\\\\\")));
					}else{
						destFile = new File(destDir + "\\" + exhibitorInfo.getCompanyEn().replaceAll("/", "") + boothNumber.replaceAll("/", "") + "." + FilenameUtils.getExtension(exhibitorInfo.getLogo().replaceAll("/", "\\\\\\\\")));
					}
					if(destFile != null) FileUtils.copyFile(srcFile, destFile);
				}
			}
		}
	}

	public void WriteStringToFile(String str, String filePath) {
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			fos.write(str.getBytes());
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//==================佛事展新增需求=================
	/**
	 * 导出客商数据
	 * @param customers
	 * @return
	 */
	public List<ExportCustomerInfo> exportCustomer(List<TVisitorInfo> customers) {
		List<ExportCustomerInfo> exportCustomerInfos = new ArrayList<ExportCustomerInfo>();
		if(customers.size() > 0){
			for(TVisitorInfo customer:customers){
				ExportCustomerInfo exportCustomerInfo = new ExportCustomerInfo();
				exportCustomerInfo.setName((customer.getFirstName() == null ? "":customer.getFirstName()) + " " + (customer.getLastName() == null ? "":customer.getLastName()));
				exportCustomerInfo.setPhone((customer.getMobileCode() == null ? "":customer.getMobileCode()) + (customer.getMobilePhone() == null ? "":customer.getMobilePhone()));
				if(StringUtils.isNotEmpty(customer.getTel())){
					if(StringUtils.isNotEmpty(customer.getTelCode2())){
						exportCustomerInfo.setTel((customer.getTelCode() == null ? "":customer.getTelCode()) + (customer.getTel() == null ? "":customer.getTel()) + "," + (customer.getTelCode2() == null ? "":customer.getTelCode2()));
					}else{
						exportCustomerInfo.setTel((customer.getTelCode() == null ? "":customer.getTelCode()) + (customer.getTel() == null ? "":customer.getTel()));
					}
				}else{
					exportCustomerInfo.setTel("");
				}
				if(StringUtils.isNotEmpty(customer.getFax())){
					if(StringUtils.isNotEmpty(customer.getFaxCode2())){
						exportCustomerInfo.setFaxString((customer.getFaxCode() == null ? "":customer.getFaxCode()) + (customer.getFax() == null ? "":customer.getFax()) + "," + (customer.getFaxCode2() == null ? "":customer.getFaxCode2()));
					}else{
						exportCustomerInfo.setFaxString((customer.getFaxCode() == null ? "":customer.getFaxCode()) + (customer.getFax() == null ? "":customer.getFax()));
					}
				}else{
					exportCustomerInfo.setFaxString("");
				}
				if(customer.getCountry() != null){
					WCountry country = countryProvinceService.loadCountryById(customer.getCountry());
					exportCustomerInfo.setCountryString(country.getChineseName());
				}else{
					exportCustomerInfo.setCountryString("");
				}
				StringBuffer accompanyName = new StringBuffer();
				StringBuffer accompanyContact = new StringBuffer();
				if(StringUtils.isNotEmpty(customer.getTmp_V_name1()) && !"null".equals(customer.getTmp_V_name1())) {
					accompanyName.append("人员1：" + customer.getTmp_V_name1());
				} else {
					accompanyName.append("人员1：无");
				}
				if(StringUtils.isNotEmpty(customer.getTmp_V_name2()) && !"null".equals(customer.getTmp_V_name2())) {
					accompanyName.append(", 人员2：" + customer.getTmp_V_name2());
				} else {
					accompanyName.append(", 人员2：无");
				}
				if(StringUtils.isNotEmpty(customer.getTmp_V_name3()) && !"null".equals(customer.getTmp_V_name3())) {
					accompanyName.append(", 人员3：" + customer.getTmp_V_name3());
				} else {
					accompanyName.append(", 人员3：无");
				}
				if(StringUtils.isNotEmpty(customer.getTmp_V_contact1()) && !"null".equals(customer.getTmp_V_contact1())) {
					accompanyContact.append("人员联系1：" + customer.getTmp_V_contact1());
				} else {
					accompanyContact.append("人员联系1：无");
				}
				if(StringUtils.isNotEmpty(customer.getTmp_V_contact2()) && !"null".equals(customer.getTmp_V_contact2())) {
					accompanyContact.append(", 人员联系2：" + customer.getTmp_V_contact2());
				} else {
					accompanyContact.append(", 人员联系2：无");
				}
				if(StringUtils.isNotEmpty(customer.getTmp_V_contact3()) && !"null".equals(customer.getTmp_V_contact3())) {
					accompanyContact.append(", 人员联系3：" + customer.getTmp_V_contact3());
				} else {
					accompanyContact.append(", 人员联系3：无");
				}
				exportCustomerInfo.setId(customer.getId());
				exportCustomerInfo.setTmp_Interest(customer.getTmp_Interest());
				exportCustomerInfo.setCheckingNo(customer.getCheckingNo());
				exportCustomerInfo.setAccompanyName(accompanyName.toString());
				exportCustomerInfo.setAccompanyContact(accompanyContact.toString());
				exportCustomerInfo.setCreateTime(customer.getCreateTime());
				BeanUtils.copyProperties(customer, exportCustomerInfo);
				exportCustomerInfos.add(exportCustomerInfo);
			}
		}
		return exportCustomerInfos;
	}

	/**
	 * 导出参展人员列表数据
	 * @param customers
	 * @return
	 */
	public List<ExportExhibitorjoinerInfo> exportExhibitorJoiner(List<TExhibitorJoiner> customers) {
		List<ExportExhibitorjoinerInfo> exportCustomerInfos = new ArrayList<ExportExhibitorjoinerInfo>();
		if(customers.size() > 0){
			for(TExhibitorJoiner customer:customers){
				ExportExhibitorjoinerInfo exportCustomerInfo = new ExportExhibitorjoinerInfo();
				exportCustomerInfo.setName(customer.getName());
				if(StringUtils.isNotEmpty(customer.getTelphone())){
					exportCustomerInfo.setTelphone(customer.getTelphone());
				}else{
					exportCustomerInfo.setTelphone("");
				}
				if(StringUtils.isNotEmpty(customer.getPosition())){
					exportCustomerInfo.setPosition(customer.getPosition());
				}else{
					exportCustomerInfo.setPosition("");
				}
				if(StringUtils.isNotEmpty(customer.getEmail())){
					exportCustomerInfo.setEmail(customer.getEmail());
				}else{
					exportCustomerInfo.setEmail("");
				}
				BeanUtils.copyProperties(customer, exportCustomerInfo);
				exportCustomerInfos.add(exportCustomerInfo);
			}
		}
		return exportCustomerInfos;
	}

	public ImportVegetarianOrThanilandExhibitorResponse importVegetarianExhibitor(File importFile, TUserInfo userInfo, Integer type) {
		Integer count = 0;
		Integer willImportId = 1;
		ImportVegetarianOrThanilandExhibitorResponse report = new ImportVegetarianOrThanilandExhibitorResponse();
		isExistCustomerList = new ArrayList<ImportVegetarianExhibitorInfo>();
		willImportCustomerList = new ArrayList<ImportVegetarianExhibitorInfo>();
		List resultArray = new ArrayList();
		boolean isExistCompanyNameOrAddress = false;
		boolean isExistCompanyNameOrAddressOrEmail = false;
		try {
			InputStream is = new FileInputStream(importFile);
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
			// 循环工作表Sheet
			/*for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
			}*/
			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
			List<ImportVegetarianExhibitorInfo> tHistoryCustomerListTemp = new ArrayList<ImportVegetarianExhibitorInfo>();
			if (xssfSheet != null) {
				//总行数
				int value = xssfSheet.getLastRowNum();
				for (int beginIndex = 1;beginIndex <= value; beginIndex++) {
					isExistCompanyNameOrAddress = false;
					isExistCompanyNameOrAddressOrEmail = false;

					ImportVegetarianExhibitorInfo vegetarianExhibitorInfo = new ImportVegetarianExhibitorInfo();
					vegetarianExhibitorInfo.setId(beginIndex);

					Row xssfRow = xssfSheet.getRow(beginIndex);
					if (xssfRow != null && !(xssfRow.equals(""))) {
						//地址  只有一个
						org.apache.poi.ss.usermodel.Cell addressShell = xssfRow.getCell(0);
						//公司
						org.apache.poi.ss.usermodel.Cell companyCell = xssfRow.getCell(1);
						String companyStr = "";
						String addressStr = "";
						if(companyCell != null){
							companyStr = companyCell.getStringCellValue().trim();
						}
						if(addressShell != null){
							addressStr = addressShell.getStringCellValue().trim();
						}

						org.apache.poi.ss.usermodel.Cell emailShell = xssfRow.getCell(6);
						//邮箱
						if(emailShell != null){
							String emailValue = getCellValue(emailShell).trim();
							if(StringUtil.isNotEmpty(emailValue)){
								String emailContent = emailValue.replaceAll(" ", "");
								if(StringUtil.isNotEmpty(emailContent)){
									String[] emailList = emailContent.split("\n");
									//如果isExistCompanyName为false,则说明公司名是没有重复，那么变判断地址是否有重复
									int length = emailList.length;
									if(!isExistCompanyNameOrAddress && length>0) {
										for(int emailIndex=0;emailIndex<length;emailIndex++){
											String emailTemp = emailList[emailIndex];
											tHistoryCustomerListTemp = loadHistoryCustomerByCompanyNameAndAddressAndEmail(companyStr, addressStr, emailTemp, type);
											if(tHistoryCustomerListTemp != null && tHistoryCustomerListTemp.size()>0){
												isExistCompanyNameOrAddressOrEmail = true;
												break;
											}
										}
									}
								}else {
									tHistoryCustomerListTemp = loadHistoryCustomerByCompanyNameAndAddress(companyStr, addressStr, type);
									if(tHistoryCustomerListTemp != null && tHistoryCustomerListTemp.size()>0){
										isExistCompanyNameOrAddress = true;
									}
								}
							}else {
								tHistoryCustomerListTemp = loadHistoryCustomerByCompanyNameAndAddress(companyStr, addressStr, type);
								if(tHistoryCustomerListTemp != null && tHistoryCustomerListTemp.size()>0){
									isExistCompanyNameOrAddress = true;
								}
							}
						}else{
							tHistoryCustomerListTemp = loadHistoryCustomerByCompanyNameAndAddress(companyStr, addressStr, type);
							if(tHistoryCustomerListTemp != null && tHistoryCustomerListTemp.size()>0){
								isExistCompanyNameOrAddress = true;
							}
						}

						TExhibitor exhibitor = new TExhibitor();
						TExhibitorInfo exhibitorInfo = new TExhibitorInfo();
						List<TContact> contacts = new ArrayList<TContact>();
						exhibitor.setCountry(44);
						exhibitor.setExhibitionArea("0");
						exhibitor.setExhibitor_type("1");
						if(userInfo != null){
							exhibitor.setCreateUser(userInfo.getId());
							exhibitor.setUpdateUser(userInfo.getId());
							exhibitor.setTag(userInfo.getOwnerId());
						}
						exhibitor.setCreateTime(new Date());
						exhibitor.setUpdateTime(new Date());
						exhibitorInfo.setIsDelete(0);

						//获取列数
						int size = xssfRow.getLastCellNum();
						exhibitorInfo.setAddress("");
						exhibitorInfo.setCompany("");
						for (int i = 0; i < size; i++) {
							org.apache.poi.ss.usermodel.Cell cell = xssfRow.getCell(i);
							if(cell != null){
								String cellValue = getCellValue(cell).trim();
								if(StringUtil.isNotEmpty(cellValue)){
									cellValue = cellValue.replaceAll(" ", "");
								}else{
									cellValue = "";
								}
								switch (i) {
									case 0:	//地址
										exhibitorInfo.setAddress(cellValue);
										vegetarianExhibitorInfo.setAddress(cellValue);
										break;
									case 1:	//公司名
										exhibitorInfo.setCompany(cellValue);
										vegetarianExhibitorInfo.setCompany(cellValue);
										break;
									case 2:	//联系人
										String[] names = cellValue.split("&");
										for(String name:names){
											TContact contact = new TContact();
											contact.setName(name);
											contacts.add(contact);
										}
										vegetarianExhibitorInfo.setContact(cellValue);
										break;
									case 3:	//手机
										String[] phone = cellValue.split("/");
										if(contacts.size() > 0){
											for(int k=0;k<phone.length;k++){
												contacts.get(k).setPhone(phone[k]);
											}
										}
										vegetarianExhibitorInfo.setMobilephone(cellValue);
										break;
									case 4:	//电话
										exhibitorInfo.setPhone(cellValue);
										vegetarianExhibitorInfo.setTelphone(cellValue);
										break;
									case 5:	//传真
										exhibitorInfo.setFax(cellValue);
										vegetarianExhibitorInfo.setFax(cellValue);
										break;
									case 6:	//邮箱
										String[] email = cellValue.trim().replaceAll(" ", "").split("\n");
										if(contacts.size() > 0){
											for(int k=0;k<email.length;k++){
												contacts.get(k).setEmail(email[k]);
											}
										}
										vegetarianExhibitorInfo.setEmail(cellValue);
										break;
									case 7:	//展品
										exhibitorInfo.setMainProduct(cellValue);
										vegetarianExhibitorInfo.setProduct(cellValue);
										break;
									default:
										break;
								}
							}
						}

						if(isExistCompanyNameOrAddress || isExistCompanyNameOrAddressOrEmail) {
							isExistCustomerList.addAll(tHistoryCustomerListTemp);
							willImportCustomerList.add(vegetarianExhibitorInfo);
						}else{
							Integer eid = (Integer) getHibernateTemplate().save(exhibitor);
							if(eid != null){
								exhibitor.setEid(eid);

								exhibitorInfo.setEid(eid);
								exhibitorInfo.setCreateTime(new Date());
								exhibitorInfo.setAdminUser(1);
								exhibitorInfoDao.create(exhibitorInfo);

								for(TContact contact:contacts){
									contact.setEid(eid);
									contact.setIsDelete(0);
									contactService.addContact(contact);
								}
							}
							count ++;
						}
					}
				}
			}

			if(willImportCustomerList != null && willImportCustomerList.size()>0){
				StringBuffer resultBuffer = new StringBuffer();
				resultBuffer.append("共导入：" + count + "条数据，其中有" + willImportCustomerList.size() + "条记录，可能存在重复！");
				resultArray.add(resultBuffer.toString());
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, Boolean.TRUE);
				JSONArray resultJson = JSONArray.fromObject(resultArray);
				report.setResult(resultJson.toString());
				JSONArray isExistDataJsonArray = JSONArray.fromObject(isExistCustomerList);
				JSONArray willImportDataJsonArray = JSONArray.fromObject(willImportCustomerList);
				report.setIsExistData(isExistDataJsonArray.toString());
				report.setWillImportData(willImportDataJsonArray.toString());
				report.setResultCode(1);
			}else{
				StringBuffer resultBuffer = new StringBuffer();
				resultBuffer.append("共导入：" + count + "条数据");
				resultArray.add(resultBuffer.toString());
				JSONArray resultJson = JSONArray.fromObject(resultArray);
				report.setResult(resultJson.toString());
				report.setIsExistData("");
				report.setWillImportData("");
				report.setResultCode(0);
			}
			xssfWorkbook.close();
		} catch (Exception e) {
			System.out.println(e);
			report.setResultCode(-1);
			e.printStackTrace();
		}
		return report;
	}

	public ImportVegetarianOrThanilandExhibitorResponse importThailandExhibitor(File importFile, TUserInfo userInfo, Integer type) {
		Integer count = 0;
		Integer willImportId = 1;
		ImportVegetarianOrThanilandExhibitorResponse report = new ImportVegetarianOrThanilandExhibitorResponse();
		isExistCustomerList = new ArrayList<ImportVegetarianExhibitorInfo>();
		willImportCustomerList = new ArrayList<ImportVegetarianExhibitorInfo>();
		List resultArray = new ArrayList();
		boolean isExistCompanyNameOrAddress = false;
		boolean isExistCompanyNameOrAddressOrEmail = false;
		try {
			InputStream is = new FileInputStream(importFile);
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
			// 循环工作表Sheet
			/*for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
			}*/
			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
			List<ImportVegetarianExhibitorInfo> tHistoryCustomerListTemp = new ArrayList<ImportVegetarianExhibitorInfo>();
			if (xssfSheet != null) {
				//总行数
				int value = xssfSheet.getLastRowNum();
				for (int beginIndex = 1;beginIndex <= value; beginIndex++) {
					isExistCompanyNameOrAddress = false;
					isExistCompanyNameOrAddressOrEmail = false;

					ImportVegetarianExhibitorInfo vegetarianExhibitorInfo = new ImportVegetarianExhibitorInfo();
					vegetarianExhibitorInfo.setId(beginIndex);

					Row xssfRow = xssfSheet.getRow(beginIndex);
					if (xssfRow != null && !(xssfRow.equals(""))) {
						//公司
						org.apache.poi.ss.usermodel.Cell companyCell = xssfRow.getCell(0);
						//地址  只有一个
						org.apache.poi.ss.usermodel.Cell addressShell = xssfRow.getCell(6);

						String companyStr = "";
						String addressStr = "";
						if(companyCell != null){
							companyStr = companyCell.getStringCellValue().trim();
						}
						if(addressShell != null){
							addressStr = addressShell.getStringCellValue().trim();
						}

						org.apache.poi.ss.usermodel.Cell emailShell = xssfRow.getCell(5);
						//邮箱
						if(emailShell != null){
							String emailValue = getCellValue(emailShell).trim();
							if(StringUtil.isNotEmpty(emailValue)){
								String emailContent = emailValue.replaceAll(" ", "");
								if(StringUtil.isNotEmpty(emailContent)){
									String[] emailList = emailContent.split("\n");
									//如果isExistCompanyName为false,则说明公司名是没有重复，那么变判断地址是否有重复
									int length = emailList.length;
									if(!isExistCompanyNameOrAddress && length>0) {
										for(int emailIndex=0;emailIndex<length;emailIndex++){
											String emailTemp = emailList[emailIndex];
											tHistoryCustomerListTemp = loadHistoryCustomerByCompanyNameAndAddressAndEmail(companyStr, addressStr, emailTemp, type);
											if(tHistoryCustomerListTemp != null && tHistoryCustomerListTemp.size()>0){
												isExistCompanyNameOrAddressOrEmail = true;
												break;
											}
										}
									}
								}else {
									tHistoryCustomerListTemp = loadHistoryCustomerByCompanyNameAndAddress(companyStr, addressStr, type);
									if(tHistoryCustomerListTemp != null && tHistoryCustomerListTemp.size()>0){
										isExistCompanyNameOrAddress = true;
									}
								}
							}else {
								tHistoryCustomerListTemp = loadHistoryCustomerByCompanyNameAndAddress(companyStr, addressStr, type);
								if(tHistoryCustomerListTemp != null && tHistoryCustomerListTemp.size()>0){
									isExistCompanyNameOrAddress = true;
								}
							}
						}else{
							tHistoryCustomerListTemp = loadHistoryCustomerByCompanyNameAndAddress(companyStr, addressStr, type);
							if(tHistoryCustomerListTemp != null && tHistoryCustomerListTemp.size()>0){
								isExistCompanyNameOrAddress = true;
							}
						}

						TExhibitor exhibitor = new TExhibitor();
						TExhibitorInfo exhibitorInfo = new TExhibitorInfo();
						List<TContact> contacts = new ArrayList<TContact>();
						exhibitor.setCountry(44);
						exhibitor.setExhibitionArea("0");
						exhibitor.setExhibitor_type("2");
						if(userInfo != null){
							exhibitor.setCreateUser(userInfo.getId());
							exhibitor.setUpdateUser(userInfo.getId());
							exhibitor.setTag(userInfo.getOwnerId());
						}
						exhibitor.setCreateTime(new Date());
						exhibitor.setUpdateTime(new Date());
						exhibitorInfo.setIsDelete(0);

						//获取列数
						int size = xssfRow.getLastCellNum();
						exhibitorInfo.setAddress("");
						exhibitorInfo.setCompany("");
						for (int i = 0; i < size; i++) {
							org.apache.poi.ss.usermodel.Cell cell = xssfRow.getCell(i);
							if(cell != null){
								String cellValue = getCellValue(cell).trim();
								if(StringUtil.isNotEmpty(cellValue)){
									cellValue = cellValue.replaceAll(" ", "");
								}else{
									cellValue = "";
								}
								switch (i) {
									case 0:	//公司名
										exhibitorInfo.setCompany(cellValue);
										vegetarianExhibitorInfo.setCompany(cellValue);
										break;
									case 1:	//联系人
										String[] names = cellValue.split("&");
										for(String name:names){
											TContact contact = new TContact();
											contact.setName(name);
											contacts.add(contact);
										}
										vegetarianExhibitorInfo.setContact(cellValue);
										break;
									case 2:	//手机
										String[] phone = cellValue.split("/");
										if(contacts.size() > 0){
											for(int k=0;k<phone.length;k++){
												contacts.get(k).setPhone(phone[k]);
											}
										}
										vegetarianExhibitorInfo.setMobilephone(cellValue);
										break;
									case 3:	//电话
										exhibitorInfo.setPhone(cellValue);
										vegetarianExhibitorInfo.setTelphone(cellValue);
										break;
									case 4:	//传真
										exhibitorInfo.setFax(cellValue);
										vegetarianExhibitorInfo.setFax(cellValue);
										break;
									case 5:	//邮箱
										String[] email = cellValue.trim().replaceAll(" ", "").split("\n");
										if(contacts.size() > 0){
											for(int k=0;k<email.length;k++){
												contacts.get(k).setEmail(email[k]);
											}
										}
										vegetarianExhibitorInfo.setEmail(cellValue);
										break;
									case 6:	//地址
										exhibitorInfo.setAddress(cellValue);
										vegetarianExhibitorInfo.setAddress(cellValue);
										break;
									default:
										break;
								}
							}
						}

						if(isExistCompanyNameOrAddress || isExistCompanyNameOrAddressOrEmail) {
							isExistCustomerList.addAll(tHistoryCustomerListTemp);
							willImportCustomerList.add(vegetarianExhibitorInfo);
						}else{
							Integer eid = (Integer) getHibernateTemplate().save(exhibitor);
							if(eid != null){
								exhibitor.setEid(eid);

								exhibitorInfo.setEid(eid);
								exhibitorInfo.setCreateTime(new Date());
								exhibitorInfo.setAdminUser(1);
								exhibitorInfoDao.create(exhibitorInfo);

								for(TContact contact:contacts){
									contact.setEid(eid);
									contact.setIsDelete(0);
									contactService.addContact(contact);
								}
							}
							count ++;
						}
					}
				}
			}

			if(willImportCustomerList != null && willImportCustomerList.size()>0){
				StringBuffer resultBuffer = new StringBuffer();
				resultBuffer.append("共导入：" + count + "条数据，其中有" + willImportCustomerList.size() + "条记录，可能存在重复！");
				resultArray.add(resultBuffer.toString());
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, Boolean.TRUE);
				JSONArray resultJson = JSONArray.fromObject(resultArray);
				report.setResult(resultJson.toString());
				JSONArray isExistDataJsonArray = JSONArray.fromObject(isExistCustomerList);
				JSONArray willImportDataJsonArray = JSONArray.fromObject(willImportCustomerList);
				report.setIsExistData(isExistDataJsonArray.toString());
				report.setWillImportData(willImportDataJsonArray.toString());
				report.setResultCode(1);
			}else{
				StringBuffer resultBuffer = new StringBuffer();
				resultBuffer.append("共导入：" + count + "条数据");
				resultArray.add(resultBuffer.toString());
				JSONArray resultJson = JSONArray.fromObject(resultArray);
				report.setResult(resultJson.toString());
				report.setIsExistData("");
				report.setWillImportData("");
				report.setResultCode(0);
			}
			xssfWorkbook.close();
		} catch (Exception e) {
			System.out.println(e);
			report.setResultCode(-1);
			e.printStackTrace();
		}
		return report;
	}

	private String getCellValue(org.apache.poi.ss.usermodel.Cell hssfCell) {
		if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
			// 返回布尔类型的值
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
			// 返回数值类型的值
			DecimalFormat df = new DecimalFormat("#");
			//return String.valueOf(hssfCell.getNumericCellValue());
			return String.valueOf(df.format(hssfCell.getNumericCellValue()));
		} else {
			// 返回字符串类型的值
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}

	/**
	 * 根据公司名、地址和邮箱查询展商
	 * @param companyName
	 * @return
	 */
	@Transactional
	public List<ImportVegetarianExhibitorInfo> loadHistoryCustomerByCompanyNameAndAddressAndEmail(String companyName, String address, String email, Integer type) {
		List<TExhibitor> tExhibitorList = exhibitorManagerService.loadExhibitorByType(type);
		List<ImportVegetarianExhibitorInfo> isExistVegetarianList = new ArrayList<ImportVegetarianExhibitorInfo>();
		for(TExhibitor tExhibitor:tExhibitorList){
			List<TExhibitorInfo> tHistoryCustomerList = exhibitorInfoDao.queryByHql("from TExhibitorInfo where (company=? or address=? " +
					"or CHARINDEX(?,email)>0) and isDelete=0 and eid =?", new Object[]{companyName, address, email, tExhibitor.getEid()});
			if(tHistoryCustomerList != null && tHistoryCustomerList.size()>0){
				for(int i=0;i<tHistoryCustomerList.size();i++){
					TExhibitorInfo tExhibitorInfo = tHistoryCustomerList.get(i);
					ImportVegetarianExhibitorInfo vegetarianExhibitorInfo = new ImportVegetarianExhibitorInfo();
					vegetarianExhibitorInfo.setId(tExhibitorInfo.getEid());
					vegetarianExhibitorInfo.setCompany(tExhibitorInfo.getCompany());
					vegetarianExhibitorInfo.setAddress(tExhibitorInfo.getAddress());
					vegetarianExhibitorInfo.setTelphone(tExhibitorInfo.getPhone());

					List<TContact> contacts = contactDao.loadContactByEid(tExhibitorInfo.getEid());
					if(contacts != null && contacts.size()>0){
						StringBuffer nameBuffer = new StringBuffer();
						StringBuffer phoneBuffer = new StringBuffer();
						StringBuffer emailBuffer = new StringBuffer();
						for(int k=0;k<contacts.size();k++){
							TContact tContact = contacts.get(k);
							nameBuffer.append(tContact.getName() + "&");
							phoneBuffer.append(tContact.getPhone() + "/");
							emailBuffer.append(tContact.getEmail() + "/");
						}
						int nameLastIndex = nameBuffer.lastIndexOf("&");
						String nameValue = nameBuffer.substring(0,nameLastIndex).toString();
						int phoneLastIndex = phoneBuffer.lastIndexOf("/");
						String phoneValue = phoneBuffer.substring(0,phoneLastIndex).toString();
						int emailLastIndex = emailBuffer.lastIndexOf("/");
						String emailValue = emailBuffer.substring(0,emailLastIndex).toString();
						vegetarianExhibitorInfo.setContact(nameValue);
						vegetarianExhibitorInfo.setMobilephone(phoneValue);
						vegetarianExhibitorInfo.setEmail(emailValue);
					}
					isExistVegetarianList.add(vegetarianExhibitorInfo);
				}
			}
		}
		return isExistVegetarianList.size() > 0 ? isExistVegetarianList : null;
	}

	/**
	 * 根据公司名和地址查询展商
	 * @param companyName
	 * @return
	 */
	@Transactional
	public List<ImportVegetarianExhibitorInfo> loadHistoryCustomerByCompanyNameAndAddress(String companyName, String Address, Integer type) {
		List<TExhibitor> tExhibitorList = exhibitorManagerService.loadExhibitorByType(type);
		List<ImportVegetarianExhibitorInfo> isExistVegetarianList = new ArrayList<ImportVegetarianExhibitorInfo>();
		for(TExhibitor tExhibitor:tExhibitorList){
			List<TExhibitorInfo> tHistoryCustomerList = exhibitorInfoDao.queryByHql("from TExhibitorInfo where (company=? or address=?) " +
					"and isDelete=0 and eid=? ", new Object[]{companyName, Address, type, tExhibitor.getEid()});
			if(tHistoryCustomerList != null && tHistoryCustomerList.size()>0){
				for(int i=0;i<tHistoryCustomerList.size();i++){
					TExhibitorInfo tExhibitorInfo = tHistoryCustomerList.get(i);
					ImportVegetarianExhibitorInfo vegetarianExhibitorInfo = new ImportVegetarianExhibitorInfo();
					vegetarianExhibitorInfo.setId(tExhibitorInfo.getEid());
					vegetarianExhibitorInfo.setCompany(tExhibitorInfo.getCompany());
					vegetarianExhibitorInfo.setAddress(tExhibitorInfo.getAddress());
					vegetarianExhibitorInfo.setTelphone(tExhibitorInfo.getPhone());

					List<TContact> contacts = contactDao.loadContactByEid(tExhibitorInfo.getEid());
					if(contacts != null && contacts.size()>0){
						StringBuffer nameBuffer = new StringBuffer();
						StringBuffer phoneBuffer = new StringBuffer();
						StringBuffer emailBuffer = new StringBuffer();
						for(int k=0;k<contacts.size();k++){
							TContact tContact = contacts.get(k);
							nameBuffer.append(tContact.getName() + "&");
							phoneBuffer.append(tContact.getPhone() + "/");
							emailBuffer.append(tContact.getEmail() + "/");
						}
						int nameLastIndex = nameBuffer.lastIndexOf("&");
						String nameValue = nameBuffer.substring(0,nameLastIndex).toString();
						int phoneLastIndex = phoneBuffer.lastIndexOf("/");
						String phoneValue = phoneBuffer.substring(0,phoneLastIndex).toString();
						int emailLastIndex = emailBuffer.lastIndexOf("/");
						String emailValue = emailBuffer.substring(0,emailLastIndex).toString();
						vegetarianExhibitorInfo.setContact(nameValue);
						vegetarianExhibitorInfo.setMobilephone(phoneValue);
						vegetarianExhibitorInfo.setEmail(emailValue);
					}
					isExistVegetarianList.add(vegetarianExhibitorInfo);
				}
			}
		}
		return isExistVegetarianList.size() > 0 ? isExistVegetarianList : null;
	}

	public QueryDuplicatExhibitorResponse getWillImportCustomerList(QueryDuplicateExhibitorRequest request){
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		QueryDuplicatExhibitorResponse response = new QueryDuplicatExhibitorResponse();
		response.setResultCode(0);
		response.setRows(willImportCustomerList);
		response.setTotal(page.getTotalCount());
		return response;
	}

	public QueryDuplicatExhibitorResponse getIsExistCustomerList(QueryDuplicateExhibitorRequest request, Integer[] willImportCheckedItems){
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		QueryDuplicatExhibitorResponse response = new QueryDuplicatExhibitorResponse();
		response.setResultCode(0);
		List<ImportVegetarianExhibitorInfo> isExistSelectCustomerList = new ArrayList<ImportVegetarianExhibitorInfo>();
		if(willImportCheckedItems == null || willImportCheckedItems[0] == null){
			response.setRows(isExistCustomerList);
		}else{
			for(int i=0;i<willImportCheckedItems.length;i++){
				if(willImportCheckedItems[i] != null){
					int selectIndex = willImportCheckedItems[i];
					if(willImportCustomerList != null && selectIndex>0){
						for(int kk=0;kk<willImportCustomerList.size();kk++){
							ImportVegetarianExhibitorInfo tHistoryCustomer = willImportCustomerList.get(kk);
							if(tHistoryCustomer.getId() == selectIndex){
								if(isExistCustomerList != null && isExistCustomerList.size()>0){
									for(int k=0;k<isExistCustomerList.size();k++){
										ImportVegetarianExhibitorInfo tHistoryCustomer1 = isExistCustomerList.get(k);
										if(tHistoryCustomer1.getCompany().equalsIgnoreCase(tHistoryCustomer.getCompany())
												|| tHistoryCustomer1.getAddress().equalsIgnoreCase(tHistoryCustomer.getAddress())
												|| isEmailExist(tHistoryCustomer1.getEmail(),tHistoryCustomer.getEmail())){
											isExistSelectCustomerList.add(tHistoryCustomer1);
										}
									}
								}
							}
						}
					}
				}
			}
			response.setRows(isExistSelectCustomerList);
		}
		return response;
	}

	/**
	 * 判断邮箱是否存在
	 * @param emailSource 表示被比较的对象
	 * @param emailTarget 表示要比较的对象
	 * @return boolean
	 */
	private boolean isEmailExist(String emailSource, String emailTarget){
		boolean isExistFlag = false;
		if(StringUtil.isNotEmpty(emailSource) && StringUtil.isNotEmpty(emailTarget)){
			String[] emailSourceList = emailSource.split("\n");
			String[] emailTargetList = emailTarget.split("\n");
			//如果isExistCompanyName为false,则说明公司名是没有重复，那么变判断地址是否有重复
			Set<String> set = new HashSet<String>(Arrays.asList(emailSourceList));
			if(emailTargetList != null && emailTargetList.length>0){
				for(int i=0;i<emailTargetList.length;i++){
					String emailTemp = emailTargetList[i];
					if(set.contains(emailTemp)){
						isExistFlag =  true;
					}
				}
			}else{
				isExistFlag =  false;
			}
		}else{
			isExistFlag =  false;
		}
		return isExistFlag;
	}

	/**
	 * 导入资料库
	 * @param tids
	 */
	@Transactional
	public void insertCustomerInfoByTids(Integer[] tids, Integer type, TUserInfo userInfo) {
		if(tids != null && tids.length>0){
			for(int k=0;k<tids.length;k++){
				int id = tids[k];
				if(willImportCustomerList != null && willImportCustomerList.size()>0){
					for(int i=0;i<willImportCustomerList.size();i++){
						ImportVegetarianExhibitorInfo tHistoryCustomer = willImportCustomerList.get(i);

						if(tHistoryCustomer.getId() == id){
							TExhibitor exhibitor = new TExhibitor();
							TExhibitorInfo exhibitorInfo = new TExhibitorInfo();
							List<TContact> contacts = new ArrayList<TContact>();
							exhibitor.setCountry(44);
							exhibitor.setExhibitionArea("0");
							if(type == 1){
								exhibitor.setExhibitor_type("1");
							} else if(type == 2){
								exhibitor.setExhibitor_type("2");
							}
							if(userInfo != null){
								exhibitor.setCreateUser(userInfo.getId());
								exhibitor.setUpdateUser(userInfo.getId());
								exhibitor.setTag(userInfo.getOwnerId());
							}
							exhibitor.setCreateTime(new Date());
							exhibitor.setUpdateTime(new Date());
							exhibitorInfo.setIsDelete(0);

							exhibitorInfo.setAddress(tHistoryCustomer.getAddress());
							exhibitorInfo.setCompany(tHistoryCustomer.getCompany());
							String[] names = tHistoryCustomer.getContact().split("&");
							for(String name:names){
								TContact contact = new TContact();
								contact.setName(name);
								contacts.add(contact);
							}

							String[] phone = tHistoryCustomer.getMobilephone().split("/");
							if(contacts.size() > 0){
								for(int t = 0;t < phone.length; t ++){
									contacts.get(t).setPhone(phone[t]);
								}
							}

							exhibitorInfo.setPhone(tHistoryCustomer.getTelphone());
							exhibitorInfo.setFax(tHistoryCustomer.getFax());

							String[] email = tHistoryCustomer.getEmail().trim().replaceAll(" ", "").split("\n");
							if(contacts.size() > 0){
								for(int t = 0;t < email.length; t ++){
									contacts.get(t).setEmail(email[t]);
								}
							}

							exhibitorInfo.setMainProduct(tHistoryCustomer.getProduct());

							Integer eid = (Integer) getHibernateTemplate().save(exhibitor);
							if(eid != null){
								exhibitor.setEid(eid);

								exhibitorInfo.setEid(eid);
								exhibitorInfo.setCreateTime(new Date());
								exhibitorInfo.setAdminUser(1);
								exhibitorInfoDao.create(exhibitorInfo);

								for(TContact contact:contacts){
									contact.setEid(eid);
									contact.setIsDelete(0);
									contactService.addContact(contact);
								}
							}

							willImportCustomerList.remove(tHistoryCustomer);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * 忽略导入资料库
	 * @param tids
	 */
	@Transactional
	public void ignoreCustomerInfoByTids(Integer[] tids) {
		if(tids != null && tids.length>0){
			for(int k=0;k<tids.length;k++){
				int id = tids[k];
				if(willImportCustomerList != null && willImportCustomerList.size()>0){
					for(int i=0;i<willImportCustomerList.size();i++){
						ImportVegetarianExhibitorInfo tHistoryCustomer = willImportCustomerList.get(i);
						if(tHistoryCustomer.getId() == id){
							willImportCustomerList.remove(tHistoryCustomer);
							break;
						}
					}
				}
			}
		}
	}

	public boolean refreshIsExistCustomerList(Integer[] tids, TUserInfo userInfo){
		List<TExhibitorInfo> tHistoryCustomerListTemp = new ArrayList<TExhibitorInfo>();
		for(Integer eid:tids){
			TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(eid);
			if(exhibitorInfo != null) {
				for(int i=0;i<isExistCustomerList.size();i++){
					if(isExistCustomerList.get(i).getCompany().equalsIgnoreCase(exhibitorInfo.getCompany())
							|| isExistCustomerList.get(i).getAddress().equalsIgnoreCase(exhibitorInfo.getAddress())
							|| isEmailExist(isExistCustomerList.get(i).getEmail(),exhibitorInfo.getEmail())){
						isExistCustomerList.remove(i);
					} else{
						tHistoryCustomerListTemp.add(exhibitorInfo);
					}
				}
			}
		}
		if(tHistoryCustomerListTemp != null && tHistoryCustomerListTemp.size()>0){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 删除已经存在的展商资料（标记设置为1，表示不可见）
	 * @param tids
	 */
	@Transactional
	public boolean removeCustomerInfoByTids(Integer[] tids, Integer type, TUserInfo userInfo) {
		List<TExhibitorInfo> tHistoryCustomerListTemp = new ArrayList<TExhibitorInfo>();
		for(Integer eid:tids){
			TExhibitor exhibitor = loadExhibitorByEid(eid);
			TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(exhibitor.getEid());
			if(exhibitorInfo != null) {
				if(isExistCustomerList != null && isExistCustomerList.size()>0){
					for(int i=0;i<isExistCustomerList.size();i++){
						if(isExistCustomerList.get(i).getCompany().equalsIgnoreCase(exhibitorInfo.getCompany())
								|| isExistCustomerList.get(i).getAddress().equalsIgnoreCase(exhibitorInfo.getAddress())
								|| isEmailExist(isExistCustomerList.get(i).getEmail(),exhibitorInfo.getEmail())){
							exhibitorInfo.setIsDelete(1);
							exhibitorInfo.setUpdateTime(new Date());
							getHibernateTemplate().update(exhibitorInfo);

							exhibitor.setUpdateUser(userInfo.getId());
							exhibitor.setUpdateTime(new Date());
							getHibernateTemplate().update(exhibitor);
						} else{
							tHistoryCustomerListTemp.add(exhibitorInfo);
						}
					}
				}
			}
		}
		if(tHistoryCustomerListTemp != null && tHistoryCustomerListTemp.size()>0){
			return false;
		}else{
			return true;
		}
	}
}
