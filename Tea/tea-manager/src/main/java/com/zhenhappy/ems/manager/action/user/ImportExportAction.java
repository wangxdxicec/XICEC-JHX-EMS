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
import com.zhenhappy.ems.entity.*;
import com.zhenhappy.ems.manager.dto.*;
import com.zhenhappy.ems.manager.service.*;
import com.zhenhappy.ems.manager.util.ZipCompressor;
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
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.zhenhappy.ems.manager.action.BaseAction;
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
    private CustomerInfoManagerService customerInfoManagerService;
    @Autowired
    private ImportExportService importExportService;
    @Autowired
	private FreeMarkerConfigurer freeMarker;// 注入FreeMarker模版封装框架
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private ExhibitorInfoDao exhibitorInfoDao;
    @Autowired
    private WVisaManagerService wVisaManagerService;
    @Autowired
    private TVisaManagerService tVisaManagerService;

    /**
     * 导出展商列表到Excel
     * @param eids
     * @return
     */
    @RequestMapping(value = "exportExhibitorsToExcel", method = RequestMethod.POST)
    public ModelAndView exportExhibitorsToExcel(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
                                                @RequestParam(value = "type") Integer type) {
        Map model = new HashMap();
        List<TeaExhibitor> exhibitors = new ArrayList<TeaExhibitor>();
        if(eids[0] == -1)
            exhibitors = exhibitorManagerService.loadAllExhibitorsByLogType(type);
        else
            exhibitors = exhibitorManagerService.loadSelectedExhibitors(eids);
        List<QueryExhibitorInfo> queryExhibitorInfos = importExportService.exportExhibitor(exhibitors);
        model.put("list", queryExhibitorInfos);
        String[] titles = new String[] { "展位号", "公司中文名", "公司英文名", "电话", "传真", "邮箱", "网址", "中文地址", "英文地址", "邮编", "产品分类", "主营产品(中文)", "主营产品(英文)", "公司简介", "发票抬头", "地税税号" };
        model.put("titles", titles);
        String[] columns = new String[] { "boothNumber", "company", "companyEn", "phone", "fax", "email", "website", "address", "addressEn", "zipcode", "productType", "mainProduct", "mainProductEn", "mark", "invoiceTitle", "invoiceNo" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20};
        model.put("columnWidths", columnWidths);
        model.put("fileName", "展商基本信息.xls");
        model.put("sheetName", "展商基本信息");
        return new ModelAndView(new JXLExcelView(), model);
    }

    /**
     * 导出展位信息到Excel
     * @return
     */
    @RequestMapping("/exportBoothInfoToExcel_2")
    public ModelAndView exportBoothInfoToExcel_2() {
        Map model = new HashMap();
        List<TeaExhibitor> exhibitors = exhibitorManagerService.loadAllExhibitors();
        List<QueryExhibitorInfoT> queryExhibitorInfos = new ArrayList<QueryExhibitorInfoT>();
        if(exhibitors != null){
            for(TeaExhibitor exhibitor:exhibitors){
                String boothNum = exhibitorManagerService.loadBoothNum(exhibitor.getEid());
                TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(exhibitor.getEid());
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
        List<TeaExhibitor> exhibitors = exhibitorManagerService.loadAllExhibitors();
        List<QueryExhibitorInfoT> queryExhibitorInfos = new ArrayList<QueryExhibitorInfoT>();
        if(exhibitors != null){
            for(TeaExhibitor exhibitor:exhibitors){
                String boothNum = exhibitorManagerService.loadBoothNum(exhibitor.getEid());
                TExhibitorInfo exhibitorInfo = loadExhibitorInfoByEid(exhibitor.getEid());
                String boothNums[] = boothNum.split(",");
                if(boothNums.length > 1) {
                    for(String booth:boothNums){
                        QueryExhibitorInfoT queryExhibitorInfoT = new QueryExhibitorInfoT();
                        queryExhibitorInfoT.setEid(exhibitor.getEid());
                        queryExhibitorInfoT.setBoothNumber(booth.trim());
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
                                                       @RequestParam(value = "type") Integer type) {
    	Map model = new HashMap();
        List<QueryBoothNumAndMeipai> boothNumAndMeipais = new ArrayList<QueryBoothNumAndMeipai>();
        if(eids[0] == -1) boothNumAndMeipais = exhibitorManagerService.loadBoothNumAndMeipai(null, type);
        else boothNumAndMeipais = exhibitorManagerService.loadBoothNumAndMeipai(eids, type);
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
     * 导出展商参展人员列表到Excel
     * @param eids
     * @return
     */
    @RequestMapping(value = "exportExhibitorJoinersToExcel", method = RequestMethod.POST)
    public ModelAndView exportExhibitorJoinersToExcel(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
                                                      @RequestParam(value = "type") Integer type) {
        Map model = new HashMap();
        List<TeaExhibitor> exhibitors = new ArrayList<TeaExhibitor>();
        if(eids[0] == -1)
            exhibitors = exhibitorManagerService.loadAllExhibitorsByLogType(type);
        else
            exhibitors = exhibitorManagerService.loadSelectedExhibitors(eids);

        List<ExportExhibitorJoiner> exportExhibitorJoiners = importExportService.exportExhibitorJoiners(exhibitors);
        model.put("list", exportExhibitorJoiners);
        String[] titles = new String[] { "展位号", "公司中文名", "公司英文名", "名字", "职位", "电话", "邮箱" };
        model.put("titles", titles);
        String[] columns = new String[] { "boothNumber", "company", "companye", "name", "position", "telphone", "email" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{20,20,20,20,20,20,20};
        model.put("columnWidths", columnWidths);
        model.put("fileName", "展商参展人员信息.xls");
        model.put("sheetName", "展商参展人员信息");
        return new ModelAndView(new JXLExcelView(), model);
    }

    /**
     * 导入展商账号
     * @param file
     * @param isCurrent 表示是否为今年参展的展商
     * @param request
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value="upload/exhibitors", method={RequestMethod.POST,RequestMethod.GET})
    public List<String> importExhibitors(@RequestParam MultipartFile file,
                                         @RequestParam Integer isCurrent,
                                         @RequestParam String country,
                                         @RequestParam String province,
                                         @RequestParam String area,
                                         @RequestParam String group,
                                         @RequestParam String tag,
                                         @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle,
										 @ModelAttribute ImportExhibitorsRequest request) throws IOException {
    	File importFile = upload(file, "\\import", FilenameUtils.getBaseName(file.getOriginalFilename()) + new Date().getTime() + "." + FilenameUtils.getExtension(file.getOriginalFilename()));
        List<String> report = importExportService.importExhibitor(importFile, request, isCurrent, country, province, area, group, tag, principle);
//        FileUtils.deleteQuietly(importFile); // 删除临时文件
        return report;
    }
    
    /**
     * 导出会刊
     * @param eids
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/exportTransactionsToZip")
    public ModelAndView exportTransactionsToZip(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
                                                @RequestParam(value = "type") Integer type,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
//    	String dirPath = "D:\\Users\\Foshi\\tmp\\" + UUID.randomUUID();
    	String appendix_directory = systemConfig.getVal(Constants.appendix_directory).replaceAll("\\\\\\\\", "\\\\");
        String randomFile = UUID.randomUUID().toString();
    	String destDir = appendix_directory + "\\tmp\\" + randomFile;
    	FileUtils.forceMkdir(new File(destDir)); // 创建临时文件夹
        if(eids[0] == -1){
            exportTransactions(null, type, destDir);
            importExportService.copyLogo(null, destDir);
        }else{
            exportTransactions(eids, type, destDir);
            importExportService.copyLogo(eids, destDir);
        }
	    CreateZip.zipToFile(destDir, randomFile);
    	return download(destDir, randomFile, request, response);
    }

    private void exportTransactions(Integer[] eids, Integer type, String dirPath) throws Exception {
    	List<TeaExhibitor> exhibitors = new ArrayList<TeaExhibitor>();
    	if(eids == null){
    		exhibitors = exhibitorManagerService.loadAllExhibitorsByLogType(type);
    	}else{
    		exhibitors = exhibitorManagerService.loadSelectedExhibitors(eids);
    	}
    	if(exhibitors.size() > 0){
    		for(TeaExhibitor exhibitor:exhibitors){
        		TExhibitorInfo exhibitorInfo = exhibitorManagerService.loadExhibitorInfoByEid(exhibitor.getEid());
        		String boothNumber = exhibitorManagerService.loadBoothNum(exhibitor.getEid());
        		Transaction transaction = new Transaction();
        		if(exhibitorInfo != null){
    	    			/*if((StringUtils.isNotEmpty(exhibitor.getCompany()) || StringUtils.isNotEmpty(exhibitor.getCompanye())) && StringUtils.isNotEmpty(boothNumber)){*/
                        if((StringUtils.isNotEmpty(exhibitorInfo.getCompany()) || StringUtils.isNotEmpty(exhibitorInfo.getCompanyEn()))/* && StringUtils.isNotEmpty(boothNumber)*/){
    		        		if(StringUtils.isNotEmpty(boothNumber)){
                                transaction.setBoothNumber(boothNumber.trim());
                            }
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
                    if(StringUtils.isNotEmpty(boothNumber)){
                        transaction.setBoothNumber(boothNumber.trim());
                    }
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
                /*茶博会需求开始*/
//              if(StringUtils.isNotEmpty(exhibitor.getCompany())) exhibitor.setCompany("");
//              if(StringUtils.isNotEmpty(exhibitor.getCompanye())) exhibitor.setCompanye("");
                if(StringUtils.isNotEmpty(exhibitorInfo.getCompany()) && StringUtils.isNotEmpty(exhibitorInfo.getCompanyEn())){
                    String filePath = dirPath + "\\" + exhibitorInfo.getCompany().replaceAll("/", "") + exhibitorInfo.getCompanyEn().replaceAll("/", "") + boothNumber.replaceAll("/", "") + ".txt";
        		    /*茶博会需求结束*/
                    importExportService.WriteStringToFile(getTransactionText(transaction), filePath);
//	        		System.out.println("导出" + exhibitor.getCompany() + "成功");
                }else if(StringUtils.isNotEmpty(exhibitorInfo.getCompany())) {
                    String filePath = dirPath + "\\" + exhibitorInfo.getCompany().replaceAll("/", "")  + boothNumber.replaceAll("/", "") + ".txt";
        		    /*茶博会需求结束*/
                    importExportService.WriteStringToFile(getTransactionText(transaction), filePath);
                }else if(StringUtils.isNotEmpty(exhibitorInfo.getCompanyEn())) {
                    String filePath = dirPath + "\\" +  exhibitorInfo.getCompanyEn().replaceAll("/", "") + boothNumber.replaceAll("/", "") + ".txt";
        		    /*茶博会需求结束*/
                    importExportService.WriteStringToFile(getTransactionText(transaction), filePath);
                }
        	}
    	}
//    	System.out.println("全部会刊信息导出完成");
    }

    private void exportInvoiceApplyForInscrease(Integer[] eids, String dirPath) throws Exception {
        List<TInvoiceApplyExtend> invoiceApplyExtendArrayList = new ArrayList<TInvoiceApplyExtend>();
        //查询开具增值发票对应的展商
        if(eids == null){
            invoiceApplyExtendArrayList = exhibitorManagerService.loadAllInvoiceApplyByInvoiceFlag();
        }else{
            invoiceApplyExtendArrayList = exhibitorManagerService.loadSelectedInvoiceApplyByInvoiceFlag(eids);
        }
        if(invoiceApplyExtendArrayList != null && invoiceApplyExtendArrayList.size() > 0){
            for(TInvoiceApplyExtend invoiceApplyExtend:invoiceApplyExtendArrayList){
                TExhibitorInfo exhibitorInfo = exhibitorManagerService.loadExhibitorInfoByEid(invoiceApplyExtend.getEid());
                String boothNumber = exhibitorManagerService.loadBoothNum(invoiceApplyExtend.getEid());
                ExhibitorInvoiceTransaction transaction = new ExhibitorInvoiceTransaction();
                if(exhibitorInfo != null){
                    String filePath = "";
                    if(StringUtils.isNotEmpty(exhibitorInfo.getCompany()) && exhibitorInfo.getCompany() != null){
                        transaction.setBoothNumber(boothNumber.trim());
                        if(StringUtils.isNotEmpty(exhibitorInfo.getCompany())) {
                            transaction.setCompanyZh(exhibitorInfo.getCompany().trim());
                            transaction.setInvoice_company(invoiceApplyExtend.getInvoice_company());
                            transaction.setInvoice_taxpayer_no(invoiceApplyExtend.getInvoice_taxpayer_no());
                            transaction.setInvoice_bank_name(invoiceApplyExtend.getInvoice_bank_name());
                            transaction.setInvoice_bank_account(invoiceApplyExtend.getInvoice_bank_account());
                            transaction.setInvoice_company_address(invoiceApplyExtend.getInvoice_company_address());
                            transaction.setInvoice_company_tel(invoiceApplyExtend.getInvoice_company_tel());
                            transaction.setInvoice_company_contact(invoiceApplyExtend.getInvoice_company_contact());
                            transaction.setInvoice_general_taxpayer_flag(invoiceApplyExtend.getInvoice_general_taxpayer_flag());
                            transaction.setInvoice_general_tax_type(invoiceApplyExtend.getInvoice_general_tax_type());
                            String dirPathValue = "";
                            if(StringUtils.isNotEmpty(exhibitorInfo.getCompany())){
                                dirPathValue = dirPath + exhibitorInfo.getCompany();
                            }else{
                                dirPathValue = dirPath + exhibitorInfo.getCompanyEn();
                            }
                            //判断目录是否存在，如果不存在，就先创建
                            File fileTemp = new File(dirPathValue);
                            if(!fileTemp.exists()  && !fileTemp.isDirectory()) {
                                fileTemp.mkdir();
                            }
                            filePath = dirPathValue + "\\" + boothNumber.replaceAll("/", "") + ".txt";
                            if(new File(filePath).exists()) {
                                new File(filePath).createNewFile();
                            }
                            importExportService.WriteStringToFile(getExhibitorInvitationTransactionText(transaction), filePath);
                        }
                        else {
                            transaction.setCompanyZh("");
                        }
                    }
                }else{
                    continue;
                }
            }
        }
    }

    /**
     * 导出所有展商增值税专用发票
     * @param eids
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/exportExhibitorInvitationToZip")
    public ModelAndView exportExhibitorInvitationToZip(@RequestParam(value = "eids", defaultValue = "") Integer[] eids,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) throws Exception {
        String appendix_directory = systemConfig.getVal(Constants.appendix_directory).replaceAll("\\\\\\\\", "\\\\");
        //String randomFile = UUID.randomUUID().toString();
        String randomFile = "C:\\Program Files\\Apache Software Foundation\\appendix\\tea\\exhibitor_invoice\\out\\exhibitorInvoice.zip";
        String randomFile1 = "exhibitorInvoice";
        //String destDir = appendix_directory + "\\exhibitor_invoice\\" + randomFile;
        String destDir = appendix_directory + "\\exhibitor_invoice\\out\\";
        String downloadDir = appendix_directory + "\\exhibitor_invoice\\out\\exhibitorInvoice.zip";
        FileUtils.deleteDirectory(new File(destDir)); 	//删除现有文件
        FileUtils.forceMkdir(new File(destDir)); 		//创建新文件夹
        if(eids[0] == -1){
            exportInvoiceApplyForInscrease(null, destDir);
            importExportService.copyExhibitorInvoiceApplyImage(null, destDir);
        }else{
            exportInvoiceApplyForInscrease(eids, destDir);
            importExportService.copyExhibitorInvoiceApplyImage(eids, destDir);
        }
        zipCompressor(eids, randomFile, destDir);
        //CreateZip.zipToFile(destDir, randomFile);
        //return download(destDir, randomFile1, request, response);
        return downloadInvoiceFile(downloadDir, destDir, request, response);
    }

    /**
     * 通过模板构造邮件内容，参数expressNumber将替换模板文件中的${expressNumber}标签。
     */
    private String getExhibitorInvitationTransactionText(ExhibitorInvoiceTransaction transaction) throws Exception {
        // 通过指定模板名获取FreeMarker模板实例
        Template template = freeMarker.getConfiguration().getTemplate("transaction/exhibitorInvoiceTransaction.ftl");

        // FreeMarker通过Map传递动态数据
        Map<Object, Object> model = new HashMap<Object, Object>();
        model.put("transaction", transaction); // 注意动态数据的key和模板标签中指定的属性相匹配

        // 解析模板并替换动态数据，最终content将替换模板文件中的${content}标签。
        String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        return htmlText;
    }

    @RequestMapping("/download")
    public ModelAndView downloadInvoiceFile(String randomFile, String destDir, HttpServletRequest request, HttpServletResponse response) throws Exception{
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        String realName = URLEncoder.encode(randomFile, "UTF-8"); //设置下载文件名字
        /*
         * @see http://support.microsoft.com/default.aspx?kbid=816868
         */
        if (realName.length() > 150) {
            String guessCharset = "gb2312"; /*根据request的locale 得出可能的编码，中文操作系统通常是gb2312*/
            realName = new String(realName.getBytes(guessCharset), "ISO8859-1");
        }
        //String fileName = destDir + "\\" + zipName + ".zip";  //获取完整的文件名
        System.out.println(randomFile);
        long fileLength = new File(randomFile).length();
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-Disposition", "attachment; filename=" + realName);
        response.setHeader("Content-Length", String.valueOf(fileLength));
        bis = new BufferedInputStream(new FileInputStream(randomFile));
        bos = new BufferedOutputStream(response.getOutputStream());
        byte[] buff = new byte[2048];
        int bytesRead;
        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
            bos.write(buff, 0, bytesRead);
        }
        bos.flush();
        bis.close();
        bos.close();
        bos = null;
        //FileUtils.deleteDirectory(new File(randomFile)); // 删除临时文件
        FileUtils.deleteDirectory(new File(destDir));
        return null;
    }

    private void zipCompressor(Integer[] eids, String dirPath, String filePath) throws Exception {
        List<TInvoiceApplyExtend> invoiceApplyExtendArrayList = new ArrayList<TInvoiceApplyExtend>();
        //查询开具增值发票对应的展商
        if(eids[0] == -1){
            invoiceApplyExtendArrayList = exhibitorManagerService.loadAllInvoiceApplyByInvoiceFlag();
        }else{
            invoiceApplyExtendArrayList = exhibitorManagerService.loadSelectedInvoiceApplyByInvoiceFlag(eids);
        }
        if(invoiceApplyExtendArrayList != null){
            int listSize = invoiceApplyExtendArrayList.size();
            String companyNameArray[] = new String[listSize];
            for(int i=0;i<listSize;i++){
                TInvoiceApplyExtend invoiceApplyExtend = invoiceApplyExtendArrayList.get(i);
                TExhibitorInfo exhibitorInfo = exhibitorManagerService.loadExhibitorInfoByEid(invoiceApplyExtend.getEid());
                if(exhibitorInfo != null){
                    if(StringUtils.isNotEmpty(exhibitorInfo.getCompany())){
                        companyNameArray[i] = exhibitorInfo.getCompany();
                    }else{
                        companyNameArray[i] = exhibitorInfo.getCompanyEn();
                    }
                }else{
                    continue;
                }
            }
            ZipCompressor.compressForManyDirectory(filePath, companyNameArray, dirPath);
        }
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
    	//System.out.println(fileName);
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
     * 根据年份和时间导出客商列表到Excel
     * @param fieldTime
     * @param inlandOrForeign
     * @return
     */
    @RequestMapping(value = "exportCustomersByYearOrTimeToExcel", method = RequestMethod.POST)
    public ModelAndView exportCustomersByYearOrTimeToExcel(@ModelAttribute QueryCustomerRequest request,
                                                           @RequestParam(value = "fieldTime") String fieldTime,
                                                           @RequestParam(value = "inlandOrForeign") Integer inlandOrForeign) {
        Map model = new HashMap();
        List<TTeaVisitorInfo> customers = customerInfoManagerService.loadCustomerByYearOrTime(request, fieldTime, inlandOrForeign);
        List<ExportCustomerInfo> exportCustomer = importExportService.exportCustomer(customers, inlandOrForeign);
        if(inlandOrForeign == 1){
            model.put("list", exportCustomer);
            String[] titles = new String[] { "预约登记号", "公司", "地址", "联系人", "职位", "性别", "手机", "邮箱", "座机", "传真", "网址", "登记时间","修改时间", "备注" };
            model.put("titles", titles);
            String[] columns = new String[] { "checkingNo", "company", "address", "name", "position", "sex","phone", "email", "tel", "faxString",  "website", "createTime", "updateTime", "remark" };
            model.put("columns", columns);
            Integer[] columnWidths = new Integer[]{20,20,20,20,20,20,20,20,20,20,20,20,20,20};
            model.put("columnWidths", columnWidths);
            model.put("fileName", "国内客商基本信息.xls");
            model.put("sheetName", "国内客商基本信息");
        }else {
            model.put("list", exportCustomer);
            String[] titles = new String[] { "预约登记号", "国家", "公司中文名", "性别+姓名+职位", "地址", "邮箱", "网址", "城市", "手机", "电话", "传真", "登记时间","修改时间","备注" };
            model.put("titles", titles);
            String[] columns = new String[] { "checkingNo","countryString", "company", "name", "address", "email", "website", "city", "phone", "tel", "faxString", "createTime", "updateTime", "remark" };
            model.put("columns", columns);
            Integer[] columnWidths = new Integer[]{20,20,20,20,20,20,20,20,20,20,20,20,20,20};
            model.put("columnWidths", columnWidths);
            model.put("fileName", "国外客商基本信息.xls");
            model.put("sheetName", "国外客商基本信息");
        }
        return new ModelAndView(new JXLExcelView(), model);
    }

    /**
     * 导出国内客商列表到Excel
     * @param cids
     * @return
     */
    @RequestMapping(value = "exportInlandCustomersToExcel", method = RequestMethod.POST)
    public ModelAndView exportInlandCustomersToExcel(@RequestParam(value = "cids", defaultValue = "") Integer[] cids) {
        Map model = new HashMap();
        List<TTeaVisitorInfo> customers = new ArrayList<TTeaVisitorInfo>();
        if(cids[0] == -1)
            customers = customerInfoManagerService.loadAllInlandCustomer();
        else
            customers = customerInfoManagerService.loadSelectedCustomers(cids);
        List<ExportCustomerInfo> exportCustomer = importExportService.exportCustomer(customers, 1);
        model.put("list", exportCustomer);
        String[] titles = new String[] { "预约登记号", "公司", "地址", "联系人", "职位", "性别", "手机", "邮箱", "座机", "传真", "网址", "登记时间","修改时间", "备注" };
        model.put("titles", titles);
        String[] columns = new String[] { "checkingNo", "company", "address", "name", "position", "sex","phone", "email", "tel", "faxString",  "website", "createTime", "updateTime", "remark" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{20,20,20,20,20,20,20,20,20,20,20,20,20,20};
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
    public ModelAndView exportForeignCustomersToExcel(@RequestParam(value = "cids", defaultValue = "") Integer[] cids) {
        Map model = new HashMap();
        List<TTeaVisitorInfo> customers = new ArrayList<TTeaVisitorInfo>();
        if(cids[0] == -1)
            customers = customerInfoManagerService.loadAllForeignCustomer();
        else
            customers = customerInfoManagerService.loadSelectedCustomers(cids);
        List<ExportCustomerInfo> exportCustomer = importExportService.exportCustomer(customers, 2);
        model.put("list", exportCustomer);
        String[] titles = new String[] { "预约登记号", "国家", "公司中文名", "性别+姓名+职位", "地址", "邮箱", "网址", "城市", "手机", "电话", "传真", "登记时间","修改时间","备注" };
        model.put("titles", titles);
        String[] columns = new String[] { "checkingNo","countryString", "company", "name", "address", "email", "website", "city", "phone", "tel", "faxString", "createTime", "updateTime", "remark" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{20,20,20,20,20,20,20,20,20,20,20,20,20,20};
        model.put("columnWidths", columnWidths);
        model.put("fileName", "客商基本信息.xls");
        model.put("sheetName", "客商基本信息");
        return new ModelAndView(new JXLExcelView(), model);
    }

    /**
     * 导出展商VISA信息到Excel
     * @param vids
     * @return
     */
    @RequestMapping(value = "exportTVisasToExcel", method = RequestMethod.POST)
    public ModelAndView exportTVisasToExcel(@RequestParam(value = "vids", defaultValue = "") Integer[] vids) {
        Map model = new HashMap();
        List<TVisa> tVisas = new ArrayList<TVisa>();
        if(vids[0] == -1)
            tVisas = tVisaManagerService.loadAllTVisas();
        else
            tVisas = tVisaManagerService.loadSelectedTVisas(vids);
        List<ExportTVisa> queryExportTVisas = importExportService.exportTVisas(tVisas);
        model.put("list", queryExportTVisas);
        String[] titles = new String[] { "邮寄情况", "发送状态", "展商/客商", "商务局提交系列号", "Passport Name", "Passport No", "出生日期", "国籍", "称呼", "职务", "公司", "地址", "申请地", "停留时间开始", "停留时间结束", "Email",  "网址", "电话", "传真", "要不要邮寄", "快递公司", "快递单号", "Exp date有效期", "提交日期", "Hotel" };
        model.put("titles", titles);
        String[] columns = new String[] { "emailDetail", "sendStatus", "exhibitor", "commericalSeriesNo", "passportName", "passportNo", "birth", "nationality", "gender", "jobTitle", "companyName", "address", "applyFor", "from", "to", "emailInfo", "companyWebsite", "tel", "fax", "needPost", "expressTp", "expressNo", "expDate", "createTime","hotelAddress"};
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{ 20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20 };
        model.put("columnWidths", columnWidths);
        model.put("fileName", "VISA.xls");
        model.put("sheetName", "VISA");
        return new ModelAndView(new JXLExcelView(), model);
    }

    /**
     * 导出客商VISA信息到Excel
     * @param vids
     * @return
     */
    @RequestMapping(value = "exportWVisasToExcel", method = RequestMethod.POST)
    public ModelAndView exportWVisasToExcel(@RequestParam(value = "vids", defaultValue = "") Integer[] vids) {
        Map model = new HashMap();
        List<WVisa> wVisas = new ArrayList<WVisa>();
        if(vids[0] == -1)
            wVisas = wVisaManagerService.loadAllWVisas();
        else
            wVisas = wVisaManagerService.loadSelectedWVisas(vids);
        List<ExportWVisa> queryExportWVisas = importExportService.exportWVisas(wVisas);
        model.put("list", queryExportWVisas);
        String[] titles = new String[] { "邮寄情况", "发送状态", "展商/客商", "商务局提交系列号","Passport Name", "Passport No", "出生日期", "国籍", "称呼", "职务", "公司", "地址", "申请地", "停留时间开始", "停留时间结束", "Email", "网址", "电话", "传真", "要不要邮寄", "快递公司", "快递单号", "Exp date有效期", "提交日期","Hotel" };
        model.put("titles", titles);
        String[] columns = new String[] { "emailDetail", "sendStatus", "customer", "commericalSeriesNo", "passportName", "passportNo", "dateOfBirth", "nationality", "gender", "position", "company", "address", "chineseEmbassy", "durationBeginTime", "durationEndTime", "email", "website", "telephone", "fax", "needPost", "expressTp", "expressNo", "expDate", "createTime","hotelAddress" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{ 20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20 };
        model.put("columnWidths", columnWidths);
        model.put("fileName", "VISA.xls");
        model.put("sheetName", "VISA");
        return new ModelAndView(new JXLExcelView(), model);
    }
}
