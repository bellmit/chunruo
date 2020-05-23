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
 * 身份证实名认证
 * @author chunruo
 */
@Entity
@Table(name="jkd_identification",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"id_card_no"})
})
public class Identification {
	private Long idCardId;
	private String realName;
	private String idCardNo;
	private String identityFront;   //身份证正面照
	private String identityBack;    //身份证反面照
	private Boolean isVerifyImage;	//是否图片已认证
	private Boolean status;
	private Date createTime;
	private Date updateTime;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getIdCardId() {
		return idCardId;
	}
	
	public void setIdCardId(Long idCardId) {
		this.idCardId = idCardId;
	}
	
	@Column(name = "real_name", length=50)
	public String getRealName() {
		return realName;
	}
	
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	@Column(name = "id_card_no", length=20)
	public String getIdCardNo() {
		return idCardNo;
	}
	
	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	@Column(name = "is_verify_image", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsVerifyImage() {
		return isVerifyImage;
	}

	public void setIsVerifyImage(Boolean isVerifyImage) {
		this.isVerifyImage = isVerifyImage;
	}
	
	@Column(name = "status")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Column(name = "identity_front")
	public String getIdentityFront() {
		return identityFront;
	}

	public void setIdentityFront(String identityFront) {
		this.identityFront = identityFront;
	}

	@Column(name = "identity_back")
	public String getIdentityBack() {
		return identityBack;
	}

	public void setIdentityBack(String identityBack) {
		this.identityBack = identityBack;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "update_Time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	
}
