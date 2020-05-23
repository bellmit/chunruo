package com.chunruo.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * 新手引导
 * @author Administrator
 */
@Entity
@Table(name = "jkd_novice_guide",uniqueConstraints = {
		@UniqueConstraint(columnNames = {"phone_type", "width","height"})
	})
public class NoviceGuide {

	private Long guideId;
	private Integer phoneType; // 0：安卓 1：ios
	private String imagePath; // 相对地址
	private Integer width; // 宽
	private Integer height; // 高
	private Boolean status; // 是否启用，1-是，0否
	private Date createTime; // 创建时间
	private Date updateTime; // 更新时间
	
	@Transient
	List<String> imagePathList = new ArrayList<String>();

	@Id
	@GeneratedValue
	public Long getGuideId() {
		return guideId;
	}

	public void setGuideId(Long guideId) {
		this.guideId = guideId;
	}

	@Column(name = "phone_type")
	public Integer getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(Integer phoneType) {
		this.phoneType = phoneType;
	}

	@Column(name = "image_path")
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Column(name = "width")
	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	@Column(name = "height")
	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	@Column(name = "status")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
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

	@Transient
	public List<String> getImagePathList() {
		return imagePathList;
	}

	public void setImagePathList(List<String> imagePathList) {
		this.imagePathList = imagePathList;
	}

}
