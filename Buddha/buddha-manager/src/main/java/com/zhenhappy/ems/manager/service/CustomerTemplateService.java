package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.entity.TVisitorTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wangxd on 2016-03-30.
 */
public interface CustomerTemplateService {

    @Transactional
    public List<TVisitorTemplate> loadAllCustomerTemplate();

}
