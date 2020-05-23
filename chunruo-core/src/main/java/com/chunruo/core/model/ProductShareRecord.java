package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "jkd_product_share_record",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"token"})
})
public class ProductShareRecord {
	public static Integer PRODUCT_SHARE_RECORD_PRODUCT = 1 ;
	public static Integer PRODUCT_SHARE_RECORD_DISCOVERY = 2;

	private Long recordId;
	private String token;            // 分享商品产生的唯一凭证
	private String shareInfo;        // 利润信息
	private Long userId;             // 分享用户
	private Long objectId;           // 商品id或发现id
	private Boolean isAggrProduct;   // 是否聚合商品
	private Integer type;            //1：分享商品 2：分享发现
	private Integer platformType;    //平台类型 ： 1：订单微管家小程序 2：纯若小程序
	private Date createTime;
	private Date updateTime;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	@Column(name = "token")
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Column(name = "share_user_info")
	public String getShareInfo() {
		return shareInfo;
	}

	public void setShareInfo(String shareInfo) {
		this.shareInfo = shareInfo;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "is_aggr_product")
	public Boolean getIsAggrProduct() {
		return isAggrProduct;
	}

	public void setIsAggrProduct(Boolean isAggrProduct) {
		this.isAggrProduct = isAggrProduct;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "platform_type")
	public Integer getPlatformType() {
		return platformType;
	}

	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}

	@Column(name = "object_id")
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
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
