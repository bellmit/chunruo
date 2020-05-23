package com.chunruo.core.util.vo;

public class HttpVo {
	public final static int STATUS_OK = 200;
	private int statusCode;
	private String message;
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
