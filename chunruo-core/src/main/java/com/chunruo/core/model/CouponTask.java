package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/** 
 * 优惠券任务列表
 * @author Alex
 */
@Entity
@Table(name = "jkd_coupon_task")
public class CouponTask {
	public final static Long TASK_NAME_FIRST_LOGIN = 1L;        	//店长注册后首次登录APP(一次性)
	public final static Long TASK_NAME_FIRST_BUY_PACKGE = 2L; 	    //第一次购买升级礼包(一次性)
	public final static Long TASK_NAME_INVITE_TEAM = 3L; 		    //邀请5位代理商
	public final static Long TASK_NAME_TOTAL_SHARE = 4L;			//累计分享次数(一次性)
	public final static Long TASK_NAME_UPLOAD_CARD = 5L;			//上传二维码(一次性)
	public final static Long TASK_NAME_WEIXIN_FLOCK = 11L;		    //进官方微信群(一次性)
	public final static Long TASK_NAME_INVITE_VIP = 12L;			//vip邀请用户成功
	public final static Long TASK_NAME_VOTE_WIN = 13L;			    //投票赢礼包
	public final static Long TASK_NAME_VOTE_LOSE = 14L;			    //投票输礼包
	public final static Long TASK_NAME_VOTE_JOIN = 15L;			    //参加投票礼包
	public final static Long TASK_NAME_ACTIVITY_COACH = 16L;		//教练认证(开团)礼包
	public final static Long TASK_NAME_ACTIVITY_VOTE = 17L;		    //报名送券(投票活动)
	public final static Long TASK_NAME_EVALUATE_EXCELLENT = 18L;    //评价(精选)
	
	public final static int TASK_STATUS_OFF = 0;
	public final static int TASK_STATUS_ON = 1;
	private Long taskId;                 	//任务id
	private String taskName;             	//任务名称  
	private Integer taskStatus;           	//状态 0,禁用，1，启用
	private Long couponId;                	//关联优惠券ID
	private String taskContent;           	//任务内容(只针对需要量化的任务，一般不需要)
	private Date createTime;
	private Date updateTime;

	//Transient
	private Coupon coupon;
	private String hadComplete;      		//已经完成量
	private Boolean isComplete;				//是否已完成
	private Boolean isShowGet;				//是否显示领取
	private String taskTime; 				//优惠券任务时间(YYYY-MM-DD)
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	@Column(name = "task_name")
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@Column(name = "task_status")
	public Integer getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}

	@Column(name = "coupon_id")
	public Long getCouponId() {
		return couponId;
	}

	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}

	@Column(name = "task_content")
	public String getTaskContent() {
		return taskContent;
	}

	public void setTaskContent(String taskContent) {
		this.taskContent = taskContent;
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
	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}

	@Transient
	public String getHadComplete() {
		return hadComplete;
	}

	public void setHadComplete(String hadComplete) {
		this.hadComplete = hadComplete;
	}

	@Transient
	public Boolean getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(Boolean isComplete) {
		this.isComplete = isComplete;
	}

	@Transient
	public Boolean getIsShowGet() {
		return isShowGet;
	}

	public void setIsShowGet(Boolean isShowGet) {
		this.isShowGet = isShowGet;
	}

	@Transient
	public String getTaskTime() {
		return taskTime;
	}

	public void setTaskTime(String taskTime) {
		this.taskTime = taskTime;
	}

}
