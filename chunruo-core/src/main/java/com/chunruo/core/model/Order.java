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
import javax.persistence.UniqueConstraint;

/**
 * 订单列表
 * @author chunruo
 */
@Entity
@Table(name = "jkd_order",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"order_no"})
})
public class Order implements Serializable{
	private static final long serialVersionUID = 4585738000953055182L;
	private Long orderId; 				// 序号
	private Long parentOrderId; 		// 子单父类ID
	private String orderNo; 			// 订单号
	private String tradeNo; 			// 商家交易流水号(纯若)
	private Long userId; 				// 买家用户ID
	private Double postage; 			// 邮费
	private Double tax; 				// 增值税
	private Double postageTax;          // 邮费税费
	private Double productAmount; 		// 商品金额（不含邮费）
	private Double orderAmount; 		// 订单金额（含邮费）
	private Double totalRealSellPrice;  // 订单总拿货价
	private Double payAmount; 			// 实际付款金额
	private Integer paymentType; 		// 支付方式(0:微信支付;1:支付宝支付)
	private Boolean isPaymentSucc; 		// 是否支付成功
	private Long weChatConfigId; 		// 微信支付ConfigId
	private Integer productNumber; 		// 商品总件数
	private String buyerMessage; 		// 买家留言
	private String remarks; 			// 备注
	private Integer cancelMethod; 		// 订单取消方式(0:默认;1:过期自动取消;2:卖家手动取消;3:买家手动取消)
	private Boolean isSplitSingle; 		// 是否拆单
	private Boolean isSubOrder; 		// 是否子订单(拆单)
	private Boolean isCheck; 			// 是否对账(1:未对账;2:已对账)
	private Boolean isDelete; 			// 是否删除
	private Boolean isIntercept;		// 是否被拦截
	private Boolean isInvitationAgent;	// 是否邀请代理
	private Boolean isNoStoreBuyAgent;	// 是否普通用户购买代理
	private String noStoreByMobile;		// 普通无店铺用户购买验证手机号码
	private Boolean isSeckillProduct;	// 是否秒杀订单
	private Boolean isSeckillLimit;     // 是否秒杀限购
	private Boolean isGroupProduct;		// 是否组合订单
	private Boolean isMyselfStore;      // 是否自己店铺下单
	private Double packageYearNumber;   // 购买的大礼包年份
	private Long cancelReasonId;        // 取消原因id
	private Integer unDeliverStatus;    // 代发货订单状态
	private Boolean isShareBuy;         // 是否分享商品购买
	private Boolean isNeedCheckPayment; // 是否支付实名认证
	private Integer status; 			// 订单状态(1:未支付;2:未发货;3:已发货;4:已完成;5:已取消;6:退款中,7:待评价)
	private Long wareHouseId; 			// 所属仓库ID
	private Integer productType; 		// 商品类型(1:国内;2:跨境;3:直邮)
	private Boolean isUserCoupon;		// 是否使用优惠券
	private Long userCouponId;          // 优惠券ID
	private Double preferentialAmount;  // 优惠金额
	private Long paymentRecordId;		// 订单支付成功
	private Double payAccountAmount;    // 账户余额支付金额
	private Boolean isUseAccount;       // 是否使用账户余额支付
	private Integer level;              // 用户下单时等级
	private Integer loginType;			// 用户登录类型
	
	private Boolean isFriendPayment;	// 是否朋友代付
	private Boolean isRechargeProductCoupon;// 是否使用充值赠送商品券
	private Boolean isAdvanceSale;      // 是否缺货预售
	private Integer memberGiftType;     // 会员礼包类型(1:无赠品 2：赠品为优惠券 3:赠品为商品)
	private Integer memberUserLevel;    // 购买会员等级
	private String couponIds;          // 购买会员
	private Long memberTemplateId;      // 会员年限id
	private Long memberGiftId;          // 会员赠品id
	private Boolean isTemporaryMember;  // 是否临时试用经销商
	private Boolean isLevelLimitProduct;//是否等级限购订单
	private Long topUserId; 			// 上线用户ID
	private Long storeId;               // 店铺ID
	private Long shareUserId;           // 分享用户ID
	private Double profitTop; 		    // 上线利润
	private Double profitSub; 			// 下线利润

