package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * 用户池
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_user_pool",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id"})
})
public class UserPool {
	
	public final static Integer USER_POOL_TYPE1=1; //用户池
	public final static Integer USER_POOL_TYPE2=2; //对接池
	public final static Integer USER_POOL_TYPE3=3; //已分配
    public final static Integer USER_POOL_TYPE4=4; //回流池
	
	private Long poolId;
	private Long userId;
    private Long topUserId;
    private Integer poolType;
	private Boolean isBindBdUser; // 是否已绑定bd用户
	private Date bindTime;        // 分配时间
	private Long teamId;          // bd账号删除后用户重新流入池中，此字段才会有值
	private Date createTime;
	private Date updateTime;
	
	private String mobile;
	private Integer level;
	private String nickName;
	private String registerTime;
	private Integer consultationNumber; // 咨询次数
	private String bdUserName;          // 对接人姓名
	private String enterPoolTime;       // 入池时间
	private Boolean isSpecialInvite = false;//是否特殊用户邀请
	private Integer startCount;        //启动次数

	@Id
	@GeneratedValue
	public Long getPoolId() {
		return poolId;
	}

	public void setPoolId(Long poolId) {
		this.poolId = poolId;
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
	
	@Column(name = "is_bind_bd_user")
	public Boolean getIsBindBdUser() {
		return isBindBdUser;
	}

	public void setIsBindBdUser(Boolean isBindBdUser) {
		this.isBindBdUser = isBindBdUser;
	}

	@Column(name = "pool_type")
	public Integer getPoolType() {
		return poolType;
	}

	public void setPoolType(Integer poolType) {
		this.poolType = poolType;
	}

	@Column(name = "team_id")
	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	@Column(name = "bind_time")
	public Date getBindTime() {
		return bindTime;
	}

	public void setBindTime(Date bindTime) {
		this.bindTime = bindTime;
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
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Transient
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Transient
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Transient
	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	@Transient
	public Integer getConsultationNumber() {
		return consultationNumber;
	}

	public void setConsultationNumber(Integer consultationNumber) {
		this.consultationNumber = consultationNumber;
	}

	@Transient
	public String getBdUserName() {
		return bdUserName;
	}

	public void setBdUserName(String bdUserName) {
		this.bdUserName = bdUserName;
	}

	@Transient
	public String getEnterPoolTime() {
		return enterPoolTime;
	}

	public void setEnterPoolTime(String enterPoolTime) {
		this.enterPoolTime = enterPoolTime;
	}

	@Transient
	public Boolean getIsSpecialInvite() {
		return isSpecialInvite;
	}

	public void setIsSpecialInvite(Boolean isSpecialInvite) {
		this.isSpecialInvite = isSpecialInvite;
	}

	@Transient
	public Integer getStartCount() {
		return startCount;
	}

	public void setStartCount(Integer startCount) {
		this.startCount = startCount;
	}
}
