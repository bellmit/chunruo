package com.chunruo.portal.vo;

/**
 * 商品对象校验
 * @author chunruo
 *
 */
public class ProductVerifyVo {  
	private Long productSpecId;             //商品规格id  
	private Integer stockNumber;			//商品库数量
    private Double priceWholesale;			//最低市场价格(成本价格+上级利润)
    private Double priceRecommend;			//售卖价格
    private Double v2Price;                 //v2价
    private Double v3Price;                 //v3价
//    private Double minSellPrice;            //最低售价
    private Double priceCost;				//成本价格(供货商成本价格)
	private Double seckillPrice;		    //秒杀价格
	private Double seckillMinSellPrice;     //秒杀最低售价
    private Double seckillProfit;			//秒杀利润
    private Integer seckillTotalStock;		//秒杀库存数量
    private Integer seckillSalesNumber;		//秒杀商品销量
    private Integer seckillLockNumber;		//秒杀锁定库存数量
    private Integer seckillLimitNumber;		//秒杀商品限购数量
    
    
	public Long getProductSpecId() {
		return productSpecId;
	}

	public void setProductSpecId(Long productSpecId) {
		this.productSpecId = productSpecId;
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
	
	public Double getV2Price() {
		return v2Price;
	}

	public void setV2Price(Double v2Price) {
		this.v2Price = v2Price;
	}

	public Double getV3Price() {
		return v3Price;
	}

	public void setV3Price(Double v3Price) {
		this.v3Price = v3Price;
	}

	public Double getSeckillPrice() {
		return seckillPrice;
	}
	
	public void setSeckillPrice(Double seckillPrice) {
		this.seckillPrice = seckillPrice;
	}
	
	public Double getSeckillProfit() {
		return seckillProfit;
	}
	
	public void setSeckillProfit(Double seckillProfit) {
		this.seckillProfit = seckillProfit;
	}
	
//	public Double getMinSellPrice() {
//		return minSellPrice;
//	}
//
//	public void setMinSellPrice(Double minSellPrice) {
//		this.minSellPrice = minSellPrice;
//	}

	public Double getSeckillMinSellPrice() {
		return seckillMinSellPrice;
	}

	public void setSeckillMinSellPrice(Double seckillMinSellPrice) {
		this.seckillMinSellPrice = seckillMinSellPrice;
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
}
