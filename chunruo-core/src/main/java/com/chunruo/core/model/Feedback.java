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


@Entity
@Table(name = "jkd_feedback")
public class Feedback implements Serializable{
	private static final long serialVersionUID = 262233985810324191L;
	public static final String APP = "APP";

	public static final Integer NOREPLY = 0;//未回复
	public static final Integer REPLYED = 1;//已回复

	private Long feedbackId;		// 反馈ID
	private Long userId;			// 用户ID
	private String mobile;			// 手机号
	private String ftype;			// 类型，默认：App
	private String content;			// 反馈内容
	private String uuid;			// 用户设备标识
	private String userIp;			// 用户IP
	private Integer isReply;		// 是否回复，0：未回复，1：已回复
	private Boolean isPushUser;		// 是否提送用户
	private String replyMsg;		// 回复内容
	private Date createTime;		// 创建时间
	private Date updateTime;		// 更新时间


	//@Transient
	private String userName;        //用户名称

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(Long feedbackId) {
		this.feedbackId = feedbackId;
	}

	@Column(name = "ftype", nullable = false, length = 10)
	public String getFtype() {
		return ftype;
	}

	public void setFtype(String ftype) {
		this.ftype = ftype;
	}

	@Column(name = "user_id", length = 20, nullable = false)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "content", nullable = false, length = 1024)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "uuid", length = 100)
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Column(name = "user_ip", length = 20)
	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	@Column(name = "is_reply", length = 11, nullable = false)
	public Integer getIsReply() {
		return isReply;
	}

	public void setIsReply(Integer isReply) {
		this.isReply = isReply;
	}

	@Column(name = "is_push_user", length = 2, nullable = false, columnDefinition = "INT DEFAULT 0")
	public Boolean getIsPushUser() {
		return isPushUser;
	}

	public void setIsPushUser(Boolean isPushUser) {
		this.isPushUser = isPushUser;
	}

	@Column(name = "reply_msg", length = 1024)
	public String getReplyMsg() {
		return replyMsg;
	}

	public void setReplyMsg(String replyMsg) {
		this.replyMsg = replyMsg;
	}

	@Column(name = "create_time", nullable = false, length = 19)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="update_time", length = 19)
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name="mobile")
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Transient
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
