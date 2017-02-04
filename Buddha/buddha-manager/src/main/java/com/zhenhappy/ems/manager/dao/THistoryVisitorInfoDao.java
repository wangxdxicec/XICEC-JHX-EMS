package com.zhenhappy.ems.manager.dao;

import com.zhenhappy.ems.dao.BaseDao;
import com.zhenhappy.ems.manager.entity.THistoryVisitorInfo;

import java.util.List;

/**
 * Created by wangxd on 2016-08-11.
 */
public interface THistoryVisitorInfoDao extends BaseDao<THistoryVisitorInfo> {
    public List<THistoryVisitorInfo> loadHistoryCustomersByIds(Integer[] ids, Integer inOrOut);
    public List<THistoryVisitorInfo> loadHistoryCustomersByTypeId(Integer typeId, Integer inOrOut);
}
