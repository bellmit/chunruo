package com.chunruo.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 退款退货原因列表
 * @author chunruo
 *
 */
@Entity
@Table(name = "jkd_refund_reason")
public class RefundReason {
	public static final Long REFUND_REASON_OTHER  = 17L; // 其他原因
	public static final Long REFUND_REASON_ACCOUNT_ERROR  = 20L; // 使用余额退款
	
	public static final Integer REFUND_REASON_MONEY = 1;    //退款
	public static final Integer REFUND_REASON_GOODS = 2;    //退货退款
	public static final Integer REFUND_REASON_CANCEL = 3;   //取消订单原因
	public static final Integer REFUND_REASON_REMIND = 4;   //温馨提醒
	
	private Long reasonId;		//序号
	private int reasonType;		//原因类型      1.退款 2 退货退款 3.取消订单原因 4.温馨提醒
	private String reason;		//退货原因
	private Boolean isSpecialHandle; //是否特殊处理
	
	//Transient
	private Double amount;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getReasonId() {
		return reasonId;
	}

	public void setReasonId(Long reasonId) {
		this.reasonId = reasonId;
	}

	@Column(name = "reason_type")
	public int getReasonType() {
		return this.reasonType;
	}

	public void setReasonType(int reasonType) {
		this.reasonType = reasonType;
	}

	@Column(name = "reason")
	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Column(name = "is_special_handle")
	public Boolean getIsSpecialHandle() {
		return isSpecialHandle;
	}

	public void setIsSpecialHandle(Boolean isSpecialHandle) {
		this.isSpecialHandle = isSpecialHandle;
	}

	@Transient
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
}
