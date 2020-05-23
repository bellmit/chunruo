package com.chunruo.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;
/**
 * 商品任务
 * @author Administrator
 */
@Entity
@Table(name = "jkd_product_task")
public class ProductTask implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final Integer PRODUCT_TASK_STATUS_NOTSTART = 1;  //即将开始
	public static final Integer PRODUCT_TASK_STATUS_START = 2;     //已开始
	
	private Long taskId;
	private String taskName;             //任务名称
	private Long productId;              //商品id
	private String productName;          //商品名称
	private String imagePath;            //商品图片
	private Integer taskNumber;          //任务数量
	private Integer reward;              //每组返利
	private Integer maxGroupNumber;      //最大组数
	private Date beginTime;              //开始时间
	private Date endTime;                //结束时间
	private Boolean isEnable;            //是否启用
	private Boolean isDelete;            //是否删除
	private Date createTime;
	private Date updateTime;
	
	private Integer taskStatus;          //任务状态
    private String buttonTitle;          //按钮标题   
    private String rewardNotes;          //奖励说明
    private String rewardTag;            //奖励标签
    private String purchaseIntroduce;    //已购介绍
    private String taskRule;             //任务规则
    private Integer headNumber;          //进度条头节点
    private Integer tailNumber;          //进度条尾节点
    private Integer salesNumber;         //销量
    private Integer targetReward;        //目标奖励    
    private Boolean isCompleted = false; //任务是否完成
    private Integer obtainReward;        //已获得奖励
    private Integer nextNumber;          //再卖数量
    private Integer groupNumber;         //已销组数
    private String sort;				 //排序字段(默认taskId升序)
    
	@Id
	@GeneratedValue
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

	@Column(name = "product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "product_name")
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Column(name = "image_path")
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Column(name = "task_number")
	public Integer getTaskNumber() {
		return taskNumber;
	}

	public void setTaskNumber(Integer taskNumber) {
		this.taskNumber = taskNumber;
	}

	@Column(name = "reward")
	public Integer getReward() {
		return reward;
	}

	public void setReward(Integer reward) {
		this.reward = reward;
	}

	@Column(name = "max_group_number")
	public Integer getMaxGroupNumber() {
		return maxGroupNumber;
	}

	public void setMaxGroupNumber(Integer maxGroupNumber) {
		this.maxGroupNumber = maxGroupNumber;
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "begin_time")
	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "end_time")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "is_enable")
	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	@Column(name = "is_delete")
	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
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
	public Integer getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}

	@Transient
	public String getButtonTitle() {
		return buttonTitle;
	}

	public void setButtonTitle(String buttonTitle) {
		this.buttonTitle = buttonTitle;
	}

	@Transient
	public String getRewardNotes() {
		return rewardNotes;
	}

	public void setRewardNotes(String rewardNotes) {
		this.rewardNotes = rewardNotes;
	}

	@Transient
	public Integer getHeadNumber() {
		return headNumber;
	}

	public void setHeadNumber(Integer headNumber) {
		this.headNumber = headNumber;
	}

	@Transient
	public Integer getTailNumber() {
		return tailNumber;
	}

	public void setTailNumber(Integer tailNumber) {
		this.tailNumber = tailNumber;
	}

	@Transient
	public Integer getSalesNumber() {
		return salesNumber;
	}

	public void setSalesNumber(Integer salesNumber) {
		this.salesNumber = salesNumber;
	}

	@Transient
	public Integer getTargetReward() {
		return targetReward;
	}

	public void setTargetReward(Integer targetReward) {
		this.targetReward = targetReward;
	}

	@Transient
	public String getRewardTag() {
		return rewardTag;
	}

	public void setRewardTag(String rewardTag) {
		this.rewardTag = rewardTag;
	}

	@Transient
	public String getPurchaseIntroduce() {
		return purchaseIntroduce;
	}

	public void setPurchaseIntroduce(String purchaseIntroduce) {
		this.purchaseIntroduce = purchaseIntroduce;
	}

	@Transient
	public String getTaskRule() {
		return taskRule;
	}

	public void setTaskRule(String taskRule) {
		this.taskRule = taskRule;
	}

	@Transient
	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	@Transient
	public Integer getObtainReward() {
		return obtainReward;
	}

	public void setObtainReward(Integer obtainReward) {
		this.obtainReward = obtainReward;
	}

	@Transient
	public Integer getNextNumber() {
		return nextNumber;
	}

	public void setNextNumber(Integer nextNumber) {
		this.nextNumber = nextNumber;
	}

	@Transient
	public Integer getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(Integer groupNumber) {
		this.groupNumber = groupNumber;
	}

	@Transient
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
}
