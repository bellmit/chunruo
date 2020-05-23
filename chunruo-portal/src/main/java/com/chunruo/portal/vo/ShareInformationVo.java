package com.chunruo.portal.vo;

public class ShareInformationVo {
	private String originalId; //小程序原始ID
	private Long productId;   //商品id
	private String productPath;  //分享商品路径
	private String title;    //分享标题
	private String shareUserSecret;    //分享商品利润信息
	private String imageUrl; //分享商品图片
	
	public String getOriginalId() {
		return originalId;
	}
	public void setOriginalId(String originalId) {
		this.originalId = originalId;
	}
	public String getProductPath() {
		return productPath;
	}
	public void setProductPath(String productPath) {
		this.productPath = productPath;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getShareUserSecret() {
		return shareUserSecret;
	}
	public void setShareUserSecret(String shareUserSecret) {
		this.shareUserSecret = shareUserSecret;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
