package com.chunruo.core.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * 用户销售记录
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_user_sale_record", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"order_id" }) 
})
public class UserSaleRecord {

	private Long recordId;
	private Long userId;
	private Long orderId;
	private Double saleAmount;       // 销售额
	private Double totalProfit;      // 销售利润
	private Double refundAmount;     // 总退款销售额
	private Double refundProfit;     // 总退款利润
	private Double realRefundAmount; // 真实已退款的销售额
	private Double realRefundProfit; // 真实已退款的利润
	
	private Integer orderStatus;     // 订单状态
	private Date orderPayTime;       // 订单创建时间
	private Date orderSentTime;      // 订单发货时间

	private Date createTime;        // 创建时间
	private Date updateTime;        // 更新时间

	private Boolean isHaveRefund = false; // 是否有退款记录

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Column(name = "sale_amount")
	public Double getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(Double saleAmount) {
		this.saleAmount = saleAmount;
	}

	@Column(name = "total_profit")
	public Double getTotalProfit() {
		return totalProfit;
	}

	public void setTotalProfit(Double totalProfit) {
		this.totalProfit = totalProfit;
	}

	@Column(name = "refund_amount")
	public Double getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(Double refundAmount) {
		this.refundAmount = refundAmount;
	}

	@Column(name = "refund_profit")
	public Double getRefundProfit() {
		return refundProfit;
	}

	public void setRefundProfit(Double refundProfit) {
		this.refundProfit = refundProfit;
	}

	@Column(name = "real_refund_amount")
	public Double getRealRefundAmount() {
		return realRefundAmount;
	}

	public void setRealRefundAmount(Double realRefundAmount) {
		this.realRefundAmount = realRefundAmount;
	}

	@Column(name = "real_refund_profit")
	public Double getRealRefundProfit() {
		return realRefundProfit;
	}

	public void setRealRefundProfit(Double realRefundProfit) {
		this.realRefundProfit = realRefundProfit;
	}

	@Column(name = "order_status")
	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Column(name = "order_pay_time")
	public Date getOrderPayTime() {
		return orderPayTime;
	}

	public void setOrderPayTime(Date orderPayTime) {
		this.orderPayTime = orderPayTime;
	}

	@Column(name = "order_sent_time")
	public Date getOrderSentTime() {
		return orderSentTime;
	}

	public void setOrderSentTime(Date orderSentTime) {
		this.orderSentTime = orderSentTime;
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

	@Transient
	public Boolean getIsHaveRefund() {
		return isHaveRefund;
	}

	public void setIsHaveRefund(Boolean isHaveRefund) {
		this.isHaveRefund = isHaveRefund;
	}
}
