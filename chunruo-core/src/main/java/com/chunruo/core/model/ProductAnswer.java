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
 * 商品疑问回答
 * @author xuzhongwei
 *
 */
@Entity
@Table(name="jkd_product_answers")
public class ProductAnswer {
	private Long answerId;					//问题id
	private Long questionId;				//对应的商品id
	private Long userId;					//提出问题的用户id
	private Boolean status = false;			//状态 false-审核中  true-审核通过 默认为false
	private Boolean isDelete;				//是否删除 false-使用中 true- 已删除
	private String content;					//问题内容
	private Date createTime;				//创建时间
	private Date updateTime;				//更新时间

	//@Transient
	private UserInfo userInfo;				//用户信息
	private String productName;             //商品名称
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getAnswerId() {
		return answerId;
	}
	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}
	
	@Column(name = "question_id", nullable = false)
	public Long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	
	@Column(name = "user_id", nullable = false)
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name = "status", nullable = false)
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	@Column(name = "content", nullable = false)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Column(name = "is_delete", nullable = false)
	public Boolean getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}
	
	@Column(name = "create_time", nullable = false)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name = "update_time", nullable = false)
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	@Transient
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	@Transient
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	
}