	private Integer buyWayType; 		// 物流方式(0:快递发货;1:上门自提)
	private String consignee; 		    // 收货人
	private String consigneePhone; 	    // 收货人电话
	private String address; 			// 收货地址
	private Long provinceId; 		    // 省ID
	private Long cityId; 			    // 市ID
	private Long areaId; 			    // 区ID
	private String identityName;		// 身份证真实姓名
	private String identityNo; 		// 买家身份证
	private String identityFront; 	// 买家身份证-正面
	private String identityBack; 	// 买家身份证-反面

	private Boolean isPushComplete;     // 完成订单是否推送ERP
	private Integer syncCompleteNumber; // 完成订单同步次数
	private Boolean isPushErp; 			// 订单是否推送ERP
	private Boolean isDirectPushErp; 	// 是否可以直接推送ERP
	private Boolean isPushCustoms;		// 是否需要支付报关
	private Boolean isRequestPushCustoms;// 是否已请求报关支付
	private Boolean isSyncExpress; 		// 是否同步快递信息
	private int syncNumber;				// 同步次数
	private String batchNumber;			// 同步批次号
	private String errorMsg;			// 错误信息
	private Date syncTime; 				// 同步时间
	private String productNames;		// 商品名称集合|分割

	private Date payTime; 				// 支付付款时间
	private Date sentTime; 				// 发货时间
	private Date deliveryTime; 			// 收货时间
	private Date cancelTime; 			// 取消时间
	private Date complateTime; 			// 完成时间
	private Date refundTime; 			// 退款时间
	private Date createTime; 			// 创建时间
	private Date updateTime; 			// 更新时间

	@Transient
	private List<OrderItems> orderItemsList = new ArrayList<OrderItems>();
	private List<Long> lockStockProductIdList = new ArrayList<Long> ();
	private Integer totalNumber; 
	private String province; 	//省 
	private String city;		//市 
	private String cityarea;	//区
	private String storeName;
	private String userName;
	private String wareHouseName;
	private String topStoreName;
	private String fullAddress;
	private String acceptPayName;
	private String storeMobile;
	private Long endPaymentTime;
	private String cancelReason;       //取消订单原因
	private Integer logisticsStatus;   //物流状态
	private Integer userLevel;         //用户等级
	private Double totalDiscount;	   //总优惠金额(优惠券+余额)
	private Integer refundStatus = 0; //退款状态
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Column(name = "parent_order_id")
	public Long getParentOrderId() {
		return parentOrderId;
	}

	public void setParentOrderId(Long parentOrderId) {
		this.parentOrderId = parentOrderId;
	}

	@Column(name = "is_split_single")
	public Boolean getIsSplitSingle() {
		return isSplitSingle;
	}

	public void setIsSplitSingle(Boolean isSplitSingle) {
		this.isSplitSingle = isSplitSingle;
	}

	@Column(name = "is_sub_order")
	public Boolean getIsSubOrder() {
		return isSubOrder;
	}

	public void setIsSubOrder(Boolean isSubOrder) {
		this.isSubOrder = isSubOrder;
	}

	@Column(name = "is_intercept")
	public Boolean getIsIntercept() {
		return isIntercept;
	}

	public void setIsIntercept(Boolean isIntercept) {
		this.isIntercept = isIntercept;
	}

	@Column(name = "is_invitation_agent")
	public Boolean getIsInvitationAgent() {
		return isInvitationAgent;
	}

	public void setIsInvitationAgent(Boolean isInvitationAgent) {
		this.isInvitationAgent = isInvitationAgent;
	}
	
	@Column(name = "is_no_store_buy_agent")
	public Boolean getIsNoStoreBuyAgent() {
		return isNoStoreBuyAgent;
	}

	public void setIsNoStoreBuyAgent(Boolean isNoStoreBuyAgent) {
		this.isNoStoreBuyAgent = isNoStoreBuyAgent;
	}
	
	@Column(name = "no_store_by_mobile")
	public String getNoStoreByMobile() {
		return noStoreByMobile;
	}

