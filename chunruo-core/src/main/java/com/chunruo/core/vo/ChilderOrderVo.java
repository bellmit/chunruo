package com.chunruo.core.vo;

/**
 * 拆单子订单
 * @author chunruo
 *
 */
public class ChilderOrderVo {
	private Integer totalNumber = new Integer(0);		//订单总件数
	private Double tax = new Double (0);					//总税收
	private Double postage = new Double (0);				//总邮费
	private Double realSellPrice = new Double (0);      //商品拿货价
	private Double productAmount = new Double(0);		//商品总金额
	private Double orderAmount = new Double (0);			//订单总金额
	private Double payAmount = new Double (0);			//实际付款金额
	private Double topProfit = new Double (0);			//上级供应店铺利润
	private Double subProfit = new Double(0);           //分销利润
	private Integer productType;							//商品类型
	private Long wareHouseId;							//仓库ID
	private Boolean isAssemblyProduct;					//是否有组合商品
	
	public Integer getTotalNumber() {
		return totalNumber;
	}
	
	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}
	
	public Double getTax() {
		return tax;
	}
	
	public void setTax(Double tax) {
		this.tax = tax;
	}
	
	public Double getPostage() {
		return postage;
	}
	
	public void setPostage(Double postage) {
		this.postage = postage;
	}
	
	public Double getProductAmount() {
		return productAmount;
	}
	
	public void setProductAmount(Double productAmount) {
		this.productAmount = productAmount;
	}
	
	public Double getOrderAmount() {
		return orderAmount;
	}
	
	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}

	public Long getWareHouseId() {
		return wareHouseId;
	}

	public void setWareHouseId(Long wareHouseId) {
		this.wareHouseId = wareHouseId;
	}

	public Double getTopProfit() {
		return topProfit;
	}

	public void setTopProfit(Double topProfit) {
		this.topProfit = topProfit;
	}

	public Double getSubProfit() {
		return subProfit;
	}

	public void setSubProfit(Double subProfit) {
		this.subProfit = subProfit;
	}

	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	public Double getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(Double payAmount) {
		this.payAmount = payAmount;
	}

	public Boolean getIsAssemblyProduct() {
		return isAssemblyProduct;
	}

	public void setIsAssemblyProduct(Boolean isAssemblyProduct) {
		this.isAssemblyProduct = isAssemblyProduct;
	}

	public Double getRealSellPrice() {
		return realSellPrice;
	}

	public void setRealSellPrice(Double realSellPrice) {
		this.realSellPrice = realSellPrice;
	}
}
