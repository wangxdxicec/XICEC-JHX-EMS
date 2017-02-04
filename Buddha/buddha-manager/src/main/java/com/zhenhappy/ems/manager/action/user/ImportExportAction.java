package com.zhenhappy.ems.manager.action.user;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zhenhappy.ems.dao.ExhibitorInfoDao;
import com.zhenhappy.ems.dao.VisitorInfoDao;
import com.zhenhappy.ems.dto.BaseResponse;
import com.zhenhappy.ems.entity.*;
import com.zhenhappy.ems.entity.managerrole.TUserInfo;
import com.zhenhappy.ems.manager.dto.*;
import com.zhenhappy.ems.manager.entity.TExhibitorBooth;
import com.zhenhappy.ems.manager.entity.THistoryVisitorInfo;
import com.zhenhappy.ems.manager.service.CustomerInfoManagerService;
import com.zhenhappy.ems.service.ExhibitorService;
import com.zhenhappy.ems.service.JoinerService;
import com.zhenhappy.ems.service.managerrole.TUserInfoService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.service.ExhibitorManagerService;
import com.zhenhappy.ems.manager.service.ImportExportService;
import com.zhenhappy.ems.manager.sys.Constants;
import com.zhenhappy.ems.manager.util.CreateZip;
import com.zhenhappy.ems.manager.util.JXLExcelView;
import com.zhenhappy.system.SystemConfig;

import freemarker.template.Template;

/**
 * Created by wujianbin on 2014-08-26.
 */
@Controller
@RequestMapping(value = "user")
@SessionAttributes(value = ManagerPrinciple.MANAGERPRINCIPLE)
public class ImportExportAction extends BaseAction {

	private static Logger log = Logger.getLogger(ImportExportAction.class);

    @Autowired
    private ExhibitorManagerService exhibitorManagerService;
    @Autowired
    private ImportExportService importExportService;
    @Autowired
	private FreeMarkerConfigurer freeMarker;// 注入FreeMarker模版封装框架
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private CustomerInfoManagerService customerInfoManagerService;
    @Autowired
    private JoinerService joinerService;
    @Autowired
    private ExhibitorInfoDao exhibitorInfoDao;
    @Autowired
    private TUserInfoService userInfoService;

    /**
     * 导出展商列表到Excel
     * @param eids
     * @return
     */
    @RequestMapping(value = "exportExhibitorsToExcel", method = RequestMethod.POST)
    public ModelAndView exportExhibitorsToExcel(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
                                                @RequestParam(value = "tag", defaultValue = "-1") Integer tag,
                                                @RequestParam(value = "type") Integer type,
                                                @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        Map model = new HashMap();
        List<TExhibitor> exhibitors = new ArrayList<TExhibitor>();
        TUserInfo userInfo = (TUserInfo) principle.getAdmin();
        TUserInfo userInfo1 = userInfoService.findOneUserInfo(userInfo.getId());
        if(eids[0] == -1)
            exhibitors = exhibitorManagerService.loadAllExhibitorsByTagAndRole(tag, type, userInfo1);
        else
            exhibitors = exhibitorManagerService.loadSelectedExhibitors(eids);
        List<QueryExhibitorInfo> queryExhibitorInfos = importExportService.exportExhibitor(exhibitors);
        model.put("list", queryExhibitorInfos);
        String[] titles = new String[] { "展位号", "用户名", "密码", "公司中文名", "公司英文名", "电话", "传真", "邮箱", "网址", "中文地址", "英文地址", "邮编", "产品分类", "主营产品(中文)", "主营产品(英文)", "公司简介", "发票抬头", "地税税号" };
        model.put("titles", titles);
        String[] columns = new String[] { "boothNumber", "username", "password", "company", "companyEn", "phone", "fax", "email", "website", "address", "addressEn", "zipcode", "productType", "mainProduct", "mainProductEn", "mark", "invoiceTitle", "invoiceNo" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20};
        model.put("columnWidths", columnWidths);
        model.put("fileName", "展商基本信息.xls");
        model.put("sheetName", "展商基本信息");
        return new ModelAndView(new JXLExcelView(), model);
    }

    /**
     * 根据eid查询展商基本信息
     * @param eid
     * @return
     */
    @Transactional
    public TExhibitorInfo loadExhibitorInfoByEid(Integer eid) {
        if(eid != null){
            List<TExhibitorInfo> exhibitorInfo = exhibitorInfoDao.queryByHql("from TExhibitorInfo where eid=?", new Object[]{ eid });
            return exhibitorInfo.size() > 0 ? exhibitorInfo.get(0) : null;
        }else return null;
    }

