package com.chunruo.webapp.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.chunruo.core.model.ProductCategory;

public class CouponProductCatrgory {
	private Long id;			         //ID
    private String name;				//分类名称
    private String description;			//分类描述
    private Long parentId;				//父类ID
    private String imagePath;			//栏目图片
    private Integer status;				//状态
    private Integer sort;				//排序(值越大越前)
    private Integer level;				//级别
    private String profit;				//利润设置
    private Integer selectType; 
    private Integer att;
    private Date createTime;
    private Date updateTime;
    

    private Boolean leaf = false;
    private Boolean isCurrentPage = false;	 //是否当前页面
    private String pathName;				 //路径地址
    private List<ProductCategory> childCategoryList = new ArrayList<ProductCategory> ();

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
        return name;
    }
    
	public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }


    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

    public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}


    public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	

    public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
    

    public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
    

    public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	

	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}


	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


	public Boolean getIsCurrentPage() {
		return isCurrentPage;
	}

	public void setIsCurrentPage(Boolean isCurrentPage) {
		this.isCurrentPage = isCurrentPage;
	}


	public List<ProductCategory> getChildCategoryList() {
		return childCategoryList;
	}

	public void setChildCategoryList(List<ProductCategory> childCategoryList) {
		this.childCategoryList = childCategoryList;
	}


	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}


	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public Integer getSelectType() {
		return selectType;
	}

	public void setSelectType(Integer selectType) {
		this.selectType=selectType;
	}

	public Integer getAtt() {
		return att;
	}

	public void setAtt(Integer att) {
		this.att = att;
	}
    


	
}
