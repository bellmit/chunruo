package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Transient;

/**
 * 第三方账号绑定登录
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_user_society",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"open_id", "union_id"})
})
public class UserSociety {
	private Long userSocietyId;		
	private Long appConfigId;		// 对应微信APP配置Id
	private String unionId;			// 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
	private String oldUnionId;		// 旧的unionId必须保留兼容之前用户正常登陆
	private String openId;			// 用户的唯一标识
	private String nickname;		// 昵称
	private String sex;				// 性别
	private String province;		// 省份
	private String city;			// 城市
	private String country;			// 国家，如中国为CN
	private String headImgUrl;		// 用户头像地址
	private Date createTime;		// 创建时间
	private Date updateTime;		// 更新时间
	
	//Transient
	private Long userId;
	private UserInfo userInfo;
	private String clientIp;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getUserSocietyId() {
		return userSocietyId;
	}

	public void setUserSocietyId(Long userSocietyId) {
		this.userSocietyId = userSocietyId;
	}

	@Column(name = "app_config_id", nullable=false)
	public Long getAppConfigId() {
		return appConfigId;
	}

	public void setAppConfigId(Long appConfigId) {
		this.appConfigId = appConfigId;
	}

	@Column(name = "union_id")
	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	@Column(name = "old_union_id")
	public String getOldUnionId() {
		return oldUnionId;
	}

	public void setOldUnionId(String oldUnionId) {
		this.oldUnionId = oldUnionId;
	}
	
	@Column(name = "open_id", nullable=false)
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	@Column(name = "nick_name")
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Column(name = "sex")
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Column(name = "province")
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	@Column(name = "city")
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "country")
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "head_img_url")
	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	@Column(name="create_time")
    public Date getCreateTime() {
        return this.createTime;
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
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Transient
	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	@Transient
	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
}
