package com.xianguo.pay.entity;

import lombok.Data;

@Data
public class PayRequest {
	/**
	 * 支付金额
	 */
	private String amount;
	/**
	 * 商品详情
	 */
	private String detail;
	/**
	 * 错误信息(字段为空或者为null即为无错误，否则中止支付并返回错误信息给前台)
	 */
	private String errorMsg;
}


