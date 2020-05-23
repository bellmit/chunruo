package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户签到记录
 * @author hehai
 */
@Entity
@Table(name = "jkd_sign_record")
public class SignRecord {
	private Long recordId;   // 记录id
	private Long userId;     // 用户id
	private String signDate; // 签到日期
	private Date createTime; // 创建时间
	private Date updateTime; // 更新时间

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getSingId() {
		return recordId;
	}

	public void setSingId(Long recordId) {
		this.recordId = recordId;
	}

	@Column(name="user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name="sign_date")
	public String getSignDate() {
		return signDate;
	}

	public void setSignDate(String signDate) {
		this.signDate = signDate;
	}

	@Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
