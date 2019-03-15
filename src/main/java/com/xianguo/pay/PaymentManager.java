package com.xianguo.pay;

import com.xianguo.pay.entity.PayModel;
import com.xianguo.pay.entity.PayRequest;

public interface PaymentManager {
	
	/**
	 * 支付成功的操作
	 * 作者:鲜果
	 * 日期:2018年7月10日
	 * @param content
	 * @return
	 * boolean
	 */
	public boolean paymentSuccess(PayModel payModel)throws Exception;
	
	/**
	 * 支付失败的操作
	 * 作者:鲜果
	 * 日期:2018年7月10日
	 * @param content
	 * @return
	 * boolean
	 */
	public boolean paymentError(PayModel payModel)throws Exception;
	
	/**
	 * 调起支付
	 * @author:鲜果
	 * @date:2019年1月7日
	 * @param tuneUpPay
	 * @return
	 * @throws Exception
	 * PayRequest
	 */
	public PayRequest tuneUpPay(PayModel tuneUpPay)throws Exception;
}
