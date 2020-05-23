package com.chunruo.core.util.vo;

import java.io.Serializable;

public class TokenVo implements Serializable{
	private static final long serialVersionUID = 6549764955525049151L;
	private Long userId;
	private Integer level;
	private Long lastLoginTime;
	private String version;
	private String loginPcIp;
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLoginPcIp() {
		return loginPcIp;
	}

	public void setLoginPcIp(String loginPcIp) {
		this.loginPcIp = loginPcIp;
	}
}
