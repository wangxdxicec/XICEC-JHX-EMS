package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.dao.ExhibitorInfoDao;
import com.zhenhappy.ems.entity.*;
import com.zhenhappy.ems.manager.dto.*;
import com.zhenhappy.ems.manager.entity.TExhibitorBooth;
import com.zhenhappy.ems.manager.util.JChineseConvertor;
import com.zhenhappy.ems.manager.util.StringUtil;
import com.zhenhappy.ems.service.CountryProvinceService;
import com.zhenhappy.ems.service.ExhibitorService;

import com.zhenhappy.ems.service.InvoiceExtendService;
import com.zhenhappy.ems.service.InvoiceService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by wujianbin on 2014-08-25.
 */
@Service
public class ImportExportService extends ExhibitorService {

	private static Logger log = Logger.getLogger(ImportExportService.class);

	@Autowired
	private ExhibitorManagerService exhibitorManagerService;
	@Autowired
	private CountryProvinceService countryProvinceService;
	@Autowired
	private ExhibitorInfoDao exhibitorInfoDao;
	@Autowired
	private ContactManagerService contactService;
	@Autowired
	private InvoiceExtendService invoiceService;
    @Autowired
    private JoinerManagerService joinerManagerService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private CustomerInfoManagerService customerInfoManagerService;

	public List<QueryExhibitorInfo> exportExhibitor(List<TeaExhibitor> exhibitors) {
		List<QueryExhibitorInfo> queryExhibitorInfos = new ArrayList<QueryExhibitorInfo>();
		if(exhibitors != null){
			for(TeaExhibitor exhibitor:exhibitors){
				TExhibitorInfo exhibitorInfo = exhibitorManagerService.loadExhibitorInfoByEid(exhibitor.getEid());
				if(exhibitorInfo != null){
					QueryExhibitorInfo queryExhibitorInfo = new QueryExhibitorInfo();
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
					TInvoiceApplyExtend invoice = invoiceService.getByEid(exhibitorInfo.getEid());
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
					queryExhibitorInfo.setCompany(exhibitorInfo.getCompany());
					queryExhibitorInfo.setCompanyEn(exhibitorInfo.getCompanyEn());
					queryExhibitorInfos.add(queryExhibitorInfo);
				}
			}
		}
		return queryExhibitorInfos;
	}

	/**
	 * 导出展商参展人员列表
	 * @param exhibitors
	 * @return
	 */
	public List<ExportExhibitorJoiner> exportExhibitorJoiners(List<TeaExhibitor> exhibitors) {
		List<ExportExhibitorJoiner> exportExhibitorJoiners = new ArrayList<ExportExhibitorJoiner>();
		if(exhibitors != null){
			for(TeaExhibitor exhibitor:exhibitors){
				TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(exhibitor.getEid());
				List<TExhibitorJoiner> joiners = joinerManagerService.loadExhibitorJoinerByEid(exhibitor.getEid());
				String booth_number = exhibitorManagerService.loadBoothNum(exhibitor.getEid());
				if(joiners != null){
					for(TExhibitorJoiner joiner:joiners){
						ExportExhibitorJoiner exportExhibitorJoiner = new ExportExhibitorJoiner();
						BeanUtils.copyProperties(joiner, exportExhibitorJoiner);
						exportExhibitorJoiner.setBoothNumber(booth_number);
						exportExhibitorJoiner.setCompany(exhibitorInfo.getCompany());
						exportExhibitorJoiner.setCompanye(exhibitorInfo.getCompanyEn());
						exportExhibitorJoiners.add(exportExhibitorJoiner);
					}
				}
			}
		}
		return exportExhibitorJoiners;
	}

