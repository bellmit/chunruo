package com.chunruo.core.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpressVO {
	private String message;
	private String othername;
	private String othernum;
	private String nu;
	private String ischeck;
	private String condition;
	private String com;
	private String expressCompany;
	private String orderNo;
	private String status;
	private String state;
	private List<Data> data = new ArrayList<Data>();
	private Date orderTime;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNu() {
		return nu;
	}

	public void setNu(String nu) {
		this.nu = nu;
	}

	public String getIscheck() {
		return ischeck;
	}

	public void setIscheck(String ischeck) {
		this.ischeck = ischeck;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<Data> getData() {
		return data;
	}

	public void setData(List<Data> data) {
		this.data = data;
	}


	public String getExpressCompany() {
		return expressCompany;
	}

	public void setExpressCompany(String expressCompany) {
		this.expressCompany = expressCompany;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public String getOthername() {
		return othername;
	}

	public void setOthername(String othername) {
		this.othername = othername;
	}

	public String getOthernum() {
		return othernum;
	}

	public void setOthernum(String othernum) {
		this.othernum = othernum;
	}

	public static class Data {
		private String time;
		private String ftime;
		private String context;

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getFtime() {
			return ftime;
		}

		public void setFtime(String ftime) {
			this.ftime = ftime;
		}

		public String getContext() {
			return context;
		}

		public void setContext(String context) {
			this.context = context;
		}

	}
}
