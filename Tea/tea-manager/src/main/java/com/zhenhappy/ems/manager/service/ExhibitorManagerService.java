package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.dao.ExhibitorClassDao;
import com.zhenhappy.ems.dao.ExhibitorInfoDao;
import com.zhenhappy.ems.dao.TInvoiceApplyDao;
import com.zhenhappy.ems.dao.TeaExhibitorDao;
import com.zhenhappy.ems.entity.*;
import com.zhenhappy.ems.manager.dao.ExhibitorBoothDao;
import com.zhenhappy.ems.manager.dto.*;
import com.zhenhappy.ems.manager.entity.TExhibitorBooth;
import com.zhenhappy.ems.manager.entity.TExhibitorTerm;
import com.zhenhappy.ems.manager.exception.DuplicateUsernameException;
import com.zhenhappy.ems.manager.util.JChineseConvertor;
import com.zhenhappy.ems.service.ExhibitorService;
import com.zhenhappy.ems.service.InvoiceService;
import com.zhenhappy.ems.service.MeipaiService;
import com.zhenhappy.util.Page;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wujianbin on 2014-04-22.
 */
@Service
public class ExhibitorManagerService extends ExhibitorService {
	
	private static Logger log = Logger.getLogger(ExhibitorManagerService.class);
	
    @Autowired
    private TeaExhibitorDao exhibitorDao;
    @Autowired
    private ExhibitorInfoDao exhibitorInfoDao;
    @Autowired
    private ExhibitorClassDao exhibitorClassDao;
    @Autowired
    private ContactManagerService contactService;
    @Autowired
    private JoinerManagerService joinerManagerService;
    @Autowired
    private TermManagerService termManagerService;
    @Autowired
    private MeipaiService meipaiService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private ProductManagerService productManagerService;
    @Autowired
    private ExhibitorBoothDao exhibitorBoothDao;
    @Autowired
    private TInvoiceApplyDao invoiceApplyDao;

    /**
     * 分页获取展商列表
     * @param request
     * @return
     * @throws UnsupportedEncodingException 
     */
	@Transactional
    public QueryExhibitorResponse queryExhibitorsByPage(QueryExhibitorRequest request) throws UnsupportedEncodingException {
		List<String> conditions = new ArrayList<String>();
		if (request.getTag() != null) {
			conditions.add(" e.tag = " + request.getTag().intValue() + " ");
        }
		if (request.getGroup() != null) {
			conditions.add(" e.group = " + request.getGroup().intValue() + " ");
        }
		if (StringUtils.isNotEmpty(request.getBoothNumber())) {
			conditions.add(" e.eid in (select eid from TExhibitorBooth where boothNumber like '%" + new String(request.getBoothNumber().getBytes("ISO-8859-1"),"GBK") + "%') ");
        }
        if (StringUtils.isNotEmpty(request.getCompany())) {
        	conditions.add(" (i.company like '%" + new String(request.getCompany().getBytes("ISO-8859-1"),"GBK") + "%' OR i.company like '%" + new String(request.getCompany().getBytes("ISO-8859-1"),"utf-8") + "%' OR i.company like '%" + request.getCompany() + "%' ) ");
        }
        if (StringUtils.isNotEmpty(request.getCompanye())) {
            conditions.add(" (i.companyEn like '%" + new String(request.getCompanye().getBytes("ISO-8859-1"),"GBK") + "%' OR i.companyEn like '%" + new String(request.getCompanye().getBytes("ISO-8859-1"),"utf-8") + "%' OR i.companyEn like '%" + request.getCompanye() + "%' ) ");
        }
        if (request.getArea() != null) {
			conditions.add(" e.area = " + request.getArea().intValue() + " ");
        }
        if (request.getCountry() != null) {
        	conditions.add(" e.country = " + request.getCountry().intValue() + " ");
        }
        if (request.getProvince() != null) {
        	conditions.add(" e.province = " + request.getProvince().intValue() + " ");
        }
        if (request.getIsLogout() != null) {
            conditions.add(" e.isLogout = " + request.getIsLogout().intValue() + " ");
        }

        if(request.getType() == 1){
            conditions.add(" e.isLogout = 0 ");
        }

        String conditionsSql = StringUtils.join(conditions, " and ");
        String conditionsSqlNoOrder = "";
        if(StringUtils.isNotEmpty(conditionsSql)){
        	conditionsSqlNoOrder = " where " + conditionsSql;
        }
        conditions.add(" e.eid=b.eid and e.eid=i.eid");
        conditionsSql = StringUtils.join(conditions, " and ");
        String conditionsSqlOrder = "";
        if(StringUtils.isNotEmpty(request.getSort()) && StringUtils.isNotEmpty(request.getOrder())){
        	if("boothNumber".equals(request.getSort())){
        		conditionsSqlOrder = " where " + conditionsSql + " order by b." + request.getSort() + " " + request.getOrder() + " ";
        	}else if ("company".equals(request.getSort()) || "companyEn".equals(request.getSort())) {
                conditionsSqlOrder = " where " + conditionsSql + " order by i." + request.getSort() + " " + request.getOrder() + " ";
            }else{
        		conditionsSqlOrder = " where " + conditionsSql + " order by e." + request.getSort() + " " + request.getOrder() + " ";
        	}
        }else{
        	conditionsSqlOrder = " where " + conditionsSql + " order by e.createTime DESC ";
        }
        Page page = new Page();
        page.setPageSize(request.getRows());
        page.setPageIndex(request.getPage());
        if(StringUtils.isNotEmpty(conditionsSqlNoOrder)) {
            conditionsSqlNoOrder = conditionsSqlNoOrder + " and e.eid=b.eid and e.eid=i.eid";
        } else {
            conditionsSqlNoOrder = " where e.eid=b.eid and e.eid=i.eid";
        }
        List<QueryExhibitor> exhibitors = exhibitorDao.queryPageByHQL("select count(*) from TeaExhibitor e, TExhibitorBooth b, TExhibitorInfo i " + conditionsSqlNoOrder,
        		"select new com.zhenhappy.ems.manager.dto.QueryExhibitor(e.eid, e.username, e.password, e.area, i.company, i.companyEn, e.country, e.province, e.isLogout, e.isLogin, e.tag, e.group, b.boothNumber, b.exhibitionArea) "
        		+ "from TeaExhibitor e, TExhibitorBooth b, TExhibitorInfo i " + conditionsSqlOrder, new Object[]{}, page);
        for(QueryExhibitor exhibitor:exhibitors){
        	TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(exhibitor.getEid());
    		if(exhibitorInfo != null){
    			if(StringUtils.isNotEmpty(exhibitorInfo.getEmail()) && (StringUtils.isNotEmpty(exhibitorInfo.getAddress())
                        || StringUtils.isNotEmpty(exhibitorInfo.getAddressEn()))) {
                    exhibitor.setInfoFlag(1);
                } else {
                    exhibitor.setInfoFlag(0);
                }
                if(null != exhibitor.getIsLogin() && exhibitor.getIsLogin() == 0){
                    exhibitor.setInfoFlag(5);
                }
    		}else{
    			exhibitor.setInfoFlag(0);
    		}
        }
        QueryExhibitorResponse response = new QueryExhibitorResponse();
        response.setResultCode(0);
        response.setRows(exhibitors);
        response.setTotal(page.getTotalCount());
        return response;
    }
	
