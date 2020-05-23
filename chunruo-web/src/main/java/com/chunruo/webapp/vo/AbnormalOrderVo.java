package com.chunruo.webapp.vo;

import java.util.Date;

public class AbnormalOrderVo {

	private Long orderId; //订单Id
	private String orderNo;  //订到号
	private Long notdeliverDays; //未发货天数
    private Date payTime;   //付款时间
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Long getNotdeliverDays() {
		return notdeliverDays;
	}
	public void setNotdeliverDays(Long notdeliverDays) {
		this.notdeliverDays = notdeliverDays;
	}
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	
	
}
