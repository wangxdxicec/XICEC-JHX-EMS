package com.zhenhappy.ems.manager.action.user;

import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.dto.*;
import com.zhenhappy.ems.manager.service.CustomerInfoManagerService;
import com.zhenhappy.ems.manager.service.CustomerTemplateService;
import com.zhenhappy.util.report.EchartMapResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by wangxd on 2016/4/5.
 */
@Controller
@RequestMapping(value = "user")
@SessionAttributes(value = ManagerPrinciple.MANAGERPRINCIPLE)
public class DataReportAction extends BaseAction {

    private static Logger log = Logger.getLogger(DataReportAction.class);

    @Autowired
    private CustomerTemplateService customerTemplaeService;

    @Autowired
    private CustomerInfoManagerService customerInfoManagerService;

    @Autowired
    JavaMailSender mailSender;// 注入Spring封装的javamail，Spring的xml中已让框架装配

    /**
     * 分页查询国内客商用于报表
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryExhibitorForReport")
    public EchartMapResponse queryExhibitorForReport(@RequestParam(value = "cids", defaultValue = "") Integer[] cids,
                                                           @ModelAttribute QueryCustomerRequest request) {
        EchartMapResponse response = new EchartMapResponse();
        try {
            response = customerInfoManagerService.queryExhibitorForReport(request, cids[0]);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query inland customers report error.", e);
        }
        return response;
    }
}
