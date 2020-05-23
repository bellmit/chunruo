package com.chunruo.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="jkd_fx_children")
public class FxChildren{
	public final static Integer FXCHILDREN_TYP_BANNER = 0;		//Banner
	public final static Integer FXCHILDREN_TYP_NAVIGATION = 1;	//导航
	public final static Integer FXCHILDREN_TYP_SPECIAL = 2;		//专题
	public final static Integer FXCHILDREN_TYP_PRODUCT = 3;		//商品
	public final static Integer FXCHILDREN_TYP_MODULE = 4;      //模块
	public final static Integer FXCHILDREN_TYP_SECKILL= 7;      //秒杀
	private Long childrenId;			//序号
	private Long pageId;				//页面ID
	private Integer type;				//类型(0:Banner;1:导航;2:专题;3:商品;4:模块;5:秒杀)
	private Integer attribute;			//页面属性: Banner ：0-多张轮播图，1-单张宽，(2-单张窄 )->(2-双排显示) 导航：0-一行4个，1-二行8个  专题：0-一行1个， 1-一行两个  商品：0-一行1个， 1-一行两个
	private String specialName;			//专题名称
	private Integer sort;				//页面排序
	private String picture;				//单张图片：Banner、专题
	private String contents;			//内容
	private Date createTime;			//创建时间
	private Date updateTime;			//更新时间
	
	@Transient
	private List<Map<String, Object>> detailMapList = new ArrayList<Map<String, Object>> ();
	private List<Map<String, Object>> detailSpecialMapList = new ArrayList<Map<String, Object>> ();
	private String reason;
	private Integer errorCode;
	private Long startTime;
	private Long endTime;
	private Long seckillId;
	private String seckillName;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getChildrenId() {
		return this.childrenId;
	}

	public void setChildrenId(Long childrenId) {
		this.childrenId = childrenId;
	}

	@Column(name="page_id", nullable=false)
	public Long getPageId() {
		return this.pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	@Column(name="type", nullable=false, length=1)
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name="attribute", nullable=false)
	public Integer getAttribute() {
		return this.attribute;
	}

	public void setAttribute(Integer attribute) {
		this.attribute = attribute;
	}

	@Column(name="special_name", length=50)
	public String getSpecialName() {
		return this.specialName;
	}

	public void setSpecialName(String specialName) {
		this.specialName = specialName;
	}

	@Column(name="sort")
	public Integer getSort() {
		return this.sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Column(name="picture", length=220)
	public String getPicture() {
		return this.picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	@Column(name="contents", length=3000)
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
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
	public List<Map<String, Object>> getDetailMapList() {
		return detailMapList;
	}

	public void setDetailMapList(List<Map<String, Object>> detailMapList) {
		this.detailMapList = detailMapList;
	}

	@Transient
	public List<Map<String, Object>> getDetailSpecialMapList() {
		return detailSpecialMapList;
	}

	public void setDetailSpecialMapList(List<Map<String, Object>> detailSpecialMapList) {
		this.detailSpecialMapList = detailSpecialMapList;
	}

	@Transient
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@Transient
	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	@Transient
	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	@Transient
	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	@Transient
	public Long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(Long seckillId) {
		this.seckillId = seckillId;
	}

	@Transient
	public String getSeckillName() {
		return seckillName;
	}

	public void setSeckillName(String seckillName) {
		this.seckillName = seckillName;
	}
	
	
}
