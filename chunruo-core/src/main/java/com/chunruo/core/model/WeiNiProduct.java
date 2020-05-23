package com.chunruo.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@Table(name = "jkd_wei_ni_product",uniqueConstraints = {
		@UniqueConstraint(columnNames = {"sku_no"})
	})
public class WeiNiProduct {

	private Long productId;
	private String skuNo;       //商品编码
	private String skuName;     //商品名称
	private String barCode;     //商品条码
	private Double settlePrice; // 结算价
	private String retailPrice; // 市场参考价
	private String brand; // 品牌
	private String country; // 国别
	private String category; // 一级分类
	private String twoCategory; // 二级分类
	private String threeCategory; // 三级分类
	private String details; // 详情介绍
	private Double rate; // 税率，非保税区这个字段为0
	private Integer deliveryCode; // 发货方式 （1：保税区发货 2：香港直邮 4：海外直邮 5：国内发货）
	private Integer saleType; // 销售类型 （0：批发价 1：包邮包税价）
	private Integer weight; // 商品重量 （单微：克）
	private String displayImgUrls; // 商品主图（分号隔开）
	private String detailImgUrls; // 商品详情图（分号隔开）
	private String deliveryCity; // 发货地
	private String goodsNo; // 商品编码
	private String spec; // 商品规格
	private Integer limitNumber; // 限购数量（0表示不限购）
	private String validDay; // 有限期（2020-10-22）
	private Boolean isLimitPrice; // 是否控价
	private Boolean ifInvoice; // 是否开票

	@Id
	@GeneratedValue
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "sku_no")
	@JSONField(name="SkuNo")
	public String getSkuNo() {
		return skuNo;
	}

	public void setSkuNo(String skuNo) {
		this.skuNo = skuNo;
	}

	@Column(name = "sku_name")
	@JSONField(name="SkuName")
	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	@Column(name = "bar_code")
	@JSONField(name="BarCode")
	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	@Column(name = "settle_price")
	@JSONField(name="SettlePrice")
	public Double getSettlePrice() {
		return settlePrice;
	}

	public void setSettlePrice(Double settlePrice) {
		this.settlePrice = settlePrice;
	}

	@Column(name = "retail_price")
	@JSONField(name="RetailPrice")
	public String getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(String retailPrice) {
		this.retailPrice = retailPrice;
	}

	@Column(name = "brand")
	@JSONField(name="Brand")
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@Column(name = "country")
	@JSONField(name="Country")
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "category")
	@JSONField(name="Category")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Column(name = "two_category")
	@JSONField(name="TwoCategory")
	public String getTwoCategory() {
		return twoCategory;
	}

	public void setTwoCategory(String twoCategory) {
		this.twoCategory = twoCategory;
	}

	@Column(name = "three_category")
	@JSONField(name="ThreeCategory")
	public String getThreeCategory() {
		return threeCategory;
	}

	public void setThreeCategory(String threeCategory) {
		this.threeCategory = threeCategory;
	}

	@Lob
	@Column(name = "details")
	@JSONField(name="Details")
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Column(name = "rate")
	@JSONField(name="Rate")
	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	@Column(name = "delivery_code")
	@JSONField(name="DeliveryCode")
	public Integer getDeliveryCode() {
		return deliveryCode;
	}

	public void setDeliveryCode(Integer deliveryCode) {
		this.deliveryCode = deliveryCode;
	}

	@Column(name = "sale_type")
	@JSONField(name="SaleType")
	public Integer getSaleType() {
		return saleType;
	}

	public void setSaleType(Integer saleType) {
		this.saleType = saleType;
	}

	@Column(name = "weight")
	@JSONField(name="Weight")
	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	@Lob
	@Column(name = "display_img_urls")
	@JSONField(name="displayImgUrls")
	public String getDisplayImgUrls() {
		return displayImgUrls;
	}

	public void setDisplayImgUrls(String displayImgUrls) {
		this.displayImgUrls = displayImgUrls;
	}

	@Lob
	@Column(name = "detail_img_urls")
	@JSONField(name="detailImgUrls")
	public String getDetailImgUrls() {
		return detailImgUrls;
	}

	public void setDetailImgUrls(String detailImgUrls) {
		this.detailImgUrls = detailImgUrls;
	}

	@Column(name = "delivery_city")
	@JSONField(name="DeliveryCity")
	public String getDeliveryCity() {
		return deliveryCity;
	}

	public void setDeliveryCity(String deliveryCity) {
		this.deliveryCity = deliveryCity;
	}

	@Column(name = "goods_no")
	@JSONField(name="GoodsNo")
	public String getGoodsNo() {
		return goodsNo;
	}

	public void setGoodsNo(String goodsNo) {
		this.goodsNo = goodsNo;
	}

	@Column(name = "spec")
	@JSONField(name="Spec")
	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	@Column(name = "limit_number")
	@JSONField(name="LimitNumber")
	public Integer getLimitNumber() {
		return limitNumber;
	}

	public void setLimitNumber(Integer limitNumber) {
		this.limitNumber = limitNumber;
	}

	@Column(name = "valid_day")
	@JSONField(name="ValidDay")
	public String getValidDay() {
		return validDay;
	}

	public void setValidDay(String validDay) {
		this.validDay = validDay;
	}

	@Column(name = "is_limit_price")
	@JSONField(name="IsLimitPrice")
	public Boolean getIsLimitPrice() {
		return isLimitPrice;
	}

	public void setIsLimitPrice(Boolean isLimitPrice) {
		this.isLimitPrice = isLimitPrice;
	}

	@Column(name = "if_invoice")
	@JSONField(name="IfInvoice")
	public Boolean getIfInvoice() {
		return ifInvoice;
	}

	public void setIfInvoice(Boolean ifInvoice) {
		this.ifInvoice = ifInvoice;
	}

}