    /**
     * 查询所有展商列表
     * @return
     */
    @Transactional
    public List<TeaExhibitor> loadAllExhibitors() {
        List<TeaExhibitor> exhibitors = exhibitorDao.query();
        return exhibitors.size() > 0 ? exhibitors : null;
    }

    /**
     * 查询所有展商列表
     * @return
     */
    @Transactional
    public List<TeaExhibitor> loadAllExhibitorsByLogType(Integer type) {
        List<TeaExhibitor> exhibitors = exhibitorDao.loadAllExhibitorsByLogType(type);
        return exhibitors.size() > 0 ? exhibitors : null;
    }

    /**
     * 根据eids查询展商列表
     * @return
     */
    @Transactional
    public List<TeaExhibitor> loadSelectedExhibitors(Integer[] eids) {
//        List<TExhibitor> exhibitors = exhibitorDao.loadExhibitorsByEids(eids);
//		List<TExhibitor> exhibitors = exhibitorDao.queryByHql("from TExhibitor where isLogout = 0 && eid=?", new Object[]{ eids });
//        return exhibitors.size() > 0 ? exhibitors : null;
        List<TeaExhibitor> exhibitors = new ArrayList<TeaExhibitor>();
        for (Integer eid:eids){
            TeaExhibitor exhibitor = loadTeaExhibitorByEid(eid);
            if (exhibitor != null) exhibitors.add(exhibitor);
        }
        return exhibitors.size() > 0 ? exhibitors : null;
    }
    
    /**
     * 根据eid查询展商
     * @param eid
     * @return
     */
    @Transactional
    public TeaExhibitor loadTeaExhibitorByEid(Integer eid) {
        return exhibitorDao.query(eid);
    }
    
    /**
     * 通过展团查询展商
     * @param groupId
     */
    @Transactional
    public List<TeaExhibitor> loadExhibitorByGroupId(Integer groupId) {
    	List<TeaExhibitor> exhibitors = exhibitorDao.queryByHql("from TeaExhibitor where group=?", new Object[]{groupId});
    	return exhibitors.size() > 0 ? exhibitors : null;
    }
    
    /**
     * 根据username查询展商
     * @param username
     * @param exceptEid 排除指定的eid
     * @return
     */
    @Transactional
    public TeaExhibitor loadAllExhibitorByUsername(String username, Integer exceptEid) {
    	if(StringUtils.isNotEmpty(username)){
			List<TeaExhibitor> exhibitors = new ArrayList<TeaExhibitor>();
			if(exceptEid != null){
				exhibitors = getHibernateTemplate().find("from TeaExhibitor where username = ? and eid <> ?", new Object[]{username, exceptEid.intValue()});
			}else{
				exhibitors = getHibernateTemplate().find("from TeaExhibitor where username = ?", new Object[]{username});
			}
    		return exhibitors.size() > 0 ? exhibitors.get(0) : null;
    	}else return null;
    }
    
    /**
     * 根据company查询展商
     * @param company
     * @return
     */
    @Transactional
    public TExhibitorInfo loadAllExhibitorByCompany(String company) {
		if(StringUtils.isNotEmpty(company)){
			List<TExhibitorInfo> exhibitors = getHibernateTemplate().find("from TExhibitorInfo where company = ?", new Object[]{company});
    		return exhibitors.size() > 0 ? exhibitors.get(0) : null;
    	}else return null;
    }

    /**
     * 根据username查询展商
     * @param username
     * @return
     */
    @Transactional
    public TeaExhibitor loadAllExhibitorByUserName(String username) {
        if(StringUtils.isNotEmpty(username)){
            List<TeaExhibitor> exhibitors = getHibernateTemplate().find("from TeaExhibitor where username = ?", new Object[]{username});
            return exhibitors.size() > 0 ? exhibitors.get(0) : null;
        }else return null;
    }
    
    /**
     * 根据company查询展商
     * @param company
     * @param exceptEid 排除指定的eid
     * @return
     */
    @Transactional
    public TExhibitorInfo loadAllExhibitorByCompany(String company, Integer exceptEid) {
		if(StringUtils.isNotEmpty(company)){
			List<TExhibitorInfo> exhibitors = new ArrayList<TExhibitorInfo>();
			if(exceptEid != null){
				exhibitors = getHibernateTemplate().find("from TExhibitorInfo where company = ? and eid <> ?", new Object[]{company, exceptEid.intValue()});
			}else{
				exhibitors = getHibernateTemplate().find("from TExhibitorInfo where company = ?", new Object[]{company});
			}
    		return exhibitors.size() > 0 ? exhibitors.get(0) : null;
    	}else return null;
    }
    
    /**
     * 根据companye查询展商
     * @param companye
     * @return
     */
    @Transactional
    public TExhibitorInfo loadAllExhibitorByCompanye(String companye) {
    	if(StringUtils.isNotEmpty(companye)){
			List<TExhibitorInfo> exhibitors = getHibernateTemplate().find("from TExhibitorInfo where companyEn = ?", new Object[]{companye});
    		return exhibitors.size() > 0 ? exhibitors.get(0) : null;
    	}else return null;
    }

