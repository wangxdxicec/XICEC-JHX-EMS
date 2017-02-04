package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.dao.CustomerInfoDao;
import com.zhenhappy.ems.dao.ProductDao;
import com.zhenhappy.ems.dao.ProductImageDao;
import com.zhenhappy.ems.dao.ProductTypeDao;
import com.zhenhappy.ems.entity.*;
import com.zhenhappy.ems.manager.dao.QrCodeScanDao;
import com.zhenhappy.ems.manager.dto.QueryProductsRequest;
import com.zhenhappy.ems.manager.dto.QueryProductsResponse;
import com.zhenhappy.ems.manager.entity.QRCodeScanInfo;
import com.zhenhappy.ems.service.ProductService;
import com.zhenhappy.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wangxd on 2016-04-19.
 */
@Service
public class CodeScanManagerService extends ProductService {
    @Autowired
    private QrCodeScanDao qrCodeScanDao;
	/**
	 * @return
	 */
	@Transactional
    public QRCodeScanInfo loadPhoneByCheckNo(String value) {
		List<QRCodeScanInfo> groups = getHibernateTemplate().find("from QRCodeScanInfo where phonenumber=?", new Object[]{value});
        if (groups != null && groups.size()>0){
            return groups.get(0);
        } else{
            return null;
        }
    }

    /**
     * @return
     */
    @Transactional
    public List<QRCodeScanInfo> loadAllPhoneByPhone() {
        List<QRCodeScanInfo> groups = qrCodeScanDao.queryByHql("from QRCodeScanInfo", new Object[]{});
        return groups.size() > 0 ? groups : null;
    }

    @Transactional
    public TVisitorInfo loadVisitorByCheckNo(String phone) {
        List<TVisitorInfo> groups = getHibernateTemplate().find("from TVisitorInfo where CheckingNo=?", new Object[]{phone});
        if (groups != null && groups.size()>0){
            return groups.get(0);
        } else{
            return null;
        }
    }

    @Transactional
    public TVisitorInfo loadVisitorByPhone(String phone) {
        List<TVisitorInfo> groups = getHibernateTemplate().find("from TVisitorInfo where Mobile=?", new Object[]{phone});
        if (groups != null && groups.size()>0){
            return groups.get(0);
        } else{
            return null;
        }
    }

    public boolean isExiistVisitor(String value) {
        TVisitorInfo wCustomer1 = loadVisitorByCheckNo(value);
        TVisitorInfo wCustomer2 = loadVisitorByPhone(value);
        if(wCustomer1 != null || wCustomer2 != null){
            return true;
        }else {
            return false;
        }
    }

	/**
	 * 添加手机
	 * @param phone
	 */
    @Transactional
    public void addPhone(QRCodeScanInfo phone){
        getHibernateTemplate().save(phone);
    }
    
    @Transactional
    public void modifyPhone(QRCodeScanInfo phone){
        getHibernateTemplate().update(phone);
    }

}
