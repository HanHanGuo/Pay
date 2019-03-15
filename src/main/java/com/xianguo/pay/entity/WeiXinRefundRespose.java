package com.xianguo.pay.entity;

import lombok.Data;

/**
 * 微信退款提交实体
 * @author:鲜果
 * @date:2018年9月4日
 */
@Data
public class WeiXinRefundRespose {
	private String return_code; //返回状态码		是	String(16)	SUCCESS	SUCCESS/FAIL
	private String return_msg; //返回信息		否	String(128)	签名失败	返回信息，如非空，为错误原因  签名失败  参数格式校验错误
	private String result_code; //业务结果		是	String(16)	SUCCESS	SUCCESS/FAIL  SUCCESS退款申请接收成功，结果通过退款查询接口查询   FAIL 提交业务失败
	private String err_code; //错误代码		否	String(32)	SYSTEMERROR	列表详见第6节
	private String err_code_des; //错误代码描述		否	String(128)	系统超时	结果信息描述
	private String appid; //公众账号ID		是	String(32)	wx8888888888888888	微信分配的公众账号ID
	private String mch_id; //商户号		是	String(32)	1900000109	微信支付分配的商户号
	private String sub_appid; //子商户公众账号ID		否	String(32)	wx8888888888888888	微信分配的子商户公众账号ID
	private String sub_mch_id; //子商户号		是	String(32)	1900000109	微信支付分配的子商户号
	private String nonce_str; //随机字符串		是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位
	private String sign; //签名		是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	签名，详见签名算法
	private String transaction_id; //微信订单号		是	String(32)	1217752501201407033233368018	微信订单号
	private String out_trade_no;  //商户订单号		是	String(32)	1217752501201407033233368018	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
	private String out_refund_no; //商户退款单号		是	String(64)	1217752501201407033233368018	商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
	private String refund_id; //微信退款单号		是	String(32)	1217752501201407033233368018	微信退款单号
	private String refund_fee; //申请退款金额		是	Int	100	退款总金额,单位为分,可以做部分退款
	private String settlement_refund_fee; //退款金额		否	Int	100	去掉非充值代金券退款金额后的退款金额，退款金额=申请退款金额-非充值代金券退款金额，退款金额<=申请退款金额
	private String total_fee;		// 订单金额		是	Int	100	订单总金额，单位为分，只能为整数，详见支付金额
	private String settlement_total_fee; //应结订单金额		否	Int	100	应结订单金额=订单金额-免充值代金券金额，应结订单金额<=订单金额。
	private String fee_type; //货币种类		否	String(8)	CNY	订单金额货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
	private String cash_fee; //现金支付金额		是	Int	100	现金支付金额，单位为分，只能为整数，详见支付金额
	private String cash_refund_fee; //现金退款金额		否	Int	100	现金退款金额，单位为分，只能为整数，详见支付金额
	private String coupon_refund_fee; //代金券退款总金额		否	Int	100	代金券退款金额<=退款金额，退款金额-代金券或立减优惠退款金额为现金，说明详见代金券或立减优惠
	private String coupon_refund_count; //退款代金券使用数量		否	Int	1	退款代金券使用数量
	private String coupon_type_$n; //代金券类型		否	String(8)	CASH	CASH--充值代金券 NO_CASH---非充值代金券  订单使用代金券时有返回（取值：CASH、NO_CASH）。$n为下标,从0开始编号，举例：coupon_type_0
	private String coupon_refund_id_$n; //退款代金券ID		否	String(20)	10000 	退款代金券ID, $n为下标，从0开始编号
	private String coupon_refund_fee_$n;  //单个代金券退款金额		否	Int	100	单个退款代金券支付金额, $n为下标，从0开始编号
	private String refund_channel;
}
