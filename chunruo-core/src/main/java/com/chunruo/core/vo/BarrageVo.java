package com.chunruo.core.vo;

public class BarrageVo {

	private String headerImage;
	private String content;
	
	public BarrageVo(String headerImage, String content) {
		super();
		this.headerImage = headerImage;
		this.content = content;
	}
	public String getHeaderImage() {
		return headerImage;
	}
	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
