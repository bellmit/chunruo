package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 限购
 * @author Administrator
 */
@Entity
@Table(name = "jkd_purchase_limit")
public class PurchaseLimit {

	public static final Integer PURCHASE_LLIMIT_SUBSCRIBER = 1;//订购人限购 
	public static final Integer PURCHASE_LLIMIT_USER = 2;      //用户限购 
	public static final Integer PURCHASE_LLIMIT_USERLEVEL = 3; //等级限购
	private Long limitId;
	private Integer type; 			// 类型（1：订购人限购 2：用户限购 3：等级限购）
	private Integer limitNumber; 	// 限购数量
	private Long productId; 		// 商品id
	private Integer hours; 			// 几小时内限购数量
	private Integer v1Number; 		// v1预留存库
	private Integer v2Number; 		// v2预留库存
	private Long adminUserId;
	private Boolean isEnable;       //是否启用
	private Boolean isDelete;       //是否删除

	private Date createTime;
	private Date updateTime;
	
	private String productName;
	private String adminUserName;
	private Integer stockNumber;  //商品库存

	@Id
	@GeneratedValue
	public Long getLimitId() {
		return limitId;
	}

	public void setLimitId(Long limitId) {
		this.limitId = limitId;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "limit_number")
	public Integer getLimitNumber() {
		return limitNumber;
	}

	public void setLimitNumber(Integer limitNumber) {
		this.limitNumber = limitNumber;
	}

	@Column(name = "product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "hours")
	public Integer getHours() {
		return hours;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}

	@Column(name = "v1_number")
	public Integer getV1Number() {
		return v1Number;
	}

	public void setV1Number(Integer v1Number) {
		this.v1Number = v1Number;
	}

	@Column(name = "v2_number")
	public Integer getV2Number() {
		return v2Number;
	}

	public void setV2Number(Integer v2Number) {
		this.v2Number = v2Number;
	}

	@Column(name = "admin_user_id")
	public Long getAdminUserId() {
		return adminUserId;
	}

	public void setAdminUserId(Long adminUserId) {
		this.adminUserId = adminUserId;
	}

	@Column(name = "is_enable")
	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	@Column(name = "is_delete")
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
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Transient
	public String getAdminUserName() {
		return adminUserName;
	}

	public void setAdminUserName(String adminUserName) {
		this.adminUserName = adminUserName;
	}

	@Transient
	public Integer getStockNumber() {
		return stockNumber;
	}

	public void setStockNumber(Integer stockNumber) {
		this.stockNumber = stockNumber;
	}

}
