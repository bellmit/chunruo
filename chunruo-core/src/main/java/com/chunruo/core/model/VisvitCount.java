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
 * 活动访问量统计
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_visvit_count",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id"})
})
public class VisvitCount {
	private Long visvitId;
	private Long userId;
	private Date createTime;
	private Date updateTime;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getVisvitId() {
		return visvitId;
	}
	public void setVisvitId(Long visvitId) {
		this.visvitId = visvitId;
	}
	
	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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
