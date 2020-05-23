package com.chunruo.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 热卖商品记录
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_hot_sale_record")
public class HotSaleRecord implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long recordId;
	private Long productId;					//商品Id
	private String categoryFids;			//商品分类父类ID
    private String categoryIds;				//商品分类ID
    private Long brandId;					//品牌id
    private Integer quantity;				//商品购买数量
    private Date createTime;				//创建时间
    private Date updateTime;				//更新时间
    
    @Transient
    private List<Long> categoryFidList = new ArrayList<Long>(); //父类id集合
    private List<Long> categoryIdList = new ArrayList<Long>();  //分类id集合

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getRecordId() {
		return recordId;
	}
    
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	
	@Column(name="product_id")
	public Long getProductId() {
		return productId;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	@Column(name="category_fids")
	public String getCategoryFids() {
		return categoryFids;
	}

	public void setCategoryFids(String categoryFids) {
		this.categoryFids = categoryFids;
	}

	@Column(name="category_ids")
	public String getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(String categoryIds) {
		this.categoryIds = categoryIds;
	}

	@Column(name="brand_id")
	public Long getBrandId() {
		return brandId;
	}
	
	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}
	
	@Column(name="quantity")
	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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
	public List<Long> getCategoryFidList() {
		return categoryFidList;
	}

	public void setCategoryFidList(List<Long> categoryFidList) {
		this.categoryFidList = categoryFidList;
	}

	@Transient
	public List<Long> getCategoryIdList() {
		return categoryIdList;
	}

	public void setCategoryIdList(List<Long> categoryIdList) {
		this.categoryIdList = categoryIdList;
	} 
	
}
