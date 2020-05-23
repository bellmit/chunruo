package com.chunruo.webapp.vo;

import java.util.Date;

import com.chunruo.core.model.ProductAnswer;
/**
 * 商品疑问回答
 * @author xuzhongwei
 *
 */
public class ProductAnswerVo {
	private Long answerId;					//问题id
	private Long questionId;				//对应的商品id
	private Long userId;					//提出问题的用户id
	private Boolean status = false;			//状态 false-审核中  true-审核通过 默认为false
	private String content;					//问题内容
	private Date createTime;				//创建时间
	private Date updateTime;				//更新时间
	private String userName;				//用户信息
	private String questionContent;			//问题内容
	private String productName;				//商品名称
	public ProductAnswerVo(){}
	public ProductAnswerVo(ProductAnswer answer){
		this.answerId = answer.getAnswerId();
		this.questionId = answer.getQuestionId();
		this.userId = answer.getUserId();
		this.status = answer.getStatus();
		this.content = answer.getContent();
		this.createTime = answer.getCreateTime();
		this.updateTime = answer.getUpdateTime();
	}
	public Long getAnswerId() {
		return answerId;
	}
	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}
	
	public Long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getQuestionContent() {
		return questionContent;
	}
	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
}
