package com.xianguo.pay.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xianguo.hotmapper.service.impl.HotServiceImpl;
import com.xianguo.pay.dao.PayLogDao;
import com.xianguo.pay.entity.PayLog;
import com.xianguo.pay.service.PayLogService;

/**
 * 
 * @Title: PayLogServiceImpl.java
 * @Package com.yrsoft.service.sysService.Impl
 * @Description: 支付日志Service
 * @author zhangke
 * @date 2018年7月7日 上午11:17:18
 */
@Service
public class PayLogServiceImpl extends HotServiceImpl<PayLog, PayLogDao> implements PayLogService {

	@Autowired
	private PayLogDao payLogDao;
	
	@Override
	public PayLogDao getDao() {
		return payLogDao;
	}
}
