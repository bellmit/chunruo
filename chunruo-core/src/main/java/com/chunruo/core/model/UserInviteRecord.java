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
 * 用户邀请续费记录
 * @author chunruo
 *
 */
@Entity
@Table(name = "jkd_user_invite_record",uniqueConstraints = {
		@UniqueConstraint(columnNames = {"order_no"})
	})
public class UserInviteRecord {
	public static Integer RECORD_TYPE_RENEW = 1;		//续费类型
	public static Integer RECORD_TYPE_FIRST = 2;		//第一次购买
	public static Integer VIP_TYPE_AGENT = 1;			//代理商
	public static Integer VIP_TYPE_SPECIAL_DEALER = 2;	//平台总代
	public static Integer VIP_TYPE_BUYERS = 3;			//店长
	
	private Long recordId;
	private String recordNo;			//记录编号
	private String orderNo;				//关联订单编号
	private Long userId;				//用户id
	private Long topUserId;				//上级用户id
	private Double number;              //购买大礼包数量
	private Integer recordType;			//记录类型 1-续费 2-首次购买
	private Integer inviteType;			//邀请类型 1-普通代理 2-总代 3-店长
	private Boolean isPaymentSucc;		//是否支付成功(false 未支付 true 已支付)
	private Double costAmount;			//费用金额
	private Double profitAmount;		//返利金额
	private String tradeNo; 			//商家交易流水号(纯若)
	private Integer paymentType; 		//支付方式(0:微信支付;1:支付宝支付)
	private String startDate;			//有效期开始时间
	private String endDate;				//有效期结束时间
	private Date createTime;			//创建时间
	private Date updateTime;			//更新时间
	
	@Transient
	private String mobile;              //用户手机号码
	private String templateName;        //会员年限模板名称
	private String giftName;            //会员赠品名称
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "record_id", unique = true, nullable = false)
	public Long getRecordId() {
		return recordId;
	}
	
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	
	@Column(name = "record_no", nullable = false)
	public String getRecordNo() {
		return recordNo;
	}
	
	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}
	
	@Column(name = "order_no")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name = "user_id", nullable = false)
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name = "record_type", nullable = false)
	public Integer getRecordType() {
		return recordType;
	}
	
	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}
	
	@Column(name = "is_payment_succ", nullable = false)
	public Boolean getIsPaymentSucc() {
		return isPaymentSucc;
	}

	public void setIsPaymentSucc(Boolean isPaymentSucc) {
		this.isPaymentSucc = isPaymentSucc;
	}
	
	@Column(name = "invite_type")
	public Integer getInviteType() {
		return inviteType;
	}

	public void setInviteType(Integer inviteType) {
		this.inviteType = inviteType;
	}

	@Column(name = "cost_amount")
	public Double getCostAmount() {
		return costAmount;
	}

	public void setCostAmount(Double costAmount) {
		this.costAmount = costAmount;
	}
	
	@Column(name = "profit_amount")
	public Double getProfitAmount() {
		return profitAmount;
	}

	public void setProfitAmount(Double profitAmount) {
		this.profitAmount = profitAmount;
	}

	@Column(name = "trade_no")
	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	@Column(name = "payment_type")
	public Integer getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}
	
	@Column(name = "number")
	public Double getNumber() {
		return number;
	}

	public void setNumber(Double number) {
		this.number = number;
	}

	@Column(name = "top_user_id")
	public Long getTopUserId() {
		return topUserId;
	}

	public void setTopUserId(Long topUserId) {
		this.topUserId = topUserId;
	}
	
	@Column(name = "start_date", nullable = false)
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	@Column(name = "end_date", nullable = false)
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
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
	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@Transient
	public String getGiftName() {
		return giftName;
	}

	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}

}
