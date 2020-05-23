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
  * 商品分组
  * @author chunruo
  *
  */
@Entity
@Table(name="jkd_product_group")
public class ProductGroup implements Cloneable{
	private Long groupId;
	private Long productGroupId;			//组合商品Id
	private Long productId;					//子商品ID
	private Long productSpecId;				//规格商品Id
	private Double groupPriceCost;			//组合成本价格
	private Double groupPriceWholesale;		//组合市场价格
	private Double groupPriceRecommend;		//组合售卖价格
    private Integer saleTimes;				//出售商品倍数
    private String productTags;				//商品标签
	private Date createTime;
	private Date updateTime;
	
	//Transient
	private Integer paymentStockNumber;
	private Double paymentPrice;
	private Boolean isSpceProduct;
	private Boolean isPaymentSoldout;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
    @Column(name="product_group_id")
    public Long getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(Long productGroupId) {
		this.productGroupId = productGroupId;
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
	
	@Column(name="group_price_cost")
	public Double getGroupPriceCost() {
		return groupPriceCost;
	}

	public void setGroupPriceCost(Double groupPriceCost) {
		this.groupPriceCost = groupPriceCost;
	}

	@Column(name="group_price_recommend")
	public Double getGroupPriceRecommend() {
		return groupPriceRecommend;
	}

	public void setGroupPriceRecommend(Double groupPriceRecommend) {
		this.groupPriceRecommend = groupPriceRecommend;
	}

	@Column(name="group_price_wholesale")
	public Double getGroupPriceWholesale() {
		return groupPriceWholesale;
	}

	public void setGroupPriceWholesale(Double groupPriceWholesale) {
		this.groupPriceWholesale = groupPriceWholesale;
	}

	@Column(name="sale_times")
	public Integer getSaleTimes() {
		return saleTimes;
	}

	public void setSaleTimes(Integer saleTimes) {
		this.saleTimes = saleTimes;
	}
	
	@Column(name="product_tags")
	public String getProductTags() {
		return productTags;
	}

	public void setProductTags(String productTags) {
		this.productTags = productTags;
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
	public Integer getPaymentStockNumber() {
		return paymentStockNumber;
	}

	public void setPaymentStockNumber(Integer paymentStockNumber) {
		this.paymentStockNumber = paymentStockNumber;
	}

	@Transient
	public Double getPaymentPrice() {
		return paymentPrice;
	}

	public void setPaymentPrice(Double paymentPrice) {
		this.paymentPrice = paymentPrice;
	}

	@Transient
	public Boolean getIsSpceProduct() {
		return isSpceProduct;
	}

	public void setIsSpceProduct(Boolean isSpceProduct) {
		this.isSpceProduct = isSpceProduct;
	}

	@Transient
	public Boolean getIsPaymentSoldout() {
		return isPaymentSoldout;
	}

	public void setIsPaymentSoldout(Boolean isPaymentSoldout) {
		this.isPaymentSoldout = isPaymentSoldout;
	}
	
	@Override
	public ProductGroup clone(){
		//浅拷贝
		try {
			// 直接调用父类的clone()方法
			return (ProductGroup) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}