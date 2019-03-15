package com.xianguo.pay.config;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.xianguo.pay.PaymentManager;
import com.xianguo.pay.annotation.PaySign;
import com.xianguo.pay.util.PayUtil;

@Component
public class PayConfig implements ApplicationRunner, ApplicationContextAware {
	private static final Logger logger = LoggerFactory.getLogger(PayConfig.class);
	ApplicationContext applicationContext = null;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Map<String, Object> map = this.applicationContext.getBeansWithAnnotation(PaySign.class);
		map.forEach((k, v) -> {
			Annotation[] check = v.getClass().getAnnotationsByType(PaySign.class);
			if(check.length > 0) {
				if(v instanceof PaymentManager) {
					PayUtil.getPayService().put(((PaySign)check[0]).value(), v);
				}
			}
		});
		PayUtil.getPayService().forEach((k,v)->{
			logger.info("加载PayService:PaySign->"+k+"\tClassPath->"+v);
		});
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
