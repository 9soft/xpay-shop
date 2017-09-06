package com.xpay.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.xpay.shop.constant.XPayConstants;
import com.xpay.shop.utils.HttpClientUtil;
import com.xpay.shop.utils.HttpConnectionUtil;
import com.xpay.shop.utils.XpayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service("xPayService")
public class XPayService {
	private static final Logger log = LoggerFactory.getLogger(XPayService.class);
	public JSONObject pay(long trxamt,String reqsn,String paytype,String body,String remark,String acct,String authcode,String notify_url,String limit_pay) throws Exception{
		TreeMap<String,Object> params = new TreeMap<String,Object>();
		params.put("cusid", XPayConstants.SYB_CUSID);
        params.put("appid", XPayConstants.SYB_APPID);
        params.put("version", "11");
        params.put("trxamt", String.valueOf(trxamt));
        params.put("reqsn", reqsn);
        params.put("paytype", paytype);
        params.put("randomstr", XpayUtil.getValidatecode(8));
        params.put("body", body);
        params.put("remark", remark);
        params.put("acct", acct);
        params.put("notify_url", notify_url);
        params.put("limit_pay", limit_pay);
        params.put("sign", XpayUtil.sign(params, XPayConstants.SYB_APPKEY));
        String responseContent = HttpClientUtil.sendPostRequest(XPayConstants.SYB_APIURL, params);
		log.info("--------------收到xpay返回的信息"+responseContent);
        handleResult(responseContent);
        return JSONObject.parseObject(responseContent);
		
	}
	
	/*public Map<String,String> cancel(long trxamt,String reqsn,String oldtrxid,String oldreqsn) throws Exception{
		HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_APIURL+"/cancel");
		http.init();
		TreeMap<String,Object> params = new TreeMap<String,Object>();
		params.put("cusid", SybConstants.SYB_CUSID);
		params.put("appid", SybConstants.SYB_APPID);
		params.put("version", "11");
		params.put("trxamt", String.valueOf(trxamt));
		params.put("reqsn", reqsn);
		params.put("oldtrxid", oldtrxid);
		params.put("oldreqsn", oldreqsn);
		params.put("randomstr", SybUtil.getValidatecode(8));
		params.put("sign", SybUtil.sign(params,SybConstants.SYB_APPKEY));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(result);
		return map;
	}
	*/
	public Map<String,String> refund(long trxamt,String reqsn,String oldtrxid,String oldreqsn) throws Exception{
		HttpConnectionUtil http = new HttpConnectionUtil(XPayConstants.SYB_APIURL+"/refund");
		http.init();
		TreeMap<String,Object> params = new TreeMap<String,Object>();
		params.put("cusid", XPayConstants.SYB_CUSID);
		params.put("appid", XPayConstants.SYB_APPID);
		params.put("version", "11");
		params.put("trxamt", String.valueOf(trxamt));
		params.put("reqsn", reqsn);
		params.put("oldreqsn", oldreqsn);
		params.put("oldtrxid", oldtrxid);
		params.put("randomstr", XpayUtil.getValidatecode(8));
		params.put("sign", XpayUtil.sign(params, XPayConstants.SYB_APPKEY));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(result);
		return map;
	}
	/*
	public Map<String,String> query(String reqsn,String trxid) throws Exception{
		HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_APIURL+"/query");
		http.init();
		TreeMap<String,String> params = new TreeMap<String,String>();
		params.put("cusid", SybConstants.SYB_CUSID);
		params.put("appid", SybConstants.SYB_APPID);
		params.put("version", "11");
		params.put("reqsn", reqsn);
		params.put("trxid", trxid);
		params.put("randomstr", SybUtil.getValidatecode(8));
		params.put("sign", SybUtil.sign(params,SybConstants.SYB_APPKEY));
		byte[] bys = http.postParams(params, true);
		String result = new String(bys,"UTF-8");
		Map<String,String> map = handleResult(result);
		return map;
	}
*/	
	
	public static Map<String,String> handleResult(String result) throws Exception{
		Map map = XpayUtil.json2Obj(result, Map.class);
		if(map == null){
			throw new Exception("返回数据错误");
		}
		if("SUCCESS".equals(map.get("retcode"))){
			TreeMap tmap = new TreeMap();
			tmap.putAll(map);
			String sign = tmap.remove("sign").toString();
			tmap.remove("xpayinfo");
			String sign1 = XpayUtil.sign(tmap, XPayConstants.SYB_APPKEY);
			if(sign1.toLowerCase().equals(sign.toLowerCase())){
				return map;
			}else{
				throw new Exception("验证签名失败");
			}
			
		}else{
			throw new Exception(map.get("retmsg").toString());
		}
	}
	
	public static Map<String,String> trxFile() throws Exception{
		Map<String,Object> paraMap = new HashMap<>();
		paraMap.put("cusid", "990440148166000");
		paraMap.put("appid", "00000003");
		paraMap.put("date", "20170823");
		paraMap.put("randomstr", XpayUtil.getValidatecode(8));
		TreeMap<String, Object> treeMap = new TreeMap<>(paraMap);
		paraMap.put("sign", XpayUtil.sign(treeMap, XPayConstants.SYB_APPKEY));
		String responseContent = HttpClientUtil.sendPostRequest("https://vsp.allinpay.com/apiweb/trxfile/get", paraMap);
		return handleResult(responseContent);
	}
}
