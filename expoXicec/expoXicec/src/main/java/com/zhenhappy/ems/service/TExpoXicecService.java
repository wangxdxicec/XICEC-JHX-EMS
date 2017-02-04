package com.zhenhappy.ems.service;

import com.zhenhappy.ems.dao.expoxicec.TexpoXicecDao;
import com.zhenhappy.ems.entity.TExpoXicec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wangxd on 2017-01-16.
 */
@Service
public class TExpoXicecService {
    @Autowired
    private HibernateTemplate hibernateTemplate;
    @Autowired
    private TexpoXicecDao texpoXicecDao;

    public List<TExpoXicec> loadAll(){
        return hibernateTemplate.find("from TExpoXicec");
    }

    @Transactional
    public TExpoXicec loadTExpoXicec(Integer id){
        return hibernateTemplate.get(TExpoXicec.class,id);
    }

    @Transactional
    public TExpoXicec loadTExpoXicecByPhone(String phone, Integer fairType){
        List<TExpoXicec> tExpoXicecList = texpoXicecDao.queryByHql("from TExpoXicec where mobilephone = ? and fair_type = ? ", new Object[]{phone, fairType});
        return tExpoXicecList.size() > 0 ? tExpoXicecList.get(0) : null;
    }

    @Transactional
    public TExpoXicec loadTExpoXicecByPhoneAndPassword(String phone, String password, Integer fair_type){
        List<TExpoXicec> tExpoXicecList = texpoXicecDao.queryByHql("from TExpoXicec where mobilephone = ? and password = ? and fair_type = ? ",
                new Object[]{phone, password, fair_type});
        return tExpoXicecList.size() > 0 ? tExpoXicecList.get(0) : null;
    }
}
