package com.chunruo.cache.portal;

import java.util.Date;

public class CacheObject {
	private int size;
	private Date lastUpdateTime;
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}	
}
