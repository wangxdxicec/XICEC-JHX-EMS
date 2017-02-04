package com.zhenhappy.ems.manager.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zhenhappy.ems.dao.TeaCustomerInfoDao;
import com.zhenhappy.ems.entity.TTeaVisitorInfo;
import com.zhenhappy.ems.manager.dto.ModifyCustomerInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhenhappy.ems.dao.CustomerInfoDao;
import com.zhenhappy.ems.entity.WCustomer;
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
	private TeaCustomerInfoDao customerInfoDao;
	@Autowired
	private HibernateTemplate hibernateTemplate;

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
        Page page = new Page();
        page.setPageSize(request.getRows());
        page.setPageIndex(request.getPage());
        List<WCustomer> customers = customerInfoDao.queryPageByHQL("select count(*) from WCustomer", "from WCustomer", new Object[]{}, page);
        QueryCustomerResponse response = new QueryCustomerResponse();
        response.setResultCode(0);
        response.setRows(customers);
        response.setTotal(page.getTotalCount());
        return response;
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
	 * 分页获取客商列表
	 * @param request
	 * @return
	 */
	public QueryCustomerResponse queryInlandCustomersByPage(QueryCustomerRequest request) {
		List<String> conditions = new ArrayList<String>();
		try {
			if (StringUtils.isNotEmpty(request.getFirstName())) {
				conditions.add(" (e.firstName like '%" + request.getFirstName() + "%' OR e.firstName like '%" + new String(request.getFirstName().getBytes("ISO-8859-1"),"GBK") + "%' OR e.firstName like '%" + new String(request.getFirstName().getBytes("ISO-8859-1"),"utf-8") + "%') ");
			}
			if (StringUtils.isNotEmpty(request.getCompany())) {
				conditions.add(" (e.company like '%" + request.getCompany() + "%' OR e.company like '%" + new String(request.getCompany().getBytes("ISO-8859-1"),"GBK") + "%' OR e.company like '%" + new String(request.getCompany().getBytes("ISO-8859-1"),"utf-8") + "%') ");
			}
			if (StringUtils.isNotEmpty(request.getCity())) {
				conditions.add(" (e.city like '%" + request.getCity() + "%' OR e.city like '%" + new String(request.getCity().getBytes("ISO-8859-1"),"GBK") + "%' OR e.city like '%" + new String(request.getCity().getBytes("ISO-8859-1"),"utf-8") + "%') ");
			}
			if (request.getCountry() != null) {
				conditions.add(" e.country = " + request.getCountry().intValue() + " ");
			}
			if (StringUtils.isNotEmpty(request.getAddress())) {
				conditions.add(" (e.address like '%" + request.getAddress() + "%' OR e.address like '%" + new String(request.getAddress().getBytes("ISO-8859-1"),"GBK") + "%' OR e.address like '%" + new String(request.getAddress().getBytes("ISO-8859-1"),"utf-8") + "%') ");
			}
			if (StringUtils.isNotEmpty(request.getMobilePhone())) {
				conditions.add(" e.mobilePhone like '%" + new String(request.getMobilePhone().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (request.getTelephone() != null) {
				conditions.add(" e.telephone like '%" + new String(request.getTelephone().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (request.getCreateTime() != null) {
				conditions.add(" e.createdTime like '%" + new String(request.getCreateTime().getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (StringUtils.isNotEmpty(request.getEmail())) {
				conditions.add(" (e.email like '%" + request.getEmail() + "%' OR e.email like '%" + new String(request.getEmail().getBytes("ISO-8859-1"),"GBK") + "%' OR e.email like '%" + new String(request.getEmail().getBytes("ISO-8859-1"),"utf-8") + "%') ");
			}
			if(request.getIsProfessional() != null){
				if (request.getIsProfessional() == 1) {
					conditions.add(" e.isProfessional=1 ");
				} else if(request.getIsProfessional() == 0) {
					conditions.add(" e.isProfessional=0 ");
				}
			}
			/*if(request.getIsActivated() != null){
				if (request.getIsActivated() == 1) {
					conditions.add(" e.isActivated=1 ");
				} else if(request.getIsActivated() == 0) {
					conditions.add(" e.isActivated=0 ");
				}
			}*/
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(request.getInlandOrForeign() != null) {
			if(request.getInlandOrForeign() == 1) {
				conditions.add("e.country=44 ");
			} else {
				conditions.add("e.country<>44 ");
			}
		}
		String conditionsSql = StringUtils.join(conditions, " and ");
		String conditionsSqlNoOrder = "";
		if(StringUtils.isNotEmpty(conditionsSql)){
			conditionsSqlNoOrder = " where " + conditionsSql;
		}
		String conditionsSqlOrder = "";
		if(StringUtils.isNotEmpty(conditionsSql)){
			conditionsSqlOrder = " where " + conditionsSql + " order by e.updateTime desc";
		}
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		List<TTeaVisitorInfo> customers = customerInfoDao.queryPageByHQL("select count(*) from TTeaVisitorInfo e" + conditionsSqlNoOrder,
				"select new com.zhenhappy.ems.manager.dto.QueryCustomerInfo(e.id, e.firstName, e.company,"
						+  (request.getInlandOrForeign() == 1 ? "e.city" : "e.country")
						+ ", e.address, e.mobilePhone, e.tel, e.email, e.createTime, e.updateTime, e.isProfessional, e.isDisabled) "
						+ "from TTeaVisitorInfo e"  + conditionsSqlOrder, new Object[]{}, page);
		QueryCustomerResponse response = new QueryCustomerResponse();
		response.setResultCode(0);
		response.setRows(customers);
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
		if (StringUtils.isNotEmpty(request.getUpdateTime())) {
			conditions.add(" (updateTime like '%" + new String(request.getUpdateTime().getBytes("ISO-8859-1"),"GBK") + "%' OR updateTime like '%" + new String(request.getUpdateTime().getBytes("ISO-8859-1"),"utf-8") + "%') ");
		}
		/*if(request.getIsActivated() != null){
			if (request.getIsActivated() == 1) {
				conditions.add(" isActivated=1 ");
			} else if(request.getIsActivated() == 0) {
				conditions.add(" isActivated=0 ");
			}
		}*/
		if(request.getInlandOrForeign() != null) {
			if(request.getInlandOrForeign() == 1) {
				conditions.add("e.country=44 ");
			} else {
				conditions.add("e.country<>44 ");
			}
		}

		String conditionsSql = StringUtils.join(conditions, " and ");
		String conditionsSqlNoOrder = "";
		if(StringUtils.isNotEmpty(conditionsSql)){
			conditionsSqlNoOrder = " where " + conditionsSql;
		}
		String conditionsSqlOrder = "";
		if(StringUtils.isNotEmpty(conditionsSql)){
			conditionsSqlOrder = " where " + conditionsSql + " order by e.updateTime desc";
		}
		/*String conditionsSql = StringUtils.join(conditions, " and ");
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
		}*/
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		List<TTeaVisitorInfo> customers = customerInfoDao.queryPageByHQL("select count(*) from TTeaVisitorInfo e" + conditionsSqlNoOrder,
				"select new com.zhenhappy.ems.manager.dto.QueryCustomerInfo(e.id, e.firstName, e.company,"
						+  (request.getInlandOrForeign() == 1 ? "e.city" : "e.country")
						+ ", e.address, e.mobilePhone, e.tel, e.email, e.createTime, e.updateTime, e.isProfessional, e.isDisabled) "
						+ "from TTeaVisitorInfo e"  + conditionsSqlOrder, new Object[]{}, page);
		QueryCustomerResponse response = new QueryCustomerResponse();
		response.setResultCode(0);
		response.setRows(customers);
		response.setTotal(page.getTotalCount());
		return response;
	}

	/**
	 * 根据id查询客商基本信息
	 * @param id
	 * @return
	 */
	@Transactional
	public TTeaVisitorInfo loadCustomerInfoById(Integer id) {
		TTeaVisitorInfo customerInfo = customerInfoDao.query(id);
		return customerInfo;
	}

	/**
	 * 根据邮箱查询客商
	 * @param email
	 * @return
	 */
	@Transactional
	public List<TTeaVisitorInfo> loadCustomerByEmail(String email) {
		List<TTeaVisitorInfo> wCustomers = customerInfoDao.queryByHql("from TTeaVisitorInfo where email=?", new Object[]{email});
		return wCustomers.size() > 0 ? wCustomers : null;
	}

	/**
	 * 修改客商账号
	 * @param request
	 * @param adminId
	 * @throws Exception
	 */
	@Transactional
	public void modifyCustomerAccount(ModifyCustomerInfo request, Integer adminId) throws Exception {
		TTeaVisitorInfo customer = customerInfoDao.query(request.getId());
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
	 * 激活或注销客商信息
	 * @param request
	 * @throws Exception
	 */
	@Transactional
	public void setCustomerActiveOrUnactive(QueryCustomerRequest request, Integer id) throws Exception {
		TTeaVisitorInfo customer = customerInfoDao.query(id);
		if(customer != null){
			customer.setIsDisabled(!customer.getIsDisabled());
			customer.setUpdateTime(new Date());
			customerInfoDao.update(customer);
		}
	}

	/**
	 * 修改客商是否专业
	 * @param request
	 * @throws Exception
	 */
	@Transactional
	public void modifyCustomerProfessional(QueryCustomerRequest request, Integer id) throws Exception {
		TTeaVisitorInfo customer = customerInfoDao.query(id);
		if(customer != null){
			if(null == customer.getIsProfessional() || customer.getIsProfessional() == 0) {
				customer.setIsProfessional(1);
			} else {
				customer.setIsProfessional(0);
			}
			customer.setUpdateTime(new Date());
			customerInfoDao.update(customer);
		}
	}

	/**
	 * 查询国内客商基本信息
	 * @return
	 */
	@Transactional
	public List<TTeaVisitorInfo> loadAllInlandCustomer() {
		List<TTeaVisitorInfo> customers = customerInfoDao.queryByHql("from TTeaVisitorInfo where country = 44 order by updateTime desc", new Object[]{});
		return customers.size() > 0 ? customers : null;
	}

	/**
	 * 根据eids查询展商列表
	 * @return
	 */
	@Transactional
	public List<TTeaVisitorInfo> loadSelectedCustomers(Integer[] ids) {
		List<TTeaVisitorInfo> customers = customerInfoDao.loadTeaCustomersByIds(ids);
		return customers;
	}

	/**
	 * 更新客商邮件数量
	 * @throws Exception
	 */
	@Transactional
	public void updateCustomerEmailNum(Integer id) throws Exception {
		TTeaVisitorInfo customer = customerInfoDao.query(id);
		int oldEmailNum = customer.getSendEmailNum();
		if(customer != null){
			customer.setUpdateTime(new Date());
			customer.setSendEmailTime(new Date());
			customer.setSendEmailNum(oldEmailNum+1);
			customerInfoDao.update(customer);
		}
	}

	/**
	 * 更新客商短信数量
	 * @throws Exception
	 */
	@Transactional
	public void updateCustomerMsgNum(Integer id) throws Exception {
		TTeaVisitorInfo customer = customerInfoDao.query(id);
		int oldMsgNum = customer.getSendMsgNum();
		if(customer != null){
			customer.setSendMsgTime(new Date());
			customer.setSendMsgNum(oldMsgNum+1);
			customerInfoDao.update(customer);
		}
	}

	/**
	 * 查询国外客商基本信息
	 * @return
	 */
	@Transactional
	public List<TTeaVisitorInfo> loadAllForeignCustomer() {
		List<TTeaVisitorInfo> customers = customerInfoDao.queryByHql("from TTeaVisitorInfo where country <> 44 order by updateTime desc", new Object[]{});
		return customers.size() > 0 ? customers : null;
	}

	/**
	 * 根据手机号查询客商
	 * @param phone
	 * @return
	 */
	@Transactional
	public List<TTeaVisitorInfo> loadCustomerByPhone(String phone) {
		List<TTeaVisitorInfo> wCustomers = customerInfoDao.queryByHql("from TTeaVisitorInfo where mobilePhone=?", new Object[]{phone});
		return wCustomers.size() > 0 ? wCustomers : null;
	}

	/**
	 * 根据年度和时间查询客商基本信息
	 * @return
	 */
	@Transactional
	public List<TTeaVisitorInfo> loadCustomerByYearOrTime(QueryCustomerRequest request, String fieldTime, Integer inlandOrForeign) {
		List<String> conditions = new ArrayList<String>();
		if(inlandOrForeign == 1){
			conditions.add(" country = 44 ");
		}else {
			conditions.add(" country <> 44 ");
		}
		String conditionsSql = StringUtils.join(conditions, " and ");
		String conditionsSqlNoOrder = "";
		if(StringUtils.isNotEmpty(conditionsSql)){
			conditionsSqlNoOrder = " where " + conditionsSql;
		}

		String sql = "from TTeaVisitorInfo w " + conditionsSqlNoOrder + "order by " + (Integer.parseInt(fieldTime) == 0 ?"w.createTime": "w.updateTime") + " desc";
		List<TTeaVisitorInfo> customers = customerInfoDao.queryByHql(sql, new Object[]{});
		return customers.size() > 0 ? customers : null;
	}
}
