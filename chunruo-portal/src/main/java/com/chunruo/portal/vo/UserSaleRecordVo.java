package com.chunruo.portal.vo;

public class UserSaleRecordVo {

	private String strKey;
	private Double saleAmount = new Double(0); // 销售额
	private Double totalProfit = new Double(0);// 销售利润
	private Double refundAmount = new Double(0); // 退款销售额
	private Double refundProfit = new Double(0); // 退款利润
	private Boolean isHaveRefund; //是否有退款订单
	
	
	public String getStrKey() {
		return strKey;
	}
	public void setStrKey(String strKey) {
		this.strKey = strKey;
	}
	public Double getSaleAmount() {
		return saleAmount;
	}
	public void setSaleAmount(Double saleAmount) {
		this.saleAmount = saleAmount;
	}
	public Double getTotalProfit() {
		return totalProfit;
	}
	public void setTotalProfit(Double totalProfit) {
		this.totalProfit = totalProfit;
	}
	public Double getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(Double refundAmount) {
		this.refundAmount = refundAmount;
	}
	public Double getRefundProfit() {
		return refundProfit;
	}
	public void setRefundProfit(Double refundProfit) {
		this.refundProfit = refundProfit;
	}
	public Boolean getIsHaveRefund() {
		return isHaveRefund;
	}
	public void setIsHaveRefund(Boolean isHaveRefund) {
		this.isHaveRefund = isHaveRefund;
	}
	
	
}
