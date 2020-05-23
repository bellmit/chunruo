package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 短信发送记录
 * @author chunruo
 */
@Entity
@Table(name = "jkd_sms_send_record")
public class SmsSendRecord implements java.io.Serializable {
	private static final long serialVersionUID = -6490242699173329166L;
	private Long recordId;
	private String mobile;
	private String content;
	private Boolean status;
	private Date createTime;
	private Date updateTime;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getRecordId() {
		return recordId;
	}
	
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	
	@Column(name = "mobile", length=20)
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Column(name = "content", length=100)
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	@Column(name = "status")
	public Boolean getStatus() {
		return status;
	}
	
	public void setStatus(Boolean status) {
		this.status = status;
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
