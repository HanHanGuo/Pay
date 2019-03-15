package com.xianguo.pay;

public interface BalancePay {
	
	/**
	 * 余额支付接口
	 * @param amount 支付金额
	 * @param memberId 支付用户id
	 * @return 返回null以及“”为支付成功，返回其他为支付失败并返回前台提示
	 */
	public String BalancePay(String amount,String memberId);
	
}
