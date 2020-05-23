package com.chunruo.cache.portal.vo;

import com.chunruo.core.model.UserInfo;

/**
 * PC微信登录授权
 * @author chunruo
 */
public class WeixinAuthLoginVo {
	public final static int LOGIN_STATUS_INIT = 1;		//请求初始化
	public final static int LOGIN_STATUS_FAIL = 2;		//请求失败
	public final static int LOGIN_STATUS_AUTH = 3;		//授权成功
	public final static int LOGIN_STATUS_SUCC = 4;		//请求成功
	
	private String sessionId;
	private int loginStatus;
	private String unionid;
	private UserInfo userInfo;
	private String message;
	private Long createTime;

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(int loginStatus) {
		this.loginStatus = loginStatus;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	
	
}
