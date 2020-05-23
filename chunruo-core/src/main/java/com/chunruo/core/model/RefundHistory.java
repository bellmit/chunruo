package com.chunruo.core.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 物流操作日志
 * @author chunruo
 */
@Entity
@Table(name = "jkd_refund_history")
public class RefundHistory implements java.io.Serializable {
	private static final long serialVersionUID = -3786000279860981978L;
	private Long historyId;
	private long refundId;
	private String title;
	private String content;
	private String adminName;
	private Long userId;
	private Date createTime;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getHistoryId() {
		return this.historyId;
	}

	public void setHistoryId(Long historyId) {
		this.historyId = historyId;
	}

	@Column(name = "refund_id", nullable = false)
	public long getRefundId() {
		return this.refundId;
	}

	public void setRefundId(long refundId) {
		this.refundId = refundId;
	}

	@Column(name = "title")
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "content")
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "admin_name")
	public String getAdminName() {
		return this.adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
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
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
