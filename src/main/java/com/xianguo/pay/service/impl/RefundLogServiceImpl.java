package com.xianguo.pay.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xianguo.hotmapper.service.impl.HotServiceImpl;
import com.xianguo.pay.dao.RefundLogDao;
import com.xianguo.pay.entity.RefundLog;
import com.xianguo.pay.service.RefundLogService;

/**
 * 退款日志service
 * @author:鲜果
 * @date:2018年9月4日
 */
@Service
public class RefundLogServiceImpl extends HotServiceImpl<RefundLog, RefundLogDao> implements RefundLogService {
	
	@Autowired
	private RefundLogDao refundLogDao;
	
	@Override
	public RefundLogDao getDao() {
		return refundLogDao;
	}

}
