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
 * 订单商品列表
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_order_items")
public class OrderItems implements Cloneable{
    private Long itemId;					//序号
    private Long orderId;					//订单ID
    private Long subOrderId;				//子订单ID
    private Integer sort;					//子订单排序
    private Long productId;					//商品ID
    private String productCode;				//商品编号
    private String productName;				//商品名称
    private String productSku;				//商品SKU
    private Long productSpecId;				//商品规格ID
    private Long primarySpecId;				//主规格ID
    private String primarySpecName;			//主规格名称
    private String primarySpecModelName;	//主规格类型名称
	private Long secondarySpecId;			//次规格ID
	private String secondarySpecName;		//次规格名称
	private String secondarySpecModelName;	//次规格类型名称
	private String productTags;				//商品规格信息
	private Boolean isMoreSpecProduct;		//是否多规格商品
	private Boolean isSpceProduct;			//是否规格商品
	private Boolean isSeckillProduct;		//是否秒杀商品
	private Boolean isSeckillLimit;         //是否秒杀限购
	private Integer seckillLimitNumber;     //限购数量
	private Long seckillId;					//秒杀场次ID
	private String productImagePath;		//图片地址
    private Long wareHouseId;				//所属仓库ID
    private Integer quantity;				//订单数量		
    private Double price;					//商品单价格	
    private Double amount;					//商品总金额
    private Double realSellPrice;           //商品拿货价
    private Double discountAmount;			//折后商品总金额
    private Double preferentialAmount;  	//让利商品总金额
    private Double weight;					//净重
    private Double profit;					//分销单件商品利润
    private Double topProfit;				//上级分销单件商品利润
    private Double priceCost;				//供货商成本价
    private Double priceWholesale;			//市场成本价
    private Double tax;						//增值税
    private Boolean isFresh;                //是否生鲜类商品
    private Boolean isGroupProduct;			//是否组合商品
    private Boolean isMainGroupItem;		//是否主退款组合商品
    private Long groupProductId;			//组合商品Id
    private String groupUniqueBatch;		//组合商品拆单唯一批次号
    private Integer groupQuantity;			//组合商品订单数量
    private Integer saleTimes;				//出售倍数
    private Boolean isEvaluate;             //是否已评价
    private Boolean isLastProduct;          //是否开启时间段内限购的最后一单商品
    private Boolean isLevelLimitProduct;    //是否等级限购商品
    private Boolean isRechargeProductCoupon;//是否充值赠送商品券
    private Boolean isGiftProduct;          //是否赠品
    private Date createTime;				//创建时间
    private Date updateTime;				//更新时间
    
    //Transient
    private Integer isSoldout;
    private Refund refund;
    private Integer refundStatus;
    private Integer refundType;
    private String rufundStatusName;
    private String wareHouseName;
    private Boolean isMyselfStore;         // 是否自己店铺下单
    private Double discountFee;			   // 商品总让利金额
    private String groupProductName;       // 组合商品名称
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@Column(name="order_id")
    public Long getOrderId() {
        return orderId;
    }

	public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
	
	@Column(name="sub_order_id")
	public Long getSubOrderId() {
		return subOrderId;
	}

	public void setSubOrderId(Long subOrderId) {
		this.subOrderId = subOrderId;
	}

	@Column(name="group_product_id")
	public Long getGroupProductId() {
		return groupProductId;
	}

	public void setGroupProductId(Long groupProductId) {
		this.groupProductId = groupProductId;
	}
	
	@Column(name="is_main_group_item")
	public Boolean getIsMainGroupItem() {
		return isMainGroupItem;
	}

	public void setIsMainGroupItem(Boolean isMainGroupItem) {
		this.isMainGroupItem = isMainGroupItem;
	}

	@Column(name="is_group_product")
	public Boolean getIsGroupProduct() {
		return isGroupProduct;
	}

	public void setIsGroupProduct(Boolean isGroupProduct) {
		this.isGroupProduct = isGroupProduct;
	}

	@Column(name="group_unique_batch")
	public String getGroupUniqueBatch() {
		return groupUniqueBatch;
	}

	public void setGroupUniqueBatch(String groupUniqueBatch) {
		this.groupUniqueBatch = groupUniqueBatch;
	}

	@Column(name="group_quantity")
	public Integer getGroupQuantity() {
		return groupQuantity;
	}

	public void setGroupQuantity(Integer groupQuantity) {
		this.groupQuantity = groupQuantity;
	}

