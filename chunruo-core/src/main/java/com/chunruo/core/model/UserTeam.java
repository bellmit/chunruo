package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 团队信息
 * @author hehai
 */
@Entity
@Table(name = "jkd_user_team",uniqueConstraints = {
		@UniqueConstraint(columnNames = {"user_id"})
	})
public class UserTeam {

	private Long teamId;
	private Long userId;
	private Long topUserId;
	private String logo;
	private Integer level;
	private String storeName;
	private String expireEndDate;     // 到期结束日期
	private Date userCreateTime;      // 用户创建时间
	private Integer vipCount;         // 下线vip数量
	private Integer declareCount;     // 下线经销商数量
	private Integer agentCount;       // 下线总代数量
	private Integer v2DeclareCount;   // 下线v2数量
	private Integer v3DeclareCount;   // 下线v3数量
	private Date createTime;
	private Date updateTime;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "top_user_id")
	public Long getTopUserId() {
		return topUserId;
	}

	public void setTopUserId(Long topUserId) {
		this.topUserId = topUserId;
	}

	@Column(name = "logo")
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Column(name = "level")
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Column(name = "store_name")
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	@Column(name = "expire_end_date")
	public String getExpireEndDate() {
		return expireEndDate;
	}

	public void setExpireEndDate(String expireEndDate) {
		this.expireEndDate = expireEndDate;
	}
	
	
	@Column(name = "vip_count")
	public Integer getVipCount() {
		return vipCount;
	}

	public void setVipCount(Integer vipCount) {
		this.vipCount = vipCount;
	}

	@Column(name = "declare_count")
	public Integer getDeclareCount() {
		return declareCount;
	}

	public void setDeclareCount(Integer declareCount) {
		this.declareCount = declareCount;
	}
	
	@Column(name = "agent_count")
	public Integer getAgentCount() {
		return agentCount;
	}

	public void setAgentCount(Integer agentCount) {
		this.agentCount = agentCount;
	}

	@Column(name = "v2_declare_count")
	public Integer getV2DeclareCount() {
		return v2DeclareCount;
	}

	public void setV2DeclareCount(Integer v2DeclareCount) {
		this.v2DeclareCount = v2DeclareCount;
	}

	@Column(name = "v3_declare_count")
	public Integer getV3DeclareCount() {
		return v3DeclareCount;
	}

	public void setV3DeclareCount(Integer v3DeclareCount) {
		this.v3DeclareCount = v3DeclareCount;
	}

	@Column(name = "user_create_time")
	public Date getUserCreateTime() {
		return userCreateTime;
	}

	public void setUserCreateTime(Date userCreateTime) {
		this.userCreateTime = userCreateTime;
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
