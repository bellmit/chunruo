package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 用户认证订单
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_user_auth_order", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"order_no"})
})
public class UserAuthOrder {
	private Long orderId;
	private Long userId;    			//用户ID
	private String orderNo; 			//订单号
	private String tradeNo; 			//支付交易号
	private String idCardName; 			//身份证姓名
	private String idCardNo; 			//身份证号
	private Integer payType;  			//支付类型
	private Double payAmount = 1D; 		//支付金额
	private Boolean isAuthSucc = false; //是否认证成功
	private Boolean isPaySucc = false; 	//是否支付成功
	private Boolean isRefund = false; 	//是否已退款
	private String refundNumber;     	//退款单号
	private Date payTime;            	//支付时间
	private Date refundTime;         	//退款时间
	private Integer customNumber;    	//报关次数
	private Date createTime;
	private Date updateTime;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "order_no")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name = "trade_no")
	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	@Column(name = "id_card_name")
	public String getIdCardName() {
		return idCardName;
	}

	public void setIdCardName(String idCardName) {
		this.idCardName = idCardName;
	}

	@Column(name = "id_card_no")
	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	@Column(name = "pay_amount")
	public Double getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(Double payAmount) {
		this.payAmount = payAmount;
	}

	@Column(name = "pay_type")
	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	@Column(name = "is_auth_succ", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsAuthSucc() {
		return isAuthSucc;
	}

	public void setIsAuthSucc(Boolean isAuthSucc) {
		this.isAuthSucc = isAuthSucc;
	}

	@Column(name = "is_pay_succ", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsPaySucc() {
		return isPaySucc;
	}

	public void setIsPaySucc(Boolean isPaySucc) {
		this.isPaySucc = isPaySucc;
	}

	@Column(name = "is_refund", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsRefund() {
		return isRefund;
	}

	public void setIsRefund(Boolean isRefund) {
		this.isRefund = isRefund;
	}

	@Column(name = "refund_number")
	public String getRefundNumber() {
		return refundNumber;
	}

	public void setRefundNumber(String refundNumber) {
		this.refundNumber = refundNumber;
	}

	@Column(name = "refund_time")
	public Date getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(Date refundTime) {
		this.refundTime = refundTime;
	}

	@Column(name = "pay_time")
	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	@Column(name = "custom_number")
	public Integer getCustomNumber() {
		return customNumber;
	}

	public void setCustomNumber(Integer customNumber) {
		this.customNumber = customNumber;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
