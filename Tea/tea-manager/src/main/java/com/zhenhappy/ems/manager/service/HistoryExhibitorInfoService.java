package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.manager.dao.HistoryExhibitorInfoDao;
import com.zhenhappy.ems.manager.entity.THistoryExhibitorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wangxd on 2016-07-14.
 */
@Service
public class HistoryExhibitorInfoService {
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private HistoryExhibitorInfoDao historyExhibitorInfoDao;

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    @Transactional
    public void saveTHistoryExhibitorInfo(THistoryExhibitorInfo tHistoryExhibitorInfo) {
        hibernateTemplate.save(tHistoryExhibitorInfo);
    }

    @Transactional
    public List<THistoryExhibitorInfo> isExistTHistoryExhibitorInfo(String company_zh, String company_en, String boothNum, String fairYear) {
        List<THistoryExhibitorInfo> tHistoryExhibitorInfoList = historyExhibitorInfoDao.queryByHql("from THistoryExhibitorInfo where company_zh = ? and company_en=? and " +
                "booth_number=? and fair_year=?", new Object[]{company_zh, company_en, boothNum, fairYear});
        return tHistoryExhibitorInfoList;
    }

    /**
     * 通过id获取归档资料
     * @param id
     * @return
     */
    @Transactional
    public THistoryExhibitorInfo loadHistoryExhibitorInfoById(Integer id) {
        return historyExhibitorInfoDao.query(id);
    }
}
