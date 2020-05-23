package com.chunruo.core.model;

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
 * 分销页面分类
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_fx_page")
public class FxPage{
	public final static int CATEGORY_TYPE_HOME = 0;	//频道首页
	public final static int CATEGORY_TYPE_PAGE = 1;	//内页
	public final static int CATEGORY_TYPE_THEME = 2; //专题
	private Long pageId;			//序号
	private Long channelId;			//渠道ID
	private String pageName;		//渠道页面名称
	private Integer categoryType;	//分类类型(0:频道首页;1:内页;2:专题)
	private Boolean isDelete;
	private Date createTime;		//创建时间
	private Date updateTime;		//更新时间
	
	@Transient
	private String channelName;		//频道名称
	private String categoryName;	//分类名称
	private List<FxChildren> fxChildrenList = new ArrayList<FxChildren> ();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getPageId() {
		return this.pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	@Column(name = "channel_id", nullable = false)
	public Long getChannelId() {
		return this.channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	@Column(name = "page_name", nullable = false, length = 45)
	public String getPageName() {
		return this.pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	@Column(name="category_type", nullable = false, length = 45)
	public Integer getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(Integer categoryType) {
		this.categoryType = categoryType;
	}

	@Column(name = "is_delete")
	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
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
	public List<FxChildren> getFxChildrenList() {
		return fxChildrenList;
	}

	public void setFxChildrenList(List<FxChildren> fxChildrenList) {
		this.fxChildrenList = fxChildrenList;
	}

	@Transient
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	@Transient
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	
}
