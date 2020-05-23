package com.chunruo.cache.portal.vo;

/**
 * 地址详情
 * @author chunruo
 *
 */
public class AddressVo {
	private String province;	//省份名称
	private Long provinceId;	//省份ID
	private Long cityId;        //城市ID
	private String city;		//城市名称
	private String district;	//详情地址
	
	public String getProvince() {
		return province;
	}
	
	public void setProvince(String province) {
		this.province = province;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getDistrict() {
		return district;
	}
	
	public void setDistrict(String district) {
		this.district = district;
	}

	public Long getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Long provinceId) {
		this.provinceId = provinceId;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}
}
