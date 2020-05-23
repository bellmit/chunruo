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
 * 秒杀场次
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_product_seckill")
public class ProductSeckill {
	public final static Integer SECKILL_TYPE_START = 0;		//已开抢
	public final static Integer SECKILL_TYPE_ROBING = 1;		//疯抢中
	public final static Integer SECKILL_TYPE_READY = 2;		//即将开始
	private Long seckillId;
	private String seckillName;
	private String startTime;
	private Boolean status;
	private Boolean isDelete;
	private String endTime;
	private Date createTime;
	private Date updateTime;
	
	//Transient
	private Integer seckillStatus;
	private String statusName;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getSeckillId() {
		return seckillId;
	}
	
	public void setSeckillId(Long seckillId) {
		this.seckillId = seckillId;
	}
	
	@Column(name="seckill_name")
	public String getSeckillName() {
		return seckillName;
	}
	
	public void setSeckillName(String seckillName) {
		this.seckillName = seckillName;
	}
	
	@Column(name="status", columnDefinition = "INT DEFAULT 0")
	public Boolean getStatus() {
		return status;
	}
	
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	@Column(name="is_delete", columnDefinition = "INT DEFAULT 0")
	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Column(name="start_time")
	public String getStartTime() {
		return startTime;
	}
	
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	@Column(name="end_time")
	public String getEndTime() {
		return endTime;
	}
	
	public void setEndTime(String endTime) {
		this.endTime = endTime;
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
	public Integer getSeckillStatus() {
		return seckillStatus;
	}

	public void setSeckillStatus(Integer seckillStatus) {
		this.seckillStatus = seckillStatus;
	}

	@Transient
	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
}
