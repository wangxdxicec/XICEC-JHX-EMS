package com.zhenhappy.ems.manager.action.user;

import com.alibaba.fastjson.JSONArray;
import com.zhenhappy.ems.dao.CustomerApplyEmailInfoDao;
import com.zhenhappy.ems.dao.CustomerApplyMsgInfoDao;
import com.zhenhappy.ems.dto.BaseResponse;
import com.zhenhappy.ems.dto.Principle;
import com.zhenhappy.ems.entity.*;
import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.dto.GetMailSendDetailsResponse;
import com.zhenhappy.ems.manager.dto.ManagerPrinciple;
import com.zhenhappy.ems.manager.service.CustomerInfoManagerService;
import com.zhenhappy.ems.manager.service.CustomerTemplateService;
import com.zhenhappy.ems.manager.tag.StringUtil;
import com.zhenhappy.ems.service.EmailMailService;
import com.zhenhappy.ems.service.ExhibitorService;
import com.zhenhappy.ems.service.VisitorLogMsgService;
import com.zhenhappy.util.EmailPattern;
import com.zhenhappy.util.Page;
import com.zhenhappy.util.ReadWriteEmailAndMsgFile;
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

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lianghaijian on 2014-08-12.
 */
@Controller
@SessionAttributes(value = ManagerPrinciple.MANAGERPRINCIPLE)
@RequestMapping(value = "user")
public class MailAction extends BaseAction {

    private static Logger log = Logger.getLogger(MailAction.class);

    @Autowired
    private ExhibitorService exhibitorService;
    @Autowired
    private CustomerInfoManagerService customerInfoManagerService;

    @Autowired
    private EmailMailService mailService;

    @Autowired
    private CustomerTemplateService customerTemplaeService;

