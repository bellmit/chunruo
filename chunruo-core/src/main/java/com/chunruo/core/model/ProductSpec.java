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
 * 规格/库存
 * @author chunruo
*/
@Entity
@Table(name="jkd_product_spec")
public class ProductSpec implements Cloneable{
	private Long productSpecId;				//序号
	private Long productId;					//商品ID
	private String productCode;				//商品编码
	private String productSku;				//商品编号
	private Long primarySpecId;				//主规格Id
	private Long secondarySpecId;			//主规格名称
	private String specImagePath;			//规格图片地址
	private Double v2Price;                 //v2价
	private Double v3Price;                 //v3价
    private Double priceWholesale;			//最低市场价格(成本价格+上级利润)
    private Double priceRecommend;			//售卖价格
    private Double priceCost;				//成本价格(供货商成本价格)
    private Double seckillPrice;			//秒杀价格
    private Double seckillProfit;			//秒杀利润
    private Integer seckillTotalStock;		//秒杀库存数量
    private Integer seckillSalesNumber;		//秒杀商品销量
    private Integer seckillLimitNumber;		//秒杀商品限购数量
	private Integer stockNumber;			//库存数量
	private Integer salesNumber;            //商品销量
	private Double weigth;					//商品重量
	private String primarySpecName;			//主规格名称
	private String primarySpecModelName;	//主规格类型名称
	private String secondarySpecName;		//次规格名称
	private String secondarySpecModelName;	//次规格类型名称
	private String productTags;				//商品规格信息
	private Integer sort;					//排序
	private Date createTime;				//创建时间
	private Date updateTime;				//更新时间
	
	@Transient
	private Double tax;                     //税费
	private String tmpPrimarySpecId;		//临时主规格ID
	private String tmpSecondarySpecId;		//临时次规格ID
	private Boolean isMoreSpecProduct;		//是否多规格商品
	private Integer buyQuantity;			//购买数量
	private Boolean isPaymentSoldout;		//是否售罄
	private Double paymentPrice;			//实际支付价格
	private Integer paymentStockNumber;		//实际库存信息
	private String priceDiscount;			//优惠信息
	private Integer seckillLockNumber;		//秒杀锁定库存数量
	private String wareHouseName;			//仓库名称
	private Long groupId;					//组合商品Id
	private String groupIds;				//组合商品Ids(pc端使用)
	private Integer saleTimes;				//组合购买分数
	private String productSpecDesc;        	//规格描述
	private Boolean isSeckillLimit;			//是否秒杀限购
	private Integer seckillExistBuyNum;		//秒杀已购买总数量
	private Integer seckillWaitBuyNum;		//秒杀待支付总数量
	private Integer yearNumber;             //大礼包年份
	private String discount;                //优惠
	private Integer productProfit;          //规格利润
	private Double realSellPrice;           //用户等级真正价格
	private Integer soldoutNoticeType;      //售罄通知类型
	private String soldoutNotice;           //售罄通知说明

	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getProductSpecId() {
		return productSpecId;
	}

	public void setProductSpecId(Long productSpecId) {
		this.productSpecId = productSpecId;
	}
	
