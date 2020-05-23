package com.chunruo.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 会员礼包
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_member_gift")
public class MemberGift {

	public static final Integer MEMBER_TPYE_PRODUCT = 1; //商品
	public static final Integer MEMBER_TYPE_COUPON = 2;  //优惠券
	public static final Integer MEMBER_TYPE_PRO_COU = 3;  //商品和赠品
	private Long giftId;
	private Long templateId; //所属会员年限模板id
	private Integer type;   //类型（1：商品 2:优惠券）
	private String name; // 名称
	private Double price; // 价格
	private String productCode; // 商品编码
	private String productSku; // 商品sku
	private Integer stockNumber; // 商品库存
	private Long wareHouseId; // 所属仓库
	private String imagePath; // 主图
	private String detailImagePath; // 详情图片
	private String couponIds;       //赠送优惠券id
	private Boolean status;   //是否启用
	private Boolean isDelete; //是否删除

	private Date createTime;
	private Date updateTime;
	
	private Integer productType;
	List<String> detailImagePathList = new ArrayList<String>();

	@Id
	@GeneratedValue
	public Long getGiftId() {
		return giftId;
	}

	public void setGiftId(Long giftId) {
		this.giftId = giftId;
	}

	@Column(name = "template_id")
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "price")
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Column(name = "product_code")
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	@Column(name = "product_sku")
	public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}

	@Column(name = "stock_number")
	public Integer getStockNumber() {
		return stockNumber;
	}

	public void setStockNumber(Integer stockNumber) {
		this.stockNumber = stockNumber;
	}

	@Column(name = "ware_house_id")
	public Long getWareHouseId() {
		return wareHouseId;
	}

	public void setWareHouseId(Long wareHouseId) {
		this.wareHouseId = wareHouseId;
	}

	@Column(name = "image_path")
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Column(name = "detail_image_path")
	public String getDetailImagePath() {
		return detailImagePath;
	}

	public void setDetailImagePath(String detailImagePath) {
		this.detailImagePath = detailImagePath;
	}

	@Column(name = "status", columnDefinition="bit default true")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Column(name = "coupon_ids")
	public String getCouponIds() {
		return couponIds;
	}

	public void setCouponIds(String couponIds) {
		this.couponIds = couponIds;
	}

	@Column(name = "is_delete", columnDefinition="bit default false")
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
	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	@Transient
	public List<String> getDetailImagePathList() {
		return detailImagePathList;
	}

	public void setDetailImagePathList(List<String> detailImagePathList) {
		this.detailImagePathList = detailImagePathList;
	}

}
