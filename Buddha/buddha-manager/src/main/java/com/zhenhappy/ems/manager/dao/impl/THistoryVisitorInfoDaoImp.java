package com.zhenhappy.ems.manager.dao.impl;

import com.zhenhappy.ems.dao.imp.BaseDaoHibernateImp;
import com.zhenhappy.ems.manager.dao.THistoryVisitorInfoDao;
import com.zhenhappy.ems.manager.entity.THistoryVisitorInfo;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * reated by wangxd on 2016-08-11.
 */
@Repository
public class THistoryVisitorInfoDaoImp extends BaseDaoHibernateImp<THistoryVisitorInfo> implements THistoryVisitorInfoDao {
    @Override
    public List<THistoryVisitorInfo> loadHistoryCustomersByIds(Integer[] ids, Integer inOrOut) {
        Query q = this.getSession().createQuery("select new THistoryVisitorInfo(a.id, a.address, a.company, a.contact, a.mobile, a.telphone, a.fax," +
                "a.email, a.tel_remark, a.type, a.create_time, a.update_time, a.fair_time, a.inlandOrOutland, a.field_bak2, a.field_bak3) from THistoryVisitorInfo a " +
                "where a.id in (:ids) and a.inlandOrOutland = " + inOrOut);
        q.setParameterList("ids", ids);
        return q.list();
    }

    @Override
    public List<THistoryVisitorInfo> loadHistoryCustomersByTypeId(Integer typeId, Integer inOrOut) {
        Query q = this.getSession().createQuery("select new THistoryVisitorInfo(a.id, a.address, a.company, a.contact, a.mobile, a.telphone, a.fax," +
                "a.email, a.tel_remark, a.type, a.create_time, a.update_time, a.fair_time, a.inlandOrOutland, a.field_bak2, a.field_bak3) from THistoryVisitorInfo a " +
                "where a.type in (:typeId) and a.inlandOrOutland = " + inOrOut);
        q.setParameter("typeId", typeId);
        return q.list();
    }
}
