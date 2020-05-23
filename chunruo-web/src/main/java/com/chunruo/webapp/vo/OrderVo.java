package com.chunruo.webapp.vo;

import java.io.Serializable;
import java.util.List;

public class OrderVo implements Serializable{
	private static final long serialVersionUID = -1330583701869527483L;
	private String orderNo;				                               //订单号
	private Long channelId = 2L;                                       //频道id
	private String payInitalRequest;	                               //支付原始请求
	private String payInitalResponse;	                               //支付原始响应
	private Integer verDept;                                           //验核机构
	private Integer paymentType;                                       //支付类型（0 微信，1支付宝）
	private String payTransactionId;	                               //交易流水号
	private Double totalAmount;			                               //交易金额
	private String payType;				                               //支付类型(用户支付的类型。1-APP;2-PC;3-扫码;4-其他)
	private String tradingTime;			                               //交易成功时间
	private String note;                                               //备注
	
	
	private List<OrderItemVo> orderItemList;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}
	
	public Integer getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}

	public String getPayInitalRequest() {
		return payInitalRequest;
	}

	public void setPayInitalRequest(String payInitalRequest) {
		this.payInitalRequest = payInitalRequest;
	}

	public String getPayInitalResponse() {
		return payInitalResponse;
	}

	public void setPayInitalResponse(String payInitalResponse) {
		this.payInitalResponse = payInitalResponse;
	}

	public Integer getVerDept() {
		return verDept;
	}

	public void setVerDept(Integer verDept) {
		this.verDept = verDept;
	}

	public String getPayTransactionId() {
		return payTransactionId;
	}

	public void setPayTransactionId(String payTransactionId) {
		this.payTransactionId = payTransactionId;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getTradingTime() {
		return tradingTime;
	}

	public void setTradingTime(String tradingTime) {
		this.tradingTime = tradingTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<OrderItemVo> getOrderItemList() {
		return orderItemList;
	}

	public void setOrderItemList(List<OrderItemVo> orderItemList) {
		this.orderItemList = orderItemList;
	}
}
