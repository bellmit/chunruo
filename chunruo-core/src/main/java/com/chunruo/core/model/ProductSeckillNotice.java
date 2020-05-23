package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 秒杀提醒通知
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_product_seckill_notice",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"seckill_id", "product_id","user_id"})
})
public class ProductSeckillNotice {
	private Long noticeId;
	private Long productId;
	private Long seckillId;
	private String mobile;
	private Long userId;
	private Date noticeTime;
	private Date createTime;
	private Date updateTime;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getNoticeId() {
		return noticeId;
	}
	
	public void setNoticeId(Long noticeId) {
		this.noticeId = noticeId;
	}
	
	@Column(name="seckill_id")
	public Long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(Long seckillId) {
		this.seckillId = seckillId;
	}

	@Column(name="product_id")
	public Long getProductId() {
		return productId;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	@Column(name="mobile")
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Column(name="user_id")
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name="notice_time")
	public Date getNoticeTime() {
		return noticeTime;
	}
	
	public void setNoticeTime(Date noticeTime) {
		this.noticeTime = noticeTime;
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
}
