package com.chunruo.portal.vo;

import java.util.ArrayList;
import java.util.List;

public class CategoryVo {
	private Long categoryId;			//分类ID
    private String name;				//分类名称
    private Long parentId;				//父类ID
    private String wapImage;			//WAP端栏目图片
    private String pcImage;				//PC端栏目图片
    private List<CategoryVo> childCategoryList = new ArrayList<CategoryVo> ();
    
	public Long getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Long getParentId() {
		return parentId;
	}
	
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public String getWapImage() {
		return wapImage;
	}
	
	public void setWapImage(String wapImage) {
		this.wapImage = wapImage;
	}
	
	public String getPcImage() {
		return pcImage;
	}
	
	public void setPcImage(String pcImage) {
		this.pcImage = pcImage;
	}

	public List<CategoryVo> getChildCategoryList() {
		return childCategoryList;
	}

	public void setChildCategoryList(List<CategoryVo> childCategoryList) {
		this.childCategoryList = childCategoryList;
	}
}
