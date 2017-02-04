package com.zhenhappy.ems.manager.action.user;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.zhenhappy.ems.dao.VisitorInfoDao;
import com.zhenhappy.ems.entity.TVisitorInfo;
import com.zhenhappy.ems.entity.managerrole.TUserInfo;
import com.zhenhappy.ems.manager.dao.THistoryVisitorInfoDao;
import com.zhenhappy.ems.manager.dto.*;
import com.zhenhappy.ems.manager.entity.THistoryVisitorInfo;
import com.zhenhappy.ems.manager.entity.TVisitorType;
import com.zhenhappy.ems.manager.exception.DuplicateUsernameException;
import com.zhenhappy.ems.manager.service.TVisitorTypeService;
import com.zhenhappy.ems.manager.sys.Constants;
import com.zhenhappy.ems.manager.tag.StringUtil;
import com.zhenhappy.ems.manager.util.JXLExcelView;
import com.zhenhappy.ems.service.managerrole.TUserInfoService;
import com.zhenhappy.system.SystemConfig;
import com.zhenhappy.util.EmailPattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    private VisitorInfoDao visitorInfoDao;
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private THistoryVisitorInfoDao tHistoryVisitorInfoDao;
    @Autowired
    private TVisitorTypeService tVisitorTypeService;
    @Autowired
    private TUserInfoService userInfoService;

    /**
     * 分页查询客商
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryCustomersByPage")
    public QueryCustomerResponse queryCustomersByPage(@ModelAttribute QueryCustomerRequest request) {
    	QueryCustomerResponse response = new QueryCustomerResponse();
        try {
        	response = customerInfoManagerService.queryCustomersByPage(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query customers error.", e);
        }
        return response;
    }
    
    @RequestMapping(value = "customer")
    public ModelAndView directToCustomer() {
        ModelAndView modelAndView = new ModelAndView("/user/customer");
        return modelAndView;
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
                List<TVisitorInfo> wCustomers = customerInfoManagerService.loadCustomerByEmail(request.getEmail());
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

    //==================佛事展新增需求=================
    @RequestMapping(value = "inlandCustomer")
    public ModelAndView directToInlandCustomer() {
        ModelAndView modelAndView = new ModelAndView("/WEB-INF/tpl/user/customer/inlandCustomer.jsp");
        return modelAndView;
    }

    @RequestMapping(value = "foreignCustomer")
    public ModelAndView directToForeignCustomer() {
        ModelAndView modelAndView = new ModelAndView("/WEB-INF/tpl/user/customer/foreignCustomer.jsp");
        return modelAndView;
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
            log.error("query queryInland CustomersByPage error.", e);
        }
        return response;
    }

    /**
     * 查询所有国家
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryCustomerType", method = RequestMethod.POST)
    public List<TVisitorType> queryCustomerType(@ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        List<TVisitorType> tVisitorTypeList = new ArrayList<TVisitorType>();
        try {
            tVisitorTypeList = tVisitorTypeService.loadVisitorType();
        } catch (Exception e) {
            log.error("query inland customer type error.", e);
        }
        return tVisitorTypeList;
    }

    /**
     * 一键归类国内客商
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "classVisitorByOneKey")
    public BaseResponse classVisitorByOneKey(@ModelAttribute QueryExhibitorTimeRequest request) {
        BaseResponse response = new BaseResponse();
        try {
            List<TVisitorType> tVisitorTypeList = tVisitorTypeService.loadVisitorType();
            List<THistoryVisitorInfo> customers = tHistoryVisitorInfoDao.queryByHql("from THistoryVisitorInfo ", new Object[]{});

            for(THistoryVisitorInfo visitorInfo:customers){
                if(StringUtil.isNotEmpty(visitorInfo.getCompany())){
                    for(TVisitorType tVisitorType:tVisitorTypeList){
                        if(StringUtil.isNotEmpty(tVisitorType.getTypevalue()) && tVisitorType.getTypevalue() != null){
                            String[] visitorTypeArray = tVisitorType.getTypevalue().trim().split(",");
                            for(int i=0;i<visitorTypeArray.length;i++){
                                if(visitorInfo.getCompany().indexOf(visitorTypeArray[i])>=0){
                                    visitorInfo.setType(tVisitorType.getId());
                                }
                            }
                        }
                    }
                }else{
                    //如果对应的公司名为空，则属于“未分类”，未分类对应的类别值是空
                    for(TVisitorType tVisitorType:tVisitorTypeList){
                        if(StringUtil.isEmpty(tVisitorType.getTypevalue())){
                            visitorInfo.setType(tVisitorType.getId());
                            break;
                        }
                    }
                }
                if(visitorInfo.getType() == null){
                    for(TVisitorType tVisitorType:tVisitorTypeList){
                        if(StringUtil.isEmpty(tVisitorType.getTypevalue())){
                            visitorInfo.setType(tVisitorType.getId());
                            break;
                        }
                    }
                }
                tHistoryVisitorInfoDao.update(visitorInfo);
            }
            response.setResultCode(0);
        } catch (Exception e) {
            log.error("class visitor by one key error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 分页查询国内法师
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryInlandRabbiCustomersByPage")
    public QueryCustomerResponse queryInlandRabbiCustomersByPage(@ModelAttribute QueryCustomerRequest request) {
        QueryCustomerResponse response = new QueryCustomerResponse();
        try {
            response = customerInfoManagerService.queryInlandRabbiCustomersByPage(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query queryInland CustomersByPage error.", e);
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
            log.error("query queryForeign CustomersByPage error.", e);
        }
        return response;
    }

    /**
     * 分页查询国外法师
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryForeignRabbiCustomersByPage")
    public QueryCustomerResponse queryForeignRabbiCustomersByPage(@ModelAttribute QueryCustomerRequest request) {
        QueryCustomerResponse response = new QueryCustomerResponse();
        try {
            response = customerInfoManagerService.queryForeignRabbiCustomersByPage(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query queryForeign CustomersByPage error.", e);
        }
        return response;
    }

    /**
     * 显示客商详细页面
     * @param id
     * @return
     */
    @RequestMapping(value = "directToCustomerInfo")
    public ModelAndView directToCustomerInfo(@RequestParam("id") Integer id) {
        ModelAndView modelAndView = new ModelAndView("user/customer/customerInfo");
        modelAndView.addObject("id", id);
        modelAndView.addObject("customer", customerInfoManagerService.loadCustomerInfoById(id));
        return modelAndView;
    }

    /**
     * 修改客商是否专业
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "modifyCustomerProfessional", method = RequestMethod.POST)
    public BaseResponse modifyCustomerProfessional(@ModelAttribute QueryCustomerRequest request,
                                                   @RequestParam(value = "id", defaultValue = "")Integer id,
                                                   @RequestParam(value = "type")Integer type) {
        BaseResponse response = new BaseResponse();
        try {
            customerInfoManagerService.modifyCustomerProfessional(request, id, type);
        } catch (DuplicateUsernameException e) {
            response.setResultCode(2);
            response.setDescription(e.getMessage());
        } catch (Exception e) {
            log.error("modify customer professional error: ", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 普通客商与法师相互转化
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "modifyCustomerNormalOrRabbi", method = RequestMethod.POST)
    public BaseResponse modifyCustomerNormalOrRabbi(@ModelAttribute QueryCustomerRequest request,
                                                   @RequestParam(value = "id", defaultValue = "")Integer id) {
        BaseResponse response = new BaseResponse();
        try {
            customerInfoManagerService.modifyCustomerNormalOrRabbi(request, id);
        } catch (DuplicateUsernameException e) {
            response.setResultCode(2);
            response.setDescription(e.getMessage());
        } catch (Exception e) {
            log.error("modify customer norimal or rabbi error: ", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 分页查询归档资料
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryHistoryVisitorInfosByPage")
    public QueryHistoryVisitorResponse queryHistoryExhibitorInfosByPage(@ModelAttribute QueryHistoryInfoRequest request) {
        QueryHistoryVisitorResponse response = new QueryHistoryVisitorResponse();
        try {
            response = customerInfoManagerService.queryHistoryExhibitorInfosByPage(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query customers error.", e);
        }
        return response;
    }

    /**
     * 导入客商信息
     * @param file
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value="importHistoryVisitoryInfos", method={RequestMethod.POST,RequestMethod.GET})
    public List<String> importHistoryVisitoryInfos(@RequestParam MultipartFile file,
                                                   @RequestParam Integer inlandOrOutland,
                                                   @ModelAttribute ImportHistoryVisitoryRequest request) throws IOException{
        BaseResponse response = new BaseResponse();
        File importFile = callCenterUpload(file, "\\import", FilenameUtils.getBaseName(file.getOriginalFilename()) + new Date().getTime() + "." + FilenameUtils.getExtension(file.getOriginalFilename()));
        List<String> report = customerInfoManagerService.importHistoryVisitoryInfos(importFile, request, inlandOrOutland);
        return report;
    }

    @RequestMapping("callcenter/upload")
    public File callCenterUpload(@RequestParam MultipartFile file, String destDir, String fileName){
        String appendix_directory = systemConfig.getVal(Constants.appendix_directory).replaceAll("\\\\\\\\", "\\\\");
        if(StringUtils.isEmpty(fileName)) fileName = new Date().getTime() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        if(StringUtils.isNotEmpty(destDir)) destDir = appendix_directory + destDir;
        else destDir = appendix_directory;
        File targetFile = new File(destDir, fileName);
        if(!targetFile.exists()) {
            targetFile.mkdirs();
        }
        try {
            file.transferTo(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetFile;
    }

    @RequestMapping(value = "historyVisitorDetailInfo")
    public ModelAndView directToHistoryExhibitorDetailInfo(@RequestParam("id") Integer id) {
        ModelAndView modelAndView = new ModelAndView("user/managerreset/historyVisitorDetailInfo");
        modelAndView.addObject("id", id);
        modelAndView.addObject("historyVisitorDetailInfo", customerInfoManagerService.loadHistoryVisitorInfoById(id));
        return modelAndView;
    }
}
