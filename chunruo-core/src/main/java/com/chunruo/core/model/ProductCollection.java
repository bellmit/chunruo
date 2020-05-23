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
import javax.persistence.UniqueConstraint;

/**
 * 商品收藏
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_product_collection",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"product_id","user_id"})
})
public class ProductCollection implements Cloneable { 
	private Long collectionId;			//序号
	private Long productId;				//商品ID
    private Long userId;				//用户ID
    private Boolean status;				//是否收藏
    private Date createTime;			//创建时间
    private Date updateTime;			//更新时间

    @Transient
    private Product product;            //商品详情
    private Long longSort;				//关键字搜索排序
    List<ProductCollectionProfit> collectionProfitList = new ArrayList<ProductCollectionProfit>();  //商品利润信息
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(Long collectionId) {
		this.collectionId = collectionId;
	}

	@Column(name="product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name="user_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name="status")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
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
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Transient
	public Long getLongSort() {
		return longSort;
	}

	public void setLongSort(Long longSort) {
		this.longSort = longSort;
	}

	@Transient
	public List<ProductCollectionProfit> getCollectionProfitList() {
		return collectionProfitList;
	}

	public void setCollectionProfitList(List<ProductCollectionProfit> collectionProfitList) {
		this.collectionProfitList = collectionProfitList;
	}
	
	
}