package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 用户会员购买记录（试用经销商）
 * @author Administrator
 */
@Entity
@Table(name = "jkd_user_invite_member", 
uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id" }) 
})
public class UserInviteMember {

	private Long memberId;
	private Long orderId;
	private Long userId;
	private Boolean isDowngrade; // 是否已将级
	private Date endTime;        // 有效期
	private String batchNumber;  

	private Date createTime;
	private Date updateTime;

	@Id
	@GeneratedValue
	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	@Column(name = "order_id")
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

	@Column(name = "is_down_grade")
	public Boolean getIsDowngrade() {
		return isDowngrade;
	}

	public void setIsDowngrade(Boolean isDowngrade) {
		this.isDowngrade = isDowngrade;
	}

	@Column(name = "end_time")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "batch_number")
	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
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
