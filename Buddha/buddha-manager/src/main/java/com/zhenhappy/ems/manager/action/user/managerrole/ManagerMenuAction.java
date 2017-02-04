package com.zhenhappy.ems.manager.action.user.managerrole;

import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.dto.ManagerPrinciple;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Created by wangxd on 2016-05-18.
 */
@Controller
@RequestMapping(value = "user")
@SessionAttributes(value = ManagerPrinciple.MANAGERPRINCIPLE)
public class ManagerMenuAction extends BaseAction {
    //境内客商历史资料
    @RequestMapping(value = "inlandHistoryVisitorInfo")
    public String inlandHistoryVisitorInfo(){
        return "user/managerreset/inlandHistoryVisitorInfo";
    }

    //境外客商历史资料
    @RequestMapping(value = "outlandHistoryVisitorInfo")
    public String outlandHistoryVisitorInfo(){
        return "user/managerreset/outlandHistoryVisitorInfo";
    }

    //展会相关时间参数设置
    @RequestMapping(value = "resetExhibitorBuddhaTime")
    public String resetExhibitorBuddhaTime(){
        return "user/managerreset/exhibitor_time";
    }

    //预登记展商
    @RequestMapping(value = "preExhibitorIndex")
    public String preExhibitorIndex(){
        return "/user/preregistratemanager/preexhibitor";
    }

    //预登记境内客商
    @RequestMapping(value = "preInlandCustomerIndex")
    public String preInlandCustomerIndex(){
        return "/user/preregistratemanager/preinlandCustomer";
    }

    //预登记境外客商
    @RequestMapping(value = "preForeignCustomerIndex")
    public String preForeignCustomerIndex(){
        return "/user/preregistratemanager/preforeignCustomer";
    }

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

    //素食展菜单
    @RequestMapping(value = "exhibitorOfVegetarianIndex")
    public String exhibitorOfVegetarianIndex(){
        return "/user/exhibitor/vegetarianExhibitor";
    }

    //泰国展菜单
    @RequestMapping(value = "exhibitorOfThailandIndex")
    public String exhibitorOfThailandIndex(){
        return "/user/exhibitor/thailandExhibitor";
    }

    //国内客商菜单
    @RequestMapping(value = "inlandCustomerIndex")
    public String inlandCustomerIndex(){
        return "/user/customer/inlandCustomer";
    }

    //境内法师菜单
    @RequestMapping(value = "inlandRabbiCustomerIndex")
    public String inlandRabbiCustomerIndex(){
        return "/user/customer/inlandRabbiCustomer";
    }

    //境外法师菜单
    @RequestMapping(value = "foreignRabbiCustomerIndex")
    public String foreignRabbiCustomerIndex(){
        return "/user/customer/foreignRabbiCustomer";
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

    //菜单管理
    @RequestMapping("menuIndex")
    public String menuIndex(){
        return "user/managerrole/menu";
    }

    //角色管理
    @RequestMapping("roleIndex")
    public String roleIndex(){
        return "user/managerrole/role";
    }

    //用户管理
    @RequestMapping(value = "userIndex")
    public String userIndex(){
        return "user/managerrole/userinfo";
    }
}
