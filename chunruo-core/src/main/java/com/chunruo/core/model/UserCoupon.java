package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/** 
 * 用户领取的优惠券
 * @author Alex
*/
@Entity
@Table(name = "jkd_user_coupon",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"coupon_no"})
})
public class UserCoupon {
	public final static int USER_COUPON_STATUC_DELETE = -2;         //被禁用
	public final static int USER_COUPON_STATUS_OCCUPIED = -1;       //被占用
	public final static int USER_COUPON_STATUS_NOT_USED = 0;		//未使用
	public final static int USER_COUPON_STATUS_USED = 1;			//已使用
	public final static int USER_COUPON_STATUS_TIME_OUT = 2;		//已过期
	
	private Long userCouponId; 		// 用户优惠券ID
	private Long couponId; 			// 优惠券ID
	private Long userId; 			// 用户ID
	private String couponNo; 		// 优惠券号
	private Long couponTaskId; 		// 券对应任务ID，自动派送的优惠券为空
	private Integer couponStatus; 	// 优惠券状态（-2，被禁用，0，未使用。-1，被占用。1，已使用。2，已过期）
	private Boolean isShowGet;		// 显示领取
	private Boolean isGiftCoupon;   // 是否赠品
	private String receiveTime; 	// 优惠券领取时间(YYYY-MM-DD)
	private String effectiveTime; 	// 优惠券有效期(YYYY-MM-DD)
	private Boolean isRechargeProductCoupon; //是否充值赠送商品券
	private String productImagePath;  //商品图片
	private Long productId;         //优惠券商品id
	private Date createTime; 		// 创建时间
	private Date updateTime; 		// 更新时间
	
	//Transient
	private Coupon coupon;
	private CouponTask couponTask;
	private String effectiveTimeFormat;     //过期时间（已格式化）
	private String mobile;                  //用户手机号码
	private String nickName;                // 用户昵称
	private Boolean isLastDay;              //是否最后一天
	private String lastDayInfo;             //今日即将过期
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getUserCouponId() {
		return userCouponId;
	}

	public void setUserCouponId(Long userCouponId) {
		this.userCouponId = userCouponId;
	}

	@Column(name = "coupon_id")
	public Long getCouponId() {
		return couponId;
	}

	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}

	@Column(name = "coupon_task_id")
	public Long getCouponTaskId() {
		return couponTaskId;
	}

	public void setCouponTaskId(Long couponTaskId) {
		this.couponTaskId = couponTaskId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "coupon_no")
	public String getCouponNo() {
		return couponNo;
	}

	public void setCouponNo(String couponNo) {
		this.couponNo = couponNo;
	}

	@Column(name = "coupon_status")
	public Integer getCouponStatus() {
		return couponStatus;
	}

	public void setCouponStatus(Integer couponStatus) {
		this.couponStatus = couponStatus;
	}

	@Column(name = "receive_time")
	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	@Column(name = "is_gift_coupon")
	public Boolean getIsGiftCoupon() {
		return isGiftCoupon;
	}

	public void setIsGiftCoupon(Boolean isGiftCoupon) {
		this.isGiftCoupon = isGiftCoupon;
	}

	@Column(name = "effective_time")
	public String getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	
	@Column(name = "is_show_get")
	public Boolean getIsShowGet() {
		return isShowGet;
	}

	public void setIsShowGet(Boolean isShowGet) {
		this.isShowGet = isShowGet;
	}

	@Column(name = "is_recharge_product_coupon")
	public Boolean getIsRechargeProductCoupon() {
		return isRechargeProductCoupon;
	}

	public void setIsRechargeProductCoupon(Boolean isRechargeProductCoupon) {
		this.isRechargeProductCoupon = isRechargeProductCoupon;
	}

	@Column(name = "product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	@Column(name = "product_image_path")
	public String getProductImagePath() {
		return productImagePath;
	}

	public void setProductImagePath(String productImagePath) {
		this.productImagePath = productImagePath;
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
	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}
	
	@Transient
	public String getEffectiveTimeFormat() {
		return effectiveTimeFormat;
	}

	public void setEffectiveTimeFormat(String effectiveTimeFormat) {
		this.effectiveTimeFormat = effectiveTimeFormat;
	}
	
	@Transient
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Transient
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Transient
	public CouponTask getCouponTask() {
		return couponTask;
	}

	public void setCouponTask(CouponTask couponTask) {
		this.couponTask = couponTask;
	}

	@Transient
	public Boolean getIsLastDay() {
		return isLastDay;
	}

	public void setIsLastDay(Boolean isLastDay) {
		this.isLastDay = isLastDay;
	}

	@Transient
	public String getLastDayInfo() {
		return lastDayInfo;
	}

	public void setLastDayInfo(String lastDayInfo) {
		this.lastDayInfo = lastDayInfo;
	}

}
