package com.chunruo.core.util.vo;

import java.io.Serializable;
import java.util.Date;

public class SmsCodeVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String mobile;
	private String smsCode;
	private String countryCode;
	private Integer codeType;
	private Date createTime;
	
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getCodeType() {
		return codeType;
	}

	public void setCodeType(Integer codeType) {
		this.codeType = codeType;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
}
