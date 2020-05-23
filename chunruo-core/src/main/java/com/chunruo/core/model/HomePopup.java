package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 首页弹窗
 * 
 * @author Administrator
 */
@Entity
@Table(name = "jkd_home_popup")
public class HomePopup {

	public static final Integer HOME_POPUP_TYPE0 = 0;    //首页弹窗
	public static final Integer HOME_POPUP_TYPE1 = 1;    //右侧悬浮
	
	public static final Integer HOME_POPUP_NONE = 0;      //无推手
	public static final Integer HOME_POPUP_COMMON = 1;    //实习推手
	public static final Integer HOME_POPUP_HIGHER = 2;    //高级推手
	public static final Integer HOME_POPUP_VIP0 = 3;      //vip0
	public static final Integer HOME_POPUP_VIP1 = 4;      //vip1
	
	public static final Integer HOME_POPUP_JUMP_NULL = 0;
	public static final Integer HOME_POPUP_JUMP_INVITE = 1;  
	public static final Integer HOME_POPUP_JUMP_PRODUCT = 2;  
	public static final Integer HOME_POPUP_JUMP_WEB = 8;     
	
	
	private Long popupId;
	private Integer pushLevel;             // 显示弹框的推手等级 (1:实习推手 2：高级推手 3：vip0 4：vip1)
	private Integer type;                  // 0：首页弹窗，1：右侧悬浮
	private Integer jumpPageType;          // 1:跳转邀请页 
	private String content;                // h5页面地址、具体id
	private String level;                  // 显示等级
	private Long productId;                // 商品id
	private Boolean isEnable;              // 是否启用
	private String imageUrl;
	private Date beginTime;                // 开始时间
	private Date endTime;                  // 结束时间
	private Date createTime;
	private Date updateTime;
	
	private Boolean isInvitePage;          // 是否跳转邀请页
	private String pageName;               // 页面名称
	private Integer discoveryType;         // 发现类型
	private Integer realTargetType;        // 真实跳转类型

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getPopupId() {
		return popupId;
	}

	public void setPopupId(Long popupId) {
		this.popupId = popupId;
	}

	@Column(name = "jump_page_type")
	public Integer getJumpPageType() {
		return jumpPageType;
	}

	public void setJumpPageType(Integer jumpPageType) {
		this.jumpPageType = jumpPageType;
	}

	@Column(name = "content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "push_level")
	public Integer getPushLevel() {
		return pushLevel;
	}

	public void setPushLevel(Integer pushLevel) {
		this.pushLevel = pushLevel;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "is_enable")
	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	@Column(name = "image_url")
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Column(name = "level")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
	@Column(name = "begin_time")
	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	@JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
	@Column(name = "end_time")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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
	public Boolean getIsInvitePage() {
		return isInvitePage;
	}

	public void setIsInvitePage(Boolean isInvitePage) {
		this.isInvitePage = isInvitePage;
	}

	@Transient
	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	@Transient
	public Integer getDiscoveryType() {
		return discoveryType;
	}

	public void setDiscoveryType(Integer discoveryType) {
		this.discoveryType = discoveryType;
	}

	@Transient
	public Integer getRealTargetType() {
		return realTargetType;
	}

	public void setRealTargetType(Integer realTargetType) {
		this.realTargetType = realTargetType;
	}

}