    /**
     * 根据用户名查询展商
     * @param username
     * @return
     */
    @Transactional
    public TeaExhibitor loadAllExhibitorByUsername(String username) {
        List<TeaExhibitor> exhibitors = new ArrayList<TeaExhibitor>();
        if(StringUtils.isNotEmpty(username)){
            exhibitors = getHibernateTemplate().find("from TeaExhibitor where username = ?", new Object[]{username});
        }
        return exhibitors.size() > 0 ? exhibitors.get(0) : null;
    }
    
    /**
     * 根据companye查询展商
     * @param companye
     * @param exceptEid 排除指定的eid
     * @return
     */
    @Transactional
    public TExhibitorInfo loadAllExhibitorByCompanye(String companye, Integer exceptEid) {
    	if(StringUtils.isNotEmpty(companye)){
			List<TExhibitorInfo> exhibitors = new ArrayList<TExhibitorInfo>();
			if(exceptEid != null){
				exhibitors = getHibernateTemplate().find("from TeaExhibitor where companye = ? and eid <> ?", new Object[]{companye, exceptEid.intValue()});
			}else{
				exhibitors = getHibernateTemplate().find("from TeaExhibitor where companye = ?", new Object[]{companye});
			}
    		return exhibitors.size() > 0 ? exhibitors.get(0) : null;
    	}else return null;
    }
    
    /**
     * 根据eid查询展商基本信息
     * @param eid
     * @return
     */
    @Transactional
    public TExhibitorInfo loadExhibitorInfoByEid(Integer eid) {
    	if(eid != null){
    		List<TExhibitorInfo> exhibitorInfo = exhibitorInfoDao.queryByHql("from TExhibitorInfo where eid=?", new Object[]{eid});
        	return exhibitorInfo.size() > 0 ? exhibitorInfo.get(0) : null;
    	}else return null;
    }
    
    /**
     * 根据展商列表查询展商基本信息列表
     * @param exhibitors
     * @return
     */
    @Transactional
    public List<TExhibitorInfo> loadExhibitorInfoByExhibitors(List<TeaExhibitor> exhibitors) {
    	if(exhibitors != null){
    		List<TExhibitorInfo> exhibitorInfos = new ArrayList<TExhibitorInfo>();
        	for(TeaExhibitor exhibitor:exhibitors){
        		TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(exhibitor.getEid());
        		if(exhibitorInfo != null){
        			/*exhibitorInfo.setCompany(exhibitor.getCompany());
            		exhibitorInfo.setCompanyEn(exhibitor.getCompanye());*/
            		exhibitorInfos.add(exhibitorInfo);
        		}
        	}
        	return exhibitorInfos.size() > 0 ? exhibitorInfos : null;
    	}else return null;
    }
    
    /**
     * 根据eids删除商基本信息
     * @param eids
     */
    @Transactional
    public void deleteExhibitorInfoByEid(Integer[] eids) {
    	if(eids.length > 0){
	    	for(Integer eid:eids){
	    		TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(eid);
	    		if(exhibitorInfo != null) getHibernateTemplate().delete(exhibitorInfo);
	    	}
    	}
    }
    
    /**
     * 根据eids查询所有展位号+楣牌
     * @param eids
     * @return
     */
    @Transactional
    public List<QueryBoothNumAndMeipai> loadBoothNumAndMeipai(Integer[] eids, Integer type){
    	List<TeaExhibitor> exhibitors = new ArrayList<TeaExhibitor>();
    	if(eids == null)
            exhibitors = loadAllExhibitorsByLogType(type);
    	else
            exhibitors = loadSelectedExhibitors(eids);
    	List<QueryBoothNumAndMeipai> boothNumAndMeipais = new ArrayList<QueryBoothNumAndMeipai>();
		for(TeaExhibitor exhibitor:exhibitors){
			TExhibitorBooth booth = queryBoothByEid(exhibitor.getEid());
			if(booth != null){
				QueryBoothNumAndMeipai boothNumAndMeipai = new QueryBoothNumAndMeipai();
				boothNumAndMeipai.setEid(exhibitor.getEid());
				if(StringUtils.isNotEmpty(booth.getBoothNumber())) boothNumAndMeipai.setBoothNumber(booth.getBoothNumber());
				else boothNumAndMeipai.setBoothNumber("");
				TExhibitorMeipai exhibitorMeipai = meipaiService.getMeiPaiByEid(exhibitor.getEid());
				if(exhibitorMeipai != null){
					if(StringUtils.isNotEmpty(exhibitorMeipai.getMeipai())) boothNumAndMeipai.setMeipai(exhibitorMeipai.getMeipai());
					else boothNumAndMeipai.setMeipai("");
					if(StringUtils.isNotEmpty(exhibitorMeipai.getMeipaiEn()))  boothNumAndMeipai.setMeipaiEn(exhibitorMeipai.getMeipaiEn());
					else boothNumAndMeipai.setMeipaiEn("");
				}
				if(StringUtils.isEmpty(booth.getBoothNumber()) && StringUtils.isEmpty(exhibitorMeipai.getMeipai()) && StringUtils.isEmpty(exhibitorMeipai.getMeipaiEn())) continue;
				else boothNumAndMeipais.add(boothNumAndMeipai);
			}
		}
		return boothNumAndMeipais;
    }
    
    /**
     * 根据eid查询展会届数
     * @param eid
     * @return
     */
    @Transactional
    public TExhibitorTerm getExhibitorTermByEid(Integer eid) {
    	if(eid != null){
    		List<TExhibitorTerm> terms = getHibernateTemplate().find("from TExhibitorTerm where eid = ?", new Object[]{eid});
    		String current_term = getJdbcTemplate().queryForObject("select value from t_dictionary where keyname='current_term'", String.class);
    		if(terms.size() > 0){
    			for (TExhibitorTerm term:terms) {
    				if(term.getJoinTerm().toString().equals(current_term)) return term;
    			}
    		}else return null;
    	}
   		return null;
    }

    /**
     * 根据eid获取展位信息
     * @param eid
     * @return
     */
    @Transactional
    public TExhibitorBooth queryBoothByEid(Integer eid) {
        List<TExhibitorBooth> booths = getHibernateTemplate().find("from TExhibitorBooth where eid = ?", new Object[]{ eid });
        return booths.size() > 0 ? booths.get(0) : null;
    }
    
