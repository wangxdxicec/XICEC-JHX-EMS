package com.zhenhappy.ems.service.expoxicec;

import com.zhenhappy.ems.entity.*;
import com.zhenhappy.ems.service.ExhibitorService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxd on 2017-01-19.
 */
@Service
public class ExpoXicecManagerService extends ExhibitorService {
	private static Logger log = Logger.getLogger(ExpoXicecManagerService.class);

    /**
     * 根据展位号查询展位信息
     * @param boothNum
     * @return
     */
    @Transactional
    public List<TExpoExhibitorInfo> queryBoothByBoothNum(String boothNum) {
        List<TExpoExhibitorInfo> tExpoExhibitorInfoList = new ArrayList<TExpoExhibitorInfo>();
        List<TExhibitorBooth> tExhibitorBoothList = getHibernateTemplate().find("from TExhibitorBooth where boothNumber like '%" + boothNum + "%') ");
        if(tExhibitorBoothList != null && tExhibitorBoothList.size()>0){
            int i=1;
            for(TExhibitorBooth booth:tExhibitorBoothList){
                TExhibitor tExhibitor = loadExhibitorByEid(booth.getEid());
                if(tExhibitor != null && tExhibitor.getIsLogout() == 0){
                    TExhibitorInfo tExhibitorInfo = loadExhibitorInfoByEid(tExhibitor.getEid());
                    TExpoExhibitorInfo tExpoExhibitorInfo = new TExpoExhibitorInfo();
                    tExpoExhibitorInfo.setId(i++);
                    tExpoExhibitorInfo.setEid(tExhibitor.getEid());
                    tExpoExhibitorInfo.setBoothnumber(booth.getBoothNumber());
                    tExpoExhibitorInfo.setCompany(tExhibitorInfo.getCompany());
                    tExpoExhibitorInfo.setAreanumber(tExhibitor.getExhibitionArea());
                    tExpoExhibitorInfoList.add(tExpoExhibitorInfo);
                }
            }
        }
        return tExpoExhibitorInfoList;
    }
}
