package com.chunruo.core.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.chunruo.core.model.Product;
import com.chunruo.core.model.UserAddress;

public class MsgModel<T> implements Serializable {
	private static final long serialVersionUID = -9060754970778112090L;
	private Boolean isSucc;
	private String message;
	private String errorCode;
	private Long objectId;
	private T data;
	private Map<String, Object> map;
    private Integer requestType;   //0:app 1：微页面 2：小程序
	private Integer productType;
	private List<Long> productIdList;
	private List<Product> productList = new ArrayList<Product> ();
	private Boolean isInviteRebate;
	private Boolean isMoreSpecProduct;
	private Boolean isGroupProduct;
	private Boolean isDistributor;
	private Boolean isExpire;
	private Boolean isWebRequest;
	private Boolean isHaveProxy;
	private Double accountAmount;
	private Double preferentialAmount;
	private Integer level;
	private Integer pushLevel;
	private String evaluateRate;
	private Integer passSize;
	private String paymentBody;
	private Integer productProfit;
	private Date lastTime;
	private String transactionId;
	private UserAddress userAddress;
	
	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Boolean getIsSucc() {
		return isSucc;
	}
	
	public void setIsSucc(Boolean isSucc) {
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

	public T getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data = data;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public List<Long> getProductIdList() {
		return productIdList;
	}

	public void setProductIdList(List<Long> productIdList) {
		this.productIdList = productIdList;
	}

	public Boolean getIsInviteRebate() {
		return isInviteRebate;
	}

	public void setIsInviteRebate(Boolean isInviteRebate) {
		this.isInviteRebate = isInviteRebate;
	}

	public Boolean getIsMoreSpecProduct() {
		return isMoreSpecProduct;
	}

	public void setIsMoreSpecProduct(Boolean isMoreSpecProduct) {
		this.isMoreSpecProduct = isMoreSpecProduct;
	}
	
	public Double getAccountAmount() {
		return accountAmount;
	}

	public void setAccountAmount(Double accountAmount) {
		this.accountAmount = accountAmount;
	}

	public Double getPreferentialAmount() {
		return preferentialAmount;
	}

	public void setPreferentialAmount(Double preferentialAmount) {
		this.preferentialAmount = preferentialAmount;
	}

	public Boolean getIsGroupProduct() {
		return isGroupProduct;
	}

	public void setIsGroupProduct(Boolean isGroupProduct) {
		this.isGroupProduct = isGroupProduct;
	}

	public Boolean getIsDistributor() {
		return isDistributor;
	}

	public void setIsDistributor(Boolean isDistributor) {
		this.isDistributor = isDistributor;
	}
	
	public Boolean getIsExpire() {
		return isExpire;
	}

	public void setIsExpire(Boolean isExpire) {
		this.isExpire = isExpire;
	}

	public Boolean getIsWebRequest() {
		return isWebRequest;
	}

	public void setIsWebRequest(Boolean isWebRequest) {
		this.isWebRequest = isWebRequest;
	}

	public Boolean getIsHaveProxy() {
		return isHaveProxy;
	}

	public void setIsHaveProxy(Boolean isHaveProxy) {
		this.isHaveProxy = isHaveProxy;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	
	public Integer getPushLevel() {
		return pushLevel;
	}

	public void setPushLevel(Integer pushLevel) {
		this.pushLevel = pushLevel;
	}

	public String getEvaluateRate() {
		return evaluateRate;
	}

	public void setEvaluateRate(String evaluateRate) {
		this.evaluateRate = evaluateRate;
	}

	public Integer getPassSize() {
		return passSize;
	}

	public void setPassSize(Integer passSize) {
		this.passSize = passSize;
	}

	public Integer getProductProfit() {
		return productProfit;
	}

	public void setProductProfit(Integer productProfit) {
		this.productProfit = productProfit;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	public Integer getRequestType() {
		return requestType;
	}

	public void setRequestType(Integer requestType) {
		this.requestType = requestType;
	}

	public String getPaymentBody() {
		return paymentBody;
	}

	public void setPaymentBody(String paymentBody) {
		this.paymentBody = paymentBody;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public UserAddress getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(UserAddress userAddress) {
		this.userAddress = userAddress;
	}
}
