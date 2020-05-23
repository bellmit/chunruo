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
 * 用户签到统计
 * @author hehai
 */
@Entity
@Table(name = "jkd_sign",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id"})
})
public class Sign {
	private Long signId; // 签到id
	private Long userId; // 用户id
	private Integer ContinuedDays; // 连续签到天数
	private Integer shareCount; // 分享次数统计
	private Integer signIntegral; // 积分
	private Date createTime; // 创建时间
	private Date updateTime; // 更新时间
	
	@Transient
	private String mobile;
	private String nickName;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getSignId() {
		return signId;
	}

	public void setSignId(Long signId) {
		this.signId = signId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "continued_days")
	public Integer getContinuedDays() {
		return ContinuedDays;
	}

	public void setContinuedDays(Integer continuedDays) {
		ContinuedDays = continuedDays;
	}

	@Column(name = "share_count")
	public Integer getShareCount() {
		return shareCount;
	}

	public void setShareCount(Integer shareCount) {
		this.shareCount = shareCount;
	}

	@Column(name = "sign_integral")
	public Integer getSignIntegral() {
		return signIntegral;
	}

	public void setSignIntegral(Integer signIntegral) {
		this.signIntegral = signIntegral;
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
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Transient
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	
	
}
