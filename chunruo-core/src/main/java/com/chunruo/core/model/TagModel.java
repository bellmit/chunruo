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
 * 标签名称列表
 * @author chunruo
 */
@Entity
@Table(name="jkd_tag_model", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"name"})
})
public class TagModel {
	public final static int BRAND_TAG_TYPE = 1;			//商品品牌标签
	public final static int CATEGORY_TAG_TYPE = 2;		//商品分类标签
	public final static int CUSTOM_TAG_TYPE= 3;         //自定义标签
	private Long tagId;
	private String name;			
	private Integer tagType;
	private Long objectId;
	private Boolean isHotWord;          //是否热搜词
	private Integer sort;               //排序
	private Date createTime;
	private Date updateTime;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}
	
	@Column(name="name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="tag_type")
	public Integer getTagType() {
		return tagType;
	}

	public void setTagType(Integer tagType) {
		this.tagType = tagType;
	}
	
	@Column(name="object_id")
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	
	@Column(name="is_hot_word")
	public Boolean getIsHotWord() {
		return isHotWord;
	}

	public void setIsHotWord(Boolean isHotWord) {
		this.isHotWord = isHotWord;
	}

	@Column(name="sort")
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name="update_time")
	public Date getUpdateTime() {
		return updateTime;
	}
	
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
