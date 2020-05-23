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
 * 申请退款退货列表
 */
@Entity
@Table(name = "jkd_refund",uniqueConstraints = {
		@UniqueConstraint(columnNames = {"refund_number"})
	})
public class Refund{
	public final static Integer REFUND_STATUS_INIT = 0; 		
	public final static Integer REFUND_STATUS_WAIT = 1; 		
	public final static Integer REFUND_STATUS_REFUSE = 2; 		
	public final static Integer REFUND_STATUS_SUCCESS = 3; 		
	public final static Integer REFUND_STATUS_RECEIPT = 4; 		
	public final static Integer REFUND_STATUS_COMPLETED = 5; 	
	public final static Integer REFUND_STATUS_TIMEOUT = 6; 		
	public final static Integer REFUND_TYPE_MONEY = 1; 	
	public final static Integer REFUND_TYPE_GOODS = 2; 	
	public final static Integer REFUND_TYPE_CANCEL = 3;
	public final static Integer REFUND_TYPE_PART = 4;   
	
	private Long refundId;				//序号
	private String refundNumber;		//退款单号
	private Long orderId;				//订单ID
	private Long orderItemId;			//订单商品ID
	private Boolean isCurrentTask;		//是否当前任务
	private Long productId;				//退货商品ID
	private String productPrice;		//商品原价格
	private String productName;			//商品名称
	private String totalAmount;			//商品总金额(含税)
	private Integer refundType;			//售后类型(1:退款;2:退款退货)
	private Integer refundCount;		//申请退货商品数量
	private String refundAmount;		//申请退货商品总金额			
	private Long reasonId;				//申请退货原因ID
	private Long userId;				//申请退货用户ID
	private Long storeId;				//申请退货店铺ID
	private Integer refundStatus;  		//退货状态
	private String refundExplain;		//退款说明
	private String expressNumber;		//退货回寄快递单号
	private String expressCompany;		//退货回寄物流公司
	private Boolean isGroupProduct;		//是否组合商品
	private String groupUniqueBatch;	//是否组合商品标识
	private Boolean isReceive;			//是否收到商品
	private Boolean isSendPackage;		//是否回寄商品
	private String refusalReason;		//拒绝原因(系统)
	private String refundAddress; 		//退货地址
	private String image1; 				//图片地址1
	private String image2; 				//图片地址2
	private String image3; 				//图片地址3
	private Date completedTime;			//完成时间
	private Date createTime;			//创建时间
	private Date updateTime;			//更新时间
	private String expressImage1;       //快递图片地址1
	private String expressImage2;       //快递图片地址2
	private String expressImage3;       //快递图片地址3
	private String expressExplain;      //快递备注
	private String remarks;             //备注
	private Integer remarkReasonId;        //备注退款原因
	
	//Transient
	private String userName;
	private String storeName;
	private String userMobile;
	private String reason;
	private int paymentType;
	private String orderNo;
	private RefundReason refundReason;
	private OrderItems orderItems;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getRefundId() {
		return refundId;
	}
	
	public void setRefundId(Long refundId) {
		this.refundId = refundId;
	}
	
	@Column(name = "refund_number")
	public String getRefundNumber() {
		return refundNumber;
	}
	
