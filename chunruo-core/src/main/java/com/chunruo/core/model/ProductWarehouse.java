package com.chunruo.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 仓库列表
 * 模板邮费
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_product_warehouse")
public class ProductWarehouse implements Serializable{
	private static final long serialVersionUID = 2457068545127117618L;
	private Long warehouseId;			//序号
    private String name;				//仓库名称
    private String tplArea;				//仓库配送区域
    private Integer productType;		//仓库属性(1:国内;2:跨境;3:BC直邮;4:行邮)
    private Boolean isPushCustoms;		//是否推送海关
    private Boolean isDirectPushErp;	//是否直接推送ERP
    private Integer warehouseType;  	//仓库类型(1:自营;2:他营)
    private String customs;				//海关名称(NINGBO 宁波)
	private String mchCustomsNo;		//商户在海关登记的备案号
	private String mchCustomsCode;		//商户海关备案编号
	private Long templateId;            //仓库模板id
    private Date createTime;			//创建时间
    private Date updateTime;			//更新时间
    
    @Transient
    private Boolean expanded;	 		//是否展开

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name="product_type" ,nullable=false)
    public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}
	
	@Column(name="warehouse_type" ,nullable=false)
	public Integer getWarehouseType() {
		return warehouseType;
	}

	public void setWarehouseType(Integer warehouseType) {
		this.warehouseType = warehouseType;
	}

	@Column(name="is_push_customs")
    public Boolean getIsPushCustoms() {
		return isPushCustoms;
	}

	public void setIsPushCustoms(Boolean isPushCustoms) {
		this.isPushCustoms = isPushCustoms;
	}
	
	@Column(name="is_direct_push_erp")
	public Boolean getIsDirectPushErp() {
		return isDirectPushErp;
	}

	public void setIsDirectPushErp(Boolean isDirectPushErp) {
		this.isDirectPushErp = isDirectPushErp;
	}

	@Column(name="customs")
	public String getCustoms() {
		return customs;
	}

	public void setCustoms(String customs) {
		this.customs = customs;
	}

	@Column(name="mch_customs_no")
	public String getMchCustomsNo() {
		return mchCustomsNo;
	}

	public void setMchCustomsNo(String mchCustomsNo) {
		this.mchCustomsNo = mchCustomsNo;
	}

	@Column(name="mch_customs_code")
	public String getMchCustomsCode() {
		return mchCustomsCode;
	}

	public void setMchCustomsCode(String mchCustomsCode) {
		this.mchCustomsCode = mchCustomsCode;
	}

	@Column(name="template_id")
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
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
	public Boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}
}