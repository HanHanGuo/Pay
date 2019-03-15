package com.xianguo.pay.controller;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alipay.api.internal.util.AlipaySignature;
import com.xianguo.pay.PaymentManager;
import com.xianguo.pay.entity.PayLog;
import com.xianguo.pay.entity.PayModel;
import com.xianguo.pay.entity.PayModel.PayType;
import com.xianguo.pay.service.PayLogService;
import com.xianguo.pay.util.AlipayUtil;
import com.xianguo.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class AliPayController {

    @Autowired
	private PayLogService payLogService;
    
	@Autowired
	private BeanFactory beanFactory;
	
	@Autowired
	private AlipayUtil alipayUtil;
	/**
	 * 支付宝交易成功后，调用的回调方法
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/orderNotify")
	@Transactional
	public String orderNotify(HttpServletRequest request) {
		try {
			log.info("orderNotify--支付宝回调BEGIN");
			Map<String, String> params = new HashMap<String, String>();
			Map<String, String[]> requestParams = request.getParameterMap();
			for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
				}
				// 乱码解决，这段代码在出现乱码时使用。
				//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				params.put(name, valueStr);
			}
			log.info("orderNotify--订单号:"+params.get("out_trade_no"));

			boolean flag = AlipaySignature.rsaCheckV1(params, alipayUtil.alipayPublicKey, "UTF-8","RSA2");
			if(flag) {
				// 操作序列化订单操作对象
				String attach = URLDecoder.decode(params.get("passback_params"),"UTF-8");
				Map<String, Object> attachMap = (Map<String, Object>) JSON.parse(attach);
				String className = attachMap.get("ClassName").toString();
				Class<?> clases = Class.forName(className);
				Object obj = beanFactory.getBean(clases);
				
				// 查询订单日志
				PayLog payLog = payLogService.selectById(params.get("out_trade_no"));
				if (payLog == null) {
					log.error("orderNotify---订单不存在");
					throw new Exception("orderNotify---订单不存在");
				} else {
					// 如果订单已经支付直接返回成功
					if ("1".equals(payLog.getPayState())) {
						log.info("orderNotify---订单已支付");
						return "success";
					} else {
						String payMoney = new BigDecimal(payLog.getPayMoney()).divide(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
						if(!payMoney.equals(params.get("total_amount"))) {
							log.error("orderNotify---金额异常");
							throw new Exception("orderNotify---金额异常");
						} else {
							payLog.setNotifyTime(DateUtils.formatNowDate());
							payLog.setNotifyContent(JSON.toJSONString(params));
							payLog.setPayState("1");
							int result = payLogService.update(payLog);
							if (result <= 0) {
								log.error("orderNotify---更新支付日志失败");
								throw new RuntimeException("orderNotify---更新支付日志失败");
							} else {
								// 处理支付成功的订单信息
								if (obj instanceof PaymentManager) {
									PayModel payModel = new PayModel();
									payModel.setOrderId(attachMap.get("id").toString());
									payModel.setPayType(PayType.ALI_PAY);
									boolean istrue = ((PaymentManager) obj).paymentSuccess(payModel);
									if (istrue) {
										return "success";
									} else {
										log.error("orderNotify---订单更新操作失败");
										throw new RuntimeException("orderNotify---订单更新操作失败");
									}
								} else {
									log.error("orderNotify---attach数据包ClassName字段传输有误");
									throw new RuntimeException("orderNotify---attach数据包ClassName字段传输有误");
								}

							}
						}
					}
				}
			}else {
				log.info("orderNotify---支付宝回调签名不正确");
				return "SignError";
			}
		} catch (Exception e) {
			log.error("orderNotify---支付宝post回调方法执行失败!"+ e.getMessage(),e);
		} finally {
			log.error("orderNotify--支付宝回调END");
		}
		return "";
	}
}
