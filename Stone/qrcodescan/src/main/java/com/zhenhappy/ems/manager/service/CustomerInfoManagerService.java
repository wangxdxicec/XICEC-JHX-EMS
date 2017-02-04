package com.zhenhappy.ems.manager.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.zhenhappy.ems.dto.QueryEmailOrMsgRequest;
import com.zhenhappy.ems.entity.*;
import com.zhenhappy.ems.manager.dao.CustomerSurveyDao;
import com.zhenhappy.ems.manager.dto.ModifyCustomerInfo;
import com.zhenhappy.ems.manager.entity.TVisitor_Info_Survey;
import com.zhenhappy.ems.service.CountryProvinceService;
import com.zhenhappy.util.EmailPattern;
import com.zhenhappy.util.report.EchartMapResponse;
import com.zhenhappy.util.report.JsonDataForReport;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhenhappy.ems.dao.CustomerInfoDao;
import com.zhenhappy.ems.manager.dto.QueryCustomerRequest;
import com.zhenhappy.ems.manager.dto.QueryCustomerResponse;
import com.zhenhappy.ems.manager.exception.DuplicateCustomerException;
import com.zhenhappy.util.Page;

/**
 * Created by wujianbin on 2014-08-11.
 */
@Service
public class CustomerInfoManagerService {
	@Autowired
	private CustomerInfoDao customerInfoDao;
	@Autowired
	private CustomerSurveyDao customerSurveyDao;
	@Autowired
	private HibernateTemplate hibernateTemplate;
	@Autowired
	private CountryProvinceService countryProvinceService;

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	/**
	 * 分页获取客商列表
	 * @param request
	 * @return
	 */
	public QueryCustomerResponse queryCustomersByPage(QueryCustomerRequest request) {
		List<String> conditions = new ArrayList<String>();
		try {
			if (StringUtils.isNotEmpty(request.getFirstName())) {
				conditions.add(" e.firstName like '%" + new String(request.getFirstName().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (StringUtils.isNotEmpty(request.getCompany())) {
				conditions.add(" e.company like '%" +new String(request.getCompany().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (StringUtils.isNotEmpty(request.getCity())) {
				conditions.add(" e.city like '%" + new String(request.getCity().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (request.getCountry() != null) {
				conditions.add(" e.country = " + request.getCountry().intValue() + " ");
			}
			if (StringUtils.isNotEmpty(request.getAddress())) {
				conditions.add(" e.address like '%" +new String(request.getAddress().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (StringUtils.isNotEmpty(request.getMobilePhone())) {
				conditions.add(" e.mobilePhone like '%" + new String(request.getMobilePhone().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (request.getTelephone() != null) {
				conditions.add(" e.telephone like '%" + new String(request.getTelephone().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (request.getCreateTime() != null) {
				conditions.add(" e.createTime like '%" + new String(request.getCreateTime().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (request.getEmail() != null) {
				conditions.add(" e.email like '%" + new String(request.getEmail().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if(request.getIsProfessional() != null){
				if (request.getIsProfessional() == 1) {
					conditions.add(" e.isProfessional=1 ");
				} else if(request.getIsProfessional() == 0) {
					conditions.add(" e.isProfessional=0 ");
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(request.getInlandOrForeign() == 1) {
			conditions.add("e.country=44 ");
		} else {
			conditions.add("e.country<>44 ");
		}
		String conditionsSql = StringUtils.join(conditions, " and ");
		String conditionsSqlNoOrder = "";
		if(StringUtils.isNotEmpty(conditionsSql)){
			conditionsSqlNoOrder = " where " + conditionsSql;
		}
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		List<WCustomer> exhibitors = customerInfoDao.queryPageByHQL("select count(*) from WCustomer e" + conditionsSqlNoOrder,
				"select new com.zhenhappy.ems.manager.dto.QueryCustomerInfo(e.id, e.firstName, e.company,"
						+  (request.getInlandOrForeign() == 1 ? "e.city" : "e.country")
						+ ", e.address, e.mobilePhone, e.telephone, e.email, e.createdTime, e.isProfessional) "
						+ "from WCustomer e"  + conditionsSqlNoOrder, new Object[]{}, page);
		QueryCustomerResponse response = new QueryCustomerResponse();
		response.setResultCode(0);
		response.setRows(exhibitors);
		response.setTotal(page.getTotalCount());
		return response;
	}

	/**
	 * 分页邮件申请记录
	 * @param request
	 * @param flag 1:表示查询邮件  2：表示查询短信
	 * @return
	 */
	public QueryCustomerResponse queryEmailOrMsgApplyByPage(QueryEmailOrMsgRequest request, int flag) {
		List<String> conditions = new ArrayList<String>();
		try {
			if(request.getCustomerID() != null) {
				conditions.add(" e.customerID like '%" + request.getCustomerID().intValue() + "%'");
			}
			if (StringUtils.isNotEmpty(request.getCreateTime())) {
				conditions.add(" e.createTime like '%" + new String(request.getCreateTime().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (StringUtils.isNotEmpty(request.getCreateIP())) {
				conditions.add(" e.createIP like '%" + new String(request.getCreateIP().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (StringUtils.isNotEmpty(request.getConfirmTime())) {
				conditions.add(" e.confirmTime like '%" + new String(request.getConfirmTime().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (StringUtils.isNotEmpty(request.getConfirmIP())) {
				conditions.add(" e.confirmIP like '%" + new String(request.getConfirmIP().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if(request.getStatus() != null) {
				conditions.add(" e.status '%" + request.getStatus().intValue() + "%'");
			}
			if (StringUtils.isNotEmpty(request.getAdmin())) {
				conditions.add(" e.admin like '%" + new String(request.getAdmin().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String conditionsSql = StringUtils.join(conditions, " and ");
		String conditionsSqlNoOrder = "";
		if(StringUtils.isNotEmpty(conditionsSql)){
			conditionsSqlNoOrder = " where " + conditionsSql;
		}
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		if(flag == 1){
			List<VApplyEmail> exhibitors = customerInfoDao.queryPageByHQL("select count(*) from VApplyEmail e" + conditionsSqlNoOrder,
					"select new com.zhenhappy.ems.dto.QueryEmailOrMsgInfo(e.customerID, e.createTime, e.createIP, e.confirmTime, " +
							"e.confirmIP, e.status, e.admin) from VApplyEmail e"  + conditionsSqlNoOrder, new Object[]{}, page);
			QueryCustomerResponse response = new QueryCustomerResponse();
			response.setResultCode(0);
			response.setRows(exhibitors);
			response.setTotal(page.getTotalCount());
			return response;
		} else {
			List<VApplyMsg> exhibitors = customerInfoDao.queryPageByHQL("select count(*) from VApplyMsg e" + conditionsSqlNoOrder,
					"select new com.zhenhappy.ems.dto.QueryEmailOrMsgInfo(e.customerID, e.createTime, e.createIP, e.confirmTime, " +
							"e.confirmIP, e.status, e.admin) from VApplyMsg e"  + conditionsSqlNoOrder, new Object[]{}, page);
			QueryCustomerResponse response = new QueryCustomerResponse();
			response.setResultCode(0);
			response.setRows(exhibitors);
			response.setTotal(page.getTotalCount());
			return response;
		}
	}

	/**
	 * 分页获取国内客商列表
	 * @param request
	 * @return
	 */
	public QueryCustomerResponse queryInlandCustomersByPage(QueryCustomerRequest request) {
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		List<WCustomer> customers = customerInfoDao.queryPageByHQL("select count(*) from WCustomer where country = 44", "from WCustomer where country = 44 order by updateTime DESC", new Object[]{}, page);
		QueryCustomerResponse response = new QueryCustomerResponse();
		response.setResultCode(0);
		response.setRows(customers);
		response.setTotal(page.getTotalCount());
		return response;
	}

	/**
	 * 分页获取国内客商列表用于报表
	 * @param request
	 * @return
	 */
	public EchartMapResponse queryExhibitorForReport(QueryCustomerRequest request, Integer flag) {
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());

		EchartMapResponse mapResponse = new EchartMapResponse();

		List<JsonDataForReport> jsonList = new ArrayList<JsonDataForReport>();
		List<String> provinceList = new ArrayList<String>();
		if(flag == 0){
			mapResponse.setLegend("国内展商");
			List<WProvince> provinces = countryProvinceService.loadAllProvince();
			if(provinces != null && provinces.size()>0){
				for (int i=0;i<provinces.size();i++){
					WProvince province = provinces.get(i);
					provinceList.add(province.getChineseName());
					List<TExhibitor> groups = getHibernateTemplate().find("from TExhibitor where province = ?", new Object[]{province.getId()});
					int provinceCount = groups.size();
					JsonDataForReport json = new JsonDataForReport();
					json.setName(province.getChineseName());
					json.setValue(provinceCount);
					jsonList.add(json);
				}
			}
		} else{
			mapResponse.setLegend("全球展商");
			List<WCountry> countries = countryProvinceService.loadAllCountry();
			if(countries != null && countries.size()>0){
				for (int i=0;i<countries.size();i++){
					WCountry country = countries.get(i);
					provinceList.add(country.getChineseName());
					List<TExhibitor> groups = getHibernateTemplate().find("from TExhibitor where country = ?", new Object[]{country.getId()});
					int provinceCount = groups.size();
					JsonDataForReport json = new JsonDataForReport();
					json.setName(country.getChineseName());
					json.setValue(provinceCount);
					jsonList.add(json);
				}
			}
		}

		mapResponse.setCategory(provinceList);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, Boolean.TRUE);
		JSONArray jsonArray = JSONArray.fromObject(jsonList);
		System.out.println("--result: " + jsonArray.toString());
		mapResponse.setData(jsonArray.toString());
		mapResponse.setResultCode(0);
		return mapResponse;
	}

	/**
	 * 分页获取邮件申请列表
	 * @param request
	 * @return
	 */
	public QueryCustomerResponse loadEmailApplyByPage(QueryCustomerRequest request) {
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		EmailPattern pattern = new EmailPattern();
		List<VApplyEmail> customers = customerInfoDao.queryPageByHQL("select count(*) from VApplyEmail", "from VApplyEmail where Status = 0 order by CreateTime DESC", new Object[]{}, page);
		List<VApplyEmail> customerList = new ArrayList<VApplyEmail>();
		if(customers != null && customers.size()>0){
			for(int k=0;k<customers.size();k++){
				VApplyEmail applyEmail = customers.get(k);
				if(applyEmail != null){
					WCustomer customer = loadCustomerInfoById(applyEmail.getCustomerID());
					if(pattern.isEmailPattern(customer.getEmail())){
						customerList.add(applyEmail);
					}
				}
			}
		}
		QueryCustomerResponse response = new QueryCustomerResponse();
		response.setResultCode(0);
		response.setRows(customers);
		response.setTotal(page.getTotalCount());
		return response;
	}

	/**
	 * 分页获取短信申请列表
	 * @param request
	 * @return
	 */
	public QueryCustomerResponse loadMsgApplyByPage(QueryCustomerRequest request) {
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		EmailPattern pattern = new EmailPattern();
		List<VApplyMsg> customers = customerInfoDao.queryPageByHQL("select count(*) from VApplyMsg", "from VApplyMsg  where Status = 0 order by CreateTime DESC", new Object[]{}, page);
		List<VApplyMsg> customerList = new ArrayList<VApplyMsg>();
		if(customers != null && customers.size()>0){
			for(int k=0;k<customers.size();k++){
				VApplyMsg applyMsg = customers.get(k);
				if(applyMsg != null){
					WCustomer customer = loadCustomerInfoById(applyMsg.getCustomerID());
					if(pattern.isMobileNO(customer.getMobilePhone())){
						customerList.add(applyMsg);
					}
				}
			}
		}
		QueryCustomerResponse response = new QueryCustomerResponse();
		response.setResultCode(0);
		response.setRows(customerList);
		response.setTotal(page.getTotalCount());
		return response;
	}

	/**
	 * 分页获取国外客商列表
	 * @param request
	 * @return
	 */
	public QueryCustomerResponse queryForeignCustomersByPage(QueryCustomerRequest request) throws UnsupportedEncodingException {
		List<String> conditions = new ArrayList<String>();
		if (StringUtils.isNotEmpty(request.getFirstName())) {
			conditions.add(" (firstName like '%" + new String(request.getFirstName().getBytes("ISO-8859-1"),"GBK") + "%' OR firstName like '%" + new String(request.getFirstName().getBytes("ISO-8859-1"),"utf-8") + "%') ");
		}
		if (StringUtils.isNotEmpty(request.getCompany())) {
			conditions.add(" (company like '%" + new String(request.getCompany().getBytes("ISO-8859-1"),"GBK") + "%' OR company like '%" + new String(request.getCompany().getBytes("ISO-8859-1"),"utf-8") + "%') ");
		}
		if (request.getCountry() != null) {
			conditions.add(" country = " + request.getCountry().intValue() + " ");
		}
		if (StringUtils.isNotEmpty(request.getAddress())) {
			conditions.add(" (address like '%" + new String(request.getAddress().getBytes("ISO-8859-1"),"GBK") + "%' OR address like '%" + new String(request.getAddress().getBytes("ISO-8859-1"),"utf-8") + "%') ");
		}
		if (StringUtils.isNotEmpty(request.getMobilePhone())) {
			conditions.add(" (mobilePhone like '%" + new String(request.getMobilePhone().getBytes("ISO-8859-1"),"GBK") + "%' OR mobilePhone like '%" + new String(request.getMobilePhone().getBytes("ISO-8859-1"),"utf-8") + "%') ");
		}
		if (StringUtils.isNotEmpty(request.getTelephone())) {
			conditions.add(" (telephone like '%" + new String(request.getTelephone().getBytes("ISO-8859-1"),"GBK") + "%' OR telephone like '%" + new String(request.getTelephone().getBytes("ISO-8859-1"),"utf-8") + "%') ");
		}
		if (StringUtils.isNotEmpty(request.getEmail())) {
			conditions.add(" (email like '%" + new String(request.getEmail().getBytes("ISO-8859-1"),"GBK") + "%' OR email like '%" + new String(request.getEmail().getBytes("ISO-8859-1"),"utf-8") + "%') ");
		}
		if (StringUtils.isNotEmpty(request.getCreateTime())) {
			conditions.add(" (createdTime like '%" + new String(request.getCreateTime().getBytes("ISO-8859-1"),"GBK") + "%' OR createdTime like '%" + new String(request.getCreateTime().getBytes("ISO-8859-1"),"utf-8") + "%') ");
		}
		String conditionsSql = StringUtils.join(conditions, " and ");
		String conditionsSqlNoOrder = "";
		if(StringUtils.isNotEmpty(conditionsSql)){
			conditionsSqlNoOrder = " and " + conditionsSql;
			conditionsSql = " and " + conditionsSql;
		}
		String conditionsSqlOrder = "";
		if(StringUtils.isNotEmpty(request.getSort()) && StringUtils.isNotEmpty(request.getOrder())){
			conditionsSqlOrder = conditionsSql + " order by " + request.getSort() + " " + request.getOrder() + " ";
		}else{
			conditionsSqlOrder = conditionsSql + " order by updateTime DESC ";
		}
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		List<WCustomer> customers = customerInfoDao.queryPageByHQL("select count(*) from WCustomer where country <> 44" + conditionsSqlNoOrder, "from WCustomer where country <> 44" + conditionsSqlOrder, new Object[]{}, page);
		QueryCustomerResponse response = new QueryCustomerResponse();
		response.setResultCode(0);
		response.setRows(customers);
		response.setTotal(page.getTotalCount());
		return response;
	}

	/**
	 * 查询国内客商基本信息
	 * @return
	 */
	@Transactional
	public List<WCustomer> loadAllInlandCustomer() {
		List<WCustomer> customers = customerInfoDao.queryByHql("from WCustomer where country = 44 order by createdTime desc", new Object[]{});
		return customers.size() > 0 ? customers : null;
	}

	/**
	 * 查询国外客商基本信息
	 * @return
	 */
	@Transactional
	public List<WCustomer> loadAllForeignCustomer() {
		List<WCustomer> customers = customerInfoDao.queryByHql("from WCustomer where country <> 44 order by createdTime desc", new Object[]{});
		return customers.size() > 0 ? customers : null;
	}

	/**
	 * 根据id查询客商基本信息
	 * @param id
	 * @return
	 */
	@Transactional
	public WCustomer loadCustomerInfoById(Integer id) {
		WCustomer customerInfo = customerInfoDao.query(id);
		return customerInfo;
	}

	/**
	 * 根据eids查询展商列表
	 * @return
	 */
	@Transactional
	public List<WCustomer> loadSelectedCustomers(Integer[] ids) {
		List<WCustomer> customers = customerInfoDao.loadCustomersByIds(ids);
		return customers;
	}

	/**
	 * 添加客商账号
	 * 
	 * @param customer
	 * @throws DuplicateCustomerException
	 */
	@Transactional
	public void addCustomer(WCustomer customer) throws DuplicateCustomerException {
		getHibernateTemplate().save(customer);
	}
	
	/**
	 * 修改客商账号
	 * @param customer
	 */
    @Transactional
    public void modifyCustomer(WCustomer customer) {
        getHibernateTemplate().update(customer);
    }

	/**
	 * 修改客商账号
	 * @param request
	 * @param adminId
	 * @throws Exception
	 */
	@Transactional
	public void modifyCustomerAccount(ModifyCustomerInfo request, Integer adminId) throws Exception {
		WCustomer customer = customerInfoDao.query(request.getId());
		if(customer != null){
			customer.setFirstName(request.getUsername());
			customer.setPassword(request.getPassword());
			customer.setCompany(request.getCompany());
			customer.setAddress(request.getAddress());
			customer.setEmail(request.getEmail());
			customer.setMobilePhone(request.getMobilePhone());
			customer.setPosition(request.getPosition());
			customer.setFax(request.getFax());
			customer.setWebsite(request.getWebsite());
			customer.setUpdateTime(new Date());
			customerInfoDao.update(customer);
		}
	}

	/**
	 * 根据邮箱查询客商
	 * @param email
	 * @return
	 */
	@Transactional
	public List<WCustomer> loadCustomerByEmail(String email) {
		List<WCustomer> wCustomers = customerInfoDao.queryByHql("from WCustomer where email=?", new Object[]{email});
		return wCustomers.size() > 0 ? wCustomers : null;
	}

	/**
	 * 修改客商是否专业
	 * @param request
	 * @throws Exception
	 */
	@Transactional
	public void modifyCustomerProfessional(QueryCustomerRequest request, Integer id) throws Exception {
		WCustomer customer = customerInfoDao.query(id);
		if(customer != null){
			if(customer.getIsProfessional() == 1) {
				customer.setIsProfessional(0);
			} else {
				customer.setIsProfessional(1);
			}
			customerInfoDao.update(customer);
		}
	}

	/**
	 * 更新客商短信数量
	 * @throws Exception
	 */
	@Transactional
	public void updateCustomerMsgNum(Integer id) throws Exception {
		WCustomer customer = customerInfoDao.query(id);
		int oldMsgNum = customer.getSendMsgNum();
		if(customer != null){
			customer.setSendMsgNum(oldMsgNum+1);
			customerInfoDao.update(customer);
		}
	}

	/**
	 * 更新客商邮件数量
	 * @throws Exception
	 */
	@Transactional
	public void updateCustomerEmailNum(Integer id) throws Exception {
		WCustomer customer = customerInfoDao.query(id);
		int oldEmailNum = customer.getSendEmailNum();
		if(customer != null){
			customer.setSendEmailDate(new Date());
			customer.setSendEmailNum(oldEmailNum+1);
			customerInfoDao.update(customer);
		}
	}

	/**
	 * 根据id查询客商问卷调查
	 * @param id
	 * @return
	 */
	@Transactional
	public TVisitor_Info_Survey loadCustomerSurveyInfoById(Integer id) {
		List<TVisitor_Info_Survey> customerSurvey = customerSurveyDao.queryByHql("from TVisitor_Info_Survey where CustomerID=? order by UpdateTime desc", new Object[]{id});
		return customerSurvey.size() > 0 ? customerSurvey.get(0) : null;
	}

	public static void main(String[] args) throws IOException {
		String[] wscList = "GLOBAL FORUM OF MASTER ARCHITECTS;LAUNCH OUT @Xiamen Stone Fair – NEW QUARRYCOLLECTION 2016;LAUNCH OUT @Xiamen Stone Fair – MECHANICAL INNOVATION 2016;STONE DESIGN DAY;EDUCATIONAL SESSIONS".split("–");
		if(wscList != null && wscList.length>0){
			StringBuffer reslut = new StringBuffer();
			for (int i=0; i<wscList.length;i++){
				reslut.append(wscList[i] + "        ");
			}
			System.out.println(reslut.toString());
		}
	}

	/**
	 * 查询所有客商问卷调查
	 * @return
	 */
	@Transactional
	public List<TVisitor_Info_Survey> loadAllCustomerSurvey() {
		List<TVisitor_Info_Survey> customerSurvey = customerSurveyDao.queryByHql("from TVisitor_Info_Survey order by createdTime desc", new Object[]{});
		return customerSurvey.size() > 0 ? customerSurvey : null;
	}
}
