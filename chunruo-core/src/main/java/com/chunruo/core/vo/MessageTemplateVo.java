package com.chunruo.core.vo;

public class MessageTemplateVo {

	private String value;
	private String color;
	
	
	public MessageTemplateVo() {
		super();
	}
	public MessageTemplateVo(String value, String color) {
		super();
		this.value = value;
		this.color = color;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
}
