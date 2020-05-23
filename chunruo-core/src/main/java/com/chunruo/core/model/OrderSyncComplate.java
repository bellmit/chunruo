package com.chunruo.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 订单同步erp完成任务表
 * @author chunruo
 */
@Entity
@Table(name = "jkd_order_sync_complate",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"order_id"})
})
public class OrderSyncComplate implements Serializable{
	private static final long serialVersionUID = 4585738000953055182L;
	private Long syncId;
	private Long orderId; 				//序号
	private int syncNumber;				//同步次数
	private String batchNumber;			//批次号
	private Date createTime; 			//创建时间
	private Date updateTime; 			//更新时间
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getSyncId() {
		return syncId;
	}

	public void setSyncId(Long syncId) {
		this.syncId = syncId;
	}
	
	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}
	
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Column(name="sync_number", columnDefinition = "INT DEFAULT 0")
	public int getSyncNumber() {
		return syncNumber;
	}
	
	public void setSyncNumber(int syncNumber) {
		this.syncNumber = syncNumber;
	}
	
	@Column(name="batch_number", length=50)
	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
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