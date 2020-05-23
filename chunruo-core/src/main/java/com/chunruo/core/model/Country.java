package com.chunruo.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 国家列表
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_country")
public class Country{
	private Long countryId;		  	//序号
	private String countryName;		//国家名称
	private String telCode;  		//区号
	private String area;  			//所在的洲
	private Boolean status;			//状态
	private Boolean isProductShow;	//是否商品归属国家显示
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getCountryId() {
		return countryId;
	}
    
	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}
	
	@Column(name="country_name")
	public String getCountryName() {
		return countryName;
	}
	
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	@Column(name="tel_code")
	public String getTelCode() {
		return telCode;
	}

	public void setTelCode(String telCode) {
		this.telCode = telCode;
	}
	
	@Column(name="status")
	public Boolean getStatus() {
		return status;
	}
	
	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Column(name="area")
	public String getArea() {
		return area;
	}
	
	public void setArea(String area) {
		this.area = area;
	}

	@Column(name="is_product_show")
	public Boolean getIsProductShow() {
		return isProductShow;
	}

	public void setIsProductShow(Boolean isProductShow) {
		this.isProductShow = isProductShow;
	}
}