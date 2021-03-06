package com.chunruo.portal.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.cache.portal.impl.OrderByIdCacheManager;
import com.chunruo.cache.portal.impl.OrderListByStoreIdCacheManager;
import com.chunruo.cache.portal.impl.OrderListByUserIdCacheManager;
import com.chunruo.cache.portal.impl.PostageTemplateCacheManager;
import com.chunruo.cache.portal.impl.UserCartListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.BuyPostType;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.PostageTemplate;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.UserAddress;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.ChilderOrderVo;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.interceptor.LoginInterceptor;
import com.chunruo.portal.tag.OrderCheckTag;
import com.chunruo.portal.util.OrderUtil;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.PostageUtil;
import com.chunruo.portal.util.ProductCheckUtil;
import com.chunruo.portal.util.RequestUtil;
import com.chunruo.portal.util.UserAddressUtil;
import com.chunruo.portal.vo.PostageVo;

@Controller
@RequestMapping("/api/order/")
public class OrderController extends BaseController{
	
	public static Integer COMMON_MAX_NUMBER = 500;
	
	public static Double TOP_PROFIT_RATE = 0.1;
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private OrderByIdCacheManager orderByIdCacheManager;
	@Autowired
	private PostageTemplateCacheManager postageTemplateCacheManager;
	@Autowired
	private UserCartListByUserIdCacheManager userCartListByUserIdCacheManager;
	@Autowired
	private OrderListByUserIdCacheManager orderListByUserIdCacheManager;
	@Autowired
	private OrderListByStoreIdCacheManager orderListByStoreIdCacheManager;
	

