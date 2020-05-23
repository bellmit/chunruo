package com.chunruo.core.vo;

import java.util.ArrayList;
import java.util.List;

public class CommodityPriceVo {

	private String name;
	private String price;
	private Integer level;
	private Boolean isCurrentLevel = false;
	private String content;
	private List<String> contentList = new ArrayList<String>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Boolean getIsCurrentLevel() {
		return isCurrentLevel;
	}
	public void setIsCurrentLevel(Boolean isCurrentLevel) {
		this.isCurrentLevel = isCurrentLevel;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<String> getContentList() {
		return contentList;
	}
	public void setContentList(List<String> contentList) {
		this.contentList = contentList;
	}
	
}
