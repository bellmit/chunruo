package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 商品售罄通知
 * 
 * @author Administrator
 *
 */
@Entity
@Table(name="jkd_product_soldout_notice",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"product_id", "product_spec_id","user_id"})
})
public class ProductSoldOutNotice {

	private Integer noticeId;
	private Long productId;
	private Long productSpecId;
	private Long userId;
	private Boolean isNotice; // 是否已提醒
	private Date createTime;
	private Date updateTime;

	@Id
	@GeneratedValue
	public Integer getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(Integer noticeId) {
		this.noticeId = noticeId;
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

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "is_notice", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsNotice() {
		return isNotice;
	}

	public void setIsNotice(Boolean isNotice) {
		this.isNotice = isNotice;
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
