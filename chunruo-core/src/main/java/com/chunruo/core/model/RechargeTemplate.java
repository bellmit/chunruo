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
 * 充值套餐模板
 * @author Administrator
 */
@Entity
@Table(name = "jkd_recharge_template")
public class RechargeTemplate {
	// 套餐类型
	public final static Integer RECHARGE_TEMPLATE_TYPE_NONE = 0; //无赠品
	public final static Integer RECHARGE_TEMPLATE_TYPE_COUPON = 1; //优惠券
	public final static Integer RECHARGE_TEMPLATE_TYPE_AMOUNT = 2; //赠送余额
	public final static Integer RECHARGE_TEMPLATE_TYPE_DECLARE = 3; //经销商等级
	public final static Integer RECHARGE_TEMPLATE_TYPE_PRODUCT = 4; //商品
	
	// 特定用户可见套餐
	public final static Integer RECHARGE_TEMPLATE_USERLEVEL_V0 = 1;
	public final static Integer RECHARGE_TEMPLATE_USERLEVEL_V1 = 2;
	public final static Integer RECHARGE_TEMPLATE_USERLEVEL_V2 = 4;
	public final static Integer RECHARGE_TEMPLATE_USERLEVEL_V3 = 5;
	public final static Integer RECHARGE_TEMPLATE_USERLEVEL_ALLDECLARE = 6;  //所有经销商
	public final static Integer RECHARGE_TEMPLATE_USERLEVEL_ALLUSER = 7;     //所有用户
	
	// 适用于等级类型
	public final static Integer RECHARGE_TEMPLATE_GIFTUSERLEVEL_V1 = 2;
	public final static Integer RECHARGE_TEMPLATE_GIFTUSERLEVEL_V2 = 4;
	public final static Integer RECHARGE_TEMPLATE_GIFTUSERLEVEL_V3 = 5;

	private Long templateId;
	private Double amount;                      // 充值金额
	private Double giftAmount;                  // 赠送金额
	private Integer giftUserLevel;              // 赠送等级
	private Integer giftUserLevelTime;          // 赠送时长
	private Long productId;                     // 赠送商品(适用于商品)
	private Integer type;                       // 赠品类型 （0： 无赠品 1：优惠券 2：赠送余额 3：经销商等级 4：商品）
	private String couponId;                    // 赠送优惠券(可以赠送多个逗号分隔)
	private String giftName;                    // 赠品名称
	private String imageUrl;					// 赠品图片
	private Boolean isRecommend;				// 是否推荐
	private Boolean isEnable;                   // 是否启用
	private Boolean isDelete;                   // 是否删除(隐藏)
	private Integer userLevel;                  // 用户等级 （1：v0 2：v1 4：v2 5：v3 6：所有经销商 7：所有用户）
	private Date createTime;
	private Date updateTime;

	@Transient
	private List<String> imageUrlList = new ArrayList<String>(); //图片地址集合
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	@Column(name = "amount")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "gift_amount")
	public Double getGiftAmount() {
		return giftAmount;
	}

	public void setGiftAmount(Double giftAmount) {
		this.giftAmount = giftAmount;
	}

	@Column(name = "gift_user_level")
	public Integer getGiftUserLevel() {
		return giftUserLevel;
	}

	public void setGiftUserLevel(Integer giftUserLevel) {
		this.giftUserLevel = giftUserLevel;
	}

	@Column(name = "gift_user_level_time")
	public Integer getGiftUserLevelTime() {
		return giftUserLevelTime;
	}

	public void setGiftUserLevelTime(Integer giftUserLevelTime) {
		this.giftUserLevelTime = giftUserLevelTime;
	}

	@Column(name = "product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "coupon_id")
	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}

	@Column(name = "gift_name")
	public String getGiftName() {
		return giftName;
	}

	public void setGiftName(String giftName) {
		this.giftName = giftName;
	}

	@Column(name = "image_url")
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Column(name = "is_recommend", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(Boolean isRecommend) {
		this.isRecommend = isRecommend;
	}

	@Column(name = "is_enable", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	@Column(name = "is_delete", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Column(name = "user_level")
	public Integer getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(Integer userLevel) {
		this.userLevel = userLevel;
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
	public List<String> getImageUrlList() {
		return imageUrlList;
	}

	public void setImageUrlList(List<String> imageUrlList) {
		this.imageUrlList = imageUrlList;
	}
	
	

}
