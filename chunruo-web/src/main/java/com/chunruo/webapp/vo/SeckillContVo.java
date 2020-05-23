package com.chunruo.webapp.vo;

/**
 * 秒杀商品编辑对象
 * @author chunruo
 *
 */
public class SeckillContVo {
	private Long objectId;
	private Long productId;					//商品Id
	private Long productSpecId;				//规格商品Id
	private String name;				    //商品名称
	private String productCode;				//商品货号
	private String productTags;             //规格标签
	private Double seckillPrice;			//秒杀价格
	private Double seckillMinSellPrice;     //秒杀最低售价
    private Double seckillProfit;			//秒杀利润
    private Integer seckillTotalStock;		//秒杀库存数量
    private Integer seckillSalesNumber;		//秒杀商品销量
    private Integer seckillLockNumber;		//秒杀锁定库存数量
    private Integer seckillLimitNumber;		//秒杀限制购买数量
    private Integer stockNumber;			//商品库数量
    private Double priceWholesale;			//最低市场价格(成本价格+上级利润)
    private Double priceRecommend;			//售卖价格
    private Double priceCost;				//成本价格(供货商成本价格)
    private Integer seckillSort;            //秒杀排序
    
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Long getProductId() {
		return productId;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	public Long getProductSpecId() {
		return productSpecId;
	}
	
	public void setProductSpecId(Long productSpecId) {
		this.productSpecId = productSpecId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getProductCode() {
		return productCode;
	}
	
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	public String getProductTags() {
		return productTags;
	}

	public void setProductTags(String productTags) {
		this.productTags = productTags;
	}

	public Double getSeckillPrice() {
		return seckillPrice;
	}
	
	public void setSeckillPrice(Double seckillPrice) {
		this.seckillPrice = seckillPrice;
	}
	
	public Double getSeckillMinSellPrice() {
		return seckillMinSellPrice;
	}

	public void setSeckillMinSellPrice(Double seckillMinSellPrice) {
		this.seckillMinSellPrice = seckillMinSellPrice;
	}

	public Double getSeckillProfit() {
		return seckillProfit;
	}
	
	public void setSeckillProfit(Double seckillProfit) {
		this.seckillProfit = seckillProfit;
	}
	
	public Integer getSeckillTotalStock() {
		return seckillTotalStock;
	}
	
	public void setSeckillTotalStock(Integer seckillTotalStock) {
		this.seckillTotalStock = seckillTotalStock;
	}
	
	public Integer getSeckillSalesNumber() {
		return seckillSalesNumber;
	}
	
	public void setSeckillSalesNumber(Integer seckillSalesNumber) {
		this.seckillSalesNumber = seckillSalesNumber;
	}
	
	public Integer getStockNumber() {
		return stockNumber;
	}
	
	public void setStockNumber(Integer stockNumber) {
		this.stockNumber = stockNumber;
	}
	
	public Double getPriceWholesale() {
		return priceWholesale;
	}
	
	public void setPriceWholesale(Double priceWholesale) {
		this.priceWholesale = priceWholesale;
	}
	
	public Double getPriceRecommend() {
		return priceRecommend;
	}
	
	public void setPriceRecommend(Double priceRecommend) {
		this.priceRecommend = priceRecommend;
	}
	
	public Double getPriceCost() {
		return priceCost;
	}
	
	public void setPriceCost(Double priceCost) {
		this.priceCost = priceCost;
	}

	public Integer getSeckillLockNumber() {
		return seckillLockNumber;
	}

	public void setSeckillLockNumber(Integer seckillLockNumber) {
		this.seckillLockNumber = seckillLockNumber;
	}

	public Integer getSeckillLimitNumber() {
		return seckillLimitNumber;
	}

	public void setSeckillLimitNumber(Integer seckillLimitNumber) {
		this.seckillLimitNumber = seckillLimitNumber;
	}

	public Integer getSeckillSort() {
		return seckillSort;
	}

	public void setSeckillSort(Integer seckillSort) {
		this.seckillSort = seckillSort;
	}
}
