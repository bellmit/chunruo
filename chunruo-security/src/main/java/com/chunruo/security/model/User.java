package com.chunruo.security.model;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.chunruo.core.util.StringUtil;

/**
 * 超级管理员
 * @author chunruo
 */
@Entity
@Table(name="jkd_admin_user",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"username"})
})
public class User implements UserDetails, Serializable {
    private static final long serialVersionUID = 3832626162173359411L;
    public static Integer ADMIN_USER_LEVEL_CUSTOMER	= 1;
    public static Integer ADMIN_USER_LEVEL_MANAGER	= 2;
    public static Integer ADMIN_USER_LEVEL_FINANCE	= 3;
    public static Integer ADMIN_USER_LEVEL_SUPER = 4;
    
    private Long userId;
    private Long groupId;						//群组Id
    private String username;                    //账号名
    private String password;                    //密码
    private String realname;					//真实姓名
    private Boolean sex;						//性别
    private Boolean isAdmin;					//是否为管理员
    private String email;                       //邮箱
    private String mobile;						//手机号码
    private String birthday;					//生日
    private String addresss;					//通讯地址
    private int loginErrorTimes;				//登陆错误次数
    private Date createTime;					//创建时间
    private Date updateTime;					//更新时间
    private Integer version;
    private Integer level;                     //管理员身份（1客服、2客服主管、3财务、4超级管理员）
    
    private boolean enabled;
	private boolean accountExpired;
	private boolean accountLocked;
	private boolean credentialsExpired;
    
