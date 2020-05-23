package com.chunruo.portal.vo;

import java.io.Serializable;

public class WebHtmlContVo implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final int CONT_TYPE_HTML = 1;
	public static final int CONT_TYPE_IMAGE = 2;
	public static final int CONT_TYPE_VIDEO = 3;
	public static final int CONT_TYPE_PRODUCT = 4;
	private int contType;
	private String content;
	private WebViewFileVo webViewFileVo;
	
	public int getContType() {
		return contType;
	}
	
	public void setContType(int contType) {
		this.contType = contType;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public WebViewFileVo getWebViewFileVo() {
		return webViewFileVo;
	}
	
	public void setWebViewFileVo(WebViewFileVo webViewFileVo) {
		this.webViewFileVo = webViewFileVo;
	}
}
