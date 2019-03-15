package com.xianguo.pay.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.xianguo.config.PropertiesUtil;
import com.xianguo.pay.entity.WeiXinOrderRequest;
import com.xianguo.pay.entity.WeiXinOrderRespose;
import com.xianguo.pay.entity.WeiXinRefundRequest;
import com.xianguo.pay.entity.WeiXinRefundRespose;
import com.xianguo.util.GUID;

import lombok.extern.slf4j.Slf4j;

/**
 * 微信支付工具类
 * 作者:鲜果
 * 日期:2018年7月9日
 */
@Slf4j
@Component
public class WXPayUtil {
	
	
	/**
	 * 微信下单地址
	 */
	public String ORDER_LOCATION = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	
	public PropertiesUtil propertiesUtil;
	
	@Autowired
	public void setPropertiesUtil(PropertiesUtil propertiesUtil) {
		this.propertiesUtil = propertiesUtil;
		init();
	}
	
	private void init() {
		WEIXIN_KEY = propertiesUtil.getResource().getString("weixin.key");//应用密钥
		
		APPID = propertiesUtil.getResource().getString("weixin.appid");//应用ID
		
		MCH_ID = propertiesUtil.getResource().getString("weixin.mch_id");//商户ID
		
		NOTIFY_URL = propertiesUtil.getResource().getString("weixin.notify_url");//通知地址
		
		TRADE_TYPE = propertiesUtil.getResource().getString("weixin.trade_type");//交易类型
		
		REFUND_NOTIFY_URL = propertiesUtil.getResource().getString("weixin.refund.notify_url");//退款通知地址
		
		ZHENSHU_PATH = propertiesUtil.getResource().getString("weixin.refund.P12");//"D:\\apiclient_cert.p12";//证书地址
	}

	/**
	 * 微信退款地址
	 */
	public String REFUND_ORDER_LOCATION = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	
	public String WEIXIN_KEY;
	
	public String APPID;
	
	public String MCH_ID;
	
	public String NOTIFY_URL;
	
	public String TRADE_TYPE;
	
	public String REFUND_NOTIFY_URL;
	
	public String ZHENSHU_PATH;
	
    //微信退款HTTP请求器
    private CloseableHttpClient refundHttpClient;
    
    /**
     * 构造带证书的httpclient
     */
	{
		LoadP12();
    }
	
	public void LoadP12() {
		try {
    		if(!StringUtils.isEmpty(ZHENSHU_PATH) && refundHttpClient==null) {
				//拼接证书的路径
				String path = ZHENSHU_PATH;
				KeyStore keyStore = KeyStore.getInstance("PKCS12");
				
				//加载本地的证书进行https加密传输
				FileInputStream instream = new FileInputStream(new File(path));
				keyStore.load(instream, MCH_ID.toCharArray());  //加载证书密码，默认为商户ID

				SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, MCH_ID.toCharArray()).build();
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
				refundHttpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();//构造退款请求类
				instream.close();
				log.info("微信证书加载成功");
    		}
		} catch (Exception e) {
			log.info("微信证书加载失败");
		}
	}
	
	/**
	 * 微信退款
	 * @author:鲜果
	 * @date:2018年9月4日
	 * @param weiXinRefundRequest 退款请求
	 * @return
	 * WeiXinRefundRespose  
	 */
	public WeiXinRefundRespose RefundOrder(WeiXinRefundRequest weiXinRefundRequest) {
		weiXinRefundRequest.setAppid(APPID);//填充应用id
		weiXinRefundRequest.setMch_id(MCH_ID);//填充商户号
		weiXinRefundRequest.setNonce_str(GUID.newGUID());//填充随机字符串
		//weiXinRefundRequest.setSub_mch_id(SUB_MCH_ID);//填充子商户号
		weiXinRefundRequest.setNotify_url(REFUND_NOTIFY_URL);//填充回调地址
		
		SortedMap<String, Object> OrderParameter = new TreeMap<String, Object>();
		OrderParameter.put("appid", weiXinRefundRequest.getAppid());
		OrderParameter.put("mch_id", weiXinRefundRequest.getMch_id());
		//OrderParameter.put("sub_mch_id", weiXinRefundRequest.getSub_mch_id());
		OrderParameter.put("nonce_str", weiXinRefundRequest.getNonce_str());
		OrderParameter.put("out_trade_no", weiXinRefundRequest.getOut_trade_no());
		OrderParameter.put("out_refund_no", weiXinRefundRequest.getOut_refund_no());
		OrderParameter.put("total_fee", weiXinRefundRequest.getTotal_fee());
		OrderParameter.put("refund_fee", weiXinRefundRequest.getRefund_fee());
		OrderParameter.put("notify_url", weiXinRefundRequest.getNotify_url());
		
		//签名
		String sign = WXSignUtils.createSign("UTF-8", OrderParameter, WEIXIN_KEY);
		weiXinRefundRequest.setSign(sign);
		
		//生成请求XML
		String orderXml = xstream.toXML(weiXinRefundRequest).replace("__", "_").replace("com.yrsoft.entity.WeiXinRefundRequest", "xml");
		log.info(orderXml);
		
		WeiXinRefundRespose weiXinRefundRespose = HttpRefundOrder(orderXml);
		return weiXinRefundRespose;
	}
	
