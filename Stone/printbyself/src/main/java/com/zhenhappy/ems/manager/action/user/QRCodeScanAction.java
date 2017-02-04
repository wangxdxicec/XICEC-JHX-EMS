package com.zhenhappy.ems.manager.action.user;

import com.zhenhappy.ems.entity.WCustomer;
import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.dto.ManagerPrinciple;
import com.zhenhappy.ems.manager.service.CustomerInfoManagerService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by wangxd on 2016/4/5.
 */
@Controller
@RequestMapping(value = "user")
@SessionAttributes(ManagerPrinciple.MANAGERPRINCIPLE)
public class QRCodeScanAction extends BaseAction {
    private static Logger log = Logger.getLogger(QRCodeScanAction.class);

    @Autowired
    private CustomerInfoManagerService customerInfoManagerService;

    /**
     * 根据预约登记号获取客商信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryVisitorInfoByCheckNo")
    public WCustomer queryVisitorInfoByCheckNo(@RequestParam("checkNo") String checkValue) {
        WCustomer customer = new WCustomer();
        try{
            customer = customerInfoManagerService.loadCustomerSurveyInfoById(checkValue);
        }catch (Exception e){
            System.out.println("====查询信息错误====" + e);
        }
        return customer;
    }
}
