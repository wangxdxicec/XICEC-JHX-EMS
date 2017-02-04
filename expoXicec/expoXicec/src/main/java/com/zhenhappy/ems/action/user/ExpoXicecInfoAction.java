package com.zhenhappy.ems.action.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhenhappy.ems.action.BaseAction;
import com.zhenhappy.ems.dto.ExpoXicecPrinciple;
import com.zhenhappy.ems.entity.TExpoExhibitorInfo;
import com.zhenhappy.ems.entity.TExpoXicec;
import com.zhenhappy.ems.service.expoxicec.ExpoXicecManagerService;
import com.zhenhappy.ems.util.JdbcUtils_C3P0;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by wangxd on 2017-01-17.
 */
@Controller
@RequestMapping(value = "user")
@SessionAttributes(value = ExpoXicecPrinciple.PRINCIPLE_SESSION_ATTRIBUTE)
public class ExpoXicecInfoAction extends BaseAction {
    private static Logger log = Logger.getLogger(ExpoXicecInfoAction.class);

    @Autowired
    private ExpoXicecManagerService expoXicecManagerService;

    /**
     * redirect to booth empty page.
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "boothempty", method = RequestMethod.GET)
    public ModelAndView redirectBoothEmpty(HttpServletRequest request, Locale locale) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/user/expoxicec/boothempty");
        return modelAndView;
    }

    /**
     * redirect to information manager.service page.
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "addbooth", method = RequestMethod.GET)
    public ModelAndView redirectAddBooth(HttpServletRequest request, Locale locale) {
        ModelAndView modelAndView = new ModelAndView();
        TExpoXicec tExpoXicec = (TExpoXicec) request.getSession().getAttribute(ExpoXicecPrinciple.PRINCIPLE_SESSION_ATTRIBUTE);
        if(tExpoXicec != null){
            modelAndView.addObject("contactname", tExpoXicec.getUsername());
            modelAndView.addObject("mobile", tExpoXicec.getMobilephone());
        }
        modelAndView.setViewName("/user/expoxicec/addbooth");
        return modelAndView;
    }

    /**
     * redirect to information manager.service page.
     *
     * @param booth
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryCompanyInfoByBoothNumber", method = RequestMethod.POST)
    public JSONObject queryCompanyInfoByBoothNumber(@RequestParam("booth") String booth,
                                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                @RequestParam(value = "pageSize", required = false, defaultValue = "30") Integer pageSize) {
        List<TExpoExhibitorInfo> tExpoExhibitorInfoList = expoXicecManagerService.queryBoothByBoothNum(booth);
        JSONObject root = new JSONObject();
        JSONArray children = new JSONArray();
        if(tExpoExhibitorInfoList != null){
            Integer count = tExpoExhibitorInfoList.size();
            root.put("total", count);
            JSONObject child;
            for (TExpoExhibitorInfo tExpoExhibitorInfo : tExpoExhibitorInfoList) {
                child = new JSONObject();
                child.put("eid", tExpoExhibitorInfo.getEid());
                child.put("booth", tExpoExhibitorInfo.getBoothnumber());
                child.put("company", tExpoExhibitorInfo.getCompany());
                child.put("area", tExpoExhibitorInfo.getAreanumber());
                child.put("id", tExpoExhibitorInfo.getId());
                children.add(child);
            }
            root.put("items", children);
            return root;
        }else{
            root.put("items", children);
            return root;
        }
    }

    /**
     * create booth
     *
     * @param booth, company, contact, mobile, type
     * @return
     */
    @RequestMapping(value = "createbooth", method = RequestMethod.POST)
    public ModelAndView addExhibitorAccount(@RequestParam("booth") String booth,
                                            @RequestParam("company") String company,
                                            @RequestParam("contact") String contact,
                                            @RequestParam("mobile") String mobile,
                                            @RequestParam("type") String type,
                                            HttpServletRequest request,
                                            Locale locale) throws IOException {
        ModelAndView modelAndView = new ModelAndView();
        //在这里进行保存操作
        return modelAndView;
    }

    /**
     * type：表示展会类型
     *
     * @param type=1：石材展   type=2：佛事展    type=3：茶业展
     * @return
     */
    public static void getExhibitorInfoByExhibitorType(int type){
        JdbcUtils_C3P0 jdbcUtils_c3P0 = new JdbcUtils_C3P0();
        String url = "";
        if(1 == type){
            url = "jdbc:jtds:sqlserver://10.33.0.224:1433;DatabaseName=xmut";
        }else if(2 == type){
            url = "jdbc:jtds:sqlserver://10.33.0.224:1433;DatabaseName=foshi";
        }else if(3 == type){
            url = "jdbc:jtds:sqlserver://10.33.0.224:1433;DatabaseName=tea";
        }
        jdbcUtils_c3P0.executeDataBaseByType("net.sourceforge.jtds.jdbc.Driver", url, "Jhx03SA", "gogo03Jhx", "t_exhibitor", type);
    }

    public static void main(String[] args) {
        //getExhibitorInfoByExhibitorType(1);
        //getExhibitorInfoByExhibitorType(2);
        //getExhibitorInfoByExhibitorType(3);
    }
}
