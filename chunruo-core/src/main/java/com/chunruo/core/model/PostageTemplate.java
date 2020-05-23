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
 * 邮费模板
 * @author admin
 *
 */
@Entity
@Table(name="jkd_postage_template")
public class PostageTemplate {
	private Long templateId;				//模版ID
    private Long warehouseId;				//仓库ID
    private String name;					//模版名称
    private String tplArea;					//模板配送区域
    private Boolean isFreeTemplate;			//是否是包邮模版
    private Double freePostageAmount;		//商品包邮要求金额
    private Date createTime;				//创建时间
    private Date updateTime;				//更新时间

    @Transient
    private String warehouseName;		//仓库名称
    private String firstWeigth;			//首重重量
    private String firstPrice;			//首重邮费
    private String afterWeigth;			//续重重量
    private String afterPrice;			//续重邮费
    private String packageWeigth;       //包材重量
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	
	@Column(name="warehouse_id")
    public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}
	
    @Column(name="name", length=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Column(name="tpl_area", length=10000)
    public String getTplArea() {
        return tplArea;
    }

    public void setTplArea(String tplArea) {
        this.tplArea = tplArea == null ? null : tplArea.trim();
    }

	@Column(name="is_free_template")
	public Boolean getIsFreeTemplate() {
		return isFreeTemplate;
	}

	public void setIsFreeTemplate(Boolean isFreeTemplate) {
		this.isFreeTemplate = isFreeTemplate;
	}
	
	@Column(name="free_postage_amount")
	public Double getFreePostageAmount() {
		return freePostageAmount;
	}

	public void setFreePostageAmount(Double freePostageAmount) {
		this.freePostageAmount = freePostageAmount;
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
	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	@Transient
	public String getFirstPrice() {
		return firstPrice;
	}

	public void setFirstPrice(String firstPrice) {
		this.firstPrice = firstPrice;
	}

	@Transient
	public String getFirstWeigth() {
		return firstWeigth;
	}

	public void setFirstWeigth(String firstWeigth) {
		this.firstWeigth = firstWeigth;
	}

	@Transient
	public String getAfterWeigth() {
		return afterWeigth;
	}

	public void setAfterWeigth(String afterWeigth) {
		this.afterWeigth = afterWeigth;
	}

	@Transient
	public String getAfterPrice() {
		return afterPrice;
	}

	public void setAfterPrice(String afterPrice) {
		this.afterPrice = afterPrice;
	}

	@Transient
	public String getPackageWeigth() {
		return packageWeigth;
	}

	public void setPackageWeigth(String packageWeigth) {
		this.packageWeigth = packageWeigth;
	}
}