/*	public void main(String[] args) {
		WeiXinRefundRequest weiXinRefundRequest = new WeiXinRefundRequest();
		weiXinRefundRequest.setOut_refund_no(GUID.newGUID());
		weiXinRefundRequest.setOut_trade_no("930228e1e4e24a0eb8c7305d02dbd992");
		weiXinRefundRequest.setTotal_fee("100000");
		weiXinRefundRequest.setRefund_fee("99400");
		RefundOrder(weiXinRefundRequest);
	}*/
	
	/**
	 * 微信退款
	 * @author:鲜果
	 * @date:2018年9月4日
	 * @param xml
	 * @return
	 * WeiXinRefundRespose
	 */
	public WeiXinRefundRespose HttpRefundOrder(String xml) {
		try {
			LoadP12();
			HttpPost httpPost = new HttpPost(REFUND_ORDER_LOCATION);
			// 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
			httpPost.addHeader("Content-Type", "text/xml");
			httpPost.setEntity(new StringEntity(xml, "UTF-8"));
			// 加载含有证书的http请求
			CloseableHttpResponse httpResponse = refundHttpClient.execute(httpPost);
			String refundXml = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
			log.info(refundXml);
			xstream.alias("xml", WeiXinRefundRespose.class);
			WeiXinRefundRespose weiXinRefundRespose = (WeiXinRefundRespose)xstream.fromXML(refundXml);
			return weiXinRefundRespose;
		} catch (Exception e) {
			log.info("微信退款请求失败");
			return null;
		}
	}
	
	/**
	 * 微信下单
	 */
	public WeiXinOrderRespose PlaceanOrder(WeiXinOrderRequest weixinOrderRequest){
		//填充必要参数
		weixinOrderRequest.setAppid(APPID);//填充应用id
		weixinOrderRequest.setMch_id(MCH_ID);//填充商户号
		weixinOrderRequest.setNonce_str(GUID.newGUID());//填充随机字符串
		weixinOrderRequest.setNotify_url(NOTIFY_URL);//填充回调地址
		weixinOrderRequest.setTrade_type(TRADE_TYPE);//填充交易类型
		
		//生成一个map计算sign
		SortedMap<String, Object> OrderParameter = new TreeMap<String, Object>();
		OrderParameter.put("appid", weixinOrderRequest.getAppid());
		OrderParameter.put("mch_id", weixinOrderRequest.getMch_id());
		OrderParameter.put("nonce_str", weixinOrderRequest.getNonce_str());
		OrderParameter.put("out_trade_no", weixinOrderRequest.getOut_trade_no());
		OrderParameter.put("notify_url", weixinOrderRequest.getNotify_url());
		OrderParameter.put("trade_type", weixinOrderRequest.getTrade_type());
		
		OrderParameter.put("body", weixinOrderRequest.getBody());
		OrderParameter.put("total_fee", weixinOrderRequest.getTotal_fee());
		OrderParameter.put("spbill_create_ip", weixinOrderRequest.getSpbill_create_ip());
		OrderParameter.put("attach", weixinOrderRequest.getAttach());
		
		//签名
		String sign = WXSignUtils.createSign("UTF-8", OrderParameter, WEIXIN_KEY);
		OrderParameter.put("sign", sign);
		weixinOrderRequest.setSign(sign);
		
		//生成请求XML
		String orderXml = xstream.toXML(weixinOrderRequest).replace("__", "_").replace("com.yrsoft.entity.WeiXinOrderRequest", "xml");
		log.info(orderXml);
		
		//请求统一支付API
		WeiXinOrderRespose weixinOrderRespose = httpOrder(orderXml);
		log.info(weixinOrderRespose.toString());
		return weixinOrderRespose;
	}
	
	/**
	 * 生成微信支付订单号
	 * 作者:鲜果
	 * 日期:2018年7月9日
	 * @return
	 * String
	 */
	private String OrderNo() {
		String orderNo = System.currentTimeMillis()+GUID.newGUID();
		orderNo = orderNo.substring(0, 32);
		return orderNo;
	}
	
	/**
	 * 调用统一下单API
	 * 作者:鲜果
	 * 日期:2018年7月9日
	 * @param orderInfo
	 * @return
	 * UnifiedOrderRespose
	 */
	public WeiXinOrderRespose httpOrder(String orderInfo) {
	    try {  
	        HttpURLConnection conn = (HttpURLConnection) new URL(ORDER_LOCATION).openConnection();  
	        //加入数据    
			conn.setRequestMethod("POST");    
			conn.setDoOutput(true);    
			BufferedOutputStream buffOutStr = new BufferedOutputStream(conn.getOutputStream());    
			buffOutStr.write(orderInfo.getBytes("UTF-8"));  
			buffOutStr.flush();    
			buffOutStr.close();    
			//获取输入流    
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));    
			String line = null;    
			StringBuffer sb = new StringBuffer();    
			while((line = reader.readLine())!= null){    
				sb.append(line);    
			}
			//将请求返回的内容通过xStream转换为UnifiedOrderRespose对象  
			xstream.alias("xml", WeiXinOrderRespose.class);  
			WeiXinOrderRespose weixinOrderRespose = (WeiXinOrderRespose)xstream.fromXML(sb.toString());  
			return weixinOrderRespose;
	    } catch (Exception e) {  
	    	log.error(e.getMessage(),e);
	        return null;
	    }
	}
	
	/**
     * 判断签名是否正确，必须包含sign字段，否则返回false。
     *
     * @param data Map类型数据
     * @param key API密钥
     * @param signType 签名方式
     * @return 签名是否正确
     * @throws Exception
     */
    public boolean isSignatureValid(Map<String, Object> data) throws Exception {
        if (!data.containsKey("sign") ) {
            return false;
        }
        String sign = data.get("sign").toString();
        SortedMap<String, Object> OrderParameter = new TreeMap<String, Object>();
        for(String key : data.keySet()) {
        	OrderParameter.put(key, data.get(key));
        }
        return WXSignUtils.createSign("UTF-8", OrderParameter, WEIXIN_KEY).equals(sign);
    }
    

    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public Map<String, Object> xmlToMap(String strXML) {
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception e) {
            	log.error(e.getMessage(),e);
            }
            return data;
        } catch (Exception e) {
        	log.error(e.getMessage(),e);
        	return null;
        }
 
    }
    
    /**
     * Map转Xml
     * 作者:鲜果
     * 日期:2018年7月9日
     * @param param
     * @return
     * String
     */
    public String GetMapToXML(Map<String,Object> param){  
        StringBuffer sb = new StringBuffer();  
        sb.append("<xml>");  
        for (Map.Entry<String,Object> entry : param.entrySet()) {   
        	sb.append("<"+ entry.getKey() +">");  
        	sb.append(entry.getValue().toString());  
        	sb.append("</"+ entry.getKey() +">");  
        }    
        sb.append("</xml>");
        return sb.toString();  
    }
    
    /**
	 * 获取用户实际IP
	 * 作者:鲜果
	 * 日期:2018年7月10日
	 * @param request
	 * @return
	 * String
	 */
	public String getIpAddress(HttpServletRequest request) {
	    String ip = request.getHeader("x-forwarded-for");
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getRemoteAddr();
	    }
	    if (ip.contains(",")) {
	        return ip.split(",")[0];
	    } else {
	        return ip;
	    }
	}
	
	/**
	 * 创建XStream
	 */
	private XStream xstream = new XStream(new XppDriver() {  
        public HierarchicalStreamWriter createWriter(Writer out) {  
            return new PrettyPrintWriter(out) {  
                // 对所有xml节点的转换都增加CDATA标记  
                boolean cdata = true;  
                String NodeName = "";
                @SuppressWarnings("unchecked")  
                public void startNode(String name, Class clazz) {  
                	NodeName = name;
                    super.startNode(name, clazz);  
                }  
                protected void writeText(QuickWriter writer, String text) {  
                    if (cdata) {  
                    	if(!NodeName.equals("detail")){
                    		writer.write(text); 
                    	}else{
                    		writer.write("<![CDATA[");  
                            writer.write(text);  
                            writer.write("]]>"); 
                    	}
                    } else {  
                        writer.write(text);  
                    }  
                }  
            };  
        }  
    });
}
