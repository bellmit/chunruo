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
 * 商品疑问
 * @author xuzhongwei
 *
 */
@Entity
@Table(name="jkd_product_questions")
public class ProductQuestion {
	private Long questionId;				//问题id
	private Long productId;					//对应的商品id
	private Long userId;					//提出问题的用户id
	private Boolean status = false;			//状态 false-审核中  true-审核通过 默认为false
	private String content;					//问题内容
	private Boolean isDelete;				//是否删除 false-使用中 true- 已删除
	private Date createTime;				//创建时间
	private Date updateTime;				//更新时间
	
	//@Transient
	private Integer answerNumber = 0;		//回答的总数
	private String answerContent;			//第一个回答的内容
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	
	@Column(name = "product_id", nullable = false)
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
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
	public Integer getAnswerNumber() {
		return answerNumber;
	}
	public void setAnswerNumber(Integer answerNumber) {
		this.answerNumber = answerNumber;
	}
	
	@Transient
	public String getAnswerContent() {
		return answerContent;
	}
	public void setAnswerContent(String answerContent) {
		this.answerContent = answerContent;
	}
	

	
}
