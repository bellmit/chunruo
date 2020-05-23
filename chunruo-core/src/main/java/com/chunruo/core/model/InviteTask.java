package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 邀请经商任务
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_invite_task")
public class InviteTask {
	
	public final static Integer INVITE_TASK_COUPON = 1 ;       //优惠券
	public final static Integer INVITE_TASK_JIBI = 2;          //集币
	public final static Integer INVITE_TASK_COUPON_JIBI = 3;   //优惠券&集币

	
	private Long taskId;
	private Integer number; // 邀请人数
	private Integer type; // 奖励类型（1：优惠券 2：集币 3：优惠券&集币）
	private String couponIds; // 赠送优惠券ids
	private Double amount; // 集币返利
	private String imagePath; // 图片
	private String inviteDesc; // 奖励描述

	private Date createTime;
	private Date updateTime;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	@Column(name = "number")
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "coupon_ids")
	public String getCouponIds() {
		return couponIds;
	}

	public void setCouponIds(String couponIds) {
		this.couponIds = couponIds;
	}

	@Column(name = "amount")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "image_path")
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Column(name = "invite_desc")
	public String getInviteDesc() {
		return inviteDesc;
	}

	public void setInviteDesc(String inviteDesc) {
		this.inviteDesc = inviteDesc;
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
