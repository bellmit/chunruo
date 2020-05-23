package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 集币充值
 * @author Administrator
 */
@Entity
@Table(name = "jkd_user_recharge")
public class UserRecharge implements Cloneable{

	public static final Integer USER_RECHARGE_MANAGER = 1;     //主管审核
	public static final Integer USER_RECHARGE_FIANCE = 2;      //财务审核
	public static final Integer USER_RECHARGE_SUCC = 3;        //审核通过
	public static final Integer USER_RECHARGE_REFUSE = 4;      //审核被拒
	private Long recordId; // 序号
	private String applicant; // 申请人
	private Double amount; // 充值金额
	private Long userId; // 充值用户
	private Integer status; // 审核状态（1：主管审核 2：财务审核 3：审核通过 4：审核被拒）
	private String profitNotice; // 收益通知
	private String reason; // 申请原因
	private String attachmentPath; // 附件地址
	private Date completeTime; // 完成时间
	private String adminName; //拒绝人
	private String refuseReason; //拒绝理由
	private Date createTime; // 创建时间
	private Date updateTime; // 更新时间

	private String mobile; // 用户手机号
	private String nickName; // 昵称

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	@Column(name = "applicant")
	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	@Column(name = "amount")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "profit_notice")
	public String getProfitNotice() {
		return profitNotice;
	}

	public void setProfitNotice(String profitNotice) {
		this.profitNotice = profitNotice;
	}

	@Column(name = "reason")
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Column(name = "attachment_path")
	public String getAttachmentPath() {
		return attachmentPath;
	}

	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}

	@Column(name = "complete_time")
	public Date getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Date completeTime) {
		this.completeTime = completeTime;
	}

	@Column(name = "admin_name")
	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	
	@Column(name = "refuse_reason")
	public String getRefuseReason() {
		return refuseReason;
	}

	public void setRefuseReason(String refuseReason) {
		this.refuseReason = refuseReason;
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
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Transient
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	@Override
	public UserRecharge clone(){
		//浅拷贝
		try {
			// 直接调用父类的clone()方法
			return (UserRecharge) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
