package com.chunruo.core.model;

import java.io.Serializable;
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
 * 商品分类
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_product_category")
public class ProductCategory implements Serializable{
	public static final Integer PRODUCT_CATEGORY_LEVEL_FIRST = 1;  //一个分类
	public static final Integer PRODUCT_CATEGORY_LEVEL_SECOND = 2; //二级分类
	
	public static final Integer PRODUCT_CATEGORY_TARGET_TYPE_PRODUCT = 2;  //商品
	public static final Integer PRODUCT_CATEGORY_TARGET_TYPE_THEME = 3;    //页面
	public static final Integer PRODUCT_CATEGORY_TARGET_TYPE_AWARD = 6;    //任务中心
	public static final Integer PRODUCT_CATEGORY_TARGET_TYPE_MINI = 7;     //小程序
	public static final Integer PRODUCT_CATEGORY_TARGET_TYPE_WEB = 8;      //内嵌h5
	public static final Integer PRODUCT_CATEGORY_TARGET_TYPE_DISCOVERY = 9;  //发现详情
	public static final Integer PRODUCT_CATEGORY_TARGET_TYPE_DISCOVERYMODULE = 10; //发现话题标签
	public static final Integer PRODUCT_CATEGORY_TARGET_TYPE_DISCOVERYCREATER = 11;  //发现主体
	public static final Integer PRODUCT_CATEGORY_TARGET_TYPE_OUTWEB = 12;  //外部h5
	public static final Integer PRODUCT_CATEGORY_TARGET_TYPE_BRAND = 13;  //品牌详情

	private static final long serialVersionUID = 1L;
	private Long categoryId;			//分类ID
    private String name;				//分类名称
    private String description;			//分类描述
    private Long parentId;				//父类ID
    private String imagePath;			//栏目图片
    private Integer status;				//状态 0:禁用 1:启用
    private Integer sort;				//排序(值越大越前)
    private Boolean isRecommend;        //是否精选
    private Integer level;				//级别
    private Integer targetType;         //跳转类型
    private String microImagePath;      //一级缩略图
    private String adImagePath;         //活动图片
    private String content;             //跳转目的地
    private String brandIds;            //推介品牌
    private String profit;				//利润设置
    private Date createTime;
    private Date updateTime;
    
    @Transient
    private Boolean leaf = false;
    private Boolean isCurrentPage = false;	 //是否当前页面
    private String pathName;				 //路径地址
    private Boolean expanded;	 		     //是否展开
    private String tagNames;				//该分类的标签名称
    private String pageName;                //内页名称
    private Integer discoveryType;          //发现类型
    private Integer realTargetType;    
    private List<ProductCategory> childCategoryList = new ArrayList<ProductCategory> ();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

    @Column(name="name")
    public String getName() {
        return name;
    }
    
	public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    @Column(name="description")
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

    @Column(name="parent_id")
    public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

    @Column(name="image_path")
    public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
    @Column(name="status",length=1)
    public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
    
    @Column(name="sort")
    public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
    
	@Column(name="target_type")
    public Integer getTargetType() {
		return targetType;
	}

	public void setTargetType(Integer targetType) {
		this.targetType = targetType;
	}

	@Column(name="micro_image_path")
	public String getMicroImagePath() {
		return microImagePath;
	}

	public void setMicroImagePath(String microImagePath) {
		this.microImagePath = microImagePath;
	}

	@Column(name="ad_image_path")
	public String getAdImagePath() {
		return adImagePath;
	}

	public void setAdImagePath(String adImagePath) {
		this.adImagePath = adImagePath;
	}

	@Column(name="content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name="is_recommend")
	public Boolean getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(Boolean isRecommend) {
		this.isRecommend = isRecommend;
	}

	@Column(name="level")
    public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	
	@Column(name="profit",length=1000)
	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}

	@Column(name="brand_ids")
	public String getBrandIds() {
		return brandIds;
	}

	public void setBrandIds(String brandIds) {
		this.brandIds = brandIds;
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
	public Boolean getIsCurrentPage() {
		return isCurrentPage;
	}

	public void setIsCurrentPage(Boolean isCurrentPage) {
		this.isCurrentPage = isCurrentPage;
	}

	@Transient
	public List<ProductCategory> getChildCategoryList() {
		return childCategoryList;
	}

	public void setChildCategoryList(List<ProductCategory> childCategoryList) {
		this.childCategoryList = childCategoryList;
	}

	@Transient
	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	@Transient
	public Boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}

	@Transient
	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	@Transient
	public String getTagNames() {
		return tagNames;
	}

	public void setTagNames(String tagNames) {
		this.tagNames = tagNames;
	}

	@Transient
	public String getPageName() {
		return pageName;
	}
	
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	@Transient
	public Integer getDiscoveryType() {
		return discoveryType;
	}

	public void setDiscoveryType(Integer discoveryType) {
		this.discoveryType = discoveryType;
	}

	@Transient
	public Integer getRealTargetType() {
		return realTargetType;
	}

	public void setRealTargetType(Integer realTargetType) {
		this.realTargetType = realTargetType;
	}

}