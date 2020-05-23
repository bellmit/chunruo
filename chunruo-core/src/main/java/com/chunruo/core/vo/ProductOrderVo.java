package com.chunruo.core.vo;

public class ProductOrderVo {
	private Double priceCost;
	private Double totalPriceCost;
	private Integer quantity;
	private Integer stockNumber;
	private Boolean isSoldout;
	private Long productId;
	private Long productSpecId;
	private Boolean isSpceProduct;
	private Boolean isSeckillProduct;
	private Integer seckillSalesNumber;	
	private Integer salesNumber;
	private Boolean isGiftProduct;
	private Integer limitMaxNumber;
	
	public Double getPriceCost() {
		return priceCost;
	}
	
	public void setPriceCost(Double priceCost) {
		this.priceCost = priceCost;
	}
	
	public Double getTotalPriceCost() {
		return totalPriceCost;
	}

	public void setTotalPriceCost(Double totalPriceCost) {
		this.totalPriceCost = totalPriceCost;
	}

	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public Boolean getIsSoldout() {
		return isSoldout;
	}
	
	public void setIsSoldout(Boolean isSoldout) {
		this.isSoldout = isSoldout;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getStockNumber() {
		return stockNumber;
	}

	public void setStockNumber(Integer stockNumber) {
		this.stockNumber = stockNumber;
	}

	public Long getProductSpecId() {
		return productSpecId;
	}

	public void setProductSpecId(Long productSpecId) {
		this.productSpecId = productSpecId;
	}

	public Boolean getIsSpceProduct() {
		return isSpceProduct;
	}

	public void setIsSpceProduct(Boolean isSpceProduct) {
		this.isSpceProduct = isSpceProduct;
	}

	public Boolean getIsSeckillProduct() {
		return isSeckillProduct;
	}

	public void setIsSeckillProduct(Boolean isSeckillProduct) {
		this.isSeckillProduct = isSeckillProduct;
	}
	
	public Integer getSeckillSalesNumber() {
		return seckillSalesNumber;
	}

	public void setSeckillSalesNumber(Integer seckillSalesNumber) {
		this.seckillSalesNumber = seckillSalesNumber;
	}

	public Integer getSalesNumber() {
		return salesNumber;
	}

	public void setSalesNumber(Integer salesNumber) {
		this.salesNumber = salesNumber;
	}

	public Boolean getIsGiftProduct() {
		return isGiftProduct;
	}

	public void setIsGiftProduct(Boolean isGiftProduct) {
		this.isGiftProduct = isGiftProduct;
	}

	public Integer getLimitMaxNumber() {
		return limitMaxNumber;
	}

	public void setLimitMaxNumber(Integer limitMaxNumber) {
		this.limitMaxNumber = limitMaxNumber;
	}
}