	@Column(name="sort")
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

    @Column(name="quantity")
    public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

    @Column(name="price")
    public Double getPrice() {
		return price;
	}

    public void setPrice(Double price) {
		this.price = price;
	}
    
    @Column(name="amount")
    public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name="discount_amount")
	public Double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Double discountAmount) {
		this.discountAmount = discountAmount;
	}

	@Column(name="product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name="product_code")
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	@Column(name="product_name")
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Column(name="product_sku")
	public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}
	
	@Column(name="product_spec_id")
	public Long getProductSpecId() {
		return productSpecId;
	}

	public void setProductSpecId(Long productSpecId) {
		this.productSpecId = productSpecId;
	}

	@Column(name="primary_spec_id")
	public Long getPrimarySpecId() {
		return primarySpecId;
	}

	public void setPrimarySpecId(Long primarySpecId) {
		this.primarySpecId = primarySpecId;
	}

	@Column(name="primary_spec_name")
	public String getPrimarySpecName() {
		return primarySpecName;
	}

	public void setPrimarySpecName(String primarySpecName) {
		this.primarySpecName = primarySpecName;
	}

	@Column(name="primary_spec_model_name")
	public String getPrimarySpecModelName() {
		return primarySpecModelName;
	}

	public void setPrimarySpecModelName(String primarySpecModelName) {
		this.primarySpecModelName = primarySpecModelName;
	}

	@Column(name="secondary_spec_id")
	public Long getSecondarySpecId() {
		return secondarySpecId;
	}

	public void setSecondarySpecId(Long secondarySpecId) {
		this.secondarySpecId = secondarySpecId;
	}

	@Column(name="secondary_spec_name")
	public String getSecondarySpecName() {
		return secondarySpecName;
	}

	public void setSecondarySpecName(String secondarySpecName) {
		this.secondarySpecName = secondarySpecName;
	}

	@Column(name="secondary_spec_model_name")
	public String getSecondarySpecModelName() {
		return secondarySpecModelName;
	}

	public void setSecondarySpecModelName(String secondarySpecModelName) {
		this.secondarySpecModelName = secondarySpecModelName;
	}

	@Column(name="product_tags")
	public String getProductTags() {
		return productTags;
	}

	public void setProductTags(String productTags) {
		this.productTags = productTags;
	}
	
	@Column(name="is_fresh")
	public Boolean getIsFresh() {
		return isFresh;
	}

	public void setIsFresh(Boolean isFresh) {
		this.isFresh = isFresh;
	}

	@Column(name="is_more_spec_product")
	public Boolean getIsMoreSpecProduct() {
		return isMoreSpecProduct;
	}

	public void setIsMoreSpecProduct(Boolean isMoreSpecProduct) {
		this.isMoreSpecProduct = isMoreSpecProduct;
	}

	@Column(name="is_spce_product")
	public Boolean getIsSpceProduct() {
		return isSpceProduct;
	}

	public void setIsSpceProduct(Boolean isSpceProduct) {
		this.isSpceProduct = isSpceProduct;
	}
	
	@Column(name = "is_seckill_product")
	public Boolean getIsSeckillProduct() {
		return isSeckillProduct;
	}

	public void setIsSeckillProduct(Boolean isSeckillProduct) {
		this.isSeckillProduct = isSeckillProduct;
	}
	
	@Column(name = "is_seckill_limit")
	public Boolean getIsSeckillLimit() {
		return isSeckillLimit;
	}

	public void setIsSeckillLimit(Boolean isSeckillLimit) {
		this.isSeckillLimit = isSeckillLimit;
	}

	@Column(name = "seckill_limit_number")
	public Integer getSeckillLimitNumber() {
		return seckillLimitNumber;
	}

	public void setSeckillLimitNumber(Integer seckillLimitNumber) {
		this.seckillLimitNumber = seckillLimitNumber;
	}

	@Column(name = "seckill_id")
	public Long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(Long seckillId) {
		this.seckillId = seckillId;
	}

	@Column(name="product_image_path")
	public String getProductImagePath() {
		return productImagePath;
	}

	public void setProductImagePath(String productImagePath) {
		this.productImagePath = productImagePath;
	}

	@Column(name="weight")
	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}
	
    @Column(name="profit")
    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    @Column(name="top_profit")
    public Double getTopProfit() {
		return topProfit;
	}

	public void setTopProfit(Double topProfit) {
		this.topProfit = topProfit;
	}

	@Column(name="price_cost")
    public Double getPriceCost() {
        return priceCost;
    }

    public void setPriceCost(Double priceCost) {
        this.priceCost = priceCost;
    }

    @Column(name="tax")
    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    @Column(name="ware_house_id")
	public Long getWareHouseId() {
		return wareHouseId;
	}

	public void setWareHouseId(Long wareHouseId) {
		this.wareHouseId = wareHouseId;
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
	
	@Column(name="price_wholesale")
	public Double getPriceWholesale() {
		return priceWholesale;
	}

	public void setPriceWholesale(Double priceWholesale) {
		this.priceWholesale = priceWholesale;
	}
	
	@Column(name = "preferential_amount")
	public Double getPreferentialAmount() {
		return preferentialAmount;
	}

	public void setPreferentialAmount(Double preferentialAmount) {
		this.preferentialAmount = preferentialAmount;
	}
	
	@Column(name="sale_times")
	public Integer getSaleTimes() {
		return saleTimes;
	}

	public void setSaleTimes(Integer saleTimes) {
		this.saleTimes = saleTimes;
	}
	
	@Column(name="real_sell_price")
	public Double getRealSellPrice() {
		return realSellPrice;
	}

	public void setRealSellPrice(Double realSellPrice) {
		this.realSellPrice = realSellPrice;
	}

	@Column(name="is_evaluate")
	public Boolean getIsEvaluate() {
		return isEvaluate;
	}

	@Column(name="is_last_product", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsLastProduct() {
		return isLastProduct;
	}

	public void setIsLastProduct(Boolean isLastProduct) {
		this.isLastProduct = isLastProduct;
	}

	@Column(name="is_gift_product", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsGiftProduct() {
		return isGiftProduct;
	}

	public void setIsGiftProduct(Boolean isGiftProduct) {
		this.isGiftProduct = isGiftProduct;
	}

	@Column(name="is_recharge_product_coupon", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsRechargeProductCoupon() {
		return isRechargeProductCoupon;
	}

	public void setIsRechargeProductCoupon(Boolean isRechargeProductCoupon) {
		this.isRechargeProductCoupon = isRechargeProductCoupon;
	}

	@Column(name="is_level_limit_product", columnDefinition = "BIT DEFAULT FALSE")
	public Boolean getIsLevelLimitProduct() {
		return isLevelLimitProduct;
	}

	public void setIsLevelLimitProduct(Boolean isLevelLimitProduct) {
		this.isLevelLimitProduct = isLevelLimitProduct;
	}

	public void setIsEvaluate(Boolean isEvaluate) {
		this.isEvaluate = isEvaluate;
	}

	@Transient
	public Integer getIsSoldout() {
		return isSoldout;
	}

	public void setIsSoldout(Integer isSoldout) {
		this.isSoldout = isSoldout;
	}

	@Transient
	public Refund getRefund() {
		return refund;
	}

	public void setRefund(Refund refund) {
		this.refund = refund;
	}

	@Transient
	public Integer getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(Integer refundStatus) {
		this.refundStatus = refundStatus;
	}
	
	@Transient
	public Integer getRefundType() {
		return refundType;
	}

	public void setRefundType(Integer refundType) {
		this.refundType = refundType;
	} 
	
	@Transient
	public String getRufundStatusName() {
		return rufundStatusName;
	}

	public void setRufundStatusName(String rufundStatusName) {
		this.rufundStatusName = rufundStatusName;
	}
	
	@Transient
	public String getWareHouseName() {
		return wareHouseName;
	}

	public void setWareHouseName(String wareHouseName) {
		this.wareHouseName = wareHouseName;
	}
	
	@Transient
	public Boolean getIsMyselfStore() {
		return isMyselfStore;
	}

	public void setIsMyselfStore(Boolean isMyselfStore) {
		this.isMyselfStore = isMyselfStore;
	}
	
	@Transient
	public Double getDiscountFee() {
		return discountFee;
	}

	public void setDiscountFee(Double discountFee) {
		this.discountFee = discountFee;
	}

	@Transient
	public String getGroupProductName() {
		return groupProductName;
	}

	public void setGroupProductName(String groupProductName) {
		this.groupProductName = groupProductName;
	}

	@Override
	public OrderItems clone(){
		//浅拷贝
		try {
			// 直接调用父类的clone()方法
			return (OrderItems) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}


}