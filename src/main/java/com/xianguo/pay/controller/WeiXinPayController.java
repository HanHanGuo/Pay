package com.xianguo.pay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.xianguo.pay.PaymentManager;
import com.xianguo.pay.entity.PayLog;
import com.xianguo.pay.entity.PayModel;
import com.xianguo.pay.entity.PayModel.PayType;
import com.xianguo.pay.service.PayLogService;
import com.xianguo.pay.util.WXPayUtil;
import com.xianguo.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class WeiXinPayController {

	@Autowired
	private PayLogService payLogService;

	@Autowired
	private BeanFactory beanFactory;
	
	@Autowired
	private WXPayUtil wxPayUtil;
	/**
	 * 微信支付回调通知
	 * 作者:鲜果
	 * 日期:2018年7月12日
	 * @param request
	 * @param response
	 * @return
	 * String
	 */
	@SuppressWarnings("finally")
	@RequestMapping(value = "/paymentNotice", produces = "text/html;charset=utf-8")
	@Transactional
	public String paymentNotice(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> return_data = new HashMap<String, Object>();
		log.info("orderNotify--支付宝回调BEGIN");

		return_data.put("return_code", "FAIL");
		return_data.put("return_msg", "服务器错误");
		try {
			log.info("paymentNotice----微信支付回调BGIN");
			ServletInputStream instream = request.getInputStream();
			StringBuffer sb = new StringBuffer();
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len = instream.read(buffer)) != -1) {
				sb.append(new String(buffer, 0, len));
			}
			instream.close();
			Map<String, Object> map = wxPayUtil.xmlToMap(sb.toString());// 接受微信的回调的通知参数
			log.info("orderNotify--订单号:"+map.get("out_trade_no").toString());
			// 判断签名是否正确
			if (wxPayUtil.isSignatureValid(map)) {
				if (map.get("return_code").toString().equals("FAIL")) {
					return_data.put("return_code", "FAIL");
					return_data.put("return_msg", map.get("return_msg"));
					
				} else if (map.get("return_code").toString().equals("SUCCESS")) {

					// 获取状态码
					String result_code = map.get("result_code").toString();
					String out_trade_no = map.get("out_trade_no").toString();
					
					// 操作序列化订单操作对象
					String attach = map.get("attach").toString();
					Map<String, Object> attachMap = (Map<String, Object>) JSON.parse(attach);
					String className = attachMap.get("ClassName").toString();
					Class<?> clases = Class.forName(className);
					Object obj = beanFactory.getBean(clases);

					// 查询订单日志
					PayLog payLog = payLogService.selectById(out_trade_no);
					if (payLog == null) {
						return_data.put("return_code", "FAIL");
						return_data.put("return_msg", "订单不存在");
						log.error("paymentNotice----订单不存在");
						throw new RuntimeException();
					} else {
						// 2 已支付（不确定是否支付成功）3 支付完成 4 取消支付 5支付失败
						if (result_code.equals("SUCCESS")) {// 支付成功
							// 如果订单已经支付直接返回成功
							if ("1".equals(payLog.getPayState())) {
								return_data.put("return_code", "SUCCESS");
								return_data.put("return_msg", "OK");
								throw new RuntimeException();
							} else {
								String total_fee = map.get("total_fee").toString();// 订单金额
								if (!payLog.getPayMoney().equals(total_fee)) {// 订单金额是否一致
									return_data.put("return_code", "FAIL");
									return_data.put("return_msg", "金额异常");
									log.error("paymentNotice----金额异常");
									throw new RuntimeException();
								} else {
									payLog.setNotifyTime(DateUtils.formatNowDate());
									payLog.setNotifyContent(sb.toString());
									payLog.setPayState("1");
									int result = payLogService.update(payLog);
									if (result <= 0) {
										return_data.put("return_code", "FAIL");
										return_data.put("return_msg", "更新支付日志失败");
										log.error("paymentNotice----更新支付日志失败");
										throw new RuntimeException();
									} else {
										// 处理支付成功的订单信息
										if (obj instanceof PaymentManager) {
											PayModel payModel = new PayModel();
											payModel.setOrderId(attachMap.get("id").toString());
											payModel.setPayType(PayType.WEIXIN_PAY);
											boolean istrue = ((PaymentManager) obj).paymentSuccess(payModel);
											if (istrue) {
												return_data.put("return_code", "SUCCESS");
												return_data.put("return_msg", "OK");
											} else {
												return_data.put("return_code", "FAIL");
												return_data.put("return_msg", "订单更新操作失败");
												log.error("paymentNotice----订单更新操作失败");
												throw new RuntimeException();
											}
										} else {
											return_data.put("return_code", "FAIL");
											return_data.put("return_msg", "attach数据包ClassName字段传输有误");
											log.error("paymentNotice----attach数据包ClassName字段传输有误");
											throw new RuntimeException();
										}

									}
								}
							}
						} else {// 支付失败，更新支付结果

							if (payLog != null) {
								payLog.setPayState("2");
								payLog.setNotifyTime(DateUtils.formatNowDate());
								payLog.setNotifyContent(sb.toString());
								payLogService.update(payLog);
							}
							if (obj instanceof PaymentManager) {
								PayModel payModel = new PayModel();
								payModel.setOrderId(attachMap.get("id").toString());
								payModel.setPayType(PayType.WEIXIN_PAY);
								((PaymentManager) obj).paymentError(payModel);
							} else {
								return_data.put("return_code", "FAIL");
								return_data.put("return_msg", "attach数据包ClassName字段传输有误");
								log.error("paymentNotice----attach数据包ClassName字段传输有误");
								throw new RuntimeException();
							}
							return_data.put("return_code", "SUCCESS");
							return_data.put("return_msg", "OK");
						}

						
					}
				}
			} else {
				return_data.put("return_code", "FAIL");
				return_data.put("return_msg", "签名错误");
				log.error("paymentNotice----签名错误");
				throw new RuntimeException();
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new RuntimeException();
		} finally {
			String xml = wxPayUtil.GetMapToXML(return_data);
			log.info("paymentNotice----支付通知回调结果：" + xml);
			log.info("paymentNotice----微信支付回调END");
			return xml;
		}
	}
	
}
