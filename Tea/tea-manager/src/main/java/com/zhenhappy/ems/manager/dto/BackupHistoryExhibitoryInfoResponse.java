package com.zhenhappy.ems.manager.dto;

import com.zhenhappy.ems.dto.BaseResponse;

/**
 * query customers by page.
 * <p/>
 * Created by wangxd on 2016-05-31.
 */
public class BackupHistoryExhibitoryInfoResponse extends BaseResponse {
    public String result;
    public String isExistData;
    public String willImportData;

    public BackupHistoryExhibitoryInfoResponse() {
    }

    public BackupHistoryExhibitoryInfoResponse(String result, String isExistData, String willImportData) {
        this.result = result;
        this.isExistData = isExistData;
        this.willImportData = willImportData;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getIsExistData() {
        return isExistData;
    }

    public void setIsExistData(String isExistData) {
        this.isExistData = isExistData;
    }

    public String getWillImportData() {
        return willImportData;
    }

    public void setWillImportData(String willImportData) {
        this.willImportData = willImportData;
    }
}
