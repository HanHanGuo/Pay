package com.xianguo.pay.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.xianguo.config.PropertiesUtil;

@Component
public class AlipayUtil {
	
	public PropertiesUtil propertiesUtil;
	
	@Autowired
	public void setPropertiesUtil(PropertiesUtil propertiesUtil) {
		this.propertiesUtil = propertiesUtil;
		init();
	}
	
	private void init() {
		appId = propertiesUtil.getResource().getString("alipay.appId");
		appPrivateKey = propertiesUtil.getResource().getString("alipay.privateKey");
		charset="UTF-8";
		alipayPublicKey = propertiesUtil.getResource().getString("alipay.publicKey");
		serverUrl = propertiesUtil.getResource().getString("alipay.serverUrl");
		notifyUrl = propertiesUtil.getResource().getString("alipay.notifyUrl");
	}


	public String appId;
	public String appPrivateKey;
	public String charset="UTF-8";
	public String alipayPublicKey;
	public String serverUrl;
	public String notifyUrl;
	
	static AlipayClient alipayClient = null;
	
	public AlipayClient getAlipayClient() {
		if(alipayClient == null) {
			alipayClient = new DefaultAlipayClient(serverUrl, appId, appPrivateKey, "json", charset, alipayPublicKey, "RSA2");
		}
		return alipayClient;
	}
}

