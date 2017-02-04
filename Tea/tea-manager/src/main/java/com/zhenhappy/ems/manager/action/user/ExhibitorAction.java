package com.zhenhappy.ems.manager.action.user;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.zhenhappy.ems.dao.JoinerDao;
import com.zhenhappy.ems.dao.TeaExhibitorDao;
import com.zhenhappy.ems.entity.*;
import com.zhenhappy.ems.manager.dao.ExhibitorBoothDao;
import com.zhenhappy.ems.manager.dto.*;
import com.zhenhappy.ems.manager.entity.THistoryExhibitorInfo;
import com.zhenhappy.ems.manager.service.HistoryExhibitorInfoService;
import com.zhenhappy.ems.manager.service.JoinerManagerService;
import com.zhenhappy.ems.service.InvoiceExtendService;
import com.zhenhappy.util.Page;
import net.sf.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.zhenhappy.ems.dto.BaseResponse;
import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.entity.TExhibitorBooth;
import com.zhenhappy.ems.manager.entity.TExhibitorTerm;
import com.zhenhappy.ems.manager.exception.DuplicateUsernameException;
import com.zhenhappy.ems.manager.service.ExhibitorManagerService;
import com.zhenhappy.ems.manager.service.TagManagerService;
import com.zhenhappy.ems.service.CountryProvinceService;
import com.zhenhappy.ems.service.MeipaiService;
import com.zhenhappy.system.SystemConfig;

/**
 * Created by wujianbin on 2014-04-22.
 */
@Controller
@RequestMapping(value = "user")
@SessionAttributes(value = ManagerPrinciple.MANAGERPRINCIPLE)
public class ExhibitorAction extends BaseAction {

	private static Logger log = Logger.getLogger(ExhibitorAction.class);
    private List<THistoryExhibitorInfo> existExhibitorInfo = new ArrayList<THistoryExhibitorInfo>();

