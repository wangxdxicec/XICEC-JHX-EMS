package com.zhenhappy.ems.dao.imp;

import com.zhenhappy.ems.dao.VisitorInfoDao;
import com.zhenhappy.ems.entity.TVisitorInfo;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wujianbin on 2014-08-11.
 */
@Repository
public class VisitorInfoDaoImp extends BaseDaoHibernateImp<TVisitorInfo> implements VisitorInfoDao {

    @Override
    public List<TVisitorInfo> loadCustomersByIds(Integer[] ids) {
        Query q = this.getSession().createQuery("select new TVisitorInfo(a.id, a.email, a.checkingNo, a.password, a.firstName, a.lastName, a.sex, a.company, a.position, a.country, a.province, a.city, a.address, a.backupEmail, a.mobileCode, a.mobile, a.telCode, a.tel, a.telCode2, a.faxCode, a.fax, a.faxCode2, a.website, a.remark, a.createIp, a.createTime, a.updateIp, a.updateTime, a.sendEmailNum, a.sendEmailTime, a.isDisabled, a.guid) from TVisitorInfo a where a.id in (:ids)");
        q.setParameterList("ids", ids);
        return q.list();
    }
}
