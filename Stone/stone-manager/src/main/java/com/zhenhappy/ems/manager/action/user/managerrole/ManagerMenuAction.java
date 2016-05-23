package com.zhenhappy.ems.manager.action.user.managerrole;

import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.dto.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by wangxd on 2016-05-18.
 */
@Controller
@RequestMapping(value = "user")
@SessionAttributes(value = ManagerPrinciple.MANAGERPRINCIPLE)
public class ManagerMenuAction extends BaseAction {
    //展商列表菜单
    @RequestMapping(value = "exhibitorIndex")
    public String exhibitorIndex(){
        return "/user/exhibitor/exhibitor";
    }

    //展团列表菜单
    @RequestMapping(value = "exhibitorGroupIndex")
    public String exhibitorGroupIndex(){
        return "/user/group/group";
    }

    //国内客商菜单
    @RequestMapping(value = "inlandCustomerIndex")
    public String inlandCustomerIndex(){
        return "/user/customer/inlandCustomer";
    }

    //国外客商菜单
    @RequestMapping(value = "foreignCustomerIndex")
    public String foreignCustomerIndex(){
        return "/user/customer/foreignCustomer";
    }

    //邮件申请列表菜单
    @RequestMapping(value = "emailApplyPageIndex")
    public String emailApplyPageIndex(){
        return "/user/customer/emailApplyPage";
    }

    //短信申请列表菜单
    @RequestMapping(value = "msgApplyPageIndex")
    public String msgApplyPageIndex(){
        return "/user/customer/msgApplyPage";
    }

    //展商VISA菜单
    @RequestMapping(value = "exhibitorVisaIndex")
    public String exhibitorVisaIndex(){
        return "/user/visa/tvisa";
    }

    //客商VISA菜单
    @RequestMapping(value = "wcustomerVisaIndex")
    public String wcustomerVisaIndex(){
        return "/user/visa/wvisa";
    }

    //标签列表菜单
    @RequestMapping(value = "tagIndex")
    public String tagIndex(){
        return "/user/tag/tag";
    }

    //公告管理菜单
    @RequestMapping(value = "articleIndex")
    public String articleIndex(){
        return "/user/article/article";
    }

    //邮件模板管理菜单
    @RequestMapping(value = "emailTemplateIndex")
    public String emailTemplateIndex(){
        return "/user/email/emailTemplate";
    }

    //短信模板管理菜单
    @RequestMapping(value = "msgTemplateIndex")
    public String msgTemplateIndex(){
        return "/user/email/messageTemplate";
    }

    //数据报表菜单
    @RequestMapping(value = "dataReportIndex")
    public String dataReportIndex(){
        return "/user/report/dataReport";
    }

    //数据分布菜单
    @RequestMapping(value = "dataDistributeIndex")
    public String dataDistributeIndex(){
        return "/user/report/dataDistribute";
    }

    //菜单管理
    @RequestMapping("menuIndex")
    public String menuIndex(){
        return "user/managerrole/menu";
    }

    //角色管理
    @RequestMapping("roleIndex")
    public String roleIndex(){
        return "user/managerrole//role";
    }

    //用户管理
    @RequestMapping(value = "userIndex")
    public String userIndex(){
        return "user/managerrole/userinfo";
    }
}
