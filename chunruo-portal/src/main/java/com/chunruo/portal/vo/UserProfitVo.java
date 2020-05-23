package com.chunruo.portal.vo;

import java.io.Serializable;

public class UserProfitVo implements Serializable {
	private static final long serialVersionUID = 868143473151481022L;
	private Long userId;
	private String storeName;
	private Double monthIncome;		//当月贡献
    private Double historyIncome;	//历史贡献
    private Integer level;			//用户等级
    private Integer downCount;     //下线经销商人数
    private String userNumber;     //用户number
    private String expireTime;		//用户等级到期时间
    private String logo;			//用户头像
    private Long userCreateTime;	//用户创建时间
    
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public Double getMonthIncome() {
		return monthIncome;
	}
	
	public void setMonthIncome(Double monthIncome) {
		this.monthIncome = monthIncome;
	}
	
	public Double getHistoryIncome() {
		return historyIncome;
	}
	
	public void setHistoryIncome(Double historyIncome) {
		this.historyIncome = historyIncome;
	}
	
	public Integer getLevel() {
		return level;
	}
	
	public void setLevel(Integer level) {
		this.level = level;
	}
	
	public String getExpireTime() {
		return expireTime;
	}
	
	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}
	
	public String getLogo() {
		return logo;
	}
	
	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	
	public Integer getDownCount() {
		return downCount;
	}

	public void setDownCount(Integer downCount) {
		this.downCount = downCount;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public Long getUserCreateTime() {
		return userCreateTime;
	}
	
	public void setUserCreateTime(Long userCreateTime) {
		this.userCreateTime = userCreateTime;
	}
}
