package com.chunruo.core.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.DefinitionList;
import org.htmlparser.util.NodeList;

import com.chunruo.core.Constants;
import com.chunruo.core.vo.ExpressVO;
import com.chunruo.core.vo.ExpressVO.Data;
import com.google.gson.Gson;
import com.chunruo.core.util.HttpClientUtil;
import com.chunruo.core.util.Md5Util;
import com.chunruo.core.util.StringUtil;

public class LogisticsUtil {

	private static String key;
	private static String customer;
	private static String url;
	
	/**
	 * 包裹信息
	 * @param order
	 * @return
	 */
	public static ExpressVO getExpressInfo(String expressNo, String expressCode,String consigneePhone){
		if (!StringUtil.isNull(expressNo) && !StringUtil.isNull(expressCode)){
			try {
				return LogisticsUtil.getLogisticsInfo(expressCode, expressNo, consigneePhone);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 国内快递
	 * @param com
	 * @param num
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static ExpressVO getLogisticsInfoByKuaiDi100(String com, String num, String consigneePhone)
			throws UnsupportedEncodingException {
		
//		key = "lYxnDjhh4546";
//		customer = "3FCE3F7BFF78B7B12981D5686B26B466";
//		url = "http://poll.kuaidi100.com/poll/query.do";
		key = Constants.conf.getProperty("kuaidi100.key");
		customer = Constants.conf.getProperty("kuaidi100.customer");
		url = Constants.conf.getProperty("kuaidi100.query.url");

		Param p = new Param();
		p.setCom(com);
		p.setNum(num);
		p.setPhone(consigneePhone);

		Gson gson = new Gson();
		String param = gson.toJson(p);
		String signString = param + key + customer;
		String sign = DigestUtils.md5Hex(signString).toUpperCase();
		Map<String, String> postParamMap = new HashMap<String, String>();
		postParamMap.put("customer", customer);
		postParamMap.put("sign", sign);
		postParamMap.put("param", param);
		String result = HttpClientUtil.post(url, postParamMap);
		ExpressVO vo = gson.fromJson(result, ExpressVO.class);
		return vo;
	}
	
	/**
	 * 越洋仓快递
	 * @return
	 */
	public static ExpressVO getLogisticsInfoByYeahyoung(String num,String consigneePhone){
		url = Constants.conf.getProperty("yeahyoung.query.url");
		String result = HttpClientUtil.get(url + num);
		result = result.replaceAll("/", "-");
		Gson gson = new Gson();
		ExpressVO vo = gson.fromJson(result, ExpressVO.class);
		vo.setCom("yeahyoung");
		//如果已经转到国内
		if (!StringUtil.isNull(vo.getOthername()) 
				&& !StringUtil.isNull(vo.getOthernum()) 
				){
			Data data = new Data();
			Data lastData = vo.getData().get(vo.getData().size() -1);
			data.setTime(lastData.getTime());
			data.setContext("已经转给国内快递:" + vo.getOthername() + " 快递单号为:" +vo.getOthernum());
			vo.getData().add(data);
			if (!StringUtil.isNull(Constants.packageComMap.get(vo.getOthername()))){
				try {
					ExpressVO expressVO = getLogisticsInfoByKuaiDi100(Constants.packageComMap.get(vo.getOthername()), vo.getOthernum(),consigneePhone);
					//物流信息反转
					Collections.reverse(expressVO.getData());
					vo.getData().addAll(expressVO.getData());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
		}
		//物流信息反转
		Collections.reverse(vo.getData());
		return vo;
	}
	
	/**
	 * 飞鸽快件
	 * @param num
	 * @return
	 */
	public static ExpressVO getLogisticsInfoByFeige(String num){
		ExpressVO expressVo = new ExpressVO ();
		try {
			//物流获取页面http://www.feigex.com
			String htmlContent = HttpClientUtil.get("http://feigex.com/search.aspx?keyword=" + num);
			Parser parser = new Parser(htmlContent);
			NodeList list = parser.extractAllNodesThatMatch(new TagNameFilter("dl"));   
			for (int i = 0; i < list.size(); i++) {   
				try {
					Node childNode = list.elementAt(i);
					if (childNode instanceof DefinitionList){
						DefinitionList table = (DefinitionList) childNode;
						String classContent = StringUtil.null2Str(table.getAttribute("class")).toLowerCase();
						if(classContent.contains("form-group") && !classContent.contains("head")) {
							String content = StringUtil.null2Str(table.toPlainTextString()).trim();
							if(content.contains("【") && content.indexOf("【") > 0) {
								int length = content.indexOf("【");
								ExpressVO.Data data = new ExpressVO.Data ();
								data.setContext(content.substring(length, content.length()));
								data.setTime(content.substring(0, length).replace("/", "-"));
								expressVo.getData().add(0, data);
							}
						}
					}
				}catch(Exception e) {
					continue;
				}
			}   
		}catch(Exception e) {
			e.printStackTrace();
		}
		return expressVo;
	}
	
	/**
	 * 跨境物流查询
	 * @param num
	 * @return
	 */
	public static ExpressVO getLogisticsInfoByPca(String num){
		url = StringUtil.null2Str(Constants.conf.getProperty("pca.query.url"));
		String apiKey = StringUtil.null2Str(Constants.conf.getProperty("pca.apiKey"));
		String apiId= StringUtil.null2Str(Constants.conf.getProperty("pca.apiId"));
		String test= StringUtil.null2Str(Constants.conf.getProperty("pca.test"));
		
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("api_id",apiId);
		paramMap.put("test", test);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("connote", num);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		ExpressVO expressVO = new ExpressVO();
		paramMap.put("data", StringUtil.null2Str(jsonObject));
		//签名
		String signStr = hashSign(paramMap, apiKey) ;
		paramMap.put("sign", signStr);
		//获取物流信息
		String result = HttpClientUtil.post(url, paramMap);
		try {
			//设置物流公司
			expressVO.setCom("pca");
			JSONObject resultObj = new JSONObject(result);
			//获取物流信息列表
			if (resultObj != null || resultObj.has("tracks")){
				JSONArray dataList = resultObj.getJSONArray("tracks");
				if (dataList != null && dataList.length() > 0){
					//解析物流信息
					for (int i=0 ; i< dataList.length() ; i++){
						JSONArray  array = (JSONArray) dataList.get(i);
						Data data = new Data();
						data.setContext(array.getString(0));
						data.setFtime(array.getString(2));
						data.setTime(data.getFtime());
						expressVO.getData().add(data);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//物流信息反转
		Collections.reverse(expressVO.getData());
		return expressVO;
	}
	
	/**
	 * 跨境物流查询签名
	 * 规则如下
	 * 1.将map中的数据按照 ASCII 码从小到大排序（字典序）
	 * 2.将排序好的map中的key value取出并且拼成字符串
	 * 3 在第二部得到的字符串头尾加上apiKey
	 * 4 字符串做md5加密
	 * 5加密后的字符串转成大写
	 * @param map
	 * @param apiKey
	 * @return
	 */
	public static String hashSign(Map<String, String> map,String apiKey){
		if (map == null || map.size() == 0){
			return null;
		}
		List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
		// 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});
		
		String signStr = "";
		for (Entry<String, String> entry : infoIds) {
			signStr = signStr + entry.getKey() + StringUtil.null2Str(entry.getValue());
		}
		
		if (StringUtil.isNull(signStr)){
			return null;
		}
		signStr = apiKey + signStr + apiKey;
		String md5String = Md5Util.md5String(signStr);
	    return md5String.toUpperCase();
	}
	
	public static class Param {
		private String com;
		private String num;
		private String from;
		private String to;
		private String phone;

		public String getCom() {
			return com;
		}

		public void setCom(String com) {
			this.com = com;
		}

		public String getNum() {
			return num;
		}

		public void setNum(String num) {
			this.num = num;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

	}
	
	/**
	 * 获取快递信息
	 * @param com
	 * @param num
	 * @return
	 */
	public static ExpressVO getLogisticsInfo(String com, String num,String consigneePhone){
		ExpressVO expressVO = new ExpressVO();
		if(StringUtil.compareObject(StringUtil.null2Str(com).toLowerCase(), "pca")){
			expressVO = getLogisticsInfoByPca(num);
		}else if (StringUtil.compareObject(StringUtil.null2Str(com).toLowerCase(), "yeahyoung")){
			expressVO = getLogisticsInfoByYeahyoung(num,consigneePhone);
		}else if (StringUtil.compareObject(StringUtil.null2Str(com).toLowerCase(), "feigekuaijian")){
			expressVO = getLogisticsInfoByFeige(num);
		}else{
			try {
				expressVO = getLogisticsInfoByKuaiDi100(com, num, consigneePhone);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return expressVO;
	}
}
