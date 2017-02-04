package com.zhenhappy.ems.manager.action.user;

import com.zhenhappy.ems.dao.CustomerApplyMsgInfoDao;
import com.zhenhappy.ems.dao.TeaCustomerInfoDao;
import com.zhenhappy.ems.dto.BaseResponse;
import com.zhenhappy.ems.entity.*;
import com.zhenhappy.ems.entity.managerrole.TUserInfo;
import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.dto.ManagerPrinciple;
import com.zhenhappy.ems.manager.service.CustomerInfoManagerService;
import com.zhenhappy.ems.manager.service.CustomerTemplateService;
import com.zhenhappy.ems.manager.sys.Constants;
import com.zhenhappy.ems.manager.util.StringUtil;
import com.zhenhappy.ems.service.EmailMailService;
import com.zhenhappy.ems.service.VisitorLogMsgService;
import com.zhenhappy.system.SystemConfig;
import com.zhenhappy.util.EmailPattern;
import com.zhenhappy.util.ReadWriteEmailAndMsgFile;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangxd on 2017-01-5.
 */
@Controller
@SessionAttributes(value = ManagerPrinciple.MANAGERPRINCIPLE)
@RequestMapping(value = "user")
public class MailAction extends BaseAction {

    private static Logger log = Logger.getLogger(MailAction.class);

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
    FreeMarkerConfigurer freeMarker;// 注入FreeMarker模版封装框架
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    private CustomerApplyMsgInfoDao customerApplyMsgInfoDao;
    @Autowired
    private TeaCustomerInfoDao customerInfoDao;

    private Email initEmailTemplate() {
        Email email = new Email();
        List<TVisitorTemplate> customerTemplatesList = customerTemplaeService.loadAllCustomerTemplate();
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
        return email;
    }

