package com.zhenhappy.ems.dao;

import com.zhenhappy.ems.entity.WCustomerTemplate;

import java.util.List;

/**
 * Created by wujianbin on 2014-07-02.
 */
public interface WCustomerTemplateDao extends BaseDao<WCustomerTemplate> {

	List<WCustomerTemplate> loadWCustomerTemplate(Integer[] cids);
}
