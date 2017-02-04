package com.zhenhappy.ems.manager.action.user;

import com.zhenhappy.ems.entity.TVisitorInfo;
import com.zhenhappy.ems.entity.WCustomer;
import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.dto.ManagerPrinciple;
import com.zhenhappy.ems.manager.entity.EchartBarResponse;
import com.zhenhappy.ems.manager.entity.QRCodeScanInfo;
import com.zhenhappy.ems.manager.entity.ScanCodeReport;
import com.zhenhappy.ems.manager.service.CodeScanManagerService;
import com.zhenhappy.ems.manager.service.CustomerInfoManagerService;
import com.zhenhappy.util.EmailPattern;
import com.zhenhappy.util.report.EchartMapResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Iterator;
import java.util.List;

/**
 * Created by wangxd on 2016/4/5.
 */
@Controller
@RequestMapping(value = "user")
@SessionAttributes(value = ManagerPrinciple.MANAGERPRINCIPLE)
public class QRCodeScanAction extends BaseAction {
    private static Logger log = Logger.getLogger(QRCodeScanAction.class);

    @Autowired
    private CodeScanManagerService codeScanManagerService;

    @Autowired
    private CustomerInfoManagerService customerInfoManagerService;

    @ResponseBody
    @RequestMapping(value = "isValidPhone")
    public EchartMapResponse isValidPhone(@RequestParam(value = "phone", defaultValue = "") String phoneNum) {
        EchartMapResponse response = new EchartMapResponse();
        //http://oa.xicec.com/xicecwx/external/queryWxRegist.aspx
        //{"mobile": "13616027176"}

        JSONObject json = new JSONObject();
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost method = new HttpPost("http://oa.xicec.com/xicecwx/external/queryWxRegist.aspx");

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("mobile", phoneNum);// 红谷滩新闻资讯，channelId 77

        StringEntity entity = null;//解决中文乱码问题
        try {
            entity = new StringEntity(jsonParam.toString(),"utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            method.setEntity(entity);
            HttpResponse result = httpClient.execute(method);

            //请求结束
            String resData = EntityUtils.toString(result.getEntity());
            JSONObject jsonResult = JSONObject.fromObject(resData);
            for (Iterator iter = jsonResult.keys(); iter.hasNext();) {
                String key = (String)iter.next();
                if(key.equalsIgnoreCase("result") && jsonResult .getString(key).equalsIgnoreCase("1")){
                    QRCodeScanInfo qrCodeScanInfo = codeScanManagerService.loadPhoneByCheckNo(phoneNum);
                    if(qrCodeScanInfo != null) {
                        qrCodeScanInfo.setCount(qrCodeScanInfo.getCount()+1);
                        codeScanManagerService.modifyPhone(qrCodeScanInfo);
                        response.setDescription(String.valueOf(qrCodeScanInfo.getCount() + 1));
                    }else{
                        qrCodeScanInfo = new QRCodeScanInfo();
                        qrCodeScanInfo.setCount(1);
                        qrCodeScanInfo.setPhonenumber(phoneNum);
                        codeScanManagerService.addPhone(qrCodeScanInfo);
                        response.setDescription("1");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            response.setResultCode(1);
            response.setDescription("无效的预约登记号");
            e.printStackTrace();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "queryDataReportTable")
    public EchartBarResponse queryDataReportTable() {
        EchartBarResponse response = new EchartBarResponse();
        List<QRCodeScanInfo> qrCodeScanList = codeScanManagerService.loadAllPhoneByPhone();
        EmailPattern pattern = new EmailPattern();
        int phoneCount = 0;
        int netCount = 0;
        if(qrCodeScanList != null && qrCodeScanList.size()>0) {
            for(int i=0;i<qrCodeScanList.size();i++){
                QRCodeScanInfo qrCodeScanInfo = qrCodeScanList.get(i);
                if(qrCodeScanInfo != null){
                    if(pattern.isMobileNO(qrCodeScanInfo.getPhonenumber())){
                        phoneCount = phoneCount + qrCodeScanInfo.getCount();
                    } else{
                        netCount = netCount + qrCodeScanInfo.getCount();
                    }
                }
            }
            System.out.println("phoneCount: " + phoneCount + "   ,netCount: " + netCount);
            response.setPhoneType("微信登记");
            response.setPhoneValue(phoneCount);
            response.setNetType("官网登记");
            response.setNetValue(netCount);
            response.setResultCode(0);
        } else {
            response.setResultCode(1);
        }
        return response;
    }

    @RequestMapping(value = "dataReport")
    public ModelAndView directToDataDistribute() {
        ModelAndView modelAndView = new ModelAndView("user/main/datareport");
        return modelAndView;
    }

    /**
     * 测试扫描二维码
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "testPhoneAction")
    public EchartMapResponse testPhoneAction(@RequestParam(value = "phone", defaultValue = "") String phoneNum) {
        EchartMapResponse response = new EchartMapResponse();
        QRCodeScanInfo qrCodeScanInfo = codeScanManagerService.loadPhoneByCheckNo(phoneNum);
        if(qrCodeScanInfo != null) {
            qrCodeScanInfo.setCount(qrCodeScanInfo.getCount()+1);
            codeScanManagerService.modifyPhone(qrCodeScanInfo);
            response.setDescription(String.valueOf(qrCodeScanInfo.getCount() + 1));
        }else{
            qrCodeScanInfo = new QRCodeScanInfo();
            qrCodeScanInfo.setCount(1);
            qrCodeScanInfo.setPhonenumber(phoneNum);
            codeScanManagerService.addPhone(qrCodeScanInfo);
            response.setDescription("1");
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "queryVisitorInfoByCheckNo")
    public EchartMapResponse queryVisitorInfoByCheckNo(@RequestParam(value = "checkno", defaultValue = "") String checkno) {
        EchartMapResponse response = new EchartMapResponse();
        boolean flag = codeScanManagerService.isExiistVisitor(checkno);
        if(flag) {
            QRCodeScanInfo qrCodeScanInfo = codeScanManagerService.loadPhoneByCheckNo(checkno);
            if(qrCodeScanInfo != null) {
                qrCodeScanInfo.setCount(qrCodeScanInfo.getCount()+1);
                codeScanManagerService.modifyPhone(qrCodeScanInfo);
                response.setDescription(String.valueOf(qrCodeScanInfo.getCount()));
            }else{
                qrCodeScanInfo = new QRCodeScanInfo();
                qrCodeScanInfo.setCount(1);
                qrCodeScanInfo.setPhonenumber(checkno);
                codeScanManagerService.addPhone(qrCodeScanInfo);
                response.setDescription("1");
            }
        } else {
            response.setResultCode(1);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "queryCheckingNoByTelphoneValue")
    public EchartMapResponse queryCheckingNoByTelphoneValue(@RequestParam(value = "phone", defaultValue = "") String phone) {
        EchartMapResponse response = new EchartMapResponse();
        TVisitorInfo visitorInfo = codeScanManagerService.loadVisitorByPhone(phone);
        if(visitorInfo != null){
            response.setData(visitorInfo.getCheckingNo());
        }else{
            response.setResultCode(1);
        }
        return response;
    }
}
