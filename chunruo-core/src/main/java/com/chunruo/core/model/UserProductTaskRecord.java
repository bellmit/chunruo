package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * 用户商品任务统计
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_user_product_task_record",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"task_id", "user_id"})
})
public class UserProductTaskRecord {

	public static final Integer USER_PRODUCT_TASK_STATUS_WAIT = 0;        //待获奖
	public static final Integer USER_PRODUCT_TASK_STATUS_UNAWARDED = 1;   //未获奖
	public static final Integer USER_PRODUCT_TASK_STATUS_AWARDED = 2;     //已获奖
	private Long recordId;
	private Long userId;          // 用户id
	private Long taskId;          // 任务id
	private Integer totalNumber;  // 购买任务商品总数量
	private Integer groupNumber;  // 组数
	private Integer totalReward;  // 总返利
	private Integer status;       // 0:待获奖 1：未获奖 2：已获奖

	private Date createTime;
	private Date updateTime;
	
	private ProductTask productTask;
	
	private String userName;
	private String taskName;
	

	@Id
	@GeneratedValue
	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "task_id")
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	@Column(name = "total_number")
	public Integer getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}

	@Column(name = "group_number")
	public Integer getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(Integer groupNumber) {
		this.groupNumber = groupNumber;
	}

	@Column(name = "total_reward")
	public Integer getTotalReward() {
		return totalReward;
	}

	public void setTotalReward(Integer totalReward) {
		this.totalReward = totalReward;
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
	public ProductTask getProductTask() {
		return productTask;
	}

	public void setProductTask(ProductTask productTask) {
		this.productTask = productTask;
	}

	@Transient
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Transient
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
