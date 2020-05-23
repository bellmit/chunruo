package com.chunruo.core.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 定时任务
 * 
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_schedule_task")
public class ScheduleTask {
	
	public static final Integer SCHEDULE_TASK_TYPE_MESSAGE = 1;  //推送消息
	public static final Integer SCHEDULE_TASK_TYPE_GOODS = 3;    //推送商品
	public static final Integer SCHEDULE_TASK_TYPE_SINGLE = 4;   //推送单个商品
	public static final Integer SCHEDULE_TASK_TYPE_COUPON = 5;   //发送优惠券
	private Long taskId;
	private Date beginTime;
	private Integer type;
	private Long objectId;
	private String levels;
	private Boolean isEnable;
	private Boolean isDelete;                 //是否删除
	private String adminUserName;            // 操作员


	private Date createTime;
	private Date updateTime;
	
	@Transient
	private Boolean isVip0;
	private Boolean isVip1;
	private Boolean isVip2;
	private Boolean isVip3;
	

	@Id
	@GeneratedValue
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	@Column(name = "begin_time")
	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "is_enable")
	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	@Column(name = "object_id")
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	@Column(name = "levels")
	public String getLevels() {
		return levels;
	}

	public void setLevels(String levels) {
		this.levels = levels;
	}

	@Column(name = "is_delete")
	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Column(name = "admin_user_name")
	public String getAdminUserName() {
		return adminUserName;
	}

	public void setAdminUserName(String adminUserName) {
		this.adminUserName = adminUserName;
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
	public Boolean getIsVip0() {
		return isVip0;
	}

	public void setIsVip0(Boolean isVip0) {
		this.isVip0 = isVip0;
	}

	@Transient
	public Boolean getIsVip1() {
		return isVip1;
	}

	public void setIsVip1(Boolean isVip1) {
		this.isVip1 = isVip1;
	}

	@Transient
	public Boolean getIsVip2() {
		return isVip2;
	}

	public void setIsVip2(Boolean isVip2) {
		this.isVip2 = isVip2;
	}

	@Transient
	public Boolean getIsVip3() {
		return isVip3;
	}

	public void setIsVip3(Boolean isVip3) {
		this.isVip3 = isVip3;
	}

}
