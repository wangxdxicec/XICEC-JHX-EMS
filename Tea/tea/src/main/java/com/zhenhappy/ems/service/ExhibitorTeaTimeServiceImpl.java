package com.zhenhappy.ems.service;

import com.zhenhappy.ems.teatime.TExhibitorTeaTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExhibitorTeaTimeServiceImpl implements ExhibitorTeaTimeService {
    private static final Logger logger = LoggerFactory.getLogger(ExhibitorTeaTimeServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public TExhibitorTeaTime loadExhibitorTime() {
        String tea_Fair_Show_Date_Zh = jdbcTemplate.queryForObject("select tea_Fair_Show_Date_Zh from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);
        String tea_Fair_Show_Date_En = jdbcTemplate.queryForObject("select tea_Fair_Show_Date_En from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);
        String exhibitor_Info_Submit_Deadline_Zh = jdbcTemplate.queryForObject("select exhibitor_Info_Submit_Deadline_Zh from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);
        String exhibitor_Info_Submit_Deadline_En = jdbcTemplate.queryForObject("select exhibitor_Info_Submit_Deadline_En from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);
        String tea_Fair_Show_Year = jdbcTemplate.queryForObject("select tea_Fair_Show_Year from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);
        String tea_Fair_Show_Begin_Date = jdbcTemplate.queryForObject("select tea_Fair_Show_Begin_Date from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);
        String tea_Fair_Data_End_Html = jdbcTemplate.queryForObject("select tea_Fair_Data_End_Html from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);
        String tea_Fair_Contact_Submit_Deadline_Zh = jdbcTemplate.queryForObject("select tea_Fair_Contact_Submit_Deadline_Zh from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);
        String tea_Fair_Contact_Submit_Deadline_En = jdbcTemplate.queryForObject("select tea_Fair_Contact_Submit_Deadline_En from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);
        String tea_Fair_Invoice_Submit_Deadline_Zh = jdbcTemplate.queryForObject("select tea_Fair_Invoice_Submit_Deadline_Zh from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);
        String tea_Fair_Invoice_Submit_Deadline_En = jdbcTemplate.queryForObject("select tea_Fair_Invoice_Submit_Deadline_En from [t_exhibitor_teafair_time] ", new Object[]{}, String.class);

        TExhibitorTeaTime tExhibitorTime = new TExhibitorTeaTime();
        tExhibitorTime.setTea_Fair_Show_Date_Zh(tea_Fair_Show_Date_Zh);
        tExhibitorTime.setTea_Fair_Show_Date_En(tea_Fair_Show_Date_En);
        tExhibitorTime.setExhibitor_Info_Submit_Deadline_Zh(exhibitor_Info_Submit_Deadline_Zh);
        tExhibitorTime.setExhibitor_Info_Submit_Deadline_En(exhibitor_Info_Submit_Deadline_En);
        tExhibitorTime.setTea_Fair_Show_Year(tea_Fair_Show_Year);
        tExhibitorTime.setTea_Fair_Show_Begin_Date(tea_Fair_Show_Begin_Date);
        tExhibitorTime.setTea_Fair_Data_End_Html(tea_Fair_Data_End_Html);
        tExhibitorTime.setTea_Fair_Contact_Submit_Deadline_Zh(tea_Fair_Contact_Submit_Deadline_Zh);
        tExhibitorTime.setTea_Fair_Contact_Submit_Deadline_En(tea_Fair_Contact_Submit_Deadline_En);
        tExhibitorTime.setTea_Fair_Invoice_Submit_Deadline_Zh(tea_Fair_Invoice_Submit_Deadline_Zh);
        tExhibitorTime.setTea_Fair_Invoice_Submit_Deadline_En(tea_Fair_Invoice_Submit_Deadline_En);
        return tExhibitorTime;
    }
}
