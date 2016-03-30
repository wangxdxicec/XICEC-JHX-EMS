package com.zhenhappy.ems.manager.action.user;

import com.alibaba.fastjson.JSONArray;
import com.zhenhappy.ems.dto.BaseResponse;
import com.zhenhappy.ems.entity.TVisitorInfo;
import com.zhenhappy.ems.entity.WCustomerTemplate;
import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.dto.GetMailSendDetailsResponse;
import com.zhenhappy.ems.dto.Principle;
import com.zhenhappy.ems.manager.dto.QueryCustomerRequest;
import com.zhenhappy.ems.manager.entity.Email;
import com.zhenhappy.ems.manager.service.CustomerInfoManagerService;
import com.zhenhappy.ems.manager.service.CustomerTemplateService;
import com.zhenhappy.ems.manager.service.ImportExportService;
import com.zhenhappy.ems.service.ExhibitorService;
import com.zhenhappy.ems.manager.service.EmailMailService;
import com.zhenhappy.util.Page;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lianghaijian on 2014-08-12.
 */
@Controller
@SessionAttributes(value = Principle.PRINCIPLE_SESSION_ATTRIBUTE)
@RequestMapping(value = "user")
public class MailAction extends BaseAction {

    private static Logger log = Logger.getLogger(MailAction.class);

    @Autowired
    private ExhibitorService exhibitorService;
    @Autowired
    private CustomerInfoManagerService customerInfoManagerService;
    @Autowired
    private ImportExportService importExportService;

    @Autowired
    private EmailMailService mailService;
    @Autowired
    private CustomerTemplateService customerTemplaeService;
    @Autowired
    TaskExecutor taskExecutor;// 注入Spring封装的异步执行器

    @RequestMapping(value = "sendMail", method = RequestMethod.GET)
    public String sendMail() {
        return "/user/mail/mails";
    }

