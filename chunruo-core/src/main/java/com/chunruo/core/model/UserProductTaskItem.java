package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Transient;

/**
 * 用户任务商品明细
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_user_product_task_item",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"order_item_id"})
})
public class UserProductTaskItem {
	public static final Integer USER_PRODUCT_TASK_STATUS_WAITTING = 0;   //结算中
	public static final Integer USER_PRODUCT_TASK_STATUS_SUCC = 1;       //完成
	public static final Integer USER_PRODUCT_TASK_STATUS_FAIL = 2;       //失败
	
	private Long itemId;
	private Long userId;
	private Long orderId;
	private Long orderItemId;
	private Long productId;
	private Long productSpecId;
	private Long taskId;
	private Integer quantity;   //购买数量
	private Integer status;    //  0:结算中 1:完成 2：失败
	private Date createTime;
	private Date updateTime;
	
	//Transient
	private Integer taskNumber;          //任务数量
	private Integer reward;              //每组返利
	private Integer maxGroupNumber;      //最大组数

	@Id
	@GeneratedValue
	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Column(name = "order_item_id")
	public Long getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}

	@Column(name = "product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "product_spec_id")
	public Long getProductSpecId() {
		return productSpecId;
	}

	public void setProductSpecId(Long productSpecId) {
		this.productSpecId = productSpecId;
	}

	@Column(name = "task_id")
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	@Column(name = "quantity")
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
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

	@Transient
	public Integer getTaskNumber() {
		return taskNumber;
	}

	public void setTaskNumber(Integer taskNumber) {
		this.taskNumber = taskNumber;
	}

	@Transient
	public Integer getReward() {
		return reward;
	}

	public void setReward(Integer reward) {
		this.reward = reward;
	}

	@Transient
	public Integer getMaxGroupNumber() {
		return maxGroupNumber;
	}

	public void setMaxGroupNumber(Integer maxGroupNumber) {
		this.maxGroupNumber = maxGroupNumber;
	}
}