    @ResponseBody
    @RequestMapping(value = "emailAllInlandTeaCustomers", method = RequestMethod.POST)
    public BaseResponse emailAllInlandTeaCustomers(@RequestParam(value = "cids", defaultValue = "") Integer[] cids,
                                                     @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse baseResponse = new BaseResponse();
        List<TTeaVisitorInfo> customers = new ArrayList<TTeaVisitorInfo>();
        try {
            if(cids[0] == -1){
                customers = customerInfoManagerService.loadAllInlandCustomer();
            } else {
                customers = customerInfoManagerService.loadSelectedCustomers(cids);
            }
            if(customers.size()>0) {
                ReadWriteEmailAndMsgFile.creatTxtFile(ReadWriteEmailAndMsgFile.teaEmailFileName);
                for(int i=0;i<customers.size();i++) {
                    Email email = initEmailTemplate();
                    TTeaVisitorInfo customer = customers.get(i);
                    EmailPattern pattern = new EmailPattern();
                    if(customer != null && pattern.isEmailPattern(customer.getEmail())) {
                        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm:ss");
                        Date date = new Date();
                        String str = bartDateFormat.format(date);
                        ReadWriteEmailAndMsgFile.setFileContentIsNull();
                        ReadWriteEmailAndMsgFile.readTxtFile(ReadWriteEmailAndMsgFile.teaEmailFileName);
                        ReadWriteEmailAndMsgFile.writeTxtFile(str + ", 给邮箱为：" + customer.getEmail() + "账号发邮件。", ReadWriteEmailAndMsgFile.teaEmailFileName);
                        if(StringUtil.isNotEmpty(customer.getBackupEmail())){
                            email.setBackupReceivers(customer.getBackupEmail());
                            ReadWriteEmailAndMsgFile.writeTxtFile(str + ", 给邮箱为：" + customer.getBackupEmail() + "账号发邮件。", ReadWriteEmailAndMsgFile.teaEmailFileName);
                        }

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

                        customerInfoManagerService.updateCustomerEmailNum(customer.getId());
                        if(StringUtil.isNotEmpty(customer.getBackupEmail())){
                            customerInfoManagerService.updateCustomerEmailNum(customer.getId());
                        }
                        mailService.sendMailByAsyncAnnotationMode(email);
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
        List<TTeaVisitorInfo> customers = new ArrayList<TTeaVisitorInfo>();
        try {
            if(cids[0] == -1){
                customers = customerInfoManagerService.loadAllForeignCustomer();
            }else {
                customers = customerInfoManagerService.loadSelectedCustomers(cids);
            }
            if(customers.size()>0) {
                ReadWriteEmailAndMsgFile.creatTxtFile(ReadWriteEmailAndMsgFile.teaEmailFileName);
                for(int i=0;i<customers.size();i++) {
                    Email email = initEmailTemplate();
                    TTeaVisitorInfo customer = customers.get(i);
                    EmailPattern pattern = new EmailPattern();
                    if(customer != null && pattern.isEmailPattern(customer.getEmail())) {
                        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm:ss");
                        Date date = new Date();
                        String str = bartDateFormat.format(date);
                        ReadWriteEmailAndMsgFile.setFileContentIsNull();
                        ReadWriteEmailAndMsgFile.readTxtFile(ReadWriteEmailAndMsgFile.teaEmailFileName);
                        ReadWriteEmailAndMsgFile.writeTxtFile(str + ", 给邮箱为：" + customer.getEmail() + "账号发邮件。", ReadWriteEmailAndMsgFile.teaEmailFileName);
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

                        customerInfoManagerService.updateCustomerEmailNum(customer.getId());
                        mailService.sendMailByAsyncAnnotationMode(email);

                        //邮件申请列表，更新对应的申请状态
                        /*List<VApplyEmail> customerApplyEmailList = customerApplyEmailInfoDao.queryByHql("from VApplyEmail where CustomerID=?", new Object[]{customer.getId()});
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
                        }*/
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

    /**
     * 下载邮件内容
     * @param cid
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/exportEmailContentToZip")
    public ModelAndView exportEmailContentToZip(@RequestParam(value = "cids") Integer cid,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        String appendix_directory = systemConfig.getVal(Constants.appendix_directory).replaceAll("\\\\\\\\", "\\\\");
        String randomFile = UUID.randomUUID().toString();
        String destDir = appendix_directory + "\\tmp\\emailContent\\";
        FileUtils.forceMkdir(new File(destDir)); // 创建临时文件夹

        TTeaVisitorInfo customer = customerInfoManagerService.loadCustomerInfoById(cid);
        EmailPattern pattern = new EmailPattern();
        if(customer != null && pattern.isEmailPattern(customer.getEmail())) {
            Email email = initEmailTemplate();
            if(customer.getIsProfessional() == 1) {
                email.setFlag(1);//专业采购商
            } else {
                email.setFlag(0);//展会观众
            }

            email.setEmailType(customer.getIsProfessional());
            email.setCheckingNo(customer.getCheckingNo());
            email.setCustomerId(customer.getId());
            email.setCountry(customer.getCountry() == 44 ? 0:1);
            email.setCompany(customer.getCompany());
            email.setName(customer.getFirstName());
            if(customer.getPosition() == null || customer.getPosition() == ""){
                email.setPosition("");
            } else {
                email.setPosition(customer.getPosition());
            }
            email.setRegID(customer.getCheckingNo());
            email.setReceivers(customer.getEmail());

            String emailContentHtml = getMailContentByAsyncAnnotationMode(email);
            String inputFile = destDir + customer.getCompany() + ".html";
            OutputStream os = new FileOutputStream(new File(inputFile));
            os.write(emailContentHtml.getBytes());
            os.close();
        }
        return downloadEmailContent(destDir, customer.getCompany(), request, response);
    }

    public ModelAndView downloadEmailContent(String destDir, String zipName, HttpServletRequest request, HttpServletResponse response) throws Exception{
        String filePath = destDir + "\\" + zipName + ".html";  //获取完整的文件名
        try {
            File file = new File(filePath);
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);//得到文件名
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");//把文件名按UTF-8取出并按ISO8859-1编码，保证弹出窗口中的文件名中文不乱码，中文不要太多，最多支持17个中文，因为header有150个字节限制。
            response.setContentType("application/octet-stream");//告诉浏览器输出内容为流
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);//Content-Disposition中指定的类型是文件的扩展名，并且弹出的下载对话框中的文件类型图片是按照文件的扩展名显示的，点保存后，文件以filename的值命名，保存类型以Content中设置的为准。注意：在设置Content-Disposition头字段之前，一定要设置Content-Type头字段。
            String len = String.valueOf(file.length());
            response.setHeader("Content-Length", len);//设置内容长度
            OutputStream out = response.getOutputStream();
            FileInputStream in = new FileInputStream(file);
            byte[] b = new byte[1024];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getMailContentByAsyncAnnotationMode(Email email) {
        if (email.getReceivers() == null) {
            throw new IllegalArgumentException("收件人不能为空");
        }

        String emailContent = "";
        if(email.getEmailType() ==0){
            if(email.getCountry() == 0){
                emailContent = email.getMail_register_content_cn_unpro().replace("@@_NAME_@@",email.getName());
            } else {
                emailContent = email.getMail_register_content_en_unpro().replace("@@_NAME_@@",email.getName());
            }
        } else {
            if(email.getCountry() == 0){
                email.setRegister_content_cn(email.getRegister_content_cn().replace("@@_NAME_@@",email.getName()));
                email.setRegister_content_cn(email.getRegister_content_cn().replace("@@_FAIRNAME_@@","第十七届中国厦门国际石材展览会"));
                email.setRegister_content_cn(email.getRegister_content_cn().replace("@@_COMPANY_@@",email.getCompany()));
                email.setRegister_content_cn(email.getRegister_content_cn().replace("@@_NAME_@@",email.getName()));
                email.setRegister_content_cn(email.getRegister_content_cn().replace("@@_POSITION_@@",email.getPosition()));
                email.setRegister_content_cn(email.getRegister_content_cn().replace("@@_QRCODEURL_@@","http://www.stonefair.org.cn/UploadFile/QRCode/" + email.getCheckingNo() + ".jpg"));
                email.setRegister_content_cn(email.getRegister_content_cn().replace("@@_CHECKINGNUMBER_@@",email.getRegID()));
                email.setRegister_content_cn(email.getRegister_content_cn().replace("@@_YEAR_@@","2017"));
                email.setRegister_content_cn(email.getRegister_content_cn().replace("@@_POLICY_DECLARE_@@",email.getPoliceDecareCn()));
                emailContent = email.getRegister_content_cn();
            } else {
                email.setRegister_content_en(email.getRegister_content_en().replace("@@_NAME_@@",email.getName()));
                email.setRegister_content_en(email.getRegister_content_en().replace("@@_FAIRNAME_@@","Xiamen Stone Fair 2017"));
                email.setRegister_content_en(email.getRegister_content_en().replace("@@_COMPANY_@@",email.getCompany()));
                email.setRegister_content_en(email.getRegister_content_en().replace("@@_NAME_@@",email.getName()));
                email.setRegister_content_en(email.getRegister_content_en().replace("@@_POSITION_@@",email.getPosition()));
                email.setRegister_content_en(email.getRegister_content_en().replace("@@_QRCODEURL_@@","http://www.stonefair.org.cn/UploadFile/QRCode/" + email.getCheckingNo() + ".jpg"));
                email.setRegister_content_en(email.getRegister_content_en().replace("@@_CHECKINGNUMBER_@@",email.getRegID()));
                email.setRegister_content_en(email.getRegister_content_en().replace("@@_YEAR_@@","2017"));
                email.setRegister_content_en(email.getRegister_content_en().replace("@@_POLICY_DECLARE_@@",email.getPoliceDecareEn()));
                emailContent = email.getRegister_content_en();
            }
        }
        return emailContent;
    }

    //type字段：1：已预登记(已激活)；2：表示已预登记(未激活)；3：表示未预登记
    @ResponseBody
    @RequestMapping(value = "msgAllInlandTeaCustomers", method = RequestMethod.POST)
    public BaseResponse msgAllInlandTeaCustomers(@RequestParam(value = "cids", defaultValue = "") Integer[] cids,
                                                   @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse baseResponse = new BaseResponse();
        List<TTeaVisitorInfo> customers = new ArrayList<TTeaVisitorInfo>();
        try {
            if(cids[0] == -1){
                customers = customerInfoManagerService.loadAllInlandCustomer();
            }
            else if(cids[0] == -2) {
                List<VApplyMsg> customerApplyMsgList = customerApplyMsgInfoDao.queryByHql("from VApplyMsg where status=?", new Object[]{0});
                if(customerApplyMsgList !=null && customerApplyMsgList.size()>0) {
                    for (int m=0;m<customerApplyMsgList.size();m++){
                        VApplyMsg applyMsg = customerApplyMsgList.get(m);
                        TTeaVisitorInfo customer = customerInfoManagerService.loadCustomerInfoById(applyMsg.getCustomerID());
                        customers.add(customer);
                    }
                }
            }
            else {
                customers = customerInfoManagerService.loadSelectedCustomers(cids);
            }

            if(customers.size()>0) {
                StringBuffer mobileStr = new StringBuffer();
                ReadWriteEmailAndMsgFile.creatTxtFile(ReadWriteEmailAndMsgFile.teaMsgFileName);
                for(int i=0;i<customers.size();i++) {
                    int type = 1;
                    TVisitorMsgLog visitorMsgLog = new TVisitorMsgLog();
                    TTeaVisitorInfo customer = customers.get(i);
                    if(customer.getIsDisabled()){
                        type = 2;
                    }
                    String mobileSubject = getMsgSubject(type);
                    String mobileContent = getMsgContent(type);
                    EmailPattern pattern = new EmailPattern();
                    if(customer != null) {
                        String telphoneTemp = customer.getMobilePhone();
                        if(telphoneTemp.contains("/")){
                            String[] telphoneArray = telphoneTemp.split("/");
                            for(String telphoneValue:telphoneArray){
                                if(pattern.isMobileNO(telphoneValue)){
                                    sendMsgByTelphone(principle, customer, mobileSubject, mobileContent, telphoneValue, type);
                                }
                            }
                        }else{
                            if(pattern.isMobileNO(telphoneTemp)){
                                sendMsgByTelphone(principle, customer, mobileSubject, mobileContent, telphoneTemp, type);
                            }
                        }
                    }
                    customer.setUpdateTime(new Date());
                    customerInfoDao.update(customer);
                }
            } else {
                throw new Exception("mobile can not found");
            }
        } catch (Exception e) {
            baseResponse.setResultCode(1);
        }
        return baseResponse;
    }

    private void sendMsgByTelphone(ManagerPrinciple principle, TTeaVisitorInfo customer, String mobileSubject,
                                   String mobileContent, String telphone, Integer content){
        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm:ss");
        Date date = new Date();
        String str = bartDateFormat.format(date);
        ReadWriteEmailAndMsgFile.setFileContentIsNull();
        ReadWriteEmailAndMsgFile.readTxtFile(ReadWriteEmailAndMsgFile.teaMsgFileName);
        try {
            ReadWriteEmailAndMsgFile.writeTxtFile(str + ", 给境内手机号为：" + telphone + "发短信。", ReadWriteEmailAndMsgFile.teaMsgFileName);
            //log.info("======给境内手机号为：" + customer.getMobilePhone() + "发短信======");
            String mobileContentTemp = mobileContent;
            if(1 == content && customer != null && !customer.getIsDisabled()) {
                mobileContent = mobileContentTemp.replace("@@_CHECKINGNUMBER_@@",customer.getCheckingNo());
            }else if(2 == content && customer != null && customer.getIsDisabled()){
                mobileContent = mobileContentTemp.replace("@@_username_@@",customer.getFirstName());
            }
            //发送短信
            if(customer != null && ((1== content && !customer.getIsDisabled()) || (2 == content && customer.getIsDisabled()))){
                sendMsgByAsynchronousMode(principle, customer, customer.getMobilePhone(), customer.getId(), mobileContent, mobileSubject, content);
            }
        } catch (IOException e) {
            log.info("======给境内手机号为：" + telphone + "发短信，出错了======");
            e.printStackTrace();
        }
    }

    /**
     * 异步发送
     */
    public void sendMsgByAsynchronousMode(final ManagerPrinciple principle, final TTeaVisitorInfo customer, final String phone,
                                          final Integer cusId, final String mobileContent, final String mobileSubject, final Integer type) {
        if (log.isDebugEnabled()) {
            log.debug("当前短信采取异步发送....");
        }
        taskExecutor.execute(new Runnable() {
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet post = new HttpGet("http://113.106.91.228:9000/WebService.asmx/mt2?Sn=SDK100&Pwd=123321&mobile="
                            + phone + "&content=" + mobileContent);
                    HttpResponse response = httpClient.execute(post);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        saveMsgInfoLog(principle, customer, mobileContent, mobileSubject, phone, type);
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

    private void saveMsgInfoLog(ManagerPrinciple principle, TTeaVisitorInfo customer, String mobileContent,
                                String mobileSubject, String telphone, Integer content) {
        TVisitorMsgLog visitorMsgLog = new TVisitorMsgLog();
        visitorMsgLog.setMsgContent(mobileContent);

        visitorMsgLog.setCreateTime(new Date());
        visitorMsgLog.setLogSubject("");
        visitorMsgLog.setReply(0);
        visitorMsgLog.setLogSubject("");
        visitorMsgLog.setLogContent("");
        visitorMsgLog.setGUID("");
        visitorMsgLog.setMsgSubject(mobileSubject);
        visitorMsgLog.setMsgFrom("");
        visitorMsgLog.setMsgTo(telphone);
        visitorMsgLog.setStatus(0);
        if(customer != null){
            visitorMsgLog.setCustomerID(customer.getId());
        }
        visitorLogMsgService.insertLogMsg(visitorMsgLog);

        if(3 != content){
            List<VApplyMsg> customerApplyMsgList = customerApplyMsgInfoDao.queryByHql("from VApplyMsg where CustomerID=?", new Object[]{customer.getId()});
            if(customerApplyMsgList != null && customerApplyMsgList.size()>0){
                for(int k=0;k<customerApplyMsgList.size();k++){
                    VApplyMsg applyMsg = customerApplyMsgList.get(k);
                    if(principle != null && principle.getAdmin() != null){
                        applyMsg.setAdmin(principle.getAdmin().getName());
                    }
                    applyMsg.setStatus(1);
                    applyMsg.setConfirmTime(new Date());
                    try {
                        applyMsg.setConfirmIP(InetAddress.getLocalHost().getHostAddress());
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    customerApplyMsgInfoDao.update(applyMsg);
                }
            }
        }
    }

    //获取短信标题
    private String getMsgSubject(Integer content){
        String mobileSubject = "";
        List<TVisitorTemplate> customerTemplatesList = customerTemplaeService.loadAllCustomerTemplate();
        if(customerTemplatesList != null && customerTemplatesList.size()>0){
            for(int k=0;k<customerTemplatesList.size();k++){
                TVisitorTemplate customerTemplate = customerTemplatesList.get(k);
                if(1 == content && customerTemplate.getTpl_key().equals("msg_register_subject_cn")) {
                    mobileSubject = customerTemplate.getTpl_value();
                }else if(2 == content && customerTemplate.getTpl_key().equals("msg_unactive_subject_cn")) {
                    mobileSubject = customerTemplate.getTpl_value();
                }else if(3 == content && customerTemplate.getTpl_key().equals("msg_unregister_subject_cn")) {
                    mobileSubject = customerTemplate.getTpl_value();
                }
            }
        }
        return mobileSubject;
    }

    //获取短信内容
    private String getMsgContent(Integer content){
        String mobileContent = "";
        List<TVisitorTemplate> customerTemplatesList = customerTemplaeService.loadAllCustomerTemplate();
        if(customerTemplatesList != null && customerTemplatesList.size()>0){
            for(int k=0;k<customerTemplatesList.size();k++){
                TVisitorTemplate customerTemplate = customerTemplatesList.get(k);
                if(1 == content && customerTemplate.getTpl_key().equals("msg_register_content_cn")) {
                    mobileContent = customerTemplate.getTpl_value();
                }else if(2 == content && customerTemplate.getTpl_key().equals("msg_unactive_content_cn")) {
                    mobileContent = customerTemplate.getTpl_value();
                }else if(3 == content && customerTemplate.getTpl_key().equals("msg_unregister_content_cn")) {
                    mobileContent = customerTemplate.getTpl_value();
                }
            }
        }
        return mobileContent;
    }

    /**
     * 接收客商短信回复内容
     * @param phone   对应的手机号
     * @param content 回复内容
     * @param type    类别：1：表示客商；2：表示展商
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "msgReturnContent")
    public int msgReturnContent(@RequestParam("phone") String phone,
                                @RequestParam("content") Integer content,
                                @RequestParam("type") Integer type) {
        int result = 0;

        if(StringUtil.isNotEmpty(phone)){
            String mobileSubject = "";
            String mobileContent = "";
            if(1 == content) {
                mobileContent = "您已成功预登记，确认号为@@_CHECKINGNUMBER_@@。凭此号在现场领取专业观众证。厦门茶业展展览时间：2017.10.12-16，地址：厦门会展中心，真诚期待您的光临！【金泓信展览】";
            }

            List<TTeaVisitorInfo> wCustomerList = customerInfoManagerService.loadCustomerByPhone(phone);
            if(wCustomerList != null && wCustomerList.size()>0 && 1 == content){
                TTeaVisitorInfo customer = wCustomerList.get(0);
                //如果是已经激活的，则不再发短信
                if(customer.getIsDisabled()){
                    //设置2表示短信激活
                    customer.setIsDisabled(false);
                    customer.setUpdateTime(new Date());
                    customerInfoDao.update(customer);

                    sendMsgByTelphone(null, customer, mobileSubject, mobileContent, phone, content);

                    result = 1;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {

    }
}
