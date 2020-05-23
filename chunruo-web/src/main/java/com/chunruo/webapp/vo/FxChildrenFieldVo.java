package com.chunruo.webapp.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * 微页面数据模板
 * @author chunruo
 *
 */
public class FxChildrenFieldVo {
	
	private String field_type;
	private Map<String,Object> content = new HashMap<String, Object>();
	
	public String getField_type() {
		return field_type;
	}
	public void setField_type(String field_type) {
		this.field_type = field_type;
	}
	
	public Map<String,Object>  getContent() {
		return content;
	}
	public void setContent(Map<String,Object> content) {
		this.content = content;
	}
	
	
}
