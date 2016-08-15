package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.manager.entity.TVisitorType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wangxd on 2016-08-11.
 */
public interface TVisitorTypeService {

    @Transactional
    public List<TVisitorType> loadVisitorType();

}
