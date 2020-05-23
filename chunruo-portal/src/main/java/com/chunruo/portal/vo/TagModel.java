package com.chunruo.portal.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagModel<T> implements Serializable {
	private static final long serialVersionUID = -1643524135083155156L;
	private String code;
	private String msg;
	private String nextPageURL;
	private Integer page;
	private Integer pagesize;
	private Long total;
	private Integer totalPage;
	private Long objectId;
	private T data;
	private String minImagePath;
	private String shareURL;
	private String seckillCode;
	private List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>> ();
	private Map<String, Object> dataMap = new HashMap<String, Object> ();
	private Map<String, T> listMap = new HashMap<String, T> ();
	private List<Object> dataList = new ArrayList<Object> ();
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String getNextPageURL() {
		return nextPageURL;
	}
	
	public void setNextPageURL(String nextPageURL) {
		this.nextPageURL = nextPageURL;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPagesize() {
		return pagesize;
	}

	public void setPagesize(Integer pagesize) {
		this.pagesize = pagesize;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public List<Map<String, Object>> getMapList() {
		return mapList;
	}

	public void setMapList(List<Map<String, Object>> mapList) {
		this.mapList = mapList;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Map<String, T> getListMap() {
		return listMap;
	}

	public void setListMap(Map<String, T> listMap) {
		this.listMap = listMap;
	}

	public List<Object> getDataList() {
		return dataList;
	}

	public void setDataList(List<Object> dataList) {
		this.dataList = dataList;
	}

	public String getMinImagePath() {
		return minImagePath;
	}

	public void setMinImagePath(String minImagePath) {
		this.minImagePath = minImagePath;
	}

	public String getShareURL() {
		return shareURL;
	}

	public void setShareURL(String shareURL) {
		this.shareURL = shareURL;
	}

	public String getSeckillCode() {
		return seckillCode;
	}

	public void setSeckillCode(String seckillCode) {
		this.seckillCode = seckillCode;
	}
}
