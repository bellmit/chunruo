package com.chunruo.core.util.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 分享商品的信息
 * @author chunruo
 *
 */
public class ShareUserVo {
	private Long userId;
	private Long productId;
	private Boolean isShareProduct;
	private Integer type;
	private Double price;
	private Integer profit;
	private List<ProductSpecVo> productSpecList = new ArrayList<ProductSpecVo>();
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Boolean getIsShareProduct() {
		return isShareProduct;
	}
	public void setIsShareProduct(Boolean isShareProduct) {
		this.isShareProduct = isShareProduct;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getProfit() {
		return profit;
	}
	public void setProfit(Integer profit) {
		this.profit = profit;
	}
	
    public List<ProductSpecVo> getProductSpecList() {
		return productSpecList;
	}
	public void setProductSpecList(List<ProductSpecVo> productSpecList) {
		this.productSpecList = productSpecList;
	}

	public static class ProductSpecVo{
		private Long productSpecId;
		private Double price;
		private Integer profit;
		public Long getProductSpecId() {
			return productSpecId;
		}
		public void setProductSpecId(Long productSpecId) {
			this.productSpecId = productSpecId;
		}
		public Double getPrice() {
			return price;
		}
		public void setPrice(Double price) {
			this.price = price;
		}
		public Integer getProfit() {
			return profit;
		}
		public void setProfit(Integer profit) {
			this.profit = profit;
		}
	}
}
