package com.chunruo.core.model;

import java.io.Serializable;
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
 * 店铺金额变更记录
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_user_amount_change_record", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"object_id", "type"})
})
public class UserAmountChangeRecord implements Serializable{
	private static final long serialVersionUID = -7283558303338195453L;
	public static final Integer AMOUNT_CHANGE_DRAWAL = 1;	//提现
	public static final Integer AMOUNT_CHANGE_CHECK = 2;	//结算
	public static final Integer AMOUNT_CHANGE_RECHARGE = 3; //充值（集币）
	public static final Integer AMOUNT_CHANGE_DRAWAL_FAIL = 4;//提现失败
	public static final Integer AMOUNT_CHANGE_TASK_PRODUCT = 5; //商品任务奖励
	public static final Integer AMOUNT_CHANGE_ACCOUNT = 6; //充值（账户余额）
	public static final Integer AMOUNT_CHANGE_CLEAN = 7; //集币清零

	
	private Long id;
    private Long userId; 					//用户ID
    private Integer type;					//变更类型 1-提现，2-结算，3-充值
    private Long objectId;					//操作对象ID
    private Double beforeAmount;			//变更前金额
    private Double changeAmount;			//变更金额
    private Double afterAmount;				//变更后金额
    private Date createTime;				//创建时间
    private Date updateTime;				//更改时间
   
    //@Transient
    private String storeName;               //店铺名称
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name="type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	@Column(name="object_id")
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	@Column(name="before_amount")
	public Double getBeforeAmount() {
		return beforeAmount;
	}

	public void setBeforeAmount(Double beforeAmount) {
		this.beforeAmount = beforeAmount;
	}
	
	@Column(name="change_amount")
	public Double getChangeAmount() {
		return changeAmount;
	}

	public void setChangeAmount(Double changeAmount) {
		this.changeAmount = changeAmount;
	}
	
	@Column(name="after_amount")
	public Double getAfterAmount() {
		return afterAmount;
	}

	public void setAfterAmount(Double afterAmount) {
		this.afterAmount = afterAmount;
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
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
}