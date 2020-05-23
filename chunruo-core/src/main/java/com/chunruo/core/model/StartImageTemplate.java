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

/**
 * 启动图模板
 * @author Administrator
 */
@Entity
@Table(name = "jkd_start_template")
public class StartImageTemplate {

	private Long templateId;
	private Boolean status;  //状态
	private Boolean isDelete;//是否删除
	private Long productId; // 商品id
	private Boolean isInvitePage; // 是否跳转邀请页
	private Date beginTime; // 开始时间
	private Date endTime; // 结束时间

	private Date updateTime; // 更新时间
	private Date createTime; // 创建时间
	
	private String productName; //商品名称
	private List<StartImage> startImageList = new ArrayList<StartImage>();

	@Id
	@GeneratedValue
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	@Column(name = "status")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Column(name = "is_delete")
	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Column(name = "product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "is_invite_page")
	public Boolean getIsInvitePage() {
		return isInvitePage;
	}

	public void setIsInvitePage(Boolean isInvitePage) {
		this.isInvitePage = isInvitePage;
	}

	@Column(name = "begin_time")
	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	@Column(name = "end_time")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Transient
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Transient
	public List<StartImage> getStartImageList() {
		return startImageList;
	}

	public void setStartImageList(List<StartImage> startImageList) {
		this.startImageList = startImageList;
	}
}
