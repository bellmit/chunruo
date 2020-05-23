package com.chunruo.webapp.vo;

public class ProductGroupVo {
	private Long objectId;
	private Long groupId;
	private Long productId;					//商品Id
	private Long productSpecId;				//规格商品Id
	private String name;					//商品名称
	private String productCode;				//商品货号
	private Double groupPriceCost;			//组合成本价格
	private Double groupPriceWholesale;		//组合市场价格
	private Double groupPriceRecommend;		//组合售卖价格
    private Integer saleTimes;				//出售商品倍数
    private Double priceWholesale;			//最低市场价格(成本价格+上级利润)
    private Double priceRecommend;			//售卖价格
    private Double priceCost;				//成本价格(供货商成本价格)
    private Integer stockNumber;			//库存
    private String productTags;
    
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getProductCode() {
		return productCode;
	}
	
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	public Double getGroupPriceCost() {
		return groupPriceCost;
	}

	public void setGroupPriceCost(Double groupPriceCost) {
		this.groupPriceCost = groupPriceCost;
	}

	public Double getGroupPriceRecommend() {
		return groupPriceRecommend;
	}
	
	public void setGroupPriceRecommend(Double groupPriceRecommend) {
		this.groupPriceRecommend = groupPriceRecommend;
	}
	
	public Double getGroupPriceWholesale() {
		return groupPriceWholesale;
	}
	
	public void setGroupPriceWholesale(Double groupPriceWholesale) {
		this.groupPriceWholesale = groupPriceWholesale;
	}
	
	public Integer getSaleTimes() {
		return saleTimes;
	}
	
	public void setSaleTimes(Integer saleTimes) {
		this.saleTimes = saleTimes;
	}
	
	public Double getPriceWholesale() {
		return priceWholesale;
	}
	
	public void setPriceWholesale(Double priceWholesale) {
		this.priceWholesale = priceWholesale;
	}
	
	public Double getPriceRecommend() {
		return priceRecommend;
	}
	
	public void setPriceRecommend(Double priceRecommend) {
		this.priceRecommend = priceRecommend;
	}
	
	public Double getPriceCost() {
		return priceCost;
	}
	
	public void setPriceCost(Double priceCost) {
		this.priceCost = priceCost;
	}

	public Integer getStockNumber() {
		return stockNumber;
	}

	public void setStockNumber(Integer stockNumber) {
		this.stockNumber = stockNumber;
	}

	public String getProductTags() {
		return productTags;
	}

	public void setProductTags(String productTags) {
		this.productTags = productTags;
	}
}
