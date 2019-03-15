package com.xianguo.pay.entity;

import lombok.Data;

/**
 * 统一下单返回参数
 * 作者:鲜果
 * 日期:2018年7月9日
 */
@Data
public class WeiXinOrderRespose {
    private String return_code;             //返回状态码
    private String return_msg;              //返回信息
    private String appid;                   //应用ID
    private String mch_id;                  //商户号
    private String device_info;             //设备号
    private String nonce_str;               //随机字符串
    private String sign;                    //签名
    private String result_code;             //业务结果
    private String err_code;                //错误代码
    private String err_code_des;            //错误代码描述
    private String trade_type;              //交易类型
    private String prepay_id;               //预支付交易会话标识
    private String code_url;				//二维码路径
    private String timestamp;				//时间戳
}