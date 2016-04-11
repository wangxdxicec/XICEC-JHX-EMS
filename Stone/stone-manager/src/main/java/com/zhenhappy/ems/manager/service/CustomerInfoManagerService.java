package com.zhenhappy.ems.manager.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zhenhappy.ems.manager.dto.ModifyCustomerInfo;
import com.zhenhappy.ems.manager.exception.DuplicateUsernameException;
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
	private CustomerInfoDao customerInfoDao;
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
						+ ", e.address, e.mobilePhone, e.telephone, e.email, e.createdTime) "
						+ "from WCustomer e"  + conditionsSqlNoOrder, new Object[]{}, page);
		QueryCustomerResponse response = new QueryCustomerResponse();
		response.setResultCode(0);
		response.setRows(exhibitors);
		response.setTotal(page.getTotalCount());
		return response;
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
	 * 修改客商是否专业
	 * @param request
	 * @throws Exception
	 */
	@Transactional
	public void modifyCustomerProfessional(QueryCustomerRequest request, Integer id) throws Exception {
		WCustomer customer = customerInfoDao.query(id);
		if(customer != null){
			customer.setIsProfessional(!customer.getIsProfessional());
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
			customer.setSendEmailNum(oldEmailNum+1);
			customerInfoDao.update(customer);
		}
	}
}
