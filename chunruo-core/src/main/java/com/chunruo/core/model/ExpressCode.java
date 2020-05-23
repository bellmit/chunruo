package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 快递物流公司查询对应的物流编码表
 * @author chunruo
 *
 */
@Entity
@Table(name = "jkd_express_code")
public class ExpressCode {
	private Long codeId;
	private String companyName;
	private String companyCode;
	private String expressCode;
	private Date createTime;
	private Date updateTime;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getCodeId() {
		return codeId;
	}
	
	public void setCodeId(Long codeId) {
		this.codeId = codeId;
	}
	
	@Column(name = "company_name", length=150)
	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	@Column(name = "company_code", length=50)
	public String getCompanyCode() {
		return companyCode;
	}
	
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	
	@Column(name = "express_code", length=50)
	public String getExpressCode() {
		return expressCode;
	}
	
	public void setExpressCode(String expressCode) {
		this.expressCode = expressCode;
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
