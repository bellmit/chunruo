package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chunruo.cache.portal.impl.CollectionListByUserIdCacheManager;
import com.chunruo.cache.portal.impl.CollectionProfitListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductCollection;
import com.chunruo.core.model.ProductCollectionProfit;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.service.ProductCollectionManager;
import com.chunruo.core.service.ProductCollectionProfitManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;

/**
 * 商品代理工具类
 * @author chunruo
 */
public class ProductProxyUtil {

	/**
	 * 设置收藏商品
	 * @param product
	 * @return
	 */
	public static MsgModel<ProductCollection> collectionProduct(Long userId, Product product, boolean isProxy){
		MsgModel<ProductCollection> resultCheckModel = new MsgModel<ProductCollection> ();
		try{
			ProductCollectionManager productCollectionManager = Constants.ctx.getBean(ProductCollectionManager.class);
			CollectionListByUserIdCacheManager collectionListByUserIdCacheManager = Constants.ctx.getBean(CollectionListByUserIdCacheManager.class);
			CollectionProfitListByUserIdCacheManager collectionProfitListByUserIdCacheManager = Constants.ctx.getBean(CollectionProfitListByUserIdCacheManager.class);
            
            // 检查代理商品是否存在
			if(StringUtil.nullToBoolean(isProxy)){
				// 检查商品是否已代理
				MsgModel<ProductCollection> msgModel = ProductProxyUtil.getCollectionProductInfo(product, userId);
				if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
					resultCheckModel.setProductProfit(StringUtil.nullToInteger(msgModel.getProductProfit()));
					resultCheckModel.setData(msgModel.getData());
					resultCheckModel.setIsHaveProxy(true);
					resultCheckModel.setIsSucc(true);
					resultCheckModel.setMessage("请求成功");
					return resultCheckModel;
				}
			}else{
				// 取消收藏，检查商品是否未代理
				MsgModel<ProductCollection> msgModel = ProductProxyUtil.getCollectionProductInfo(product, userId);
				if(!StringUtil.nullToBoolean(msgModel.getIsSucc())){
					resultCheckModel.setIsSucc(true);
					resultCheckModel.setMessage("请求成功");
					return resultCheckModel;
				}
			}
			//收藏商品
			productCollectionManager.saveProductCollection(product, userId, isProxy);
			try {
				collectionListByUserIdCacheManager.removeSession(userId);
				collectionProfitListByUserIdCacheManager.removeSession(userId);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			resultCheckModel.setIsSucc(true);
			resultCheckModel.setMessage("请求成功");
			return resultCheckModel;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultCheckModel.setIsSucc(false);
		resultCheckModel.setMessage("请求失败");
		return resultCheckModel;
	}


	/**
	 * 查找店铺下是否已经代理的商品信息
	 * @param productId
	 * @param storeId
	 * @param isCheckProfit
	 * @return
	 */
	public static MsgModel<ProductCollection> getCollectionProductInfo(Product product, Long userId){
		MsgModel<ProductCollection> resultCheckModel = new MsgModel<ProductCollection>();
		try{
			if(product != null && product.getProductId() != null) {
				CollectionListByUserIdCacheManager collectionListByUserIdCacheManager = Constants.ctx.getBean(CollectionListByUserIdCacheManager.class);
				Map<String, ProductCollection> collectionProdcutIdMap =  collectionListByUserIdCacheManager.getSession(userId);
				if(collectionProdcutIdMap == null || collectionProdcutIdMap.size() <= 0){
					resultCheckModel.setIsSucc(false);
					resultCheckModel.setMessage("未代理商品");
					return resultCheckModel;
				}
                
				// 查询收藏商品
				String productId = StringUtil.null2Str(product.getProductId());
				if(collectionProdcutIdMap.containsKey(productId)){
					ProductCollection collection = collectionProdcutIdMap.get(productId);
					if(collection != null && collection.getCollectionId() != null) {
						Integer productProfit = 0;  //商品利润
						List<ProductCollectionProfit> collectionProfitList = collection.getCollectionProfitList();
						if(collectionProfitList != null && collectionProfitList.size() > 0) {
							//需要更新的数据
							List<ProductCollectionProfit> changeProfitList = new ArrayList<ProductCollectionProfit>();
							if(!StringUtil.nullToBoolean(product.getIsSpceProduct())) {
								//非规格商品
					    		ProductCollectionProfit productCollectionProfit = collectionProfitList.get(0);
								productProfit = productCollectionProfit.getProfit();
								product.setProductProfit(productProfit);
							}else {
								//规格商品
								Map<Long,ProductCollectionProfit>  profitMap = new HashMap<Long,ProductCollectionProfit>();
								
								//最新的利润规格信息（规格信息可能新增或删除）
								List<ProductCollectionProfit> newestProfitList = new ArrayList<ProductCollectionProfit>();
								for (ProductCollectionProfit collectionProfit : collectionProfitList) {
									 profitMap.put(StringUtil.nullToLong(collectionProfit.getProductSpecId()), collectionProfit);
								}
								
								List<ProductSpec> productSpecList = product.getProductSpecList();
								if(productSpecList != null && productSpecList.size() > 0) {
									Set<Double> priceWholesaleSet = new HashSet<Double>();
									for(ProductSpec productSpec : productSpecList) {
										ProductCollectionProfit colProfit = profitMap.get(StringUtil.nullToLong(productSpec.getProductSpecId()));
								         if(colProfit == null || colProfit.getProfitId() == null) {
								        	 colProfit = new ProductCollectionProfit();
								        	 colProfit.setProductId(StringUtil.nullToLong(product.getProductId()));
								        	 colProfit.setProductSpecId(StringUtil.nullToLong(productSpec.getProductSpecId()));
								        	 colProfit.setUserId(userId);
								        	 colProfit.setProfit(StringUtil.nullToInteger(productSpec.getProductProfit()));
								        	 colProfit.setCreateTime(DateUtil.getCurrentDate());
								        	 colProfit.setUpdateTime(colProfit.getCreateTime());
								        	 changeProfitList.add(colProfit);
								         }
							    		 priceWholesaleSet.add(StringUtil.nullToDoubleFormat(productSpec.getRealSellPrice()));
							    		 if(StringUtil.compareObject(StringUtil.nullToLong(product.getProductSpecId()), colProfit.getProductSpecId())) {
											productProfit = colProfit.getProfit();
										 }
							    		 productSpec.setProductProfit(colProfit.getProfit());
							    		 newestProfitList.add(colProfit);
									}
									
									//若价格相同，显示收藏最高利润(秒杀商品价格都是秒杀价)
									if(StringUtil.nullToInteger(priceWholesaleSet.size()) == 1) {
										Collections.sort(newestProfitList, new Comparator<ProductCollectionProfit>(){
											@Override
											public int compare(ProductCollectionProfit o1, ProductCollectionProfit o2) {
												 Double object1 = StringUtil.nullToDouble(o1.getProfit());
												 Double object2 = StringUtil.nullToDouble(o2.getProfit());
												 return object1.compareTo(object2);
											}
										 });
										productProfit = newestProfitList.get(0).getProfit();
									}
								}
							}
							
							try {
								if(changeProfitList != null && changeProfitList.size() > 0) {
									//更新商品收藏利润
									ProductCollectionProfitManager productCollectionProfitManager = Constants.ctx.getBean(ProductCollectionProfitManager.class);
									CollectionProfitListByUserIdCacheManager collectionProfitListByUserIdCacheManager = Constants.ctx.getBean(CollectionProfitListByUserIdCacheManager.class);
									productCollectionProfitManager.batchInsert(changeProfitList, changeProfitList.size());
									collectionListByUserIdCacheManager.removeSession(userId);
									collectionProfitListByUserIdCacheManager.removeSession(userId);
								}
								
							}catch(Exception e) {
								e.printStackTrace();
							}
						}
						resultCheckModel.setProductProfit(productProfit);
						resultCheckModel.setIsSucc(true);
						resultCheckModel.setData(collection);
						resultCheckModel.setMessage("商品已代理");
						return resultCheckModel;
					}
				}
				resultCheckModel.setIsSucc(false);
				resultCheckModel.setMessage("商品未代理");
				return resultCheckModel;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		resultCheckModel.setIsSucc(false);
		resultCheckModel.setMessage("商品未代理");
		return resultCheckModel;
	}
	
}
