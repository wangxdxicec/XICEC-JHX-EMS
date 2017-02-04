package com.zhenhappy.ems.manager.service;

import com.zhenhappy.ems.manager.dao.QrCodeScanDao;
import com.zhenhappy.ems.manager.entity.QRCodeScanInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2016/4/19.
 */
public class QrCodeScanService {
    @Autowired
    private QrCodeScanDao qrCodeScanDao;

    @Transactional()
    public void add(QRCodeScanInfo qrCodeScanInfo) {
        qrCodeScanDao.create(qrCodeScanInfo);
    }
}
