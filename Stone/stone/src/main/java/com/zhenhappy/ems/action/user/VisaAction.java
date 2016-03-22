package com.zhenhappy.ems.action.user;

import com.zhenhappy.ems.action.BaseAction;
import com.zhenhappy.ems.dto.*;
import com.zhenhappy.ems.entity.TProductImage;
import com.zhenhappy.ems.entity.TVisa;
import com.zhenhappy.ems.service.VisaService;
import com.zhenhappy.ems.sys.Constants;
import com.zhenhappy.system.SystemConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lianghaijian on 2014-05-10.
 */
@Controller
@RequestMapping(value = "/user")
@SessionAttributes(value = Principle.PRINCIPLE_SESSION_ATTRIBUTE)
public class VisaAction extends BaseAction {

    @Autowired
    private VisaService visaService;

    @Autowired
    private SystemConfig systemConfig;

    @RequestMapping(value = "/visa/list")
    public String visaList() {
        return "/user/visa/visalist";
    }

    private static Logger log = Logger.getLogger(VisaAction.class);

    @RequestMapping(value = "/visa/index")
    public String index() {
        return "/user/visa/index";
    }

	@RequestMapping(value = "/addFromJoiner")
	public String addFromJoiner() {
		return "/user/visa/addFromJoiner";
	}

    @RequestMapping(value = "/visa/queryVisas")
    @ResponseBody
    public QueryVisaResponse queryVisa(@ModelAttribute(Principle.PRINCIPLE_SESSION_ATTRIBUTE) Principle principle) {
        QueryVisaResponse response = null;
        try {
            List<TVisa> visas = visaService.queryVisasByEid(principle.getExhibitor().getEid());
            for(TVisa visa:visas){
                eachProperties(visa);
            }
            response = new QueryVisaResponse();
            response.setRows(visas);
            response.setResultCode(0);
        } catch (Exception e) {
            log.error("query visas error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    private void eachProperties(Object model) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Field[] field = model.getClass().getDeclaredFields(); //获取实体类的所有属性，返回Field数组
        for(int j=0 ; j<field.length ; j++){ //遍历所有属性
            String name = field[j].getName(); //获取属性的名字

            System.out.println("attribute name:"+name);
            name = name.substring(0,1).toUpperCase()+name.substring(1); //将属性的首字符大写，方便构造get，set方法
            String type = field[j].getGenericType().toString(); //获取属性的类型
            if(type.equals("class java.lang.String")){ //如果type是类类型，则前面包含"class "，后面跟类名
                Method get = model.getClass().getMethod("get"+name);
                String value = (String) get.invoke(model);
                Method set = model.getClass().getMethod("set"+name, new Class[] {String.class});
                set.invoke(model,new Object[] { replaceBlank(value) });
            }
        }
    }

    private String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    @RequestMapping(value = "/visa/saveVisa", method = RequestMethod.POST)
    public ModelAndView saveVisa(@ModelAttribute(Principle.PRINCIPLE_SESSION_ATTRIBUTE) Principle principle, @ModelAttribute SaveVisaInfoRequest visa, @RequestParam(value = "license", required = false) MultipartFile license, @RequestParam(value = "passportPageFile", required = false) MultipartFile passportPage) {
        ModelAndView modelAndView = new ModelAndView("/user/callback");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if(StringUtils.isNotEmpty(visa.getBirthDay())){
                visa.setBirth(sdf.parse(visa.getBirthDay()));
            }
            if(StringUtils.isNotEmpty(visa.getFromDate())) {
                visa.setFrom(sdf.parse(visa.getFromDate()));
            }
            if(StringUtils.isNotEmpty(visa.getToDate())) {
                visa.setTo(sdf.parse(visa.getToDate()));
            }
            if(StringUtils.isNotEmpty(visa.getExpireDate())) {
                visa.setExpDate(sdf.parse(visa.getExpireDate()));
            }
//            if (license != null) {
//                String fileName = systemConfig.getVal(Constants.appendix_directory)+"/" + new Date().getTime() + "." + FilenameUtils.getExtension(license.getOriginalFilename());
//                if (license != null && license.getSize() != 0) {
//                    FileUtils.copyInputStreamToFile(license.getInputStream(), new File(fileName));
//                    visa.setBusinessLicense(fileName);
//                }
//            }
            if (passportPage != null) {
                String fileName = systemConfig.getVal(Constants.appendix_directory)+"/" + new Date().getTime() + "." + FilenameUtils.getExtension(passportPage.getOriginalFilename());
                if (passportPage != null && passportPage.getSize() != 0) {
                    FileUtils.copyInputStreamToFile(passportPage.getInputStream(), new File(fileName));
                    visa.setPassportPage(fileName);
                }
            }
            visa.setEid(principle.getExhibitor().getEid());
			visa.setStatus(1);
            TVisa temp = new TVisa();
            BeanUtils.copyProperties(visa,temp);
            visaService.saveOrUpdate(temp);
            modelAndView.addObject("method", "addSuccess");
        } catch (Exception e) {
            log.error("add visa error",e);
            modelAndView.addObject("method", "addFailure");
        }
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value = "/visa/delete",method = RequestMethod.POST)
    public BaseResponse deleteVisa(@RequestParam(value = "vid") Integer vid,@ModelAttribute(Principle.PRINCIPLE_SESSION_ATTRIBUTE) Principle principle){
        BaseResponse response = new BaseResponse();
        try{
            visaService.delete(principle.getExhibitor().getEid(),vid);
            response.setResultCode(0);
        }catch (Exception e){
            log.error("delete visa error.",e);
            response.setResultCode(1);
        }
        return response;
    }

    @RequestMapping(value = "/visa/visaZhengjian",method = RequestMethod.GET)
    public void visaPage(@RequestParam("vid") Integer vid,@RequestParam("type") Integer type,HttpServletResponse response){
        try {
            TVisa image = visaService.queryByVid(vid);
            if (image != null) {
                OutputStream outputStream = response.getOutputStream();
                File img = null;
                if(type.intValue()==1){
                    img = new File(image.getPassportPage());
                }else{
                    img = new File(image.getBusinessLicense());
                }
                if(!img.exists()){
                    return;
                }
                FileUtils.copyFile(img, outputStream);
                outputStream.close();
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
