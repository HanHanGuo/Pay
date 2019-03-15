package com.xianguo.pay.util;

import java.util.HashMap;
import java.util.Map;

public class PayUtil {
	private static Map<String, Object> payService = new HashMap<>();
	
	public static Map<String, Object> getPayService(){
		return payService;
	}
	
	public static Object getPayService(String sign){
		for(String key : payService.keySet()) {
			if(key.equals(sign)) {
				return payService.get(key);
			}
		}
		return null;
	}
}