    /**
     * 导出展位信息到Excel
     * @return
     */
    @RequestMapping("/exportBoothInfoToExcel_2")
    public ModelAndView exportBoothInfoToExcel_2() {
        Map model = new HashMap();
        List<TExhibitor> exhibitors = exhibitorManagerService.loadAllExhibitors();
        List<QueryExhibitorInfoT> queryExhibitorInfos = new ArrayList<QueryExhibitorInfoT>();
        if(exhibitors != null){
            for(TExhibitor exhibitor:exhibitors){
                TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(exhibitor.getEid());
                String boothNum = exhibitorManagerService.loadBoothNum(exhibitor.getEid());
                String boothNums[] = boothNum.split(",");
                if(boothNums.length > 1) {
                    for(String booth:boothNums){
                        QueryExhibitorInfoT queryExhibitorInfoT = new QueryExhibitorInfoT();
                        queryExhibitorInfoT.setEid(exhibitor.getEid());
                        queryExhibitorInfoT.setBoothNumber(booth.trim());
                        /*if(StringUtils.isNotEmpty(exhibitor.getCompany()))
                            queryExhibitorInfoT.setCompany(exhibitor.getCompany());
                        else
                            queryExhibitorInfoT.setCompany(exhibitor.getCompanye());*/
                        if(StringUtils.isNotEmpty(exhibitorInfo.getCompany()))
                            queryExhibitorInfoT.setCompany(exhibitorInfo.getCompany());
                        else
                            queryExhibitorInfoT.setCompany(exhibitorInfo.getCompanyEn());
                        queryExhibitorInfos.add(queryExhibitorInfoT);
                    }
                }else if(boothNums.length == 1) {
                    QueryExhibitorInfoT queryExhibitorInfoT = new QueryExhibitorInfoT();
                    queryExhibitorInfoT.setEid(exhibitor.getEid());
                    queryExhibitorInfoT.setBoothNumber(boothNum.trim());
                    /*if(StringUtils.isNotEmpty(exhibitor.getCompany()))
                        queryExhibitorInfoT.setCompany(exhibitor.getCompany());
                    else
                        queryExhibitorInfoT.setCompany(exhibitor.getCompanye());*/
                    if(StringUtils.isNotEmpty(exhibitorInfo.getCompany()))
                        queryExhibitorInfoT.setCompany(exhibitorInfo.getCompany());
                    else
                        queryExhibitorInfoT.setCompany(exhibitorInfo.getCompanyEn());
                    queryExhibitorInfos.add(queryExhibitorInfoT);
                }
            }
        }
        model.put("list", queryExhibitorInfos);
        String[] titles = new String[] { "ID", "展位号", "公司名" };
        model.put("titles", titles);
        String[] columns = new String[] { "eid", "boothNumber", "company" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{20,20,20};
        model.put("columnWidths", columnWidths);
        model.put("fileName", "展位信息.xls");
        model.put("sheetName", "展位信息");
        return new ModelAndView(new JXLExcelView(), model);
    }

    /**
     * 导出展位信息到Excel
     * @return
     */
    @RequestMapping("/exportBoothInfoToExcel_1")
    public ModelAndView exportBoothInfoToExcel_1() {
        Map model = new HashMap();
        // 构造数据
        List<TExhibitor> exhibitors = exhibitorManagerService.loadAllExhibitors();
        List<QueryExhibitorInfoT> queryExhibitorInfos = new ArrayList<QueryExhibitorInfoT>();
        if(exhibitors != null){
            for(TExhibitor exhibitor:exhibitors){
                TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(exhibitor.getEid());
                String boothNum = exhibitorManagerService.loadBoothNum(exhibitor.getEid());
                String boothNums[] = boothNum.split(",");
                if(boothNums.length > 1) {
                    for(String booth:boothNums){
                        QueryExhibitorInfoT queryExhibitorInfoT = new QueryExhibitorInfoT();
                        queryExhibitorInfoT.setEid(exhibitor.getEid());
                        queryExhibitorInfoT.setBoothNumber(booth.trim());
                        /*if(StringUtils.isNotEmpty(exhibitor.getCompany()))
                            queryExhibitorInfoT.setCompany(exhibitor.getCompany());
                        else
                            queryExhibitorInfoT.setCompany(exhibitor.getCompanye());*/
                        if(StringUtils.isNotEmpty(exhibitorInfo.getCompany()))
                            queryExhibitorInfoT.setCompany(exhibitorInfo.getCompany());
                        else
                            queryExhibitorInfoT.setCompany(exhibitorInfo.getCompanyEn());
                        queryExhibitorInfos.add(queryExhibitorInfoT);
                    }
                }else if(boothNums.length == 1) {
                    QueryExhibitorInfoT queryExhibitorInfoT = new QueryExhibitorInfoT();
                    queryExhibitorInfoT.setEid(exhibitor.getEid());
                    queryExhibitorInfoT.setBoothNumber(boothNum.trim());
                    /*if(StringUtils.isNotEmpty(exhibitor.getCompany())) queryExhibitorInfoT.setCompany(exhibitor.getCompany());
                    if(StringUtils.isNotEmpty(exhibitor.getCompanye())) queryExhibitorInfoT.setCompanye(exhibitor.getCompanye());
                    if(StringUtils.isNotEmpty(exhibitor.getCompanyt())) queryExhibitorInfoT.setCompanyt(exhibitor.getCompanyt());*/
                    if(StringUtils.isNotEmpty(exhibitorInfo.getCompany()))
                        queryExhibitorInfoT.setCompany(exhibitorInfo.getCompany());
                    if(StringUtils.isNotEmpty(exhibitorInfo.getCompanyEn()))
                        queryExhibitorInfoT.setCompanye(exhibitorInfo.getCompanyEn());
                    if(StringUtils.isNotEmpty(exhibitorInfo.getCompanyT()))
                        queryExhibitorInfoT.setCompanyt(exhibitorInfo.getCompanyT());
                    queryExhibitorInfos.add(queryExhibitorInfoT);
                }
            }
        }
        model.put("list", queryExhibitorInfos);
        String[] titles = new String[] { "ID", "展位号", "公司中文名", "公司繁体名", "公司英文名" };
        model.put("titles", titles);
        String[] columns = new String[] { "eid", "boothNumber", "company", "companyt", "companye" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{20,20,20,20,20};
        model.put("columnWidths", columnWidths);
        model.put("fileName", "展位信息.xls");
        model.put("sheetName", "展位信息");
        return new ModelAndView(new JXLExcelView(), model);
    }

    /**
     * 导出展位号+企业楣牌到Excel
     * @param eids
     * @return
     */
    @RequestMapping(value = "exportBoothNumAndMeipaiToExcel", method = RequestMethod.POST)
	public ModelAndView exportBoothNumAndMeipaiToExcel(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
                                                       @RequestParam(value = "tag", defaultValue = "-1") Integer tag,
                                                       @RequestParam(value = "type") Integer type,
                                                       @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
    	Map model = new HashMap();
        List<QueryBoothNumAndMeipai> boothNumAndMeipais = new ArrayList<QueryBoothNumAndMeipai>();
        if(eids[0] == -1)
            boothNumAndMeipais = exhibitorManagerService.loadBoothNumAndMeipai(null, tag, type, principle);
        else
            boothNumAndMeipais = exhibitorManagerService.loadBoothNumAndMeipai(eids, tag, type, principle);
        model.put("list", boothNumAndMeipais);
        String[] titles = new String[] { "展位号", "企业楣牌(中文)", "企业楣牌(英文)" };
		model.put("titles", titles);
		String[] columns = new String[] { "boothNumber", "meipai", "meipaiEn" };
		model.put("columns", columns);
		Integer[] columnWidths = new Integer[]{20,20,20};
		model.put("columnWidths", columnWidths);
		model.put("fileName", "展位号+企业楣牌.xls");
		model.put("sheetName", "展位号+企业楣牌");
		return new ModelAndView(new JXLExcelView(), model);
	}

    /**
     * 导出参展人员到Excel
     * @param eids
     * @return
     */
    @RequestMapping(value = "exportExhibitorJoinersToExcel", method = RequestMethod.POST)
    public ModelAndView exportExhibitorJoinersToExcel(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
                                                      @RequestParam(value = "tag", defaultValue = "-1") Integer tag,
                                                      @RequestParam(value = "type") Integer type,
                                                      @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        Map model = new HashMap();
        log.info("====eid.length: " + eids.length);
        List<TExhibitorJoinerEx> exhibitorJoiners = new ArrayList<TExhibitorJoinerEx>();
        if(eids[0] == -1){
            TUserInfo userInfo = (TUserInfo) principle.getAdmin();
            TUserInfo userInfo1 = userInfoService.findOneUserInfo(userInfo.getId());
            exhibitorJoiners = joinerService.queryAllJoinersByTagAndRole(tag, type, userInfo1);
        }
        else {
            List<TExhibitorJoiner> exhibitorJoinerInfo = joinerService.queryAllJoinersByEids(eids);
            for(TExhibitorJoiner joinerInfo:exhibitorJoinerInfo){
                TExhibitor exhibitor = exhibitorManagerService.loadExhibitorByEid(joinerInfo.getEid());
                TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(exhibitor.getEid());
                TExhibitorBooth exhibitorBooth = exhibitorManagerService.queryBoothByEid(joinerInfo.getEid());
                TExhibitorJoinerEx exhibitorJoinerEx = new TExhibitorJoinerEx();
                log.info("====name: " + joinerInfo.getName() + "  tel: " + joinerInfo.getTelphone() + "  email: " + joinerInfo.getEmail() + "  pos: " + joinerInfo.getPosition());
                exhibitorJoinerEx.setName(joinerInfo.getName());
                exhibitorJoinerEx.setTelphone(joinerInfo.getTelphone());
                exhibitorJoinerEx.setEmail(joinerInfo.getEmail());
                exhibitorJoinerEx.setPosition(joinerInfo.getPosition());
                if(exhibitor != null) {
                    log.info("====company: " + exhibitorInfo.getCompany());
                    exhibitorJoinerEx.setCompany(exhibitorInfo.getCompany());
                }else{
                    exhibitorJoinerEx.setCompany("");
                }
                if(exhibitorBooth != null){
                    log.info("====boothNum: " + exhibitorBooth.getBoothNumber());
                    exhibitorJoinerEx.setExhibitorPosition(exhibitorBooth.getBoothNumber());
                }else {
                    exhibitorJoinerEx.setExhibitorPosition("");
                }
                exhibitorJoiners.add(exhibitorJoinerEx);
            }
        }
        model.put("list", exhibitorJoiners);
        String[] titles = new String[] { "公司", "展位号", "姓名", "职位", "手机", "邮件" };
        model.put("titles", titles);
        String[] columns = new String[] { "company", "exhibitorPosition", "name", "position", "telphone", "email" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{50,20,20,20,20,20};
        model.put("columnWidths", columnWidths);
        model.put("fileName", "参展人员.xls");
        model.put("sheetName", "参展人员信息");
        return new ModelAndView(new JXLExcelView(), model);
    }

    /**
     * 导入展商账号
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value="upload/exhibitors", method={RequestMethod.POST,RequestMethod.GET})
    public List<String> importExhibitors(@RequestParam MultipartFile file,
										 @ModelAttribute ImportExhibitorsRequest request) throws IOException {
    	File importFile = upload(file, "\\import", FilenameUtils.getBaseName(file.getOriginalFilename()) + new Date().getTime() + "." + FilenameUtils.getExtension(file.getOriginalFilename()));
        List<String> report = importExportService.importExhibitor(importFile, request);
//        FileUtils.deleteQuietly(importFile); // 删除临时文件
        return report;
    }
    
    /**
     * 导出会刊
     * @param eids
     * @param request
     * @param tag：表示当前的所属者
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/exportTransactionsToZip")
    public ModelAndView exportTransactionsToZip(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
                                                @RequestParam(value = "tag", defaultValue = "-1") Integer tag,
                                                @RequestParam(value = "type") Integer type,
                                                HttpServletRequest request,
                                                HttpServletResponse response,
                                                @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) throws Exception {
//    	String dirPath = "D:\\Users\\Foshi\\tmp\\" + UUID.randomUUID();
    	String appendix_directory = systemConfig.getVal(Constants.appendix_directory).replaceAll("\\\\\\\\", "\\\\");
        String randomFile = UUID.randomUUID().toString();
    	String destDir = appendix_directory + "\\tmp\\" + randomFile;
    	FileUtils.forceMkdir(new File(destDir)); // 创建临时文件夹
        if(eids[0] == -1){
            exportTransactions(null, destDir, tag, type, principle);
            importExportService.copyLogo(null, destDir);
        }else{
            exportTransactions(eids, destDir, tag, type, principle);
            importExportService.copyLogo(eids, destDir);
        }
	    CreateZip.zipToFile(destDir, randomFile);
    	return download(destDir, randomFile, request, response);
    }

    private void exportTransactions(Integer[] eids, String dirPath, Integer owner, Integer type, ManagerPrinciple principle) throws Exception {
        TUserInfo userInfo = (TUserInfo) principle.getAdmin();
        TUserInfo userInfo1 = userInfoService.findOneUserInfo(userInfo.getId());
    	List<TExhibitor> exhibitors = new ArrayList<TExhibitor>();
    	if(eids == null){
    		exhibitors = exhibitorManagerService.loadAllExhibitorsByTagAndRole(owner,type, userInfo1);
    	}else{
    		exhibitors = exhibitorManagerService.loadSelectedExhibitors(eids);
    	}
    	if(exhibitors.size() > 0){
    		for(TExhibitor exhibitor:exhibitors){
        		TExhibitorInfo exhibitorInfo = exhibitorManagerService.loadExhibitorInfoByEid(exhibitor.getEid());
        		String boothNumber = exhibitorManagerService.loadBoothNum(exhibitor.getEid());
        		Transaction transaction = new Transaction();
        		if(exhibitorInfo != null){
    	    			if((StringUtils.isNotEmpty(exhibitorInfo.getCompany()) || StringUtils.isNotEmpty(exhibitorInfo.getCompanyEn()))/* && StringUtils.isNotEmpty(boothNumber)*/){
    		        		transaction.setBoothNumber(boothNumber.trim());
    		        		if(StringUtils.isNotEmpty(exhibitorInfo.getCompany())) transaction.setCompany(exhibitorInfo.getCompany().trim());
    		        		else transaction.setCompany(null);
    		        		if(StringUtils.isNotEmpty(exhibitorInfo.getCompanyEn())) transaction.setCompanye(exhibitorInfo.getCompanyEn().trim());
    		        		else transaction.setCompanye(null);
    		        		if(StringUtils.isNotEmpty(exhibitorInfo.getAddress())) transaction.setAddress(exhibitorInfo.getAddress().trim());
    		        		else transaction.setAddress(null);
    		        		if(StringUtils.isNotEmpty(exhibitorInfo.getAddressEn())) transaction.setAddressEn(exhibitorInfo.getAddressEn().trim());
    		        		else transaction.setAddressEn(null);
    		        		if(StringUtils.isNotEmpty(exhibitorInfo.getZipcode())) transaction.setZipcode(exhibitorInfo.getZipcode().trim());
    		        		else transaction.setZipcode(null);
    		        		if(StringUtils.isNotEmpty(exhibitorInfo.getPhone())) transaction.setPhone(exhibitorInfo.getPhone().trim());
    		        		else transaction.setPhone(null);
    		        		if(StringUtils.isNotEmpty(exhibitorInfo.getFax())) transaction.setFax(exhibitorInfo.getFax().trim());
    		        		else transaction.setFax(null);
    		        		if(StringUtils.isNotEmpty(exhibitorInfo.getWebsite())) transaction.setWebsite(exhibitorInfo.getWebsite().trim());
    		        		else transaction.setWebsite(null);
    		        		if(StringUtils.isNotEmpty(exhibitorInfo.getEmail())) transaction.setEmail(exhibitorInfo.getEmail().trim());
    		        		else transaction.setEmail(null);
    		        		if(StringUtils.isNotEmpty(exhibitorInfo.getMark())) transaction.setMark(exhibitorInfo.getMark().trim());
    		        		else transaction.setMark(null);
    	    			}
        		}else if((StringUtils.isNotEmpty(exhibitorInfo.getCompany()) || StringUtils.isNotEmpty(exhibitorInfo.getCompanyEn()))/* && StringUtils.isNotEmpty(boothNumber)*/){
            		transaction.setBoothNumber(boothNumber.trim());
            		ModifyExhibitorInfoRequest modifyExhibitorInfoRequest = new ModifyExhibitorInfoRequest();
            		if(StringUtils.isNotEmpty(exhibitorInfo.getCompany())) {
            			transaction.setCompany(exhibitorInfo.getCompany().trim());
            			modifyExhibitorInfoRequest.setCompany(exhibitorInfo.getCompany().trim());
            		}
	        		else transaction.setCompany(null);
	        		if(StringUtils.isNotEmpty(exhibitorInfo.getCompanyEn())) {
	        			transaction.setCompanye(exhibitorInfo.getCompanyEn().trim());
	        			modifyExhibitorInfoRequest.setCompanyEn(exhibitorInfo.getCompanyEn().trim());
	        		}
	        		else transaction.setCompanye(null);
            		transaction.setAddress(null);
            		transaction.setZipcode(null);
            		transaction.setPhone(null);
            		transaction.setFax(null);
            		transaction.setWebsite(null);
            		transaction.setEmail(null);
            		transaction.setMark(null);
            		exhibitorManagerService.modifyExhibitorInfo(modifyExhibitorInfoRequest, exhibitor.getEid(), 1);
    			}else{
    				continue;
    			}
        		String filePath = "";
                if(StringUtils.isNotEmpty(exhibitorInfo.getCompany()) || StringUtils.isNotEmpty(exhibitorInfo.getCompanyEn())){
                    if(StringUtils.isNotEmpty(exhibitorInfo.getCompany()))
                        filePath = dirPath + "\\" + exhibitorInfo.getCompany().replaceAll("/", "") + boothNumber.replaceAll("/", "") + ".txt";
                    else
                        filePath = dirPath + "\\" + exhibitorInfo.getCompanyEn().replaceAll("/", "") + boothNumber.replaceAll("/", "") + ".txt";
                    importExportService.WriteStringToFile(getTransactionText(transaction), filePath);
                }
//	        		System.out.println("导出" + exhibitor.getCompany() + "成功");
        	}
    	}
