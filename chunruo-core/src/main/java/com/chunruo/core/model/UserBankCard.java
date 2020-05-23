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
 * 用户银行卡
 * 易生支付
 * @author chunruo
 */
@Entity
@Table(name="jkd_user_bank_card",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "bank_card_no"})
})
public class UserBankCard {
	private Long bankCardId;	//序号
	private Long userId;		//用户ID
	private String bankCardNo;	//银行开号
	private int cardType;      	//卡类型: 1储蓄卡;2信用卡
	private Boolean isDefault;	//是否默认卡
	private Date createTime;	//创建时间
	private Date updateTime;	//更新时间
	
	@Transient
	private String outTradeNo; //订单的订单编号
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getBankCardId() {
		return bankCardId;
	}

	public void setBankCardId(Long bankCardId) {
		this.bankCardId = bankCardId;
	}

	@Column(name="user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name="bank_card_no")
	public String getBankCardNo() {
		return bankCardNo;
	}

	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}
	
	@Column(name="is_default", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Column(name="card_type")
	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	@Column(name="create_time")
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
	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	
	
}