    /**
     * 根据展位号查询展位信息
     * @param boothNum
     * @return
     */
    @Transactional
    public TExhibitorBooth queryBoothByBoothNum(String boothNum) {
        List<TExhibitorBooth> booths = getHibernateTemplate().find("from TExhibitorBooth where boothNumber = ?", new Object[]{boothNum});
        return booths.size() > 0 ? booths.get(0) : null;
    }
    
    /**
     * 查询所有展会届数
     * @return
     */
    @Transactional
    public Integer queryCurrentTermNumber() {
        return getJdbcTemplate().queryForObject("select t.value from t_dictionary t where t.keyname = ?", new Object[]{"current_term"}, Integer.class);
    }
    
    /**
     * 根据classId查询产品类型
     * @param classId
     * @return
     */
    @Transactional
    public TExhibitorClass loadExhibitorClassByClassId(Integer classId) {
    	List<TExhibitorClass> exhibitorClasses = exhibitorClassDao.queryByHql("from TExhibitorClass where classId=?", new Object[]{classId});
    	return exhibitorClasses.size()>0?exhibitorClasses.get(0):null;
	}
    
    /**
	 * 根据einfoids查询产品类型
	 * @param einfoids
	 * @return
	 */
	@Transactional
	public List<TExhibitorClass> queryExhibitorClassByClassEinfoids(List<Integer> einfoids) {
    	List<TExhibitorClass> exhibitorClasses = new ArrayList<TExhibitorClass>();
    	for(Integer einfoid:einfoids){
    		List<TExhibitorClass> exhibitorClass = exhibitorClassDao.queryByHql("from TExhibitorClass where einfoId=?", new Object[]{einfoid});
    		exhibitorClasses.addAll(exhibitorClass);
    	}
    	return exhibitorClasses;
	}
	
	/**
     * 根据eid查询产品类型
     * @param eid
     * @return
     */
    @Transactional
    public List<TExhibitorClass> queryExhibitorClassByEid(Integer eid) {
    	List<TExhibitorClass> exhibitorClass = exhibitorClassDao.queryByHql("from TExhibitorClass where einfoId = ?", new Object[]{eid});
    	return exhibitorClass;
    }
    
    /**
     * 根据id查询产品类型
     * @param id
     * @return
     */
    @Transactional
    public TExhibitorClass queryExhibitorClassById(Integer id) {
    	TExhibitorClass exhibitorClass = exhibitorClassDao.query(id);
    	return exhibitorClass;
    }
    
    /**
     * 根据einfoid查询产品类型
     * @param einfoid
     * @return
     */
    @Transactional
    public String queryExhibitorClassByEinfoid(Integer einfoid){
    	List<TExhibitorClass> exhibitorClasses = queryExhibitorClassByEid(einfoid);
    	String productType = "";
    	for(TExhibitorClass exhibitorClass:exhibitorClasses){
    		productType += productManagerService.loadClassNameById(exhibitorClass.getClassId()) + ";";
    	}
    	return productType;
    }
    
	/**
     * 添加展商账号
     * @param request
     * @param adminId
     * @throws Exception
     */
    @Transactional
    public void addExhibitor(AddExhibitorRequest request, Integer adminId) throws Exception {
    	if(StringUtils.isEmpty(request.getCompanyName().trim()) && StringUtils.isEmpty(request.getCompanyNameE().trim())){
    		throw new DuplicateUsernameException("公司中英文名至少要有一项");
    	}
    	if(loadAllExhibitorByCompany(request.getCompanyName().trim()) != null) throw new DuplicateUsernameException("公司中文名重复");
    	if(loadAllExhibitorByCompanye(request.getCompanyNameE().trim()) != null) throw new DuplicateUsernameException("公司英文名重复");
    	if(request.getBoothNumber() != null && !"".equals(request.getBoothNumber())){
    		if(queryBoothByBoothNum(request.getBoothNumber()) != null) throw new DuplicateUsernameException("展位号重复");
    	}
    	TeaExhibitor exhibitor = new TeaExhibitor();
        TExhibitorInfo exhibitorInfo = new TExhibitorInfo();
        exhibitorInfo.setCompany(request.getCompanyName().trim());
        exhibitorInfo.setCompanyEn(request.getCompanyNameE().trim());
        exhibitorInfo.setCompanyT(JChineseConvertor.getInstance().s2t(request.getCompanyName().trim()));
        /*exhibitor.setCompany(request.getCompanyName().trim());
        exhibitor.setCompanye(request.getCompanyNameE().trim());
        exhibitor.setCompanyt(JChineseConvertor.getInstance().s2t(request.getCompanyName().trim()));*/
        exhibitor.setCountry(request.getCountry());
        exhibitor.setProvince(request.getProvince());
        exhibitor.setLevel(request.getLevel());
        exhibitor.setArea(request.getArea());
        if(StringUtils.isNotEmpty(request.getExhibitionArea()))
            exhibitor.setExhibitionArea(request.getExhibitionArea());
        else
            exhibitor.setExhibitionArea("0");
        if(StringUtils.isNotEmpty(request.getUsername())){
        	if(loadAllExhibitorByUsername(request.getUsername()) != null) throw new DuplicateUsernameException("用户名重复");
        	else{
        		exhibitor.setUsername(request.getUsername());
                exhibitor.setPassword(request.getPassword());
        	}
    	}
        exhibitor.setTag(request.getTag());
        exhibitor.setIsLogout(0);
        exhibitor.setCreateUser(adminId);
        exhibitor.setCreateTime(new Date());
        Integer eid = (Integer) getHibernateTemplate().save(exhibitor);
        //---add by wangxd, begin---
        /*exhibitorInfo.setEid(eid);
        getHibernateTemplate().save(exhibitorInfo);*/
        //---add by wangxd, end---
        ModifyExhibitorInfoRequest modifyExhibitorInfoRequest = new ModifyExhibitorInfoRequest();
        modifyExhibitorInfoRequest.setEid(eid);
        /*modifyExhibitorInfoRequest.setCompany(exhibitor.getCompany());
        modifyExhibitorInfoRequest.setCompanyEn(exhibitor.getCompanye());*/
        modifyExhibitorInfoRequest.setCompany(exhibitorInfo.getCompany());
        modifyExhibitorInfoRequest.setCompanyEn(exhibitorInfo.getCompanyEn());
        modifyExhibitorInfo(modifyExhibitorInfoRequest, eid, adminId);
    	if(eid != null){
    		TExhibitorBooth booth = new TExhibitorBooth();
            booth.setBoothNumber(request.getBoothNumber().trim().replace(" ", ""));
            booth.setExhibitionArea(request.getBoothNumber().trim().replace(" ", "").substring(0,1) + "厅");
            booth.setEid(eid);
            booth.setMark("");
            booth.setCreateTime(new Date());
            booth.setCreateUser(adminId);
            bindBoothInfo(booth);
    	}else{
    		throw new Exception();
    	}
    }
    
