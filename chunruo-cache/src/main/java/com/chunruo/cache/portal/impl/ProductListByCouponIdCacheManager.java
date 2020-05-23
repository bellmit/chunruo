package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.Constants;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.Product;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.util.StringUtil;

@Service("productListByCouponIdCacheManager")
public class ProductListByCouponIdCacheManager {
	@Autowired
	private ProductManager productManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productListByCouponId_'+#couponId")
	public List<String> getSession(Long couponId){
		List<String> productIdList = new ArrayList<String> ();
		try{
			Coupon coupon = Constants.COUPON_MAP.get(couponId);
			if(coupon != null && coupon.getCouponId() != null){
				List<Product> productList = null;
				List<Long> contentIdList = null;
				try{
					if(!StringUtil.isNullStr(coupon.getAttributeContent())){
						contentIdList = StringUtil.stringToLongArray(coupon.getAttributeContent());
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				// 检查优惠券列表
				if(contentIdList == null || contentIdList.size() <= 0){
					productList = this.productManager.getProductListByStatusAndIsSoldout(true, false);
				}else{
					if(coupon.getAttribute().equals(Coupon.COUPON_ATTRIBUTE_CATEGORY)){
						productList = this.productManager.getProductListByCategoryIdList(contentIdList, true);
					}else if(coupon.getAttribute().equals(Coupon.COUPON_ATTRIBUTE_PRODUCT)){
						productList = this.productManager.getByIdList(contentIdList);
					}
				}
				
				// 商品信息转换
				if(productList != null && productList.size() > 0){
					for(Product product : productList){
//						if(StringUtil.nullToBoolean(product.getIsGroupProduct()) && !StringUtil.nullToBoolean(coupon.getIsRechargeProductCoupon())) {
//							//组合商品不显示
//							continue;
//						}
						productIdList.add(StringUtil.null2Str(product.getProductId()));
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return productIdList;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productListByCouponId_'+#couponId")
	public void removeSession(Long categoryId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
