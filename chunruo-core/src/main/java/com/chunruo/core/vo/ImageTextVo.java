package com.chunruo.core.vo;

public class ImageTextVo {

	private String imagePath;
	private Long productId;
	private String productName;
	private Double price;
	private String productImagePath;
	private Boolean isValidProduct = false;
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getProductImagePath() {
		return productImagePath;
	}
	public void setProductImagePath(String productImagePath) {
		this.productImagePath = productImagePath;
	}
	public Boolean getIsValidProduct() {
		return isValidProduct;
	}
	public void setIsValidProduct(Boolean isValidProduct) {
		this.isValidProduct = isValidProduct;
	}
}