    /**
     * 修改展商账号
     * @param request
     * @param adminId
     * @throws Exception
     */
    @Transactional
    public void modifyExhibitorAccount(ModifyExhibitorRequest request, Integer adminId) throws Exception {
    	if(StringUtils.isEmpty(request.getCompanyName().trim()) && StringUtils.isEmpty(request.getCompanyNameE().trim())){
    		 throw new DuplicateUsernameException("公司中英文名至少要有一项");
    	}
    	if(loadAllExhibitorByCompany(request.getCompanyName().trim(), request.getEid()) != null) throw new DuplicateUsernameException("公司中文名重复");
    	if(loadAllExhibitorByCompanye(request.getCompanyNameE().trim(), request.getEid()) != null) throw new DuplicateUsernameException("公司英文名重复");
        if(loadAllExhibitorByUsername(request.getUsername()) != null) throw new DuplicateUsernameException("用户名重复");
        TeaExhibitor exhibitor = exhibitorDao.query(request.getEid());
        TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(request.getEid());
    	if(exhibitor != null){
    		if(StringUtils.isNotEmpty(request.getUsername())){
    			if(loadAllExhibitorByUsername(request.getUsername(), request.getEid()) != null) throw new DuplicateUsernameException("用户名重复");
	        	else{
	        		exhibitor.setUsername(request.getUsername());
	                exhibitor.setPassword(request.getPassword());
	        	}
	    	}else{
	    		exhibitor.setUsername(null);
	            exhibitor.setPassword(null);
	    	}
	        exhibitor.setUpdateTime(new Date());
	        exhibitor.setUpdateUser(adminId);
            if(request.getCompanyName() != null && request.getCompanyName().trim() != null){
                exhibitorInfo.setCompany(request.getCompanyName().trim());
            }
            if(request.getCompanyNameE() != null && request.getCompanyNameE().trim() != null) {
                exhibitorInfo.setCompanyEn(request.getCompanyNameE().trim());
            }
	        /*exhibitor.setCompany(request.getCompanyName());
	        exhibitor.setCompanye(request.getCompanyNameE());*/
	        exhibitor.setCountry(request.getCountry());
	        exhibitor.setProvince(request.getProvince());
	        exhibitor.setTag(request.getTag());
            exhibitor.setArea(request.getArea());
            if(StringUtils.isNotEmpty(request.getExhibitionArea())) exhibitor.setExhibitionArea(request.getExhibitionArea());
            else exhibitor.setExhibitionArea("0");
	        exhibitorDao.update(exhibitor);
            exhibitorInfoDao.update(exhibitorInfo);
    	}
    }

    /**
     * 激活展商
     * @param term
     */
    @Transactional
    public void activeExhibitor(TExhibitorTerm term) {
        if (term.getId() != null) {
            TExhibitorTerm saved = getHibernateTemplate().get(TExhibitorTerm.class, term.getId());
            saved.setJoinTerm(term.getJoinTerm());
            saved.setMark(term.getMark());
            saved.setBoothNumber(term.getBoothNumber());
            saved.setUpdateTime(new Date());
            saved.setUpdateUser(term.getUpdateUser());
            getHibernateTemplate().update(saved);
        } else {
            getHibernateTemplate().save(term);
        }
    }

    /**
     * 绑定展位号
     * @param booth
     */
    @Transactional
    public void bindBoothInfo(TExhibitorBooth booth) {
        if (booth.getId() != null) {
            TExhibitorBooth temp = getHibernateTemplate().get(TExhibitorBooth.class, booth.getId());
            temp.setExhibitionArea(booth.getExhibitionArea());
            temp.setBoothNumber(booth.getBoothNumber());
            temp.setMark(booth.getMark());
            temp.setUpdateUser(booth.getUpdateUser());
            temp.setUpdateTime(new Date());
            getHibernateTemplate().update(temp);
        } else {
            getHibernateTemplate().save(booth);
        }
    }

    /**
     * 根据eids批量修改所属人
     * @param eids
     * @param tag
     * @param adminId
     */
    @Transactional
    public void modifyExhibitorsTag(Integer[] eids, Integer tag, Integer adminId) {
    	if(eids != null){
    		List<TeaExhibitor> exhibitors = loadSelectedExhibitors(eids);
            if (exhibitors.size() > 0){
    			for(TeaExhibitor exhibitor:exhibitors){
            		exhibitor.setTag(tag);
            		exhibitor.setUpdateUser(adminId);
            		exhibitor.setUpdateTime(new Date());
            		getHibernateTemplate().update(exhibitor);
            	}
    		}
    	}
    }
    
    /**
     * 根据eids批量修改所属人展团
     * @param eids
     * @param group
     * @param adminId
     */
    @Transactional
    public void modifyExhibitorsGroup(Integer[] eids, Integer group, Integer adminId) {
    	if(eids != null){
    		List<TeaExhibitor> exhibitors = loadSelectedExhibitors(eids);
            if (exhibitors.size() > 0){
    			for(TeaExhibitor exhibitor:exhibitors){
            		exhibitor.setGroup(group);
            		exhibitor.setUpdateUser(adminId);
            		exhibitor.setUpdateTime(new Date());
            		getHibernateTemplate().update(exhibitor);
            	}
    		}
    	}
    }
    
