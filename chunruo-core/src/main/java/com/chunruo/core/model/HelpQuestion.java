package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "jkd_help_question")
public class HelpQuestion {

	private Long questionId;
	private Integer sort;
	private Integer type; // 问题类型 （1：售前问题 2：售后问题）
	private String name; // 问题名称
	private String questionDesc; // 问题描述
	private Boolean isNoteRed; // 是否标红

	private Date createTime;
	private Date updateTime;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	@Column(name = "sort")
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "question_desc")
	public String getQuestionDesc() {
		return questionDesc;
	}

	public void setQuestionDesc(String questionDesc) {
		this.questionDesc = questionDesc;
	}

	@Column(name = "is_note_red")
	public Boolean getIsNoteRed() {
		return isNoteRed;
	}

	public void setIsNoteRed(Boolean isNoteRed) {
		this.isNoteRed = isNoteRed;
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

}
