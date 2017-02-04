package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.dao.CustomerInfoDao;
import com.zhenhappy.ems.entity.WCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wujianbin on 2014-08-11.
 */
@Service
public class CustomerInfoManagerService {
	@Autowired
	private CustomerInfoDao customerInfoDao;


	/**
	 * 根据CheckingNo查询展商列表
	 * @return
	 */
	@Transactional
	public WCustomer loadCustomerSurveyInfoById(String checkNo) {
		String hql = "from WCustomer where checkingNo='" + checkNo + "'";
		List<WCustomer> customers = customerInfoDao.queryByHql(hql, new Object[]{});
		return customers.size() > 0 ? customers.get(0) : null;
	}
}
