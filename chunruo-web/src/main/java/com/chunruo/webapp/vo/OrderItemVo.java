package com.chunruo.webapp.vo;

import java.io.Serializable;

public class OrderItemVo implements Serializable{
	private static final long serialVersionUID = -1330583701869527483L;
	private String gname;		//商品名称应据实填报
	private String itemLink;	//商品展示链接地址应据实填报
	
	public String getGname() {
		return gname;
	}
	public void setGname(String gname) {
		this.gname = gname;
	}
	public String getItemLink() {
		return itemLink;
	}
	public void setItemLink(String itemLink) {
		this.itemLink = itemLink;
	}

}
