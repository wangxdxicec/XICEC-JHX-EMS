package com.zhenhappy.ems.dao.imp;

import com.zhenhappy.ems.dao.WCustomerTemplateDao;
import com.zhenhappy.ems.entity.WCustomerTemplate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangxd9 on 2016-03-30.
 */
@Repository
public class WCustomerTemplateDaoImp extends BaseDaoHibernateImp<WCustomerTemplate> implements WCustomerTemplateDao {

	@Override
	public List<WCustomerTemplate> loadWCustomerTemplate(Integer[] cids) {
		Query q = this.getSession().createQuery("select new WCustomerTemplate(c.id) from WCustomerTemplate c where c.id in (:pids)");
		q.setParameterList("cids", cids);
		return q.list();
	}
	
}
