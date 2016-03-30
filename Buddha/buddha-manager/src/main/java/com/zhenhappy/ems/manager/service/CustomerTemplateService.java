package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.entity.WCustomerTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wangxd on 2016-03-30.
 */
public interface CustomerTemplateService {

    @Transactional
    public List<WCustomerTemplate> loadAllCustomerTemplate();

}
