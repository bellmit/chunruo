package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 签到图文
 * @author hehai
 */
@Entity
@Table(name = "jkd_sign_image_text")
public class SignImageText {
	private Long textId;      //图文id
	private Date oneDate;   //作用时间
	private String imagePath; //图片
	private String content;   //描述
	private Date createTime;  //创建时间
	private Date updateTime;  //更新时间
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getTextId() {
		return textId;
	}
	public void setTextId(Long textId) {
		this.textId = textId;
	}
	
	@Column(name = "one_date")
	public Date getOneDate() {
		return oneDate;
	}
	public void setOneDate(Date oneDate) {
		this.oneDate = oneDate;
	}
	
	@Column(name = "image_path")
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	@Column(name = "content")
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	
}
