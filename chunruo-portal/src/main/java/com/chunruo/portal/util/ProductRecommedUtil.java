package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.scheduling.annotation.Async;

import com.chunruo.cache.portal.impl.HotSaleRecordListCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.HotSaleRecord;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;

/**
 * 商品推荐
 * @author chunruo
 *
 */
public class ProductRecommedUtil {
	
	public static Map<String, List<Product>> getProductRecommendList(Product product, UserInfo userInfo, int size) {
		Map<String, List<Product>>  productListMap = new HashMap<String, List<Product>> ();
		try{
			List<Long> sameKindProductIdList = new ArrayList<Long> ();
			List<Long> recommendProductIdList = new ArrayList<Long> ();
			Map<String, List<Long>> productIdListMap = ProductRecommedUtil.getProductRecommendList(product);
			if(productIdListMap != null && productIdListMap.size() > 0){
				sameKindProductIdList = productIdListMap.get("sameKindProductIdList");
				recommendProductIdList = productIdListMap.get("recommendProductIdList");
			}
			
			List<Product> sameKindProductList = new ArrayList<Product> ();
			List<Product> recommendProductList = new ArrayList<Product> ();
			if(recommendProductIdList != null && recommendProductIdList.size() > 0){
				// 同类热销
				List<Long> existProductIdList = new ArrayList<Long> ();
				if(sameKindProductIdList != null && sameKindProductIdList.size() > 0){
					for(Long productId : sameKindProductIdList){
						MsgModel<Product> msgModel = ProductUtil.getProductByUserLevel(productId, userInfo, true);
						if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
							Product xproduct = msgModel.getData();
							if(StringUtil.nullToBoolean(xproduct.getIsTest())
									|| StringUtil.nullToBoolean(xproduct.getIsTeamPackage())
									|| StringUtil.nullToBoolean(xproduct.getIsPackage())){
								continue;
							}
							
							sameKindProductList.add(xproduct);
							existProductIdList.add(xproduct.getProductId());
							if(sameKindProductList.size() >= size){
								break;
							}
						}
					}
				}
				
				// 为你推荐前热卖商品作为随机池
				List<Product> realRecommendProductList = new ArrayList<Product> ();
				for(Long productId : recommendProductIdList){
					if(!existProductIdList.contains(productId)){
						MsgModel<Product> msgModel = ProductUtil.getProductByUserLevel(productId, userInfo, true);
						if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
							Product xproduct = msgModel.getData();
							if(StringUtil.nullToBoolean(xproduct.getIsTest())
									|| StringUtil.nullToBoolean(xproduct.getIsTeamPackage())
									|| StringUtil.nullToBoolean(xproduct.getIsPackage())){
								continue;
							}
							
							realRecommendProductList.add(xproduct);
							if(realRecommendProductList.size() >= (size * 3)){
								break;
							}
						}
					}
				}
				
				// 同类热销不足补热卖随机商品
				if(sameKindProductList.size() < size && realRecommendProductList.size() > 0){
					try{
						int complementedSize = (size - sameKindProductList.size());
						List<Integer> indexList = ProductRecommedUtil.getIndexRandomList(complementedSize, 0, realRecommendProductList.size() - 1);
						if(indexList != null && indexList.size() > 0){
							List<Product> productList = new ArrayList<Product> ();
							for(int index : indexList){
								Product pecommendProduct = realRecommendProductList.get(index);
								sameKindProductList.add(pecommendProduct);
								productList.add(pecommendProduct);
							}
							realRecommendProductList.removeAll(productList);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
				// 为你推荐
				if(realRecommendProductList != null && realRecommendProductList.size() > 0){
					try{
						if(realRecommendProductList.size() <= size){
							recommendProductList.addAll(realRecommendProductList);
						}else{
							List<Integer> indexList = ProductRecommedUtil.getIndexRandomList(size, 0, realRecommendProductList.size() - 1);
							if(indexList != null && indexList.size() > 0){
								for(int index : indexList){
									recommendProductList.add(realRecommendProductList.get(index));
								}
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
				//同类热销的10个商品
				productListMap.put("sameKindWholesaleList", sameKindProductList);
				//为你推荐的10个商品
				productListMap.put("recommendWholesaleList", recommendProductList);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return productListMap;
	}

	/**
	 * 获取随机下标数字
	 * @param max
	 * @param min
	 * @param size
	 * @return
	 */
	public static List<Integer> getIndexRandomList(int size, int min, int max){
		List<Integer> indexList = new ArrayList<Integer> ();
		try{
			if(max - min <= size){
				for(int index = min; index <= max; index ++){
					indexList.add(index);
					if(indexList.size() > size){
				    	break;
				    }
				}
			}else{
				Random random = new Random();
				do{
				    int index = random.nextInt(max) % (max - min + 1) + min;
				    if(!indexList.contains(index)){
				    	indexList.add(index);
				    }
				}while(indexList.size() <= size);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return indexList;
	}
	
	/**
	 * 热卖商品汇总
	 * @param product
	 * @param userInfo
	 * @param size
	 * @return
	 */
	@Async
	public static Map<String, List<Long>> getProductRecommendList(Product product) {
		Map<String, List<Long>> recommendListMap = new HashMap<String, List<Long>> ();
		try{
			HotSaleRecordListCacheManager hotSaleRecordListCacheManager = Constants.ctx.getBean(HotSaleRecordListCacheManager.class);
			
			// 热卖商品
			List<Long> productIdList = new ArrayList<Long> ();
			Map<String, HotSaleRecord> hotSaleRecordMap = hotSaleRecordListCacheManager.getSession();
			if(hotSaleRecordMap != null && hotSaleRecordMap.size() > 0){
				List<Map.Entry<String, HotSaleRecord>> mappingList = new ArrayList<Map.Entry<String, HotSaleRecord>>(hotSaleRecordMap.entrySet());
				Collections.sort(mappingList, new Comparator<Map.Entry<String, HotSaleRecord>>() {
					public int compare(Map.Entry<String, HotSaleRecord> obj1, Map.Entry<String, HotSaleRecord> obj2) {
						HotSaleRecord record1 = obj1.getValue();
						HotSaleRecord record2 = obj2.getValue();
						int quantity1 = StringUtil.nullToInteger(record1.getQuantity());
						int quantity2 = StringUtil.nullToInteger(record2.getQuantity());
						return (quantity1 < quantity2) ? 1 : -1;
					}
				});
				
				List<Long> allProductIdList = new ArrayList<Long> ();
				List<Long> productIdOfCategoryIdList = new ArrayList<Long> ();
				List<Long> productIdOfBrandIdList = new ArrayList<Long> ();
				List<Long> productIdOfCategoryFidList = new ArrayList<Long> ();
				for (Map.Entry<String, HotSaleRecord> entry : mappingList) {
					HotSaleRecord hotSaleRecord = entry.getValue();
					if(!StringUtil.compareObject(product.getProductId(), hotSaleRecord.getProductId())){
						allProductIdList.add(hotSaleRecord.getProductId());
						if(product.getCategoryIdList().retainAll(hotSaleRecord.getCategoryIdList())){
							// 小分类
							productIdOfCategoryIdList.add(hotSaleRecord.getProductId());
						}else if(StringUtil.compareObject(product.getBrandId(), hotSaleRecord.getBrandId())){
							// 品牌
							productIdOfBrandIdList.add(hotSaleRecord.getProductId());
						}else if(product.getCategoryFidList().retainAll(hotSaleRecord.getCategoryFidList())){
							// 大分类
							productIdOfCategoryFidList.add(hotSaleRecord.getProductId());
						}
					}
				}
				
				productIdList.addAll(productIdOfCategoryIdList);
				productIdList.addAll(productIdOfBrandIdList);
				productIdList.addAll(productIdOfCategoryFidList);
				recommendListMap.put("sameKindProductIdList", productIdList);
				recommendListMap.put("recommendProductIdList", allProductIdList);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return recommendListMap;
	}
	
}
