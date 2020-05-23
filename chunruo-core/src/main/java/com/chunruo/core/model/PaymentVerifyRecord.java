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
 * 余额支付认证记录
 * 只保留请求最后一次记录
 * @author chunruo
 */
@Entity
@Table(name="jkd_payment_verify_record", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id"})
})
public class PaymentVerifyRecord {
	public final static int SMS_PAYMENT_VERIFY = 1; 		//短信认证
	public final static int PASSWORD_PAYMENT_VERIFY = 2; 	//安全密码认证
	private Long recordId;
	private Long userId;
	private Integer verifyType;
	private Date verifyTime;
	private Date createTime;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@Column(name="verify_type")
	public Integer getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(Integer verifyType) {
		this.verifyType = verifyType;
	}

	@Column(name="verify_time")
	public Date getVerifyTime() {
		return verifyTime;
	}

	public void setVerifyTime(Date verifyTime) {
		this.verifyTime = verifyTime;
	}

	@Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
