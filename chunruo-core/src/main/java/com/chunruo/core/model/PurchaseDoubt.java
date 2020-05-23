package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "jkd_purchase_doubt")
public class PurchaseDoubt {
	private Long doubtId;        // 答疑id
	private String title;        // 答疑标题
	private String description;  // 答疑描述
	private Integer sort;        // 排序
	private Date createTime; 
	private Date updateTime;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getDoubtId() {
		return doubtId;
	}

	public void setDoubtId(Long doubtId) {
		this.doubtId = doubtId;
	}
	
	@Column(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "sort")
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
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
