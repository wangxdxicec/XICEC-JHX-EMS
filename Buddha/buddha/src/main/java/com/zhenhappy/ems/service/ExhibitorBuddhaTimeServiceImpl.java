package com.zhenhappy.ems.service;

import com.zhenhappy.ems.buddhatime.TExhibitorBuddhaTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ExhibitorBuddhaTimeServiceImpl implements ExhibitorBuddhaTimeService {
    private static final Logger logger = LoggerFactory.getLogger(ExhibitorBuddhaTimeServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public TExhibitorBuddhaTime loadExhibitorTime() {
        String buddha_Fair_Show_Date = jdbcTemplate.queryForObject("select buddha_Fair_Show_Date from [t_exhibitor_buddha_time] ", new Object[]{}, String.class);
        String exhibitor_Info_Submit_Deadline = jdbcTemplate.queryForObject("select exhibitor_Info_Submit_Deadline from [t_exhibitor_buddha_time] ", new Object[]{}, String.class);
        String buddha_Fair_Show_Date_Begin = jdbcTemplate.queryForObject("select buddha_Fair_Show_Date_Begin from [t_exhibitor_buddha_time] ", new Object[]{}, String.class);

        TExhibitorBuddhaTime tExhibitorTime = new TExhibitorBuddhaTime();
        tExhibitorTime.setBuddha_Fair_Show_Date(buddha_Fair_Show_Date);
        tExhibitorTime.setExhibitor_Info_Submit_Deadline(exhibitor_Info_Submit_Deadline);
        tExhibitorTime.setBuddha_Fair_Show_Date_Begin(buddha_Fair_Show_Date_Begin);
        return tExhibitorTime;
    }
}
