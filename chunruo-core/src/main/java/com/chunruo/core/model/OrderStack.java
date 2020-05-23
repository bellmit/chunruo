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
import javax.persistence.UniqueConstraint;

/**
 * 批量导入缓存堆栈
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_order_stack",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"order_no"})
})
public class OrderStack {
	private Long orderStackId;			//序号
	private String orderNo;				//订单号
	private Long userId;				//归属用户ID
	private Boolean isSpceProduct;		//是否规格商品
	private Long productId;				//商品ID
	private Long productSpecId;			//商品规格ID
	private String groupProductInfo;	//组合商品信息
	private String groupKey;			//订单组合关键字
	private Integer quantity;			//订单数量	
	private String consignee; 		    //收货人
	private String consigneePhone; 	    //收货人电话
	private String address; 			//收货地址
	private Long provinceId; 		    //省ID
	private Long cityId; 			    //市ID
	private Long areaId; 			    //区ID
	private String identityName;		//身份证真实姓名
	private String identityNo; 			//买家身份证
	private Date createTime;			//创建时间
	private Date updateTime;			//更新时间
	
	//Transient
	private Double productAmount;		//商品总金额
	private Double taxAmount;			//税费总金额
	private String provinceName; 		//省名称
	private String cityName; 			//市名称
	private String areaName; 			//区名称
	private String productCode;			//商品货号
	private Boolean isError;			//是否错误
	private Boolean isNeedIdentity;		//是否需要身份证
	private String message;				//错误信息
	private List<UserCart> userCartList = new ArrayList<UserCart> ();
	
	// 转换地址
	@Transient
	public UserAddress getUserAddress() {
		UserAddress userAddress = new UserAddress ();
		userAddress.setProvinceId(this.getProvinceId());
		userAddress.setCityId(this.getCityId());
		userAddress.setAreaId(this.getAreaId());
		userAddress.setAddress(this.getAddress());
		userAddress.setName(this.getConsignee());
		userAddress.setMobile(this.getConsigneePhone());
		userAddress.setRealName(this.getIdentityName());
		userAddress.setIdentityNo(this.getIdentityNo());
		return userAddress;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getOrderStackId() {
		return orderStackId;
	}

	public void setOrderStackId(Long orderStackId) {
		this.orderStackId = orderStackId;
	}
	
	@Column(name="order_no", length=60)
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name="user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name="is_spce_product", columnDefinition = "INT DEFAULT 0")
	public Boolean getIsSpceProduct() {
		return isSpceProduct;
	}

	public void setIsSpceProduct(Boolean isSpceProduct) {
		this.isSpceProduct = isSpceProduct;
	}

	@Column(name="product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name="product_spec_id")
	public Long getProductSpecId() {
		return productSpecId;
	}

	public void setProductSpecId(Long productSpecId) {
		this.productSpecId = productSpecId;
	}

	@Column(name="group_Product_Info")
	public String getGroupProductInfo() {
		return groupProductInfo;
	}

	public void setGroupProductInfo(String groupProductInfo) {
		this.groupProductInfo = groupProductInfo;
	}
	
	@Column(name="group_key", length=50)
	public String getGroupKey() {
		return groupKey;
	}

	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}

	@Column(name="quantity")
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Column(name="consignee", length=50)
	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	@Column(name="consignee_phone", length=50)
	public String getConsigneePhone() {
		return consigneePhone;
	}

	public void setConsigneePhone(String consigneePhone) {
		this.consigneePhone = consigneePhone;
	}

	@Column(name="address", length=200)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name="province_id")
	public Long getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Long provinceId) {
		this.provinceId = provinceId;
	}

	@Column(name="city_id")
	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	@Column(name="area_id")
	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	@Column(name="identity_name", length=50)
	public String getIdentityName() {
		return identityName;
	}

	public void setIdentityName(String identityName) {
		this.identityName = identityName;
	}

	@Column(name="identity_no", length=30)
	public String getIdentityNo() {
		return identityNo;
	}

	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
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
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	@Transient
	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	@Transient
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Transient
	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	@Transient
	public Boolean getIsError() {
		return isError;
	}

	public void setIsError(Boolean isError) {
		this.isError = isError;
	}

	@Transient
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Transient
	public List<UserCart> getUserCartList() {
		return userCartList;
	}

	public void setUserCartList(List<UserCart> userCartList) {
		this.userCartList = userCartList;
	}

	@Transient
	public Boolean getIsNeedIdentity() {
		return isNeedIdentity;
	}

	public void setIsNeedIdentity(Boolean isNeedIdentity) {
		this.isNeedIdentity = isNeedIdentity;
	}

	@Transient
	public Double getProductAmount() {
		return productAmount;
	}

	public void setProductAmount(Double productAmount) {
		this.productAmount = productAmount;
	}

	@Transient
	public Double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(Double taxAmount) {
		this.taxAmount = taxAmount;
	}
}
