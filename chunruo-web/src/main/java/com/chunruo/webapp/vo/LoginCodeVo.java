package com.chunruo.webapp.vo;

/**
 * 登陆验证码
 * @author chunruo
 *
 */
public class LoginCodeVo {
	private String username;
	private String mobile;
	private String smsCode;
	
	public static LoginCodeVo setLoginCode(String username, String mobile, String smsCode){
		LoginCodeVo loginCode = new LoginCodeVo ();
		loginCode.setUsername(username);
		loginCode.setMobile(mobile);
		loginCode.setSmsCode(smsCode);
		return loginCode;
	} 
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
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
}	
