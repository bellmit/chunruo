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
 * 收货地址管理
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_user_address")
public class UserAddress {
    private Long addressId;				//序号
    private Long userId;				//用户ID
    private String name;				//收货人
    private String mobile;				//手机号码
    private Long provinceId;			//省份
    private String provinceName;		//省份名称
    private Long cityId;				//城市
    private String cityName;			//城市名称
    private Long areaId;				//区域
    private String areaName;			//区域名称
    private String address;				//详细地址
    private String zipcode;				//邮编
    private String realName;			//真实姓名
    private String identityNo;			//身份证号码
    private String identityFront;		//身份证正面
    private String identityBack;		//身份证反面
    private Boolean isDefault;			//是否默认地址
    private Date createTime;			//创建时间
    private Date updateTime;			//更新时间
    
    //Transient
    private Boolean isChecked;			//是否选择
    private Boolean isHavRealInfo;		//是否有真实信息(身份证)
    private String fullAddress;			//全地址
    private String msg;                 //地址显示信息
    private String identityFrontData;   //身份证正面base64
    private String identityBackData;    //身份证反面base64
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

    @Column(name="user_id")
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

    @Column(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    @Column(name="mobile")
    public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name="province_id")
	public Long getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Long provinceId) {
		this.provinceId = provinceId;
	}

	@Column(name="province_name")
	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	@Column(name="city_id")
	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	@Column(name="city_name")
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Column(name="area_id")
	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	@Column(name="area_name")
	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

    @Column(name="address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    @Column(name="zip_code")
    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode == null ? null : zipcode.trim();
    }

    @Column(name="real_name")
	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	 @Column(name="identity_no")
	public String getIdentityNo() {
		return identityNo;
	}

	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}
	
	@Column(name="identity_front")
	public String getIdentityFront() {
		return identityFront;
	}

	public void setIdentityFront(String identityFront) {
		this.identityFront = identityFront;
	}

	@Column(name="identity_back")
	public String getIdentityBack() {
		return identityBack;
	}

	public void setIdentityBack(String identityBack) {
		this.identityBack = identityBack;
	}

	@Column(name="is_default")
	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
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
	public Boolean getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(Boolean isChecked) {
		this.isChecked = isChecked;
	}

	@Transient
	public Boolean getIsHavRealInfo() {
		return isHavRealInfo;
	}

	public void setIsHavRealInfo(Boolean isHavRealInfo) {
		this.isHavRealInfo = isHavRealInfo;
	}

	@Transient
	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}
	
	@Transient
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@Transient
	public String getIdentityFrontData() {
		return identityFrontData;
	}

	public void setIdentityFrontData(String identityFrontData) {
		this.identityFrontData = identityFrontData;
	}

	@Transient
	public String getIdentityBackData() {
		return identityBackData;
	}

	public void setIdentityBackData(String identityBackData) {
		this.identityBackData = identityBackData;
	}
}