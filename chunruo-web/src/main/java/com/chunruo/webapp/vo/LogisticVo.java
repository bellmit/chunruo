package com.chunruo.webapp.vo;

public class LogisticVo {
	private Integer orderStatus;
	private String orderNumber;
	private String expressNumber;
	private String logisticCode;
	private String logisticName;

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public String getExpressNumber() {
		return expressNumber;
	}
	
	public void setExpressNumber(String expressNumber) {
		this.expressNumber = expressNumber;
	}
	
	public String getLogisticCode() {
		return logisticCode;
	}
	
	public void setLogisticCode(String logisticCode) {
		this.logisticCode = logisticCode;
	}
	
	public String getLogisticName() {
		return logisticName;
	}
	
	public void setLogisticName(String logisticName) {
		this.logisticName = logisticName;
	}
}
