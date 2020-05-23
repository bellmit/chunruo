package com.chunruo.security.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;

/**
 * 用户群组
 * @author chunruo
 */
@Entity
@Table(name="jkd_admin_group",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"name"})
})
public class Group implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long groupId;
	private String name;
	private Boolean isEnable;
	private Date createTime;
	private Date updateTime;
	private Set<Role> roles = new HashSet<Role>();
	
	//Transient
	private String roleIds;
	private String rolePath;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getGroupId() {
		return this.groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Column(name = "name", nullable = false, length = 100)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "is_enable", columnDefinition = "INT DEFAULT 0")
	public Boolean getIsEnable() {
		return this.isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	@ManyToMany(fetch = FetchType.EAGER) 
    @JoinTable(name="jkd_admin_group_role", joinColumns = { @JoinColumn( name="group_id") }, inverseJoinColumns = @JoinColumn( name="role_id"))    
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
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
	
    @Transient
	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	@Transient
	public String getRolePath() {
		return rolePath;
	}

	public void setRolePath(String rolePath) {
		this.rolePath = rolePath;
	}
}