    @RequestMapping(value = "sendMail", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse sendMail(@RequestParam(value = "context") String context, @ModelAttribute(Principle.PRINCIPLE_SESSION_ATTRIBUTE) Principle principle) {
        BaseResponse baseResponse = new BaseResponse();
        try {

            List<Email> emails = JSONArray.parseArray(context, Email.class);
            String booth = exhibitorService.loadBoothNum(principle.getExhibitor().getEid());
            String company = exhibitorService.getExhibitorByEid(principle.getExhibitor().getEid()).getCompany();
            String companye = exhibitorService.getExhibitorByEid(principle.getExhibitor().getEid()).getCompanye();
            for (Email email : emails) {
                if (email.getFlag() == 1) {
                    email.setSubject(company + "邀请函");
                    email.setBoothNumber(booth);
                    email.setCompany(principle.getExhibitor().getCompany());
                } else {
                    email.setSubject("The invitation Of " + companye);
                    email.setBoothNumber(booth);
                    email.setCompany(principle.getExhibitor().getCompanye());
                }
                mailService.sendMailByAsynchronousMode(email, principle.getExhibitor().getEid());
            }
        } catch (Exception e) {
            baseResponse.setResultCode(1);
        }
        return baseResponse;
    }

    @ResponseBody
    @RequestMapping(value = "emailAllInlandCustomers", method = RequestMethod.POST)
    public BaseResponse emailAllInlandCustomers(@ModelAttribute QueryCustomerRequest request,
                                                 @RequestParam(value = "cids", defaultValue = "") Integer[] eids) {
        BaseResponse baseResponse = new BaseResponse();
        List<TVisitorInfo> customers = new ArrayList<TVisitorInfo>();
        try {
            Email email = new Email();
            if(eids[0] == -1)
            customers = customerInfoManagerService.loadAllInlandCustomer();
            else customers = customerInfoManagerService.loadSelectedCustomers(eids);
            if(customers.size()>0) {
                for(int i=0;i<customers.size();i++) {
                    TVisitorInfo customer = customers.get(i);
                    log.info("======给境内邮箱为：" + customer.getEmail() + "账号发邮件======");
                    if(customer.getIsProfessional()) {
                        email.setFlag(1);//专业采购商
                    } else {
                        email.setFlag(0);//展会观众
                    }
                    email.setSubject("厦门国际佛事用品展览邀请函");
                    email.setCountry(0);
                    email.setCompany(customer.getCompany());
                    email.setName(customer.getFirstName());
                    if(org.apache.commons.lang.StringUtils.isEmpty(customer.getTmp_V_name1())
                            && org.apache.commons.lang.StringUtils.isEmpty(customer.getTmp_V_name2())
                    && org.apache.commons.lang.StringUtils.isEmpty(customer.getTmp_V_name3())) {
                        email.setFollowName("无");
                    } else {
                        email.setFollowName(customer.getTmp_V_name1() + "," + customer.getTmp_V_name2() + "," + customer.getTmp_V_name3());
                    }
                    email.setRegID(customer.getCheckingNo());
                    email.setReceivers(customer.getEmail());
                    //email.setReceivers("datea120@163.com");

                    mailService.sendMailByAsyncAnnotationMode(email);
                }
            } else {
                throw new Exception("Mail can not found");
            }
        } catch (Exception e) {
            baseResponse.setResultCode(1);
        }
        return baseResponse;
    }

    @ResponseBody
    @RequestMapping(value = "emailAllForeignCustomers", method = RequestMethod.POST)
    public BaseResponse emailAllForeignCustomers(@ModelAttribute QueryCustomerRequest request,
                                                @RequestParam(value = "cids", defaultValue = "") Integer[] cids) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            Email email = new Email();
            List<TVisitorInfo> customers = customerInfoManagerService.loadAllForeignCustomer();

            if(customers.size()>0) {
                for(int i=0;i<customers.size();i++) {
                    TVisitorInfo customer = customers.get(i);
                    log.info("======给境外邮箱为：" + customer.getEmail() + "账号发邮件======");
                    if(customer.getIsProfessional()) {
                        email.setFlag(1);//专业采购商
                    } else {
                        email.setFlag(0);//展会观众
                    }

                    email.setSubject("China Xiamen International Buddhist Items & Crafts Fair Invitation");
                    email.setCountry(1);
                    email.setCompany(customer.getCompany());
                    email.setName(customer.getFirstName());
                    if(org.apache.commons.lang.StringUtils.isEmpty(customer.getTmp_V_name1())
                            && org.apache.commons.lang.StringUtils.isEmpty(customer.getTmp_V_name2())
                            && org.apache.commons.lang.StringUtils.isEmpty(customer.getTmp_V_name3())) {
                        email.setFollowName("nobody");
                    } else {
                        email.setFollowName(customer.getTmp_V_name1() + "," + customer.getTmp_V_name2() + "," + customer.getTmp_V_name3());
                    }
                    email.setRegID(customer.getCheckingNo());
                    email.setReceivers(customer.getEmail());

                    mailService.sendMailByAsyncAnnotationMode(email);
                }
            } else {
                throw new Exception("Mail can not found");
            }
        } catch (Exception e) {
            baseResponse.setResultCode(1);
        }
        return baseResponse;
    }

    @ResponseBody
    @RequestMapping(value = "msgAllInlandCustomers", method = RequestMethod.POST)
    public BaseResponse msgAllInlandCustomers(@ModelAttribute QueryCustomerRequest request,
                                                @RequestParam(value = "cids", defaultValue = "") Integer[] eids) {
        BaseResponse baseResponse = new BaseResponse();
        List<TVisitorInfo> customers = new ArrayList<TVisitorInfo>();
        List<WCustomerTemplate> customerTemplatesList = new ArrayList<WCustomerTemplate>();
        String mobileContent = "";
        try {
            if(eids[0] == -1)
                customers = customerInfoManagerService.loadAllInlandCustomer();
            else customers = customerInfoManagerService.loadSelectedCustomers(eids);
            customerTemplatesList = customerTemplaeService.loadAllCustomerTemplate();
            if(customerTemplatesList != null && customerTemplatesList.size()>0){
                for(int k=0;k<customerTemplatesList.size();k++){
                    WCustomerTemplate customerTemplate = customerTemplatesList.get(k);
                    if(customerTemplate.getTpl_key().equals("msg_register_content_cn")){
                        mobileContent = customerTemplate.getTpl_value();
                        break;
                    }
                }
            }
            if(customers.size()>0) {
                StringBuffer mobileStr = new StringBuffer();
                for(int i=0;i<customers.size();i++) {
                    TVisitorInfo customer = customers.get(i);
                    log.info("======给境内手机号为：" + customer.getMobile() + "发短信======");
                    String mobileContentTemp = mobileContent;
                    mobileContent = mobileContent.replace("@@_CHECKINGNUMBER_@@",customer.getCheckingNo());
                    sendMsgByAsynchronousMode(customer.getMobile(), mobileContent);
                    mobileContent = mobileContentTemp;
                }
            } else {
                throw new Exception("mobile can not found");
            }
        } catch (Exception e) {
            baseResponse.setResultCode(1);
        }
        return baseResponse;
    }

    /**
     * 异步发送
     */
    public void sendMsgByAsynchronousMode(final String phoneStr, final String mobileContent) {
        if (log.isDebugEnabled()) {
            log.debug("当前短信采取异步发送....");
        }
        taskExecutor.execute(new Runnable() {
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet post = new HttpGet("http://113.106.91.228:9000/WebService.asmx/mt?Sn=SDK100&Pwd=123321&mobile="
                            + phoneStr/*"13459298520,15880215740,13666033579"*/ + "&content=" + mobileContent);
                    HttpResponse response = httpClient.execute(post);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        log.info("群发短信任务完成");
                    } else {
                        log.error("群发短信失败，错误码：" + response.getStatusLine().getStatusCode());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    @RequestMapping(value = "mails", method = RequestMethod.GET)
    @ResponseBody
    public GetMailSendDetailsResponse getMailSendDetail(@ModelAttribute Page page, @ModelAttribute(Principle.PRINCIPLE_SESSION_ATTRIBUTE) Principle principle) {
        GetMailSendDetailsResponse response = new GetMailSendDetailsResponse();
        try {
            if (page == null) {
                page = new Page();
                page.setPageIndex(1);
                page.setPageSize(10);
            }
            mailService.loadDetailByEid(page, principle.getExhibitor().getEid());
            BeanUtils.copyProperties(page, response);
            response.setDatas(page.getRows());
        } catch (Exception e) {
            log.error("get mails error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    @RequestMapping(value = "previewMail", method = RequestMethod.POST)
    public ModelAndView previewMail(@ModelAttribute("mail") Email email, @ModelAttribute(Principle.PRINCIPLE_SESSION_ATTRIBUTE) Principle principle) {
        ModelAndView modelAndView = new ModelAndView();
        String booth = exhibitorService.loadBoothNum(principle.getExhibitor().getEid());
        if (email.getFlag() == 1) {
            modelAndView.setViewName("/user/mail/VisitorReplay");
            email.setSubject("厦门国际石材展邀请函");
            email.setBoothNumber(booth);
            email.setCompany(principle.getExhibitor().getCompany());
        } else {
            modelAndView.setViewName("/user/mail/VisitorReplay_unPro");
            email.setSubject("The invitation Of China Xiamen International Stone Fair");
            email.setBoothNumber(booth);
            email.setCompany(principle.getExhibitor().getCompanye());
        }
        modelAndView.addObject("email", email);
        return modelAndView;
    }
}
