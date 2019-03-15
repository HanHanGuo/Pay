package com.xianguo.pay.entity;

import lombok.Data;

/**
 * 统一下单亲求参数
 * 作者:鲜果
 * 日期:2018年7月9日
 */
@Data
public class WeiXinOrderRequest {
	//变量名	字段名	必填	 	类型		示例值	描述
	private String appid;//	公众账号ID   是		String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
	private String mch_id;//商户号  必填	String(32)	1230000109	微信支付分配的商户号
	private String device_info;	//设备号	否		String(32)	013467007045764	自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
	private String nonce_str;//随机字符串	是 	     String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，长度要求在32位以内。推荐随机数生成算法
	private String sign;//签名	是 	     String(32)	  C380BEC2BFD727A4B6845133519F3AD6	通过签名算法计算得出的签名值，详见签名生成算法
	private String sign_type;//签名类型	sign_type	否	String(32)	HMAC-SHA256	签名类型，默认为MD5，支持HMAC-SHA256和MD5。
	private String body;//商品描述	body 是	 String(128)	腾讯充值中心-QQ会员充值  商品简单描述，该字段请按照规范传递，具体请见参数规定	
	private String detail;//商品详情	detail	否	String(6000)	 	单品优惠字段(暂未上线)
	private String attach;//附加数据	attach	否	String(127)	深圳分店	附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。
	private String out_trade_no;//商户订单号	out_trade_no	是	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。详见商户订单号
	private String fee_type;//标价币种	fee_type	否	String(16)	CNY	符合ISO 4217标准的三位字母代码，默认人民币：CNY，详细列表请参见货币类型
	private String total_fee;//标价金额	total_fee	是	Int	88	订单总金额，单位为分，详见支付金额
	private String spbill_create_ip;//终端IP	spbill_create_ip	是	String(16)	123.12.12.123	APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
	private String time_start;//交易起始时间	time_start	否	String(14)	20091225091010	订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
	private String time_expire;//交易结束时间	time_expire	否	String(14)	20091227091010	订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。其他详见时间规则   注意：最短失效时间间隔必须大于5分钟
	private String goods_tag;//订单优惠标记	goods_tag	否	String(32)	WXG	订单优惠标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠
	private String notify_url;//通知地址	notify_url	是	String(256)	http://www.weixin.qq.com/wxpay/pay.php	异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
	private String trade_type;//交易类型	trade_type	是	String(16)	JSAPI	取值如下：JSAPI，NATIVE，APP等，说明详见参数规定
	private String product_id;//商品ID	product_id	否	String(32)	12235413214070356458058	trade_type=NATIVE时（即扫码支付），此参数必传。此参数为二维码中包含的商品ID，商户自行定义。
	private String limit_pay;//指定支付方式	limit_pay	否	String(32)	no_credit	上传此参数no_credit--可限制用户不能使用信用卡支付
	
}
