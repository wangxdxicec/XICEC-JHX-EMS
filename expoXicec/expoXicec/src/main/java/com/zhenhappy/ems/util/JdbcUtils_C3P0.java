package com.zhenhappy.ems.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zhenhappy.ems.entity.TExpoXicecExhibitorInfo;
import org.apache.commons.lang.StringUtils;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxd on 2017/1/24.
 */
public class JdbcUtils_C3P0 {
    public void executeDataBaseByType(String driverClass, String jdbcUrl, String username, String password, String table, int type){
        ComboPooledDataSource ds = new ComboPooledDataSource();
        try {
            ds.setDriverClass(driverClass);
            ds.setJdbcUrl(jdbcUrl);
            ds.setUser(username);
            ds.setPassword(password);
            ds.setMaxPoolSize(400);
            ds.setInitialPoolSize(50);
            ds.setMaxIdleTime(2000);

            Connection cn = ds.getConnection();
            Statement st = cn.createStatement();

            List<TExpoXicecExhibitorInfo> tExpoXicecExhibitorInfoList = new ArrayList<TExpoXicecExhibitorInfo>();
            //先获取t_exhibitor表里所需的字段值
            String sql = "select * from " + table + " where is_logout=0";
            ResultSet exhibitor = st.executeQuery(sql);
            while(exhibitor.next()) {
                String eidValue = exhibitor.getString(1);
                TExpoXicecExhibitorInfo tExpoXicecExhibitorInfo = new TExpoXicecExhibitorInfo();
                tExpoXicecExhibitorInfo.setEid(Integer.parseInt(eidValue));
                tExpoXicecExhibitorInfo.setUsername(exhibitor.getString(2));
                tExpoXicecExhibitorInfo.setPassword(exhibitor.getString(3));
                tExpoXicecExhibitorInfo.setCountry(exhibitor.getString(19));
                if(StringUtils.isNotEmpty(exhibitor.getString(22))){
                    tExpoXicecExhibitorInfo.setExhibitor_area(exhibitor.getString(22));
                }else{
                    tExpoXicecExhibitorInfo.setExhibitor_area("0");
                }
                tExpoXicecExhibitorInfoList.add(tExpoXicecExhibitorInfo);
            }

            List<TExpoXicecExhibitorInfo> tExpoXicecExhibitorInfoListTemp = new ArrayList<TExpoXicecExhibitorInfo>();
            for(TExpoXicecExhibitorInfo tExpoXicecExhibitorInfo: tExpoXicecExhibitorInfoList){
                sql = "select * from t_exhibitor_info where eid=" + tExpoXicecExhibitorInfo.getEid();
                ResultSet exhibitorInfo = st.executeQuery(sql);
                while(exhibitorInfo.next()){
                    TExpoXicecExhibitorInfo tExpoXicecExhibitorInfoTemp = new TExpoXicecExhibitorInfo();
                    tExpoXicecExhibitorInfoTemp.setEid(tExpoXicecExhibitorInfo.getEid());
                    tExpoXicecExhibitorInfoTemp.setUsername(tExpoXicecExhibitorInfo.getUsername());
                    tExpoXicecExhibitorInfoTemp.setPassword(tExpoXicecExhibitorInfo.getPassword());
                    tExpoXicecExhibitorInfoTemp.setExhibitor_area(tExpoXicecExhibitorInfo.getExhibitor_area());
                    if(tExpoXicecExhibitorInfo.getCountry() == null || (StringUtils.isNotEmpty(tExpoXicecExhibitorInfo.getCountry()) && tExpoXicecExhibitorInfo.getCountry().equalsIgnoreCase("44"))){
                        tExpoXicecExhibitorInfoTemp.setCompany(exhibitorInfo.getString(4));
                    }else{
                        tExpoXicecExhibitorInfoTemp.setCompany(exhibitorInfo.getString(5));
                    }
                    tExpoXicecExhibitorInfoListTemp.add(tExpoXicecExhibitorInfoTemp);
                }
            }

            /*cn = ds.getConnection();
            st = cn.createStatement();*/
            List<TExpoXicecExhibitorInfo> tExpoXicecExhibitorInfoListTemp1 = new ArrayList<TExpoXicecExhibitorInfo>();
            for(TExpoXicecExhibitorInfo tExpoXicecExhibitorInfo: tExpoXicecExhibitorInfoListTemp){
                sql = "select * from t_exhibitor_booth where eid=" + tExpoXicecExhibitorInfo.getEid();
                ResultSet boothInfo = st.executeQuery(sql);
                while(boothInfo.next()){
                    TExpoXicecExhibitorInfo tExpoXicecExhibitorInfoTemp = new TExpoXicecExhibitorInfo();
                    tExpoXicecExhibitorInfoTemp.setEid(tExpoXicecExhibitorInfo.getEid());
                    tExpoXicecExhibitorInfoTemp.setUsername(tExpoXicecExhibitorInfo.getUsername());
                    tExpoXicecExhibitorInfoTemp.setPassword(tExpoXicecExhibitorInfo.getPassword());
                    tExpoXicecExhibitorInfoTemp.setExhibitor_area(tExpoXicecExhibitorInfo.getExhibitor_area());
                    tExpoXicecExhibitorInfoTemp.setCompany(tExpoXicecExhibitorInfo.getCompany());
                    tExpoXicecExhibitorInfo.setBooth_number(boothInfo.getString(3));
                }
                tExpoXicecExhibitorInfoListTemp1.add(tExpoXicecExhibitorInfo);
            }

            for(TExpoXicecExhibitorInfo tExpoXicecExhibitorInfo: tExpoXicecExhibitorInfoListTemp1){
                System.out.println(tExpoXicecExhibitorInfo.getBooth_number() + " " + tExpoXicecExhibitorInfo.getCompany() + "(" + tExpoXicecExhibitorInfo.getExhibitor_area() +"㎡)");
            }
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
