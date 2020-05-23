package com.chunruo.core.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 商品规格类型
 * @author chunruo
 */
@Entity
@Table(name="jkd_product_spec_type")
public class ProductSpecType {
	public final static int PRIMARY_SPEC_TYPE = 1;		//主规格
	public final static int SECONDARY_SPEC_TYPE = 2;	//次规格
	private Long specTypeId;	
	private Long productId;
	private Long specModelId;
	private String specTypeName;
	private String specModelName;
	private Integer sort;
	private String imagePath;
	private String tmpSpecTypeId;
	private Date createTime;
	private Date updateTime;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getSpecTypeId() {
		return specTypeId;
	}

	public void setSpecTypeId(Long specTypeId) {
		this.specTypeId = specTypeId;
	}
	
	@Column(name="product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	@Column(name="spec_model_id")
	public Long getSpecModelId() {
		return specModelId;
	}

	public void setSpecModelId(Long specModelId) {
		this.specModelId = specModelId;
	}

	@Column(name="spec_type_name")
	public String getSpecTypeName() {
		return specTypeName;
	}

	public void setSpecTypeName(String specTypeName) {
		this.specTypeName = specTypeName;
	}
	
	@Column(name="spec_model_name")
	public String getSpecModelName() {
		return specModelName;
	}

	public void setSpecModelName(String specModelName) {
		this.specModelName = specModelName;
	}

	@Column(name="sort")
	public Integer getSort() {
		return sort;
	}
	
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	@Column(name="image_path")
	public String getImagePath() {
		return imagePath;
	}
	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	@Column(name="tmp_spec_type_id")
	public String getTmpSpecTypeId() {
		return tmpSpecTypeId;
	}

	public void setTmpSpecTypeId(String tmpSpecTypeId) {
		this.tmpSpecTypeId = tmpSpecTypeId;
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
