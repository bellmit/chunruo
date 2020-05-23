package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 系统推送消息记录表
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_system_send_msg")
public class SystemSendMsg implements java.io.Serializable {
	private static final long serialVersionUID = -9210162105436093805L;
	public static final Integer MESSAGE_TYPE_NOTICE = 1;				//平台公告
	public static final Integer MESSAGE_TYPE_ACTIVITY = 2;				//活动消息
	public static final Integer MESSAGE_TYPE_GOODS = 3;                 //爆款商品
	public static final Integer MESSAGE_TYPE_SINGLE = 4;                //单个商品
	public static final Integer OBJECT_TYPE_ALLSELLER = 0;				//0-所有纯若卖家
	public static final Integer OBJECT_TYPE_SPECIALDEALER = 1;			//1-所有特殊分销商
	
	public static final Integer OBJECT_TYPE_VIP0 = 1;     //VIP0
	public static final Integer OBJECT_TYPE_VIP1 = 2;     //v1、v2、v3
	public static final Integer OBJECT_TYPE_PUSH1 = 3;    //实习推手
	public static final Integer OBJECT_TYPE_PUSH2 = 4;    //纯若推送、推广经理
	public static final Integer OBJECT_TYPE_CUSTOM = 5;   //自定义推送（导入账号）
	public static final Integer OBJECT_TYPE_ALL = 6;      //全部
	public static final Integer OBJECT_TYPE_V1 = 7;       //v1
	public static final Integer OBJECT_TYPE_V2 = 8;       //v2
	public static final Integer OBJECT_TYPE_V3 = 9;       //v3
	
	public static final Integer JUMP_PAGE_TYPE_NULL = 0;     //无
	public static final Integer JUMP_PAGE_TYPE__INVITE = 1;  //邀请页
	public static final Integer HOME_POPUP_JUMP_WEB = 3;     //h5页面
	
	
	private Long id;				//记录id
	private String content;			//推送内容
	private Integer messageType;	//类型 1- 公告 2-活动 3-商品 4-单个商品
	private Long objectId;			//活动的对象id（内页id）
	private Date createTime;		//创建时间
	private Date updateTime;		//更新时间
	private String title;			//标题
	private String imageUrl;		//图片
	private String objectType;		//接收对象		0-所有纯若卖家。1-所有特殊分销商
	private Date beginTime;         //活动开始时间
	private Date endTime;           //活动结束时间
	private Boolean isInvitePage;   //是否跳转邀请页
	private Integer jumpPageType;   //1:跳转邀请页 3：跳转h5页面
	private String webUrl;          //h5页面地址
	private Boolean isDelaySend;    //是否延迟发送
	private String productIds;      //爆款商品ids 
	private String typeName;        //类型名称
	
	//@Transient
	private String channelName;		//频道名称
	private String pageName;		//页面名
	private String productName;     //商品名称
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name="message_type")
	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}
	
	@Column(name="object_id")
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	@Column(name="title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column(name="image_url")
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Column(name="object_ype")
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	@Column(name="product_ids")
	public String getProductIds() {
		return productIds;
	}
	
	public void setProductIds(String productIds) {
		this.productIds = productIds;
	}

	@Column(name="type_name")
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Column(name="is_invite_page")
	public Boolean getIsInvitePage() {
		return isInvitePage;
	}

	public void setIsInvitePage(Boolean isInvitePage) {
		this.isInvitePage = isInvitePage;
	}

	@Column(name="is_delay_send")
	public Boolean getIsDelaySend() {
		return isDelaySend;
	}

	public void setIsDelaySend(Boolean isDelaySend) {
		this.isDelaySend = isDelaySend;
	}

	@Column(name="begin_time")
	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	@Column(name="end_time")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	

	@Column(name = "jump_page_type")
	public Integer getJumpPageType() {
		return jumpPageType;
	}

	public void setJumpPageType(Integer jumpPageType) {
		this.jumpPageType = jumpPageType;
	}

	@Column(name = "web_url")
	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	@Column(name="update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Transient
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	@Transient
	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	@Transient
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

}
