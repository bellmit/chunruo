package com.chunruo.cache.portal.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chunruo.core.model.Product;
import com.chunruo.core.util.StringUtil;

public class ProductSortVo {
	public final static int PRODUCT_SORT_SN = 1;  //销量
	public final static int PRODUCT_SORT_FX	= 2;  //人气
	public final static int PRODUCT_SORT_JG = 3;  //成本
	public final static	int SORT_TYPE_DESC = 2;	  //降序
	public final static int SORT_TYPE_ASC = 1;	  //升序
	
	private Long productId;			//商品Id
	private String productName;		//商品名称
	private Integer salesNumber;    //销量
	private Integer profitPrice;	//利润
	private Integer price;			//成本价格
	private Long longSort;			//关键字搜索排序
	private Integer priceRecommend; //推荐价
	private String tagNames; 	//商品标签
	private Boolean isSoldout; //是否售罄
	
	/**
	 * 商品信息转换
	 * @param productList
	 * @return
	 */
	public static Map<String, ProductSortVo> getProductSortMap(List<Product> productList){
		Map<String, ProductSortVo> productMap = new HashMap<String, ProductSortVo> ();
		try{
			if(productList != null && productList.size() > 0){
				for(Product product : productList){
					//如果商品不存在或者商品是大礼包商品则跳过
					if(product == null 
							|| product.getProductId() == null 
							|| StringUtil.nullToBoolean(product.getIsPackage())){
						continue;
					}
					
					//设置分销
					int price = StringUtil.nullToDouble(product.getPriceRecommend()).intValue();
					
					ProductSortVo productSortVo = new ProductSortVo ();
					productSortVo.setProductId(product.getProductId());
					productSortVo.setSalesNumber(StringUtil.nullToInteger(product.getSalesNumber()));
					productSortVo.setProductName(StringUtil.null2Str(product.getName()));
					productSortVo.setProfitPrice(0);
					productSortVo.setPrice(price);
					productSortVo.setPrice(StringUtil.nullToInteger(price));
					productMap.put(StringUtil.null2Str(product.getProductId()), productSortVo);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return productMap;
	}
	
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public Integer getProfitPrice() {
		return profitPrice;
	}
	
	public void setProfitPrice(Integer profitPrice) {
		this.profitPrice = profitPrice;
	}
	
	public Integer getPrice() {
		return price;
	}
	
	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getSalesNumber() {
		return salesNumber;
	}

	public void setSalesNumber(Integer salesNumber) {
		this.salesNumber = salesNumber;
	}

	public Long getLongSort() {
		return longSort;
	}

	public void setLongSort(Long longSort) {
		this.longSort = longSort;
	}

	public Integer getPriceRecommend() {
		return priceRecommend;
	}

	public void setPriceRecommend(Integer priceRecommend) {
		this.priceRecommend = priceRecommend;
	}

	public String getTagNames() {
		return tagNames;
	}

	public void setTagNames(String tagNames) {
		this.tagNames = tagNames;
	}

	public Boolean getIsSoldout() {
		return isSoldout;
	}

	public void setIsSoldout(Boolean isSoldout) {
		this.isSoldout = isSoldout;
	}
	
	
}
