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
 * 用户标签关系
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_user_friend_tag", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "user_id","tag_id","im_user_id"}) })
public class UserFriendTag {

	private Long id;
	private Long userId;
	private Long imUserId;
	private Long tagId;
	private Date createTime;
	private Date updateTime;
	
	@Transient
	private String nickName;
	private String headerImage;
	private String tagName;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "im_user_id")
	public Long getImUserId() {
		return imUserId;
	}

	public void setImUserId(Long imUserId) {
		this.imUserId = imUserId;
	}

	@Column(name = "tag_id")
	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
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
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	@Transient
	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	@Transient
	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

}
