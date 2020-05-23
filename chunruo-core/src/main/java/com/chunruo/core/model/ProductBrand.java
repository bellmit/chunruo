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
 * 商品品牌
 * @author admin
 */
@Entity
@Table(name="jkd_product_brand")
public class ProductBrand implements Serializable{
 	
	private static final long serialVersionUID = -1404213072662068662L;
	private Long brandId;					//序号ID
    private String name;					//品牌名称
    private String shortName;				//品牌简称
    private String image;					//品牌图片
    private String countryImage;            //国家图片
    private Boolean isHot;					//是否热门
    private Boolean isHomePage;				//是否为首页
    private String initial;					//首字母
    private Integer sort;                   //排序
    private Long categroyId;                //所属分类
    private String adImage;                 //广告宣传图
    private String backgroundImage;          //背景图
    private String intro;                   //品牌简介
    private String brandDesc;               //品牌详细介绍
    private Long countryId;                 //国家Id
    private Date createTime;				//创建时间
    private Date updateTime;				//更新时间


    @Transient
    private String tagNames;
    private Integer typeNumber;			   //有多少种商品
    private String countryName;            //国家名称
    private Integer productNumber;         //商品数量
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	@Column(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="short_name")
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	@Column(name="image")
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Column(name="country_image")
	public String getCountryImage() {
		return countryImage;
	}

	public void setCountryImage(String countryImage) {
		this.countryImage = countryImage;
	}

	@Column(name="is_hot")
	public Boolean getIsHot() {
		return isHot;
	}

	public void setIsHot(Boolean isHot) {
		this.isHot = isHot;
	}

	@Column(name="initial")
	public String getInitial() {
		return initial;
	}

	public void setInitial(String initial) {
		this.initial = initial;
	}

	@Column(name="country_id")
	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	@Column(name="is_home_page")
	public Boolean getIsHomePage() {
		return isHomePage;
	}
	
	public void setIsHomePage(Boolean isHomePage) {
		this.isHomePage = isHomePage;
	}

	@Column(name="ad_image")
	public String getAdImage() {
		return adImage;
	}

	public void setAdImage(String adImage) {
		this.adImage = adImage;
	}

	@Column(name="background_image")
	public String getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	@Column(name="intro")
	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	@Column(name="brand_desc")
	public String getBrandDesc() {
		return brandDesc;
	}

	public void setBrandDesc(String brandDesc) {
		this.brandDesc = brandDesc;
	}

	@Column(name="sort")
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Column(name="categroy_id")
	public Long getCategroyId() {
		return categroyId;
	}

	public void setCategroyId(Long categroyId) {
		this.categroyId = categroyId;
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
	public String getTagNames() {
		return tagNames;
	}

	public void setTagNames(String tagNames) {
		this.tagNames = tagNames;
	}

	@Transient
	public Integer getTypeNumber() {
		return typeNumber;
	}

	public void setTypeNumber(Integer typeNumber) {
		this.typeNumber = typeNumber;
	}

	@Transient
	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	@Transient
	public Integer getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(Integer productNumber) {
		this.productNumber = productNumber;
	}

}
