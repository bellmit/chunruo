package com.chunruo.webapp.vo;

/**
 * 组合商品编辑对象
 * @author chunruo
 *
 */
public class AssemblyContVo {
	private Long objectId;
	private Long productId;					//商品Id
	private Long productSpecId;				//规格商品Id
	private Long productAssemblyId;			//规格商品Id
	private String name;					//商品名称
	private String productCode;				//商品货号
	private Double assPriceRecommend;		//组合售卖价格
	private Double assPriceWholesale;		//组合市场价格
    private Integer saleTimes;				//出售商品倍数
    private Double priceWholesale;			//最低市场价格(成本价格+上级利润)
    private Double priceRecommend;			//售卖价格
    private Double priceCost;				//成本价格(供货商成本价格)
    private String wareHouseName;			//仓库名称
    
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
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

	public Double getAssPriceRecommend() {
		return assPriceRecommend;
	}

	public void setAssPriceRecommend(Double assPriceRecommend) {
		this.assPriceRecommend = assPriceRecommend;
	}

	public Double getAssPriceWholesale() {
		return assPriceWholesale;
	}

	public void setAssPriceWholesale(Double assPriceWholesale) {
		this.assPriceWholesale = assPriceWholesale;
	}

	public Integer getSaleTimes() {
		return saleTimes;
	}

	public void setSaleTimes(Integer saleTimes) {
		this.saleTimes = saleTimes;
	}

	public Long getProductAssemblyId() {
		return productAssemblyId;
	}

	public void setProductAssemblyId(Long productAssemblyId) {
		this.productAssemblyId = productAssemblyId;
	}

	public String getWareHouseName() {
		return wareHouseName;
	}

	public void setWareHouseName(String wareHouseName) {
		this.wareHouseName = wareHouseName;
	}

}
