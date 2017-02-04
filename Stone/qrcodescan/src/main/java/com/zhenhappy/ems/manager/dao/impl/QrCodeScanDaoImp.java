package com.zhenhappy.ems.manager.dao.impl;

import com.zhenhappy.ems.dao.imp.BaseDaoHibernateImp;
import com.zhenhappy.ems.manager.dao.QrCodeScanDao;
import com.zhenhappy.ems.manager.entity.QRCodeScanInfo;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2016/4/19.
 */
@Repository
public class QrCodeScanDaoImp extends BaseDaoHibernateImp<QRCodeScanInfo> implements QrCodeScanDao {
}
