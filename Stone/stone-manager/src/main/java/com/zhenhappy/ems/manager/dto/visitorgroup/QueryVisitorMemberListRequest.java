package com.zhenhappy.ems.manager.dto.visitorgroup;

import com.zhenhappy.ems.manager.dto.EasyuiRequest;

/**
 * Created by wangxd on 2016-12-26.
 */
public class QueryVisitorMemberListRequest extends EasyuiRequest {
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