    /**
     * 根据eids批量修改展区
     * @param eids
     * @param area
     * @param adminId
     */
    @Transactional
    public void modifyExhibitorsArea(Integer[] eids, Integer area, Integer adminId) {
    	if(eids != null){
    		List<TeaExhibitor> exhibitors = loadSelectedExhibitors(eids);
            if (exhibitors.size() > 0){
    			for(TeaExhibitor exhibitor:exhibitors){
            		exhibitor.setArea(area);
            		exhibitor.setUpdateUser(adminId);
            		exhibitor.setUpdateTime(new Date());
            		getHibernateTemplate().update(exhibitor);
            	}
    		}
    	}
    }
    
    /**
     * 根据eids批量注销展商
     * @param eids
     * @param adminId
     */
    @Transactional
    public void disableExhibitors(Integer[] eids, Integer adminId) {
    	if(eids != null){
    		List<TeaExhibitor> exhibitors = loadSelectedExhibitors(eids);
            if (exhibitors.size() > 0){
    			for(TeaExhibitor exhibitor:exhibitors){
            		exhibitor.setIsLogout(1);
            		exhibitor.setUpdateUser(adminId);
            		exhibitor.setUpdateTime(new Date());
            		getHibernateTemplate().update(exhibitor);
            	}
    		}
    	}
    }

    /**
     * 根据eids批量启用展商
     * @param eids
     * @param adminId
     */
    @Transactional
    public void enableExhibitor(Integer[] eids, Integer adminId) {
    	if(eids != null){
            for (Integer eid:eids){
                TeaExhibitor exhibitor = exhibitorDao.query(eid);
                exhibitor.setIsLogout(0);
                exhibitor.setUpdateUser(adminId);
                exhibitor.setUpdateTime(new Date());
                exhibitor.setIsLogin(0);
                getHibernateTemplate().update(exhibitor);
            }
    	}
    }
    
    /**
     * 修改展商基本信息
     * @param request
     * @param eid
     * @param adminId
     * @throws Exception
     */
    @Transactional
    public void modifyExhibitorInfo(ModifyExhibitorInfoRequest request,Integer eid, Integer adminId) throws Exception {
    	if(eid != null){
            TeaExhibitor exhibitor = loadTeaExhibitorByEid(eid);
    		if(exhibitor != null){
    			TExhibitorInfo exhibitorInfo = new TExhibitorInfo();
    			if(request.getEinfoid() != null) {
    				//修改
    				exhibitorInfo = loadExhibitorInfoByEid(eid);
    				exhibitorInfo.setUpdateTime(new Date());
                    if(StringUtils.isNotEmpty(request.getCompany())){
                        exhibitorInfo.setCompany(request.getCompany().trim());
                        exhibitorInfo.setCompanyT(JChineseConvertor.getInstance().s2t(request.getCompany().trim()));
                    }
                    if(StringUtils.isNotEmpty(request.getCompanyEn())){
                        exhibitorInfo.setCompanyEn(request.getCompanyEn().trim());
                    }
                    exhibitorInfo.setCreateTime(new Date());
                    exhibitorInfo.setUpdateTime(new Date());
					if(adminId != null){
						exhibitorInfo.setAdminUser(adminId);
						exhibitorInfo.setAdminUpdateTime(new Date());
					}
    			}else{
    				//添加
    				exhibitorInfo.setEid(eid);
    				exhibitorInfo.setCreateTime(new Date());
    				if(adminId != null) exhibitorInfo.setAdminUser(adminId);
    			}
                if(StringUtils.isNotEmpty(request.getCompany())){
                    exhibitorInfo.setCompany(request.getCompany().trim());
                    exhibitorInfo.setCompanyT(JChineseConvertor.getInstance().s2t(request.getCompany().trim()));
                }
                if(StringUtils.isNotEmpty(request.getCompanyEn())){
                    exhibitorInfo.setCompanyEn(request.getCompanyEn().trim());
                }
    			exhibitorInfo.setPhone(request.getPhone());
				exhibitorInfo.setFax(request.getFax());
				exhibitorInfo.setEmail(request.getEmail());
				exhibitorInfo.setWebsite(request.getWebsite());
				exhibitorInfo.setAddress(request.getAddress());
				exhibitorInfo.setAddressEn(request.getAddressEn());
				exhibitorInfo.setZipcode(request.getZipcode());
				exhibitorInfo.setMainProduct(request.getMainProduct());
                exhibitorInfo.setMainProductEn(request.getMainProductEn());
				exhibitorInfo.setMark(request.getMark());
                exhibitorInfo.setMeipai(request.getMeipai());
                exhibitorInfo.setMeipaiEn(request.getMeipaiEn());
				if(StringUtils.isNotEmpty(request.getLogo())) exhibitorInfo.setLogo(request.getLogo());
                if(request.getEinfoid() != null) {
                    exhibitorInfoDao.update(exhibitorInfo);
                    exhibitorDao.update(exhibitor);
                }
				else
                    exhibitorInfoDao.create(exhibitorInfo);
    			modifyMeipai(request.getMeipai(), request.getMeipaiEn(), eid,adminId);
                modifyInvoice(request.getInvoiceTitle(),request.getInvoiceNo(),request.getEid());
                modifyProductType(request.getProductType(), request.getEinfoid(), adminId);
    		}
    	}
    }
    
    /**
     * 添加或修改楣牌
     * @param _meipai
     * @param _meipaiEn
     * @param eid
     * @param adminId
     */
    @Transactional
	public void modifyMeipai(String _meipai, String _meipaiEn, Integer eid, Integer adminId){
    	if(meipaiService.getMeiPaiByEid(eid) != null){
        	TExhibitorMeipai meipai = meipaiService.getMeiPaiByEid(eid);
        	meipai.setMeipai(_meipai);
        	meipai.setMeipaiEn(_meipaiEn);
        	if(adminId !=null ){
        		meipai.setUpdateAdmin(adminId);
        		meipai.setAdminUpdateTime(new Date());
        	}else{
        		meipai.setUpdateTime(new Date());
        	}
        	meipaiService.saveOrUpdate(meipai);
        }else{
        	TExhibitorMeipai meipai = new TExhibitorMeipai();
        	meipai.setMeipai(_meipai);
        	meipai.setMeipaiEn(_meipaiEn);
        	meipai.setExhibitorId(eid);
        	meipai.setCreateTime(new Date());
        	meipaiService.saveOrUpdate(meipai);
        }
    }
    
