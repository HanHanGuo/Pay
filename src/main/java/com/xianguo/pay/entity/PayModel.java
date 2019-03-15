package com.xianguo.pay.entity;

import lombok.Data;

@Data
public class PayModel {
	/**
	 * 支付类型
	 */
	private PayType payType;

	/**
	 * 订单Id
	 */
	private String orderId;

	/**
	 * 支付类型
	 * 
	 * @author:鲜果
	 * @date:2019年1月4日
	 */
	public enum PayType {
		ALI_PAY("0"), // 支付宝
		WEIXIN_PAY("1"), // 微信
		BALANCE_PAY("2"); // 余额
		private final String value;

		private PayType(String v) {
			this.value = v;
		}

		public String toString() {
			return this.value;
		}

		public static PayType get(String str) {
			for (PayType e : values()) {
				if (e.toString().equals(str)) {
					return e;
				}
			}
			return null;
		}
	}
}
