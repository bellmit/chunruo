package com.chunruo.portal.vo;

import java.io.Serializable;

public class WebViewFileVo implements Serializable{
	private static final long serialVersionUID = 1L;
	private int uniqueId;		// 唯一ID
	private String ref; 		// 替换符号
	private String type; 		// 文件类型
	private String src; 		// 链接地址
	private String pixel; 		// 文件尺寸 
	private String price;       //价格
	private String brand;       //品牌
	private String title;       //标题
	private Boolean isLocal;	// 是否本地文件
	private Integer showType ;  //1 表示图片，2表示视频
	private String imgURL =""; //视频封面
	private Long storeId;      //店铺id
	private Long productId;    //产品id
	private String storeName;  //店铺名称
	
	public int getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(int uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getRef() {
		return ref;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	} 
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getSrc() {
		return src;
	}
	
	public void setSrc(String src) {
		this.src = src;
	}
	
	public String getPixel() {
		return pixel;
	}
	
	public void setPixel(String pixel) {
		this.pixel = pixel;
	}

	public Boolean getIsLocal() {
		return isLocal;
	}

	public void setIsLocal(Boolean isLocal) {
		this.isLocal = isLocal;
	}

	public Integer getShowType() {
		return showType;
	}

	public void setShowType(Integer showType) {
		this.showType = showType;
	}

	public String getImgURL() {
		return imgURL;
	}

	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
}
