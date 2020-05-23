package com.chunruo.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.chunruo.core.util.JsonParseUtil;
import com.chunruo.core.util.StringUtil;

public class JsonParseUtil {
	
	public static List<Map<String, Object>> parseJSON2List(String jsonStr)
			throws JSONException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (StringUtil.isNull(jsonStr)) {
			return list;
		}
		JSONArray jsonArr = new JSONArray(jsonStr);
		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject json2 = jsonArr.getJSONObject(i);
			list.add(json2Map(json2.toString()));
		}
		return list;
	}

	public static Map<String, Object> json2Map(String jsonStr)
			throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtil.isNull(jsonStr)) {
			return map;
		}
		// 最外层解析
		JSONObject json = new JSONObject(jsonStr);
		Iterator it = json.keys();
		while (it.hasNext()) {
			Object k = it.next();
			Object v = json.get(k.toString());
			// 如果内层还是数组的话，继续解析
			if (v instanceof JSONArray) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				JSONArray ar = (JSONArray) v;
				for (int i = 0; i < ar.length(); i++) {
					JSONObject json2 = ar.getJSONObject(i);
					list.add(json2Map(json2.toString()));
				}
				map.put(k.toString(), list);
			} else {
				map.put(k.toString(), v);
			}
		}
		return map;
	}

	public static void main(String[] args) {
		String test = FileIO.file2String("D://data.json");
		try {
			System.out.println(parseJSON2List(test).toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		String json = "{'result':'1','desc':'成功','lastRefreshTime':'1373869215754.8','list':[{'optionId':0,'sortrank':-1,'category':5,'picUrl':'http://pj.vstudying.com/portal-basic/image/word.png','dataUrl':'http://eyews.vstudying.com/uploads/1386602747.doc','updateTime':'1386603003350.5','contentTitle':'心之力-毛泽东','videoId':0,'shareTitle':'心之力-毛泽东','contentId':223,'shareContent':''},{'optionId':0,'sortrank':0,'category':7,'picUrl':'http://eyews.vstudying.com/include/ke/attached/image/20140111/20140111063418_48040.jpg','dataUrl':'http://eyews.vstudying.com/uploads/1389392534.pdf','updateTime':'1393904947671.8','contentTitle':'2014年春节放假通知','videoId':0,'shareTitle':'2014年春节放假通知','contentId':227,'shareContent':''},{'optionId':0,'sortrank':0,'category':7,'picUrl':'http://pj.vstudying.com/portal-basic/image/pdf.png','dataUrl':'http://eyews.vstudying.com/uploads/1389246546.pdf','updateTime':'1389246724309.6','contentTitle':'企业文化征集活动通知','videoId':0,'shareTitle':'企业文化征集活动通知','contentId':225,'shareContent':''},{'optionId':0,'sortrank':0,'category':5,'picUrl':'http://pj.vstudying.com/portal-basic/image/word.png','dataUrl':'http://eyews.vstudying.com/uploads/1389246659.docx','updateTime':'1389246714697.3','contentTitle':'企业文化表述语征集表','videoId':0,'shareTitle':'企业文化表述语征集表','contentId':226,'shareContent':''},{'optionId':0,'sortrank':0,'category':7,'picUrl':'http://eyews.vstudying.com/include/ke/attached/image/20130619/20130619120211_22094.jpg','dataUrl':'http://eyews.vstudying.com/uploads/1371631427.pdf','updateTime':'1374042187541','contentTitle':'2013年第15期工信部领导寄望国产数据库，杨学山副部长莅临达梦公司调研','videoId':0,'shareTitle':'2013年第15期工信部领导寄望国产数据库，杨学山副部长莅临达梦公司调研','contentId':191,'shareContent':''},{'optionId':0,'sortrank':0,'category':7,'picUrl':'http://eyews.vstudying.com/include/ke/attached/image/20130619/20130619115211_73708.jpg','dataUrl':'http://eyews.vstudying.com/uploads/1371613968.pdf','updateTime':'1374031325555.1','contentTitle':'2013年第4期公司连续十次蝉联“国家规划布局内 重点软件企业”','videoId':0,'shareTitle':'2013年第4期公司连续十次蝉联“国家规划布局内 重点软件企业”','contentId':180,'shareContent':''},{'optionId':0,'sortrank':0,'category':3,'picUrl':'http://eyews.vstudying.com/include/ke/attached/image/20130620/20130620124146_89457.jpg','dataUrl':'http://pj.vstudying.com/portal-basic/getWebPage.vs?type=0&id=206&channelId=88888','updateTime':'1373429381606.9','contentTitle':'中国软件上榜“中国软件创新力20强”','videoId':0,'shareTitle':'中国软件上榜“中国软件创新力20强”','contentId':206,'shareContent':''},{'optionId':0,'sortrank':0,'category':6,'picUrl':'http://eyews.vstudying.com/include/ke/attached/image/20130620/20130620122224_55118.jpg','dataUrl':'http://eyews.vstudying.com/uploads/1371702112.doc','updateTime':'1371702146101.1','contentTitle':'中国软件技术设计编程大赛通知','videoId':0,'shareTitle':'中国软件技术设计编程大赛通知','contentId':198,'shareContent':''},{'optionId':0,'sortrank':0,'category':7,'picUrl':'http://eyews.vstudying.com/include/ke/attached/image/20130619/20130619115211_73708.jpg','dataUrl':'http://eyews.vstudying.com/uploads/1371614241.pdf','updateTime':'1371614249315.3','contentTitle':'2013年第12期四川省南充市副市长访问中国软件','videoId':0,'shareTitle':'2013年第12期四川省南充市副市长访问中国软件','contentId':188,'shareContent':''},{'optionId':0,'sortrank':0,'category':7,'picUrl':'http://eyews.vstudying.com/include/ke/attached/image/20130619/20130619115211_73708.jpg','dataUrl':'http://eyews.vstudying.com/uploads/1371614106.pdf','updateTime':'1371614126293','contentTitle':'2013年第8期国资委宣传局到中国软件调研指导党建云','videoId':0,'shareTitle':'2013年第8期国资委宣传局到中国软件调研指导党建云','contentId':184,'shareContent':''},{'optionId':0,'sortrank':3,'category':3,'picUrl':'http://eyews.vstudying.com/include/ke/attached/image/20130620/20130620124030_49831.jpg','dataUrl':'http://pj.vstudying.com/portal-basic/getWebPage.vs?type=0&id=204&channelId=88888','updateTime':'1373869215754.8','contentTitle':'4.20芦山大地震四川中软抗震救灾纪实','videoId':0,'shareTitle':'4.20芦山大地震四川中软抗震救灾纪实','contentId':204,'shareContent':''}]}";
//		try {
//			Map map = JsonParseUtil.json2Map(json);
//			System.out.println(map.toString());
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
	}

}