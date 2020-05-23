package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 优惠券
 * @author chunruo
 *
 */
@Entity
@Table(name = "jkd_coupon")
public class Coupon {
	// 优惠券类型
	public final static int COUPON_TYPE_FULL = 1;      
	public final static int COUPON_TYPE_CASH = 2;
	public final static int COUPON_TYPE_RECHARGE = 3;
	// 优惠券属性
	public final static int COUPON_ATTRIBUTE_CATEGORY = 1;
	public final static int COUPON_ATTRIBUTE_PRODUCT = 2;
	public final static int COUPON_ATTRIBUTE_ALL = 3; 
	// 领取方法
	public final static int receive_Type_TASK = 0; 
	public final static int receive_Type_AUTO = 1; 
	public final static int receive_Type_HAND = 2;
	//使用范围
	public final static int USER_RANGE_TYPE_ORDER = 1;     //订单满足金额
	public final static int USER_RANGE_TYPE_PRODUCT = 2;   //订单内的商品要满足优惠券的类型
	
	
	private Long couponId;                   // 优惠券ID
	private String couponName;               // 优惠券名称
	private Integer totalCount;              // 优惠券数量
	private Integer usedCount;               // 优惠券已使用数量
	private Integer couponType;              // 优惠券类型（1，满减券。2，代金券. 3,赠品券）
	private Integer receiveType;             // 领取方法（0，任务领取。1，自动发放，2，手动领取）
	private Integer useRangeType;            // 使用范围  // 1 订单满金额  2 区间内商品的金额
	private Double fullAmount;               // 满多少钱
	private Double giveAmount;               // 送多少钱
	private Boolean isEnable;                // 是否启用
	private Boolean isGiftCoupon;            // 是否赠品优惠券
	private Integer attribute;               // 优惠券属性 （1，品类。2，商品。3，全场通用）
	private String attributeContent;         // 优惠券属性详情，（一般绑定品类或商品，[1,2,3,4]以数组形式存储）
	private Integer effectiveTime;           // 优惠券有效期，单位为“天”；
	private String sender;                   // 发送对象(1:VIP;2:经销商;3:总代)
	private String receiveBeginTime;         // 开始领取优惠券时间
	private String receiveEndTime;           // 结束领取优惠券时间
	private Date createTime;                 // 创建时间
	private Date updateTime;                 // 更新时间
	private String adminUserName;            // 操作员
	private Long rechargeTemplateId;         // 充值套餐id
	private Boolean isRechargeProductCoupon; // 是否充值赠送商品券
	private String remark;                   // 备注

	@Transient
	private String title;                    // 优惠券说明抬头
	private String bindNames;                // 该优惠券绑定的分类名或商品名，多个以逗号分隔
	private String typeName;                 // 满减券,代金券
	private String bindIntro;                // 以下商品适用
	private String productNames;             // 优惠券适用商品的名称，隔开

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getCouponId() {
		return couponId;
	}

	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}

	@Column(name = "coupon_name")
	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	@Column(name = "total_count")
	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	@Column(name = "used_count")
	public Integer getUsedCount() {
		return usedCount;
	}

	public void setUsedCount(Integer usedCount) {
		this.usedCount = usedCount;
	}

	@Column(name = "coupon_type")
	public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}

	@Column(name = "receive_type")
	public Integer getReceiveType() {
		return receiveType;
	}

	public void setReceiveType(Integer receiveType) {
		this.receiveType = receiveType;
	}
	
	@Column(name = "use_range_type")
	public Integer getUseRangeType() {
		return useRangeType;
	}

	public void setUseRangeType(Integer useRangeType) {
		this.useRangeType = useRangeType;
	}

	@Column(name = "full_amount")
	public Double getFullAmount() {
		return fullAmount;
	}

	public void setFullAmount(Double fullAmount) {
		this.fullAmount = fullAmount;
	}

	@Column(name = "give_amount")
	public Double getGiveAmount() {
		return giveAmount;
	}

	public void setGiveAmount(Double giveAmount) {
		this.giveAmount = giveAmount;
	}

	@Column(name = "is_enable")
	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	@Column(name = "is_gift_coupon", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsGiftCoupon() {
		return isGiftCoupon;
	}

	public void setIsGiftCoupon(Boolean isGiftCoupon) {
		this.isGiftCoupon = isGiftCoupon;
	}

	@Column(name = "attribute")
	public Integer getAttribute() {
		return attribute;
	}

	public void setAttribute(Integer attribute) {
		this.attribute = attribute;
	}

	@Column(name = "attribute_content")
	public String getAttributeContent() {
		return attributeContent;
	}

	public void setAttributeContent(String attributeContent) {
		this.attributeContent = attributeContent;
	}
	
	@Column(name = "receive_begin_time")
	public String getReceiveBeginTime() {
		return receiveBeginTime;
	}

	public void setReceiveBeginTime(String receiveBeginTime) {
		this.receiveBeginTime = receiveBeginTime;
	}

	@Column(name = "receive_end_time")
	public String getReceiveEndTime() {
		return receiveEndTime;
	}

	public void setReceiveEndTime(String receiveEndTime) {
		this.receiveEndTime = receiveEndTime;
	}

	@Column(name = "effective_time")
	public Integer getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Integer effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	@Column(name = "sender")
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	@Column(name = "admin_user_name")
	public String getAdminUserName() {
		return adminUserName;
	}

	public void setAdminUserName(String adminUserName) {
		this.adminUserName = adminUserName;
	}

	@Column(name = "recharge_template_id")
	public Long getRechargeTemplateId() {
		return rechargeTemplateId;
	}

	public void setRechargeTemplateId(Long rechargeTemplateId) {
		this.rechargeTemplateId = rechargeTemplateId;
	}

	@Column(name = "is_recharge_product_coupon")
	public Boolean getIsRechargeProductCoupon() {
		return isRechargeProductCoupon;
	}

	public void setIsRechargeProductCoupon(Boolean isRechargeProductCoupon) {
		this.isRechargeProductCoupon = isRechargeProductCoupon;
	}

	@Column(name = "remark")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
	public String getBindNames() {
		return bindNames;
	}

	public void setBindNames(String bindNames) {
		this.bindNames = bindNames;
	}

	@Transient
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Transient
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Transient
	public String getBindIntro() {
		return bindIntro;
	}

	public void setBindIntro(String bindIntro) {
		this.bindIntro = bindIntro;
	}

	@Transient
	public String getProductNames() {
		return productNames;
	}

	public void setProductNames(String productNames) {
		this.productNames = productNames;
	}

}
