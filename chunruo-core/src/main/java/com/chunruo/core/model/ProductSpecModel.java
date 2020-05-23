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
 * 商品规格名称列表
 * @author chunruo
 */
@Entity
@Table(name="jkd_product_spec_model", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"name"})
})
public class ProductSpecModel {
	private Long specModelId;
	private String name;
	private Integer sort;
	private Date createTime;
	private Date updateTime;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getSpecModelId() {
		return specModelId;
	}
	
	public void setSpecModelId(Long specModelId) {
		this.specModelId = specModelId;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="sort")
	public Integer getSort() {
		return sort;
	}
	
	public void setSort(Integer sort) {
		this.sort = sort;
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
