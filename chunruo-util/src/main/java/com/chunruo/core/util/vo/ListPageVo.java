package com.chunruo.core.util.vo;

public class ListPageVo<T> {
	private T dataList;
	private Long lastId;
	private Integer count;
	private Boolean isNextPageURL;
	private Long pageMax;
	
	private Integer pageidx;

	public T getDataList() {
		return dataList;
	}
	
	public void setDataList(T dataList) {
		this.dataList = dataList;
	}
	
	public Long getLastId() {
		return lastId;
	}
	
	public void setLastId(Long lastId) {
		this.lastId = lastId;
	}
	
	public Boolean getIsNextPageURL() {
		return isNextPageURL;
	}
	
	public void setIsNextPageURL(Boolean isNextPageURL) {
		this.isNextPageURL = isNextPageURL;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Long getPageMax() {
		return pageMax;
	}

	public void setPageMax(Long pageMax) {
		this.pageMax = pageMax;
	}

	public Integer getPageidx() {
		return pageidx;
	}

	public void setPageidx(Integer pageidx) {
		this.pageidx = pageidx;
	}
}
