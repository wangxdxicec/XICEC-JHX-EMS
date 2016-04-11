package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.dao.TVisitorTemplateDao;
import com.zhenhappy.ems.entity.TVisitorTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangxd on 2016-03-30.
 */
@Service
public class CustomerTemplateServiceImp implements CustomerTemplateService {

    @Autowired
    private TVisitorTemplateDao customerTemplateDao;
    public List<TVisitorTemplate> loadAllCustomerTemplate(){
        List<TVisitorTemplate> customerTemplatesList = null;
        customerTemplatesList = customerTemplateDao.queryByHql("from TVisitorTemplate", new Object[]{});
        return customerTemplatesList;
    }
}
