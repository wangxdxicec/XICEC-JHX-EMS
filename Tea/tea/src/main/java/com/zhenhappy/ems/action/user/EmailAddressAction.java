package com.zhenhappy.ems.action.user;

import com.zhenhappy.ems.action.BaseAction;
import com.zhenhappy.ems.dto.BaseResponse;
import com.zhenhappy.ems.dto.Principle;
import com.zhenhappy.ems.entity.TExhibitorInfo;
import com.zhenhappy.ems.entity.TInvoiceApply;
import com.zhenhappy.ems.service.ExhibitorService;
import com.zhenhappy.ems.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * Created by wangxd on 2016-08-30.
 */
@Controller
@RequestMapping(value = "/user/email")
@SessionAttributes(value = Principle.PRINCIPLE_SESSION_ATTRIBUTE)
public class EmailAddressAction extends BaseAction {
    @Autowired
    private ExhibitorService exhibitorService;

    @RequestMapping(value = "update")
    public ModelAndView index(@ModelAttribute(Principle.PRINCIPLE_SESSION_ATTRIBUTE) Principle principle) {
        TExhibitorInfo exhibitorInfo = exhibitorService.loadExhibitorInfoByEid(principle.getExhibitor().getEid());
        if (exhibitorInfo == null) {
            return new ModelAndView("/user/email/update");
        } else {
            ModelAndView modelAndView = new ModelAndView("/user/email/update");
            modelAndView.addObject("exhibitorInfo", exhibitorInfo);
            return modelAndView;
        }
    }

    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public BaseResponse save(@RequestParam("email_address") String email_address,
                             @RequestParam("email_contact") String email_contact,
                             @RequestParam("email_telphone") String email_telphone,
                             @RequestParam(value = "eid",required = false) Integer eid,
                             @ModelAttribute(Principle.PRINCIPLE_SESSION_ATTRIBUTE) Principle principle) {
        BaseResponse response = new BaseResponse();
        try {
            TExhibitorInfo exhibitorInfo = exhibitorService.loadExhibitorInfoByEid(principle.getExhibitor().getEid());
            exhibitorInfo.setEmail_address(email_address);
            exhibitorInfo.setEmail_contact(email_contact);
            exhibitorInfo.setEmail_telphone(email_telphone);
            exhibitorService.update(exhibitorInfo);
            response.setResultCode(0);
        } catch (Exception e) {
            response.setResultCode(1);
        }
        return response;
    }
}
