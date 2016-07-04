package com.zhenhappy.ems.manager.action.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zhenhappy.ems.entity.TVisitorInfo;
import com.zhenhappy.ems.manager.exception.DuplicateUsernameException;
import org.apache.log4j.Logger;
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
import com.zhenhappy.ems.manager.dto.AddArticleRequest;
import com.zhenhappy.ems.manager.dto.ManagerPrinciple;
import com.zhenhappy.ems.manager.dto.ModifyArticleRequest;
import com.zhenhappy.ems.manager.dto.QueryCustomerRequest;
import com.zhenhappy.ems.manager.dto.QueryCustomerResponse;
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
}
