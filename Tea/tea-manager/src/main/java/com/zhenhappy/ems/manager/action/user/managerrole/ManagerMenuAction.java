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
    //展会结束相关数据归档设置
    @RequestMapping(value = "resetAllExhibitorToDefaultIndex")
    public String resetAllExhibitorToDefaultIndex(){ return "user/managerreset/resetview"; }

    //展会相关时间参数设置
    @RequestMapping(value = "resetExhibitorTeaFairTimeIndex")
    public String resetExhibitorTeaFairTimeIndex(){
        return "user/managerreset/exhibitor_time";
    }

    //本届展商列表菜单
    @RequestMapping(value = "currentExhibitorIndex")
    public String currentExhibitorIndex(){
        return "user/exhibitor/current_exhibitor";
    }

    //展商列表菜单
    @RequestMapping(value = "exhibitorIndex")
    public String exhibitorIndex(){
        return "user/exhibitor/exhibitor";
    }

    //展团列表菜单
    @RequestMapping(value = "exhibitorGroupIndex")
    public String exhibitorGroupIndex(){
        return "user/group/group";
    }

    //标签列表菜单
    @RequestMapping(value = "tagIndex")
    public String tagIndex(){
        return "user/tag/tag";
    }

    //公告管理菜单
    @RequestMapping(value = "articleIndex")
    public String articleIndex(){
        return "user/article/article";
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

    //境内客商菜单
    @RequestMapping(value = "inlandCustomerIndex")
    public String inlandCustomerIndex(){
        return "/user/customer/inlandCustomer";
    }

    //境外客商菜单
    @RequestMapping(value = "foreignCustomerIndex")
    public String foreignCustomerIndex(){
        return "/user/customer/foreignCustomer";
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
}
