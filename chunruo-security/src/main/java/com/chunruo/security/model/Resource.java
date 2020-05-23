package com.chunruo.security.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 访问权限资源
 * @author chunruo
 */
@Entity
@Table(name="jkd_admin_resource",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"name"})
})
public class Resource implements Serializable{
	private static final long serialVersionUID = 2625651915484873566L;
	private Long resourceId;
	private String name;
	private String linkPath;
	private Long menuId;
	private String menuPath;
	private Boolean isEnable;
	private Date createTime;
	private Date updateTime;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	
	@Column(name = "name", nullable = true, length = 200)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "menu_id")
	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	@Column(name = "menuPath", length = 200)
	public String getMenuPath() {
		return menuPath;
	}

	public void setMenuPath(String menuPath) {
		this.menuPath = menuPath;
	}

	@Column(name = "link_path", nullable = true, length = 200)
	public String getLinkPath() {
		return linkPath;
	}
	
	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}
	
	@Column(name = "is_enable", columnDefinition = "INT DEFAULT 0")
	public Boolean getIsEnable() {
		return this.isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}
	
	@Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