    @Autowired
    TaskExecutor taskExecutor;// 注入Spring封装的异步执行器
    @Autowired
    VisitorLogMsgService visitorLogMsgService;
    @Autowired
    private CustomerApplyEmailInfoDao customerApplyEmailInfoDao;
    @Autowired
    private CustomerApplyMsgInfoDao customerApplyMsgInfoDao;

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
            String company = exhibitorService.query(principle.getExhibitor().getEid()).getCompany();
            String companye = exhibitorService.query(principle.getExhibitor().getEid()).getCompanyEn();
            /*String company = exhibitorService.getExhibitorByEid(principle.getExhibitor().getEid()).getCompany();
            String companye = exhibitorService.getExhibitorByEid(principle.getExhibitor().getEid()).getCompanye();*/
            for (Email email : emails) {
                if (email.getFlag() == 1) {
                    email.setSubject(company + "邀请函");
                    email.setBoothNumber(booth);
                    email.setCompany(company);
                } else {
                    email.setSubject("The invitation Of " + companye);
                    email.setBoothNumber(booth);
                    email.setCompany(companye);
                }
                mailService.sendMailByAsynchronousMode(email, principle.getExhibitor().getEid());
            }
        } catch (Exception e) {
            baseResponse.setResultCode(1);
        }
        return baseResponse;
    }

    @ResponseBody
    @RequestMapping(value = "emailAllInlandStoneCustomers", method = RequestMethod.POST)
    public BaseResponse emailAllInlandStoneCustomers(@RequestParam(value = "cids", defaultValue = "") Integer[] cids,
                                                     @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse baseResponse = new BaseResponse();
        List<WCustomer> customers = new ArrayList<WCustomer>();
        List<TVisitorTemplate> customerTemplatesList = new ArrayList<TVisitorTemplate>();
        try {
            Email email = new Email();
            if(cids[0] == -1){
                customers = customerInfoManagerService.loadAllInlandCustomer();
            }
            else if(cids[0] == -2) {
                List<VApplyEmail> customerApplyEmailList = customerApplyEmailInfoDao.queryByHql("from VApplyEmail where status=?", new Object[]{0});
                if(customerApplyEmailList !=null && customerApplyEmailList.size()>0) {
                    for (int m=0;m<customerApplyEmailList.size();m++){
                        VApplyEmail applyEmail = customerApplyEmailList.get(m);
                        WCustomer customer = customerInfoManagerService.loadCustomerInfoById(applyEmail.getCustomerID());
                        customers.add(customer);
                    }
                }
            }
            else {
                customers = customerInfoManagerService.loadSelectedCustomers(cids);
            }
            if(customers.size()>0) {
                customerTemplatesList = customerTemplaeService.loadAllCustomerTemplate();
                if(customerTemplatesList != null && customerTemplatesList.size()>0){
                    for(int k=0;k<customerTemplatesList.size();k++) {
                        TVisitorTemplate customerTemplate = customerTemplatesList.get(k);
                        if (customerTemplate.getTpl_key().equals("mail_register_subject_cn")) {
                            email.setRegister_subject_cn(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_content_cn")) {
                            email.setRegister_content_cn(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_invite_subject_cn")) {
                            email.setInvite_subject_cn(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_invite_content_cn")) {
                            email.setInvite_content_cn(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_subject_en")) {
                            email.setRegister_subject_en(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_content_en")) {
                            email.setRegister_content_en(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_invite_subject_en")) {
                            email.setInvite_subject_en(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_invite_content_en")) {
                            email.setInvite_content_en(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_policyDeclare_cn")) {
                            email.setPoliceDecareCn(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_policyDeclare_en")) {
                            email.setPoliceDecareEn(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_subject_cn_unpro")) {
                            email.setMail_register_subject_cn_unpro(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_content_cn_unpro")) {
                            email.setMail_register_content_cn_unpro(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_subject_en_unpro")) {
                            email.setMail_register_subject_en_unpro(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_content_en_unpro")) {
                            email.setMail_register_content_en_unpro(customerTemplate.getTpl_value());
                        }
                    }
                }
                ReadWriteEmailAndMsgFile.creatTxtFile(ReadWriteEmailAndMsgFile.stoneEmailFileName);
                for(int i=0;i<customers.size();i++) {
                    WCustomer customer = customers.get(i);
                    EmailPattern pattern = new EmailPattern();
                    if(customer != null && pattern.isEmailPattern(customer.getEmail())) {
                        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm:ss");
                        Date date = new Date();
                        String str = bartDateFormat.format(date);
                        ReadWriteEmailAndMsgFile.setFileContentIsNull();
                        ReadWriteEmailAndMsgFile.readTxtFile(ReadWriteEmailAndMsgFile.stoneEmailFileName);
                        ReadWriteEmailAndMsgFile.writeTxtFile(str + ", 给邮箱为：" + customer.getEmail() + "账号发邮件。", ReadWriteEmailAndMsgFile.stoneEmailFileName);
                        if(StringUtil.isNotEmpty(customer.getBackupEmail())){
                            email.setBackupReceivers(customer.getBackupEmail());
                            ReadWriteEmailAndMsgFile.writeTxtFile(str + ", 给邮箱为：" + customer.getBackupEmail() + "账号发邮件。", ReadWriteEmailAndMsgFile.stoneEmailFileName);
                        }
                        //log.info("======给境内邮箱为：" + customer.getEmail() + "账号发邮件======");
                        if(customer.getIsProfessional() == 1) {
                            email.setFlag(1);//专业采购商
                        } else {
                            email.setFlag(0);//展会观众
                        }

                        email.setEmailType(customer.getIsProfessional());
                        email.setCheckingNo(customer.getCheckingNo());
                        email.setCustomerId(customer.getId());
                        email.setCountry(customer.getCountry() == 44 ? 0:1);
                        email.setUseTemplate(false);
                        email.setCompany(customer.getCompany());
                        email.setName(customer.getFirstName());
                        if(customer.getPosition() == null || customer.getPosition() == ""){
                            email.setPosition("");
                        } else {
                            email.setPosition(customer.getPosition());
                        }
                        email.setRegID(customer.getCheckingNo());
                        email.setReceivers(customer.getEmail());
                        //email.setReceivers("datea120@163.com");

                        customerInfoManagerService.updateCustomerEmailNum(customer.getId());
                        if(StringUtil.isNotEmpty(customer.getBackupEmail())){
                            customerInfoManagerService.updateCustomerEmailNum(customer.getId());
                        }
                        mailService.sendMailByAsyncAnnotationMode(email);

                        List<VApplyEmail> customerApplyEmailList = customerApplyEmailInfoDao.queryByHql("from VApplyEmail where CustomerID=?", new Object[]{customer.getId()});
                        if(customerApplyEmailList != null && customerApplyEmailList.size()>0){
                            for(int k=0;k<customerApplyEmailList.size();k++){
                                VApplyEmail applyEmail = customerApplyEmailList.get(k);
                                if(principle.getAdmin() != null){
                                    applyEmail.setAdmin(principle.getAdmin().getName());
                                }
                                applyEmail.setStatus(1);
                                applyEmail.setConfirmTime(new Date());
                                applyEmail.setConfirmIP(InetAddress.getLocalHost().getHostAddress());
                                customerApplyEmailInfoDao.update(applyEmail);
                            }
                        }
                    }
                }
            } else {
                throw new Exception("Mail can not found");
            }
        } catch (Exception e) {
            System.out.println("=====exception: " + e);
            baseResponse.setResultCode(1);
        }
        return baseResponse;
    }

    @ResponseBody
    @RequestMapping(value = "emailAllForeignStoneCustomers", method = RequestMethod.POST)
    public BaseResponse emailAllForeignStoneCustomers(@RequestParam(value = "cids", defaultValue = "") Integer[] cids,
                                                      @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse baseResponse = new BaseResponse();
        List<WCustomer> customers = new ArrayList<WCustomer>();
        try {
            Email email = new Email();
            if(cids[0] == -1){
                customers = customerInfoManagerService.loadAllForeignCustomer();
            }
            else if(cids[0] == -2) {
                List<VApplyEmail> customerApplyEmailList = customerApplyEmailInfoDao.queryByHql("from VApplyEmail where status=?", new Object[]{0});
                if(customerApplyEmailList !=null && customerApplyEmailList.size()>0) {
                    for (int m=0;m<customerApplyEmailList.size();m++){
                        VApplyEmail applyEmail = customerApplyEmailList.get(m);
                        WCustomer customer = customerInfoManagerService.loadCustomerInfoById(applyEmail.getCustomerID());
                        customers.add(customer);
                    }
                }
            }
            else {
                customers = customerInfoManagerService.loadSelectedCustomers(cids);
            }
            List<TVisitorTemplate> customerTemplatesList = new ArrayList<TVisitorTemplate>();
            if(customers.size()>0) {
                customerTemplatesList = customerTemplaeService.loadAllCustomerTemplate();
                if(customerTemplatesList != null && customerTemplatesList.size()>0){
                    for(int k=0;k<customerTemplatesList.size();k++){
                        TVisitorTemplate customerTemplate = customerTemplatesList.get(k);
                        if (customerTemplate.getTpl_key().equals("mail_register_subject_cn")) {
                            email.setRegister_subject_cn(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_content_cn")) {
                            email.setRegister_content_cn(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_invite_subject_cn")) {
                            email.setInvite_subject_cn(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_invite_content_cn")) {
                            email.setInvite_content_cn(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_subject_en")) {
                            email.setRegister_subject_en(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_content_en")) {
                            email.setRegister_content_en(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_invite_subject_en")) {
                            email.setInvite_subject_en(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_invite_content_en")) {
                            email.setInvite_content_en(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_policyDeclare_cn")) {
                            email.setPoliceDecareCn(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_policyDeclare_en")) {
                            email.setPoliceDecareEn(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_subject_cn_unpro")) {
                            email.setMail_register_subject_cn_unpro(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_content_cn_unpro")) {
                            email.setMail_register_content_cn_unpro(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_subject_en_unpro")) {
                            email.setMail_register_subject_en_unpro(customerTemplate.getTpl_value());
                        }
                        if (customerTemplate.getTpl_key().equals("mail_register_content_en_unpro")) {
                            email.setMail_register_content_en_unpro(customerTemplate.getTpl_value());
                        }
                    }
                }
                ReadWriteEmailAndMsgFile.creatTxtFile(ReadWriteEmailAndMsgFile.stoneEmailFileName);
                for(int i=0;i<customers.size();i++) {
                    WCustomer customer = customers.get(i);
                    EmailPattern pattern = new EmailPattern();
                    if(customer != null && pattern.isEmailPattern(customer.getEmail())) {
                        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm:ss");
                        Date date = new Date();
                        String str = bartDateFormat.format(date);
                        ReadWriteEmailAndMsgFile.setFileContentIsNull();
                        ReadWriteEmailAndMsgFile.readTxtFile(ReadWriteEmailAndMsgFile.stoneEmailFileName);
                        ReadWriteEmailAndMsgFile.writeTxtFile(str + ", 给邮箱为：" + customer.getEmail() + "账号发邮件。", ReadWriteEmailAndMsgFile.stoneEmailFileName);
                        //log.info("======给境外邮箱为：" + customer.getEmail() + "账号发邮件======");
                        if(customer.getIsProfessional() == 1) {
                            email.setFlag(1);//专业采购商
                        } else {
                            email.setFlag(0);//展会观众
                        }

                        email.setEmailType(customer.getIsProfessional());
                        email.setCheckingNo(customer.getCheckingNo());
                        email.setCustomerId(customer.getId());
                        email.setCountry(customer.getCountry() == 44 ? 0:1);
                        email.setUseTemplate(false);
                        email.setCompany(customer.getCompany());
                        email.setName(customer.getFirstName());
                        if(customer.getPosition() == null || customer.getPosition() == ""){
                            email.setPosition("");
                        } else {
                            email.setPosition(customer.getPosition());
                        }
                        email.setRegID(customer.getCheckingNo());
                        email.setReceivers(customer.getEmail());
                        //email.setReceivers("datea120@163.com");

                        customerInfoManagerService.updateCustomerEmailNum(customer.getId());
                        mailService.sendMailByAsyncAnnotationMode(email);

                        List<VApplyEmail> customerApplyEmailList = customerApplyEmailInfoDao.queryByHql("from VApplyEmail where CustomerID=?", new Object[]{customer.getId()});
                        if(customerApplyEmailList != null && customerApplyEmailList.size()>0){
                            for(int k=0;k<customerApplyEmailList.size();k++){
                                VApplyEmail applyEmail = customerApplyEmailList.get(k);
                                if(principle.getAdmin() != null){
                                    applyEmail.setAdmin(principle.getAdmin().getName());
                                }
                                applyEmail.setStatus(1);
                                applyEmail.setConfirmTime(new Date());
                                applyEmail.setConfirmIP(InetAddress.getLocalHost().getHostAddress());
                                customerApplyEmailInfoDao.update(applyEmail);
                            }
                        }
                    }
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
    @RequestMapping(value = "msgAllInlandStoneCustomers", method = RequestMethod.POST)
    public BaseResponse msgAllInlandStoneCustomers(@RequestParam(value = "cids", defaultValue = "") Integer[] cids,
                                                   @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse baseResponse = new BaseResponse();
        List<WCustomer> customers = new ArrayList<WCustomer>();
        String mobileContent = "";
        String mobileSubject = "";
        try {
            if(cids[0] == -1){
                customers = customerInfoManagerService.loadAllInlandCustomer();
            }
            else if(cids[0] == -2) {
                List<VApplyMsg> customerApplyMsgList = customerApplyMsgInfoDao.queryByHql("from VApplyMsg where status=?", new Object[]{0});
                if(customerApplyMsgList !=null && customerApplyMsgList.size()>0) {
                    for (int m=0;m<customerApplyMsgList.size();m++){
                        VApplyMsg applyMsg = customerApplyMsgList.get(m);
                        WCustomer customer = customerInfoManagerService.loadCustomerInfoById(applyMsg.getCustomerID());
                        customers.add(customer);
                    }
                }
            }
            else {
                customers = customerInfoManagerService.loadSelectedCustomers(cids);
            }
            List<TVisitorTemplate> customerTemplatesList = customerTemplaeService.loadAllCustomerTemplate();
            if(customerTemplatesList != null && customerTemplatesList.size()>0){
                for(int k=0;k<customerTemplatesList.size();k++){
                    TVisitorTemplate customerTemplate = customerTemplatesList.get(k);
                    if(customerTemplate.getTpl_key().equals("msg_register_content_cn")){
                        mobileContent = customerTemplate.getTpl_value();
                    } else if(customerTemplate.getTpl_key().equals("msg_register_subject_cn")) {
                        mobileSubject = customerTemplate.getTpl_value();
                    }
                }
            }
            if(customers.size()>0) {
                StringBuffer mobileStr = new StringBuffer();
                ReadWriteEmailAndMsgFile.creatTxtFile(ReadWriteEmailAndMsgFile.stoneMsgFileName);
                for(int i=0;i<customers.size();i++) {
                    TVisitorMsgLog visitorMsgLog = new TVisitorMsgLog();
                    WCustomer customer = customers.get(i);
                    EmailPattern pattern = new EmailPattern();
                    if(customer != null && pattern.isMobileNO(customer.getMobilePhone())) {
                        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm:ss");
                        Date date = new Date();
                        String str = bartDateFormat.format(date);
                        ReadWriteEmailAndMsgFile.setFileContentIsNull();
                        ReadWriteEmailAndMsgFile.readTxtFile(ReadWriteEmailAndMsgFile.stoneMsgFileName);
                        ReadWriteEmailAndMsgFile.writeTxtFile(str + ", 给境内手机号为：" + customer.getMobilePhone() + "发短信。", ReadWriteEmailAndMsgFile.stoneMsgFileName);
                        //log.info("======给境内手机号为：" + customer.getMobilePhone() + "发短信======");
                        String mobileContentTemp = mobileContent;
                        mobileContent = mobileContent.replace("@@_CHECKINGNUMBER_@@",customer.getCheckingNo());
                        sendMsgByAsynchronousMode(customer, mobileContent);
                        visitorMsgLog.setMsgContent(mobileContent);
                        mobileContent = mobileContentTemp;

                        visitorMsgLog.setCreateTime(new Date());
                        visitorMsgLog.setLogSubject("");
                        visitorMsgLog.setReply(0);
                        visitorMsgLog.setLogSubject("");
                        visitorMsgLog.setLogContent("");
                        visitorMsgLog.setGUID("");
                        visitorMsgLog.setMsgSubject(mobileSubject);
                        visitorMsgLog.setMsgFrom("");
                        visitorMsgLog.setMsgTo(customer.getMobilePhone());
                        visitorMsgLog.setStatus(0);
                        visitorMsgLog.setCustomerID(customer.getId());
                        visitorLogMsgService.insertLogMsg(visitorMsgLog);

                        List<VApplyMsg> customerApplyMsgList = customerApplyMsgInfoDao.queryByHql("from VApplyMsg where CustomerID=?", new Object[]{customer.getId()});
                        if(customerApplyMsgList != null && customerApplyMsgList.size()>0){
                            for(int k=0;k<customerApplyMsgList.size();k++){
                                VApplyMsg applyMsg = customerApplyMsgList.get(k);
                                if(principle.getAdmin() != null){
                                    applyMsg.setAdmin(principle.getAdmin().getName());
                                }
                                applyMsg.setStatus(1);
                                applyMsg.setConfirmTime(new Date());
                                applyMsg.setConfirmIP(InetAddress.getLocalHost().getHostAddress());
                                customerApplyMsgInfoDao.update(applyMsg);
                            }
                        }
                    }
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
    public void sendMsgByAsynchronousMode(final WCustomer customer, final String mobileContent) {
        if (log.isDebugEnabled()) {
            log.debug("当前短信采取异步发送....");
        }
        final String phone = customer.getMobilePhone();
        final Integer cusId = customer.getId();
        taskExecutor.execute(new Runnable() {
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet post = new HttpGet("http://113.106.91.228:9000/WebService.asmx/mt?Sn=SDK100&Pwd=123321&mobile="
                            + phone + "&content=" + mobileContent);
                    HttpResponse response = httpClient.execute(post);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        customerInfoManagerService.updateCustomerMsgNum(cusId);
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
        String company = exhibitorService.query(principle.getExhibitor().getEid()).getCompany();
        String companye = exhibitorService.query(principle.getExhibitor().getEid()).getCompanyEn();
        if (email.getFlag() == 1) {
            modelAndView.setViewName("/user/mail/VisitorReplay");
            email.setSubject("厦门国际石材展邀请函");
            email.setBoothNumber(booth);
            email.setCompany(company);
        } else {
            modelAndView.setViewName("/user/mail/VisitorReplay_unPro");
            email.setSubject("The invitation Of China Xiamen International Stone Fair");
            email.setBoothNumber(booth);
            email.setCompany(companye);
        }
        modelAndView.addObject("email", email);
        return modelAndView;
    }

    public static void main(String[] args) throws IOException {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher("13459298520");
        System.out.println(m.matches());
    }
}
