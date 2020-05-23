package com.chunruo.portal.vo;

public class ProductGroupVo {
	private Long groupId;
	private Long productId;
	private Long productSpecId;
	private Integer quantity;
	private Double paymentPrice;
	private Integer productType;
	private Integer saleTimes;
	
	public Long getGroupId() {
		return groupId;
	}
	
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	public Long getProductId() {
		return productId;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	public Long getProductSpecId() {
		return productSpecId;
	}

	public void setProductSpecId(Long productSpecId) {
		this.productSpecId = productSpecId;
	}

	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public Double getPaymentPrice() {
		return paymentPrice;
	}
	
	public void setPaymentPrice(Double paymentPrice) {
		this.paymentPrice = paymentPrice;
	}
	
	public Integer getProductType() {
		return productType;
	}
	
	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	public Integer getSaleTimes() {
		return saleTimes;
	}

	public void setSaleTimes(Integer saleTimes) {
		this.saleTimes = saleTimes;
	}
}
