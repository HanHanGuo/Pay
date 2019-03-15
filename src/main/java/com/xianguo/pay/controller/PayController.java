package com.xianguo.pay.controller;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.xianguo.pay.BalancePay;
import com.xianguo.pay.PaymentManager;
import com.xianguo.pay.entity.PayLog;
import com.xianguo.pay.entity.PayModel;
import com.xianguo.pay.entity.PayModel.PayType;
import com.xianguo.pay.entity.PayRequest;
import com.xianguo.pay.entity.WeiXinOrderRequest;
import com.xianguo.pay.entity.WeiXinOrderRespose;
import com.xianguo.pay.service.PayLogService;
import com.xianguo.pay.util.AlipayUtil;
import com.xianguo.pay.util.PayUtil;
import com.xianguo.pay.util.WXPayUtil;
import com.xianguo.pay.util.WXSignUtils;
import com.xianguo.pay.vo.TuneUpPayVo;
import com.xianguo.util.DateUtils;
import com.xianguo.util.GUID;
import com.xianguo.util.JsonResult;
import com.xianguo.util.RandomUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RestController
public class PayController {

	@Autowired
	private PayLogService payLogService;
	
	@Autowired
	private BalancePay balancePay;
	
	@Autowired
	private AlipayUtil alipayUtil;
	
	@Autowired
	private WXPayUtil wxPayUtil;
	/**
	 * 请求支付
	 * @author:鲜果
	 * @date:2019年1月7日
	 * @param orderId  请求支付的订单id
	 * @param orderNo  请求支付的订单编号
	 * @param paySign  订单类型（就是打上PaySign注解的值）
	 * @param payType  支付类型（支付类型：0-支付宝/1-微信/2-余额支付/）
	 * @param memberId 支付人员id
	 * @return JsonResult
	 */
	@PostMapping("/TuneUpPay")
	public JsonResult TuneUpPay(String orderId, String orderNo, String paySign, String payType, String memberId,HttpServletRequest httpServletRequest) {
		try {
			log.info("调起支付 ---orderId-"+orderId+"-orderNo--"+orderNo+"--paySign-"+paySign+"-payType--"+memberId+"--BEGIN");
			PaymentManager pm = PayUtil.getPayService(paySign) == null ? null : (PaymentManager) PayUtil.getPayService(paySign);
			if (pm == null) {
				log.error("TuneUpPay---PaySign错误");
				return JsonResult.error("PaySign不存在");
			}
			PayModel tuneUpPay = new PayModel();
			tuneUpPay.setOrderId(orderId);
			PayType ePayType = PayType.get(payType);
			if (ePayType == null) {
				log.error("TuneUpPay---支付类型错误");
				return JsonResult.error("payType不存在");
			}
			tuneUpPay.setPayType(ePayType);
			PayRequest payRequest = pm.tuneUpPay(tuneUpPay);
			if (payRequest == null) {
				log.error("TuneUpPay---返回为NULL");
				return JsonResult.error("tuneUpPay返回为null");
			}
			if(!StringUtils.isEmpty(payRequest.getErrorMsg())) {
				log.error("TuneUpPay---"+payRequest.getErrorMsg());
				return JsonResult.error(payRequest.getErrorMsg());
			}
			PayLog payLog = new PayLog();
			payLog.setId(GUID.newGUID());
			payLog.setPayTime(DateUtils.formatNowDate());
			payLog.setPayState("0");
			payLog.setPayType(payType);
			payLog.setSyncState("0");
			payLog.setOrderId(orderId);
			payLog.setOrderNo(orderNo);
			payLog.setPayMoney(payRequest.getAmount());
			payLog.setMemberId(memberId);
			payLogService.save(payLog);
			switch (ePayType) {
			case ALI_PAY:
				AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
				model.setSubject(payRequest.getDetail());
				model.setOutTradeNo(payLog.getId());
				model.setTotalAmount(new BigDecimal(payRequest.getAmount()).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				model.setProductCode("QUICK_MSECURITY_PAY");
				Map<String, String> params = new HashMap<String, String>();
				params.put("ClassName", PayUtil.getPayService(paySign).getClass().getName());// 支付成功处理类地址 此类必须接入PaymentManager接口
				params.put("id", orderId);// Data数据包传入数据会原样返回
				model.setPassbackParams(URLEncoder.encode(JSON.toJSONString(params),"UTF-8"));
				AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
				request.setBizModel(model);
				request.setNotifyUrl(alipayUtil.notifyUrl);
				AlipayTradeAppPayResponse response = alipayUtil.getAlipayClient().sdkExecute(request);
		        return JsonResult.success(response.getBody());
			case WEIXIN_PAY:// 微信支付
				WeiXinOrderRequest weixinPay = new WeiXinOrderRequest();// 支付参数设置
				weixinPay.setSpbill_create_ip(wxPayUtil.getIpAddress(httpServletRequest));
				weixinPay.setBody(payRequest.getDetail());
				weixinPay.setTotal_fee(payRequest.getAmount());
				weixinPay.setOut_trade_no(payLog.getId());
				Map<String, String> attach = new HashMap<String, String>();
				attach.put("ClassName", PayUtil.getPayService(paySign).getClass().getName());// 支付成功处理类地址 此类必须接入PaymentManager接口
				attach.put("id", orderId);// Data数据包传入数据会原样返回
				weixinPay.setAttach(JSONObject.toJSONString(attach));
				WeiXinOrderRespose weiXinOrderRespose = wxPayUtil.PlaceanOrder(weixinPay);
				if (weiXinOrderRespose == null) {
					log.error("TuneUpPay---微信下单失败");
					return JsonResult.error("微信下单失败");
				} else {
					TuneUpPayVo tuneUpPayVo = new TuneUpPayVo();
					tuneUpPayVo.setAppid(weiXinOrderRespose.getAppid());
					tuneUpPayVo.setPartnerid(weiXinOrderRespose.getMch_id());
					tuneUpPayVo.setPrepayid(weiXinOrderRespose.getPrepay_id());
					tuneUpPayVo.setNoncestr(weiXinOrderRespose.getNonce_str());
					tuneUpPayVo.setTimestamp(String.valueOf(new Date().getTime() / 1000));

					@SuppressWarnings("unchecked")
					Map<String, Object> map = (Map) JSON.parse(JSONObject.toJSONString(tuneUpPayVo));
					map.put("package", "Sign=WXPay");
					SortedMap<String, Object> sort = new TreeMap<String, Object>(map);
					sort.put("sign", WXSignUtils.createSign("UTF-8", sort, wxPayUtil.WEIXIN_KEY));
					return JsonResult.success(sort);
				}
			case BALANCE_PAY:
				payLog.setPaySerialCode(DateUtils.formatNowDate()+RandomUtil.getRandomString(5));
				payLogService.update(payLog);
				String msg = balancePay.BalancePay(payRequest.getAmount(), memberId);
				if(!StringUtils.isEmpty(msg)) {
					log.error("TuneUpPay---msg");
					return JsonResult.error("msg");
				}else {
					payLog.setPayState("1");
					payLogService.update(payLog);
					return JsonResult.success();
				}
			default:
				break;
			}
		} catch (Exception e) {
			log.error("TuneUpPay---"+e.getMessage(),e);
			return JsonResult.error("系统内部错误");
		} finally {
			log.info("调起支付END");
		}
		return JsonResult.success();
	}
}
