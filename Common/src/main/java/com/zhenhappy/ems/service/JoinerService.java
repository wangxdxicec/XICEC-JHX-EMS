package com.zhenhappy.ems.service;

import com.zhenhappy.ems.dao.JoinerDao;
import com.zhenhappy.ems.entity.TExhibitorJoiner;
import com.zhenhappy.ems.entity.TExhibitorJoinerEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wujianbin on 2014-04-20.
 */
@Service
public class JoinerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private JoinerDao joinerDao;

    public JoinerDao getJoinerDao() {
        return joinerDao;
    }

    public void setJoinerDao(JoinerDao joinerDao) {
        this.joinerDao = joinerDao;
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * query all joiners in exhibitor.
     * @param eid
     * @return
     */
    public List<TExhibitorJoiner> queryAllJoinersByEid(Integer eid){
        List<TExhibitorJoiner> joiners = hibernateTemplate.find("from TExhibitorJoiner where eid = ? order by isDelete",new Object[]{eid});
        return joiners;
    }

    /**
     * query all joiners in exhibitor.
     * @return
     */
    public List<TExhibitorJoinerEx> queryAllJoiners(){
        List<TExhibitorJoinerEx> joiners = new ArrayList<TExhibitorJoinerEx>();
        List<Map<String, Object>> items = jdbcTemplate.queryForList("select e.company, b.booth_number, j.name, j.position, j.telphone, j.email from t_exhibitor_joiner j, t_exhibitor_info e, t_exhibitor_booth b where j.eid = e.eid and e.eid = b.eid");
        for (Map<String, Object> item : items) {
            TExhibitorJoinerEx joiner = new TExhibitorJoinerEx();
            joiner.setCompany((String) item.get("company"));
            joiner.setName((String) item.get("name"));
            joiner.setPosition((String) item.get("position"));
            joiner.setEmail((String) item.get("email"));
            joiner.setTelphone((String) item.get("telphone"));
            joiner.setExhibitorPosition((String) item.get("booth_number"));
            joiners.add(joiner);
        }

        return joiners;
    }

    /**
     * query all joiners in exhibitor.
     * @return
     */
    public List<TExhibitorJoiner> queryAllJoinersByEids(Integer[] eids){
        return joinerDao.loadJoinersByEids(eids);
    }

    @Transactional
    public void saveOrUpdate(TExhibitorJoiner joiner){
        hibernateTemplate.saveOrUpdate(joiner);
    }

    @Transactional
    public int delete(Integer jid,Integer eid){
        return jdbcTemplate.update("delete from t_exhibitor_joiner where id =? and eid =?",new Object[]{jid,eid});
    }
}
