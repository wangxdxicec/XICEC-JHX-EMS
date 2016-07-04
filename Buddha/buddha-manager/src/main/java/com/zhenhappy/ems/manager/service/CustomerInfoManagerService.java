package com.zhenhappy.ems.manager.service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zhenhappy.ems.dao.VisitorInfoDao;
import com.zhenhappy.ems.entity.TVisitorInfo;
import com.zhenhappy.ems.manager.dto.QueryExhibitor;
import com.zhenhappy.ems.manager.dto.QueryVisitorInfo;
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
	private VisitorInfoDao visitorInfoDao;
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
				conditions.add(" e.firstName like '%" + new String(request.getFirstName().replace(",","").getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (StringUtils.isNotEmpty(request.getCompany())) {
				conditions.add(" e.company like '%" +new String(request.getCompany().replace(",","").getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (StringUtils.isNotEmpty(request.getCity())) {
				conditions.add(" e.city like '%" + new String(request.getCity().replace(",","").getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (request.getCountry() != null) {
				conditions.add(" e.country = " + request.getCountry().intValue() + " ");
			}
			if (StringUtils.isNotEmpty(request.getAddress())) {
				conditions.add(" e.address like '%" +new String(request.getAddress().replace(",","").getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (StringUtils.isNotEmpty(request.getMobile())) {
				conditions.add(" e.mobilePhone like '%" + new String(request.getMobile().replace(",","").getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (request.getTel() != null) {
				conditions.add(" e.tel like '%" + new String(request.getTel().replace(",","").getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (request.getCreateTime() != null) {
				conditions.add(" e.createTime like '%" + new String(request.getCreateTime().replace(",","").getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
			if (request.getEmail() != null) {
				conditions.add(" e.email like '%" + new String(request.getEmail().replace(",","").getBytes("ISO-8859-1"),"utf-8") + "%'");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(request.getInlandOrForeign() == 1) {
			conditions.add(" e.country=44 ");
		} else {
			conditions.add(" e.country<>44 ");
		}
		if(request.getSourceValue() != null){
			if(request.getSourceValue() == 1){
				conditions.add(" e.isMobile = 1 ");
			} else if(request.getSourceValue() == 0){
				conditions.add(" e.isMobile = 0 ");
			}
		}
		if(request.getIsRabbicFlag() == 1) {
			conditions.add(" e.rabbi = 1 ");
		} else {
			conditions.add(" (e.rabbi is null or e.rabbi = 0) ");
		}
		if(request.getPre() != null && 1==request.getPre()){
			conditions.add(" convert(varchar, e.createTime,120) >= '2016-04-25 00:00:00' and convert(varchar, e.createTime,120) <= '2016-10-18 23:59:59' ");
		}
		String conditionsSql = StringUtils.join(conditions, " and ");
		String conditionsSqlNoOrder = "";
		if(StringUtils.isNotEmpty(conditionsSql)){
			conditionsSqlNoOrder = " where " + conditionsSql;
		}

		conditionsSql = StringUtils.join(conditions, " and ");
		String conditionsSqlOrder = "";
		if(StringUtils.isNotEmpty(request.getSort()) && StringUtils.isNotEmpty(request.getOrder())){
			if(!"null".equals(request.getOrder())){
				if("boothNumber".equals(request.getSort())){
					conditionsSqlOrder = " where " + conditionsSql + " order by b." + request.getSort() + " " + request.getOrder() + " ";
				}else{
					//conditionsSqlOrder = " where " + conditionsSql + " order by e." + request.getSort() + " " + request.getOrder() + " ";
					//排序空值任何情况下都最后
					conditionsSqlOrder = " where " + conditionsSql + " order by case when (e." + request.getSort() + "='' or e." + request.getSort() + " is null) then 1 else 0 end , e." + request.getSort() + " " + request.getOrder() + " ";
				}
			}else{
				conditionsSqlOrder = " where " + conditionsSql + " order by e.updateTime desc ";
			}
		}else{
			conditionsSqlOrder = " where " + conditionsSql + " order by e.updateTime desc ";
		}

        Page page = new Page();
        page.setPageSize(request.getRows());
        page.setPageIndex(request.getPage());
		List<QueryVisitorInfo> visitorInfos = visitorInfoDao.queryPageByHQL("select count(*) from TVisitorInfo e" + conditionsSqlNoOrder,
				"select new com.zhenhappy.ems.manager.dto.QueryVisitorInfo(e.id, e.firstName, e.company,"
						+  (request.getInlandOrForeign() == 1 ? "e.city" : "e.country")
						+ ", e.address, e.mobilePhone, e.tel, e.email, e.createTime, e.govement, e.rabbi, e.isMobile) "
						+ "from TVisitorInfo e"  + conditionsSqlOrder, new Object[]{}, page);
		for(QueryVisitorInfo queryVisitorInfo:visitorInfos){
			boolean sendEmailFlag = isBetweenBeginDateAndEndDate(queryVisitorInfo.getSendEmailTime(), "2016-4-25", "2016-10-24");
			if(sendEmailFlag){
				queryVisitorInfo.setSendEmailFlag(1);
			}else{
				queryVisitorInfo.setSendEmailFlag(0);
			}
			boolean sendMsgFlag = isBetweenBeginDateAndEndDate(queryVisitorInfo.getSendMsgDate(), "2016-4-25", "2016-10-24");
			if(sendMsgFlag){
				queryVisitorInfo.setSendMsgFlag(1);
			}else{
				queryVisitorInfo.setSendMsgFlag(0);
			}
		}
		QueryCustomerResponse response = new QueryCustomerResponse();
        response.setResultCode(0);
        response.setRows(visitorInfos);
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

	//==================佛事展新增需求=================
	/**
	 * 分页获取国内客商列表
	 * @param request
	 * @return
	 */
	public QueryCustomerResponse queryInlandCustomersByPage(QueryCustomerRequest request) {
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		List<QueryVisitorInfo> customers = visitorInfoDao.queryPageByHQL("select count(*) from TVisitorInfo e where country = 44 and (rabbi = 0  or rabbi is null)",
				"select new com.zhenhappy.ems.manager.dto.QueryVisitorInfo(e.id,  e.email, e.checkingNo,e.password, e.firstName, e.lastName, e.sex," +
						"e.company, e.position, e.country, e.province,e.city, e.address, e.backupEmail,e.mobileCode, e.mobilePhone, e.telCode," +
						"e.tel,e.telCode2, e.faxCode,e.fax, e.faxCode2, e.website, e.industry,e.industryOther, e.remark," +
						"e.createIp, e.createTime,e.updateIp,e.updateTime, e.sendEmailNum,e.sendEmailTime, e.sendMsgNum,e.sendMsgTime," +
						"e.langFlag,e.visitDate,e.beenToFair,e.beenToRole,e.isRecieveEmail,e.isRecieveDoc,e.isMobile,e.isjudged,e.isProfessional,e.isAccommodation," +
						"e.isDisabled, e.isReaded,e.tmp_Country,e.tmp_Postcode,e.tmp_Interest,e.tmp_InterestOthers," +
						"e.tmp_Knowfrom,e.tmp_KnowfromOthers,e.tmp_V_name1,e.tmp_V_title1, e.tmp_V_position1,e.tmp_V_contact1,e.tmp_V_name2,e.tmp_V_title2," +
						"e.tmp_V_position2,e.tmp_V_contact2,e.tmp_V_name3,e.tmp_V_title3,e.tmp_V_position3,e.tmp_V_contact3,e.guid, e.govement, e.rabbi) "
						+ "from TVisitorInfo e where country = 44 and (rabbi = 0  or rabbi is null) " +
						(1==request.getPre()?" and convert(varchar, e.createTime,120) >= '2016-04-25 00:00:00' and convert(varchar, e.createTime,120) <= '2016-10-18 23:59:59' ":" ")
						+ " order by e.updateTime desc ", new Object[]{}, page);
		for(QueryVisitorInfo queryVisitorInfo:customers){
			boolean sendEmailFlag = isBetweenBeginDateAndEndDate(queryVisitorInfo.getSendEmailTime(), "2016-4-25", "2016-10-24");
			if(sendEmailFlag){
				queryVisitorInfo.setSendEmailFlag(1);
			}else{
				queryVisitorInfo.setSendEmailFlag(0);
			}
			boolean sendMsgFlag = isBetweenBeginDateAndEndDate(queryVisitorInfo.getSendMsgDate(), "2016-4-25", "2016-10-24");
			if(sendMsgFlag){
				queryVisitorInfo.setSendMsgFlag(1);
			}else{
				queryVisitorInfo.setSendMsgFlag(0);
			}
		}
		QueryCustomerResponse response = new QueryCustomerResponse();
		response.setResultCode(0);
		response.setRows(customers);
		response.setTotal(page.getTotalCount());
		return response;
	}

	public boolean isBetweenBeginDateAndEndDate(Date cur, String star,String end){
		SimpleDateFormat localTime = new SimpleDateFormat("yyyy-MM-dd");
		boolean flag = false;
		try{
			if(cur != null){
				Date sdate = localTime.parse(star);
				Date edate = localTime.parse(end);
				if(cur.getTime() >= sdate.getTime() && cur.getTime() <= edate.getTime()){
					flag = true;
				} else {
					flag = false;
				}
			}else{
				flag = false;
			}
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}

	/**
	 * 分页获取国内法师列表
	 * @param request
	 * @return
	 */
	public QueryCustomerResponse queryInlandRabbiCustomersByPage(QueryCustomerRequest request) {
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		List<QueryVisitorInfo> customers = visitorInfoDao.queryPageByHQL("select count(*) from TVisitorInfo e where country = 44 and rabbi = 1 ",
				"select new com.zhenhappy.ems.manager.dto.QueryVisitorInfo(e.id,  e.email, e.checkingNo,e.password, e.firstName, e.lastName, e.sex," +
						"e.company, e.position, e.country, e.province,e.city, e.address, e.backupEmail,e.mobileCode, e.mobilePhone, e.telCode," +
						"e.tel,e.telCode2, e.faxCode,e.fax, e.faxCode2, e.website, e.industry,e.industryOther, e.remark," +
						"e.createIp, e.createTime,e.updateIp,e.updateTime, e.sendEmailNum,e.sendEmailTime, e.sendMsgNum,e.sendMsgTime," +
						"e.langFlag,e.visitDate,e.beenToFair,e.beenToRole,e.isRecieveEmail,e.isRecieveDoc,e.isMobile,e.isjudged,e.isProfessional,e.isAccommodation," +
						"e.isDisabled, e.isReaded,e.tmp_Country,e.tmp_Postcode,e.tmp_Interest,e.tmp_InterestOthers," +
						"e.tmp_Knowfrom,e.tmp_KnowfromOthers,e.tmp_V_name1,e.tmp_V_title1, e.tmp_V_position1,e.tmp_V_contact1,e.tmp_V_name2,e.tmp_V_title2," +
						"e.tmp_V_position2,e.tmp_V_contact2,e.tmp_V_name3,e.tmp_V_title3,e.tmp_V_position3,e.tmp_V_contact3,e.guid, e.govement, e.rabbi) "
						+ "from TVisitorInfo e where country = 44 and rabbi = 1 order by e.updateTime desc ", new Object[]{}, page);
		for(QueryVisitorInfo queryVisitorInfo:customers){
			boolean sendEmailFlag = isBetweenBeginDateAndEndDate(queryVisitorInfo.getSendEmailTime(), "2016-4-25", "2016-10-24");
			if(sendEmailFlag){
				queryVisitorInfo.setSendEmailFlag(1);
			}else{
				queryVisitorInfo.setSendEmailFlag(0);
			}
			boolean sendMsgFlag = isBetweenBeginDateAndEndDate(queryVisitorInfo.getSendMsgDate(), "2016-4-25", "2016-10-24");
			if(sendMsgFlag){
				queryVisitorInfo.setSendMsgFlag(1);
			}else{
				queryVisitorInfo.setSendMsgFlag(0);
			}
		}
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
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		List<QueryVisitorInfo> customers = visitorInfoDao.queryPageByHQL("select count(*) from TVisitorInfo e where country <> 44 and (rabbi = 0  or rabbi is null)",
				"select new com.zhenhappy.ems.manager.dto.QueryVisitorInfo(e.id,  e.email, e.checkingNo,e.password, e.firstName, e.lastName, e.sex," +
						"e.company, e.position, e.country, e.province,e.city, e.address, e.backupEmail,e.mobileCode, e.mobilePhone, e.telCode," +
						"e.tel,e.telCode2, e.faxCode,e.fax, e.faxCode2, e.website, e.industry,e.industryOther, e.remark," +
						"e.createIp, e.createTime,e.updateIp,e.updateTime, e.sendEmailNum,e.sendEmailTime, e.sendMsgNum,e.sendMsgTime," +
						"e.langFlag,e.visitDate,e.beenToFair,e.beenToRole,e.isRecieveEmail,e.isRecieveDoc,e.isMobile,e.isjudged,e.isProfessional,e.isAccommodation," +
						"e.isDisabled, e.isReaded,e.tmp_Country,e.tmp_Postcode,e.tmp_Interest,e.tmp_InterestOthers," +
						"e.tmp_Knowfrom,e.tmp_KnowfromOthers,e.tmp_V_name1,e.tmp_V_title1, e.tmp_V_position1,e.tmp_V_contact1,e.tmp_V_name2,e.tmp_V_title2," +
						"e.tmp_V_position2,e.tmp_V_contact2,e.tmp_V_name3,e.tmp_V_title3,e.tmp_V_position3,e.tmp_V_contact3,e.guid, e.govement, e.rabbi) "
						+ "from TVisitorInfo e where country <> 44 and (rabbi = 0  or rabbi is null)" +
						(1==request.getPre()?" and convert(varchar, e.createTime,120) >= '2016-04-25 00:00:00' and convert(varchar, e.createTime,120) <= '2016-10-18 23:59:59' ":" ")
						+ " order by e.updateTime desc ", new Object[]{}, page);
		for(QueryVisitorInfo queryVisitorInfo:customers){
			boolean sendEmailFlag = isBetweenBeginDateAndEndDate(queryVisitorInfo.getSendEmailTime(), "2016-4-25", "2016-10-24");
			if(sendEmailFlag){
				queryVisitorInfo.setSendEmailFlag(1);
			}else{
				queryVisitorInfo.setSendEmailFlag(0);
			}
			boolean sendMsgFlag = isBetweenBeginDateAndEndDate(queryVisitorInfo.getSendMsgDate(), "2016-4-25", "2016-10-24");
			if(sendMsgFlag){
				queryVisitorInfo.setSendMsgFlag(1);
			}else{
				queryVisitorInfo.setSendMsgFlag(0);
			}
		}
		QueryCustomerResponse response = new QueryCustomerResponse();
		response.setResultCode(0);
		response.setRows(customers);
		response.setTotal(page.getTotalCount());
		return response;
	}

	/**
	 * 分页获取国外法师列表
	 * @param request
	 * @return
	 */
	public QueryCustomerResponse queryForeignRabbiCustomersByPage(QueryCustomerRequest request) throws UnsupportedEncodingException {
		Page page = new Page();
		page.setPageSize(request.getRows());
		page.setPageIndex(request.getPage());
		List<QueryVisitorInfo> customers = visitorInfoDao.queryPageByHQL("select count(*) from TVisitorInfo e where country <> 44 and rabbi = 1 ",
				"select new com.zhenhappy.ems.manager.dto.QueryVisitorInfo(e.id,  e.email, e.checkingNo,e.password, e.firstName, e.lastName, e.sex," +
						"e.company, e.position, e.country, e.province,e.city, e.address, e.backupEmail,e.mobileCode, e.mobilePhone, e.telCode," +
						"e.tel,e.telCode2, e.faxCode,e.fax, e.faxCode2, e.website, e.industry,e.industryOther, e.remark," +
						"e.createIp, e.createTime,e.updateIp,e.updateTime, e.sendEmailNum,e.sendEmailTime, e.sendMsgNum,e.sendMsgTime," +
						"e.langFlag,e.visitDate,e.beenToFair,e.beenToRole,e.isRecieveEmail,e.isRecieveDoc,e.isMobile,e.isjudged,e.isProfessional,e.isAccommodation," +
						"e.isDisabled, e.isReaded,e.tmp_Country,e.tmp_Postcode,e.tmp_Interest,e.tmp_InterestOthers," +
						"e.tmp_Knowfrom,e.tmp_KnowfromOthers,e.tmp_V_name1,e.tmp_V_title1, e.tmp_V_position1,e.tmp_V_contact1,e.tmp_V_name2,e.tmp_V_title2," +
						"e.tmp_V_position2,e.tmp_V_contact2,e.tmp_V_name3,e.tmp_V_title3,e.tmp_V_position3,e.tmp_V_contact3,e.guid, e.govement, e.rabbi) "
						+ "from TVisitorInfo e where country <> 44 and rabbi = 1 order by e.updateTime desc ", new Object[]{}, page);
		for(QueryVisitorInfo queryVisitorInfo:customers){
			boolean sendEmailFlag = isBetweenBeginDateAndEndDate(queryVisitorInfo.getSendEmailTime(), "2016-4-25", "2016-10-24");
			if(sendEmailFlag){
				queryVisitorInfo.setSendEmailFlag(1);
			}else{
				queryVisitorInfo.setSendEmailFlag(0);
			}
			boolean sendMsgFlag = isBetweenBeginDateAndEndDate(queryVisitorInfo.getSendMsgDate(), "2016-4-25", "2016-10-24");
			if(sendMsgFlag){
				queryVisitorInfo.setSendMsgFlag(1);
			}else{
				queryVisitorInfo.setSendMsgFlag(0);
			}
		}
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
	public List<TVisitorInfo> loadAllInlandCustomer() {
		List<TVisitorInfo> customers = visitorInfoDao.queryByHql("from TVisitorInfo where country = 44 order by createTime asc", new Object[]{});
		return customers.size() > 0 ? customers : null;
	}

	/**
	 * 查询法师基本信息
	 * @param inlandOrForeignflag 1：表示境内；2：表示境外;
	 * @param rabbiFlag 0：表示普通客商；品：表示法师;
	 * @return
	 */
	@Transactional
	public List<TVisitorInfo> loadAllRabbiCustomer(int inlandOrForeignflag, int rabbiFlag) {
		String hql = "select new com.zhenhappy.ems.entity.TVisitorInfo(e.id,  e.email, e.checkingNo,e.password, e.firstName, e.lastName, e.sex," +
				"e.company, e.position, e.country, e.province,e.city, e.address, e.backupEmail,e.mobileCode, e.mobilePhone, e.telCode," +
				"e.tel,e.telCode2, e.faxCode,e.fax, e.faxCode2, e.website, e.industry,e.industryOther, e.remark," +
				"e.createIp, e.createTime,e.updateIp,e.updateTime, e.sendEmailNum,e.sendEmailTime, e.sendMsgNum,e.sendMsgTime," +
				"e.langFlag,e.visitDate,e.beenToFair,e.beenToRole,e.isRecieveEmail,e.isRecieveDoc,e.isMobile,e.isjudged,e.isProfessional,e.isAccommodation," +
				"e.isDisabled, e.isReaded,e.tmp_Country,e.tmp_Postcode,e.tmp_Interest,e.tmp_InterestOthers," +
				"e.tmp_Knowfrom,e.tmp_KnowfromOthers,e.tmp_V_name1,e.tmp_V_title1, e.tmp_V_position1,e.tmp_V_contact1,e.tmp_V_name2,e.tmp_V_title2," +
				"e.tmp_V_position2,e.tmp_V_contact2,e.tmp_V_name3,e.tmp_V_title3,e.tmp_V_position3,e.tmp_V_contact3,e.guid, e.govement, e.rabbi) "
				+ "from TVisitorInfo e where country " + (inlandOrForeignflag == 1? "=":"<>") + " 44 and rabbi " + (rabbiFlag == 1?"= 1":"= 0 or rabbi is null");
		List<TVisitorInfo> customers = visitorInfoDao.queryByHql(hql, new Object[]{});
		return customers.size() > 0 ? customers : null;
	}

	/**
	 * 查询国外客商基本信息
	 * @return
	 */
	@Transactional
	public List<TVisitorInfo> loadAllForeignCustomer() {
		List<TVisitorInfo> customers = visitorInfoDao.queryByHql("from TVisitorInfo where country <> 44 order by createTime asc", new Object[]{});
		return customers.size() > 0 ? customers : null;
	}

	/**
	 * 根据eids查询展商列表
	 * @return
	 */
	@Transactional
	public List<TVisitorInfo> loadSelectedCustomers(Integer[] ids, int rabbiFlag) {
		List<TVisitorInfo> customers = visitorInfoDao.loadCustomersByIds(ids, rabbiFlag);
		return customers;
	}

	/**
	 * 根据id查询客商基本信息
	 * @param id
	 * @return
	 */
	@Transactional
	public TVisitorInfo loadCustomerInfoById(Integer id) {
		String hql = "select new com.zhenhappy.ems.entity.TVisitorInfo(e.id,  e.email, e.checkingNo,e.password, e.firstName, e.lastName, e.sex," +
				"e.company, e.position, e.country, e.province,e.city, e.address, e.backupEmail,e.mobileCode, e.mobilePhone, e.telCode," +
				"e.tel,e.telCode2, e.faxCode,e.fax, e.faxCode2, e.website, e.industry,e.industryOther, e.remark," +
				"e.createIp, e.createTime,e.updateIp,e.updateTime, e.sendEmailNum,e.sendEmailTime, e.sendMsgNum,e.sendMsgTime," +
				"e.langFlag,e.visitDate,e.beenToFair,e.beenToRole,e.isRecieveEmail,e.isRecieveDoc,e.isMobile,e.isjudged,e.isProfessional,e.isAccommodation," +
				"e.isDisabled, e.isReaded,e.tmp_Country,e.tmp_Postcode,e.tmp_Interest,e.tmp_InterestOthers," +
				"e.tmp_Knowfrom,e.tmp_KnowfromOthers,e.tmp_V_name1,e.tmp_V_title1, e.tmp_V_position1,e.tmp_V_contact1,e.tmp_V_name2,e.tmp_V_title2," +
				"e.tmp_V_position2,e.tmp_V_contact2,e.tmp_V_name3,e.tmp_V_title3,e.tmp_V_position3,e.tmp_V_contact3,e.guid, e.govement, e.rabbi) "
				+ "from TVisitorInfo e where id=" + id;
		List<TVisitorInfo> customers = visitorInfoDao.queryByHql(hql, new Object[]{});
		return customers.size() > 0 ? customers.get(0) : null;
	}

	/**
	 * 修改客商是否专业
	 * @param request
	 * @throws Exception
	 */
	@Transactional
	public void modifyCustomerProfessional(QueryCustomerRequest request, Integer id, Integer type) throws Exception {
		TVisitorInfo customer = loadCustomerInfoById(id);
		if(customer != null){
			if(type == 1){
				customer.setIsProfessional(1);
			}else {
				customer.setIsProfessional(0);
				if(type == 0){
					customer.setGovement(0);
				}else if(type == 2){
					customer.setGovement(1);
				}
			}
			visitorInfoDao.update(customer);
		}
	}

	/**
	 * 普通客商与法师相互转化
	 * @param request
	 * @throws Exception
	 */
	@Transactional
	public void modifyCustomerNormalOrRabbi(QueryCustomerRequest request, Integer id) throws Exception {
		TVisitorInfo customer = loadCustomerInfoById(id);
		if(customer != null){
			if("1".equals(customer.getRabbi())){
				customer.setRabbi("0");
			}else {
				customer.setRabbi("1");
			}
			visitorInfoDao.update(customer);
		}
	}
}
