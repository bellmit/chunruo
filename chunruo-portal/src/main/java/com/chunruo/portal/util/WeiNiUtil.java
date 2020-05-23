package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chunruo.core.model.WeiNiProduct;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.HttpClientUtil;
import com.chunruo.core.util.Md5Util;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;

/**
 * 一个接口调用每分钟60次  一天最多5000次
 * @author Administrator
 *
 */
public class WeiNiUtil {
	protected final transient static  Log log = LogFactory.getLog(WeiNiUtil.class);

	private final static String INTERFACENAME = "SkuListSynchro"; // 接口名称
//	private final static String PARENTER = "6000907_6000758";                   //测试
//	private final static String KEY = "0cc826b161d511ea865a7cd30adfe8ac";       //测试
//	private final static String DOMAIN = "http://121.41.84.251:9090/api/SkuListSynchro.shtml"; //测试
 
	private final static String PARENTER = "12742_5550";                        //生产
	private final static String KEY = "317f84a5c427424fa08eff1cc18776a2";       //生产
	private final static String DOMAIN = "http://vip.nysochina.com/api/SkuListSynchro.shtml"; //生产

	public static void main(String[] args) {
		getSkuListSynchro(1,100);   //生产环境20863个商品
	}

	/**
	 * 唯妮商品列表
	 */
	public static MsgModel<List<WeiNiProduct>> getSkuListSynchro(int pageNo,int pageNum) {
		MsgModel<List<WeiNiProduct>> msgModel = new MsgModel<List<WeiNiProduct>>();
		try {

			//请求参数
			Map<String,Object> contentMap = new HashMap<String,Object>();
			contentMap.put("PageNo", pageNo);
			contentMap.put("PageNum", pageNum);
			
			String date = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, DateUtil.getCurrentDate());
			
			String token = Md5Util.md5String(KEY + date + INTERFACENAME + JSONObject.toJSONString(contentMap));
			System.out.println(String.format("getSkuListSynchro[token=%s]",token));
			
			//消息头
			Map<String, String> headersMap = new HashMap<String, String>();
			headersMap.put("interfacename", INTERFACENAME);
			headersMap.put("parenter", PARENTER);
			headersMap.put("token", token);
			
			String body = JSONObject.toJSONString(contentMap);
			System.out.println(String.format("getSkuListSynchro[body=%s]",body));
			
			String result = HttpClientUtil.post(DOMAIN, headersMap, body);
			System.out.println((String.format("getSkuListSynchro[result=%s]",result)));
			JSONObject jsonObject = JSON.parseObject(result);
			if(jsonObject != null && !jsonObject.isEmpty()
					&& StringUtil.nullToBoolean(jsonObject.getBoolean("success"))
					&& StringUtil.nullToBoolean(jsonObject.getBoolean("validate"))) {
				JSONObject resultObject = jsonObject.getJSONObject("result");
				if(resultObject != null && !resultObject.isEmpty()) {
					JSONArray jsonArray = resultObject.getJSONArray("SkuList");
					if(jsonArray != null && !jsonArray.isEmpty()) {
						System.out.println("商品数量===》"+jsonArray.size());
						List<WeiNiProduct> productList = new ArrayList<WeiNiProduct>();
						for(int i = 0; i < jsonArray.size(); i++) {
							JSONObject javaObject = jsonArray.getJSONObject(i);
							WeiNiProduct weiNiProduct = javaObject.toJavaObject(WeiNiProduct.class);
							productList.add(weiNiProduct);
						}
						msgModel.setData(productList);
					}
					msgModel.setPassSize(resultObject.getInteger("TotalCount"));
				}
				msgModel.setIsSucc(true);
				return msgModel;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		return msgModel;
	}
}
