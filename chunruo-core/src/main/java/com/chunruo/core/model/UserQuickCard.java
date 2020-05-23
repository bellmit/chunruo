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
 * 快捷支付签名列表
 * @author chunruo
 */
@Entity
@Table(name="jkd_user_quick_card",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"bank_card_number", "payment_type"})
})
public class UserQuickCard {
	public final static int CARD_TYPE_LEND = 1; //借记卡
	public final static int CARD_TYPE_LOAN = 2; //信用卡
	private Long quickCardId;
	private Long userId;			//用户ID
	private String realName;		//真实姓名
	private String identityNo;		//身份证号码
	private Integer cardType;		//银行卡类型(1:借记卡;2:信用卡)
	private Integer paymentType;	//支付类型(上海汇付、易生支付)
	private Long bankType;			//开户银行
	private String bankCardNumber;	//银行卡号
	private String mobile;			//银行预留手机号码
	private String expiryDate;		//贷记卡有效期
	private String cvvNumber;		//贷记卡卡片安全码
	private String quickSignId;		//支付签约ID
	private Date createTime;
	private Date updateTime;

	@Transient
	private String bankName;
	private String cardTypeName;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getQuickCardId() {
		return quickCardId;
	}
	
	public void setQuickCardId(Long quickCardId) {
		this.quickCardId = quickCardId;
	}
	
	@Column(name="user_id")
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name="payment_type")
	public Integer getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}

	@Column(name="real_name")
	public String getRealName() {
		return realName;
	}
	
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	@Column(name="identity_no")
	public String getIdentityNo() {
		return identityNo;
	}
	
	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}
	
	@Column(name="card_type")
	public Integer getCardType() {
		return cardType;
	}
	
	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}
	
	@Column(name="bank_type")
	public Long getBankType() {
		return bankType;
	}
	
	public void setBankType(Long bankType) {
		this.bankType = bankType;
	}
	
	@Column(name="bank_card_number")
	public String getBankCardNumber() {
		return bankCardNumber;
	}
	
	public void setBankCardNumber(String bankCardNumber) {
		this.bankCardNumber = bankCardNumber;
	}
	
	@Column(name="mobile")
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Column(name="expiry_date")
	public String getExpiryDate() {
		return expiryDate;
	}
	
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	@Column(name="cvv_number")
	public String getCvvNumber() {
		return cvvNumber;
	}
	
	public void setCvvNumber(String cvvNumber) {
		this.cvvNumber = cvvNumber;
	}
	
	@Column(name="quick_sign_id")
	public String getQuickSignId() {
		return quickSignId;
	}
	
	public void setQuickSignId(String quickSignId) {
		this.quickSignId = quickSignId;
	}
	
	@Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name="update_time")
	public Date getUpdateTime() {
		return updateTime;
	}
	
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Transient
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Transient
	public String getCardTypeName() {
		return cardTypeName;
	}

	public void setCardTypeName(String cardTypeName) {
		this.cardTypeName = cardTypeName;
	}
}
