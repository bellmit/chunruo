package com.chunruo.portal.vo;

public class ProductVo {

	private Long productId;
	private Long primarySpecId;
	private Integer productType;
	private Double tax;
	private Integer stockNumber;
	private Boolean isFreeTax;
	private String name;
	private String image;
	private String shortName;
	private Double price;
	private String desc;
	
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Long getPrimarySpecId() {
		return primarySpecId;
	}
	public void setPrimarySpecId(Long primarySpecId) {
		this.primarySpecId = primarySpecId;
	}
	public Integer getProductType() {
		return productType;
	}
	public void setProductType(Integer productType) {
		this.productType = productType;
	}
	public Double getTax() {
		return tax;
	}
	public void setTax(Double tax) {
		this.tax = tax;
	}
	public Integer getStockNumber() {
		return stockNumber;
	}
	public void setStockNumber(Integer stockNumber) {
		this.stockNumber = stockNumber;
	}
	public Boolean getIsFreeTax() {
		return isFreeTax;
	}
	public void setIsFreeTax(Boolean isFreeTax) {
		this.isFreeTax = isFreeTax;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
