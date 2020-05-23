package com.chunruo.core.model;

import java.util.Date; 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 同步订单记录店铺列表
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_order_sync_store")
public class OrderSyncStore{
	private Long appStoreId;
	private String nick;				//店铺昵称
	private Date lastSyncTime;			//最后同步时间
	private Date createTime;			//创建时间
	private Date updateTime;			//更新时间
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getAppStoreId() {
		return appStoreId;
	}

	public void setAppStoreId(Long appStoreId) {
		this.appStoreId = appStoreId;
	}
	@Column(name="nick", nullable=false, length=100)
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	@Column(name="last_sync_time")
	public Date getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(Date lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}

	@Column(name="createTime")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="updateTime")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
