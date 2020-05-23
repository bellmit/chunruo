package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 搜索关键词
 * @author admin
 *
 */
@Entity
@Table(name="jkd_keywords")
public class Keywords {
	private Long keywordsId;				//关键词ID
    private String name;					//关键词名称
    private Integer seekCount;				//搜索次数
    private Boolean isDefault;				//是否默认搜索
    private Date createTime;				//创建时间
    private Date updateTime;				//更新时间

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getKeywordsId() {
		return keywordsId;
	}

	public void setKeywordsId(Long keywordsId) {
		this.keywordsId = keywordsId;
	}
	
    @Column(name="name", length=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Column(name="seek_count")
    public Integer getSeekCount() {
		return seekCount;
	}

	public void setSeekCount(Integer seekCount) {
		this.seekCount = seekCount;
	}

    
    @Column(name="is_default")
    public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
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
