package com.chunruo.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author chunruo
 */
@Entity
@Table(name = "jkd_user_withdrawal")
public class UserWithdrawal implements Serializable {
	private static final long serialVersionUID = 1L;
	public final static int USER_WITHDRAWAL_STATUS_CHECK = 1;	//申请中
	public final static int USER_WITHDRAWAL_STATUS_SUCC = 3;	//提现成功
	public final static int USER_WITHDRAWAL_STATUS_FAIL = 4;	//提现失败
	
	private Long recordId; 			// 序号
	private String tradeNo; 		// 交易流水号
	private Long userId; 			// 用户ID
	private String name;            // 用户名称
	private Integer status; 		// 状态(1:申请中;3:提现成功;4:提现失败)
	private Double amount; 			// 提现金额
	private String remarks; 		// 备注
	private Date complateTime; 		// 完成时间
	private Date createTime; 		// 创建时间
	private Date updateTime; 		// 更新时间

	@Transient
	private String userName;		// 用户昵称

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	@Column(name = "trade_no")
	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo == null ? null : tradeNo.trim();
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "amount")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}


	@Column(name = "remarks", length = 500)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	@Column(name = "complate_time")
	public Date getComplateTime() {
		return complateTime;
	}

	public void setComplateTime(Date complateTime) {
		this.complateTime = complateTime;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}



}