package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.dao.TVisitorTemplateDao;
import com.zhenhappy.ems.entity.TVisitorTemplate;
import com.zhenhappy.ems.manager.dto.QueryEmailTemplateRequest;
import com.zhenhappy.ems.manager.dto.QueryEmailTemplateResponse;
import com.zhenhappy.util.Page;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wangxd on 2016-03-30.
 */
@Service
public class CustomerTemplateServiceImp implements CustomerTemplateService {
    private static Logger log = Logger.getLogger(CustomerTemplateServiceImp.class);

    @Autowired
    private TVisitorTemplateDao customerTemplateDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<TVisitorTemplate> loadAllCustomerTemplate(){
        List<TVisitorTemplate> customerTemplatesList = customerTemplateDao.queryByHql("from TVisitorTemplate", new Object[]{});
        return customerTemplatesList;
    }

    @Transactional
    public QueryEmailTemplateResponse queryEmailManagerTemplate(QueryEmailTemplateRequest request){
        Page page = new Page();
        page.setPageSize(request.getRows());
        page.setPageIndex(request.getPage());
        List<TVisitorTemplate> customerTemplates = customerTemplateDao.queryPageByHQL("select count(*) from TVisitorTemplate",
                "from TVisitorTemplate", new Object[]{}, page);
        QueryEmailTemplateResponse response = new QueryEmailTemplateResponse();
        response.setResultCode(0);
        response.setRows(customerTemplates);
        response.setTotal(page.getTotalCount());
        return response;
    }

    @Transactional
    public void modifyEmailManagerTemplate(String value1, String value2, String value3, String value4, String value5,
                                                   String value6, String value7, String value8, String value9, String value10) throws Exception{
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'mail_register_subject_cn'", new Object[]{value1});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'mail_register_subject_en'", new Object[]{value2});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'mail_register_content_cn'", new Object[]{value3});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'mail_register_content_en'", new Object[]{value4});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'mail_invite_subject_cn'", new Object[]{value5});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'mail_invite_subject_en'", new Object[]{value6});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'mail_invite_content_cn'", new Object[]{value7});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'mail_invite_content_en'", new Object[]{value8});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'mail_register_policyDeclare_cn'", new Object[]{value9});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'mail_register_policyDeclare_en'", new Object[]{value10});
    }

    @Transactional
    public void modifyMessageManagerTemplate(String value1, String value2, String value3, String value4, String value5,
                                           String value6, String value7, String value8) throws Exception{
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'msg_register_subject_cn'", new Object[]{value1});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'msg_register_subject_en'", new Object[]{value2});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'msg_register_content_cn'", new Object[]{value3});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'msg_register_content_en'", new Object[]{value4});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'msg_invite_subject_cn'", new Object[]{value5});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'msg_invite_subject_en'", new Object[]{value6});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'msg_invite_content_cn'", new Object[]{value7});
        jdbcTemplate.update("update visitor_Template set tpl_value=? where tpl_key = 'msg_invite_content_en'", new Object[]{value8});
    }
}
