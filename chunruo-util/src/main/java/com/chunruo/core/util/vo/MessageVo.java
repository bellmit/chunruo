package com.chunruo.core.util.vo;

public class MessageVo {
	public boolean isSucc = true;
	public String message;
	public Long objectId;
	public String status;
	public String strObjectId;
	public Integer wmsOrderStatus;
	
	public boolean getIsSucc() {
		return isSucc;
	}
	
	public void setIsSucc(boolean isSucc) {
		this.isSucc = isSucc;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public String getStrObjectId() {
		return strObjectId;
	}

	public void setStrObjectId(String strObjectId) {
		this.strObjectId = strObjectId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getWmsOrderStatus() {
		return wmsOrderStatus;
	}

	public void setWmsOrderStatus(Integer wmsOrderStatus) {
		this.wmsOrderStatus = wmsOrderStatus;
	}
}