	@Column(name="product_code")
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	@Column(name="product_sku")
	public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}
	
	@Column(name="product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	@Column(name="primary_spec_id")
	public Long getPrimarySpecId() {
		return primarySpecId;
	}

	public void setPrimarySpecId(Long primarySpecId) {
		this.primarySpecId = primarySpecId;
	}
	
	@Column(name="secondary_spec_id")
	public Long getSecondarySpecId() {
		return secondarySpecId;
	}

	public void setSecondarySpecId(Long secondarySpecId) {
		this.secondarySpecId = secondarySpecId;
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

	@Column(name="spec_image_path")
	public String getSpecImagePath() {
		return specImagePath;
	}

	public void setSpecImagePath(String specImagePath) {
		this.specImagePath = specImagePath;
	}
	
	@Column(name="stock_number")
	public Integer getStockNumber() {
		return stockNumber;
	}
	
	public void setStockNumber(Integer stockNumber) {
		this.stockNumber = stockNumber;
	}
	
	@Column(name="sales_number")
	public Integer getSalesNumber() {
		return salesNumber;
	}

	public void setSalesNumber(Integer salesNumber) {
		this.salesNumber = salesNumber;
	}

	@Column(name="v2_price")
	public Double getV2Price() {
		return v2Price;
	}

	public void setV2Price(Double v2Price) {
		this.v2Price = v2Price;
	}

	@Column(name="v3_price")
	public Double getV3Price() {
		return v3Price;
	}

	public void setV3Price(Double v3Price) {
		this.v3Price = v3Price;
	}

	@Column(name="price_wholesale")
    public Double getPriceWholesale() {
		return priceWholesale;
	}

	public void setPriceWholesale(Double priceWholesale) {
		this.priceWholesale = priceWholesale;
	}

    @Column(name="price_recommend")
    public Double getPriceRecommend() {
		return priceRecommend;
	}


	public void setPriceRecommend(Double priceRecommend) {
		this.priceRecommend = priceRecommend;
	}

    @Column(name="price_cost")
    public Double getPriceCost() {
        return priceCost;
    }

	public void setPriceCost(Double priceCost) {
        this.priceCost = priceCost;
    }

	@Column(name="seckill_price")
	public Double getSeckillPrice() {
		return seckillPrice;
	}

	public void setSeckillPrice(Double seckillPrice) {
		this.seckillPrice = seckillPrice;
	}

	@Column(name="seckill_profit")
	public Double getSeckillProfit() {
		return seckillProfit;
	}

	public void setSeckillProfit(Double seckillProfit) {
		this.seckillProfit = seckillProfit;
	}

	@Column(name="seckill_total_stock")
	public Integer getSeckillTotalStock() {
		return seckillTotalStock;
	}

	public void setSeckillTotalStock(Integer seckillTotalStock) {
		this.seckillTotalStock = seckillTotalStock;
	}

	@Column(name="seckill_sales_number")
	public Integer getSeckillSalesNumber() {
		return seckillSalesNumber;
	}

	public void setSeckillSalesNumber(Integer seckillSalesNumber) {
		this.seckillSalesNumber = seckillSalesNumber;
	}
	
	@Column(name="seckill_limit_number")
	public Integer getSeckillLimitNumber() {
		return seckillLimitNumber;
	}

	public void setSeckillLimitNumber(Integer seckillLimitNumber) {
		this.seckillLimitNumber = seckillLimitNumber;
	}
	
	@Column(name="weigth")
	public Double getWeigth() {
		return weigth;
	}

	public void setWeigth(Double weigth) {
		this.weigth = weigth;
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

	@Transient
	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	@Transient
	public Boolean getIsPaymentSoldout() {
		return isPaymentSoldout;
	}

	public void setIsPaymentSoldout(Boolean isPaymentSoldout) {
		this.isPaymentSoldout = isPaymentSoldout;
	}

	@Transient
	public Integer getBuyQuantity() {
		return buyQuantity;
	}

	public void setBuyQuantity(Integer buyQuantity) {
		this.buyQuantity = buyQuantity;
	}

	@Transient
	public String getTmpPrimarySpecId() {
		return tmpPrimarySpecId;
	}

	public void setTmpPrimarySpecId(String tmpPrimarySpecId) {
		this.tmpPrimarySpecId = tmpPrimarySpecId;
	}

	@Transient
	public String getTmpSecondarySpecId() {
		return tmpSecondarySpecId;
	}

	public void setTmpSecondarySpecId(String tmpSecondarySpecId) {
		this.tmpSecondarySpecId = tmpSecondarySpecId;
	}
	
	@Transient
	public Boolean getIsMoreSpecProduct() {
		return isMoreSpecProduct;
	}

	public void setIsMoreSpecProduct(Boolean isMoreSpecProduct) {
		this.isMoreSpecProduct = isMoreSpecProduct;
	}
	
	@Transient
	public Double getPaymentPrice() {
		return paymentPrice;
	}

	public void setPaymentPrice(Double paymentPrice) {
		this.paymentPrice = paymentPrice;
	}

	@Transient
	public Integer getPaymentStockNumber() {
		return paymentStockNumber;
	}

	public void setPaymentStockNumber(Integer paymentStockNumber) {
		this.paymentStockNumber = paymentStockNumber;
	}

	@Transient
	public String getPriceDiscount() {
		return priceDiscount;
	}

	public void setPriceDiscount(String priceDiscount) {
		this.priceDiscount = priceDiscount;
	}
	
	@Transient
	public Integer getSeckillLockNumber() {
		return seckillLockNumber;
	}

	public void setSeckillLockNumber(Integer seckillLockNumber) {
		this.seckillLockNumber = seckillLockNumber;
	}

	@Transient
	public String getWareHouseName() {
		return wareHouseName;
	}

	public void setWareHouseName(String wareHouseName) {
		this.wareHouseName = wareHouseName;
	}
	
	@Transient
	public Integer getSaleTimes() {
		return saleTimes;
	}

	public void setSaleTimes(Integer saleTimes) {
		this.saleTimes = saleTimes;
	}

	@Transient
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@Transient
	public String getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}

	@Transient
	public String getProductSpecDesc() {
		return productSpecDesc;
	}

	public void setProductSpecDesc(String productSpecDesc) {
		this.productSpecDesc = productSpecDesc;
	}

	@Transient
	public Boolean getIsSeckillLimit() {
		return isSeckillLimit;
	}

	public void setIsSeckillLimit(Boolean isSeckillLimit) {
		this.isSeckillLimit = isSeckillLimit;
	}
	
	@Transient
	public Integer getSeckillExistBuyNum() {
		return seckillExistBuyNum;
	}

	public void setSeckillExistBuyNum(Integer seckillExistBuyNum) {
		this.seckillExistBuyNum = seckillExistBuyNum;
	}

	@Transient
	public Integer getSeckillWaitBuyNum() {
		return seckillWaitBuyNum;
	}

	public void setSeckillWaitBuyNum(Integer seckillWaitBuyNum) {
		this.seckillWaitBuyNum = seckillWaitBuyNum;
	}
	
	@Transient
	public Integer getYearNumber() {
		return yearNumber;
	}

	public void setYearNumber(Integer yearNumber) {
		this.yearNumber = yearNumber;
	}

	@Transient
	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	@Transient
	public Integer getProductProfit() {
		return productProfit;
	}

	public void setProductProfit(Integer productProfit) {
		this.productProfit = productProfit;
	}

	@Transient
	public Double getRealSellPrice() {
		return realSellPrice;
	}

	public void setRealSellPrice(Double realSellPrice) {
		this.realSellPrice = realSellPrice;
	}

	@Transient
	public Integer getSoldoutNoticeType() {
		return soldoutNoticeType;
	}

	public void setSoldoutNoticeType(Integer soldoutNoticeType) {
		this.soldoutNoticeType = soldoutNoticeType;
	}
	
	@Transient
	public String getSoldoutNotice() {
		return soldoutNotice;
	}

	public void setSoldoutNotice(String soldoutNotice) {
		this.soldoutNotice = soldoutNotice;
	}

	@Override
	public ProductSpec clone(){
		//浅拷贝
		try {
			// 直接调用父类的clone()方法
			return (ProductSpec) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
