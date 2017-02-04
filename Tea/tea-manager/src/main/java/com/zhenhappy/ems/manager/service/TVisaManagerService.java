package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.dao.TVisaDao;
import com.zhenhappy.ems.entity.TVisa;
import com.zhenhappy.ems.manager.dto.QueryTVisaRequest;
import com.zhenhappy.ems.manager.dto.QueryTVisaResponse;
import com.zhenhappy.ems.service.VisaService;
import com.zhenhappy.util.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujianbin on 2014-11-26.
 */
@Service
public class TVisaManagerService extends VisaService {
	@Autowired
	private TVisaDao tVisaDao;

	@Transactional
	public QueryTVisaResponse queryTVisaByPage(QueryTVisaRequest request) throws UnsupportedEncodingException {
		List<String> conditions = new ArrayList<String>();
		if (StringUtils.isNotEmpty(request.getPassportName())) {
			conditions.add(" (passportName like '%" + request.getPassportName() + "%' OR passportName like '%" + new String(request.getPassportName().getBytes("ISO-8859-1"),"GBK") + "%' OR passportName like '%" + new String(request.getPassportName().getBytes("ISO-8859-1"),"utf-8") + "%') ");
		}
		if (StringUtils.isNotEmpty(request.getNationality())) {
			conditions.add(" (nationality like '%" + request.getNationality() + "%' OR nationality like '%" + new String(request.getNationality().getBytes("ISO-8859-1"),"GBK") + "%' OR nationality like '%" + new String(request.getNationality().getBytes("ISO-8859-1"),"utf-8") + "%') ");
		}
		if (StringUtils.isNotEmpty(request.getCompanyName())) {
			conditions.add(" (companyName like '%" + request.getCompanyName() + "%' OR companyName like '%" + new String(request.getCompanyName().getBytes("ISO-8859-1"),"GBK") + "%' OR companyName like '%" + new String(request.getCompanyName().getBytes("ISO-8859-1"),"utf-8") + "%') ");
		}
		if (request.getUpdateTime() != null) {
			conditions.add(" (updateTime like '%" + request.getUpdateTime() + "%");
		}
		if (request.getCreateTime() != null) {
			conditions.add(" (createTime like '%" + request.getCreateTime() + "%");
		}
		conditions.add(" (passportNo != '') ");
		String conditionsSql = StringUtils.join(conditions, " and ");
		String conditionsSqlNoOrder = "";
		if(StringUtils.isNotEmpty(conditionsSql)){
			conditionsSqlNoOrder = " where " + conditionsSql;
		}

	    Page page = new Page();
        page.setPageSize(request.getRows());
        page.setPageIndex(request.getPage());
        List<TVisa> wVisas = tVisaDao.queryPageByHQL("select count(*) from TVisa "  + conditionsSqlNoOrder, "from TVisa "  + conditionsSqlNoOrder + " order by updateTime DESC, id DESC", new Object[]{}, page);
		QueryTVisaResponse response = new QueryTVisaResponse();
        response.setResultCode(0);
        response.setRows(wVisas);
        response.setTotal(page.getTotalCount());
        return response;
	}

	/**
	 * 查询所有VISA
	 * @return
	 */
	@Transactional
	public List<TVisa> loadAllTVisas() {
		List<TVisa> tVisas = tVisaDao.queryByHql("from TVisa where passportNo != '' order by updateTime DESC, id DESC", new Object[]{ });
		return tVisas;
	}

	/**
	 * 根据vids查询展商列表
	 * @return
	 */
	@Transactional
	public List<TVisa> loadSelectedTVisas(Integer[] vids) {
		List<TVisa> tVisas = null;
		tVisas = tVisaDao.loadTVisasByVids(vids);
		return tVisas.size() > 0 ? tVisas : null;
	}

	/**
	 * 根据EID查询对应的VISA列表
	 * @return
	 */
	@Transactional
	public TVisa loadTVisaListByJoinerid(Integer joinerId) {
		List<TVisa> tVisas = tVisaDao.queryByHql("from TVisa where passportNo != '' and joinerId=? ", new Object[]{joinerId});
		return tVisas.size()>0?tVisas.get(0):null;
	}
}
