package com.chunruo.portal.vo;

public class OrderItemVo {
	private Long itemId;
	private Long productId;
	private Long productSpecId;
	private Boolean isSpceProduct;
	private Long seckillId;					
	private Integer quantity;
	private Integer status;
	
	public Long getItemId() {
		return itemId;
	}
	
	public void setItemId(Long itemId) {
		this.itemId = itemId;
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
	
	public Boolean getIsSpceProduct() {
		return isSpceProduct;
	}
	
	public void setIsSpceProduct(Boolean isSpceProduct) {
		this.isSpceProduct = isSpceProduct;
	}
	
	public Long getSeckillId() {
		return seckillId;
	}
	
	public void setSeckillId(Long seckillId) {
		this.seckillId = seckillId;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}	
}
