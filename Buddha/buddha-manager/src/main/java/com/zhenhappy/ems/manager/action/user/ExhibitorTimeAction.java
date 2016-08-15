package com.zhenhappy.ems.manager.action.user;

import com.zhenhappy.ems.buddhatime.ExhibitorBuddhaTimeDao;
import com.zhenhappy.ems.buddhatime.ExhibitorBuddhaTimeManagerService;
import com.zhenhappy.ems.buddhatime.TExhibitorBuddhaTime;
import com.zhenhappy.ems.dto.BaseResponse;
import com.zhenhappy.ems.manager.action.BaseAction;
import com.zhenhappy.ems.manager.dto.ManagerPrinciple;
import com.zhenhappy.ems.manager.dto.QueryExhibitorTimeRequest;
import com.zhenhappy.ems.manager.dto.QueryExhibitorTimeResponse;
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
    private ExhibitorBuddhaTimeManagerService exhibitorTimeManagerService;

    @Autowired
    private ExhibitorBuddhaTimeDao exhibitorTimeDao;

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
            List<TExhibitorBuddhaTime> tExhibitorTimes = exhibitorTimeDao.queryPageByHQL("select count(*) from TExhibitorBuddhaTime", "from TExhibitorBuddhaTime", new Object[]{}, page);
            response.setResultCode(0);
            response.setRows(tExhibitorTimes);
            response.setTotal(page.getTotalCount());
        } catch (Exception e) {
            response.setResultCode(1);
            log.error("query buddha exhibitor time error.", e);
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
                                            @RequestParam(value = "buddha_Fair_Show_Date", defaultValue = "") String buddha_Fair_Show_Date,
                                            @RequestParam(value = "exhibitor_Info_Submit_Deadline", defaultValue = "") String exhibitor_Info_Submit_Deadline,
                                            @RequestParam(value = "buddha_Fair_Show_Date_Begin", defaultValue = "") String buddha_Fair_Show_Date_Begin) {
        BaseResponse response = new BaseResponse();
        try {
            exhibitorTimeManagerService.modifyExhibitorTime(buddha_Fair_Show_Date, exhibitor_Info_Submit_Deadline, buddha_Fair_Show_Date_Begin);
            response.setResultCode(0);
        } catch (Exception e) {
            log.error("modify buddha exhibitor time error.", e);
            response.setResultCode(1);
        }
        return response;
    }
}
