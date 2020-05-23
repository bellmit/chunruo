package com.chunruo.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "jkd_user_friend", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "friend_user_id"}) })
public class UserFriend {
	
	public final static Integer USER_FRIEND_STATUS_BIND = 1;    
	public final static Integer USER_FRIEDN_STATUC_REMOVE = 2;
	
	public final static Integer USER_FRIEND_TYPE_COMMON = 1;   //普通用户
	public final static Integer USER_FRIEND_TYPE_DECLARE = 2;  //经销商

	private Long friendId;
	private Long userId;         //顾问id
	private Long friendUserId;   //好友用户id
	private Integer status; // 1:是好友 2：已解除好友关系
	private String remark;  //备注
	private Integer type;    // 1:普通用户 2：经销商
	private Date createTime;
	private Date updateTime;
	
	@Transient
	private String adviserName;   //顾问名称
	private String nickName;
	private Integer level;
	private String expireEndDate; //有效期
	private Date registerTime;
	private String headerImage;
	private Boolean isBindTag = false;  //是否绑定该标签
	private String tagName;            //标签名称
	private List<Long> tagIdList = new ArrayList<Long>(); //标签id
	private String desc;             

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getFriendId() {
		return friendId;
	}

	public void setFriendId(Long friendId) {
		this.friendId = friendId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "friend_user_id")
	public Long getFriendUserId() {
		return friendUserId;
	}

	public void setFriendUserId(Long friendUserId) {
		this.friendUserId = friendUserId;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
	public Boolean getIsBindTag() {
		return isBindTag;
	}

	public void setIsBindTag(Boolean isBindTag) {
		this.isBindTag = isBindTag;
	}

	@Transient
	public String getAdviserName() {
		return adviserName;
	}

	public void setAdviserName(String adviserName) {
		this.adviserName = adviserName;
	}

	@Transient
	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	@Transient
	public List<Long> getTagIdList() {
		return tagIdList;
	}

	public void setTagIdList(List<Long> tagIdList) {
		this.tagIdList = tagIdList;
	}

	@Transient
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Transient
	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

	@Transient
	public String getExpireEndDate() {
		return expireEndDate;
	}

	public void setExpireEndDate(String expireEndDate) {
		this.expireEndDate = expireEndDate;
	}

	@Transient
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