	public void setRefundNumber(String refundNumber) {
		this.refundNumber = refundNumber;
	}
	
	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}
	
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	@Column(name = "order_item_id")
	public Long getOrderItemId() {
		return orderItemId;
	}
	
	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}
	
	@Column(name = "is_current_task")
	public Boolean getIsCurrentTask() {
		return isCurrentTask;
	}

	public void setIsCurrentTask(Boolean isCurrentTask) {
		this.isCurrentTask = isCurrentTask;
	}

	@Column(name = "product_id")
	public Long getProductId() {
		return productId;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	@Column(name = "product_price")
	public String getProductPrice() {
		return productPrice;
	}
	
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}
	
	@Column(name = "product_name")
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	@Column(name = "total_amount")
	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Column(name = "refund_type")
	public Integer getRefundType() {
		return refundType;
	}

	public void setRefundType(Integer refundType) {
		this.refundType = refundType;
	}

	@Column(name = "refund_count")
	public Integer getRefundCount() {
		return refundCount;
	}
	
	public void setRefundCount(Integer refundCount) {
		this.refundCount = refundCount;
	}
	
	@Column(name = "refund_amount")
	public String getRefundAmount() {
		return refundAmount;
	}
	
	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}
	
	@Column(name = "is_group_product")
	public Boolean getIsGroupProduct() {
		return isGroupProduct;
	}

	public void setIsGroupProduct(Boolean isGroupProduct) {
		this.isGroupProduct = isGroupProduct;
	}

	@Column(name = "group_unique_batch")
	public String getGroupUniqueBatch() {
		return groupUniqueBatch;
	}

	public void setGroupUniqueBatch(String groupUniqueBatch) {
		this.groupUniqueBatch = groupUniqueBatch;
	}

	@Column(name = "reason_id")
	public Long getReasonId() {
		return reasonId;
	}
	
	public void setReasonId(Long reasonId) {
		this.reasonId = reasonId;
	}
	
	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name = "store_id")
	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	@Column(name = "refund_status")
	public Integer getRefundStatus() {
		return refundStatus;
	}
	
	public void setRefundStatus(Integer refundStatus) {
		this.refundStatus = refundStatus;
	}
	
	@Column(name = "refund_explain")
	public String getRefundExplain() {
		return refundExplain;
	}
	
	public void setRefundExplain(String refundExplain) {
		this.refundExplain = refundExplain;
	}
	
	@Column(name = "express_number")
	public String getExpressNumber() {
		return expressNumber;
	}
	
	public void setExpressNumber(String expressNumber) {
		this.expressNumber = expressNumber;
	}
	
	@Column(name = "express_company")
	public String getExpressCompany() {
		return expressCompany;
	}
	
	public void setExpressCompany(String expressCompany) {
		this.expressCompany = expressCompany;
	}
	
	@Column(name = "is_receive")
	public Boolean getIsReceive() {
		return isReceive;
	}
	
	public void setIsReceive(Boolean isReceive) {
		this.isReceive = isReceive;
	}
	
	@Column(name = "is_send_package")
	public Boolean getIsSendPackage() {
		return isSendPackage;
	}

	public void setIsSendPackage(Boolean isSendPackage) {
		this.isSendPackage = isSendPackage;
	}

	@Column(name = "refusal_reason")
	public String getRefusalReason() {
		return refusalReason;
	}
	
	public void setRefusalReason(String refusalReason) {
		this.refusalReason = refusalReason;
	}
	
	@Column(name = "refund_address")
	public String getRefundAddress() {
		return refundAddress;
	}

	public void setRefundAddress(String refundAddress) {
		this.refundAddress = refundAddress;
	}
	
	@Column(name = "image_1")
	public String getImage1() {
		return image1;
	}

	public void setImage1(String image1) {
		this.image1 = image1;
	}

	@Column(name = "image_2")
	public String getImage2() {
		return image2;
	}

	public void setImage2(String image2) {
		this.image2 = image2;
	}

	@Column(name = "image_3")
	public String getImage3() {
		return image3;
	}

	public void setImage3(String image3) {
		this.image3 = image3;
	}

	@Column(name = "completed_time")
	public Date getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(Date completedTime) {
		this.completedTime = completedTime;
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

	@Column(name = "express_image_1")
	public String getExpressImage1() {
		return expressImage1;
	}

	public void setExpressImage1(String expressImage1) {
		this.expressImage1 = expressImage1;
	}
	
	@Column(name = "express_image_2")
	public String getExpressImage2() {
		return expressImage2;
	}

	public void setExpressImage2(String expressImage2) {
		this.expressImage2 = expressImage2;
	}
	
	@Column(name = "express_image_3")
	public String getExpressImage3() {
		return expressImage3;
	}

	public void setExpressImage3(String expressImage3) {
		this.expressImage3 = expressImage3;
	}
	
	@Column(name = "express_explain")
	public String getExpressExplain() {
		return expressExplain;
	}

	public void setExpressExplain(String expressExplain) {
		this.expressExplain = expressExplain;
	}

	@Column(name = "remarks")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	@Column(name = "remark_reason_id")
	public Integer getRemarkReasonId() {
		return remarkReasonId;
	}

	public void setRemarkReasonId(Integer remarkReasonId) {
		this.remarkReasonId = remarkReasonId;
	}

	@Transient
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Transient
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	@Transient
	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	@Transient
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Transient
	public RefundReason getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(RefundReason refundReason) {
		this.refundReason = refundReason;
	}
	
	@Transient
	public int getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}
	
	@Transient
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Transient
	public OrderItems getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(OrderItems orderItems) {
		this.orderItems = orderItems;
	}

	
}
