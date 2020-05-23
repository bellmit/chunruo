package com.chunruo.core.vo;


public class UserInfoVo {

	private Long userId;
	private String nickName;
	private String mobile;
	private Integer level;
	private String registerTime;
	private Integer number;
	private Boolean isSpecialInvite = false;//是否特殊用户邀请
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public Boolean getIsSpecialInvite() {
		return isSpecialInvite;
	}
	public void setIsSpecialInvite(Boolean isSpecialInvite) {
		this.isSpecialInvite = isSpecialInvite;
	}
}
