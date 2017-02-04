package com.zhenhappy.ems.manager.action.user;

import com.zhenhappy.ems.dto.BaseResponse;
import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.dto.ManagerPrinciple;
import com.zhenhappy.ems.manager.dto.QueryExhibitorTimeRequest;
import com.zhenhappy.ems.manager.dto.QueryExhibitorTimeResponse;
import com.zhenhappy.ems.teatime.ExhibitorTeaTimeDao;
import com.zhenhappy.ems.teatime.ExhibitorTeaTimeManagerService;
import com.zhenhappy.ems.teatime.TExhibitorTeaTime;
import com.zhenhappy.util.Page;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangxd on 2016-06-28.
 */
@Controller
@RequestMapping(value = "user")
@SessionAttributes(value = ManagerPrinciple.MANAGERPRINCIPLE)
public class ExhibitorTimeAction extends BaseAction {
	
	@Autowired
    private ExhibitorTeaTimeManagerService exhibitorTimeManagerService;

    @Autowired
    private ExhibitorTeaTimeDao exhibitorTimeDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;
	
    private static Logger log = Logger.getLogger(ExhibitorTimeAction.class);
    
    /**
     * 分页查询所有时间对象
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryAllExhibitorTime")
    public QueryExhibitorTimeResponse queryAllExhibitorTime(@ModelAttribute QueryExhibitorTimeRequest request) {
        QueryExhibitorTimeResponse response = new QueryExhibitorTimeResponse();
        try {
            Page page = new Page();
            page.setPageSize(request.getRows());
            page.setPageIndex(request.getPage());
            List<TExhibitorTeaTime> tExhibitorTimes = exhibitorTimeDao.queryPageByHQL("select count(*) from TExhibitorTeaTime", "from TExhibitorTeaTime", new Object[]{}, page);
            response.setResultCode(0);
            response.setRows(tExhibitorTimes);
            response.setTotal(page.getTotalCount());
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query tea exhibitor time error.", e);
        }
        return response;
    }

    /**
     * 更新时间对象
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "modifyExhibitorTime")
    public BaseResponse modifyExhibitorTime(@ModelAttribute QueryExhibitorTimeRequest request,
                                            @RequestParam(value = "tea_Fair_Show_Date_Zh", defaultValue = "") String tea_Fair_Show_Date_Zh,
                                            @RequestParam(value = "tea_Fair_Show_Date_En", defaultValue = "") String tea_Fair_Show_Date_En,
                                            @RequestParam(value = "exhibitor_Info_Submit_Deadline_Zh", defaultValue = "") String exhibitor_Info_Submit_Deadline_Zh,
                                            @RequestParam(value = "exhibitor_Info_Submit_Deadline_En", defaultValue = "") String exhibitor_Info_Submit_Deadline_En,
                                            @RequestParam(value = "tea_Fair_Show_Year", defaultValue = "") String tea_Fair_Show_Year,
                                            @RequestParam(value = "tea_Fair_Show_Begin_Date", defaultValue = "") String tea_Fair_Show_Begin_Date,
                                            @RequestParam(value = "tea_Fair_Data_End_Html", defaultValue = "") String tea_Fair_Data_End_Html,
                                            @RequestParam(value = "tea_Fair_Contact_Submit_Deadline_Zh", defaultValue = "") String tea_Fair_Contact_Submit_Deadline_Zh,
                                            @RequestParam(value = "tea_Fair_Contact_Submit_Deadline_En", defaultValue = "") String tea_Fair_Contact_Submit_Deadline_En,
                                            @RequestParam(value = "tea_Fair_Invoice_Submit_Deadline_Zh", defaultValue = "") String tea_Fair_Invoice_Submit_Deadline_Zh,
                                            @RequestParam(value = "tea_Fair_Invoice_Submit_Deadline_En", defaultValue = "") String tea_Fair_Invoice_Submit_Deadline_En) {
        BaseResponse response = new BaseResponse();
        try {
            exhibitorTimeManagerService.modifyExhibitorTime(tea_Fair_Show_Date_Zh, tea_Fair_Show_Date_En, exhibitor_Info_Submit_Deadline_Zh,
                    exhibitor_Info_Submit_Deadline_En, tea_Fair_Show_Year, tea_Fair_Show_Begin_Date, tea_Fair_Data_End_Html,
                    tea_Fair_Contact_Submit_Deadline_Zh, tea_Fair_Contact_Submit_Deadline_En, tea_Fair_Invoice_Submit_Deadline_Zh,
                    tea_Fair_Invoice_Submit_Deadline_En);
            response.setResultCode(0);
        } catch (Exception e) {
            log.error("modify tea exhibitor time error.", e);
            response.setResultCode(1);
        }
        return response;
    }
}
