package com.chunruo.core.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 支付请求行为记录
 * 
 * @author chunruo
 *
 */
@Entity
@Table(name = "jkd_order_payment_record")
public class OrderPaymentRecord implements Serializable {
	private static final long serialVersionUID = 3549431626758220616L;
	private Long recordId;
	private Long orderId;
	private String orderNo;				// 商家订单号
	private Boolean isFriendPay;		// 是否分享朋友代付
	private Integer paymentType;
	private Long weChatConfigId;
	private String batchNumber;
	private Integer syncNumber;
	private String requestData;			//支付请求前记录
	private String responseData;		//支付请求后记录
	private Boolean isPaymentSucc;		//是否支付成功
	private Date updateTime;
	private Date createTime;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	@Column(name = "order_no")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name = "is_friend_pay", columnDefinition="bit default false")
	public Boolean getIsFriendPay() {
		return isFriendPay;
	}

	public void setIsFriendPay(Boolean isFriendPay) {
		this.isFriendPay = isFriendPay;
	}

	@Column(name = "payment_type")
	public Integer getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}

	@Column(name = "we_chat_config_id")
	public Long getWeChatConfigId() {
		return weChatConfigId;
	}

	public void setWeChatConfigId(Long weChatConfigId) {
		this.weChatConfigId = weChatConfigId;
	}

	@Column(name = "batch_number")
	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	@Column(name = "sync_number")
	public Integer getSyncNumber() {
		return syncNumber;
	}

	public void setSyncNumber(Integer syncNumber) {
		this.syncNumber = syncNumber;
	}

	@Column(name = "request_data")
	public String getRequestData() {
		return requestData;
	}

	public void setRequestData(String requestData) {
		this.requestData = requestData;
	}
	
	@Column(name = "response_data", length=2000)
	public String getResponseData() {
		return responseData;
	}
	
	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}
	
	@Column(name = "is_payment_succ", columnDefinition="bit default false")
	public Boolean getIsPaymentSucc() {
		return isPaymentSucc;
	}
	
	public void setIsPaymentSucc(Boolean isPaymentSucc) {
		this.isPaymentSucc = isPaymentSucc;
	}

	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
