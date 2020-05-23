package com.chunruo.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.chunruo.core.vo.OrderMessageVo;

/**
 * 消息推送记录
 * @author chunruo
 *
 */
@Entity
@Table(name = "jkd_push_message")
public class PushMessage {
	public static final Long DEFUALT_SYSTEM_USER_ID = -1L;	//默认系统消息用户ID
	public static final int MSG_TYPE_SYSTEM_NOTICE = 1;		//系统公告消息
	public static final int MSG_TYPE_ORDER = 2;				//订单消息
	public static final int MSG_TYPE_WITHDRAWAL = 3;		//提现消息
	public static final int MSG_TYPE_INTERACTION = 4;		//互动消息(商品问答、问题反馈)
	public static final int MSG_TYPE_SYSTEM_ACTIVITY = 5;	//系统活动消息
	public static final int MSG_TYPE_PROFIT = 6;			//收益通知
	public static final int MSG_TYPE_RETURN = 7;			//退款
	public static final int MSG_TYPE_INVITER = 8;			//团队邀请
	public static final int MSG_TYPE_COUPON = 9;			//我的资产（优惠券）
	public static final int MSG_TYPE_GOODS = 10;			//推送商品
	public static final int MSG_TYPE_EXCULLENT_EVALUATE = 11; //精选评论  
	public static final int MSG_TYPE_RECHARGE = 12;           //用户充值
	public static final int MSG_TYPE_SINGLE_PRODUCT = 14;     //单个商品
	public static final int MSG_TYPE_ORDER_CLOSE = 15;       //订单关闭
	public static final int MSG_TYPE_PRODUCT_SOLDOUT_SECKILL = 16; //到货通知、秒杀提醒

	
	public static final int SKIP_PAGE_QUESTION = 1;  //商品问答页面
	public static final int SKIP_PAGE_COUPON = 2 ;   //优惠券页面
	public static final int SKIP_PAGE_REFUND = 3;    //售后详情页面
	public static final int SKIP_PAGE_ORDER = 4;     //订单详情页面
	public static final int SKIP_PAGE_PRODUCT = 5;   //商品详情页面
	public static final int SKIP_PAGE_FEEDBACK = 6;  //意见反馈页面
	public static final int SKIP_PAGE_WEB = 7;       //h5页面
	
	private Long msgId;						//消息id
	private Long userId;					//用户id
	private Long objectId;					//对象id
	private Long relationId;				//关联对象id
	private Integer msgType;				//消息类型 1-系统公告 2-订单消息 3-提现通知 4-互动消息 5-系统活动消息  6-收益通知
	private Integer childMsgType;           //子消息类型
	private String msgContent;              //消息内容
	private Boolean isPushMsg;				//是否推送
	private Boolean isSystemMsg;			//是否系统消息
	private Date createTime;
	private Date updateTime;
	private String title;					//订单标题
	private Integer objectType;				//接收对象		0-所有纯若卖家。1-所有特殊分销商
	private Integer logisticsStatus;        //物流状态 
	private String imageUrl;				//图片
	private String orderContent;            //订单信息
	private String objectTypes;             //接受对象集合
	private String webUrl;                  //webUrl
	
	@Transient
	private Boolean isTimeOut;              //是否活动结束
	private String typeName;                //活动类型名称
	private List<Product> productList = new ArrayList<Product>();   //商品列表
	private OrderMessageVo orderMessageVo = new OrderMessageVo();   //订单信息
	private Boolean isInvitePage;          //是否跳转邀请页
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getMsgId() {
		return msgId;
	}
	
	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}
	
	@Column(name = "object_id")
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	
	@Column(name = "relation_id")
	public Long getRelationId() {
		return relationId;
	}

	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "msg_type")
	public Integer getMsgType() {
		return msgType;
	}
	
	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}
	
	@Column(name = "child_msg_type")
	public Integer getChildMsgType() {
		return childMsgType;
	}

	public void setChildMsgType(Integer childMsgType) {
		this.childMsgType = childMsgType;
	}

	@Column(name = "msg_content", length=500)
	public String getMsgContent() {
		return msgContent;
	}
	
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	
	@Column(name = "is_push_msg")
	public Boolean getIsPushMsg() {
		return isPushMsg;
	}

	public void setIsPushMsg(Boolean isPushMsg) {
		this.isPushMsg = isPushMsg;
	}
	
	@Column(name = "web_url")
	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	@Column(name = "is_system_msg")
	public Boolean getIsSystemMsg() {
		return isSystemMsg;
	}

	public void setIsSystemMsg(Boolean isSystemMsg) {
		this.isSystemMsg = isSystemMsg;
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

	@Column(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column(name = "object_type")
	public Integer getObjectType() {
		return objectType;
	}

	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}
	
	@Column(name = "object_types")
	public String getObjectTypes() {
		return objectTypes;
	}

	public void setObjectTypes(String objectTypes) {
		this.objectTypes = objectTypes;
	}

	@Column(name = "image_url")
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	@Column(name = "order_content")
	public String getOrderContent() {
		return orderContent;
	}

	public void setOrderContent(String orderContent) {
		this.orderContent = orderContent;
	}

	@Column(name = "logistics_status")
	public Integer getLogisticsStatus() {
		return logisticsStatus;
	}

	public void setLogisticsStatus(Integer logisticsStatus) {
		this.logisticsStatus = logisticsStatus;
	}

	@Transient
	public Boolean getIsTimeOut() {
		return isTimeOut;
	}

	public void setIsTimeOut(Boolean isTimeOut) {
		this.isTimeOut = isTimeOut;
	}
	
	@Transient
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Transient
	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	@Transient
	public OrderMessageVo getOrderMessageVo() {
		return orderMessageVo;
	}

	public void setOrderMessageVo(OrderMessageVo orderMessageVo) {
		this.orderMessageVo = orderMessageVo;
	}

	@Transient
	public Boolean getIsInvitePage() {
		return isInvitePage;
	}

	public void setIsInvitePage(Boolean isInvitePage) {
		this.isInvitePage = isInvitePage;
	}

	
}
