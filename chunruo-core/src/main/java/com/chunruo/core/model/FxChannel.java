package com.chunruo.core.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 分销渠道
 * @author chunruo
 */
@Entity
@Table(name="jkd_fx_channel")
public class FxChannel{
	public final static int FX_CHANNEL_STATUS_STOP = 0;		//停止
	public final static int FX_CHANNEL_STATUS_ENABLE = 1;	//启用
	public final static int FX_CHANNEL_STATUS_DELETE = 2;	//删除
	private Long channelId;			//序号
	private String channelName;		//渠道名称
	private Integer status;			//状态(0:停止;1:启用;2:删除)
	private Integer sort;			//排序
	private Date createTime;		//创建时间
	private Date updateTime;		//更新时间
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	@Column(name="channel_name", nullable=false, length=45)
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	@Column(name="status", nullable = false, length=1)
	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	@Column(name = "sort")
	public Integer getSort() {
		return this.sort;
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
