package com.zhenhappy.ems.manager.action.user;

import java.util.Date;
import java.util.List;

import com.zhenhappy.ems.dto.QueryEmailOrMsgRequest;
import com.zhenhappy.ems.entity.WCountry;
import com.zhenhappy.ems.entity.WCustomer;
import com.zhenhappy.ems.manager.dto.*;
import com.zhenhappy.ems.manager.dto.visitorgroup.QueryVisitorGroupRequest;
import com.zhenhappy.ems.manager.dto.visitorgroup.QueryVisitorMemberListRequest;
import com.zhenhappy.ems.manager.entity.WCustomerEx;
import com.zhenhappy.ems.manager.entity.backupinfo.TVisitorBackupInfo;
import com.zhenhappy.ems.manager.entity.visitorgroup.TVisitorGroupInfo;
import com.zhenhappy.ems.manager.exception.DuplicateUsernameException;
import com.zhenhappy.ems.service.CountryProvinceService;
import com.zhenhappy.util.EmailPattern;
import com.zhenhappy.util.Page;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.zhenhappy.ems.dto.BaseResponse;
import com.zhenhappy.ems.entity.TArticle;
import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.service.CustomerInfoManagerService;

/**
 * Created by wujianbin on 2014-07-02.
 */
@Controller
@RequestMapping(value = "user")
@SessionAttributes(value = ManagerPrinciple.MANAGERPRINCIPLE)
public class CustomerAction extends BaseAction {

    private static Logger log = Logger.getLogger(CustomerAction.class);

    @Autowired
    private CustomerInfoManagerService customerInfoManagerService;
    @Autowired
    private CountryProvinceService countryProvinceService;

