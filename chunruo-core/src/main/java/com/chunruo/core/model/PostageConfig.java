package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 邮费配置
 * 
 * @author Administrator
 *
 */
@Entity
@Table(name = "cr_postage_config")
public class PostageConfig {

	private Long templateId;
	private String tplArea; // 模板配送区域
	private String name; // 模板名称
	private Double firstWeigth; // 首重
	private Double firstPrice; // 运费
	private Double afterWeigth; // 续重
	private Double afterPrice; // 续费

	private Date createTime;
	private Date updateTime;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	@Column(name = "tpl_area", length = 10000)
	public String getTplArea() {
		return tplArea;
	}

	public void setTplArea(String tplArea) {
		this.tplArea = tplArea;
	}

	@Column(name = "name", length = 100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "first_weigth")
	public Double getFirstWeigth() {
		return firstWeigth;
	}

	public void setFirstWeigth(Double firstWeigth) {
		this.firstWeigth = firstWeigth;
	}

	@Column(name = "first_price")
	public Double getFirstPrice() {
		return firstPrice;
	}

	public void setFirstPrice(Double firstPrice) {
		this.firstPrice = firstPrice;
	}

	@Column(name = "after_weigth")
	public Double getAfterWeigth() {
		return afterWeigth;
	}

	public void setAfterWeigth(Double afterWeigth) {
		this.afterWeigth = afterWeigth;
	}

	@Column(name = "after_price")
	public Double getAfterPrice() {
		return afterPrice;
	}

	public void setAfterPrice(Double afterPrice) {
		this.afterPrice = afterPrice;
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
