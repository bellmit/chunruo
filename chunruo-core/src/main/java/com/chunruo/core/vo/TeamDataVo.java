package com.chunruo.core.vo;

public class TeamDataVo {
	private Integer directAgent; 	// 直属总代
	private Integer directDeclare; 	// 直属经销商
	private Integer directMonth; 	// 直属当月新增
	private Integer subAgent; 		// 次级总代
	private Integer subDeclare; 	// 次级经销商
	private Integer subMonth; 		// 附属当月新增

	public Integer getDirectAgent() {
		return directAgent;
	}

	public void setDirectAgent(Integer directAgent) {
		this.directAgent = directAgent;
	}

	public Integer getDirectDeclare() {
		return directDeclare;
	}

	public void setDirectDeclare(Integer directDeclare) {
		this.directDeclare = directDeclare;
	}

	public Integer getDirectMonth() {
		return directMonth;
	}

	public void setDirectMonth(Integer directMonth) {
		this.directMonth = directMonth;
	}

	public Integer getSubAgent() {
		return subAgent;
	}

	public void setSubAgent(Integer subAgent) {
		this.subAgent = subAgent;
	}

	public Integer getSubDeclare() {
		return subDeclare;
	}

	public void setSubDeclare(Integer subDeclare) {
		this.subDeclare = subDeclare;
	}

	public Integer getSubMonth() {
		return subMonth;
	}

	public void setSubMonth(Integer subMonth) {
		this.subMonth = subMonth;
	}

	@Override
	public String toString() {
		return "TeamDataVo [directAgent=" + directAgent + ", directDeclare=" + directDeclare + ", directMonth="
				+ directMonth + ", subAgent=" + subAgent + ", subDeclare=" + subDeclare + ", subMonth=" + subMonth
				+ "]";
	}
	
	

}
