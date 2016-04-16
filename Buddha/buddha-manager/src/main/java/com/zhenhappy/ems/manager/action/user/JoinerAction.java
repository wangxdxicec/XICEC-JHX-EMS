package com.zhenhappy.ems.manager.action.user;

import java.util.*;

import com.zhenhappy.ems.dto.BaseResponse;
import com.zhenhappy.ems.entity.TExhibitorInfo;
import com.zhenhappy.ems.entity.TExhibitorJoiner;
import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.dto.*;
import com.zhenhappy.ems.manager.service.ExhibitorManagerService;
import com.zhenhappy.ems.manager.service.ImportExportService;
import com.zhenhappy.ems.manager.service.JoinerManagerService;

import com.zhenhappy.ems.manager.util.JXLExcelView;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by wujianbin on 2014-04-24.
 */
@Controller
@RequestMapping(value = "/user")
@SessionAttributes(value = ManagerPrinciple.MANAGERPRINCIPLE)
public class JoinerAction extends BaseAction {

    private static Logger logger = Logger.getLogger(JoinerAction.class);

    @Autowired
    private JoinerManagerService joinerManagerService;

    @Autowired
    private ExhibitorManagerService exhibitorService;
    @Autowired
    private ImportExportService importExportService;

    @ResponseBody
    @RequestMapping(value = "queryJoiners", method = RequestMethod.POST)
    public QueryJoinersResponse queryJoiners(@ModelAttribute QueryJoinersRequest request) {
        QueryJoinersResponse response = null;
        try {
            TExhibitorInfo exhibitorInfo = exhibitorService.loadExhibitorInfoByEid(request.getEid());
            if (exhibitorInfo != null) {
                response = joinerManagerService.queryJoiners(request, exhibitorInfo.getEinfoid());
            } else {
                response = new QueryJoinersResponse();
                response.setRows(new ArrayList());
            }
        } catch (Exception e) {
            response = new QueryJoinersResponse();
            logger.error("query exhibitor joiners error.", e);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "addExhibitorJoiner", method = RequestMethod.POST)
    public BaseResponse addExhibitorJoiner(@ModelAttribute AddExhibitorJoinerRequest request, @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
        	TExhibitorJoiner exhibitorJoiner = new TExhibitorJoiner();
        	exhibitorJoiner.setEid(request.getEid());
        	exhibitorJoiner.setName(request.getName());
        	exhibitorJoiner.setPosition(request.getPosition());
        	exhibitorJoiner.setTelphone(request.getTelphone());
        	exhibitorJoiner.setEmail(request.getEmail());
        	exhibitorJoiner.setCreateTime(new Date());
            if(principle.getAdmin().getId() != null || "".equals(principle.getAdmin().getId())){
            	exhibitorJoiner.setAdmin(principle.getAdmin().getId());
            }
            joinerManagerService.addExhibitorJoiner(exhibitorJoiner);
        } catch (Exception e) {
        	logger.error("add exhibitor joiner error.", e);
            response.setResultCode(1);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "modifyExhibitorJoiner", method = RequestMethod.POST)
    public BaseResponse modifyExhibitorJoiner(@ModelAttribute ModifyExhibitorJoinerRequest request, @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
        	TExhibitorJoiner exhibitorJoiner = joinerManagerService.loadExhibitorJoinerById(request.getId());
        	exhibitorJoiner.setEid(request.getEid());
        	exhibitorJoiner.setName(request.getName());
        	exhibitorJoiner.setPosition(request.getPosition());
        	exhibitorJoiner.setTelphone(request.getTelphone());
        	exhibitorJoiner.setEmail(request.getEmail());
        	exhibitorJoiner.setCreateTime(new Date());
            if(principle.getAdmin().getId() != null || "".equals(principle.getAdmin().getId())){
            	exhibitorJoiner.setAdmin(principle.getAdmin().getId());
            	exhibitorJoiner.setAdminUpdateTime(new Date());
            }
            joinerManagerService.modifyExhibitorJoiner(exhibitorJoiner);
        } catch (Exception e) {
        	logger.error("modify exhibitor joiner error.", e);
            response.setResultCode(1);
        }
        return response;
    }
    
    @ResponseBody
    @RequestMapping(value = "removeJoiners", method = RequestMethod.POST)
    public BaseResponse removeJoiners(@RequestParam(value = "jids", defaultValue = "") Integer[] jids, @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        BaseResponse response = new BaseResponse();
        try {
        	if(jids == null) throw new Exception();
        	if(principle.getAdmin().getId() != null || "".equals(principle.getAdmin().getId())){
        		joinerManagerService.removeJoinerByJIds(jids, principle.getAdmin().getId());
            }
        } catch (Exception e) {
        	logger.error("remove exhibitor joiners error.", e);
            response.setResultCode(1);
        }
        return response;
    }

    /**
     * 导出参展人员列表到Excel
     * @param cids
     * @return
     */
    @RequestMapping(value = "exportJoinerExisitorToExcel", method = RequestMethod.POST)
    public ModelAndView exportJoinerExisitorToExcel(@RequestParam(value = "cids", defaultValue = "") Integer[] cids,
                                                    @RequestParam(value = "eid", defaultValue = "") Integer eid,
                                                    @ModelAttribute(ManagerPrinciple.MANAGERPRINCIPLE) ManagerPrinciple principle) {
        List<TExhibitorJoiner> joiners = new ArrayList<TExhibitorJoiner>();
        Map model = new HashMap();
        TExhibitorInfo exhibitorInfo = exhibitorService.loadExhibitorInfoByEid(eid);
        if(cids[0] == -1) {
            try {
                if (exhibitorInfo != null) {
                    joiners = joinerManagerService.queryJoinersEx(eid);
                }
            } catch (Exception e) {
                logger.error("query exhibitor joiners error.", e);
            }
        } else {
            if (exhibitorInfo != null) {
                joiners = joinerManagerService.loadSelectedJoinerByJid(cids);
            }
        }
        List<ExportExhibitorjoinerInfo> exportJoiner = importExportService.exportExhibitorJoiner(joiners);
        model.put("list", exportJoiner);
        String[] titles = new String[] { "姓名", "职位", "邮箱", "电话" };
        model.put("titles", titles);
        String[] columns = new String[] { "name", "position", "email", "telphone" };
        model.put("columns", columns);
        Integer[] columnWidths = new Integer[]{30,30,30,30};
        model.put("columnWidths", columnWidths);
        model.put("fileName", "参展人员列表.xls");
        model.put("sheetName", "参展人员基本信息");
        return new ModelAndView(new JXLExcelView(), model);
    }
}
