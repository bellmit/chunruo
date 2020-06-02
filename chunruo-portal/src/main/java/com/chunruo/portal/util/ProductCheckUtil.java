package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.chunruo.cache.portal.impl.ProductPromotListCacheManager;
import com.chunruo.cache.portal.impl.UserCartListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.BuyPostType;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductGroup;
import com.chunruo.core.model.ProductPromot;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.UserCart;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.UserCartManager;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.vo.ProductGroupVo;

/**
 * 订单检查工具类
 * @author chunruo
 */
public class ProductCheckUtil {
	
	
	
	/**
	 * 检查购物车是否已加入商品
	 * @param product
	 * @param productSpecId
	 * @param userId
	 * @param storeId
	 * @return
	 */
	public static MsgModel<UserCart> checkExistUserCartByProduct(Product product, Long productSpecId, String groupProductInfo, UserInfo userInfo){
		MsgModel<UserCart> msgModel = new MsgModel<UserCart> ();
		try{
			UserCartManager userCartManager = Constants.ctx.getBean(UserCartManager.class);
			List<UserCart> userCartList = userCartManager.getUserCartByProductId(userInfo.getUserId(), product.getProductId());
			if(userCartList != null && userCartList.size() > 0){
				if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
					// 检查商品规格是否存在
					MsgModel<ProductSpec> xmsgModel = ProductUtil.checkExistProductSpecByProductSpecId(product, productSpecId);
					if(StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
						for(UserCart userCart : userCartList){
							// 匹配已加入购物车的规格商品
							if(StringUtil.nullToBoolean(userCart.getIsSpceProduct()) 
									&& StringUtil.compareObject(StringUtil.nullToLong(userCart.getProductSpecId()), productSpecId)){
								msgModel.setIsSucc(true);
								msgModel.setData(userCart);
								return msgModel;
							}
						}
					}
				}else if(StringUtil.nullToBoolean(product.getIsGroupProduct())) {
					//检查组合商品是否存在
					MsgModel<List<ProductGroupVo>> xmsgModel = ProductCheckUtil.checkExistGroupProductByGroupInfo(product, groupProductInfo, userInfo, false, 1);
				    if(StringUtil.nullToBoolean(xmsgModel.getIsSucc())) {
				    	for(UserCart userCart : userCartList) {
				    		// 组合商品排序比较
				    		List<Long> groupProductList = StringUtil.stringToLongArray(groupProductInfo);
				    		List<Long> dbGroupProductList = StringUtil.stringToLongArray(userCart.getGroupProductInfo());
				    		Collections.sort(groupProductList);
				    		Collections.sort(dbGroupProductList);
				    		
				    		// 检查组合商品是否已存在
							if(StringUtil.compareObject(StringUtil.longArray2String(groupProductList), StringUtil.longArray2String(dbGroupProductList))){
								msgModel.setIsSucc(true);
								msgModel.setData(userCart);
								return msgModel;
							}
						}
				    }
				}else{
					// 普通商品直接返回默认第一个
					for(UserCart userCart : userCartList){
						// 匹配已加入购物车的规格商品
						if(!StringUtil.nullToBoolean(userCart.getIsSpceProduct())){
							msgModel.setIsSucc(true);
							msgModel.setData(userCart);
							return msgModel;
						}
					}
				}
			}
		}catch(Exception e){
			e.getMessage();
		}
		
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	/**
	 * 检查组合商品拆单
	 * @param productList
	 * @return
	 */
	public static MsgModel<List<Product>> getProductGroupOrderSplitSingle(List<Product> productList, UserInfo userInfo){
		MsgModel<List<Product>> msgModel = new MsgModel<List<Product>> ();
		try{
			if(productList != null && productList.size() > 0){
				boolean isGroupProduct = false;
				List<Product> resultList = new ArrayList<Product> ();
				for(Product product : productList){
					if(!StringUtil.nullToBoolean(product.getIsGroupProduct())){
						resultList.add(product);
					}else{
						// 组合商品拆单
						MsgModel<Product> xmsgModel = ProductCheckUtil.checkGroupProductByUserLevel(product, product.getGroupProductInfo(), product.getPaymentBuyNumber(), true, userInfo);
						if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
							msgModel.setIsSucc(false);
							msgModel.setMessage(xmsgModel.getMessage());
							return msgModel;
						}
						
						isGroupProduct = true;
						boolean isMainGroupItem = false;		
						String groupUniqueBatch = StringUtil.getRandomUUID();
						List<Product> list = xmsgModel.getProductList();
						for(Product tmpProduct : list){
							//是否主退款组合商品
							if(!StringUtil.nullToBoolean(isMainGroupItem)){
								isMainGroupItem = true;
								tmpProduct.setIsMainGroupItem(isMainGroupItem);
								tmpProduct.setIsBanPurchase(StringUtil.nullToBoolean(product.getIsBanPurchase()));
							}
							
							tmpProduct.setIsGroupProduct(true);
							tmpProduct.setImage(product.getImage());
							tmpProduct.setGroupProductId(product.getProductId());
							tmpProduct.setGroupUniqueBatch(groupUniqueBatch);
							tmpProduct.setWareHouseId(product.getWareHouseId());
							tmpProduct.setTemplateId(product.getTemplateId());
							tmpProduct.setPaymentTemplateId(product.getTemplateId());
							tmpProduct.setIsFreePostage(StringUtil.nullToBoolean(product.getIsFreePostage()));
						}
						resultList.addAll(list);
					}
				}
				
				msgModel.setIsSucc(true);
				msgModel.setIsGroupProduct(isGroupProduct);
				msgModel.setData(resultList);
				return msgModel;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		msgModel.setMessage("组合商品拆单步骤处理失败");
		return msgModel;
	}
	
	/**
	 * 检查促销赠送商品列表
	 * @param productId
	 * @return
	 */
	public static MsgModel<List<ProductPromot>> getProductPromotListBy(Long productId){
		MsgModel<List<ProductPromot>> msgModel = new MsgModel<List<ProductPromot>> ();
		try{
			ProductPromotListCacheManager productPromotListCacheManager = Constants.ctx.getBean(ProductPromotListCacheManager.class);
			List<ProductPromot> list = productPromotListCacheManager.getSession();
			if(list != null && list.size() > 0){
				// 遍历所有的促销赠送商品列表
				List<ProductPromot> productPromotList = new ArrayList<ProductPromot> ();
				for(ProductPromot productPromot : list){
					//促销商品启用状态且非删除状态
					if(StringUtil.nullToBoolean(productPromot.getStatus()) && !StringUtil.nullToBoolean(productPromot.getIsDelete())){
						List<Long> productIdList = StringUtil.stringToLongArray(productPromot.getTarProductIds());
						if(productIdList != null && productIdList.add(productId)){
							productPromotList.add(productPromot);
						}
					}
				}
				
				// 找到有效的促销商品列表
				if(productPromotList != null && productPromotList.size() > 0){
					msgModel.setIsSucc(true);
					msgModel.setData(list);
					return msgModel;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	/**
	 * 订单确认
	 * @param postType
	 * @param productId
	 * @param number
	 * @param userCartIdList
	 * @param storeId
	 * @param userId
	 * @param isWeb 
	 * @return
	 */
	public static MsgModel<List<Product>> check(int postType, Long productId, Long productSpecId, String groupProductInfo, int number, String cartIds, UserInfo userInfo){
		MsgModel<List<Product>> resultCheckModel = new MsgModel<List<Product>> ();
		try{
			List<Product> productList = new ArrayList<Product> ();
			if(StringUtil.compareObject(BuyPostType.POST_BUY_QUICK_TYPE, postType)){
				//立即购买
				MsgModel<Product> productCheckModel = ProductCheckUtil.checkProduct(productId, productSpecId, groupProductInfo, number, userInfo);
				if(!StringUtil.nullToBoolean(productCheckModel.getIsSucc())){
					resultCheckModel.setIsSucc(productCheckModel.getIsSucc());
					resultCheckModel.setMessage(productCheckModel.getMessage());
					resultCheckModel.setObjectId(productCheckModel.getObjectId());
					return resultCheckModel;
				}
				
				Product product = productCheckModel.getData();
				product.setPaymentBuyNumber(number);
				productList.add(product);
			}else if(StringUtil.compareObject(BuyPostType.POST_BUY_CART_TYPE, postType)){
				//购物车结算
				List<Long> userCartIdList = StringUtil.stringToLongArray(cartIds);
				if(userCartIdList == null || userCartIdList.size() <= 0){
					resultCheckModel.setIsSucc(false);
					resultCheckModel.setMessage("购物车信息不能为空");
					return resultCheckModel;
				}
				
				MsgModel<List<Product>> msgModel = ProductCheckUtil.checkCartProduct(userCartIdList, userInfo, true);
				if(!StringUtil.nullToBoolean(msgModel.getIsSucc())){
					resultCheckModel.setIsSucc(msgModel.getIsSucc());
					resultCheckModel.setMessage(msgModel.getMessage());
					resultCheckModel.setObjectId(msgModel.getObjectId());
					return resultCheckModel;
				}
				productList = msgModel.getData();
			}
			
			// 有效商品信息
			if(productList != null && productList.size() > 0){
				resultCheckModel.setIsSucc(true);
				resultCheckModel.setData(productList);
				return resultCheckModel;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultCheckModel.setIsSucc(false);
		resultCheckModel.setMessage("服务器异常,请稍后访问");
		return resultCheckModel;
	}
	
	/**
	 * 购物车订单确认校验
	 * @param userCartIdList
	 * @param userId
	 * @param storeId
	 * @param  
	 * @return
	 */
	public static MsgModel<List<Product>> checkCartProduct(List<Long> userCartIdList, UserInfo userInfo, Boolean isCheckCrossLimit){
		MsgModel<List<Product>> resultCheckModel = new MsgModel<List<Product>> ();
		try{
			// 获取当前用户所有的购物车信息
			UserCartListByUserIdCacheManager userCartListByUserIdCacheManager = Constants.ctx.getBean(UserCartListByUserIdCacheManager.class);
			Map<String, UserCart> userCartMap = userCartListByUserIdCacheManager.getSession(userInfo.getUserId());
			if(userCartMap == null || userCartMap.size() <= 0){
				//购物车为空
				resultCheckModel.setIsSucc(false);
				resultCheckModel.setMessage("购物车为空");
				return resultCheckModel;
			}
			
			Map<Long, UserCart> storeCartMap = new HashMap<Long, UserCart>();
			for(Map.Entry<String, UserCart> entry : userCartMap.entrySet()){
				UserCart userCart = entry.getValue();
				storeCartMap.put(userCart.getCartId(), userCart);
			}
			
			// 检查购物车信息是否有效
			List<UserCart> userCartList = new ArrayList<UserCart> ();
			for(Long cartId : userCartIdList){
				if(!storeCartMap.containsKey(cartId)){
					resultCheckModel.setIsSucc(false);
					resultCheckModel.setObjectId(cartId);
					resultCheckModel.setMessage("购物车信息异常,请刷新");
					return resultCheckModel;
				}
				userCartList.add(userCartMap.get(StringUtil.null2Str(cartId)));
			}
	
			// 购物车订单确认校验
			return ProductCheckUtil.checkProductList(userCartList, userInfo, isCheckCrossLimit);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		resultCheckModel.setIsSucc(false);
		resultCheckModel.setMessage("商品已售罄或不存在");
		return resultCheckModel;
	}
	
	/**
	 * 购物车订单确认校验
	 * @param userCartIdList
	 * @param userId
	 * @param storeId
	 * @param  
	 * @return
	 */
	public static MsgModel<List<Product>> checkProductList(List<UserCart> userCartList, UserInfo userInfo, Boolean isCheckCrossLimit){
		MsgModel<List<Product>> resultCheckModel = new MsgModel<List<Product>> ();
		try{
			//跨境商品数量
			Integer sumProductNumbers = 0;
			//跨境商品金额
			Double sumAmount = 0.0D;
			
			// 遍历所有请求购物车记录
			List<Product> productList = new ArrayList<Product> ();
			Map<Long,Integer> limitNumberMap = new HashMap<Long,Integer>();
			for(UserCart userCart : userCartList){
				Integer quantity = StringUtil.nullToInteger(userCart.getQuantity());
				
				// 单个商品验证是否有效
				MsgModel<Product> productCheckModel = ProductCheckUtil.checkProduct(userCart.getProductId(), userCart.getProductSpecId(), userCart.getGroupProductInfo(), quantity, userInfo);
				if(!StringUtil.nullToBoolean(productCheckModel.getIsSucc())){
					resultCheckModel.setIsSucc(false);
					resultCheckModel.setObjectId(userCart.getCartId());
					resultCheckModel.setMessage(productCheckModel.getMessage());
					return resultCheckModel;
				}
				
				// 单个商品信息
				Product product = productCheckModel.getData();
				if(!limitNumberMap.containsKey(product.getProductId())) {
                	limitNumberMap.put(product.getProductId(), quantity);
				}else {
					limitNumberMap.put(product.getProductId(), limitNumberMap.get(product.getProductId()) + quantity);
				}
		
				Integer totalNumber = StringUtil.nullToInteger(limitNumberMap.get(product.getProductId()));
				//检查是否设置单次购买最大数量限制
				Integer maxLimitNumber = StringUtil.nullToInteger(product.getMaxLimitNumber());
	            if(maxLimitNumber > 0 && totalNumber > maxLimitNumber) {
	            	resultCheckModel.setIsSucc(false);
	            	resultCheckModel.setMessage(String.format("\"%s\"数量过多，请分开结算", StringUtil.null2Str(product.getName())));
					return resultCheckModel;
	            }
				
				//检查购物车用户等级限购
				if(StringUtil.nullToBoolean(product.getIsLevelLimitProduct())
						&& StringUtil.nullToInteger(product.getLevelLimitNumber()) >= 0
						&& StringUtil.nullToInteger(product.getLevelLimitNumber()) < totalNumber){
					resultCheckModel.setIsSucc(false);
					resultCheckModel.setMessage(String.format("您当前最多可购买\"%s\"%s件", product.getName(),StringUtil.nullToInteger(product.getLevelLimitNumber())));
					return resultCheckModel;
				}
				
				//检查购物车用户限购
				Map<Long, Integer> productIdMap = PurchaseLimitUtil.USER_LIMIT_THREAD_LOCAL.get();
                if(productIdMap != null 
                		&& productIdMap.size() > 0
                		&& productIdMap.containsKey(StringUtil.nullToLong(product.getProductId()))) {
                	//剩余可购数量
                	Integer remainNumber = StringUtil.nullToInteger(productIdMap.get(StringUtil.nullToLong(product.getProductId())));
                	System.out.println(String.format("2==>[商品Id:%s;剩余可购数量:%s;购物车数量:%s]", product.getProductId(), remainNumber, totalNumber));
                	if(totalNumber > remainNumber) {
                		resultCheckModel.setIsSucc(false);
                		resultCheckModel.setMessage(String.format("您当前最多可购买商品\"%s\"%s件", product.getName(), remainNumber));
        				return resultCheckModel;
                	}
                }
				product.setPaymentBuyNumber(quantity);
				
				
				// 跨境商品累计限制
				Integer productType = StringUtil.nullToInteger(product.getProductType());
				if(Constants.PRODUCT_TYPE_CROSS_LIST.contains(productType)){
					//跨境商品订单金额累计、商品数量累计
					if(product.getIsGroupProduct()) {
						//组合商品
						sumProductNumbers += (StringUtil.nullToInteger(product.getGroupSingleTotalNumber()) * quantity);
					}else {
						sumProductNumbers += quantity;
					}
					
					// 计数订单总金额
					Double productAmount = DoubleUtil.mul(product.getPaymentPrice(), StringUtil.nullToDouble(quantity));
					// 计算商品税费
					Double productTaxAmount = DoubleUtil.mul(product.getTax(),StringUtil.nullToDouble(quantity));
					sumAmount = DoubleUtil.add(DoubleUtil.add(sumAmount, productAmount), productTaxAmount);
				}
				
				product.setPaymentBuyNumber(quantity);
				productList.add(product);
			}
			
			// 商品信息不存在或已下架
			if(productList == null || productList.size() <= 0){
				resultCheckModel.setIsSucc(false);
				resultCheckModel.setMessage("商品已售罄或不存在");
				return resultCheckModel;
			}
			
			//检查所选商品类型
			MsgModel<Map<Long, List<Product>>> msgModel = ProductCheckUtil.checkProductType(productList);
			if(!StringUtil.nullToBoolean(msgModel.getIsSucc())) {
				resultCheckModel.setIsSucc(false);
				resultCheckModel.setMessage(msgModel.getMessage());
				return resultCheckModel;
			}
			Integer totalProductType = StringUtil.nullToInteger(msgModel.getProductType());
			
			// 验证跨境商品订单金额累计、商品数量累计
			if(StringUtil.nullToBoolean(isCheckCrossLimit)) {
				//检查单笔订单金额是否超出限制
				MsgModel<Void> csgModel = ProductCheckUtil.checkMaxProductAmount(sumAmount, totalProductType);
			    if(!StringUtil.nullToBoolean(csgModel.getIsSucc())) {
			    	resultCheckModel.setIsSucc(false);
			    	resultCheckModel.setMessage(StringUtil.null2Str(csgModel.getMessage()));
					return resultCheckModel;
			    }
			}
			
			resultCheckModel.setIsSucc(true);
			resultCheckModel.setData(productList);
			return resultCheckModel;
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			//手动清楚当前线程副本数据（tomcat线程池中由于线程复用，线程本地数据不会马上清除）
			PurchaseLimitUtil.USER_LIMIT_THREAD_LOCAL.remove();
		}
		
		resultCheckModel.setIsSucc(false);
		resultCheckModel.setMessage("商品已售罄或不存在");
		return resultCheckModel;
	}
	
	/**
	 * 检查商品是否有效
	 * @param productId
	 * @param storeId
	 * @param number
	 * @return
	 */
	public static MsgModel<Product> checkProduct(Long productId, Long productSpecId, String groupProductInfo, int number, UserInfo userInfo){
		return ProductCheckUtil.checkProduct(productId, productSpecId, groupProductInfo, number, userInfo, false);
	}
	
	/**
	 * 检查商品是否有效
	 * @param productId
	 * @param storeId
	 * @param number
	 * @return
	 */
	public static MsgModel<Product> checkProduct(Long productId, Long productSpecId, String groupProductInfo, int number, UserInfo userInfo, Boolean isGroupChilderProduct){
		MsgModel<Product> msgModel = new MsgModel<Product> ();
		try{
			// 检查商品数量是否正确
			if(number <= 0){
				msgModel.setIsSucc(false);
				msgModel.setMessage("购买商品数量错误");
				return msgModel;
			}
			
			// 检查市场商品是否有效
			MsgModel<Product> xsgModel = ProductUtil.getProductByUserLevel(productId, productSpecId, userInfo, true);
			if(!StringUtil.nullToBoolean(xsgModel.getIsSucc())){
				//商品不存在
				msgModel.setIsSucc(false);
				msgModel.setMessage(xsgModel.getMessage());
				return msgModel;
			}
			
			// 商品信息
			Product product = xsgModel.getData();
			
			// 跨境限制消费限制
		    Integer productType = StringUtil.nullToInteger(product.getProductType());
			
			// 检查商品是否有库存
			if(number > StringUtil.nullToInteger(product.getPaymentStockNumber())){
				msgModel.setIsSucc(false);
				msgModel.setMessage(String.format("\"%s\"商品库存不足", product.getName()));
				if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
					msgModel.setMessage(String.format("\"%s\",\"%s\"商品库存不足", product.getName(), product.getProductTags()));
				}
				return msgModel;
			}
			
			// 检查是否组合商品的子商品(直接返回忽略以下检查)
			if(StringUtil.nullToBoolean(isGroupChilderProduct)){
				// 购买商品的真实价格
				msgModel.setData(product);
				msgModel.setIsSucc(true);
				return msgModel;
			}
			
			// 检查组合商品
			if(StringUtil.nullToBoolean(product.getIsGroupProduct())){
				product.setGroupProductInfo(groupProductInfo);
				MsgModel<Product> xmsgModel = ProductCheckUtil.checkGroupProductByUserLevel(product, product.getGroupProductInfo(), number, true, userInfo);
				if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
					msgModel.setIsSucc(false);
					msgModel.setMessage(xmsgModel.getMessage());
					return msgModel;
				}
				
				// 组合商品信息已计算完成
				product = xmsgModel.getData();
			}
			
			//检查是否设置单次购买最大数量限制
			Integer maxLimitNumber = StringUtil.nullToInteger(product.getMaxLimitNumber());
            if(maxLimitNumber > 0 && number > maxLimitNumber) {
            	msgModel.setIsSucc(false);
				msgModel.setMessage(String.format("\"%s\"数量过多，请分开结算", StringUtil.null2Str(product.getName())));
				return msgModel;
            }
		    
		    //商品总金额（含税费）
	    	Double paymentTax = StringUtil.nullToDouble(product.getTax());
	    	Double paymentPrice = StringUtil.nullToDouble(product.getPaymentPrice());
			Double totalProductAmount = DoubleUtil.mul(DoubleUtil.add(paymentPrice, paymentTax), StringUtil.nullToDouble(number));
		  
			//检查单笔订单金额是否超出限制
			MsgModel<Void> csgModel = ProductCheckUtil.checkMaxProductAmount(totalProductAmount, productType);
		    if(!StringUtil.nullToBoolean(csgModel.getIsSucc())) {
		    	msgModel.setIsSucc(false);
				msgModel.setMessage(StringUtil.null2Str(csgModel.getMessage()));
				return msgModel;
		    }
		    
		    //检查用户限购
		    MsgModel<Integer> userLimitModel = PurchaseLimitUtil.checkUserLimitByProduct(product, userInfo, number);
			if(StringUtil.nullToBoolean(userLimitModel.getIsSucc())) {
				msgModel.setIsSucc(false);
				msgModel.setMessage(userLimitModel.getMessage());
				return msgModel;
			}else if(StringUtil.nullToInteger(userLimitModel.getData()) > 0) {
				//标记此商品为达到最大限购数量商品,禁止48小时内再次购买
				if(StringUtil.nullToBoolean(userLimitModel.getIsExpire())) {
					product.setIsBanPurchase(true);
				}
				
				Map<Long, Integer> productIdMap = PurchaseLimitUtil.USER_LIMIT_THREAD_LOCAL.get();
				if(productIdMap == null || productIdMap.isEmpty()) {
	            	System.out.println(String.format("1==>[商品Id:%s;剩余可购数量:%s;当前商品数量:%s]", product.getProductId(),userLimitModel.getData(),number));
	            	productIdMap = new HashMap<Long,Integer>();
	            	productIdMap.put(StringUtil.nullToLong(product.getProductId()), StringUtil.nullToInteger(userLimitModel.getData()));
					PurchaseLimitUtil.USER_LIMIT_THREAD_LOCAL.set(productIdMap);
				}
			}
			
			// 购买商品的真实价格
			msgModel.setData(product);
			msgModel.setIsSucc(true);
			return msgModel;
		}catch(Exception e){
			e.printStackTrace();
			
			msgModel.setIsSucc(false);
			msgModel.setMessage("服务器异常");
			return msgModel;
		}
	}
	
	/**
	 * 根据仓库拆单
	 * @param productList
	 * @return
	 */
	public static MsgModel<Map<Long, List<Product>>> getSplitWarehouseIdProductListMap(List<Product> productList){
		MsgModel<Map<Long, List<Product>>> msgModel = new  MsgModel<Map<Long, List<Product>>>();
		try {
			Map<Long, List<Product>> productListMap = new HashMap<Long, List<Product>> ();
			Set<Long> wareHouseTemplateIdList = new HashSet<Long>();
			if(productList != null && productList.size() > 0){
				for(Product product : productList){
					Long wareHouseId = product.getWareHouseId();
					if(productListMap.containsKey(wareHouseId)){
						productListMap.get(wareHouseId).add(product);
					}else{
						List<Product> list = new ArrayList<Product> ();
						list.add(product);
						productListMap.put(wareHouseId, list);
					}
					wareHouseTemplateIdList.add(StringUtil.nullToLong(StringUtil.nullToLong(product.getWareHouseTemplateId())));
				}
			}
			
			if(StringUtil.nullToInteger(wareHouseTemplateIdList.size()) != 1) {
				msgModel.setIsSucc(false);
				msgModel.setMessage("当前所购商品所属多个仓库模板，请分开下单");
				return msgModel;
			}
			
			msgModel.setIsSucc(true);
			msgModel.setData(productListMap);
			return msgModel;
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("仓库拆单错误");
		return msgModel;
	}
	
	/**
	 * 检查组合商品是否有效
	 * @param product
	 * @param groupProductInfo
	 * @return
	 */
	public static MsgModel<List<ProductGroupVo>> checkExistGroupProductByGroupInfo(Product product, String groupProductInfo, UserInfo userInfo, boolean isCheckQuantity, int quantity){
		MsgModel<List<ProductGroupVo>> msgModel = new MsgModel<List<ProductGroupVo>> ();
		try{
			MsgModel<Product> xmsgModel = ProductUtil.getProductByUserLevel(product.getProductId(), userInfo, true);
			if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
				msgModel.setIsSucc(false);
				msgModel.setMessage(xmsgModel.getMessage());
				return msgModel;
			}
			
			// 高级等级用户
			List<Integer> userLevelWholesale = new ArrayList<Integer> ();
			userLevelWholesale.add(UserLevel.USER_LEVEL_DEALER);	// 经销商
			userLevelWholesale.add(UserLevel.USER_LEVEL_AGENT);		// 平台总代
			userLevelWholesale.add(UserLevel.USER_LEVEL_V2);		// v2
			userLevelWholesale.add(UserLevel.USER_LEVEL_V3);		// v3
			
			Product xproduct = xmsgModel.getData();
			if(StringUtil.nullToBoolean(xproduct.getIsGroupProduct())){
				List<Long> groupIdList = StringUtil.stringToLongArray(groupProductInfo);
				Map<Long, List<ProductGroup>> productGroupMapList = xproduct.getProductGroupListMap();
				if(productGroupMapList != null && productGroupMapList.size() > 0){
					List<ProductGroupVo> realProductGroupList = new ArrayList<ProductGroupVo> ();
					for(List<ProductGroup> groupList : productGroupMapList.values()){
						if(groupList == null || groupList.size() <= 0){
							msgModel.setIsSucc(false);
							msgModel.setMessage(String.format("\"%s\"组合商品已售罄", product.getName()));
							return msgModel;
						}
						
						Map<Long, ProductGroup> productGroupMap = new HashMap<Long, ProductGroup> ();
						for(ProductGroup productGroup : groupList){
							productGroupMap.put(productGroup.getGroupId(), productGroup);
						}
						
						if(groupIdList != null && groupIdList.size() > 0){
							for(Long groupId : groupIdList){
								if(productGroupMap.containsKey(groupId)){
									ProductGroup productGroup = productGroupMap.get(groupId);
									// 组合商品存在groupId
									ProductGroupVo productGroupVo = new ProductGroupVo ();
									productGroupVo.setGroupId(groupId);
									productGroupVo.setProductId(productGroup.getProductId());
									productGroupVo.setProductSpecId(productGroup.getProductSpecId());
									productGroupVo.setSaleTimes(productGroup.getSaleTimes());
									// 需要检查购买份数
									if(isCheckQuantity){
										productGroupVo.setQuantity(productGroup.getSaleTimes() * quantity);
										Double paymentPrice = productGroup.getGroupPriceRecommend();
										if (userLevelWholesale.contains(userInfo.getLevel())) {
											paymentPrice = productGroup.getGroupPriceWholesale();
										}
										productGroupVo.setPaymentPrice(paymentPrice);
									}
									
									realProductGroupList.add(productGroupVo);
									groupIdList.remove(groupId);
									break;
								}
							}
						}
					}
					
					// 匹配真实的groupId大小和组合商品的子商品数量一致
					if(realProductGroupList != null
							&& realProductGroupList.size() > 0
							&& StringUtil.compareObject(realProductGroupList.size(), productGroupMapList.size())){
						msgModel.setIsSucc(true);
						msgModel.setData(realProductGroupList);
						return msgModel;
					}
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		msgModel.setMessage(String.format("\"%s\"组合商品已售罄", product.getName()));
		return msgModel;
	}
	
	/**
	 * 组合商品所有的信息
	 * @param product
	 * @return
	 */
	public static MsgModel<List<Product>> getGroupProductListByUserInfo(Product groupProduct, UserInfo userInfo){
		MsgModel<List<Product>> msgModel = new MsgModel<List<Product>>();
		try {
			// 非组合商品
			if(!StringUtil.nullToBoolean(groupProduct.getIsGroupProduct())) {
				msgModel.setIsSucc(false);
				msgModel.setMessage(String.format("\"%s\"组合商品信息错误", groupProduct.getName()));
				return msgModel;
			}
			
			Map<Long, List<ProductGroup>> productGroupListMap = groupProduct.getProductGroupListMap();
			if (productGroupListMap == null || productGroupListMap.size() <= 0) {
				// 非组合商品
				msgModel.setIsSucc(false);
				msgModel.setMessage(String.format("\"%s\"组合商品信息错误", groupProduct.getName()));
				return msgModel;
			}
			
			List<Product> productList = new ArrayList<Product>();
			for(Entry<Long, List<ProductGroup>> entry : productGroupListMap.entrySet()){
				List<ProductGroup> productGroupList = entry.getValue();
				if(productGroupList == null || productGroupList.size() <= 0){
					// 单个商品已下架(组合商品下架)
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"组合商品售罄", groupProduct.getName()));
					return msgModel;
				}
				
				// 检查商品详情是否有效
				MsgModel<Product> xmsgModel = ProductUtil.getProductByProductId(entry.getKey(), true);
				if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
					// 单个商品已下架(组合商品下架)
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"组合商品售罄", groupProduct.getName()));
					return msgModel;
				}
				
				// 检查组合商品的状态
				Product product = xmsgModel.getData();
				if (StringUtil.nullToBoolean(product.getIsSpceProduct())) {
					Map<Long, ProductSpec> productSpecMap = new HashMap<Long, ProductSpec> ();
					for(ProductSpec productSpec : product.getProductSpecList()){
						// 设置默认规格信息
						productSpec.setPaymentStockNumber(0);
						productSpec.setIsPaymentSoldout(true);
						productSpecMap.put(productSpec.getProductSpecId(), productSpec);
					}
					
					// 默认设置0库存
					product.setPaymentStockNumber(0);
					product.setIsPaymentSoldout(true);
					
					int paymentStockNumber = 0;
					boolean isPaymentSoldout = true;
					List<Double> defualtPriceList = new ArrayList<Double> ();
					for(ProductGroup productGroup : productGroupList){
						if(productSpecMap.containsKey(productGroup.getProductSpecId())){
							// 规格商品
							MsgModel<ProductSpec> ssgModel = ProductUtil.checkExistProductSpecByProductSpecId(product, productGroup.getProductSpecId());
							if(!StringUtil.nullToBoolean(ssgModel.getIsSucc())) {
								msgModel.setIsSucc(false);
								msgModel.setMessage(ssgModel.getMessage());
								return msgModel;
							}
							
							// 根据用户等级获取价格
							MsgModel<Double> cmsgModel = ProductUtil.getPaymentPriceByUserInfo(groupProduct,productGroup.getGroupPriceWholesale(), productGroup.getGroupPriceRecommend(), userInfo,true);
							if(!StringUtil.nullToBoolean(cmsgModel.getIsSucc())){
								msgModel.setIsSucc(false);
								msgModel.setMessage(cmsgModel.getMessage());
								return msgModel;
							}
							
							ProductSpec productSpec = productSpecMap.get(productGroup.getProductSpecId());
							productSpec.setGroupId(productGroup.getGroupId());
							productSpec.setPaymentPrice(StringUtil.nullToDouble(cmsgModel.getData()));
							productSpec.setPriceCost(StringUtil.nullToDouble(productGroup.getGroupPriceCost()));
							productSpec.setPriceWholesale(StringUtil.nullToDouble(productGroup.getGroupPriceWholesale()));
							productSpec.setPriceRecommend(StringUtil.nullToDouble(productGroup.getGroupPriceRecommend()));
							productSpec.setPaymentStockNumber(StringUtil.nullToInteger(productGroup.getPaymentStockNumber()));
							productSpec.setIsPaymentSoldout(StringUtil.nullToBoolean(productGroup.getIsPaymentSoldout()));
							product.setSaleTimes(StringUtil.nullToInteger(productGroup.getSaleTimes()));
						
							// 可销售规格商品
							if(!StringUtil.nullToBoolean(productGroup.getIsPaymentSoldout())){
								isPaymentSoldout = false;
								int stockNumber = StringUtil.nullToInteger(product.getPaymentStockNumber());
								paymentStockNumber = stockNumber + StringUtil.nullToInteger(productGroup.getPaymentStockNumber());
								
								// 组合商品所有价格的集合
								defualtPriceList.add(StringUtil.nullToDouble(cmsgModel.getData()));
							}
						}
					}
					
					// 默认设置0库存
					product.setPaymentStockNumber(paymentStockNumber);
					product.setIsPaymentSoldout(isPaymentSoldout);
					
					// 组合商品的最低规格商品的价格
					if(defualtPriceList != null && defualtPriceList.size() > 0){
						Collections.sort(defualtPriceList);
						product.setPaymentPrice(StringUtil.nullToDouble(defualtPriceList.get(0)));
					}
				}else {
					ProductGroup productGroup = productGroupList.get(0);
					
					// 默认设置0库存
					product.setPaymentStockNumber(0);
					product.setIsPaymentSoldout(true);
					
					// 根据用户等级获取价格
					MsgModel<Double> cmsgModel = ProductUtil.getPaymentPriceByUserInfo(groupProduct,productGroup.getGroupPriceWholesale(), productGroup.getGroupPriceRecommend(), userInfo,true);
					if(!StringUtil.nullToBoolean(cmsgModel.getIsSucc())){
						msgModel.setIsSucc(false);
						msgModel.setMessage(cmsgModel.getMessage());
						return msgModel;
					}
					
					product.setGroupId(productGroup.getGroupId());
					product.setPaymentPrice(StringUtil.nullToDouble(cmsgModel.getData()));
					product.setPriceCost(StringUtil.nullToDouble(productGroup.getGroupPriceCost()));
					product.setPriceRecommend(StringUtil.nullToDouble(productGroup.getGroupPriceRecommend()));
					product.setIsPaymentSoldout(StringUtil.nullToBoolean(productGroup.getIsPaymentSoldout()));
					product.setPaymentStockNumber(StringUtil.nullToInteger(productGroup.getPaymentStockNumber()));
					product.setSaleTimes(StringUtil.nullToInteger(productGroup.getSaleTimes()));
				}
				productList.add(product);
			}
			
			msgModel.setIsSucc(true);
			msgModel.setData(productList);
			return msgModel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		msgModel.setMessage("组合商品解析错误");
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	/**
	 * 检查组合商
	 * @param product
	 * @return
	 */
	public static MsgModel<Product> checkGroupProductByUserLevel(Product groupProduct, String groupProductInfo, Integer quantity, boolean isMustSell, UserInfo userInfo){
		MsgModel<Product> msgModel = new MsgModel<Product> ();
		try{
			//检查是否为组合商品
			if(StringUtil.nullToBoolean(groupProduct.getIsGroupProduct())) {
				StringBuffer strBuffer = new StringBuffer();
				Integer groupSingleTotalNumber = 0;
				Double totalPaymentPrice = 0.0D;
				Double totalPaymentTax = 0.0D;
				Double totalWeight = 0D;
				Double totalPriceWholesale = 0.0D;
				
				// 购买的是组合商品
				List<Long> groupIdList = StringUtil.stringToLongArray(groupProductInfo);
				if(groupIdList == null || groupIdList.size() <= 0){
					msgModel.setIsSucc(false);
					msgModel.setMessage("组合商品信息错误");
					return msgModel;
				}
				
				// 组合商品的原始商品内容
				Map<Long, List<ProductGroup>> productGroupMapList = groupProduct.getProductGroupListMap();
				if(productGroupMapList == null || productGroupMapList.size() <= 0){
					msgModel.setIsSucc(false);
					msgModel.setMessage("组合商品原始信息错误");
					return msgModel;
				}
				
				// 组合原始商品内容规整
				Map<Long, ProductGroup> productGroupMap = new HashMap<Long, ProductGroup> ();
				for(List<ProductGroup> groupList : productGroupMapList.values()){
					if(groupList != null && groupList.size() > 0){
						for(ProductGroup productGroup : groupList){
							productGroupMap.put(productGroup.getGroupId(), productGroup);
						}
					}
				}
				
				// 检查每个商品包含唯一一件商品
				Set<Long> prodctIdSet = new HashSet<Long> ();
				for(Long groupId : groupIdList){
					if(productGroupMap.containsKey(groupId)){
						ProductGroup productGroup = productGroupMap.get(groupId);
						if(prodctIdSet.contains(productGroup.getProductId())){
							msgModel.setIsSucc(false);
							msgModel.setMessage("单个商品重复选择");
							return msgModel;
						}
						prodctIdSet.add(productGroup.getProductId());
					}else{
						msgModel.setIsSucc(false);
						msgModel.setMessage("组合商品已下架或不存在");
						return msgModel;
					}
				}
				
				// 检查所有的商品是否都已选择
				for(Long productId : productGroupMapList.keySet()){
					if(!prodctIdSet.contains(productId)){
						String errorMsg = "请正确选择商品";
						Product product = ProductUtil.getProduct(productId);
						if(product != null && product.getProductId() != null){
							errorMsg = String.format("\"%s\"请正确选择商品", StringUtil.null2Str(product.getName()));
						}
						
						msgModel.setIsSucc(false);
						msgModel.setMessage(errorMsg);
						return msgModel;
					}
				}
				
				Boolean isFreeTax = StringUtil.nullToBoolean(groupProduct.getIsFreeTax());
			    Integer productType = ProductUtil.getProductType(groupProduct.getWareHouseId());
					
				// 通过组合商品统计商品价格
				List<Product> productList = new ArrayList<Product> ();
				for(Long groupId : groupIdList){
					// 检查组合商品原始是否存在
					ProductGroup productGroup = productGroupMap.get(groupId);
					Product product = ProductUtil.getProduct(productGroup.getProductId());
					if(product == null || product.getProductId() == null){
						msgModel.setIsSucc(false);
						msgModel.setMessage("组合商品原始不存在错误");
						return msgModel;
					}
					
					// 根据用户等级获取价格
					MsgModel<Double> cmsgModel = ProductUtil.getPaymentPriceByUserInfo(groupProduct,productGroup.getGroupPriceWholesale(), productGroup.getGroupPriceRecommend(), userInfo,false);
					if(!StringUtil.nullToBoolean(cmsgModel.getIsSucc())){
						msgModel.setIsSucc(false);
						msgModel.setMessage(cmsgModel.getMessage());
						return msgModel;
					}
					
					//组合商品的推荐价
					Double paymentPrice = StringUtil.nullToDoubleFormat(cmsgModel.getData());
					// 跨境限制消费税
					Double paymentTax = 0.0D;
					MsgModel<Double> tsgModel = ProductUtil.getProductTax(paymentPrice, productType, isFreeTax);
		            if(StringUtil.nullToBoolean(tsgModel.getIsSucc())) {
		            	paymentTax = tsgModel.getData();
		            }
					
				    // 金额累计
					totalPaymentPrice = DoubleUtil.add(totalPaymentPrice, paymentPrice); 
					totalPaymentTax = DoubleUtil.add(totalPaymentTax, paymentTax);
					totalPriceWholesale = DoubleUtil.add(totalPriceWholesale, StringUtil.nullToDouble(productGroup.getGroupPriceWholesale()));
					groupSingleTotalNumber += StringUtil.nullToInteger(productGroup.getSaleTimes());
					// 总商品数量(购买商品数量*单个组合商品的倍数)
					int totalNumber = StringUtil.nullToInteger(productGroup.getSaleTimes()) * quantity;
					
					// 组合商品标签信息
					if (StringUtil.nullToBoolean(product.getIsSpceProduct())) {
						// 规格商品
						MsgModel<ProductSpec> xmsgModel = ProductUtil.checkExistProductSpecByProductSpecId(product, productGroup.getProductSpecId());
						if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())) {
							msgModel.setIsSucc(false);
							msgModel.setMessage(xmsgModel.getMessage());
							return msgModel;
						}
						
						ProductSpec productSpec = xmsgModel.getData();
						// 累计商品总重量
						totalWeight += DoubleUtil.mul(StringUtil.nullToDouble(productSpec.getWeigth()), StringUtil.nullToDouble(productGroup.getSaleTimes()));
						strBuffer.append(String.format("%s*%s+", productSpec.getProductTags(), productGroup.getSaleTimes()));
						
						// 单个商品拆单(立即购买)
						if(StringUtil.nullToBoolean(isMustSell)){
							// 目的只检查单个组合子商品是否有库存
							MsgModel<Product> buyMsgModel = ProductCheckUtil.checkProduct(productGroup.getProductId(), productGroup.getProductSpecId(), null, totalNumber, userInfo, true);
							if(!StringUtil.nullToBoolean(buyMsgModel.getIsSucc())){
								msgModel.setIsSucc(false);
								msgModel.setMessage(buyMsgModel.getMessage());
								return msgModel;
							}
							
							Product result = buyMsgModel.getData();
							result.setPaymentPrice(paymentPrice);
							result.setTax(paymentTax);
							result.setPaymentBuyNumber(quantity);
							result.setGroupProductId(product.getProductId());
							result.setPriceCost(productGroup.getGroupPriceCost());
							result.setPriceRecommend(productGroup.getGroupPriceRecommend());
							result.setRealSellPrice(productGroup.getGroupPriceWholesale());
							result.setSaleTimes(StringUtil.nullToInteger(productGroup.getSaleTimes()));
							result.setIsFreeTax(StringUtil.nullToBoolean(groupProduct.getIsFreeTax()));
							productList.add(result);
						}
					}else {
						// 普通商品
						strBuffer.append(String.format("%s*%s+", StringUtil.subStr(product.getName(), 24), productGroup.getSaleTimes()));
						totalWeight += DoubleUtil.mul(StringUtil.nullToDouble(product.getWeigth()), StringUtil.nullToDouble(productGroup.getSaleTimes()));

						// 单个商品拆单(立即购买)
						if(StringUtil.nullToBoolean(isMustSell)){
							// 目的只检查单个组合子商品是否有库存
							MsgModel<Product> buyMsgModel = ProductCheckUtil.checkProduct(productGroup.getProductId(), null, null, totalNumber, userInfo,true);
							if(!StringUtil.nullToBoolean(buyMsgModel.getIsSucc())){
								msgModel.setIsSucc(false);
								msgModel.setMessage(buyMsgModel.getMessage());
								return msgModel;
							}
							
							Product result = buyMsgModel.getData();
							result.setPaymentPrice(paymentPrice);
							result.setTax(paymentTax);
							result.setPaymentBuyNumber(quantity);
							result.setGroupProductId(product.getProductId());
							result.setPriceCost(productGroup.getGroupPriceCost());
							result.setPriceRecommend(productGroup.getGroupPriceRecommend());
							result.setRealSellPrice(productGroup.getGroupPriceWholesale());
							result.setSaleTimes(StringUtil.nullToInteger(productGroup.getSaleTimes()));
							result.setIsFreeTax(StringUtil.nullToBoolean(groupProduct.getIsFreeTax()));
							productList.add(result);
						}
					}
				}
				
				
					
				// 商品标签
				groupProduct.setProductTags(strBuffer.deleteCharAt(strBuffer.length() - 1).toString());
				groupProduct.setPaymentWeigth(totalWeight);
				groupProduct.setTax(totalPaymentTax);
				groupProduct.setPaymentPrice(totalPaymentPrice);
				groupProduct.setRealSellPrice(totalPriceWholesale);
				groupProduct.setGroupSingleTotalNumber(groupSingleTotalNumber);
				
				msgModel.setIsSucc(true);
				msgModel.setProductList(productList);
				msgModel.setData(groupProduct);
				return msgModel;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		msgModel.setMessage("组合商品解析错误");
		return msgModel;
	}
	
	
	/**
	 * 检查所选商品类型
	 * @param productList
	 * @return
	 */
	public static MsgModel<Map<Long, List<Product>>> checkProductType(List<Product> productList){
		MsgModel<Map<Long, List<Product>>> msgModel = new MsgModel<Map<Long, List<Product>>>();
		try {
			// 所有商品按仓库拆单
			MsgModel<Map<Long, List<Product>>> wareMsgModel = ProductCheckUtil.getSplitWarehouseIdProductListMap(productList);
			if(!StringUtil.nullToBoolean(wareMsgModel.getIsSucc())) {
				msgModel.setIsSucc(false);
				msgModel.setMessage(StringUtil.null2Str(wareMsgModel.getMessage()));
				return msgModel;
			}
			
			Map<Long, List<Product>> productListMap = wareMsgModel.getData();
			if(productListMap == null || productListMap.size() <= 0){
				msgModel.setIsSucc(false);
				msgModel.setMessage("商品对应的仓库不存在");
				return msgModel;
			}
			
			msgModel.setIsSucc(true);
			msgModel.setData(productListMap);
			return msgModel;
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("商品类型检查错误");
		return msgModel;
	}
	
	
	/**
	 * 检查单笔订单金额是否超过限制
	 * • 行邮仓：1000；
     * • 跨境直邮仓、跨境仓：2600；
	 * @param sumAmount
	 * @param productType
	 * @return
	 */
	public static MsgModel<Void> checkMaxProductAmount(Double sumAmount,Integer productType){
		MsgModel<Void> msgModel = new MsgModel<Void>();
		try {
			List<Integer> productTypeList = new ArrayList<Integer>();
		    productTypeList.add(GoodsType.GOODS_TYPE_CROSS);  //跨境
		    productTypeList.add(GoodsType.GOODS_TYPE_DIRECT); //直邮
		    
			if(productTypeList.contains(productType) 
					&& sumAmount.compareTo(Constants.MAX_PRODUCT_AMOUNT) == 1){
				//根据海关政策，单笔订单不能超过2600元，可分开下单
				msgModel.setIsSucc(false);
				msgModel.setMessage(String.format("单笔订单金额已达上限%s元。", Constants.MAX_PRODUCT_AMOUNT));
				return msgModel;
			}else if(StringUtil.compareObject(productType, GoodsType.GOODS_TYPE_DIRECT_GO)
					&& sumAmount.compareTo(Constants.MAX_PRODUCT_GO_AMOUNT) == 1) {
				//行邮
				msgModel.setIsSucc(false);
				msgModel.setMessage(String.format("行邮单笔订单金额已达上限%s元。", Constants.MAX_PRODUCT_GO_AMOUNT));
				return msgModel;
			}
			msgModel.setIsSucc(true);
			return msgModel;
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("金额上限检查错误");
		return msgModel;
	}
}
