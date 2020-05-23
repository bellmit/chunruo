package com.chunruo.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 会员年限模板
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_member_years_template")
public class MemberYearsTemplate {

	public static final Long FREE_MEMBER_YEAARS_TEMPLATE_ID = -1L;  //试用经销商上年限模板id
	private Long templateId;
	private Double yearsNumber; // 会员年限
	private String yearsName;   // 会员名称
	private Double price;       // 会员价格
	private Double profit;      // 会员邀请返利
	private Integer level;      // 会员等级
	private Boolean status;     // 是否启用
	private Integer sort;       // 排序
	private Boolean isDelete;   // 是否删除

	private Date createTime;
	private Date updateTime;
	
	
	private Double accountAmount;   //用户账户余额
	private Double payAccountAmount;//账户余额支付
	private Double payAmount;       //实际支付(不包含使用账户余额)
	private Double realPayAmount;   //实际支付(包含使用账户余额)
	private Boolean isNeedSendCode; //是否需要发送验证码
	private String tag;
	private String unitPrice;  
	private List<MemberGift> memberGiftList = new ArrayList<MemberGift>();
	
	private Boolean isFreeTemplate = false;     //是否试用经销商模板
	private Boolean isTimeout;                  //是否过期
	private Long endTime;                       //试用经销商截止时间

	@Id
	@GeneratedValue
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	@Column(name = "years_number")
	public Double getYearsNumber() {
		return yearsNumber;
	}

	public void setYearsNumber(Double yearsNumber) {
		this.yearsNumber = yearsNumber;
	}

	@Column(name = "years_name")
	public String getYearsName() {
		return yearsName;
	}

	public void setYearsName(String yearsName) {
		this.yearsName = yearsName;
	}

	@Column(name = "price")
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Column(name = "profit")
	public Double getProfit() {
		return profit;
	}

	public void setProfit(Double profit) {
		this.profit = profit;
	}

	@Column(name = "level")
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Column(name = "status", columnDefinition="bit default true")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Column(name = "is_delete", columnDefinition="bit default false")
	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Column(name = "sort")
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
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
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Transient
	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	@Transient
	public List<MemberGift> getMemberGiftList() {
		return memberGiftList;
	}

	public void setMemberGiftList(List<MemberGift> memberGiftList) {
		this.memberGiftList = memberGiftList;
	}

	@Transient
	public Boolean getIsFreeTemplate() {
		return isFreeTemplate;
	}

	public void setIsFreeTemplate(Boolean isFreeTemplate) {
		this.isFreeTemplate = isFreeTemplate;
	}

	@Transient
	public Boolean getIsTimeout() {
		return isTimeout;
	}

	public void setIsTimeout(Boolean isTimeout) {
		this.isTimeout = isTimeout;
	}

	@Transient
	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	@Transient
	public Double getAccountAmount() {
		return accountAmount;
	}

	public void setAccountAmount(Double accountAmount) {
		this.accountAmount = accountAmount;
	}

	@Transient
	public Double getPayAccountAmount() {
		return payAccountAmount;
	}

	public void setPayAccountAmount(Double payAccountAmount) {
		this.payAccountAmount = payAccountAmount;
	}

	@Transient
	public Double getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(Double payAmount) {
		this.payAmount = payAmount;
	}

	@Transient
	public Double getRealPayAmount() {
		return realPayAmount;
	}

	public void setRealPayAmount(Double realPayAmount) {
		this.realPayAmount = realPayAmount;
	}

	@Transient
	public Boolean getIsNeedSendCode() {
		return isNeedSendCode;
	}

	public void setIsNeedSendCode(Boolean isNeedSendCode) {
		this.isNeedSendCode = isNeedSendCode;
	}

}
