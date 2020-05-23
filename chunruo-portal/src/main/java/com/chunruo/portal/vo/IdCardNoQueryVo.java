package com.chunruo.portal.vo;

/**
 * 身份证查询对象
 * @author chunruo
 */
public class IdCardNoQueryVo {
	private String idCardNo;		//身份证号码
	private String identityFront;   //身份证正面照
	private String identityBack;    //身份证反面照

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public String getIdentityFront() {
		return identityFront;
	}

	public void setIdentityFront(String identityFront) {
		this.identityFront = identityFront;
	}

	public String getIdentityBack() {
		return identityBack;
	}

	public void setIdentityBack(String identityBack) {
		this.identityBack = identityBack;
	}
}
