package com.chunruo.core.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 地址详情
 * @author chunruo
 *
 */
public class AddressInfoVo implements Serializable {
	private static final long serialVersionUID = 9022111639068296727L;
	private Long addressId;				//序号
    private Long userId;				//用户ID
    private String name;				//收货人
    private String mobile;				//手机号码
    private Long provinceId;			//省份
    private String provinceName;		//省份名称
    private Long cityId;				//城市
    private String cityName;			//城市名称
    private Long countryId;				//区域
    private String countryName;			//区域名称
    private String address;				//详细地址
    private String zipcode;				//邮编
    private String realName;			//真实姓名
    private String identityNo;			//身份证号码
    private Boolean isDefault;			//是否默认地址
    private Date createTime;			//创建时间
    private Date updateTime;			//更新时间
    private String msg;                 //提示信息
    private Integer isEmpty;            //地址是否为空
    
	public Long getAddressId() {
		return addressId;
	}
	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Long getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(Long provinceId) {
		this.provinceId = provinceId;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public Long getCityId() {
		return cityId;
	}
	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public Long getCountryId() {
		return countryId;
	}
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getIdentityNo() {
		return identityNo;
	}
	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Integer getIsEmpty() {
		return isEmpty;
	}
	public void setIsEmpty(Integer isEmpty) {
		this.isEmpty = isEmpty;
	}
}
