package com.chunruo.core.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 提现操作历史记录
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_user_withdrawal_history")
public class UserWithdrawalHistory{
	private Long historyId;
	private Long recordId;			//关联ID
	private Long userId;			//操作用户ID
	private String name;			//操作名称
	private String message;			//详细描述
	private Date createTime;		//创建时间

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getHistoryId() {
		return historyId;
	}

	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
	}

	@Column(name="record_id")
	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	@Column(name="user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="message", length=1000)
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