    /**
     * 添加修改产品类型
     * @param productTypes
     * @param einfoid
     * @param adminId
     * @throws Exception
     */
    @Transactional
    public void modifyProductType(String productTypes, Integer einfoid, Integer adminId) throws Exception{
    	List<TProductType> productTypes2 = productManagerService.queryProductsTypeByLv(2);
    	for(TProductType productType2:productTypes2){
    		if(loadExhibitorClassByClassId(productType2.getId()) != null){
    			if(!checkString(productTypes,String.valueOf(productType2.getId()))){
    				TExhibitorClass delExhibitorClass = loadExhibitorClassByClassId(productType2.getId());
    				exhibitorClassDao.delete(delExhibitorClass);
    			}
    		}else{
    			if(checkString(productTypes,String.valueOf(productType2.getId()))){
    				TExhibitorClass exhibitorClass = new TExhibitorClass();
    				exhibitorClass.setEinfoId(einfoid);
    				exhibitorClass.setParentClassId(productManagerService.loadParentClassIdById(productType2.getId()));
    				exhibitorClass.setClassId(productType2.getId());
    				exhibitorClass.setCreateTime(new Date());
    				if(adminId !=null){
    					exhibitorClass.setAdmin(adminId);
    	        	}
    				exhibitorClassDao.create(exhibitorClass);
    			}
    		}
    	}
    }
    
    /**
     * 修改发票信息
     * @param invoiceTitle
     * @param invoiceNo
     * @param eid
     */
    @Transactional
    private void modifyInvoice(String invoiceTitle, String invoiceNo, Integer eid) {
    	TInvoiceApply invoice = new TInvoiceApply();
    	if(invoiceService.getByEid(eid) != null) {
    		invoice = invoiceService.getByEid(eid);
    		invoice.setTitle(invoiceTitle);
    		invoice.setInvoiceNo(invoiceNo);
    		if(queryBoothByEid(eid) != null)
                invoice.setExhibitorNo(queryBoothByEid(eid).getBoothNumber());
    		else
                invoice.setExhibitorNo("");
    	}else{
    		if(queryBoothByEid(eid) != null)
                invoice = new TInvoiceApply(queryBoothByEid(eid).getBoothNumber(), invoiceNo, invoiceTitle, eid, new Date());
    		else
                invoice = new TInvoiceApply("", invoiceNo, invoiceTitle, eid, new Date());
    	}
    	invoiceService.create(invoice);
	}
    
	private Boolean checkString(String strs,String check){
		if(StringUtils.isNotEmpty(strs)){
			String[] string = strs.split(",");
	    	for(String str:string){
	    		if(str.equals(check)) return true;
	    	}
	    	return false;
		}
		return false;
    }
	
	/**
     * 根据eids删除产品分类
     * @param eids
     */
    @Transactional
    public void deleteExhibitorClassByEids(Integer[] eids) {
    	List<Integer> einfoids = new ArrayList<Integer>();
    	int i = 0;
    	for(Integer eid:eids){
    		TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(eid);
    		if(exhibitorInfo != null){
    			einfoids.add(exhibitorInfo.getEinfoid());
        		i ++;
    		}
    	}
    	List<TExhibitorClass> exhibitorClasses = queryExhibitorClassByClassEinfoids(einfoids);
    	if(exhibitorClasses != null){
    		for(TExhibitorClass exhibitorClass:exhibitorClasses){
        		getHibernateTemplate().delete(exhibitorClass);
        	}
    	}
    }
    
    /**
     * 根据eids删除展商
     * @param eids
     * @throws Exception
     */
    @Transactional
    public void deleteExhibitorByEids(Integer[] eids) throws Exception {
    	joinerManagerService.deleteJoinersByEids(eids);
    	contactService.deleteContactsByEids(eids);
    	termManagerService.deleteTermByEids(eids);
    	meipaiService.deleteMeiPaiByEid(eids);
    	deleteExhibitorClassByEids(eids);
    	productManagerService.deleteProductByEids(eids);
    	deleteExhibitorInfoByEid(eids);
    	deleteExhibitorBooth(eids);
    	for(Integer eid:eids){
            TeaExhibitor exhibitor = loadTeaExhibitorByEid(eid);
        	if(exhibitor != null){
        		getHibernateTemplate().delete(exhibitor);
        	}
    	}
    }

    /**
     * 根据eids删除展位号
     * @param eids
     * @throws Exception
     */
    @Transactional
	public void deleteExhibitorBooth(Integer[] eids) throws Exception {
		List<TExhibitorBooth> exhibitorBooths = exhibitorBoothDao.loadExhibitorBoothByEids(eids);
		if(exhibitorBooths != null){
			for(TExhibitorBooth exhibitorBooth:exhibitorBooths){
				if(exhibitorBooth != null) exhibitorBoothDao.delete(exhibitorBooth);
			}
		}
	}

