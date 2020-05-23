package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 邀请大礼包动态列表
 * @author chunruo
 */
@Entity
@Table(name = "jkd_product_gift")
public class ProductGift {
	private Long giftId;
	private Long productSpecId;      // 大礼包规格id
	private String headerImage;      // 大礼包图片
	private String productDesc;      // 大礼包详情描述
	private String productName;      //大礼包规格名称
	private Integer yearNumber;      //大礼包年份
	private Long wareHouseId;        //所属仓库
	private Date createTime;         // 创建时间
	private Date updateTime;         // 更新时间
	
	@Transient
	private String productTags;      //规格名称

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getGiftId() {
		return giftId;
	}

	public void setGiftId(Long giftId) {
		this.giftId = giftId;
	}

	@Column(name = "product_spec_id")
	public Long getProductSpecId() {
		return productSpecId;
	}

	public void setProductSpecId(Long productSpecId) {
		this.productSpecId = productSpecId;
	}

	@Column(name = "header_image")
	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}
	
	
	@Column(name = "product_name")
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Column(name = "ware_house_id")
	public Long getWareHouseId() {
		return wareHouseId;
	}

	public void setWareHouseId(Long wareHouseId) {
		this.wareHouseId = wareHouseId;
	}

	@Column(name = "year_number")
	public Integer getYearNumber() {
		return yearNumber;
	}

	public void setYearNumber(Integer yearNumber) {
		this.yearNumber = yearNumber;
	}

	@Lob
	@Column(name = "product_desc")
	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
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
	public String getProductTags() {
		return productTags;
	}

	public void setProductTags(String productTags) {
		this.productTags = productTags;
	}
}
