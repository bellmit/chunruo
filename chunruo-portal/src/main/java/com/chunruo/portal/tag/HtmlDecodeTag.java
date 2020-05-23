package com.chunruo.portal.tag;

import com.google.gson.Gson;
import com.chunruo.core.util.StringUtil;

/**
 * josn格式过滤特殊字符串
 * @author chunruo
 *
 */
public class HtmlDecodeTag {

	public String get(String value){
		try{
			if (!StringUtil.isNull(value)) {
				String htmlValue = StringUtil.null2Str(value);
				htmlValue = htmlValue.replace("&amp;", "&");
				htmlValue = htmlValue.replace("&quot;", "\"");
				htmlValue = htmlValue.replace("&lt;", "<");
				htmlValue = htmlValue.replace("&gt;", ">");
				Gson gson = new Gson();  
				htmlValue =  gson.toJson(htmlValue);
				if (!StringUtil.isNull(htmlValue) 
						&& htmlValue.startsWith("\"") 
						&& htmlValue.endsWith("\"")
						&& htmlValue.length() > 2) {
					htmlValue = htmlValue.substring(1, htmlValue.length() - 1);
				}
				value = htmlValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return StringUtil.null2Str(value);
	}
}