package com.chunruo.core.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Transient;

/**
 * 订单秒杀库存
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_order_lock_stock",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"item_id"})
})
public class OrderLockStock implements Cloneable{
	public static Integer ORDER_LOCK_STOCK_SECKILL = 1;  //秒杀
	public static Integer ORDER_LOCK_STOCK_LEVEL = 2;    //等级限购
	public static Integer ORDER_LOCK_STOCK_SECLEVEL = 3; //秒杀&限购
	
	private Long lockStockId;				//序号
	private Long orderId;					//订单Id
	private Long itemId;					//订单商品序号
	private Long productId;					//商品Id
	private Long productSpecId;				//商品规格ID
	private Long seckillId;					//秒杀场次ID
	private Boolean isSpceProduct;			//是否规格商品
	private Integer quantity;				//订单数量
	private Boolean status;					//状态
	private Integer type;                   //锁库类型（1：秒杀，2：等级限购，3：秒杀&限购）
	private Date createTime;				//创建时间
	private Date updateTime;				//更新时间
	
	@Transient
	private Integer orderStatus;			//订单状态
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getLockStockId() {
		return lockStockId;
	}
	
	public void setLockStockId(Long lockStockId) {
		this.lockStockId = lockStockId;
	}
	
	@Column(name="order_id")
	public Long getOrderId() {
		return orderId;
	}
	
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	@Column(name="item_id")
	public Long getItemId() {
		return itemId;
	}
	
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	
	@Column(name="product_id")
	public Long getProductId() {
		return productId;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	@Column(name="product_spec_id")
	public Long getProductSpecId() {
		return productSpecId;
	}
	
	public void setProductSpecId(Long productSpecId) {
		this.productSpecId = productSpecId;
	}
	
	@Column(name="seckill_id")
	public Long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(Long seckillId) {
		this.seckillId = seckillId;
	}

	@Column(name="status")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Column(name="is_spce_product")
    public Boolean getIsSpceProduct() {
		return isSpceProduct;
	}

	public void setIsSpceProduct(Boolean isSpceProduct) {
		this.isSpceProduct = isSpceProduct;
	}
	
	@Column(name="quantity")
	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Column(name="type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	@Transient
	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
}
