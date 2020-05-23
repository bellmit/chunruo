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
import javax.persistence.UniqueConstraint;

/**
 * 订单商品评价
 * @author hehai
 */
@Entity
@Table(name = "jkd_order_evaluate",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"item_id"})
})
public class OrderEvaluate {
	public final static Integer EVALUATE_RECORD_STATUS_WAIT = 0;		//等待审核
	public final static Integer EVALUATE_RECORD_STATUS_NORMAL = 1;		//普通
	public final static Integer EVALUATE_RECORD_STATUS_GOOD = 2;		//一般
	public final static Integer EVALUATE_RECORD_STATUS_EXCELLENT = 3;	//精选
	
	private Long evaluateId;
	private Long userId;
	private Long orderId;   		//订单id
	private Long itemId;    		//订单详情id
	private Long productId; 		//商品id
	private Integer status; 		//审核状态 0 待审核 1 普通 2 一般 3 精选
	private Integer score;  		//评分 1-5
	private String content; 		//评论内容
	private String imagePath;       //商品图片
	private Date createTime; 	    // 创建时间
	private Date updateTime; 	    // 更新时间
	
	@Transient
	private OrderItems orderItems;
	private String userHeaderImage; //用户头像
	private String nickName;        //用户昵称
	private List<String> imagePathList = new ArrayList<String>();  //图片集合
	private String imageQuantity;                                  //图片张数
	private String evaluateTime;                                   //评价时间
	private String evaluateRate;                                   //好评率

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getEvaluateId() {
		return evaluateId;
	}

	public void setEvaluateId(Long evaluateId) {
		this.evaluateId = evaluateId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Column(name = "item_id")
	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@Column(name = "product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "score")
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	@Column(name = "content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "image_path")
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
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
	public OrderItems getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(OrderItems orderItems) {
		this.orderItems = orderItems;
	}

	@Transient
	public String getUserHeaderImage() {
		return userHeaderImage;
	}

	public void setUserHeaderImage(String userHeaderImage) {
		this.userHeaderImage = userHeaderImage;
	}

	@Transient
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Transient
	public List<String> getImagePathList() {
		return imagePathList;
	}

	public void setImagePathList(List<String> imagePathList) {
		this.imagePathList = imagePathList;
	}

	@Transient
	public String getImageQuantity() {
		return imageQuantity;
	}

	public void setImageQuantity(String imageQuantity) {
		this.imageQuantity = imageQuantity;
	}
	
	@Transient
	public String getEvaluateTime() {
		return evaluateTime;
	}

	public void setEvaluateTime(String evaluateTime) {
		this.evaluateTime = evaluateTime;
	}

	@Transient
	public String getEvaluateRate() {
		return evaluateRate;
	}

	public void setEvaluateRate(String evaluateRate) {
		this.evaluateRate = evaluateRate;
	}
	
	
}
