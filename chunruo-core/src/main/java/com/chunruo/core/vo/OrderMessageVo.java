package com.chunruo.core.vo;

public class OrderMessageVo {

	private String productName;
	private String productTags;
	private Integer number;
	private Integer totalNumber;
	private Double payMoney;
	private String name;
	private String orderTitle;
	private String mobile;
	private String address;
	private String expressNo;
	private String orderNotice;
	private Integer logisticsStatus; 
	private String orderNo;
	private Boolean isNewOrder;
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductTags() {
		return productTags;
	}
	public void setProductTags(String productTags) {
		this.productTags = productTags;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public Integer getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}
	public Double getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getExpressNo() {
		return expressNo;
	}
	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}
	public String getOrderNotice() {
		return orderNotice;
	}
	public void setOrderNotice(String orderNotice) {
		this.orderNotice = orderNotice;
	}
	public String getOrderTitle() {
		return orderTitle;
	}
	public void setOrderTitle(String orderTitle) {
		this.orderTitle = orderTitle;
	}
	public Integer getLogisticsStatus() {
		return logisticsStatus;
	}
	public void setLogisticsStatus(Integer logisticsStatus) {
		this.logisticsStatus = logisticsStatus;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Boolean getIsNewOrder() {
		return isNewOrder;
	}
	public void setIsNewOrder(Boolean isNewOrder) {
		this.isNewOrder = isNewOrder;
	}
	
}
