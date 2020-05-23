package com.chunruo.core.vo;

import java.io.Serializable;
import java.util.Map;

public class TModel<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private Boolean isSucc;
	public Long count = new Long(0);
	public T tModel;
	private Map<String, Object> filtersMap;
	private String filePath;
	
	public Boolean getIsSucc() {
		return isSucc;
	}

	public void setIsSucc(Boolean isSucc) {
		this.isSucc = isSucc;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
	
	public T gettModel() {
		return tModel;
	}

	public void settModel(T tModel) {
		this.tModel = tModel;
	}

	public Map<String, Object> getFiltersMap() {
		return filtersMap;
	}

	public void setFiltersMap(Map<String, Object> filtersMap) {
		this.filtersMap = filtersMap;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}

