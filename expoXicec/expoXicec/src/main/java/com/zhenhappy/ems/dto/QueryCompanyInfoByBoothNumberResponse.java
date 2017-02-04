package com.zhenhappy.ems.dto;

import com.zhenhappy.util.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxd on 2017-01-18.
 */
public class QueryCompanyInfoByBoothNumberResponse extends Page{

    private List<ExpoCompanyInfo> expoCompanyInfoArrayList= new ArrayList<ExpoCompanyInfo>();

    public List<ExpoCompanyInfo> getExpoCompanyInfoArrayList() {
        return expoCompanyInfoArrayList;
    }

    public void setExpoCompanyInfoArrayList(List<ExpoCompanyInfo> expoCompanyInfoArrayList) {
        this.expoCompanyInfoArrayList = expoCompanyInfoArrayList;
    }
}
