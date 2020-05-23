package com.chunruo.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.chunruo.core.vo.ExpressVO;

/**
 * 订单包裹
 * @author chunruo
 *
 */
@Entity
@Table(name = "jkd_order_package")
public class OrderPackage {
	private Long packageId;
	private Long orderId;				// 订单号
	private String expressCode;			// 承运公司编码
	private String expressCompany;		// 承运公司名称
	private String expressNo;			// 物流编号
	private Boolean isHandler;			//是否手动导入快递信息
	private Date createTime;
	private Date updateTime;
	
	@Transient
	private Integer total;
	private List<OrderItems> orderItemsList = new ArrayList<OrderItems>();
	private ExpressVO expressVO;
	private Date orderCreateTime;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getPackageId() {
		return packageId;
	}

	public void setPackageId(Long packageId) {
		this.packageId = packageId;
	}

	@Column(name = "order_id")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Column(name="express_code", length = 30)
	public String getExpressCode() {
		return expressCode;
	}

	public void setExpressCode(String expressCode) {
		this.expressCode = expressCode == null ? null : expressCode.trim();
	}

	@Column(name="express_company", length = 100)
	public String getExpressCompany() {
		return expressCompany;
	}

	public void setExpressCompany(String expressCompany) {
		this.expressCompany = expressCompany == null ? null : expressCompany.trim();
	}

	@Column(name="express_no", length = 60)
	public String getExpressNo() {
		return expressNo;
	}

	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo == null ? null : expressNo.trim();
	}
	
	@Column(name="is_handler")
	public Boolean getIsHandler() {
		return isHandler;
	}

	public void setIsHandler(Boolean isHandler) {
		this.isHandler = isHandler;
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

	@Transient
	public List<OrderItems> getOrderItemsList() {
		return orderItemsList;
	}

	public void setOrderItemsList(List<OrderItems> orderItemsList) {
		this.orderItemsList = orderItemsList;
	}

	@Transient
	public ExpressVO getExpressVO() {
		return expressVO;
	}

	public void setExpressVO(ExpressVO expressVO) {
		this.expressVO = expressVO;
	}

	@Transient
	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	@Transient
	public Date getOrderCreateTime() {
		return orderCreateTime;
	}

	public void setOrderCreateTime(Date orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}
}