	public List<String> importExhibitor(File importFile,
										ImportExhibitorsRequest request,
										Integer isCurrent,
										String country,
										String province,
										String area,
										String group,
										String tag,
										ManagerPrinciple principle) {
		Integer count = 0;
		List<String> report = new ArrayList<String>();
		String tea_Fair_Show_Begin_Date = jdbcTemplate.queryForObject("select tea_Fair_Show_Begin_Date from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);
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

				TeaExhibitor exhibitor = new TeaExhibitor();
				TExhibitorInfo exhibitorInfo = new TExhibitorInfo();
				List<TContact> contacts = new ArrayList<TContact>();
				String company = null;
				String companye = null;
				String userName = null;
				for (int i = 1; i < 15; i++) {
					Cell cell = sheet.getCell(i, j);
					switch (i) {
						case 1:	//用户名
							userName = cell.getContents().trim().replaceAll(" ", "");
							exhibitor.setUsername(userName);
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
						default:
							break;
					}
				}
				if(StringUtils.isEmpty(company) && StringUtils.isEmpty(companye)){
//					System.out.println("第" + (j+1) + "行有问题,原因:公司中文名和英文名都为空");
					report.add("第" + (j+1) + "行有问题,原因:公司中文名和英文名都为空");
					continue;//公司中文名和英文名都为空
				}
				if(isCurrent == 1){
					if(exhibitorManagerService.loadAllExhibitorByUserName(userName) != null){
						report.add("第" + (j+1) + "行有问题,原因:用户名"+ userName + "存在重复");
						continue;
					}
				}else if((exhibitorManagerService.loadAllExhibitorByCompany(company) != null) || (exhibitorManagerService.loadAllExhibitorByCompanye(companye) != null)){
//					System.out.println("第" + (j+1) + "行有问题,原因:公司中文名"+ company +"或英文名"+ companye +"存在重复");
					report.add("第" + (j+1) + "行有问题,原因:公司中文名"+ company +"或英文名"+ companye +"存在重复");
					continue;
				}
				//exhibitor.setCompany(company);
				exhibitorInfo.setCompany(company);
				exhibitorInfo.setCompany(company);
				exhibitor.setFair_year(tea_Fair_Show_Begin_Date);
				if(isCurrent == 1){
					//若是在本届展商列表界面导入账商信息，默认为启用状态
					exhibitor.setIsLogout(0);
				}else{
					exhibitor.setIsLogout(1);
				}
				//exhibitor.setCompanye(companye);
				exhibitorInfo.setCompanyEn(companye);
				//exhibitor.setCompanyt(JChineseConvertor.getInstance().s2t(company.trim()));
				exhibitorInfo.setCompanyT(JChineseConvertor.getInstance().s2t(company.trim()));
				if(StringUtils.isNotEmpty(country)) exhibitor.setCountry(request.getCountry());
				if(StringUtils.isNotEmpty(province)) exhibitor.setProvince(request.getProvince());
				if(StringUtils.isNotEmpty(area)) exhibitor.setArea(request.getArea());
				if(StringUtils.isNotEmpty(group)) exhibitor.setGroup(request.getGroup());
				if(StringUtils.isNotEmpty(tag)) exhibitor.setTag(request.getTag());
				exhibitor.setCreateTime(new Date());
				if(principle != null && principle.getAdmin() != null){
					exhibitor.setCreateUser(principle.getAdmin().getId());
					exhibitor.setUpdateUser(principle.getAdmin().getId());
				}
				exhibitor.setExhibitionArea("0");
				exhibitor.setCreateTime(new Date());
				exhibitor.setUpdateTime(new Date());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return report;
	}

	public void copyLogo(Integer[] eids, String destDir) throws IOException {
		List<TeaExhibitor> exhibitors = new ArrayList<TeaExhibitor>();
		if(eids == null){
			exhibitors = exhibitorManagerService.loadAllExhibitors();
		}else{
			exhibitors = exhibitorManagerService.loadSelectedExhibitors(eids);
		}
		for(TeaExhibitor exhibitor:exhibitors){
			TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(exhibitor.getEid());
			if(exhibitorInfo != null){
				if(StringUtils.isNotEmpty(exhibitorInfo.getLogo())){
					String boothNumber = loadBoothNum(exhibitor.getEid());
					File srcFile = new File(exhibitorInfo.getLogo().replaceAll("\\\\\\\\", "\\\\").replaceAll("/", "\\\\"));
					if(StringUtil.isNotEmpty(srcFile.getPath())){
						String[] filePath = srcFile.getPath().split(";");
						for(int i=0;i<filePath.length;i++){
							File fileTemp = new File(filePath[i]);
							if (fileTemp.exists() == false) continue;
							/*茶博会需求开始*/
//							if(StringUtils.isNotEmpty(exhibitor.getCompany())) exhibitor.setCompany("");
//							if(StringUtils.isNotEmpty(exhibitor.getCompanye())) exhibitor.setCompanye("");
							File destFile = new File(destDir + "\\" + exhibitorInfo.getCompany().replaceAll("/", "")
									+ exhibitorInfo.getCompanyEn().replaceAll("/", "") + boothNumber.replaceAll("/", "")
									+ (filePath.length>1?("_" + (i+1)):"") + "."
									+ FilenameUtils.getExtension(filePath[i].replaceAll("/", "\\\\\\\\")));
									/*+ FilenameUtils.getExtension(exhibitorInfo.getLogo().replaceAll("/", "\\\\\\\\")));*/
							/*茶博会需求结束*/
							if(destFile != null) {
								FileUtils.copyFile(fileTemp, destFile);
							}
						}
					}
				}
			}
		}
	}

	public void copyExhibitorInvoiceApplyImage(Integer[] eids, String destDir) throws IOException {
		List<TInvoiceApplyExtend> invoiceApplyExtendArrayList = new ArrayList<TInvoiceApplyExtend>();
		//查询开具增值发票对应的展商
		if(eids == null){
			invoiceApplyExtendArrayList = exhibitorManagerService.loadAllInvoiceApplyByInvoiceFlag();
		}else{
			invoiceApplyExtendArrayList = exhibitorManagerService.loadSelectedInvoiceApplyByInvoiceFlag(eids);
		}
		if(invoiceApplyExtendArrayList != null && invoiceApplyExtendArrayList.size() > 0){
			for(TInvoiceApplyExtend invoiceApplyExtend:invoiceApplyExtendArrayList){
				TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(invoiceApplyExtend.getEid());
				if(exhibitorInfo != null){
					if(StringUtils.isNotEmpty(invoiceApplyExtend.getInvoice_image_address())){
						String boothNumber = loadBoothNum(invoiceApplyExtend.getEid());
						File srcFile = new File(invoiceApplyExtend.getInvoice_image_address().replaceAll("\\\\\\\\", "\\\\").replaceAll("/", "\\\\"));
						if (srcFile.exists() == false) continue;
						File destFile = null;

						String dirPathValue = "";
						if(StringUtils.isNotEmpty(exhibitorInfo.getCompany())){
							dirPathValue = destDir + exhibitorInfo.getCompany();
						}else{
							dirPathValue = destDir + exhibitorInfo.getCompanyEn();
						}
						//判断目录是否存在，如果不存在，就先创建
						File fileTemp = new File(dirPathValue);
						if(!fileTemp.exists()  && !fileTemp.isDirectory()) {
							fileTemp.mkdir();
						}

						if(StringUtils.isNotEmpty(exhibitorInfo.getCompany())){
							destFile = new File(dirPathValue + "\\" + exhibitorInfo.getCompany().replaceAll("/", "") + boothNumber.replaceAll("/", "") + "." + FilenameUtils.getExtension(invoiceApplyExtend.getInvoice_image_address().replaceAll("/", "\\\\\\\\")));
						}else{
							destFile = new File(dirPathValue + "\\" + exhibitorInfo.getCompanyEn().replaceAll("/", "") + boothNumber.replaceAll("/", "") + "." + FilenameUtils.getExtension(invoiceApplyExtend.getInvoice_image_address().replaceAll("/", "\\\\\\\\")));
						}
						//destFile = new File(dirPathValue + "\\" + "." + FilenameUtils.getExtension(invoiceApplyExtend.getInvoice_image_address().replaceAll("/", "\\\\\\\\")));
						//destFile = new File(dirPathValue + "\\" + );
						FileUtils.copyFile(srcFile, destFile);
					}
				}
			}
		}
	}

	public void WriteStringToFile(String str, String filePath) {
		try {
			if(filePath != null || StringUtils.isNotEmpty(filePath)) {
				FileOutputStream fos = new FileOutputStream(filePath);
				fos.write(str.getBytes());
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出客商数据
	 * @param customers
	 * @return
	 */
	public List<ExportCustomerInfo> exportCustomer(List<TTeaVisitorInfo> customers, int flag) {
		List<ExportCustomerInfo> exportCustomerInfos = new ArrayList<ExportCustomerInfo>();
		if(customers.size() > 0){
			for(TTeaVisitorInfo customer:customers){
				ExportCustomerInfo exportCustomerInfo = new ExportCustomerInfo();
				exportCustomerInfo.setName((customer.getFirstName() == null ? "":customer.getFirstName()) + " " + (customer.getLastName() == null ? "":customer.getLastName()));
				exportCustomerInfo.setPhone(/*(customer.getMobilePhoneCode() == null ? "":customer.getMobilePhoneCode()) + */(customer.getMobilePhone() == null ? "":customer.getMobilePhone()));
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
				BeanUtils.copyProperties(customer, exportCustomerInfo);
				if(flag == 1){
					if(StringUtils.isNotEmpty(customer.getProvince())){
						WProvince province = countryProvinceService.loadProvinceById(Integer.parseInt(customer.getProvince()));
						if(province != null){
							exportCustomerInfo.setAddress(province.getChineseName() + customer.getCity() + customer.getAddress());
						}
					}
				}else {
					if("Male".equalsIgnoreCase(customer.getSex())){
						exportCustomerInfo.setName("ATTN: Mr. " + customer.getFirstName() + ", " + customer.getPosition());
					}else{
						exportCustomerInfo.setName("ATTN: Ms. " + customer.getFirstName() + ", " + customer.getPosition());
					}
				}
				exportCustomerInfos.add(exportCustomerInfo);
			}
		}
		return exportCustomerInfos;
	}

	/**
	 * 导出展商VISA数据
	 * @param tVisas
	 * @return
	 */
	public List<ExportTVisa> exportTVisas(List<TVisa> tVisas) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		List<ExportTVisa> queryExportTVisas = new ArrayList<ExportTVisa>();
		if(tVisas != null){
			for(TVisa tVisa:tVisas){
				TExhibitorInfo exhibitorInfo = exhibitorManagerService.loadExhibitorInfoByEid(tVisa.getEid());
				TExhibitor exhibitor = exhibitorManagerService.loadExhibitorByEid(tVisa.getEid());
				if(exhibitor != null){
					ExportTVisa queryExportTVisa = new ExportTVisa();
					if(exhibitorInfo != null){
						queryExportTVisa.setExhibitor(exhibitorInfo.getCompanyEn());
					} else {
						queryExportTVisa.setExhibitor("");
					}
					queryExportTVisa.setPassportName(tVisa.getPassportName());
					queryExportTVisa.setPassportNo(tVisa.getPassportNo());
					queryExportTVisa.setNationality(tVisa.getNationality());
					queryExportTVisa.setHotelAddress(tVisa.getDetailedAddress());
					if(tVisa.getBirth() != null)
						queryExportTVisa.setBirth(sdf.format(tVisa.getBirth()));
					else
						queryExportTVisa.setBirth("");
					if (tVisa.getGender() != null && tVisa.getGender() == 1)
						queryExportTVisa.setGender("Mr.");
					else if (tVisa.getGender() != null && tVisa.getGender() == 2)
						queryExportTVisa.setGender("Miss");
					//queryExportTVisa.setAddress(tVisa.getAddress());
					queryExportTVisa.setApplyFor(tVisa.getApplyFor());
					if(tVisa.getFrom() != null)
						queryExportTVisa.setFrom(sdf.format(tVisa.getFrom()));
					else
						queryExportTVisa.setFrom("");
					if(tVisa.getTo() != null)
						queryExportTVisa.setTo(sdf.format(tVisa.getTo()));
					else
						queryExportTVisa.setTo("");
					queryExportTVisa.setNeedPost("");
					queryExportTVisa.setExpressTp("");
					queryExportTVisa.setExpressNo("");
					if(tVisa.getExpDate() != null)
						queryExportTVisa.setExpDate(sdf.format(tVisa.getExpDate()));
					else queryExportTVisa.setExpDate("");
					if(tVisa.getCreateTime() != null)
						queryExportTVisa.setCreateTime(sdf.format(tVisa.getCreateTime()));
					else
						queryExportTVisa.setCreateTime("");
					if(tVisa.getJoinerId() != null){
						TExhibitorJoiner joiner = joinerManagerService.loadExhibitorJoinerById(tVisa.getJoinerId());
						if(joiner != null){
							queryExportTVisa.setJobTitle(joiner.getPosition());
							tVisa.setJobTitle(joiner.getPosition());
							queryExportTVisa.setEmail(joiner.getEmail());
							tVisa.setEmail(joiner.getEmail());
							queryExportTVisa.setTel(joiner.getTelphone());
							tVisa.setTel(joiner.getTelphone());
						}else{
							queryExportTVisa.setJobTitle("");
							queryExportTVisa.setEmail("");
							queryExportTVisa.setTel("");
						}
					}
					if(exhibitorInfo != null){
						if(StringUtils.isNotEmpty(exhibitorInfo.getCompanyEn())){
							queryExportTVisa.setCompanyName(exhibitorInfo.getCompanyEn());
							queryExportTVisa.setEmailCompany(exhibitorInfo.getEmail());
							tVisa.setCompanyName(exhibitorInfo.getCompanyEn());
						}else if(StringUtils.isNotEmpty(exhibitorInfo.getCompany())){
							queryExportTVisa.setCompanyName(exhibitorInfo.getCompany());
							tVisa.setCompanyName(exhibitorInfo.getCompany());
						}
						queryExportTVisa.setCompanyWebsite(exhibitorInfo.getWebsite());
						tVisa.setCompanyWebsite(exhibitorInfo.getWebsite());
						queryExportTVisa.setFax(exhibitorInfo.getFax());
						tVisa.setFax(exhibitorInfo.getFax());

						if(StringUtils.isNotEmpty(exhibitorInfo.getAddressEn())){
							queryExportTVisa.setAddress(exhibitorInfo.getAddressEn().trim());
						}else{
							queryExportTVisa.setAddress("");
						}
					}else{
						queryExportTVisa.setCompanyName("");
						queryExportTVisa.setCompanyWebsite("");
						queryExportTVisa.setFax("");
					}
					String boothNum = exhibitorManagerService.loadBoothNum(exhibitor.getEid());
					if(StringUtils.isNotEmpty(boothNum)){
						queryExportTVisa.setBoothNo(boothNum);
						tVisa.setBoothNo(boothNum);
					}else{
						queryExportTVisa.setBoothNo("");
					}
					if(StringUtils.isNotEmpty(queryExportTVisa.getEmail())){
						if(StringUtils.isNotEmpty(queryExportTVisa.getEmailCompany())){
							queryExportTVisa.setEmailInfo(queryExportTVisa.getEmail() + "," + queryExportTVisa.getEmailCompany());
						}else{
							queryExportTVisa.setEmailInfo(queryExportTVisa.getEmailCompany());
						}
					}else{
						queryExportTVisa.setEmailInfo(queryExportTVisa.getEmailCompany());
					}
					queryExportTVisas.add(queryExportTVisa);
				}else{
					//System.out.println("此Id没有对应展商:" + tVisa.getEid());
					ExportTVisa queryExportTVisa = new ExportTVisa();
					queryExportTVisa.setExhibitor("找不到对应展商");
					queryExportTVisa.setPassportName(tVisa.getPassportName());
					queryExportTVisa.setPassportNo(tVisa.getPassportNo());
					queryExportTVisa.setNationality(tVisa.getNationality());
					queryExportTVisa.setHotelAddress(tVisa.getDetailedAddress());
					if(tVisa.getBirth() != null)
						queryExportTVisa.setBirth(sdf.format(tVisa.getBirth()));
					else
						queryExportTVisa.setBirth("");
					if (tVisa.getGender() == 1)
						queryExportTVisa.setGender("Mr.");
					else if (tVisa.getGender() == 2)
						queryExportTVisa.setGender("Miss");
					queryExportTVisa.setJobTitle("");
					queryExportTVisa.setCompanyName("");
					queryExportTVisa.setBoothNo("");
					//queryExportTVisa.setAddress(tVisa.getAddress());
					queryExportTVisa.setAddress("");
					queryExportTVisa.setApplyFor(tVisa.getApplyFor());
					if(tVisa.getFrom() != null)
						queryExportTVisa.setFrom(sdf.format(tVisa.getFrom()));
					else
						queryExportTVisa.setFrom("");
					if(tVisa.getTo() != null)
						queryExportTVisa.setTo(sdf.format(tVisa.getTo()));
					else
						queryExportTVisa.setTo("");
					queryExportTVisa.setEmail("");
					queryExportTVisa.setEmailCompany("");
					queryExportTVisa.setCompanyWebsite("");
					queryExportTVisa.setTel("");
					queryExportTVisa.setFax("");
					queryExportTVisa.setNeedPost("");
					queryExportTVisa.setExpressTp("");
					queryExportTVisa.setExpressNo("");
					if(tVisa.getExpDate() != null)
						queryExportTVisa.setExpDate(sdf.format(tVisa.getExpDate()));
					else
						queryExportTVisa.setExpDate("");
					if(tVisa.getCreateTime() != null)
						queryExportTVisa.setCreateTime(sdf.format(tVisa.getCreateTime()));
					else
						queryExportTVisa.setCreateTime("");
					queryExportTVisa.setEmailInfo("");
					queryExportTVisas.add(queryExportTVisa);
				}
			}
		}
		return queryExportTVisas;
	}

	/**
	 * 导出客商VISA数据
	 * @param wVisas
	 * @return
	 */
	public List<ExportWVisa> exportWVisas(List<WVisa> wVisas) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		List<ExportWVisa> queryExportWVisas = new ArrayList<ExportWVisa>();
		if(wVisas != null){
			for(WVisa wVisa:wVisas){
				TTeaVisitorInfo customerInfo = customerInfoManagerService.loadCustomerInfoById(wVisa.getWCustomerInfo());
				if(customerInfo != null) {
					ExportWVisa queryExportWVisa = new ExportWVisa();
					queryExportWVisa.setCustomer("客商");
					queryExportWVisa.setPassportName(wVisa.getFullPassportName());
					queryExportWVisa.setPassportNo(wVisa.getPassportNo());
					if(wVisa.getDateOfBirth() != null){
						queryExportWVisa.setDateOfBirth(sdf.format(wVisa.getDateOfBirth()));
					}
					queryExportWVisa.setNationality(wVisa.getNationality());
					queryExportWVisa.setHotelAddress(wVisa.getHotel());
					if ("male".equalsIgnoreCase(wVisa.getGender()))
						queryExportWVisa.setGender("Mr.");
					else if ("female".equalsIgnoreCase(wVisa.getGender()))
						queryExportWVisa.setGender("Miss");
					queryExportWVisa.setPosition(customerInfo.getPosition());
					queryExportWVisa.setCompany(customerInfo.getCompany());
					queryExportWVisa.setAddress(customerInfo.getAddress());
					queryExportWVisa.setChineseEmbassy(wVisa.getChineseEmbassy());
					if(wVisa.getDurationBeginTime() != null){
						queryExportWVisa.setDurationBeginTime(sdf.format(wVisa.getDurationBeginTime()));
					}
					if(wVisa.getDurationEndTime() != null){
						queryExportWVisa.setDurationEndTime(sdf.format(wVisa.getDurationEndTime()));
					}
					queryExportWVisa.setEmail(customerInfo.getEmail());
					queryExportWVisa.setWebsite(customerInfo.getWebsite());
					StringBuffer telbuffer = new StringBuffer();
					if(customerInfo.getTelCode() != null && StringUtils.isNotEmpty(customerInfo.getTelCode())
							&& !"".equals(customerInfo.getTelCode().trim())) {
						telbuffer.append(customerInfo.getTelCode());
						if(customerInfo.getTelCode2() != null && StringUtils.isNotEmpty(customerInfo.getTelCode2())
								&& !"".equals(customerInfo.getTelCode2().trim())){
							telbuffer.append("-" + customerInfo.getTelCode2());
							if(customerInfo.getTel() != null && StringUtils.isNotEmpty(customerInfo.getTel())
									&& !"".equals(customerInfo.getTel().trim())){
								telbuffer.append("-" + customerInfo.getTel());
							}
						}else{
							if(customerInfo.getTel() != null && StringUtils.isNotEmpty(customerInfo.getTel())
									&& !"".equals(customerInfo.getTel().trim())){
								telbuffer.append(customerInfo.getTel());
							}
						}
					} else{
						if(customerInfo.getTelCode2() != null && StringUtils.isNotEmpty(customerInfo.getTelCode2())
								&& !"".equals(customerInfo.getTelCode2().trim())){
							telbuffer.append(customerInfo.getTelCode2());
							if(customerInfo.getTel() != null && StringUtils.isNotEmpty(customerInfo.getTel())
									&& !"".equals(customerInfo.getTel().trim())){
								telbuffer.append("-" + customerInfo.getTel());
							}
						}else{
							if(customerInfo.getTel() != null && StringUtils.isNotEmpty(customerInfo.getTel())
									&& !"".equals(customerInfo.getTel().trim())){
								telbuffer.append(customerInfo.getTel());
							}
						}
					}
					//queryExportWVisa.setTelephone(customerInfo.getTelephone());
					queryExportWVisa.setTelephone(telbuffer.toString());
					queryExportWVisa.setFax(customerInfo.getFax());
					if(wVisa.getNeedPost() != null && wVisa.getNeedPost())
						queryExportWVisa.setNeedPost("Yes");
					else
						queryExportWVisa.setNeedPost("No");
					queryExportWVisa.setExpressTp(wVisa.getExpressTp());
					queryExportWVisa.setExpressNo(wVisa.getExpressNo());
					if(wVisa.getExpDate() != null){
						queryExportWVisa.setExpDate(sdf.format(wVisa.getExpDate()));
					}
					if(wVisa.getCreateTime() != null){
						queryExportWVisa.setCreateTime(sdf.format(wVisa.getCreateTime()));
					}
					queryExportWVisas.add(queryExportWVisa);
				}else{
					//System.out.println("此Id没有对应客商:" + wVisa.getWCustomerInfo());
					ExportWVisa queryExportWVisa = new ExportWVisa();
					queryExportWVisa.setCustomer("找不到对应客商");
					queryExportWVisa.setPassportName(wVisa.getFullPassportName());
					queryExportWVisa.setPassportNo(wVisa.getPassportNo());
					if(wVisa.getDateOfBirth() != null){
						queryExportWVisa.setDateOfBirth(sdf.format(wVisa.getDateOfBirth()));
					}
					queryExportWVisa.setNationality(wVisa.getNationality());
					queryExportWVisa.setHotelAddress(wVisa.getHotel());
					if ("male".equalsIgnoreCase(wVisa.getGender()))
						queryExportWVisa.setGender("Mr.");
					else if ("female".equalsIgnoreCase(wVisa.getGender()))
						queryExportWVisa.setGender("Miss");
					queryExportWVisa.setPosition("");
					queryExportWVisa.setCompany("");
					queryExportWVisa.setAddress("");
					queryExportWVisa.setChineseEmbassy(wVisa.getChineseEmbassy());
					if(wVisa.getDurationBeginTime() != null){
						queryExportWVisa.setDurationBeginTime(sdf.format(wVisa.getDurationBeginTime()));
					}
					if(wVisa.getDurationEndTime() != null){
						queryExportWVisa.setDurationEndTime(sdf.format(wVisa.getDurationEndTime()));
					}
					queryExportWVisa.setEmail("");
					queryExportWVisa.setWebsite("");
					queryExportWVisa.setTelephone("");
					queryExportWVisa.setFax("");
					if(wVisa.getNeedPost() != null && wVisa.getNeedPost())
						queryExportWVisa.setNeedPost("Yes");
					else
						queryExportWVisa.setNeedPost("No");
					queryExportWVisa.setExpressTp(wVisa.getExpressTp());
					queryExportWVisa.setExpressNo(wVisa.getExpressNo());
					if(wVisa.getExpDate() != null){
						queryExportWVisa.setExpDate(sdf.format(wVisa.getExpDate()));
					}
					if(wVisa.getCreateTime() != null){
						queryExportWVisa.setCreateTime(sdf.format(wVisa.getCreateTime()));
					}
					queryExportWVisas.add(queryExportWVisa);
				}
			}
		}
		return queryExportWVisas;
	}
}
