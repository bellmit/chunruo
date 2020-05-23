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
 * 顾问标签
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_user_adviser_tag", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"name"}) })
public class UserAdviserTag {

	private Long tagId;
	private String name;
	private Integer sort;

    private Boolean isEnable;
	
	private Date createTime;
	private Date updateTime;
	
	@Transient
	private Boolean isSelected;   //是否选中
	private Integer friendNumber;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	@Column(name = "sort")
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "is_enable", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
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
	public Integer getFriendNumber() {
		return friendNumber;
	}
	
	public void setFriendNumber(Integer friendNumber) {
		this.friendNumber = friendNumber;
	}

	@Transient
	public Boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}

}
