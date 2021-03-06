package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.chunruo.cache.portal.impl.UserCartListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.BuyPostType;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductGroup;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.UserCart;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.UserCartManager;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.vo.ProductGroupVo;

/**
 * @author chunruo
 */
public class ProductCheckUtil {
	

	public static MsgModel<UserCart> checkExistUserCartByProduct(Product product, Long productSpecId, String groupProductInfo, UserInfo userInfo){
		MsgModel<UserCart> msgModel = new MsgModel<UserCart> ();
		try{
			UserCartManager userCartManager = Constants.ctx.getBean(UserCartManager.class);
			List<UserCart> userCartList = userCartManager.getUserCartByProductId(userInfo.getUserId(), product.getProductId());
			if(userCartList != null && userCartList.size() > 0){
				if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
					MsgModel<ProductSpec> xmsgModel = ProductUtil.checkExistProductSpecByProductSpecId(product, productSpecId);
					if(StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
						for(UserCart userCart : userCartList){
							if(StringUtil.nullToBoolean(userCart.getIsSpceProduct()) 
									&& StringUtil.compareObject(StringUtil.nullToLong(userCart.getProductSpecId()), productSpecId)){
								msgModel.setIsSucc(true);
								msgModel.setData(userCart);
								return msgModel;
							}
						}
					}
				}else{
					for(UserCart userCart : userCartList){
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
	 * ????????????????????????
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
						// ??????????????????
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
							//???????????????????????????
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
		msgModel.setMessage("????????????????????????????????????");
		return msgModel;
	}
	
	
	/**
	 * ????????????
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
				//????????????
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
				//???????????????
				List<Long> userCartIdList = StringUtil.stringToLongArray(cartIds);
				if(userCartIdList == null || userCartIdList.size() <= 0){
					resultCheckModel.setIsSucc(false);
					resultCheckModel.setMessage("???????????????????????????");
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
			
			// ??????????????????
			if(productList != null && productList.size() > 0){
				resultCheckModel.setIsSucc(true);
				resultCheckModel.setData(productList);
				return resultCheckModel;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultCheckModel.setIsSucc(false);
		resultCheckModel.setMessage("???????????????,???????????????");
		return resultCheckModel;
	}
	
	/**
	 * ???????????????????????????
	 * @param userCartIdList
	 * @param userId
	 * @param storeId
	 * @param  
	 * @return
	 */
	public static MsgModel<List<Product>> checkCartProduct(List<Long> userCartIdList, UserInfo userInfo, Boolean isCheckCrossLimit){
		MsgModel<List<Product>> resultCheckModel = new MsgModel<List<Product>> ();
		try{
			// ??????????????????????????????????????????
			UserCartListByUserIdCacheManager userCartListByUserIdCacheManager = Constants.ctx.getBean(UserCartListByUserIdCacheManager.class);
			Map<String, UserCart> userCartMap = userCartListByUserIdCacheManager.getSession(userInfo.getUserId());
			if(userCartMap == null || userCartMap.size() <= 0){
				//???????????????
				resultCheckModel.setIsSucc(false);
				resultCheckModel.setMessage("???????????????");
				return resultCheckModel;
			}
			
			Map<Long, UserCart> storeCartMap = new HashMap<Long, UserCart>();
			for(Map.Entry<String, UserCart> entry : userCartMap.entrySet()){
				UserCart userCart = entry.getValue();
				storeCartMap.put(userCart.getCartId(), userCart);
			}
			
			// ?????????????????????????????????
			List<UserCart> userCartList = new ArrayList<UserCart> ();
			for(Long cartId : userCartIdList){
				if(!storeCartMap.containsKey(cartId)){
					resultCheckModel.setIsSucc(false);
					resultCheckModel.setObjectId(cartId);
					resultCheckModel.setMessage("?????????????????????,?????????");
					return resultCheckModel;
				}
				userCartList.add(userCartMap.get(StringUtil.null2Str(cartId)));
			}
	
			// ???????????????????????????
			return ProductCheckUtil.checkProductList(userCartList, userInfo, isCheckCrossLimit);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		resultCheckModel.setIsSucc(false);
		resultCheckModel.setMessage("???????????????????????????");
		return resultCheckModel;
	}
	
	/**
	 * ???????????????????????????
	 * @param userCartIdList
	 * @param userId
	 * @param storeId
	 * @param  
	 * @return
	 */
	public static MsgModel<List<Product>> checkProductList(List<UserCart> userCartList, UserInfo userInfo, Boolean isCheckCrossLimit){
		MsgModel<List<Product>> resultCheckModel = new MsgModel<List<Product>> ();
		try{
			//??????????????????
			Integer sumProductNumbers = 0;
			//??????????????????
			Double sumAmount = 0.0D;
			
			// ?????????????????????????????????
			List<Product> productList = new ArrayList<Product> ();
//			Map<Long,Integer> limitNumberMap = new HashMap<Long,Integer>();
			for(UserCart userCart : userCartList){
				Integer quantity = StringUtil.nullToInteger(userCart.getQuantity());
				
				// ??????????????????????????????
				MsgModel<Product> productCheckModel = ProductCheckUtil.checkProduct(userCart.getProductId(), userCart.getProductSpecId(), userCart.getGroupProductInfo(), quantity, userInfo);
				if(!StringUtil.nullToBoolean(productCheckModel.getIsSucc())){
					resultCheckModel.setIsSucc(false);
					resultCheckModel.setObjectId(userCart.getCartId());
					resultCheckModel.setMessage(productCheckModel.getMessage());
					return resultCheckModel;
				}
				
//				// ??????????????????
				Product product = productCheckModel.getData();
//				if(!limitNumberMap.containsKey(product.getProductId())) {
//                	limitNumberMap.put(product.getProductId(), quantity);
//				}else {
//					limitNumberMap.put(product.getProductId(), limitNumberMap.get(product.getProductId()) + quantity);
//				}
//		
//				Integer totalNumber = StringUtil.nullToInteger(limitNumberMap.get(product.getProductId()));
//				
//				//?????????????????????????????????
//				if(StringUtil.nullToBoolean(product.getIsLevelLimitProduct())
//						&& StringUtil.nullToInteger(product.getLevelLimitNumber()) >= 0
//						&& StringUtil.nullToInteger(product.getLevelLimitNumber()) < totalNumber){
//					resultCheckModel.setIsSucc(false);
//					resultCheckModel.setMessage(String.format("????????????????????????\"%s\"%s???", product.getName(),StringUtil.nullToInteger(product.getLevelLimitNumber())));
//					return resultCheckModel;
//				}
//				
//				//???????????????????????????
//				Map<Long, Integer> productIdMap = PurchaseLimitUtil.USER_LIMIT_THREAD_LOCAL.get();
//                if(productIdMap != null 
//                		&& productIdMap.size() > 0
//                		&& productIdMap.containsKey(StringUtil.nullToLong(product.getProductId()))) {
//                	//??????????????????
//                	Integer remainNumber = StringUtil.nullToInteger(productIdMap.get(StringUtil.nullToLong(product.getProductId())));
//                	System.out.println(String.format("2==>[??????Id:%s;??????????????????:%s;???????????????:%s]", product.getProductId(), remainNumber, totalNumber));
//                	if(totalNumber > remainNumber) {
//                		resultCheckModel.setIsSucc(false);
//                		resultCheckModel.setMessage(String.format("??????????????????????????????\"%s\"%s???", product.getName(), remainNumber));
//        				return resultCheckModel;
//                	}
//                }
				product.setPaymentBuyNumber(quantity);
				
				
				// ????????????????????????
				Integer productType = StringUtil.nullToInteger(product.getProductType());
				if(Constants.PRODUCT_TYPE_CROSS_LIST.contains(productType)){
					//???????????????????????????????????????????????????
					if(product.getIsGroupProduct()) {
						//????????????
						sumProductNumbers += (StringUtil.nullToInteger(product.getGroupSingleTotalNumber()) * quantity);
					}else {
						sumProductNumbers += quantity;
					}
					
					// ?????????????????????
					Double productAmount = DoubleUtil.mul(product.getPaymentPrice(), StringUtil.nullToDouble(quantity));
					// ??????????????????
					Double productTaxAmount = DoubleUtil.mul(product.getTax(),StringUtil.nullToDouble(quantity));
					sumAmount = DoubleUtil.add(DoubleUtil.add(sumAmount, productAmount), productTaxAmount);
				}
				
				product.setPaymentBuyNumber(quantity);
				productList.add(product);
			}
			
			// ?????????????????????????????????
			if(productList == null || productList.size() <= 0){
				resultCheckModel.setIsSucc(false);
				resultCheckModel.setMessage("???????????????????????????");
				return resultCheckModel;
			}
			
			//????????????????????????
			MsgModel<Map<Long, List<Product>>> msgModel = ProductCheckUtil.checkProductType(productList);
			if(!StringUtil.nullToBoolean(msgModel.getIsSucc())) {
				resultCheckModel.setIsSucc(false);
				resultCheckModel.setMessage(msgModel.getMessage());
				return resultCheckModel;
			}
		
			
			resultCheckModel.setIsSucc(true);
			resultCheckModel.setData(productList);
			return resultCheckModel;
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			//???????????????????????????????????????tomcat????????????????????????????????????????????????????????????????????????
			PurchaseLimitUtil.USER_LIMIT_THREAD_LOCAL.remove();
		}
		
		resultCheckModel.setIsSucc(false);
		resultCheckModel.setMessage("???????????????????????????");
		return resultCheckModel;
	}
	
	/**
	 * ????????????????????????
	 * @param productId
	 * @param storeId
	 * @param number
	 * @return
	 */
	public static MsgModel<Product> checkProduct(Long productId, Long productSpecId, String groupProductInfo, int number, UserInfo userInfo){
		return ProductCheckUtil.checkProduct(productId, productSpecId, groupProductInfo, number, userInfo, false);
	}
	
	/**
	 * ????????????????????????
	 * @param productId
	 * @param storeId
	 * @param number
	 * @return
	 */
	public static MsgModel<Product> checkProduct(Long productId, Long productSpecId, String groupProductInfo, int number, UserInfo userInfo, Boolean isGroupChilderProduct){
		MsgModel<Product> msgModel = new MsgModel<Product> ();
		try{
			// ??????????????????????????????
			if(number <= 0){
				msgModel.setIsSucc(false);
				msgModel.setMessage("????????????????????????");
				return msgModel;
			}
			
			// ??????????????????????????????
			MsgModel<Product> xsgModel = ProductUtil.getProductByUserLevel(productId, productSpecId, userInfo, true);
			if(!StringUtil.nullToBoolean(xsgModel.getIsSucc())){
				//???????????????
				msgModel.setIsSucc(false);
				msgModel.setMessage(xsgModel.getMessage());
				return msgModel;
			}
			
			// ????????????
			Product product = xsgModel.getData();
			
			// ???????????????????????????
			if(number > StringUtil.nullToInteger(product.getPaymentStockNumber())){
				msgModel.setIsSucc(false);
				msgModel.setMessage(String.format("\"%s\"??????????????????", product.getName()));
				if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
					msgModel.setMessage(String.format("\"%s\",\"%s\"??????????????????", product.getName(), product.getProductTags()));
				}
				return msgModel;
			}
			
			// ????????????????????????????????????(??????????????????????????????)
			if(StringUtil.nullToBoolean(isGroupChilderProduct)){
				// ???????????????????????????
				msgModel.setData(product);
				msgModel.setIsSucc(true);
				return msgModel;
			}
			
			// ??????????????????
			if(StringUtil.nullToBoolean(product.getIsGroupProduct())){
				product.setGroupProductInfo(groupProductInfo);
				MsgModel<Product> xmsgModel = ProductCheckUtil.checkGroupProductByUserLevel(product, product.getGroupProductInfo(), number, true, userInfo);
				if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
					msgModel.setIsSucc(false);
					msgModel.setMessage(xmsgModel.getMessage());
					return msgModel;
				}
				
				// ?????????????????????????????????
				product = xmsgModel.getData();
			}
			
		    
			msgModel.setData(product);
			msgModel.setIsSucc(true);
			return msgModel;
		}catch(Exception e){
			e.printStackTrace();
			
			msgModel.setIsSucc(false);
			msgModel.setMessage("???????????????");
			return msgModel;
		}
	}
	
	/**
	 * ??????????????????
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
				msgModel.setMessage("????????????????????????????????????????????????????????????");
				return msgModel;
			}
			
			msgModel.setIsSucc(true);
			msgModel.setData(productListMap);
			return msgModel;
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("??????????????????");
		return msgModel;
	}
	
	/**
	 * ??????????????????????????????
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
			
			// ??????????????????
			List<Integer> userLevelWholesale = new ArrayList<Integer> ();
			userLevelWholesale.add(UserLevel.USER_LEVEL_DEALER);	// ?????????
			userLevelWholesale.add(UserLevel.USER_LEVEL_AGENT);		// ????????????
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
							msgModel.setMessage(String.format("\"%s\"?????????????????????", product.getName()));
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
									// ??????????????????groupId
									ProductGroupVo productGroupVo = new ProductGroupVo ();
									productGroupVo.setGroupId(groupId);
									productGroupVo.setProductId(productGroup.getProductId());
									productGroupVo.setProductSpecId(productGroup.getProductSpecId());
									productGroupVo.setSaleTimes(productGroup.getSaleTimes());
									// ????????????????????????
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
					
					// ???????????????groupId?????????????????????????????????????????????
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
		msgModel.setMessage(String.format("\"%s\"?????????????????????", product.getName()));
		return msgModel;
	}
	
	/**
	 * ???????????????????????????
	 * @param product
	 * @return
	 */
	public static MsgModel<List<Product>> getGroupProductListByUserInfo(Product groupProduct, UserInfo userInfo){
		MsgModel<List<Product>> msgModel = new MsgModel<List<Product>>();
		try {
			// ???????????????
			if(!StringUtil.nullToBoolean(groupProduct.getIsGroupProduct())) {
				msgModel.setIsSucc(false);
				msgModel.setMessage(String.format("\"%s\"????????????????????????", groupProduct.getName()));
				return msgModel;
			}
			
			Map<Long, List<ProductGroup>> productGroupListMap = groupProduct.getProductGroupListMap();
			if (productGroupListMap == null || productGroupListMap.size() <= 0) {
				// ???????????????
				msgModel.setIsSucc(false);
				msgModel.setMessage(String.format("\"%s\"????????????????????????", groupProduct.getName()));
				return msgModel;
			}
			
			List<Product> productList = new ArrayList<Product>();
			for(Entry<Long, List<ProductGroup>> entry : productGroupListMap.entrySet()){
				List<ProductGroup> productGroupList = entry.getValue();
				if(productGroupList == null || productGroupList.size() <= 0){
					// ?????????????????????(??????????????????)
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"??????????????????", groupProduct.getName()));
					return msgModel;
				}
				
				// ??????????????????????????????
				MsgModel<Product> xmsgModel = ProductUtil.getProductByProductId(entry.getKey(), true);
				if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
					// ?????????????????????(??????????????????)
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"??????????????????", groupProduct.getName()));
					return msgModel;
				}
				