    /**
     * 分页获取归档资料列表
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    @Transactional
    public QueryHistoryExhibitorResponse queryHistoryExhibitorInfosByPage(QueryHistoryInfoRequest request) throws UnsupportedEncodingException {
        List<String> conditions = new ArrayList<String>();
        if (StringUtils.isNotEmpty(request.getBooth_number())) {
            conditions.add(" (e.booth_number like '%" + request.getBooth_number() + "%' OR e.booth_number like '%" + new String(request.getBooth_number().getBytes("ISO-8859-1"),"GBK") + "%' OR e.booth_number like '%" + new String(request.getBooth_number().getBytes("ISO-8859-1"),"utf-8") + "%') ");
        }
        if (StringUtils.isNotEmpty(request.getCompany_zh())) {
            conditions.add(" (e.company_zh like '%" + request.getCompany_zh() + "%' OR e.company_zh like '%" + new String(request.getCompany_zh().getBytes("ISO-8859-1"),"GBK") + "%' OR e.company_zh like '%" + new String(request.getCompany_zh().getBytes("ISO-8859-1"),"utf-8") + "%') ");
        }
        if (StringUtils.isNotEmpty(request.getCompany_en())) {
            conditions.add(" (e.company_en like '%" + request.getCompany_en() + "%' OR e.company_en like '%" + new String(request.getCompany_en().getBytes("ISO-8859-1"),"GBK") + "%' OR e.company_en like '%" + new String(request.getCompany_en().getBytes("ISO-8859-1"),"utf-8") + "%') ");
        }
        if (StringUtils.isNotEmpty(request.getMain_product_zh())) {
            conditions.add(" (e.main_product_zh like '%" + request.getMain_product_zh() + "%' OR e.main_product_zh like '%" + new String(request.getMain_product_zh().getBytes("ISO-8859-1"),"GBK") + "%' OR e.main_product_zh like '%" + new String(request.getMain_product_zh().getBytes("ISO-8859-1"),"utf-8") + "%') ");
        }
        if (StringUtils.isNotEmpty(request.getMain_product_en())) {
            conditions.add(" (e.main_product_en like '%" + request.getMain_product_en() + "%' OR e.main_product_en like '%" + new String(request.getMain_product_en().getBytes("ISO-8859-1"),"GBK") + "%' OR e.main_product_en like '%" + new String(request.getMain_product_en().getBytes("ISO-8859-1"),"utf-8") + "%') ");
        }
        if (StringUtils.isNotEmpty(request.getInvoice_head())) {
            conditions.add(" (e.invoice_head like '%" + request.getInvoice_head() + "%' OR e.invoice_head like '%" + new String(request.getInvoice_head().getBytes("ISO-8859-1"),"GBK") + "%' OR e.invoice_head like '%" + new String(request.getInvoice_head().getBytes("ISO-8859-1"),"utf-8") + "%') ");
        }
        if (StringUtils.isNotEmpty(request.getLocal_tax())) {
            conditions.add(" (e.local_tax like '%" + request.getLocal_tax() + "%' OR e.local_tax like '%" + new String(request.getLocal_tax().getBytes("ISO-8859-1"),"GBK") + "%' OR e.local_tax like '%" + new String(request.getLocal_tax().getBytes("ISO-8859-1"),"utf-8") + "%') ");
        }
        if (StringUtils.isNotEmpty(request.getWebsite())) {
            conditions.add(" (e.website like '%" + request.getWebsite() + "%' OR e.website like '%" + new String(request.getWebsite().getBytes("ISO-8859-1"),"GBK") + "%' OR e.website like '%" + new String(request.getWebsite().getBytes("ISO-8859-1"),"utf-8") + "%') ");
        }
        if (StringUtils.isNotEmpty(request.getJoiner_name())) {
            conditions.add(" (e.joiner_name like '%" + request.getJoiner_name() + "%' OR e.joiner_name like '%" + new String(request.getJoiner_name().getBytes("ISO-8859-1"),"GBK") + "%' OR e.joiner_name like '%" + new String(request.getJoiner_name().getBytes("ISO-8859-1"),"utf-8") + "%') ");
        }
        String conditionsSql = StringUtils.join(conditions, " and ");
        String conditionsSqlNoOrder = "";
        if(StringUtils.isNotEmpty(conditionsSql)){
            conditionsSqlNoOrder = " where " + conditionsSql;
        }

        Page page = new Page();
        page.setPageSize(request.getRows());
        page.setPageIndex(request.getPage());
        List<QueryHistoryInfo> exhibitors = exhibitorDao.queryPageByHQL("select count(*) from THistoryExhibitorInfo e " + conditionsSqlNoOrder,
                "select new com.zhenhappy.ems.manager.dto.QueryHistoryInfo(e.id, e.booth_number, e.company_zh, e.company_en, e.telphone, e.fax, " +
                        "e.email, e.website, e.address_zh, e.address_en, e.zipcode, e.product_type, e.main_product_zh, e.main_product_en, " +
                        "e.company_profile, e.invoice_head, e.local_tax, e.joiner_name, e.joiner_telphone, e.joiner_email, e.fair_year," +
                        "e.field_back1, e.field_back2, e.field_back3, e.field_back4) "
                        + "from THistoryExhibitorInfo e " + conditionsSqlNoOrder, new Object[]{}, page);
        QueryHistoryExhibitorResponse response = new QueryHistoryExhibitorResponse();
        response.setResultCode(0);
        response.setRows(exhibitors);
        response.setTotal(page.getTotalCount());
        return response;
    }

    /**
     * 查询增值税专用发票
     * @return
     */
    @Transactional
    public List<TInvoiceApplyExtend> loadAllInvoiceApplyByInvoiceFlag() {
        List<TInvoiceApplyExtend> invoiceApplyList = invoiceApplyDao.queryByHql("from TInvoiceApplyExtend where invoice_flag = 3", new Object[]{});
        return invoiceApplyList.size() > 0 ? invoiceApplyList : null;
    }

    /**
     * 根据eids查询增值税专用发票
     * @return
     */
    @Transactional
    public List<TInvoiceApplyExtend> loadSelectedInvoiceApplyByInvoiceFlag(Integer[] eids) {
        List<TInvoiceApplyExtend> invoiceApplyExtendArrayList = new ArrayList<TInvoiceApplyExtend>();
        for (Integer eid:eids){
            TInvoiceApplyExtend invoiceApplyExtend = loadInvoiceApplyExtendByEid(eid);
            if (invoiceApplyExtend != null) invoiceApplyExtendArrayList.add(invoiceApplyExtend);
        }
        return invoiceApplyExtendArrayList.size() > 0 ? invoiceApplyExtendArrayList : null;
    }

    /**
     * 根据eid查询增值税专用发票
     * @param eid
     * @return
     */
    @Transactional
    public TInvoiceApplyExtend loadInvoiceApplyExtendByEid(Integer eid) {
        List<TInvoiceApplyExtend> invoiceApplyList = invoiceApplyDao.queryByHql("from TInvoiceApplyExtend where invoice_flag = 3 and eid=?", new Object[]{eid});
        return invoiceApplyList.size() > 0 ? invoiceApplyList.get(0) : null;
    }

    /**
     * 根据eid查询增值税专用发票
     * @param eid
     * @return
     */
    @Transactional
    public TInvoiceApplyExtend getInvoiceApplyExtendByEid(Integer eid) {
        List<TInvoiceApplyExtend> invoiceApplyList = invoiceApplyDao.queryByHql("from TInvoiceApplyExtend where eid=?", new Object[]{eid});
        return invoiceApplyList.size() > 0 ? invoiceApplyList.get(0) : null;
    }
}