	public void setNoStoreByMobile(String noStoreByMobile) {
		this.noStoreByMobile = noStoreByMobile;
	}

	@Column(name = "is_seckill_product")
	public Boolean getIsSeckillProduct() {
		return isSeckillProduct;
	}

	public void setIsSeckillProduct(Boolean isSeckillProduct) {
		this.isSeckillProduct = isSeckillProduct;
	}

	@Column(name = "is_seckill_limit")
	public Boolean getIsSeckillLimit() {
		return isSeckillLimit;
	}

	public void setIsSeckillLimit(Boolean isSeckillLimit) {
		this.isSeckillLimit = isSeckillLimit;
	}

	@Column(name = "is_myself_store")
	public Boolean getIsMyselfStore() {
		return isMyselfStore;
	}

	public void setIsMyselfStore(Boolean isMyselfStore) {
		this.isMyselfStore = isMyselfStore;
	}

	@Column(name = "order_no")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name = "trade_no")
	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "postage")
	public Double getPostage() {
		return postage;
	}

	public void setPostage(Double postage) {
		this.postage = postage;
	}

	@Column(name = "tax")
	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	@Column(name = "postage_tax")
	public Double getPostageTax() {
		return postageTax;
	}

	public void setPostageTax(Double postageTax) {
		this.postageTax = postageTax;
	}

	@Column(name = "product_amount")
	public Double getProductAmount() {
		return productAmount;
	}

	public void setProductAmount(Double productAmount) {
		this.productAmount = productAmount;
	}

	@Column(name = "order_amount")
	public Double getOrderAmount() {
		return orderAmount;
	}
	
	public void setOrderAmount(Double orderAmount) {
		this.orderAmount = orderAmount;
	}

	@Column(name = "is_share_buy")
	public Boolean getIsShareBuy() {
		return isShareBuy;
	}

	public void setIsShareBuy(Boolean isShareBuy) {
		this.isShareBuy = isShareBuy;
	}

	@Column(name = "is_need_check_payment")
	public Boolean getIsNeedCheckPayment() {
		return isNeedCheckPayment;
	}

	public void setIsNeedCheckPayment(Boolean isNeedCheckPayment) {
		this.isNeedCheckPayment = isNeedCheckPayment;
	}

	@Column(name = "consignee")
	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	@Column(name = "consignee_phone")
	public String getConsigneePhone() {
		return consigneePhone;
	}

	public void setConsigneePhone(String consigneePhone) {
		this.consigneePhone = consigneePhone;
	}

	@Column(name = "payment_type", length = 1)
	public Integer getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}
	
	@Column(name = "is_payment_succ")
	public Boolean getIsPaymentSucc() {
		return isPaymentSucc;
	}

	public void setIsPaymentSucc(Boolean isPaymentSucc) {
		this.isPaymentSucc = isPaymentSucc;
	}
	
	@Column(name = "we_chat_config_id")
	public Long getWeChatConfigId() {
		return weChatConfigId;
	}

	public void setWeChatConfigId(Long weChatConfigId) {
		this.weChatConfigId = weChatConfigId;
	}

	@Column(name = "product_number")
	public Integer getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(Integer productNumber) {
		this.productNumber = productNumber;
	}

	@Column(name = "status", length = 1)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "ware_house_id")
	public Long getWareHouseId() {
		return wareHouseId;
	}

	public void setWareHouseId(Long wareHouseId) {
		this.wareHouseId = wareHouseId;
	}
	

	@Column(name = "buyer_message")
	public String getBuyerMessage() {
		return buyerMessage;
	}

	public void setBuyerMessage(String buyerMessage) {
		this.buyerMessage = buyerMessage;
	}

	@Column(name = "remarks")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "pay_money")
	public Double getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(Double payAmount) {
		this.payAmount = payAmount;
	}

	@Column(name = "pay_account_amount")
	public Double getPayAccountAmount() {
		return payAccountAmount;
	}

	public void setPayAccountAmount(Double payAccountAmount) {
		this.payAccountAmount = payAccountAmount;
	}

	@Column(name = "is_use_account", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsUseAccount() {
		return isUseAccount;
	}

	public void setIsUseAccount(Boolean isUseAccount) {
		this.isUseAccount = isUseAccount;
	}

	@Column(name = "cancel_method", length = 1)
	public Integer getCancelMethod() {
		return cancelMethod;
	}

	public void setCancelMethod(Integer cancelMethod) {
		this.cancelMethod = cancelMethod;
	}

	@Column(name = "cancel_reason_id")
	public Long getCancelReasonId() {
		return cancelReasonId;
	}

	public void setCancelReasonId(Long cancelReasonId) {
		this.cancelReasonId = cancelReasonId;
	}

	@Column(name = "is_check", length = 1)
	public Boolean getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(Boolean isCheck) {
		this.isCheck = isCheck;
	}

	@Column(name = "is_push_complete", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsPushComplete() {
		return isPushComplete;
	}

	public void setIsPushComplete(Boolean isPushComplete) {
		this.isPushComplete = isPushComplete;
	}

	@Column(name = "is_level_limit_product", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsLevelLimitProduct() {
		return isLevelLimitProduct;
	}

	public void setIsLevelLimitProduct(Boolean isLevelLimitProduct) {
		this.isLevelLimitProduct = isLevelLimitProduct;
	}

	@Column(name = "is_push_erp")
	public Boolean getIsPushErp() {
		return isPushErp;
	}

	public void setIsPushErp(Boolean isPushErp) {
		this.isPushErp = isPushErp;
	}
	
	@Column(name = "is_direct_push_erp")
	public Boolean getIsDirectPushErp() {
		return isDirectPushErp;
	}

	public void setIsDirectPushErp(Boolean isDirectPushErp) {
		this.isDirectPushErp = isDirectPushErp;
	}
	
	@Column(name = "is_push_customs")
	public Boolean getIsPushCustoms() {
		return isPushCustoms;
	}

	public void setIsPushCustoms(Boolean isPushCustoms) {
		this.isPushCustoms = isPushCustoms;
	}

	@Column(name = "is_request_push_customs")
	public Boolean getIsRequestPushCustoms() {
		return isRequestPushCustoms;
	}

	public void setIsRequestPushCustoms(Boolean isRequestPushCustoms) {
		this.isRequestPushCustoms = isRequestPushCustoms;
	}

	@Column(name = "member_gift_type")
	public Integer getMemberGiftType() {
		return memberGiftType;
	}

	public void setMemberGiftType(Integer memberGiftType) {
		this.memberGiftType = memberGiftType;
	}

	@Column(name = "member_user_level")
	public Integer getMemberUserLevel() {
		return memberUserLevel;
	}

	public void setMemberUserLevel(Integer memberUserLevel) {
		this.memberUserLevel = memberUserLevel;
	}

	@Column(name = "coupon_ids")
	public String getCouponIds() {
		return couponIds;
	}

	public void setCouponIds(String couponIds) {
		this.couponIds = couponIds;
	}

	@Column(name = "member_template_id")
	public Long getMemberTemplateId() {
		return memberTemplateId;
	}

	public void setMemberTemplateId(Long memberTemplateId) {
		this.memberTemplateId = memberTemplateId;
	}

	@Column(name = "member_gift_id")
	public Long getMemberGiftId() {
		return memberGiftId;
	}

	public void setMemberGiftId(Long memberGiftId) {
		this.memberGiftId = memberGiftId;
	}

	@Column(name = "is_temporary_member", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsTemporaryMember() {
		return isTemporaryMember;
	}

	public void setIsTemporaryMember(Boolean isTemporaryMember) {
		this.isTemporaryMember = isTemporaryMember;
	}

	@Column(name = "is_advance_sale")
	public Boolean getIsAdvanceSale() {
		return isAdvanceSale;
	}

	public void setIsAdvanceSale(Boolean isAdvanceSale) {
		this.isAdvanceSale = isAdvanceSale;
	}

	@Column(name = "is_sync_express")
	public Boolean getIsSyncExpress() {
		return isSyncExpress;
	}

	public void setIsSyncExpress(Boolean isSyncExpress) {
		this.isSyncExpress = isSyncExpress;
	}

	@Column(name="sync_complete_number", columnDefinition = "INT DEFAULT 0")
	public Integer getSyncCompleteNumber() {
		return syncCompleteNumber;
	}

	public void setSyncCompleteNumber(Integer syncCompleteNumber) {
		this.syncCompleteNumber = syncCompleteNumber;
	}

	@Column(name="sync_number", columnDefinition = "INT DEFAULT 0")
	public int getSyncNumber() {
		return syncNumber;
	}

	public void setSyncNumber(int syncNumber) {
		this.syncNumber = syncNumber;
	}
	
	@Column(name="batch_number", length=250)
	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	
	@Column(name="error_msg", length=250)
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Column(name="sync_time")
	public Date getSyncTime() {
		return syncTime;
	}

	public void setSyncTime(Date syncTime) {
		this.syncTime = syncTime;
	}

	@Column(name = "top_user_id")
	public Long getTopUserId() {
		return topUserId;
	}

	public void setTopUserId(Long topUserId) {
		this.topUserId = topUserId;
	}
	
	@Column(name = "store_id")
	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	@Column(name = "share_user_id")
	public Long getShareUserId() {
		return shareUserId;
	}

	public void setShareUserId(Long shareUserId) {
		this.shareUserId = shareUserId;
	}

	@Column(name = "profit_top")
	public Double getProfitTop() {
		return profitTop;
	}

	@Column(name = "profit_sub")
	public Double getProfitSub() {
		return profitSub;
	}

	public void setProfitSub(Double profitSub) {
		this.profitSub = profitSub;
	}

	public void setProfitTop(Double profitTop) {
		this.profitTop = profitTop;
	}
	
	@Column(name = "is_friend_payment", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsFriendPayment() {
		return isFriendPayment;
	}

	public void setIsFriendPayment(Boolean isFriendPayment) {
		this.isFriendPayment = isFriendPayment;
	}

	@Column(name = "is_recharge_product_coupon", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsRechargeProductCoupon() {
		return isRechargeProductCoupon;
	}

	public void setIsRechargeProductCoupon(Boolean isRechargeProductCoupon) {
		this.isRechargeProductCoupon = isRechargeProductCoupon;
	}
	
	@Column(name = "buy_way_type", length = 1)
	public Integer getBuyWayType() {
		return buyWayType;
	}

	public void setBuyWayType(Integer buyWayType) {
		this.buyWayType = buyWayType;
	}
	
	@Column(name = "un_deliver_status", length = 1)
	public Integer getUnDeliverStatus() {
		return unDeliverStatus;
	}

	public void setUnDeliverStatus(Integer unDeliverStatus) {
		this.unDeliverStatus = unDeliverStatus;
	}

	@Column(name = "address", length = 1000)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address == null ? null : address.trim();
	}

	@Column(name = "identity_name")
	public String getIdentityName() {
		return identityName;
	}

	public void setIdentityName(String identityName) {
		this.identityName = identityName;
	}

	@Column(name = "identity_no")
	public String getIdentityNo() {
		return identityNo;
	}

	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo == null ? null : identityNo.trim();
	}
	
	@Column(name = "package_year_number")
	public Double getPackageYearNumber() {
		return packageYearNumber;
	}

	public void setPackageYearNumber(Double packageYearNumber) {
		this.packageYearNumber = packageYearNumber;
	}

	@Column(name = "sent_time")
	public Date getSentTime() {
		return sentTime;
	}

	public void setSentTime(Date sentTime) {
		this.sentTime = sentTime;
	}

	@Column(name = "delivery_time")
	public Date getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	@Column(name = "total_real_sell_price")
	public Double getTotalRealSellPrice() {
		return totalRealSellPrice;
	}

	public void setTotalRealSellPrice(Double totalRealSellPrice) {
		this.totalRealSellPrice = totalRealSellPrice;
	}

	@Column(name = "cancel_time")
	public Date getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}

	@Column(name = "complate_time")
	public Date getComplateTime() {
		return complateTime;
	}

	public void setComplateTime(Date complateTime) {
		this.complateTime = complateTime;
	}

	@Column(name = "refund_time")
	public Date getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(Date refundTime) {
		this.refundTime = refundTime;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "level")
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	
	@Column(name = "login_type")
	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	@Column(name = "pay_time")
	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "is_delete")
	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Column(name = "province_id")
	public Long getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Long provinceId) {
		this.provinceId = provinceId;
	}

	@Column(name = "city_id")
	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	@Column(name = "area_id")
	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	@Column(name = "product_type")
	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}
	
	@Column(name = "is_user_coupon")
	public Boolean getIsUserCoupon() {
		return isUserCoupon;
	}

	public void setIsUserCoupon(Boolean isUserCoupon) {
		this.isUserCoupon = isUserCoupon;
	}

	@Column(name = "user_coupon_Id")
	public Long getUserCouponId() {
		return userCouponId;
	}

	public void setUserCouponId(Long userCouponId) {
		this.userCouponId = userCouponId;
	}
	
	@Column(name = "preferential_amount")
	public Double getPreferentialAmount() {
		return preferentialAmount;
	}

	public void setPreferentialAmount(Double preferentialAmount) {
		this.preferentialAmount = preferentialAmount;
	}
	
	@Column(name = "payment_record_id")
	public Long getPaymentRecordId() {
		return paymentRecordId;
	}

	public void setPaymentRecordId(Long paymentRecordId) {
		this.paymentRecordId = paymentRecordId;
	}

	@Column(name = "identity_front")
	public String getIdentityFront() {
		return identityFront;
	}

	public void setIdentityFront(String identityFront) {
		this.identityFront = identityFront;
	}
	
	@Column(name = "identity_back")
	public String getIdentityBack() {
		return identityBack;
	}

	public void setIdentityBack(String identityBack) {
		this.identityBack = identityBack;
	}

	@Column(name = "is_group_product")
	public Boolean getIsGroupProduct() {
		return isGroupProduct;
	}

	public void setIsGroupProduct(Boolean isGroupProduct) {
		this.isGroupProduct = isGroupProduct;
	}
	
	@Transient
	public List<OrderItems> getOrderItemsList() {
		return orderItemsList;
	}

	public void setOrderItemsList(List<OrderItems> orderItemsList) {
		this.orderItemsList = orderItemsList;
	}

	@Transient
	public Integer getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}

	@Transient
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	@Transient
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Transient
	public String getCityarea() {
		return cityarea;
	}

	public void setCityarea(String cityarea) {
		this.cityarea = cityarea;
	}

	@Transient
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	@Transient
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Transient
	public String getTopStoreName() {
		return topStoreName;
	}

	public void setTopStoreName(String topStoreName) {
		this.topStoreName = topStoreName;
	}

	@Transient
	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	@Transient
	public String getWareHouseName() {
		return wareHouseName;
	}

	public void setWareHouseName(String wareHouseName) {
		this.wareHouseName = wareHouseName;
	}

	@Transient
	public String getAcceptPayName() {
		return acceptPayName;
	}

	public void setAcceptPayName(String acceptPayName) {
		this.acceptPayName = acceptPayName;
	}
	
	@Transient
	public String getStoreMobile() {
		return storeMobile;
	}

	public void setStoreMobile(String storeMobile) {
		this.storeMobile = storeMobile;
	}

	@Transient
	public Long getEndPaymentTime() {
		return endPaymentTime;
	}

	public void setEndPaymentTime(Long endPaymentTime) {
		this.endPaymentTime = endPaymentTime;
	}

	@Transient
	public List<Long> getLockStockProductIdList() {
		return lockStockProductIdList;
	}

	public void setLockStockProductIdList(List<Long> lockStockProductIdList) {
		this.lockStockProductIdList = lockStockProductIdList;
	}

	@Transient
	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	@Transient
	public Integer getLogisticsStatus() {
		return logisticsStatus;
	}

	public void setLogisticsStatus(Integer logisticsStatus) {
		this.logisticsStatus = logisticsStatus;
	}

	@Transient
	public Integer getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(Integer userLevel) {
		this.userLevel = userLevel;
	}

	@Transient
	public Double getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	@Transient
	public String getProductNames() {
		return productNames;
	}

	public void setProductNames(String productNames) {
		this.productNames = productNames;
	}

	@Transient
	public Integer getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(Integer refundStatus) {
		this.refundStatus = refundStatus;
	}
}