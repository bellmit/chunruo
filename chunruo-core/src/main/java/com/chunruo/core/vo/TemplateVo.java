package com.chunruo.core.vo;

import java.util.HashMap;
import java.util.Map;

public class TemplateVo {

	public static final String WEIXIN_NOTIFY_TOUSER = "touser";
	public static final String WEIXIN_NOTIFY_TEMPLATE_ID = "template_id";
	public static final String WEIXIN_NOTIFY_URL = "page";
	public static final String WEIXIN_NOTIFY_DATA = "data";
	
	private String toUser;			//接收者的 openid
	private String templateId;		//所需下发的模板消息的id
	private String url;			   //模板跳转链接（海外帐号没有跳转能力）
	private Map<String,Object> data = new HashMap<String,Object>();		//模板内容，不填则下发空模板

	public TemplateVo() {
		super();
	}
	
	public TemplateVo(String toUser, String templateId, String url,
			Map<String, Object> data) {
		super();
		this.toUser = toUser;
		this.templateId = templateId;
		this.url = url;
		this.data = data;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

}
