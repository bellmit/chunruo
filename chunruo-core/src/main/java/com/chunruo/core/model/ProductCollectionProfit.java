package com.chunruo.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * 商品代理利润表
 * 
 * @author Administrator
 *
 */
@Entity
@Table(name = "jkd_product_collection_profit", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "product_id", "product_spec_id", "user_id" }) })
public class ProductCollectionProfit {

	private Long profitId;
	private Long productId;      // 商品id
	private Long productSpecId;  // 规格id
	private Long userId;         // 用户id
	private Integer profit;      // 分享利润
	private Date createTime;
	private Date updateTime;
	
	@Transient
	private Double priceWholesale;  //商品市场价
	private String productTags;     //规格名称
	private Double sellPrice;       //出售价格
	private Double tax;             //税费
//	private Integer minProfit;       //最低利润

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getProfitId() {
		return profitId;
	}

	public void setProfitId(Long profitId) {
		this.profitId = profitId;
	}

	@Column(name = "user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "product_spec_id")
	public Long getProductSpecId() {
		return productSpecId;
	}

	public void setProductSpecId(Long productSpecId) {
		this.productSpecId = productSpecId;
	}

	@Column(name = "profit")
	public Integer getProfit() {
		return profit;
	}

	public void setProfit(Integer profit) {
		this.profit = profit;
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
	public Double getPriceWholesale() {
		return priceWholesale;
	}

	public void setPriceWholesale(Double priceWholesale) {
		this.priceWholesale = priceWholesale;
	}

	@Transient
	public String getProductTags() {
		return productTags;
	}

	public void setProductTags(String productTags) {
		this.productTags = productTags;
	}

	@Transient
	public Double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(Double sellPrice) {
		this.sellPrice = sellPrice;
	}

	@Transient
	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

//	@Transient
//	public Integer getMinProfit() {
//		return minProfit;
//	}
//
//	public void setMinProfit(Integer minProfit) {
//		this.minProfit = minProfit;
//	}
}
