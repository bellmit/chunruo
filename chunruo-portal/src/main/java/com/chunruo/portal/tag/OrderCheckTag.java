package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.chunruo.cache.portal.impl.PostageTemplateCacheManager;
import com.chunruo.cache.portal.impl.UserInfoByIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.BuyPostType;
import com.chunruo.core.model.PostageTemplate;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.UserAddress;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.OrderUtil;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.PostageUtil;
import com.chunruo.portal.util.ProductCheckUtil;
import com.chunruo.portal.util.ProductUtil;
import com.chunruo.portal.util.UserAddressUtil;
import com.chunruo.portal.vo.PostageVo;
import com.chunruo.portal.vo.TagModel;

/**
 * 订单确认页面
 * @author chunruo
 *
 */
public class OrderCheckTag extends BaseTag {
	public final static Double DEFAULT_FREE_POSTAGE_AMOUNT = 10D;

	public TagModel<Map<String, Object>> getData(Object postType_1, Object productId_1, Object productSpecId_1, Object number_1, Object cartIds_1, Object orderStackId_1, Object addressId_1, Object userCouponId_1, Object groupProductInfo_1){
		Integer postType = StringUtil.nullToInteger(postType_1);		//请求方式: 0:立即下单;1:购物车结算
		Long productId = StringUtil.nullToLong(productId_1);			//立即购买: 商品ID
		Long productSpecId = StringUtil.nullToLong(productSpecId_1);	//立即购买: 商品规格ID
		Integer number = StringUtil.nullToInteger(number_1);			//立即购买: 商品数量
		String cartIds = StringUtil.null2Str(cartIds_1);				//购物车结算: 购物车IDs
		String addressId = StringUtil.null2Str(addressId_1);			//收货地址Id: 物流方式选择用户收货地址
		String groupProductInfo = StringUtil.nullToString(groupProductInfo_1).replace("\\s+", ""); //组合商品数据
		
		TagModel<Map<String, Object>> tagModel = new TagModel<Map<String, Object>> ();
		try{
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if(userInfo == null || userInfo.getUserId() == null){
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}
			

			//请求方式
			List<Integer> buyPostTypeList = new ArrayList<Integer> ();
			buyPostTypeList.add(BuyPostType.POST_BUY_QUICK_TYPE);		//立即购买
			buyPostTypeList.add(BuyPostType.POST_BUY_CART_TYPE);		//购物车结算
			if(!buyPostTypeList.contains(StringUtil.nullToInteger(postType))){
				tagModel.setCode(PortalConstants.CODE_ERROR);
				tagModel.setMsg("请求方式错误");
				return tagModel;
			}
			
			//从缓存重新读取用户信息
			PostageTemplateCacheManager postageTemplateCacheManager = Constants.ctx.getBean(PostageTemplateCacheManager.class);
			UserInfoByIdCacheManager userInfoByIdCacheManager = Constants.ctx.getBean(UserInfoByIdCacheManager.class);
			userInfo = userInfoByIdCacheManager.getSession(userInfo.getUserId());
			
			// 商品信息
			MsgModel<List<Product>> productCheckModel = ProductCheckUtil.check(postType, productId, productSpecId, groupProductInfo, number, cartIds, userInfo);
			if(!StringUtil.nullToBoolean(productCheckModel.getIsSucc())){
				tagModel.setCode(PortalConstants.CODE_ERROR);
				tagModel.setMsg(productCheckModel.getMessage());
				return tagModel;
			}else if(productCheckModel.getData() == null || productCheckModel.getData().size() <= 0){
				tagModel.setCode(PortalConstants.CODE_ERROR);
				tagModel.setMsg("商品信息不存在或已售罄");
				return tagModel;
			}
			
			// 购买商品详细列表
			List<Product> buyProductList = productCheckModel.getData();

			// 获取用户的发货地址信息
			Long xaddressId = StringUtil.nullToLong(addressId);
			UserAddress defualtAddress = UserAddressUtil.getDefualtAddress(userInfo.getUserId(), xaddressId);
			

			

			//配送方式
			Long provinceId = 310000L;
			Map<String, Object> addressMap = new HashMap<String, Object> ();
			addressMap.put("isEmpty", Constants.YES);
			
			//物流方式
			//获取用户地址信息
			if(defualtAddress == null || defualtAddress.getAddressId() == null){
				addressMap.put("isEmpty", Constants.YES);
				addressMap.put("isValidIdentity", Constants.NO);
			}else{
				
				MsgModel<UserAddress> msgModel = UserAddressUtil.checkIsValidUserAddress(defualtAddress);
				// 是否有效的用户地址
				Integer isValidIdentity = Constants.NO;
				if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
					isValidIdentity = Constants.YES;
				}
				
				// 用户地址信息
				String xName = null;
				String xMobile = null;
				String xIdentityNo = null;
				String xAddress = null;
				String address = null;
				Long xAddressId = null;
				Long xCityId = null;
				Long xAreaId = null;
				String realName = null;
				Integer isEmpty = Constants.YES;
				Integer isDefault = Constants.NO;
				
				UserAddress userAddress = msgModel.getData();
				if(userAddress != null && userAddress.getAddressId() != null){
					isEmpty = Constants.NO;
					provinceId = StringUtil.nullToLong(userAddress.getProvinceId());
					xCityId = StringUtil.nullToLong(userAddress.getCityId());
					xAreaId = StringUtil.nullToLong(userAddress.getAreaId());
					xName = StringUtil.null2Str(userAddress.getName());
					xMobile = StringUtil.null2Str(userAddress.getMobile());
					xIdentityNo = StringUtil.null2Str(userAddress.getIdentityNo());
					xAddressId = StringUtil.nullToLong(userAddress.getAddressId());
					address = StringUtil.null2Str(userAddress.getAddress());
					xAddress = UserAddressUtil.getFullAddressInfo(userAddress);
					realName =  StringUtil.null2Str(userAddress.getRealName());
					isDefault = StringUtil.booleanToInt(userAddress.getIsDefault());
				}
				
				//获取收货人地址信息
				addressMap.put("isEmpty", isEmpty);
				addressMap.put("addressId", xAddressId);
				addressMap.put("name", StringUtil.null2Str(xName));
				addressMap.put("mobile", StringUtil.null2Str(xMobile));
				addressMap.put("provinceId", provinceId);
				addressMap.put("cityId", xCityId);
				addressMap.put("areaId", xAreaId);
				addressMap.put("address", StringUtil.null2Str(xAddress));
				addressMap.put("xaddress", StringUtil.null2Str(address));
				addressMap.put("msg", StringUtil.null2Str(msgModel.getMessage()));
				addressMap.put("realName", realName);
				addressMap.put("identityNo", StringUtil.identityNoFormat(xIdentityNo));
				addressMap.put("isValidIdentity", isValidIdentity);
				addressMap.put("isDefault", isDefault);
				
			}

			int totalNumber = 0;
			Double totalPostage = new Double (0);
			Double totalProductAmount = new Double(0);
			Double totalOrderAmount = new Double (0);
			Double totalTaxAmount = new Double(0);
			Double freePosageProductAmount = new Double(0);
			//订单商品与价格MAP
			Map<Long, Double> couponProductMap = new HashMap<Long, Double>();
			//订单商品分类与价格MAP
			Map<Long, Double> couponCategroyMap = new HashMap<Long, Double>();
			//按邮费模板计算重量
			Map<Long, Double> templateWeightsMap = new HashMap<Long, Double> ();
			List<Map<String, Object>> productMapList = new ArrayList<Map<String, Object>> ();
			for(Product product : buyProductList){
				
				//商品数量
				int productNumber = StringUtil.nullToInteger(product.getPaymentBuyNumber());
				Double doubleProductNumber = StringUtil.nullToDouble(product.getPaymentBuyNumber());
				//商品单价
				Double price = StringUtil.nullToDoubleFormat(product.getPaymentPrice());
				//商品总结
				Double productAmount = DoubleUtil.mul(price, doubleProductNumber);

				//按模版统计重量
				Double totalWeights = DoubleUtil.mul(StringUtil.nullToDouble(product.getPaymentWeigth()), doubleProductNumber); 
				//总商品数量
				totalNumber += productNumber;
				//总商品金额
				totalProductAmount = DoubleUtil.add(totalProductAmount, productAmount);

				//仓库类型
				Integer productType = ProductUtil.getProductType(product.getWareHouseId());
				//商品税费计算
				MsgModel<Double> tsgModel = ProductUtil.getProductTax(productAmount, productType, product.getIsFreeTax());
				if(StringUtil.nullToBoolean(tsgModel.getIsSucc())) {
					totalTaxAmount = DoubleUtil.add(totalTaxAmount, StringUtil.nullToDouble(tsgModel.getData()));
				}
				
				// 按模版计算邮费
				if(!StringUtil.nullToBoolean(product.getIsFreePostage())){
					Long postTplId = product.getTemplateId();
					if(templateWeightsMap.containsKey(postTplId)){
						Double weights = templateWeightsMap.get(postTplId);
						templateWeightsMap.put(postTplId, totalWeights + weights);
					}else{
						templateWeightsMap.put(postTplId, totalWeights);
					}
				}else {
					freePosageProductAmount = DoubleUtil.add(freePosageProductAmount, productAmount);
				}
				
				//判断分销商品是否售罄或下架
				Map<String, Object> objectMap = new HashMap<String, Object> ();
				objectMap.put("productId", product.getProductId());
				objectMap.put("productName",  StringUtil.null2Str(product.getName()));
				objectMap.put("imageURL",  StringUtil.null2Str(product.getImage()));
				objectMap.put("productNumber", productNumber);
				objectMap.put("price", StringUtil.nullToDoubleFormat(price));
				objectMap.put("isSeckillProduct", StringUtil.booleanToInt(product.getIsSeckillProduct()));
				
				// 多规格商品选择信息
				if(StringUtil.nullToBoolean(product.getIsSpceProduct()) && product.getCurrentProductSpec() != null){
					ProductSpec productSpec = product.getCurrentProductSpec();
					objectMap.put("productSpecId", StringUtil.null2Str(productSpec.getProductSpecId()));
					objectMap.put("productTags", StringUtil.null2Str(productSpec.getProductTags()));
				}else if(StringUtil.nullToBoolean(product.getIsGroupProduct())){
					objectMap.put("productTags", StringUtil.null2Str(product.getProductTags()));
				}
				productMapList.add(objectMap);
				
				//用户优惠券数据统计
				//按商品Id统计优惠券金额
				Double totalCouponProductAmount = StringUtil.nullToDouble(productAmount);
				if(couponProductMap.containsKey(product.getProductId())) {
					Double couponProductAmount = couponProductMap.get(product.getProductId());
					totalCouponProductAmount = DoubleUtil.add(totalCouponProductAmount, couponProductAmount);
				}
				couponProductMap.put(product.getProductId(), totalCouponProductAmount);
				
				//按商品类目统计优惠券金额
				Double totalCategoryAmount = StringUtil.nullToDouble(productAmount);
				if(product.getCategoryIdList() != null && !product.getCategoryIdList().isEmpty()) {
					for(Long categroyId : product.getCategoryIdList()) {
						if(couponCategroyMap.containsKey(categroyId)) {
							Double categoryAmount = couponCategroyMap.get(categroyId);
							totalCategoryAmount = DoubleUtil.add(totalCategoryAmount, categoryAmount);
						}
						couponCategroyMap.put(categroyId, totalCategoryAmount);
					}
				}
			}
			
//			//物流方式按地区计算邮费
//			if(templateWeightsMap != null && templateWeightsMap.size() > 0){
//				for(Entry<Long, Double> postTplEntry : templateWeightsMap.entrySet()){
//					PostageVo postageVo = PostageUtil.getPostage(postTplEntry.getKey(), provinceId, postTplEntry.getValue());
//					if(postageVo != null && postageVo.getPostage() != null){
//						//按模版计算总邮费
//						totalPostage = DoubleUtil.add(totalPostage, postageVo.getPostage());
//					}
//				}
//			}
			
			//跨境商品邮费税费
			Double productTaxAmount = totalTaxAmount;
			Double postageTaxAmount = new Double(0);
			
			//获取免运费模板
			PostageTemplate freeTemplate = null;
			List<PostageTemplate> postageTemplateList = postageTemplateCacheManager.getSession();
			if(postageTemplateList != null && postageTemplateList.size() > 0){
				for(PostageTemplate postageTemplate : postageTemplateList){
					if(StringUtil.nullToBoolean(postageTemplate.getIsFreeTemplate())) {
						freeTemplate = postageTemplate;
						break;
					}
				}
			}
			
			if(freeTemplate == null
					|| freePosageProductAmount.compareTo(StringUtil.nullToDouble(freeTemplate.getFreePostageAmount())) < 0) {
				totalPostage= OrderCheckTag.DEFAULT_FREE_POSTAGE_AMOUNT;
				System.out.println("默认运费"+totalPostage);
			}
			
			totalOrderAmount = DoubleUtil.add(totalProductAmount, DoubleUtil.add(totalPostage, totalTaxAmount));

			tagModel.setMapList(productMapList);
			tagModel.setDataMap(addressMap);
			
			// 检查是否使用优惠券
			Double preferentialAmount = 0.0D;
			Double payAmount = StringUtil.nullToDoubleFormat(totalOrderAmount);
			Double realPayAmount = payAmount;
			Long realUserCouponId = null;
			String couponName = "";
			
			//检查用户账户余额
			Double accountAmount = Double.valueOf(0.0d);  	//账户可用余额
			Double payAccountAmount = Double.valueOf(0.0d); //账户余额支付
			// 检查订单账户余额支付
			MsgModel<Double> msgModel = OrderUtil.checkOrderUserAccount(userInfo.getUserId(), realPayAmount, null, OrderUtil.CHECK_ACCOUNT_ORDER_TAG, null);
			if(StringUtil.nullToBoolean(msgModel.getIsSucc())) {
				// 重新计算使用账号余额和实际订单实付金额
				realPayAmount = msgModel.getData();
				accountAmount = msgModel.getAccountAmount();
				payAccountAmount = msgModel.getPreferentialAmount();
			}
			
			
			Map<String, Object> dataMap = new HashMap<String, Object> ();
			dataMap.put("tax", StringUtil.nullToDoubleFormatStr(totalTaxAmount));
			
			dataMap.put("postage", StringUtil.nullToDoubleFormatStr(totalPostage));
			dataMap.put("productTax", StringUtil.nullToDoubleFormatStr(productTaxAmount));
			dataMap.put("postageTax", StringUtil.nullToDoubleFormatStr(postageTaxAmount));
			dataMap.put("orderAmount", StringUtil.nullToDoubleFormatStr(totalOrderAmount));
			dataMap.put("totalPayAmount", StringUtil.nullToDoubleFormatStr(DoubleUtil.sub(totalOrderAmount, preferentialAmount)));
			dataMap.put("accountAmount", StringUtil.nullToDoubleFormatStr(accountAmount));        //账户可用余额
			dataMap.put("payAccountAmount", StringUtil.nullToDoubleFormatStr(payAccountAmount));  //账户余额支付
			dataMap.put("payAmount", StringUtil.nullToDoubleFormatStr(payAmount));                //实际支付(不包含使用账户余额)
			dataMap.put("realPayAmount", StringUtil.nullToDoubleFormatStr(realPayAmount));        //实际支付(包含使用账户余额)
			dataMap.put("productAmount", StringUtil.nullToDoubleFormatStr(totalProductAmount));
			dataMap.put("productType", 0);
			dataMap.put("totalNumber", StringUtil.nullToInteger(totalNumber));
			dataMap.put("storeName", StringUtil.null2Str(userInfo.getStoreName()));
			dataMap.put("postType", StringUtil.nullToInteger(postType));
			dataMap.put("productId", StringUtil.null2Str(productId));
			dataMap.put("productSpecId", StringUtil.null2Str(productSpecId));
			dataMap.put("number", StringUtil.null2Str(number));
			dataMap.put("cartIds", StringUtil.null2Str(cartIds));
			dataMap.put("preferentialAmount", StringUtil.nullToDoubleFormatStr(preferentialAmount));
			dataMap.put("useCouponId", realUserCouponId);
			dataMap.put("couponName", couponName);
			tagModel.setData(dataMap);
			
		}catch(Exception e){
			e.printStackTrace();
		}

		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		tagModel.setMsg("请求成功");
		return tagModel;
	}
	
	
	
}
