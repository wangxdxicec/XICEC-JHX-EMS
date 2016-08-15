package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.manager.dao.TVisitorTypeDao;
import com.zhenhappy.ems.manager.entity.TVisitorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangxd on 2016-08-11.
 */
@Service
public class TVisitorTypeServiceImp implements TVisitorTypeService {

    @Autowired
    private TVisitorTypeDao tVisitorTypeDao;
    public List<TVisitorType> loadVisitorType(){
        List<TVisitorType> tVisitorTypeList = tVisitorTypeDao.queryByHql("from TVisitorType", new Object[]{});
        return tVisitorTypeList;
    }
}