    @Autowired
    private ExhibitorManagerService exhibitorManagerService;
    @Autowired
    private TagManagerService tagManagerService;
    @Autowired
    private MeipaiService meipaiService;
    @Autowired
    private InvoiceExtendService invoiceService;
    @Autowired
    private CountryProvinceService countryProvinceService;
    @Autowired
    private ImportExportAction importExportAction;
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private JoinerDao joinerDao;
    @Autowired
    private TeaExhibitorDao exhibitorDao;
    @Autowired
    private JoinerManagerService joinerManagerService;
    @Autowired
    private HistoryExhibitorInfoService historyExhibitorInfoService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ExhibitorBoothDao exhibitorBoothDao;
    @Autowired
    private HibernateTemplate hibernateTemplate;

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    /**
     * 分页查询展商列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryExhibitorsByPage")
    public QueryExhibitorResponse queryExhibitorsByPage(@ModelAttribute QueryExhibitorRequest request) {
        QueryExhibitorResponse response = null;
        try {
        	response = exhibitorManagerService.queryExhibitorsByPage(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query exhibitors error.", e);
        }
        return response;
    }
    
    /**
     * 查询展商列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryExhibitors")
    public List<TeaExhibitor> queryExhibitors() {
    	List<TeaExhibitor> response = new ArrayList<TeaExhibitor>();
        try {
        	response = exhibitorManagerService.loadAllExhibitors();
        } catch (Exception e) {
            log.error("query exhibitors error.", e);
        }
        return response;
    }
    
    /**
     * 根据eid查询展商
     * @param eid
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryExhibitorByEid")
    public TeaExhibitor queryExhibitorByEid(@RequestParam("eid") Integer eid) {
        TeaExhibitor response = new TeaExhibitor();
        try {
        	response = exhibitorManagerService.loadTeaExhibitorByEid(eid);
        } catch (Exception e) {
            log.error("query exhibitors error.", e);
        }
        return response;
    }

    @RequestMapping(value = "exhibitor")
    public ModelAndView directToCompany(@RequestParam("eid") Integer eid) {
        ModelAndView modelAndView = new ModelAndView("/user/exhibitor/company");
        modelAndView.addObject("eid", eid);
        modelAndView.addObject("exhibitor", exhibitorManagerService.loadTeaExhibitorByEid(eid));
        modelAndView.addObject("term", exhibitorManagerService.getExhibitorTermByEid(eid));
        modelAndView.addObject("booth", exhibitorManagerService.queryBoothByEid(eid));
        modelAndView.addObject("currentTerm", exhibitorManagerService.queryCurrentTermNumber());
        modelAndView.addObject("exhibitorInfo", exhibitorManagerService.loadExhibitorInfoByEid(eid));
        modelAndView.addObject("exhibitorMeipai", meipaiService.getMeiPaiByEid(eid));
        modelAndView.addObject("invoice", invoiceService.getByEid(eid));
        return modelAndView;
    }
    
    /**
     * 添加展商账号
     * @param request
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "addExhibitor", method = RequestMethod.POST)
    public BaseResponse addExhibitorAccount(@ModelAttribute AddExhibitorRequest request,
                                            @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
        	exhibitorManagerService.addExhibitor(request, principle.getAdmin().getId());
        } catch (DuplicateUsernameException e) {
            response.setResultCode(2);
            response.setDescription(e.getMessage());
        } catch (Exception e) {
            log.error("add exhibitor account error.", e);
            response.setResultCode(1);
        }
        return response;
    }
    
    /**
     * 修改展商账号
     * @param request
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "modifyExhibitor", method = RequestMethod.POST)
    public BaseResponse modifyExhibitorAccount(@ModelAttribute ModifyExhibitorRequest request,
                                               @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
            exhibitorManagerService.modifyExhibitorAccount(request, principle.getAdmin().getId());
        } catch (DuplicateUsernameException e) {
            response.setResultCode(2);
            response.setDescription(e.getMessage());
        } catch (Exception e) {
            log.error("modify exhibitor account error.", e);
            response.setResultCode(1);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "activeExhibitor", method = RequestMethod.POST)
    public BaseResponse activeExhibitor(@ModelAttribute ActiveExhibitorRequest request,
                                        @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
            TExhibitorTerm term = new TExhibitorTerm();
            term.setEid(request.getEid());
            if (request.getId() != null) {
                term.setId(request.getId());
                term.setUpdateUser(principle.getAdmin().getId());
            } else {
                term.setCreateUser(principle.getAdmin().getId());
            }
            term.setCreateTime(new Date());
            //set base data
            term.setBoothNumber(request.getBoothNumber());
            term.setJoinTerm(request.getTerm());
            term.setIsDelete(0);
            term.setMark(request.getMark());
            exhibitorManagerService.activeExhibitor(term);
        } catch (Exception e) {
            log.error("active exhibitor error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "bindBooth", method = RequestMethod.POST)
    public BaseResponse bindBooth(@ModelAttribute BindBoothRequest request,
                                  @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
            TExhibitorBooth booth = new TExhibitorBooth();
            if (request.getId() != null) {
                booth.setId(request.getId());
                booth.setUpdateUser(principle.getAdmin().getId());
                booth.setUpdateTime(new Date());
            } else {
                booth.setCreateTime(new Date());
                booth.setCreateUser(principle.getAdmin().getId());
            }
            booth.setEid(request.getEid());
            //set base data
            booth.setBoothNumber(request.getBoothNumber().trim().replace(" ", ""));
            if(request.getExhibitionArea() == null || "".equals(request.getExhibitionArea())){
            	booth.setExhibitionArea(request.getBoothNumber().trim().replace(" ", "").substring(0,1) + "厅");
            }else{
            	booth.setExhibitionArea(request.getExhibitionArea());
            }
            booth.setMark(request.getMark());
            exhibitorManagerService.bindBoothInfo(booth);
        } catch (Exception e) {
            log.error("bind exhibitor booth number error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 批量修改所属人
     * @param eids
     * @param tag
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "modifyExhibitorsTag", method = RequestMethod.POST)
    public BaseResponse modifyExhibitorsTag(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
                                            @RequestParam("tag") Integer tag,
                                            @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
        	if(eids != null && tag != null){
        		exhibitorManagerService.modifyExhibitorsTag(eids, tag, principle.getAdmin().getId());
        	}
        } catch (Exception e) {
            log.error("modify exhibitors tag error.", e);
            response.setResultCode(1);
        }
        return response;
    }
    
    /**
     * 批量修改展团
     * @param eids
     * @param group
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "modifyExhibitorsGroup", method = RequestMethod.POST)
    public BaseResponse modifyExhibitorsGroup(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
    										  @RequestParam("group") Integer group, 
    										  @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
        	if(eids != null && group != null){
        		exhibitorManagerService.modifyExhibitorsGroup(eids, group, principle.getAdmin().getId());
        	}
        } catch (Exception e) {
            log.error("modify exhibitors group error.", e);
            response.setResultCode(1);
        }
        return response;
    }
    
    /**
     * 批量修改展区
     * @param eids
     * @param area
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "modifyExhibitorsArea", method = RequestMethod.POST)
    public BaseResponse modifyExhibitorsArea(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
                                             @RequestParam("area") Integer area,
                                             @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
        	if(eids != null && area != null){
        		exhibitorManagerService.modifyExhibitorsArea(eids, area, principle.getAdmin().getId());
        	}
        } catch (Exception e) {
            log.error("modify exhibitors area error.", e);
            response.setResultCode(1);
        }
        return response;
    }
    
    /**
     * 批量注销展商
     * @param eids
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "disableExhibitors", method = RequestMethod.POST)
    public BaseResponse disableExhibitors(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
                                          @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
        	if(eids != null){
        		exhibitorManagerService.disableExhibitors(eids, principle.getAdmin().getId());
        	}
        } catch (Exception e) {
            log.error("disable exhibitor error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 批量启用展商
     * @param eids
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "enableExhibitors", method = RequestMethod.POST)
    public BaseResponse enableExhibitors(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
                                         @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
        	if(eids != null){
        		exhibitorManagerService.enableExhibitor(eids, principle.getAdmin().getId());
        	}
        } catch (Exception e) {
            log.error("enable exhibitor error.", e);
            response.setResultCode(1);
        }
        return response;
    }
    
    /**
     * 批量删除展商
     * @param eids
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "deleteExhibitors", method = RequestMethod.POST)
    public BaseResponse deleteExhibitors(@RequestParam(value = "eids", defaultValue = "") Integer[] eids) {
        BaseResponse response = new BaseResponse();
        try {
        	if(eids == null) throw new Exception();
        	exhibitorManagerService.deleteExhibitorByEids(eids);
        } catch (Exception e) {
        	log.error("delete exhibitors error.", e);
            response.setResultCode(1);
        }
        return response;
    }
    
    /**
     * 查询所有国家
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryAllCountry", method = RequestMethod.POST)
    public List<WCountry> queryAllCountry(@ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
    	List<WCountry> country = new ArrayList<WCountry>();
    	try {
        	country = countryProvinceService.loadAllCountry();
        } catch (Exception e) {
            log.error("query country error.", e);
        }
        return country;
    }
    
    /**
     * 查询所有省份
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryAllProvince", method = RequestMethod.POST)
    public List<WProvince> queryAllProvince(@ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
    	List<WProvince> province = new ArrayList<WProvince>();
    	try {
    		province = countryProvinceService.loadAllProvince();
        } catch (Exception e) {
            log.error("query province error.", e);
        }
        return province;
    }
    
    /**
     * 通过countryId查询省份
     * @param countryId
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryProvinceByCountryId", method = RequestMethod.POST)
    public List<WProvince> queryProvinceByCountryId(@RequestParam("countryId") Integer countryId,
                                                    @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
    	List<WProvince> province = new ArrayList<WProvince>();
        try {
        	province = countryProvinceService.loadProvinceByCountryId(countryId);
        } catch (Exception e) {
            log.error("query province by countryId error.", e);
        }
        return province;
    }
    
    /**
     * 修改展商基本信息
     * @param request
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "modifyExhibitorInfo", method = RequestMethod.POST)
    public BaseResponse modifyExhibitorInfo(@ModelAttribute ModifyExhibitorInfoRequest request, 
								    		@RequestParam("eid") Integer eid,
								    		@ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
        	if(eid != null){
        		exhibitorManagerService.modifyExhibitorInfo(request, eid, principle.getAdmin().getId());
        	}else{
        		throw new Exception();
        	}
        } catch (Exception e) {
            log.error("modify exhibitor info error.", e);
            response.setResultCode(1);
        }
        return response;
    }
    
    /**
     * 上传Logo并修改展商基本信息
     * @param logoFile
     * @param request
     * @param eid
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value="upload/modifyInfo", method={RequestMethod.POST,RequestMethod.GET})
    public BaseResponse uploadModifyInfo(@RequestParam MultipartFile logoFile,
						    		  	 @ModelAttribute ModifyExhibitorInfoRequest request, 
						    		  	 @RequestParam("eid") Integer eid,
						    		  	 @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle){
    	BaseResponse response = new BaseResponse();
        try {
        	if(eid != null){
        		String oldLogoPath = exhibitorManagerService.loadExhibitorInfoByEid(eid).getLogo();
        		File logo = importExportAction.upload(logoFile, null, null);
        		if(StringUtils.isNotEmpty(oldLogoPath)){
        			File oldLogo = new File(oldLogoPath);
        			if(oldLogo.exists() == false) FileUtils.deleteQuietly(oldLogo);
        		}
        		request.setLogo(logo.getPath());
        		modifyExhibitorInfo(request, eid, principle);
        	}else{
        		throw new Exception();
        	}
        } catch (Exception e) {
            log.error("modify exhibitor info error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 显示Logo
     * @param response
     * @param eid
     */
    @RequestMapping(value = "showLogo", method = RequestMethod.GET)
    public void showLogo(HttpServletResponse response, @RequestParam("eid") Integer eid, @RequestParam(value = "page", defaultValue = "0") Integer page) {
        try {
            String logoFileName = exhibitorManagerService.loadExhibitorInfoByEid(eid).getLogo();
            if(logoFileName.contains(";")) {
                String[] logos = logoFileName.split(";");
                if (StringUtils.isNotEmpty(logos[page])) {
                    File logo = new File(logos[page]);
                    if (!logo.exists()) {
                        return;
                    }
                    OutputStream outputStream = response.getOutputStream();
                    FileUtils.copyFile(new File(logos[page]), outputStream);
                    outputStream.close();
                    outputStream.flush();
                }
            }else if(logoFileName.contains("appendix")){
                File logo = new File(logoFileName);
                if (!logo.exists()) return;
                OutputStream outputStream = response.getOutputStream();
                FileUtils.copyFile(logo, outputStream);
                outputStream.close();
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分页查询归档资料
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryHistoryExhibitorInfosByPage")
    public QueryHistoryExhibitorResponse queryHistoryExhibitorInfosByPage(@ModelAttribute QueryHistoryInfoRequest request) {
        QueryHistoryExhibitorResponse response = new QueryHistoryExhibitorResponse();
        try {
            response = exhibitorManagerService.queryHistoryExhibitorInfosByPage(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query customers error.", e);
        }
        return response;
    }

    /**
     * 一键归档所有展商信息
     * @param principle
     */
    @ResponseBody
    @RequestMapping(value = "oneKeyBackupAllExhibitorInfo", method = RequestMethod.POST)
    public BackupHistoryExhibitoryInfoResponse oneKeyBackupAllExhibitorInfo(@ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BackupHistoryExhibitoryInfoResponse backupHistoryExhibitoryInfoResponse = new BackupHistoryExhibitoryInfoResponse();
        List<String> report = new ArrayList<String>();
        existExhibitorInfo = new ArrayList<THistoryExhibitorInfo>();
        try {
            List<TeaExhibitor> tExhibitorList = exhibitorManagerService.loadAllExhibitorsByLogType(0);
            String tea_Fair_Show_Begin_Date = jdbcTemplate.queryForObject("select tea_Fair_Show_Begin_Date from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);
            for(TeaExhibitor exhibitor:tExhibitorList){
                TExhibitorInfo exhibitorInfo = exhibitorManagerService.loadExhibitorInfoByEid(exhibitor.getEid());
                //根据公司名，展位号和展会时间，判断是否有重复归档
                if(exhibitorInfo != null){
                    String boothNumber = exhibitorManagerService.loadBoothNum(exhibitor.getEid());
                    List<THistoryExhibitorInfo> tHistoryExhibitorInfoList = historyExhibitorInfoService.isExistTHistoryExhibitorInfo(exhibitorInfo.getCompany(),
                            exhibitorInfo.getCompanyEn(), boothNumber, tea_Fair_Show_Begin_Date);
                    if(tHistoryExhibitorInfoList != null && tHistoryExhibitorInfoList.size()>0){
                        for(THistoryExhibitorInfo tHistoryExhibitorInfo:tHistoryExhibitorInfoList){
                            existExhibitorInfo.add(tHistoryExhibitorInfo);
                        }
                    }
                    THistoryExhibitorInfo tHistoryExhibitorInfo = new THistoryExhibitorInfo();
                    tHistoryExhibitorInfo.setBooth_number(boothNumber);
                    tHistoryExhibitorInfo.setCompany_zh(exhibitorInfo.getCompany());
                    tHistoryExhibitorInfo.setCompany_en(exhibitorInfo.getCompanyEn());
                    tHistoryExhibitorInfo.setTelphone(exhibitorInfo.getPhone());
                    tHistoryExhibitorInfo.setFax(exhibitorInfo.getFax());
                    tHistoryExhibitorInfo.setEmail(exhibitorInfo.getEmail());
                    tHistoryExhibitorInfo.setWebsite(exhibitorInfo.getWebsite());
                    tHistoryExhibitorInfo.setAddress_zh(exhibitorInfo.getAddress());
                    tHistoryExhibitorInfo.setAddress_en(exhibitorInfo.getAddressEn());
                    tHistoryExhibitorInfo.setZipcode(exhibitorInfo.getZipcode());
                    tHistoryExhibitorInfo.setProduct_type(exhibitorManagerService.queryExhibitorClassByEinfoid(exhibitorInfo.getEinfoid()));
                    tHistoryExhibitorInfo.setMain_product_zh(exhibitorInfo.getMainProduct());
                    tHistoryExhibitorInfo.setMain_product_en(exhibitorInfo.getMainProductEn());
                    tHistoryExhibitorInfo.setCompany_profile(exhibitorInfo.getMark());
                    TInvoiceApplyExtend invoice = invoiceService.getByEid(exhibitorInfo.getEid());
                    if(invoice != null){
                        if(StringUtils.isNotEmpty(invoice.getInvoiceNo())) {
                            tHistoryExhibitorInfo.setLocal_tax(invoice.getInvoiceNo());
                        }else{
                            tHistoryExhibitorInfo.setLocal_tax("");
                        }
                        if(StringUtils.isNotEmpty(invoice.getTitle())){
                            tHistoryExhibitorInfo.setInvoice_head(invoice.getTitle());
                        }else{
                            tHistoryExhibitorInfo.setInvoice_head("");
                        }
                    }else{
                        tHistoryExhibitorInfo.setLocal_tax("");
                        tHistoryExhibitorInfo.setInvoice_head("");
                    }
                    List<TExhibitorJoiner> exhibitorJoinerList = joinerManagerService.loadExhibitorAllJoinerByEid(exhibitorInfo.getEid());
                    StringBuffer joinerNameBuf = new StringBuffer();
                    StringBuffer joinerTelphoneBuf = new StringBuffer();
                    StringBuffer joinerEmailBuf = new StringBuffer();
                    for(TExhibitorJoiner tExhibitorJoiner:exhibitorJoinerList){
                        if (StringUtils.isNotEmpty(tExhibitorJoiner.getName())) {
                            joinerNameBuf.append(tExhibitorJoiner.getName() + "&");
                        }
                        if (StringUtils.isNotEmpty(tExhibitorJoiner.getTelphone())) {
                            joinerTelphoneBuf.append(tExhibitorJoiner.getTelphone() + "&");
                        }
                        if (StringUtils.isNotEmpty(tExhibitorJoiner.getEmail())) {
                            joinerEmailBuf.append(tExhibitorJoiner.getEmail() + "&");
                        }
                    }
                    if(StringUtils.isNotEmpty(joinerNameBuf.toString())){
                        int lastNameIndex = joinerNameBuf.lastIndexOf("&");
                        String joinerNameValue = joinerNameBuf.substring(0,lastNameIndex);
                        tHistoryExhibitorInfo.setJoiner_name(joinerNameValue);
                    }
                    if(StringUtils.isNotEmpty(joinerTelphoneBuf.toString())){
                        int lastTelphoneIndex = joinerTelphoneBuf.lastIndexOf("&");
                        String joinerTelphoneValue = joinerTelphoneBuf.substring(0,lastTelphoneIndex);
                        tHistoryExhibitorInfo.setJoiner_telphone(joinerTelphoneValue);
                    }
                    if(StringUtils.isNotEmpty(joinerEmailBuf.toString())){
                        int lastEmailIndex = joinerEmailBuf.lastIndexOf("&");
                        String joinerEmailValue = joinerEmailBuf.substring(0,lastEmailIndex);
                        tHistoryExhibitorInfo.setJoiner_email(joinerEmailValue);
                    }

                    tHistoryExhibitorInfo.setFair_year(tea_Fair_Show_Begin_Date);
                    tHistoryExhibitorInfo.setField_back1("");
                    tHistoryExhibitorInfo.setField_back2("");
                    tHistoryExhibitorInfo.setField_back3("");
                    tHistoryExhibitorInfo.setField_back4("");
                    historyExhibitorInfoService.saveTHistoryExhibitorInfo(tHistoryExhibitorInfo);
                    if(tHistoryExhibitorInfo.getId() != null && tHistoryExhibitorInfo.getId() >0){
                        List<TExhibitorBooth> booths = getHibernateTemplate().find("from TExhibitorBooth where eid = ?", new Object[]{exhibitor.getEid()});
                        for(TExhibitorBooth tExhibitorBooth:booths){
                            tExhibitorBooth.setBoothNumber("");
                            exhibitorBoothDao.update(tExhibitorBooth);
                        }
                    }
                }
            }
            if(existExhibitorInfo != null && existExhibitorInfo.size()>0){
                StringBuffer resultBuffer = new StringBuffer();
                resultBuffer.append("有" + existExhibitorInfo.size() + "条记录，数据库中已经归档！");
                List resultArray = new ArrayList();
                resultArray.add(resultBuffer.toString());
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, Boolean.TRUE);
                JSONArray resultJson = JSONArray.fromObject(resultArray);
                backupHistoryExhibitoryInfoResponse.setResult(resultJson.toString());
                JSONArray isExistDataJsonArray = JSONArray.fromObject(existExhibitorInfo);
                backupHistoryExhibitoryInfoResponse.setIsExistData(isExistDataJsonArray.toString());
                backupHistoryExhibitoryInfoResponse.setResultCode(1);
            }
        } catch (Exception e) {
            backupHistoryExhibitoryInfoResponse.setResultCode(1);
            log.error("reset exhibitor list to backup error.", e);
        }
        return backupHistoryExhibitoryInfoResponse;
    }

    /**
     * 重置展商列表为初始状态
     * @param principle
     */
    @ResponseBody
    @RequestMapping(value = "resetExhibitorToDefault", method = RequestMethod.POST)
    public BaseResponse resetExhibitorToDefault(@ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
            List<TeaExhibitor> tExhibitorList = exhibitorManagerService.loadAllExhibitors();
            for(TeaExhibitor tExhibitor:tExhibitorList){
                tExhibitor.setIsLogout(1);
                exhibitorDao.update(tExhibitor);
            }
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("reset exhibitor list to default error.", e);
        }

        try {
            List<TExhibitorJoiner> joiners = joinerDao.queryByHql("from TExhibitorJoiner", new Object[]{});
            for(TExhibitorJoiner joiner: joiners){
                joiner.setIsDelete(1);
                joiner.setAdminUpdateTime(new Date());
                if(principle.getAdmin() != null){
                    joiner.setAdmin(principle.getAdmin().getId());
                }
                joinerDao.update(joiner);
            }
            response.setResultCode(0);
        } catch (Exception e) {
            response.setResultCode(1);
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 重置参展人员列表
     * @param principle
     */
    @ResponseBody
    @RequestMapping(value = "resetJoinerListToDefault", method = RequestMethod.POST)
    public BaseResponse resetJoinerListToDefault(@ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
            List<TExhibitorJoiner> joiners = joinerDao.queryByHql("from TExhibitorJoiner", new Object[]{});
            for(TExhibitorJoiner joiner: joiners){
                joiner.setIsDelete(1);
                joiner.setAdminUpdateTime(new Date());
                if(principle.getAdmin() != null){
                    joiner.setAdmin(principle.getAdmin().getId());
                }
                joinerDao.update(joiner);
            }
            response.setResultCode(0);
        } catch (Exception e) {
            response.setResultCode(1);
            e.printStackTrace();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "showExistExhibitorInfo")
    public QueryHistoryExhibitorResponse showExistExhibitorInfo(@ModelAttribute QueryHistoryInfoRequest request) {
        QueryHistoryExhibitorResponse response = new QueryHistoryExhibitorResponse();
        Page page = new Page();
        page.setPageSize(request.getRows());
        page.setPageIndex(request.getPage());
        response.setResultCode(0);
        response.setRows(existExhibitorInfo);
        response.setTotal(existExhibitorInfo.size());
        //response.setTotal(page.getTotalCount());
        return response;
    }

    @RequestMapping(value = "historyExhibitorDetailInfo")
    public ModelAndView directToHistoryExhibitorDetailInfo(@RequestParam("id") Integer id) {
        ModelAndView modelAndView = new ModelAndView("user/managerreset/historyExhibitoryDetailInfo");
        modelAndView.addObject("id", id);
        modelAndView.addObject("historyExhibitorDetailInfo", historyExhibitorInfoService.loadHistoryExhibitorInfoById(id));
        return modelAndView;
    }

    /**
     * 显示发票图片
     * @param response
     * @param eid
     */
    @RequestMapping(value = "showInvoiceImage", method = RequestMethod.GET)
    public void showInvoiceImage(HttpServletResponse response, @RequestParam("eid") Integer eid) {
        try {
            String logoFileName = invoiceService.getByEid(eid).getInvoice_image_address();
            if (StringUtils.isNotEmpty(logoFileName)) {
                OutputStream outputStream = response.getOutputStream();
                File logo = new File(logoFileName);
                if (!logo.exists()) {
                    return;
                }
                FileUtils.copyFile(new File(logoFileName), outputStream);
                outputStream.close();
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
