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
 * 用户购物车
 * @author chunruo
 */
@Entity
@Table(name="jkd_user_cart")
public class UserCart {
    private Long cartId;			//序号
    private Long userId;			//用户ID
    private Long productId;			//商品ID
    private Long productSpecId;		//规格ID
    private Boolean isSpceProduct;	//是否规格商品
    private Integer quantity;		//商品数量
    private String groupProductInfo;//组合商品信息
    private Date createTime;		//创建时间
    private Date updateTime;		//更新时间
    
    @Transient
    private Double tax;					//税费
    private Boolean isSoldout;			//是否售罄
    private Boolean isSeckillProduct;	//是否秒杀商品
    private Integer stockNumber;		//商品库存
    private String productName;			//商品名称
    private String productTags;			//商品规格信息
    private Double paymentPrice;		//商品金额
    private Integer productType;		//商品类型
    private Long warehouseTemplateId;   //仓库模板id
    private String imagePath;			//商品图片
    private Boolean isTaskProduct;      //是否任务商品
	private String taskProductTag;      //任务商品标签
	private Boolean isRechargeGiftProduct;//是否赠品
	private String couponIntro;            //优惠券信息
    private Boolean isSeckillStarted;       //是否秒杀已开始
    private Long seckillEndTime;			//秒杀结束时间

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getCartId() {
		return cartId;
	}

	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}

    @Column(name="user_id")
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
    
	@Column(name="group_Product_Info")
	public String getGroupProductInfo() {
		return groupProductInfo;
	}

	public void setGroupProductInfo(String groupProductInfo) {
		this.groupProductInfo = groupProductInfo;
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
	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	@Transient
	public Integer getStockNumber() {
		return stockNumber;
	}

	public void setStockNumber(Integer stockNumber) {
		this.stockNumber = stockNumber;
	}
	
	@Transient
	public Boolean getIsSeckillProduct() {
		return isSeckillProduct;
	}

	public void setIsSeckillProduct(Boolean isSeckillProduct) {
		this.isSeckillProduct = isSeckillProduct;
	}

	@Transient
	public Boolean getIsSoldout() {
		return isSoldout;
	}

	public void setIsSoldout(Boolean isSoldout) {
		this.isSoldout = isSoldout;
	}

	@Transient
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Transient
	public String getProductTags() {
		return productTags;
	}

	public void setProductTags(String productTags) {
		this.productTags = productTags;
	}

	@Transient
	public Double getPaymentPrice() {
		return paymentPrice;
	}

	public void setPaymentPrice(Double paymentPrice) {
		this.paymentPrice = paymentPrice;
	}

	@Transient
	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	@Transient
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Transient
	public Boolean getIsTaskProduct() {
		return isTaskProduct;
	}

	public void setIsTaskProduct(Boolean isTaskProduct) {
		this.isTaskProduct = isTaskProduct;
	}

	@Transient
	public Long getWarehouseTemplateId() {
		return warehouseTemplateId;
	}

	public void setWarehouseTemplateId(Long warehouseTemplateId) {
		this.warehouseTemplateId = warehouseTemplateId;
	}

	@Transient
	public String getTaskProductTag() {
		return taskProductTag;
	}

	public void setTaskProductTag(String taskProductTag) {
		this.taskProductTag = taskProductTag;
	}
	
	@Transient
	public Boolean getIsRechargeGiftProduct() {
		return isRechargeGiftProduct;
	}

	public void setIsRechargeGiftProduct(Boolean isRechargeGiftProduct) {
		this.isRechargeGiftProduct = isRechargeGiftProduct;
	}

	@Transient
	public String getCouponIntro() {
		return couponIntro;
	}

	public void setCouponIntro(String couponIntro) {
		this.couponIntro = couponIntro;
	}

	@Transient
	public Boolean getIsSeckillStarted() {
		return isSeckillStarted;
	}

	public void setIsSeckillStarted(Boolean isSeckillStarted) {
		this.isSeckillStarted = isSeckillStarted;
	}

	@Transient
	public Long getSeckillEndTime() {
		return seckillEndTime;
	}

	public void setSeckillEndTime(Long seckillEndTime) {
		this.seckillEndTime = seckillEndTime;
	}
}