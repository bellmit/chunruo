package com.chunruo.core.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 同步状态处理记录
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_order_sync")
public class OrderSync{
	public final static String ERP_ORDER_STATUS_CANCEL = "13";	//ERP订单取消状态
	public final static int ORDER_SYNC_STATUS_INIT = 0;			//订单未初始化
	public final static int ORDER_SYNC_STATUS_SUCCESS = 1;		//订单已完成
	public final static int ORDER_SYNC_STATUS_CANCEL = 2;		//订单已取消
	private Long recordId;			//序号
	private String storeName;		//店铺名称
	private String orderNumber;		//订单号
	private Integer orderStatus;	//订单状态
	private int syncNumber;			//同步次数
	private String batchNumber;		//批次号
	private Boolean isSyncSucc;		//是否同步成功
	private String expressNumber;	//快递单号
	private String logisticCode;	//物流公司
	private String logisticName;	//物流公司全名
	private Boolean isHandler;		//是否手动导入快递信息
	private String errorMsg;		//错误信息
	private Date createTime;		//创建时间
	private Date updateTime;		//更新时间
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	
	@Column(name="store_name")
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	@Column(name="order_number", nullable=false, length=50)
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@Column(name="order_status")
	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	@Column(name="is_sync_succ", columnDefinition = "INT DEFAULT 0")
	public Boolean getIsSyncSucc() {
		return isSyncSucc;
	}

	public void setIsSyncSucc(Boolean isSyncSucc) {
		this.isSyncSucc = isSyncSucc;
	}

	
	@Column(name="sync_number", columnDefinition = "INT DEFAULT 0")
	public int getSyncNumber() {
		return syncNumber;
	}
	
	public void setSyncNumber(int syncNumber) {
		this.syncNumber = syncNumber;
	}
	
	@Column(name="batch_number", length=250)
	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	@Column(name="express_number", length=50)
	public String getExpressNumber() {
		return expressNumber;
	}

	public void setExpressNumber(String expressNumber) {
		this.expressNumber = expressNumber;
	}

	@Column(name="logistic_code", length=50)
	public String getLogisticCode() {
		return logisticCode;
	}

	public void setLogisticCode(String logisticCode) {
		this.logisticCode = logisticCode;
	}
	
	@Column(name="logistic_name", length=150)
	public String getLogisticName() {
		return logisticName;
	}

	public void setLogisticName(String logisticName) {
		this.logisticName = logisticName;
	}

	@Column(name="is_handler")
	public Boolean getIsHandler() {
		return isHandler;
	}

	public void setIsHandler(Boolean isHandler) {
		this.isHandler = isHandler;
	}

	@Column(name="error_msg", length=500)
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
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
