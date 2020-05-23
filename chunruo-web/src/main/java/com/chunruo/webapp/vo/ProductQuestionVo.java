package com.chunruo.webapp.vo;

import java.util.Date;

import com.chunruo.core.model.ProductQuestion;

/**
 * 商品疑问
 * @author xuzhongwei
 *
 */
public class ProductQuestionVo {
	private Long questionId;				//问题id
	private Long productId;					//对应的商品id
	private Long userId;					//提出问题的用户id
	private Boolean status = false;			//状态 false-审核中  true-审核通过 默认为false
	private String content;					//问题内容
	private Date createTime;				//创建时间
	private Date updateTime;				//更新时间
	private String userName;				//用户昵称
	private String productName;				//商品名称
	private String image;					//商品图片
	
	public ProductQuestionVo(){}
	
	public ProductQuestionVo(ProductQuestion question){
		this.questionId = question.getQuestionId();
		this.status = question.getStatus();
		this.userId = question.getUserId();
		this.content = question.getContent();
		this.createTime = question.getCreateTime();
		this.updateTime = question.getUpdateTime();
		this.productId = question.getProductId();
	}
	public Long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
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
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	

	
}
