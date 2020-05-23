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
 * 支付报关记录
 * @author chunruo
 */
@Entity
@Table(name = "jkd_order_customs_record", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"order_no"}),
	@UniqueConstraint(columnNames = {"trade_no"})
})
public class OrderCustomsRecord {
	private Long recordId;
	private Long orderId;				//订单序号
	private String orderNo;				//订单号
	private String tradeNo; 			//支付单号
	private String idCardName;			//真实姓名
	private String idCardNo;			//身份证号
	private Double payAmount; 			//订单支付金额
	private Double orderTax; 			//订单税额
	private Double postage;				//订单邮费
	private Integer paymentType;		//支付类型
	private Long weChatConfigId; 		//微信支付ConfigId
	private Boolean isPushCustomSucc; 	//是否推送成功
	private String customs;				//海关名称(NINGBO 宁波)
	private String mchCustomsNo;		//商户在海关登记的备案号
	private String mchCustomsCode;		//商户海关备案编号
	private int syncNumber;				//同步次数
	private String batchNumber;			//批次号
	private String errorMsg;			//错误信息
	private Date createTime;			//创建时间
	private Date updateTime;			//更新时间
	
	@Transient
	private String weChatConfigName;	//微信支付名称
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getRecordId() {
		return recordId;
	}
	
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	
	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Column(name = "pay_amount")
	public Double getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(Double payAmount) {
		this.payAmount = payAmount;
	}
	
	@Column(name = "order_no")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name = "trade_no")
	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	@Column(name = "id_card_name")
	public String getIdCardName() {
		return idCardName;
	}

	public void setIdCardName(String idCardName) {
		this.idCardName = idCardName;
	}

	@Column(name = "id_card_no")
	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	@Column(name = "order_tax")
	public Double getOrderTax() {
		return orderTax;
	}

	public void setOrderTax(Double orderTax) {
		this.orderTax = orderTax;
	}

	@Column(name = "postage")
	public Double getPostage() {
		return postage;
	}

	public void setPostage(Double postage) {
		this.postage = postage;
	}

	@Column(name = "payment_type")
	public Integer getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}
	
	@Column(name = "we_chat_config_id")
	public Long getWeChatConfigId() {
		return weChatConfigId;
	}

	public void setWeChatConfigId(Long weChatConfigId) {
		this.weChatConfigId = weChatConfigId;
	}

	@Column(name = "is_push_custom_succ")
	public Boolean getIsPushCustomSucc() {
		return isPushCustomSucc;
	}

	public void setIsPushCustomSucc(Boolean isPushCustomSucc) {
		this.isPushCustomSucc = isPushCustomSucc;
	}
	
	@Column(name="customs")
	public String getCustoms() {
		return customs;
	}

	public void setCustoms(String customs) {
		this.customs = customs;
	}

	@Column(name="mch_customs_no")
	public String getMchCustomsNo() {
		return mchCustomsNo;
	}

	public void setMchCustomsNo(String mchCustomsNo) {
		this.mchCustomsNo = mchCustomsNo;
	}

	@Column(name="mch_customs_code")
	public String getMchCustomsCode() {
		return mchCustomsCode;
	}

	public void setMchCustomsCode(String mchCustomsCode) {
		this.mchCustomsCode = mchCustomsCode;
	}

	@Column(name="sync_number", columnDefinition = "INT DEFAULT 0")
	public int getSyncNumber() {
		return syncNumber;
	}

	public void setSyncNumber(int syncNumber) {
		this.syncNumber = syncNumber;
	}
	
	@Column(name="batch_number", length=250)
	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	
	@Column(name="error_msg", length=250)
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
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
	public String getWeChatConfigName() {
		return weChatConfigName;
	}

	public void setWeChatConfigName(String weChatConfigName) {
		this.weChatConfigName = weChatConfigName;
	}
	
	
}