    //Transient
    private String confirmPassword;				//确认密码
    private String oldPassword;
    private String groupName;
    private List<String> linkPathList = new ArrayList<String> ();
    private Set<Long> menuIdSet = new HashSet<Long> ();
	
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getUserId() {
		return userId;
	}
    
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name="group_id", nullable=false)
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Column(name="username",length=50,nullable=false)
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Column(name="password",length=50,nullable=false)
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name="real_name",length=250,nullable=false)
	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}
	
	@Column(name = "sex", columnDefinition = "BIT DEFAULT 0")
	public Boolean getSex() {
		return sex;
	}
	
	public void setSex(Boolean sex) {
		this.sex = sex;
	}
	
	@Column(name = "login_error_times", columnDefinition = "BIT DEFAULT 0")
	public int getLoginErrorTimes() {
		return loginErrorTimes;
	}

	public void setLoginErrorTimes(int loginErrorTimes) {
		this.loginErrorTimes = loginErrorTimes;
	}

	@Column(name = "is_admin", columnDefinition = "BIT DEFAULT 0")
	public Boolean getIsAdmin() {
		return isAdmin;
	}
	
	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	@Column(name = "email", length=50)
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "mobile", length=50)
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Column(name = "birthday", length=20)
	public String getBirthday() {
		return birthday;
	}
	
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	
	@Column(name = "addresss", length=250)
	public String getAddresss() {
		return addresss;
	}
	
	public void setAddresss(String addresss) {
		this.addresss = addresss;
	}
	
	@Column(name = "level")
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Column(name = "createTime")
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "updateTime")
	public Date getUpdateTime() {
		return updateTime;
	}
	
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	@Column(name="account_enabled")
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Column(name="account_expired",nullable=false)
	public boolean isAccountExpired() {
		return accountExpired;
	}
	
	public void setAccountExpired(boolean accountExpired) {
		this.accountExpired = accountExpired;
	}
	
	@Column(name="account_locked",nullable=false)
	public boolean isAccountLocked() {
		return accountLocked;
	}
	
	public void setAccountLocked(boolean accountLocked) {
		this.accountLocked = accountLocked;
	}
	
	@Column(name="credentials_expired",nullable=false)
	public boolean isCredentialsExpired() {
		return credentialsExpired;
	}
	
	public void setCredentialsExpired(boolean credentialsExpired) {
		this.credentialsExpired = credentialsExpired;
	}
	
    @Transient
    public boolean isAccountNonExpired() {
        return !isAccountExpired();
    }
    
    @Transient
    public boolean isAccountNonLocked() {
        return !isAccountLocked();
    }
    
    @Transient
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }
	
	@Version
    public Integer getVersion() {
        return version;
    }
	
	public void setVersion(Integer version) {
		this.version = version;
	}

	@Transient
	public String getConfirmPassword() {
		return confirmPassword;
	}
	
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	@Transient
	public String getOldPassword() {
		return oldPassword;
	}
	
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	@Transient
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@SuppressWarnings("serial")
	@Transient
	public Set<GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> ga = new HashSet<GrantedAuthority>();
		try{
			if(this.linkPathList != null && this.linkPathList.size() > 0){
				for(final String linkPath : this.linkPathList){
					ga.add(new GrantedAuthority(){
						@Override
						public String getAuthority() {
							return StringUtil.null2Str(linkPath);
						}
					});
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ga;
	}

	@Transient
	public List<String> getLinkPathList() {
		return linkPathList;
	}

	public void setLinkPathList(List<String> linkPathList) {
		this.linkPathList = linkPathList;
	}
	
	@Transient
	public Set<Long> getMenuIdSet() {
		return menuIdSet;
	}

	public void setMenuIdSet(Set<Long> menuIdSet) {
		this.menuIdSet = menuIdSet;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		User pojo = (User) o;
		if (this.userId != null ? !this.userId.equals(pojo.userId) : pojo.userId != null)
			return false;
		if (this.username != null ? !this.username.equals(pojo.username) : pojo.username != null)
			return false;
		if (this.password != null ? !this.password.equals(pojo.password) : pojo.password != null)
			return false;
		if (this.realname != null ? !this.realname.equals(pojo.realname) : pojo.realname != null)
			return false;
		if (this.sex != null ? !this.sex.equals(pojo.sex) : pojo.sex != null)
			return false;
		if (this.isAdmin != null ? !this.isAdmin.equals(pojo.isAdmin) : pojo.isAdmin != null)
			return false;
		if (this.email != null ? !this.email.equals(pojo.email) : pojo.email != null)
			return false;
		if (this.mobile != null ? !this.mobile.equals(pojo.mobile) : pojo.mobile != null)
			return false;
		if (this.birthday != null ? !this.birthday.equals(pojo.birthday) : pojo.birthday != null)
			return false;
		if (this.addresss != null ? !this.addresss.equals(pojo.addresss) : pojo.addresss != null)
			return false;
		if (this.level != null ? !this.level.equals(pojo.level) : pojo.level != null)
			return false;
		if (this.createTime != null ? !this.createTime.equals(pojo.createTime) : pojo.createTime != null)
			return false;
		if (this.updateTime != null ? !this.updateTime.equals(pojo.updateTime) : pojo.updateTime != null)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = 0;
		result = (userId != null ? userId.hashCode() : 0);
		result = 31 * result + (this.username != null ? this.username.hashCode() : 0);
		result = 31 * result + (this.password != null ? this.password.hashCode() : 0);
		result = 31 * result + (this.realname != null ? this.realname.hashCode() : 0);
		result = 31 * result + (this.sex != null ? this.sex.hashCode() : 0);
		result = 31 * result + (this.isAdmin != null ? this.isAdmin.hashCode() : 0);
		result = 31 * result + (this.email != null ? this.email.hashCode() : 0);
		result = 31 * result + (this.mobile != null ? this.mobile.hashCode() : 0);
		result = 31 * result + (this.birthday != null ? this.birthday.hashCode() : 0);
		result = 31 * result + (this.addresss != null ? this.addresss.hashCode() : 0);
		result = 31 * result + (this.level != null ? this.level.hashCode() : 0);
		result = 31 * result + (this.createTime != null ? this.createTime.hashCode() : 0);
		result = 31 * result + (this.updateTime != null ? this.updateTime.hashCode() : 0);
		return result;
	}
    
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(getClass().getSimpleName());
		sb.append(" [");
		sb.append("userId").append("='").append(getUserId()).append("', ");
		sb.append("username").append("='").append(getUsername()).append("', ");
		sb.append("password").append("='").append(getPassword()).append("', ");
		sb.append("realname").append("='").append(getRealname()).append("', ");
		sb.append("sex").append("='").append(getSex()).append("', ");
		sb.append("isAdmin").append("='").append(getIsAdmin()).append("', ");
		sb.append("email").append("='").append(getEmail()).append("', ");
		sb.append("mobile").append("='").append(getMobile()).append("', ");
		sb.append("birthday").append("='").append(getBirthday()).append("', ");
		sb.append("addresss").append("='").append(getAddresss()).append("', ");
		sb.append("level").append("='").append(getLevel()).append("', ");
		sb.append("createTime").append("='").append(getCreateTime()).append("', ");
		sb.append("updateTime").append("='").append(getUpdateTime()).append("'");
		sb.append("]");
		return sb.toString();
	}
}
