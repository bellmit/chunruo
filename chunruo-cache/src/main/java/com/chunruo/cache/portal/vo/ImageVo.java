package com.chunruo.cache.portal.vo;

import java.io.Serializable;

public class ImageVo implements Serializable {
	private static final long serialVersionUID = 1520199097363025517L;
	private String filePath;
	private int width;
	private int height;
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
}