				// ???????????????????????????
				Product product = xmsgModel.getData();
				if (StringUtil.nullToBoolean(product.getIsSpceProduct())) {
					Map<Long, ProductSpec> productSpecMap = new HashMap<Long, ProductSpec> ();
					for(ProductSpec productSpec : product.getProductSpecList()){
						// ????????????????????????
						productSpec.setPaymentStockNumber(0);
						productSpec.setIsPaymentSoldout(true);
						productSpecMap.put(productSpec.getProductSpecId(), productSpec);
					}
					
					// ????????????0??????
					product.setPaymentStockNumber(0);
					product.setIsPaymentSoldout(true);
					
					int paymentStockNumber = 0;
					boolean isPaymentSoldout = true;
					List<Double> defualtPriceList = new ArrayList<Double> ();
					for(ProductGroup productGroup : productGroupList){
						if(productSpecMap.containsKey(productGroup.getProductSpecId())){
							// ????????????
							MsgModel<ProductSpec> ssgModel = ProductUtil.checkExistProductSpecByProductSpecId(product, productGroup.getProductSpecId());
							if(!StringUtil.nullToBoolean(ssgModel.getIsSucc())) {
								msgModel.setIsSucc(false);
								msgModel.setMessage(ssgModel.getMessage());
								return msgModel;
							}
							
							// ??????????????????????????????
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
						
							// ?????????????????????
							if(!StringUtil.nullToBoolean(productGroup.getIsPaymentSoldout())){
								isPaymentSoldout = false;
								int stockNumber = StringUtil.nullToInteger(product.getPaymentStockNumber());
								paymentStockNumber = stockNumber + StringUtil.nullToInteger(productGroup.getPaymentStockNumber());
								
								// ?????????????????????????????????
								defualtPriceList.add(StringUtil.nullToDouble(cmsgModel.getData()));
							}
						}
					}
					
					// ????????????0??????
					product.setPaymentStockNumber(paymentStockNumber);
					product.setIsPaymentSoldout(isPaymentSoldout);
					
					// ??????????????????????????????????????????
					if(defualtPriceList != null && defualtPriceList.size() > 0){
						Collections.sort(defualtPriceList);
						product.setPaymentPrice(StringUtil.nullToDouble(defualtPriceList.get(0)));
					}
				}else {
					ProductGroup productGroup = productGroupList.get(0);
					
					// ????????????0??????
					product.setPaymentStockNumber(0);
					product.setIsPaymentSoldout(true);
					
					// ??????????????????????????????
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
		
		msgModel.setMessage("????????????????????????");
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	/**
	 * ???????????????
	 * @param product
	 * @return
	 */
	public static MsgModel<Product> checkGroupProductByUserLevel(Product groupProduct, String groupProductInfo, Integer quantity, boolean isMustSell, UserInfo userInfo){
		MsgModel<Product> msgModel = new MsgModel<Product> ();
		try{
			//???????????????????????????
			if(StringUtil.nullToBoolean(groupProduct.getIsGroupProduct())) {
				StringBuffer strBuffer = new StringBuffer();
				Integer groupSingleTotalNumber = 0;
				Double totalPaymentPrice = 0.0D;
				Double totalPaymentTax = 0.0D;
				Double totalWeight = 0D;
				Double totalPriceWholesale = 0.0D;
				
				// ????????????????????????
				List<Long> groupIdList = StringUtil.stringToLongArray(groupProductInfo);
				if(groupIdList == null || groupIdList.size() <= 0){
					msgModel.setIsSucc(false);
					msgModel.setMessage("????????????????????????");
					return msgModel;
				}
				
				// ?????????????????????????????????
				Map<Long, List<ProductGroup>> productGroupMapList = groupProduct.getProductGroupListMap();
				if(productGroupMapList == null || productGroupMapList.size() <= 0){
					msgModel.setIsSucc(false);
					msgModel.setMessage("??????????????????????????????");
					return msgModel;
				}
				
				// ??????????????????????????????
				Map<Long, ProductGroup> productGroupMap = new HashMap<Long, ProductGroup> ();
				for(List<ProductGroup> groupList : productGroupMapList.values()){
					if(groupList != null && groupList.size() > 0){
						for(ProductGroup productGroup : groupList){
							productGroupMap.put(productGroup.getGroupId(), productGroup);
						}
					}
				}
				
				// ??????????????????????????????????????????
				Set<Long> prodctIdSet = new HashSet<Long> ();
				for(Long groupId : groupIdList){
					if(productGroupMap.containsKey(groupId)){
						ProductGroup productGroup = productGroupMap.get(groupId);
						if(prodctIdSet.contains(productGroup.getProductId())){
							msgModel.setIsSucc(false);
							msgModel.setMessage("????????????????????????");
							return msgModel;
						}
						prodctIdSet.add(productGroup.getProductId());
					}else{
						msgModel.setIsSucc(false);
						msgModel.setMessage("?????????????????????????????????");
						return msgModel;
					}
				}
				
				// ???????????????????????????????????????
				for(Long productId : productGroupMapList.keySet()){
					if(!prodctIdSet.contains(productId)){
						String errorMsg = "?????????????????????";
						Product product = ProductUtil.getProduct(productId);
						if(product != null && product.getProductId() != null){
							errorMsg = String.format("\"%s\"?????????????????????", StringUtil.null2Str(product.getName()));
						}
						
						msgModel.setIsSucc(false);
						msgModel.setMessage(errorMsg);
						return msgModel;
					}
				}
				
				Boolean isFreeTax = StringUtil.nullToBoolean(groupProduct.getIsFreeTax());
			    Integer productType = ProductUtil.getProductType(groupProduct.getWareHouseId());
					
				// ????????????????????????????????????
				List<Product> productList = new ArrayList<Product> ();
				for(Long groupId : groupIdList){
					// ????????????????????????????????????
					ProductGroup productGroup = productGroupMap.get(groupId);
					Product product = ProductUtil.getProduct(productGroup.getProductId());
					if(product == null || product.getProductId() == null){
						msgModel.setIsSucc(false);
						msgModel.setMessage("?????????????????????????????????");
						return msgModel;
					}
					
					// ??????????????????????????????
					MsgModel<Double> cmsgModel = ProductUtil.getPaymentPriceByUserInfo(groupProduct,productGroup.getGroupPriceWholesale(), productGroup.getGroupPriceRecommend(), userInfo,false);
					if(!StringUtil.nullToBoolean(cmsgModel.getIsSucc())){
						msgModel.setIsSucc(false);
						msgModel.setMessage(cmsgModel.getMessage());
						return msgModel;
					}
					
					//????????????????????????
					Double paymentPrice = StringUtil.nullToDoubleFormat(cmsgModel.getData());
					// ?????????????????????
					Double paymentTax = 0.0D;
					MsgModel<Double> tsgModel = ProductUtil.getProductTax(paymentPrice, productType, isFreeTax);
		            if(StringUtil.nullToBoolean(tsgModel.getIsSucc())) {
		            	paymentTax = tsgModel.getData();
		            }
					
				    // ????????????
					totalPaymentPrice = DoubleUtil.add(totalPaymentPrice, paymentPrice); 
					totalPaymentTax = DoubleUtil.add(totalPaymentTax, paymentTax);
					totalPriceWholesale = DoubleUtil.add(totalPriceWholesale, StringUtil.nullToDouble(productGroup.getGroupPriceWholesale()));
					groupSingleTotalNumber += StringUtil.nullToInteger(productGroup.getSaleTimes());
					// ???????????????(??????????????????*???????????????????????????)
					int totalNumber = StringUtil.nullToInteger(productGroup.getSaleTimes()) * quantity;
					
					// ????????????????????????
					if (StringUtil.nullToBoolean(product.getIsSpceProduct())) {
						// ????????????
						MsgModel<ProductSpec> xmsgModel = ProductUtil.checkExistProductSpecByProductSpecId(product, productGroup.getProductSpecId());
						if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())) {
							msgModel.setIsSucc(false);
							msgModel.setMessage(xmsgModel.getMessage());
							return msgModel;
						}
						
						ProductSpec productSpec = xmsgModel.getData();
						// ?????????????????????
						totalWeight += DoubleUtil.mul(StringUtil.nullToDouble(productSpec.getWeigth()), StringUtil.nullToDouble(productGroup.getSaleTimes()));
						strBuffer.append(String.format("%s*%s+", productSpec.getProductTags(), productGroup.getSaleTimes()));
						
						// ??????????????????(????????????)
						if(StringUtil.nullToBoolean(isMustSell)){
							// ???????????????????????????????????????????????????
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
						// ????????????
						strBuffer.append(String.format("%s*%s+", StringUtil.subStr(product.getName(), 24), productGroup.getSaleTimes()));
						totalWeight += DoubleUtil.mul(StringUtil.nullToDouble(product.getWeigth()), StringUtil.nullToDouble(productGroup.getSaleTimes()));

						// ??????????????????(????????????)
						if(StringUtil.nullToBoolean(isMustSell)){
							// ???????????????????????????????????????????????????
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
				
				
					
				// ????????????
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
		msgModel.setMessage("????????????????????????");
		return msgModel;
	}
	
	
	/**
	 * ????????????????????????
	 * @param productList
	 * @return
	 */
	public static MsgModel<Map<Long, List<Product>>> checkProductType(List<Product> productList){
		MsgModel<Map<Long, List<Product>>> msgModel = new MsgModel<Map<Long, List<Product>>>();
		try {
			// ???????????????????????????
			MsgModel<Map<Long, List<Product>>> wareMsgModel = ProductCheckUtil.getSplitWarehouseIdProductListMap(productList);
			if(!StringUtil.nullToBoolean(wareMsgModel.getIsSucc())) {
				msgModel.setIsSucc(false);
				msgModel.setMessage(StringUtil.null2Str(wareMsgModel.getMessage()));
				return msgModel;
			}
			
			Map<Long, List<Product>> productListMap = wareMsgModel.getData();
			if(productListMap == null || productListMap.size() <= 0){
				msgModel.setIsSucc(false);
				msgModel.setMessage("??????????????????????????????");
				return msgModel;
			}
			
			msgModel.setIsSucc(true);
			msgModel.setData(productListMap);
			return msgModel;
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("????????????????????????");
		return msgModel;
	}
	
	
}
