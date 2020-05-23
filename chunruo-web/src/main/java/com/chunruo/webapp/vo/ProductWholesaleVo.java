package com.chunruo.webapp.vo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * 鍟嗗搧鎵瑰彂甯傚満
 * @author chunruo
 *
 */
public class ProductWholesaleVo implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long productId;				//产品地
    private String name;					//产品名称
    private Integer quantity;				//商品数量
    private Double price;					//实际价格(成本价格+上级利润)
    private Double priceRecommend;			//推荐价格
    private Double priceCost;				//成本价格(供货商成本价格)
    private Double priceLowest;				//最低销售价
    private String productCode;				//产品编码
    private String image;					//图片
    private Double profit;					//利润
    private Date usageMethod;               //有效期
    private String createTime;				//创建时间
    private Boolean isAgent;				//是否代理		
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
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
	public Double getPriceLowest() {
		return priceLowest;
	}
	public void setPriceLowest(Double priceLowest) {
		this.priceLowest = priceLowest;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Double getProfit() {
		return profit;
	}
	public void setProfit(Double profit) {
		this.profit = profit;
	}
	public Date getUsageMethod() {
		return usageMethod;
	}
	public void setUsageMethod(Date usageMethod) {
		this.usageMethod = usageMethod;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Boolean getIsAgent() {
		return isAgent;
	}
	public void setIsAgent(Boolean isAgent) {
		this.isAgent = isAgent;
	}
    
}