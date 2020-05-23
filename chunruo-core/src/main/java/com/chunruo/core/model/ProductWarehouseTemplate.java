package com.chunruo.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
@Entity
@Table(name = "jkd_product_warehouse_template", 
uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) 
})
public class ProductWarehouseTemplate {

	private Long templateId;
	private String name;
	private String imagePath;
	private Boolean status; // 是否启用
	private Date createTime;
	private Date updateTime;
	
	private Integer productType; //商品类型
	
	private List<UserCart> userCartList = new ArrayList<UserCart>();

	@Id
	@GeneratedValue
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "image_path")
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Column(name = "status")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
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
	public List<UserCart> getUserCartList() {
		return userCartList;
	}

	public void setUserCartList(List<UserCart> userCartList) {
		this.userCartList = userCartList;
	}

	@Transient
	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}
	
	
}
