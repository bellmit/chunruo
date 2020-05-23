package com.chunruo.webapp.vo;

import java.io.Serializable;

import com.chunruo.core.model.FxPage;
import com.chunruo.core.util.DateUtil;


/**
 * 分销页面分类
 * @author chunruo
 *
 */
public class FxPageVo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long pageId;			//序号
	private Long channelId;			//渠道ID
	private String pageName;		//渠道页面名称
	private Integer categoryType;	//分类类型(0:频道首页;1:内页)
	private String createTime;		//创建时间
	private String updateTime;		//更新时间
	private String image;           //专题图片
	
	public FxPageVo(){}
	public FxPageVo(FxPage page){
		if (page != null){
			this.pageId = page.getPageId();
			this.channelId = page.getChannelId();
			this.pageName = page.getPageName();
			this.categoryType = page.getCategoryType();
			this.createTime = DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, page.getCreateTime());
			this.updateTime = DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, page.getCreateTime());
		}
	}
	public Long getPageId() {
		return this.pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public Long getChannelId() {
		return this.channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public String getPageName() {
		return this.pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	public Integer getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(Integer categoryType) {
		this.categoryType = categoryType;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

}
