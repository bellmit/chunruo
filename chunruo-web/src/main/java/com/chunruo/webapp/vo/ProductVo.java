package com.chunruo.webapp.vo;

public class ProductVo {
	
	private Long productId;
	private Long productSpecId;
	private Boolean isSpecProduct;
	private String name;
	private String productTags;
	
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
	public Boolean getIsSpecProduct() {
		return isSpecProduct;
	}
	public void setIsSpecProduct(Boolean isSpecProduct) {
		this.isSpecProduct = isSpecProduct;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProductTags() {
		return productTags;
	}
	public void setProductTags(String productTags) {
		this.productTags = productTags;
	}
	
	

}