//    	System.out.println("全部会刊信息导出完成");
    }
    
    /**
	 * 通过模板构造邮件内容，参数expressNumber将替换模板文件中的${expressNumber}标签。
	 */
	private String getTransactionText(Transaction transaction) throws Exception {
		// 通过指定模板名获取FreeMarker模板实例
		Template template = freeMarker.getConfiguration().getTemplate("transaction/transaction.ftl");
		
		// FreeMarker通过Map传递动态数据
		Map<Object, Object> model = new HashMap<Object, Object>();
		model.put("transaction", transaction); // 注意动态数据的key和模板标签中指定的属性相匹配
		
		// 解析模板并替换动态数据，最终content将替换模板文件中的${content}标签。
		String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		return htmlText;
	}
	
    @RequestMapping("/download")
    public ModelAndView download(String destDir, String zipName, HttpServletRequest request, HttpServletResponse response) throws Exception{
    	BufferedInputStream bis = null;
    	BufferedOutputStream bos = null;
    	String realName = URLEncoder.encode(zipName + ".zip", "UTF-8"); //设置下载文件名字
        /* 
         * @see http://support.microsoft.com/default.aspx?kbid=816868 
         */  
        if (realName.length() > 150) {  
            String guessCharset = "gb2312"; /*根据request的locale 得出可能的编码，中文操作系统通常是gb2312*/  
            realName = new String(realName.getBytes(guessCharset), "ISO8859-1");   
        }  
    	String fileName = destDir + "\\" + zipName + ".zip";  //获取完整的文件名
    	System.out.println(fileName);
    	long fileLength = new File(fileName).length();
    	response.setContentType("application/octet-stream");
    	response.setHeader("Content-Disposition", "attachment; filename=" + realName);
    	response.setHeader("Content-Length", String.valueOf(fileLength));
    	bis = new BufferedInputStream(new FileInputStream(fileName));
    	bos = new BufferedOutputStream(response.getOutputStream());
    	byte[] buff = new byte[2048];
    	int bytesRead;
    	while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
    			bos.write(buff, 0, bytesRead);
    	}
    	bis.close();
    	bos.close();
    	FileUtils.deleteDirectory(new File(destDir)); // 删除临时文件
        return null;
    }
    
    @RequestMapping("/upload")
    public File upload(@RequestParam MultipartFile file, String destDir, String fileName){
    	String appendix_directory = systemConfig.getVal(Constants.appendix_directory).replaceAll("\\\\\\\\", "\\\\");
    	if(StringUtils.isEmpty(fileName)) fileName = new Date().getTime() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
    	if(StringUtils.isNotEmpty(destDir)) destDir = appendix_directory + destDir;
    	else destDir = appendix_directory;
        File targetFile = new File(destDir, fileName);
        if(!targetFile.exists()) {
            targetFile.mkdirs();
        }
        try {
        	file.transferTo(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetFile;
    }

    //==================佛事展新增需求=================
    /**
     * 导出国内客商列表到Excel
     * @param cids
     * @return
     */
    @RequestMapping(value = "exportInlandCustomersToExcel", method = RequestMethod.POST)
    public ModelAndView exportInlandCustomersToExcel(@RequestParam(value = "cids", defaultValue = "") Integer[] cids,
                                                     @RequestParam(value = "rabbi") Integer rabbi) {
        Map model = new HashMap();
        List<TVisitorInfo> customers = new ArrayList<TVisitorInfo>();
        if(cids[0] == -1)
            customers = customerInfoManagerService.loadAllRabbiCustomer(1, rabbi);
        else
            customers = customerInfoManagerService.loadSelectedCustomers(cids, rabbi);
        List<ExportCustomerInfo> exportCustomer = importExportService.exportCustomer(customers);
        model.put("list", exportCustomer);
        String[] titles = new String[] { "ID","注册编号", "公司", "姓名", "性别", "职位","国家","城市","邮箱","手机", "传真","电话", "网址", "地址","登记时间","随行人员","随行人员联系方式", "感兴趣产品" };
        model.put("titles", titles);
        String[] columns = new String[] { "id","checkingNo", "company", "name", "sex", "position", "countryString", "city", "email", "phone", "tel", "faxString",  "website", "address", "createTime","accompanyName", "accompanyContact", "tmp_Interest" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{10,20,50,20,20,20,20,20,20,20,20,20,20,50,20,60,60,20};
        model.put("columnWidths", columnWidths);
        model.put("fileName", "客商基本信息.xls");
        model.put("sheetName", "客商基本信息");
        return new ModelAndView(new JXLExcelView(), model);
    }

    /**
     * 导出国外客商列表到Excel
     * @param cids
     * @return
     */
    @RequestMapping(value = "exportForeignCustomersToExcel", method = RequestMethod.POST)
    public ModelAndView exportForeignCustomersToExcel(@RequestParam(value = "cids", defaultValue = "") Integer[] cids,
                                                      @RequestParam(value = "rabbi") Integer rabbi) {
        Map model = new HashMap();
        List<TVisitorInfo> customers = new ArrayList<TVisitorInfo>();
        if(cids[0] == -1)
            customers = customerInfoManagerService.loadAllRabbiCustomer(2, rabbi);
        else
            customers = customerInfoManagerService.loadSelectedCustomers(cids, rabbi);
        List<ExportCustomerInfo> exportCustomer = importExportService.exportCustomer(customers);
        model.put("list", exportCustomer);
        String[] titles = new String[] { "ID","注册编号", "公司", "姓名", "性别","电话", "国家", "城市", "邮箱", "手机", "传真", "网址", "地址", "登记时间","随行人员","随行人员联系方式","感兴趣产品" };
        model.put("titles", titles);
        String[] columns = new String[] { "id","checkingNo", "company", "name",  "sex", "countryString", "city", "email", "phone", "tel", "faxString",  "website", "address", "createTime","accompanyName", "accompanyContact","tmp_Interest" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{10,20,50,20,20,20,20,20,20,20,20,20,20,50,60,60,20};
        model.put("columnWidths", columnWidths);
        model.put("fileName", "客商基本信息.xls");
        model.put("sheetName", "客商基本信息");
        return new ModelAndView(new JXLExcelView(), model);
    }

    /**
     * 导入素食展资料
     * @param file
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value="upload/vegetarianExhibitor", method={RequestMethod.POST,RequestMethod.GET})
    public ImportVegetarianOrThanilandExhibitorResponse importExhibitors(@RequestParam MultipartFile file,
                                                                         @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) throws IOException {
        File importFile = upload(file, "\\import", FilenameUtils.getBaseName(file.getOriginalFilename()) + new Date().getTime() + "." + FilenameUtils.getExtension(file.getOriginalFilename()));
        TUserInfo userInfo = (TUserInfo) principle.getAdmin();
        TUserInfo userInfo1 = userInfoService.findOneUserInfo(userInfo.getId());
        if(userInfo != null){
            ImportVegetarianOrThanilandExhibitorResponse report = importExportService.importVegetarianExhibitor(importFile, userInfo1, 1);
            return report;
        }else {
            return null;
        }
    }

    /**
     * 导入泰国展资料
     * @param file
     * @param principle
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value="upload/thailandExhibitor", method={RequestMethod.POST,RequestMethod.GET})
    public ImportVegetarianOrThanilandExhibitorResponse importThailandExhibitor(@RequestParam MultipartFile file,
                                                @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) throws IOException {
        File importFile = upload(file, "\\import", FilenameUtils.getBaseName(file.getOriginalFilename()) + new Date().getTime() + "." + FilenameUtils.getExtension(file.getOriginalFilename()));
        TUserInfo userInfo = (TUserInfo) principle.getAdmin();
        TUserInfo userInfo1 = userInfoService.findOneUserInfo(userInfo.getId());
        if(userInfo != null){
            ImportVegetarianOrThanilandExhibitorResponse report = importExportService.importThailandExhibitor(importFile, userInfo1, 2);
            return report;
        }else {
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "showWillImportCustomerInfo")
    public QueryDuplicatExhibitorResponse showWillImportCustomerInfo(@ModelAttribute QueryDuplicateExhibitorRequest request) {
        QueryDuplicatExhibitorResponse response = new QueryDuplicatExhibitorResponse();
        try {
            response = importExportService.getWillImportCustomerList(request);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("show will import Customer Info error.", e);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "showIsExistCustomerInfo")
    public QueryDuplicatExhibitorResponse showIsExistCustomerInfo(@RequestParam(value = "willImportCheckedItems", defaultValue = "") Integer[] willImportCheckedItems,
                                                                                @ModelAttribute QueryDuplicateExhibitorRequest request) {
        QueryDuplicatExhibitorResponse response = new QueryDuplicatExhibitorResponse();
        try {
            response = importExportService.getIsExistCustomerList(request, willImportCheckedItems);
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("show is exist Customer Info error.", e);
        }
        return response;
    }

    /**
     * 插入客商资料
     * @param tids
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "insertHistoryCustomerInfo", method = RequestMethod.POST)
    public BaseResponse insertHistoryCustomerInfo(@RequestParam(value = "tids", defaultValue = "") Integer[] tids,
                                                  @RequestParam(value = "type") Integer type,
                                                  @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        TUserInfo userInfo = (TUserInfo) principle.getAdmin();
        TUserInfo userInfo1 = userInfoService.findOneUserInfo(userInfo.getId());
        try {
            if(tids == null) throw new Exception();
            importExportService.insertCustomerInfoByTids(tids, type, userInfo1);
        } catch (Exception e) {
            log.error("remove customer info error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 忽略选中要插入的客商资料
     * @param tids
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "ignoreHistoryCustomerInfo", method = RequestMethod.POST)
    public BaseResponse ignoreHistoryCustomerInfo(@RequestParam(value = "tids", defaultValue = "") Integer[] tids,
                                                  @RequestParam(value = "type") Integer type,
                                                  @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
            if(tids == null) throw new Exception();
            importExportService.ignoreCustomerInfoByTids(tids);
        } catch (Exception e) {
            log.error("remove customer info error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 删除已经存在的展商资料
     * @param tids
     * @param principle
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "deleteExistExhibitorInfo", method = RequestMethod.POST)
    public BaseResponse deleteExistExhibitorInfo(@RequestParam(value = "tids", defaultValue = "") Integer[] tids,
                                                 @RequestParam(value = "type") Integer type,
                                                 @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        TUserInfo userInfo = (TUserInfo) principle.getAdmin();
        TUserInfo userInfo1 = userInfoService.findOneUserInfo(userInfo.getId());
        try {
            if(tids == null) throw new Exception();
            boolean flag1 = importExportService.refreshIsExistCustomerList(tids, userInfo1);
            boolean flag2 = importExportService.removeCustomerInfoByTids(tids, type, userInfo1);
            if(flag1 || flag2){
                response.setResultCode(0);
            }else{
                response.setResultCode(1);
            }
        } catch (Exception e) {
            log.error("remove customer info error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 导出历史客商资料列表到Excel
     * @param cids
     * @return
     */
    @RequestMapping(value = "exportHistoryVisitoryToExcel", method = RequestMethod.POST)
    public ModelAndView exportHistoryVisitoryToExcel(@RequestParam(value = "cids", defaultValue = "") Integer[] cids,
                                                     @RequestParam(value = "inOrOut") Integer inOrOut) {
        Map model = new HashMap();
        List<THistoryVisitorInfo> customers = new ArrayList<THistoryVisitorInfo>();
        if(cids[0] == -1)
            customers = customerInfoManagerService.loadAllHistoryVisitoryInfo(inOrOut);
        else
            customers = customerInfoManagerService.loadSelectedHistoryVisitory(cids, inOrOut);
        model.put("list", customers);
        String[] titles = new String[] { "地址", "公司名", "联系人", "手机", "电话", "传真", "邮箱" ,"电话记录" };
        model.put("titles", titles);
        String[] columns = new String[] { "address", "company", "contact", "mobile", "telphone", "fax", "email", "tel_remark" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{50,30,20,20,20,20,20,60};
        model.put("columnWidths", columnWidths);
        model.put("fileName", "客商基本信息.xls");
        model.put("sheetName", "客商基本信息");
        return new ModelAndView(new JXLExcelView(), model);
    }

    /**
     * 按类别导出历史客商资料列表到Excel
     * @param typeId
     * @return
     */
    @RequestMapping(value = "exportHistoryVisitoryByTypeToExcel", method = RequestMethod.POST)
    public ModelAndView exportHistoryVisitoryByTypeToExcel(@RequestParam(value = "typeId") Integer typeId,
                                                           @RequestParam(value = "inOrOut") Integer inOrOut) {
        Map model = new HashMap();
        List<THistoryVisitorInfo> customers = customerInfoManagerService.loadSelectedHistoryVisitoryByType(typeId, inOrOut);
        model.put("list", customers);
        String[] titles = new String[] { "地址", "公司名", "联系人", "手机", "电话", "传真", "邮箱" ,"电话记录" };
        model.put("titles", titles);
        String[] columns = new String[] { "address", "company", "contact", "mobile", "telphone", "fax", "email", "tel_remark" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{50,30,20,20,20,20,20,60};
        model.put("columnWidths", columnWidths);
        model.put("fileName", "客商基本信息.xls");
        model.put("sheetName", "客商基本信息");
        return new ModelAndView(new JXLExcelView(), model);
    }
}
