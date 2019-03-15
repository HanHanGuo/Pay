package com.xianguo.pay.entity;

import lombok.Data;

/**
 * 微信退款实体
 * @author:鲜果
 * @date:2018年9月4日
 */
@Data
public class WeiXinRefundRequest {
	private String appid; //公众账号ID		是	String(32)	wx8888888888888888	微信分配的公众账号ID
	private String mch_id; //商户号		是	String(32)	1900000109	微信支付分配的商户号
	private String sub_appid; //子商户公众账号ID		否	String(32)	wx8888888888888888	微信分配的子商户公众账号ID
	private String sub_mch_id; //子商户号		是	String(32)	1900000109	微信支付分配的子商户号
	private String nonce_str; //随机字符串		是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位。推荐随机数生成算法
	private String sign; //签名		是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法
	private String transaction_id; //微信订单号		是	String(32)	1217752501201407033233368018	微信订单号
	private String out_trade_no; //商户订单号		是	String(32)	1217752501201407033233368018    商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。  transaction_id、out_trade_no二选一，如果同时存在优先级：transaction_id> out_trade_no
	private String out_refund_no; //商户退款单号		是	String(64)	1217752501201407033233368018	商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
	private String total_fee; //订单金额		是	Int	100	订单总金额，单位为分，只能为整数，详见支付金额
	private String refund_fee; //申请退款金额		是	Int	100	退款总金额，单位为分，只能为整数，可部分退款。详见支付金额
	private String refund_fee_type; //退款货币种类		否	String(8)	CNY	货币类型，需与支付一致，或者不填。符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
	private String refund_desc; //退款原因		否	String(80)	商品已售完	若商户传入，会在下发给用户的退款消息中体现退款原因
	private String refund_account; //退款资金来源		否	String(30)	REFUND_SOURCE_RECHARGE_FUNDS	仅针对老资金流商户使用 REFUND_SOURCE_UNSETTLED_FUNDS---未结算资金退款（默认使用未结算资金退款） REFUND_SOURCE_RECHARGE_FUNDS---可用余额退款
	private String notify_url; //退款结果通知url		否	String(256)	https://weixin.qq.com/notify/	 异步接收微信支付退款结果通知的回调地址，通知URL必须为外网可访问的url，不允许带参数 如果参数中传了notify_url，则商户平台上配置的回调地址将不会生效。
}
