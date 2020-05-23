package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 商品说明列表
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_product_intro")
public class ProductIntro {
    private Long introId;	    	//商品简介Id
    private String title;			//标题
    private String description;		//描述
    private String introduction;	//简介
    private Integer sort;			//排序(值越大越前)	
    private Date createTime;
    private Date updateTime;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getIntroId() {
		return introId;
	}
    
	public void setIntroId(Long introId) {
		this.introId = introId;
	}
	
	@Column(name="title")
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column(name="description")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="introduction")
	public String getIntroduction() {
		return introduction;
	}
	
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	
	@Column(name="sort")
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	@Column(name="createTime")
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