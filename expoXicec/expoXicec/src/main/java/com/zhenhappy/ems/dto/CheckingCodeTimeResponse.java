package com.zhenhappy.ems.dto;

/**
 * query customers by page.
 * <p/>
 * Created by wangxd on 2016-05-24.
 */
public class CheckingCodeTimeResponse extends BaseResponse {
    private Long codeTime;

    public Long getCodeTime() {
        return codeTime;
    }

    public void setCodeTime(Long codeTime) {
        this.codeTime = codeTime;
    }
}