	/**
	 * ???????????????????????????
	 * ????????????
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/checkBuy")
	public @ResponseBody Map<String, Object> checkBuy(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long productId = StringUtil.nullToLong(request.getParameter("productId"));
		String productSpecId = StringUtil.null2Str(request.getParameter("productSpecId"));
		Integer number = StringUtil.nullToInteger(request.getParameter("number"));
		String groupProductInfo = StringUtil.nullToString(request.getParameter("groupProductInfo")).replace("\\s+", ""); //??????????????????

		try{
			//??????????????????
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			
//			if(!StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
//			    Date expireDate = DateUtil.getMonthAfterByDay(userInfo.getCreateTime(), 6);
//				if(expireDate.before(DateUtil.getCurrentDate())) {
//					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
//					resultMap.put(PortalConstants.MSG, "???????????????????????????????????????????????????????????????");
//					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
//					return resultMap;
//				}
//			}

			//????????????
			MsgModel<Product> msgModel = ProductCheckUtil.checkProduct(productId, StringUtil.nullToLong(productSpecId), groupProductInfo, number, userInfo, false);
			if(!StringUtil.nullToBoolean(msgModel.getIsSucc())){
				// ????????????????????????
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(msgModel.getMessage()));
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			resultMap.put("productSpecId", productSpecId);
			resultMap.put("productId", msgModel.getData().getProductId());
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "????????????");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, this.getText("??????,????????????"));
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * ????????????????????? 
	 * ???????????????
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/checkCart")
	public @ResponseBody Map<String, Object> checkCart(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String cartIds = StringUtil.null2Str(request.getParameter("cartIds"));

		try{
			List<Long> userCartIdList = StringUtil.stringToLongArray(cartIds);
			if(userCartIdList == null || userCartIdList.size() <= 0){
				// ??????????????????
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "?????????????????????");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// ???????????????????????????
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if(!StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
			    Date expireDate = DateUtil.getMonthAfterByDay(userInfo.getCreateTime(), 6);
				if(expireDate.before(DateUtil.getCurrentDate())) {
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "???????????????????????????????????????????????????????????????");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
			}
			
			// ???????????????????????????????????????
			MsgModel<List<Product>> productCheckModel = ProductCheckUtil.checkCartProduct(userCartIdList, userInfo, true);
			if(!StringUtil.nullToBoolean(productCheckModel.getIsSucc())){
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(productCheckModel.getMessage()));
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "????????????");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "????????????");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	
	/**
	 * ????????????
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/orderConfrim")
	public @ResponseBody Map<String, Object> orderConfrim(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Integer postType = StringUtil.nullToInteger(request.getParameter("postType"));		//????????????: 0:????????????;1:???????????????
		Long productId = StringUtil.nullToLong(request.getParameter("productId"));			//????????????: ??????ID
		Long productSpecId = StringUtil.nullToLong(request.getParameter("productSpecId"));
		Long userCouponId = StringUtil.nullToLong(request.getParameter("userCouponId"));	//?????????ID
		Integer number = StringUtil.nullToInteger(request.getParameter("number"));			//????????????: ????????????
		String cartIds = StringUtil.null2Str(request.getParameter("cartIds"));				//???????????????: ?????????IDs
		Long addressId = StringUtil.nullToLong(request.getParameter("addressId"));			//????????????Id: ????????????????????????????????????
		String groupProductInfo = StringUtil.nullToString(request.getParameter("groupProductInfo")).replace("\\s+", ""); //??????????????????
		UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
		
		try{

			//????????????
			List<Integer> buyPostTypeList = new ArrayList<Integer> ();
			buyPostTypeList.add(BuyPostType.POST_BUY_QUICK_TYPE);		//????????????
			buyPostTypeList.add(BuyPostType.POST_BUY_CART_TYPE);		//???????????????
			if(!buyPostTypeList.contains(StringUtil.nullToInteger(postType))){
				resultMap.put(PortalConstants.MSG, "??????????????????");
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
//			if(!StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
//			    Date expireDate = DateUtil.getMonthAfterByDay(userInfo.getCreateTime(), 6);
//				if(expireDate.before(DateUtil.getCurrentDate())) {
//					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
//					resultMap.put(PortalConstants.MSG, "???????????????????????????????????????????????????????????????");
//					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
//					return resultMap;
//				}
//			}
			

			// ??????????????????????????????
			MsgModel<List<Product>> productCheckModel = ProductCheckUtil.check(postType, productId, productSpecId, groupProductInfo, number, cartIds, userInfo);
			if(!StringUtil.nullToBoolean(productCheckModel.getIsSucc())){
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(productCheckModel.getMessage()));
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}


			//??????????????????????????????
			boolean isUserCoupon = false;

			// ??????????????????
			List<Product> buyProductList = productCheckModel.getData();


			//????????????????????????????????????
			MsgModel<UserAddress> uMsgModel = UserAddressUtil.getAddressByAddressId(userInfo.getUserId(), addressId);
			if(!StringUtil.nullToBoolean(uMsgModel.getIsSucc())){
				resultMap.put(PortalConstants.MSG, uMsgModel.getMessage());
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			UserAddress userAddress = uMsgModel.getData();

			//????????????????????????????????????
			MsgModel<UserAddress> msgModel = UserAddressUtil.checkIsValidUserAddress(userAddress);
			if(!StringUtil.nullToBoolean(msgModel.getIsSucc())){
				resultMap.put(PortalConstants.MSG, msgModel.getMessage());
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			//????????????ID??????
			List<Long> productIdList = new ArrayList<Long> ();

			
			
			//??????????????????????????????
//			boolean isHaveTopProfit = false;
//			if(!StringUtil.compareObject(userInfo.getTopUserId(), 0)) {
//				UserInfo topUserInfo = this.userInfoManager.get(StringUtil.nullToLong(userInfo.getTopUserId()));
//				if(topUserInfo != null && topUserInfo.getUserId() != null
//						&& StringUtil.compareObject(topUserInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
//					isHaveTopProfit = true;
//				}
//			}
			
			boolean isHaveSubProfit = false;
			Long shareUserId = RequestUtil.getShareUserId(request);
			log.info("---------------------------------shareUserId:"+shareUserId);
			if(!StringUtil.compareObject(shareUserId, 0 )
					&& !StringUtil.compareObject(userInfo.getUserId(), shareUserId)) {
				UserInfo shareUserInfo = this.userInfoManager.get(shareUserId);
				if(shareUserInfo != null 
						&& shareUserInfo.getUserId() != null
						&& StringUtil.compareObject(shareUserInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
					isHaveSubProfit = true;
				}
			}
			
			
			int index = 1;
			int totalNumber = 0;
			Double totalPostage = new Double (0);
			Double totalProductAmount = new Double(0);
			Double totalOrderAmount = new Double (0);
			Double totalTopProfit = new Double (0);
			Double totalSubProfit = new Double (0);
			Double totalTaxAmount = new Double(0);
			Double totalSellPrice = new Double(0);
			List<ChilderOrderVo> childerOrderList = new ArrayList<ChilderOrderVo> ();
			List<OrderItems> orderItemsList = new ArrayList<OrderItems> ();
			int cTotalNumber = 0;
			Double cTotalProductAmount = new Double(0);
			Double cTotalOrderAmount = new Double (0);
			Double cTotalTopProfit = new Double (0);
			Double cTotalSubProfit = new Double (0);
			Double cTotalTaxAmount = new Double (0);
			Double cTotalSellPrice = new Double (0);
			Double freePosageProductAmount = new Double(0);
			Map<Long, Double> templateWeightsMap = new HashMap<Long, Double> ();
			for(Product product : buyProductList){
				//????????????
				int productNumber = StringUtil.nullToInteger(product.getPaymentBuyNumber());
				Double doubleProductNumber = StringUtil.nullToDouble(productNumber);
				//????????????
				Double price = StringUtil.nullToDoubleFormat(product.getPaymentPrice());
				//????????????
				Double productAmount = DoubleUtil.mul(price, doubleProductNumber);
				//?????????????????????
				Double totalWeights = DoubleUtil.mul(StringUtil.nullToDouble(product.getPaymentWeigth()), doubleProductNumber); 

				//????????????
				Double topProfit = new Double(0);
//				if(isHaveTopProfit) {
//					topProfit = DoubleUtil.mul(productAmount, TOP_PROFIT_RATE); 
//				}
				log.info("----------recommednPrice:"+product.getPriceRecommend()+"--price:"+price);

				//????????????
				Double subProfit = new Double(0);
				if(isHaveSubProfit && !StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
//					topProfit = DoubleUtil.mul(productAmount, TOP_PROFIT_RATE); 
					Double everProfit = DoubleUtil.sub(StringUtil.nullToDoubleFormat(product.getPriceRecommend()),StringUtil.nullToDoubleFormat(product.getPriceCost()));
					subProfit = DoubleUtil.mul(everProfit, doubleProductNumber);
				}

				// ?????????????????????
				if(!StringUtil.nullToBoolean(product.getIsFreePostage())) {
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

				//???????????????
				cTotalNumber += productNumber;
				//???????????????
				cTotalProductAmount = DoubleUtil.add(cTotalProductAmount, productAmount);

				OrderItems orderItems = new OrderItems ();
				orderItems.setSort(index);													
				orderItems.setProductId(product.getProductId()); 							
				orderItems.setProductName(product.getName()); 								
				orderItems.setProductCode(product.getProductCode());						
				orderItems.setWareHouseId(product.getWareHouseId());  						
				orderItems.setQuantity(productNumber);										
				orderItems.setPrice(price);													
				orderItems.setWeight(product.getPaymentWeigth());        					
				orderItems.setProfit(subProfit); 												
				orderItems.setTopProfit(topProfit); 										
				orderItems.setRealSellPrice(productAmount);                            
				orderItems.setPriceWholesale(0D);					
				orderItems.setPriceCost(product.getPriceCost());							
				orderItems.setTax(StringUtil.nullToDoubleFormat(0));			
				orderItems.setIsFresh(StringUtil.nullToBoolean(product.getIsFresh()));
				orderItems.setAmount(StringUtil.nullToDoubleFormat(productAmount));			
				orderItems.setDiscountAmount(DoubleUtil.add(orderItems.getAmount(), orderItems.getTax()));	
				orderItems.setPreferentialAmount(0.0D); 									
				orderItems.setProductSku(product.getProductSku());  						
				orderItems.setProductImagePath(product.getImage()); 						
				orderItems.setIsSpceProduct(StringUtil.nullToBoolean(product.getIsSpceProduct()));			
				orderItems.setIsMoreSpecProduct(StringUtil.nullToBoolean(product.getIsMoreSpecProduct()));	

				if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
					ProductSpec currentProductSpec = product.getCurrentProductSpec();
					orderItems.setProductCode(currentProductSpec.getProductCode());							
					orderItems.setProductSku(currentProductSpec.getProductSku());							
					orderItems.setProductSpecId(currentProductSpec.getProductSpecId());						
					orderItems.setPrimarySpecId(currentProductSpec.getPrimarySpecId());						
					orderItems.setPrimarySpecName(currentProductSpec.getPrimarySpecName());					
					orderItems.setPrimarySpecModelName(currentProductSpec.getPrimarySpecModelName());		
					orderItems.setSecondarySpecId(currentProductSpec.getSecondarySpecId());					
					orderItems.setSecondarySpecName(currentProductSpec.getSecondarySpecName());				
					orderItems.setSecondarySpecModelName(currentProductSpec.getSecondarySpecModelName());	
					orderItems.setProductImagePath(currentProductSpec.getSpecImagePath());					
					orderItems.setProductTags(currentProductSpec.getProductTags()); 						
				}


				orderItems.setCreateTime(DateUtil.getCurrentDate());
				orderItems.setUpdateTime(orderItems.getCreateTime());
				orderItemsList.add(orderItems);

				cTotalTopProfit = DoubleUtil.add(cTotalTopProfit, topProfit);
				cTotalSubProfit = DoubleUtil.add(cTotalSubProfit, subProfit);
				index ++;

				productIdList.add(product.getProductId());
			}

			//??????,??????????????????????????????
			Double postage = 0.0D;
//			if(templateWeightsMap != null && templateWeightsMap.size() > 0){
//				for(Entry<Long, Double> postTplEntry : templateWeightsMap.entrySet()){
//					PostageVo postageVo = PostageUtil.getPostage(postTplEntry.getKey(), userAddress.getProvinceId(), postTplEntry.getValue());
//					if(postageVo == null || postageVo.getPostage() == null){
//						resultMap.put(PortalConstants.MSG, "??????????????????");
//						resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
//						resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
//						return resultMap;
//					}
//					//????????????????????????
//					postage = DoubleUtil.add(postage, StringUtil.nullToDouble(postageVo.getPostage()));
//				}
//			}
			
			//?????????????????????
			PostageTemplate freeTemplate = null;
			List<PostageTemplate> postageTemplateList = this.postageTemplateCacheManager.getSession();
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
				postage= OrderCheckTag.DEFAULT_FREE_POSTAGE_AMOUNT;
				System.out.println("??????????????????"+postage);
			}

			cTotalOrderAmount = DoubleUtil.add(DoubleUtil.add(cTotalProductAmount, 0D), cTotalTaxAmount);
			cTotalProductAmount = StringUtil.nullToDoubleFormat(cTotalProductAmount);
			cTotalTopProfit = StringUtil.nullToDoubleFormat(cTotalTopProfit);
			cTotalSubProfit = StringUtil.nullToDoubleFormat(cTotalSubProfit);
			
			ChilderOrderVo childerOrderVo = new ChilderOrderVo ();
			childerOrderVo.setProductType(1);
			childerOrderVo.setPostage(postage);
			childerOrderVo.setOrderAmount(cTotalOrderAmount);
			childerOrderVo.setPayAmount(cTotalOrderAmount);
			childerOrderVo.setProductAmount(cTotalProductAmount);
			childerOrderVo.setRealSellPrice(cTotalSellPrice);
			childerOrderVo.setTax(cTotalTaxAmount);
			childerOrderVo.setTotalNumber(cTotalNumber);
			childerOrderVo.setSubProfit(cTotalSubProfit);
			childerOrderVo.setTopProfit(cTotalTopProfit);
			childerOrderList.add(childerOrderVo);

			totalNumber += cTotalNumber;
			totalPostage = DoubleUtil.add(totalPostage, postage);
			totalProductAmount = DoubleUtil.add(totalProductAmount, cTotalProductAmount);
			totalOrderAmount = DoubleUtil.add(totalOrderAmount, cTotalOrderAmount);
			totalTopProfit = DoubleUtil.add(totalTopProfit, cTotalTopProfit);
			totalSubProfit = DoubleUtil.add(totalSubProfit, cTotalSubProfit);
			totalTaxAmount = DoubleUtil.add(totalTaxAmount, cTotalTaxAmount);
			totalSellPrice = DoubleUtil.add(totalSellPrice, cTotalSellPrice);

			// ???????????????
			Double preferentialAmount = 0.0D;
			//???????????????
			totalProductAmount = StringUtil.nullToDoubleFormat(totalProductAmount);
			//???????????????
			totalPostage = StringUtil.nullToDoubleFormat(totalPostage);
			//??????????????????
			totalTaxAmount = StringUtil.nullToDoubleFormat(totalTaxAmount);
			Double postageTaxAmount = new Double(0);

			//???????????????
			totalOrderAmount = StringUtil.nullToDoubleFormat(DoubleUtil.add(totalProductAmount, DoubleUtil.add(totalPostage, totalTaxAmount)));
			//????????????
			Double payAmount =  totalOrderAmount;


			//????????????????????????
			Order order = new Order ();
			order.setUserId(userInfo.getUserId());					                            //????????????ID
			order.setLoginType(StringUtil.nullToInteger(userInfo.getLoginType()));	            //??????????????????
			order.setLevel(StringUtil.nullToInteger(userInfo.getLevel()));                      //??????????????????
			order.setIsShareBuy(isHaveSubProfit);
			order.setTopUserId(StringUtil.nullToLong(userInfo.getTopUserId()));			      
			order.setShareUserId(shareUserId);             
			order.setStoreId(StringUtil.nullToLong(userInfo.getUserId()));                 
			order.setOrderNo(CoreInitUtil.getRandomNo());     		
			order.setStatus(OrderStatus.NEW_ORDER_STATUS);  		
			order.setProductAmount(StringUtil.nullToDoubleFormat(totalProductAmount));	//?????????????????????????????????????????????
			order.setTotalRealSellPrice(StringUtil.nullToDoubleFormat(totalSellPrice));                                //??????????????????
			order.setPostage(StringUtil.nullToDoubleFormat(totalPostage));    			
			order.setTax(StringUtil.nullToDoubleFormat(totalTaxAmount)); 				
			order.setPostageTax(StringUtil.nullToDoubleFormat(postageTaxAmount));      
			order.setOrderAmount(StringUtil.nullToDoubleFormat(totalOrderAmount)); 		
			order.setPayAmount(StringUtil.nullToDoubleFormat(totalOrderAmount));  		
			order.setProductNumber(totalNumber);										//???????????????
			order.setIsDelete(false);													//??????????????????		
			order.setProfitSub(StringUtil.nullToDoubleFormat(totalSubProfit));			//????????????
			order.setProfitTop(StringUtil.nullToDoubleFormat(totalTopProfit));			//????????????????????????
			order.setProductType(1);                 					        
			order.setOrderItemsList(orderItemsList);									

			// ????????????????????????
			order.setPayAmount(StringUtil.nullToDoubleFormat(payAmount));                //????????????????????????
			//?????????????????????
			order.setIsUserCoupon(isUserCoupon);  
			//????????????
			order.setPreferentialAmount(StringUtil.nullToDoubleFormat(preferentialAmount));       	
			//?????????ID
			if(isUserCoupon){
				order.setUserCouponId(userCouponId);                  	
			}

			// ???????????????????????????
			order.setConsignee(userAddress.getName()); 					// ?????????
			order.setConsigneePhone(userAddress.getMobile()); 			// ???????????????
			order.setProvinceId(userAddress.getProvinceId()); 			// ???ID
			order.setCityId(userAddress.getCityId()); 					// ???ID
			order.setAreaId(userAddress.getAreaId()); 					// ???ID
			order.setIdentityName(userAddress.getRealName()); 			// ???????????????????????????
			order.setIdentityNo(StringUtil.null2Str(userAddress.getIdentityNo()).toUpperCase());// ???????????????
			order.setIdentityFront(userAddress.getIdentityFront()); 	// ???????????????-??????
			order.setIdentityBack(userAddress.getIdentityBack()); 		// ???????????????-??????
			order.setAddress(userAddress.getAddress()); 				// ????????????

			//???????????????
			List<Long> userCartIdList = new ArrayList<Long> ();
			if(StringUtil.compareObject(BuyPostType.POST_BUY_CART_TYPE, postType)){
				userCartIdList = StringUtil.stringToLongArray(cartIds);
			}

			// ???????????????????????????????????????
			MsgModel<Void> emgModel = OrderUtil.checkOrderAmountEffective(order);
			if(!StringUtil.nullToBoolean(emgModel.getIsSucc())) {
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(emgModel.getMessage()));
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// ????????????
			order.setIsInvitationAgent(false);
			order.setCreateTime(DateUtil.getCurrentDate());
			order.setUpdateTime(order.getCreateTime());
			order = this.orderManager.saveOrder(order, childerOrderList, userCartIdList);

			try{
				// ??????redis??????
				this.userCartListByUserIdCacheManager.removeSession(userInfo.getUserId());
				this.orderListByUserIdCacheManager.removeSession(userInfo.getUserId());
			}catch(Exception e){
				e.printStackTrace();
			}

			resultMap.put("orderId", order.getOrderId());
			resultMap.put("orderNo", order.getOrderNo());
			resultMap.put("amount", StringUtil.nullToDoubleFormatStr(order.getPayAmount()));
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "????????????");			
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, this.getText("??????,????????????"));
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * ????????????
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/cancelOrder")
	public @ResponseBody Map<String, Object> cancelOrder(final HttpServletRequest request ,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));
		UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
		try{
			Order order = this.orderManager.get(orderId);
			if(order == null || order.getOrderId() == null){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "???????????????");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			if(!StringUtil.compareObject(order.getUserId(), userInfo.getUserId())) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "???????????????????????????");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			if(!StringUtil.compareObject(order.getStatus(), OrderStatus.NEW_ORDER_STATUS)){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "?????????????????????????????????");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// ????????????
			OrderUtil.orderCloseStatus(order, userInfo.getUserId(), "????????????????????????", null, null);
			
			resultMap.put("status", Constants.OrderStatus.CANCEL_ORDER_STATUS);
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "??????????????????");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put(PortalConstants.MSG, "????????????");
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * ????????????
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/deleteOrder")
	public @ResponseBody Map<String, Object> deleteOrder(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));
		try{
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);

			// ??????????????????????????????
			List<Integer> statusList = new ArrayList<Integer>();
			statusList.add(Constants.OrderStatus.OVER_ORDER_STATUS);
			statusList.add(Constants.OrderStatus.CANCEL_ORDER_STATUS);

			Order order = orderManager.get(orderId);
			if(order == null || order.getOrderId() == null){
				//???????????????
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "???????????????");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if(!statusList.contains(order.getStatus())){
				//?????????????????????
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "?????????????????????????????????");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			if(!StringUtil.compareObject(order.getUserId(), userInfo.getUserId())) {
				//????????????????????????
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "???????????????????????????");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// ????????????
			this.orderManager.deleteOrder(orderId);

			try{
				// ??????????????????
				this.orderByIdCacheManager.removeSession(orderId);
				this.orderListByUserIdCacheManager.removeSession(userInfo.getUserId());
				this.orderListByStoreIdCacheManager.removeSession(order.getStoreId());
			}catch(Exception e){
				e.printStackTrace();
			}

			resultMap.put("status", Constants.OrderStatus.CANCEL_ORDER_STATUS);
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "????????????");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "????????????");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
	
	/**
	 * ????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/queryTake")
	public @ResponseBody Map<String, Object> queryTake(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));  

		try{
			// ????????????????????????
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			// ????????????????????????
			Order order = orderManager.get(orderId);
			if(order == null || order.getOrderId() == null){
				//???????????????
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "???????????????");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if(!StringUtil.compareObject(StringUtil.nullToInteger(order.getStatus()), Constants.OrderStatus.DELIVER_ORDER_STATUS)){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "?????????????????????????????????");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			if(!StringUtil.compareObject(order.getUserId(), userInfo.getUserId())) {
				//????????????????????????
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "???????????????????????????");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			this.orderManager.updateOrderCompleteStatus(Constants.OrderStatus.OVER_ORDER_STATUS, orderId);
			try{
				// ????????????
				this.orderByIdCacheManager.removeSession(orderId);
				this.orderListByUserIdCacheManager.removeSession(order.getUserId());
				this.orderListByStoreIdCacheManager.removeSession(order.getStoreId());
			}catch(Exception e){
				e.printStackTrace();
			}

			resultMap.put("status", Constants.OrderStatus.CANCEL_ORDER_STATUS);
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "??????????????????");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "????????????");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
}