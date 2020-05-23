package com.chunruo.security.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 枚举菜单
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_admin_menu")
public class Menu {
	private Long menuId;		//序号
	private Menu parentMenu;	//父类ID
	private String name;		//菜单名称
	private String ctrl;		//菜单控制器名称
	private Integer sequence;	//排序索引
	private Boolean status;		//状态
	private String icon;		//图标
	private String desc;		//描述
	
	//Transient
	private Long parentId;
	private Integer enableType = 0;
	private Boolean isResource;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getMenuId() {
		return menuId;
	}
	
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="parent_id", nullable=true)
	public Menu getParentMenu() {
		return parentMenu;
	}

	public void setParentMenu(Menu parentMenu) {
		this.parentMenu = parentMenu;
	}
	
	@Column(name = "name", length = 50, nullable = false)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "ctrl", length = 50)
	public String getCtrl() {
		return ctrl;
	}
	
	public void setCtrl(String ctrl) {
		this.ctrl = ctrl;
	}
	
	@Column(name = "sequence", nullable = false, columnDefinition = "INT DEFAULT 0")
	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	
	@Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 0")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Column(name = "icon", length = 50)
	public String getIcon() {
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@Column(name = "menu_desc", length = 250)
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Transient
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	@Transient
	public Boolean getIsResource() {
		return isResource;
	}

	public void setIsResource(Boolean isResource) {
		this.isResource = isResource;
	}
	
	@Transient
	public Integer getEnableType() {
		return enableType;
	}

	public void setEnableType(Integer enableType) {
		this.enableType = enableType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if ((o == null) || (getClass() != o.getClass())) return false;

		Menu pojo = (Menu)o;
		if (this.menuId != null ? !this.menuId.equals(pojo.menuId) : pojo.menuId != null)
			return false;
		if (this.name != null ? !this.name.equals(pojo.name) : pojo.name != null)
			return false;
		if (this.ctrl != null ? !this.ctrl.equals(pojo.ctrl) : pojo.ctrl != null)
			return false;
		if (this.sequence != null ? !this.sequence.equals(pojo.sequence) : pojo.sequence != null)
			return false;
		if (this.icon != null ? !this.icon.equals(pojo.icon) : pojo.icon != null)
			return false;
		if (this.desc != null ? !this.desc.equals(pojo.desc) : pojo.desc != null)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = 0;
		result = this.menuId != null ? this.menuId.hashCode() : 0;
		result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
		result = 31 * result + (this.ctrl != null ? this.ctrl.hashCode() : 0);
		result = 31 * result + (this.sequence != null ? this.sequence.hashCode() : 0);
		result = 31 * result + (this.icon != null ? this.icon.hashCode() : 0);
		result = 31 * result + (this.desc != null ? this.desc.hashCode() : 0);
		return result;
	
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(getClass().getSimpleName());
		sb.append(" [");
		sb.append("menuId").append("='").append(getMenuId()).append("', ");
		sb.append("name").append("='").append(getName()).append("', ");
		sb.append("ctrl").append("='").append(getCtrl()).append("', ");
		sb.append("sequence").append("='").append(getSequence()).append("', ");
		sb.append("icon").append("='").append(getIcon()).append("', ");
		sb.append("desc").append("='").append(getDesc()).append("'");
		sb.append("]");
		return sb.toString();
	}
	
}
