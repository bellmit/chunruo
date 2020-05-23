package com.chunruo.security.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.JoinColumn;

import org.springframework.security.core.GrantedAuthority;

/**
 * 用户角色
 * @author chunruo
 */
@Entity
@Table(name="jkd_admin_role")
@NamedQueries({@javax.persistence.NamedQuery(name="findRoleByName", query="select r from Role r where r.name = :name ")})
public class Role implements Serializable, GrantedAuthority {
	private static final long serialVersionUID = 3690197650654049848L;
	private Long id;
	private String name;
	private Date createTime;
	private Date updateTime;
	private Set<Resource> resources = new HashSet<Resource>();
	private Set<Group> groups = new HashSet<Group>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() { return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length=20)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public String getAuthority(){
		return getName();
	}

	@ManyToMany(targetEntity = Resource.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "jkd_admin_role_resource", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = @JoinColumn(name = "res_id"))
	public Set<Resource> getResources() {
		return this.resources;
	}

	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}
  
	@ManyToMany(targetEntity = Group.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "jkd_admin_group_role", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = @JoinColumn(name = "group_id"))
	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
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