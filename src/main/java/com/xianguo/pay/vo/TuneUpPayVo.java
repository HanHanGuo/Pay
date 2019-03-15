package com.xianguo.pay.vo;

import lombok.Data;

/**
 * 前端调起支付vo
 * @author:鲜果
 * @date:2018年8月20日
 */
@Data
public class TuneUpPayVo {
	private String appid;
	private String partnerid;//商户号
	private String prepayid;
	private String noncestr;
	private String timestamp;
	private String sign;
}
