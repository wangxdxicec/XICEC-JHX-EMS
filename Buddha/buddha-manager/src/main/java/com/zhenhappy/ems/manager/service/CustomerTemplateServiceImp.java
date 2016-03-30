package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.dao.WCustomerTemplateDao;
import com.zhenhappy.ems.entity.WCustomerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangxd on 2016-03-30.
 */
@Service
public class CustomerTemplateServiceImp implements CustomerTemplateService {

    @Autowired
    private WCustomerTemplateDao customerTemplateDao;
    public List<WCustomerTemplate> loadAllCustomerTemplate(){
        List<WCustomerTemplate> customerTemplatesList = null;
        customerTemplatesList = customerTemplateDao.queryByHql("from WCustomerTemplate", new Object[]{});
        return customerTemplatesList;
    }
}
