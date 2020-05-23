package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 商品促销(买赠送)
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_product_promot")
public class ProductPromot {
	private Long promotId;				//序号
	private String promotName;			//促销名称
	private Boolean status;				//状态(是否开启)
	private Boolean isDelete;			//是否删除
	private Long productId;				//促销商品ID
	private String tarProductIds;		//赠送商品列表
	private Date createTime;
	private Date updateTime;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getPromotId() {
		return promotId;
	}
	
	public void setPromotId(Long promotId) {
		this.promotId = promotId;
	}
	
	@Column(name="promot_name")
	public String getPromotName() {
		return promotName;
	}
	
	public void setPromotName(String promotName) {
		this.promotName = promotName;
	}
	
	@Column(name="status")
	public Boolean getStatus() {
		return status;
	}
	
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	@Column(name="is_delete")
	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Column(name="product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name="tar_product_ids")
	public String getTarProductIds() {
		return tarProductIds;
	}

	public void setTarProductIds(String tarProductIds) {
		this.tarProductIds = tarProductIds;
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