    /**
     * 显示客商详细页面
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "directToCustomerInfo")
    public ModelAndView directToCustomerInfo(@RequestParam("id") Integer id) {
        ModelAndView modelAndView = new ModelAndView("user/customer/customerInfo");
        modelAndView.addObject("id", id);
        WCustomer wCustomer = customerInfoManagerService.loadCustomerInfoById(id);
        WCustomerEx wCustomerEx = new WCustomerEx();
        if(wCustomer != null){
            int countryIndex = wCustomer.getCountry();
            WCountry country = countryProvinceService.loadCountryById(countryIndex);
            if(country != null){
                BeanUtils.copyProperties(wCustomer, wCustomerEx);
                wCustomerEx.setCountryValue(country.getChineseName());
            }
        }
        modelAndView.addObject("customer", wCustomerEx);
        modelAndView.addObject("customerSurvey", customerInfoManagerService.loadCustomerSurveyInfoByCustomerId(id));
        modelAndView.addObject("customerInfoYear", customerInfoManagerService.loadVisitorInfoYearByCustomerId(id));
        /*modelAndView.addObject("exhibitor", exhibitorManagerService.loadExhibitorByEid(eid));
        modelAndView.addObject("term", exhibitorManagerService.getExhibitorTermByEid(eid));
        modelAndView.addObject("booth", exhibitorManagerService.queryBoothByEid(eid));
        modelAndView.addObject("currentTerm", exhibitorManagerService.queryCurrentTermNumber());
        modelAndView.addObject("exhibitorInfo", exhibitorManagerService.loadExhibitorInfoByEid(eid));
        *//*石材展需求开始*//*
//        modelAndView.addObject("exhibitorMeipai", meipaiService.getMeiPaiByEid(eid));
        *//*石材展需求结束*//*
        modelAndView.addObject("invoice", invoiceService.getByEid(eid));*/
        return modelAndView;
    }

    /**
     * 显示客商详细页面
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "directToEmailSendFailureCustomerInfo")
    public ModelAndView directToEmailSendFailureCustomerInfo(@RequestParam("id") Integer id) {
        ModelAndView modelAndView = new ModelAndView("user/customer/emailSendFailureCustomerInfo");
        modelAndView.addObject("id", id);
        WCustomer wCustomer = customerInfoManagerService.loadCustomerInfoById(id);
        WCustomerEx wCustomerEx = new WCustomerEx();
        if(wCustomer != null){
            int countryIndex = wCustomer.getCountry();
            WCountry country = countryProvinceService.loadCountryById(countryIndex);
            if(country != null){
                BeanUtils.copyProperties(wCustomer, wCustomerEx);
                wCustomerEx.setCountryValue(country.getChineseName());
            }
        }
        modelAndView.addObject("customer", wCustomerEx);
        modelAndView.addObject("customerSurvey", customerInfoManagerService.loadCustomerSurveyInfoByCustomerId(id));
        return modelAndView;
    }

    /**
     * 根据客商ID获取公司名称
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getCompanyByCustomerId", method = RequestMethod.POST)
    public BaseResponse getCompanyByCustomerId(@RequestParam("id") Integer id) {
        BaseResponse response = new BaseResponse();
        WCustomer customer = customerInfoManagerService.loadCustomerInfoById(id);
        if(customer != null) {
            response.setDescription(customer.getCompany());
        } else {
            response.setDescription("");
        }
        return response;
    }

    /**
     * 修改客商账号
     * @param request
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "modifyCustomerInfo", method = RequestMethod.POST)
    public BaseResponse modifyCustomerAccount(@ModelAttribute ModifyCustomerInfo request, @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
            EmailPattern pattern = new EmailPattern();
            if(pattern.isEmailPattern(request.getEmail())) {
                List<WCustomer> wCustomers = customerInfoManagerService.loadCustomerByEmail(request.getEmail());
                if(wCustomers == null){
                    customerInfoManagerService.modifyCustomerAccount(request, principle.getAdmin().getId());
                } else {
                    response.setDescription("邮箱不能重复");
                    response.setResultCode(3);
                }
            } else {
                response.setResultCode(2);
                response.setDescription("请输入有效的邮箱格式");
            }
            if(!pattern.isMobileNO(request.getMobilePhone())) {
                response.setResultCode(2);
                response.setDescription("请输入有效的手机号码");
            }
        } catch (DuplicateUsernameException e) {
            response.setResultCode(2);
            response.setDescription(e.getMessage());
        } catch (Exception e) {
            log.error("modify customer account error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 修改客商是否专业
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "modifyCustomerProfessional", method = RequestMethod.POST)
    public BaseResponse modifyCustomerProfessional(@ModelAttribute QueryCustomerRequest request, @RequestParam(value = "id", defaultValue = "")Integer id) {
        BaseResponse response = new BaseResponse();
        try {
            customerInfoManagerService.modifyCustomerProfessional(request, id);
        } catch (DuplicateUsernameException e) {
            response.setResultCode(2);
            response.setDescription(e.getMessage());
        } catch (Exception e) {
            log.error("modify customer account error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 激活或注销客商信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "setCustomerActiveOrUnactive", method = RequestMethod.POST)
    public BaseResponse setCustomerActiveOrUnactive(@ModelAttribute QueryCustomerRequest request,
                                                    @RequestParam(value = "id", defaultValue = "")Integer id) {
        BaseResponse response = new BaseResponse();
        try {
            customerInfoManagerService.setCustomerActiveOrUnactive(request, id);
        } catch (DuplicateUsernameException e) {
            response.setResultCode(2);
            response.setDescription(e.getMessage());
        } catch (Exception e) {
            log.error("modify customer account error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 分页查询客商
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryStoneCustomersByPage")
    public QueryCustomerResponse queryStoneCustomersByPage(@ModelAttribute QueryCustomerRequest request) {
        QueryCustomerResponse response = new QueryCustomerResponse();
        try {
            response = customerInfoManagerService.queryCustomersByPage(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query customers error.", e);
        }
        return response;
    }

    /**
     * 分页查询国内客商邮件发送失败
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryCustomersBySendEmailFailurePage")
    public QueryCustomerResponse queryCustomersBySendEmailFailurePage(@ModelAttribute QueryCustomerRequest request) {
        QueryCustomerResponse response = new QueryCustomerResponse();
        try {
            response = customerInfoManagerService.queryCustomersBySendEmailFailurePage(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query customers for send email failure error.", e);
        }
        return response;
    }

	/**
	 * 分页查询国内客商
	 *
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "queryInlandCustomersByPage")
	public QueryCustomerResponse queryInlandCustomersByPage(@ModelAttribute QueryCustomerRequest request) {
		QueryCustomerResponse response = new QueryCustomerResponse();
		try {
			response = customerInfoManagerService.queryInlandCustomersByPage(request);
		} catch (Exception e) {
			response.setResultCode(1);
			log.error("query customers error.", e);
		}
		return response;
	}

	/**
	 * 分页查询国外客商
	 *
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "queryForeignCustomersByPage")
	public QueryCustomerResponse queryForeignCustomersByPage(@ModelAttribute QueryCustomerRequest request) {
		QueryCustomerResponse response = new QueryCustomerResponse();
		try {
			response = customerInfoManagerService.queryForeignCustomersByPage(request);
		} catch (Exception e) {
			response.setResultCode(1);
			log.error("query customers error.", e);
		}
		return response;
	}
    
    @RequestMapping(value = "inlandCustomer")
    public ModelAndView directToInlandCustomer() {
        ModelAndView modelAndView = new ModelAndView("user/customer/inlandCustomer");
        return modelAndView;
    }

	@RequestMapping(value = "foreignCustomer")
	public ModelAndView directToForeignCustomer() {
		ModelAndView modelAndView = new ModelAndView("user/customer/foreignCustomer");
		return modelAndView;
	}

    @RequestMapping(value = "emailApplyPage")
    public ModelAndView directToEmailApplyPage() {
        ModelAndView modelAndView = new ModelAndView("user/customer/emailApplyPage");
        return modelAndView;
    }

    /**
     * 分页查询邮件申请记录
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "loadEmailApplyByPage")
    public QueryCustomerResponse loadEmailApplyByPage(@ModelAttribute QueryCustomerRequest request) {
        QueryCustomerResponse response = new QueryCustomerResponse();
        try {
            response = customerInfoManagerService.loadEmailApplyByPage(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query email apply error.", e);
        }
        return response;
    }

    /**
     * 分页查询邮件申请记录
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryEmailApplyByPage")
    public QueryCustomerResponse queryEmailApplyByPage(@ModelAttribute QueryEmailOrMsgRequest request) {
        QueryCustomerResponse response = new QueryCustomerResponse();
        try {
            response = customerInfoManagerService.queryEmailOrMsgApplyByPage(request, 1);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query email apply error.", e);
        }
        return response;
    }

    /**
     * 分页查询短信申请记录
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryMsgApplyByPage")
    public QueryCustomerResponse queryMsgApplyByPage(@ModelAttribute QueryEmailOrMsgRequest request) {
        QueryCustomerResponse response = new QueryCustomerResponse();
        try {
            response = customerInfoManagerService.queryEmailOrMsgApplyByPage(request, 2);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query msg apply error.", e);
        }
        return response;
    }

    @RequestMapping(value = "msgApplyPage")
    public ModelAndView directToMmailApplyPage() {
        ModelAndView modelAndView = new ModelAndView("user/customer/msgApplyPage");
        return modelAndView;
    }

    /**
     * 分页查询短信申请记录
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "loadMsgApplyByPage")
    public QueryCustomerResponse loadMsgApplyByPage(@ModelAttribute QueryCustomerRequest request) {
        QueryCustomerResponse response = new QueryCustomerResponse();
        try {
            response = customerInfoManagerService.loadMsgApplyByPage(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query msg apply error.", e);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "addCustomer", method = RequestMethod.POST)
    public BaseResponse addCustomer(@ModelAttribute AddArticleRequest request, @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
            TArticle article = new TArticle();
            article.setTitle(request.getTitle());
            article.setTitleEn(request.getTitleEn());
            article.setDigest(request.getDigestEn());
            article.setDigestEn(request.getDigestEn());
            article.setContent(request.getContent());
            article.setContentEn(request.getContentEn());
            article.setCreateTime(new Date());
            if(principle.getAdmin().getId() != null || "".equals(principle.getAdmin().getId())){
            	article.setCreateAdmin(principle.getAdmin().getId());
            }else{
            	throw new Exception();
            }
        } catch (Exception e) {
            log.error("add article error.", e);
            response.setResultCode(1);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "modifyCustomer", method = RequestMethod.POST)
    public BaseResponse modifyCustomer(@ModelAttribute ModifyArticleRequest request, @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
        } catch (Exception e) {
            log.error("modify article error.", e);
            response.setResultCode(1);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "deleteCustomers", method = RequestMethod.POST)
    public BaseResponse deleteCustomers(@RequestParam(value = "ids", defaultValue = "") Integer[] ids) {
        BaseResponse response = new BaseResponse();
        try {
        	if(ids == null) throw new Exception();
        } catch (Exception e) {
        	log.error("delete articles error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "loadYearListForCustomer", method = RequestMethod.POST)
    public QueryCustomerYearResponse loadYearListForCustomer(@ModelAttribute QueryCustomerYearRequest request) {
        QueryCustomerYearResponse response = new QueryCustomerYearResponse();
        try {
            response = customerInfoManagerService.loadYearListForCustomer(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query tags error.", e);
        }
        return response;
    }

    /**
     * 备份客商数据
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "backupCustomerData", method = RequestMethod.POST)
    public BaseResponse backupCustomerData() {
        BaseResponse response = new BaseResponse();
        try {
            customerInfoManagerService.backupCustomerData();
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("backup customer info error.", e);
        }
        return response;
    }

    /**
     * 分页查询备份客商数据
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryAllCustomerBackupInfosByPage")
    public QueryCustomerResponse queryAllCustomerBackupInfosByPage(@ModelAttribute QueryCustomerBackupInfoRequest request) {
        QueryCustomerResponse response = new QueryCustomerResponse();
        try {
            response = customerInfoManagerService.queryAllCustomerBackupInfosByPage(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query customers error.", e);
        }
        return response;
    }

    /**
     * 显示客商详细页面
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "directToCustomerBackupInfo")
    public ModelAndView directToCustomerBackupInfo(@RequestParam("id") Integer id) {
        ModelAndView modelAndView = new ModelAndView("user/databackup/customerBackupDetailInfo");
        modelAndView.addObject("id", id);
        TVisitorBackupInfo tVisitorBackupInfo = customerInfoManagerService.loadCustomerBackupInfoById(id);
        modelAndView.addObject("customer", tVisitorBackupInfo);
        return modelAndView;
    }

    /**
     * 分页查询参观团信息
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryVisitorGroupByPage")
    public QueryCustomerResponse queryVisitorGroupByPage(@ModelAttribute QueryVisitorGroupRequest request) {
        QueryCustomerResponse response = new QueryCustomerResponse();
        try {
            response = customerInfoManagerService.queryVisitorGroupByPage(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query customers error.", e);
        }
        return response;
    }

    /**
     * 显示参观团详细页面
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "directToVisitorGroupInfo")
    public ModelAndView directToVisitorGroupInfo(@RequestParam("id") Integer id) {
        ModelAndView modelAndView = new ModelAndView("user/customer/visitorGroupDetailInfo");
        modelAndView.addObject("id", id);
        TVisitorGroupInfo tVisitorGroupInfo = customerInfoManagerService.loadVisitorGroupDetailInfoById(id);
        modelAndView.addObject("tVisitorGroupInfo", tVisitorGroupInfo);
        return modelAndView;
    }

    /**
     * 根据id查询参观团对应成员列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryVisitorMemberListByGroupId", method = RequestMethod.POST)
    public QueryExhibitorResponse queryVisitorMemberListByGroupId(@ModelAttribute QueryVisitorMemberListRequest request) {
        QueryExhibitorResponse response = new QueryExhibitorResponse();
        try {
            Page page = new Page();
            page.setPageSize(request.getRows());
            page.setPageIndex(request.getPage());
            List<TVisitorGroupInfo> tVisitorGroupInfoList = customerInfoManagerService.loadProductBackupInfoById(request.getId());
            response.setRows(tVisitorGroupInfoList);
            response.setResultCode(0);
            response.setTotal(page.getTotalCount());
        } catch (Exception e) {
            response = new QueryExhibitorResponse();
            log.error("query visitor member list error.",e);
        }
        return response;
    }
}
