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
	 * 检查商品是否能购买
	 * 立即购买
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
		String groupProductInfo = StringUtil.nullToString(request.getParameter("groupProductInfo")).replace("\\s+", ""); //组合商品数据

		try{
			//获取用户信息
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			
//			if(!StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
//			    Date expireDate = DateUtil.getMonthAfterByDay(userInfo.getCreateTime(), 6);
//				if(expireDate.before(DateUtil.getCurrentDate())) {
//					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
//					resultMap.put(PortalConstants.MSG, "半年体验期已过，请前往购买会员才可继续下单");
//					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
//					return resultMap;
//				}
//			}

			//检查商品
			MsgModel<Product> msgModel = ProductCheckUtil.checkProduct(productId, StringUtil.nullToLong(productSpecId), groupProductInfo, number, userInfo, false);
			if(!StringUtil.nullToBoolean(msgModel.getIsSucc())){
				// 没有选择有效商品
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(msgModel.getMessage()));
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			resultMap.put("productSpecId", productSpecId);
			resultMap.put("productId", msgModel.getData().getProductId());
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "校验成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, this.getText("错误,校验失败"));
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * 检查购物车商品 
	 * 添加购物车
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
				// 没有选择记录
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "购物车记录为空");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// 查找用户购物车信息
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if(!StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
			    Date expireDate = DateUtil.getMonthAfterByDay(userInfo.getCreateTime(), 6);
				if(expireDate.before(DateUtil.getCurrentDate())) {
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "半年体验期已过，请前往购买会员才可继续下单");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
			}
			
			// 校验单个订单用户是否能购买
			MsgModel<List<Product>> productCheckModel = ProductCheckUtil.checkCartProduct(userCartIdList, userInfo, true);
			if(!StringUtil.nullToBoolean(productCheckModel.getIsSucc())){
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(productCheckModel.getMessage()));
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "校验通过");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "请求失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	
	/**
	 * 订单确认
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/orderConfrim")
	public @ResponseBody Map<String, Object> orderConfrim(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Integer postType = StringUtil.nullToInteger(request.getParameter("postType"));		//请求方式: 0:立即下单;1:购物车结算
		Long productId = StringUtil.nullToLong(request.getParameter("productId"));			//立即购买: 商品ID
		Long productSpecId = StringUtil.nullToLong(request.getParameter("productSpecId"));
		Long userCouponId = StringUtil.nullToLong(request.getParameter("userCouponId"));	//优惠券ID
		Integer number = StringUtil.nullToInteger(request.getParameter("number"));			//立即购买: 商品数量
		String cartIds = StringUtil.null2Str(request.getParameter("cartIds"));				//购物车结算: 购物车IDs
		Long addressId = StringUtil.nullToLong(request.getParameter("addressId"));			//收货地址Id: 物流方式选择用户收货地址
		String groupProductInfo = StringUtil.nullToString(request.getParameter("groupProductInfo")).replace("\\s+", ""); //组合商品数据
		UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
		
		try{

			//请求方式
			List<Integer> buyPostTypeList = new ArrayList<Integer> ();
			buyPostTypeList.add(BuyPostType.POST_BUY_QUICK_TYPE);		//立即购买
			buyPostTypeList.add(BuyPostType.POST_BUY_CART_TYPE);		//购物车结算
			if(!buyPostTypeList.contains(StringUtil.nullToInteger(postType))){
				resultMap.put(PortalConstants.MSG, "请求方式错误");
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
//			if(!StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
//			    Date expireDate = DateUtil.getMonthAfterByDay(userInfo.getCreateTime(), 6);
//				if(expireDate.before(DateUtil.getCurrentDate())) {
//					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
//					resultMap.put(PortalConstants.MSG, "半年体验期已过，请前往购买会员才可继续下单");
//					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
//					return resultMap;
//				}
//			}
			

			// 检查商品信息是否有效
			MsgModel<List<Product>> productCheckModel = ProductCheckUtil.check(postType, productId, productSpecId, groupProductInfo, number, cartIds, userInfo);
			if(!StringUtil.nullToBoolean(productCheckModel.getIsSucc())){
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(productCheckModel.getMessage()));
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}


			//检查优惠券是否有效性
			boolean isUserCoupon = false;

			// 购买商品列表
			List<Product> buyProductList = productCheckModel.getData();


			//检查用户地址信息是否存在
			MsgModel<UserAddress> uMsgModel = UserAddressUtil.getAddressByAddressId(userInfo.getUserId(), addressId);
			if(!StringUtil.nullToBoolean(uMsgModel.getIsSucc())){
				resultMap.put(PortalConstants.MSG, uMsgModel.getMessage());
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			UserAddress userAddress = uMsgModel.getData();

			//检查用户地址信息是否有效
			MsgModel<UserAddress> msgModel = UserAddressUtil.checkIsValidUserAddress(userAddress);
			if(!StringUtil.nullToBoolean(msgModel.getIsSucc())){
				resultMap.put(PortalConstants.MSG, msgModel.getMessage());
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			//订单商品ID列表
			List<Long> productIdList = new ArrayList<Long> ();

			
			
			//检查上级是否能有返利
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
				//商品数量
				int productNumber = StringUtil.nullToInteger(product.getPaymentBuyNumber());
				Double doubleProductNumber = StringUtil.nullToDouble(productNumber);
				//商品单价
				Double price = StringUtil.nullToDoubleFormat(product.getPaymentPrice());
				//商品总结
				Double productAmount = DoubleUtil.mul(price, doubleProductNumber);
				//按模版统计重量
				Double totalWeights = DoubleUtil.mul(StringUtil.nullToDouble(product.getPaymentWeigth()), doubleProductNumber); 

				//上级返利
				Double topProfit = new Double(0);
//				if(isHaveTopProfit) {
//					topProfit = DoubleUtil.mul(productAmount, TOP_PROFIT_RATE); 
//				}
				log.info("----------recommednPrice:"+product.getPriceRecommend()+"--price:"+price);

				//分享返利
				Double subProfit = new Double(0);
				if(isHaveSubProfit && !StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
//					topProfit = DoubleUtil.mul(productAmount, TOP_PROFIT_RATE); 
					Double everProfit = DoubleUtil.sub(StringUtil.nullToDoubleFormat(product.getPriceRecommend()),StringUtil.nullToDoubleFormat(product.getPriceCost()));
					subProfit = DoubleUtil.mul(everProfit, doubleProductNumber);
				}

				// 按模版计算邮费
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

				//总商品数量
				cTotalNumber += productNumber;
				//总商品金额
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

			//邮费,只有在物流方式下计算
			Double postage = 0.0D;
//			if(templateWeightsMap != null && templateWeightsMap.size() > 0){
//				for(Entry<Long, Double> postTplEntry : templateWeightsMap.entrySet()){
//					PostageVo postageVo = PostageUtil.getPostage(postTplEntry.getKey(), userAddress.getProvinceId(), postTplEntry.getValue());
//					if(postageVo == null || postageVo.getPostage() == null){
//						resultMap.put(PortalConstants.MSG, "邮费计算错误");
//						resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
//						resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
//						return resultMap;
//					}
//					//按模版计算总邮费
//					postage = DoubleUtil.add(postage, StringUtil.nullToDouble(postageVo.getPostage()));
//				}
//			}
			
			//获取免运费模板
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
				System.out.println("下单默认运费"+postage);
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

			// 使用优惠券
			Double preferentialAmount = 0.0D;
			//商品总金额
			totalProductAmount = StringUtil.nullToDoubleFormat(totalProductAmount);
			//商品总邮费
			totalPostage = StringUtil.nullToDoubleFormat(totalPostage);
			//商品总总税费
			totalTaxAmount = StringUtil.nullToDoubleFormat(totalTaxAmount);
			Double postageTaxAmount = new Double(0);

			//总订单金额
			totalOrderAmount = StringUtil.nullToDoubleFormat(DoubleUtil.add(totalProductAmount, DoubleUtil.add(totalPostage, totalTaxAmount)));
			//支付金额
			Double payAmount =  totalOrderAmount;


			//是否自己店铺下单
			Order order = new Order ();
			order.setUserId(userInfo.getUserId());					                            //买家用户ID
			order.setLoginType(StringUtil.nullToInteger(userInfo.getLoginType()));	            //用户登录类型
			order.setLevel(StringUtil.nullToInteger(userInfo.getLevel()));                      //下单用户等级
			order.setIsShareBuy(isHaveSubProfit);
			order.setTopUserId(StringUtil.nullToLong(userInfo.getTopUserId()));			      
			order.setShareUserId(shareUserId);             
			order.setStoreId(StringUtil.nullToLong(userInfo.getUserId()));                 
			order.setOrderNo(CoreInitUtil.getRandomNo());     		
			order.setStatus(OrderStatus.NEW_ORDER_STATUS);  		
			order.setProductAmount(StringUtil.nullToDoubleFormat(totalProductAmount));	//商品金额（不含邮费，不含税费）
			order.setTotalRealSellPrice(StringUtil.nullToDoubleFormat(totalSellPrice));                                //商品总拿货价
			order.setPostage(StringUtil.nullToDoubleFormat(totalPostage));    			
			order.setTax(StringUtil.nullToDoubleFormat(totalTaxAmount)); 				
			order.setPostageTax(StringUtil.nullToDoubleFormat(postageTaxAmount));      
			order.setOrderAmount(StringUtil.nullToDoubleFormat(totalOrderAmount)); 		
			order.setPayAmount(StringUtil.nullToDoubleFormat(totalOrderAmount));  		
			order.setProductNumber(totalNumber);										//商品总件数
			order.setIsDelete(false);													//订单是否隐藏		
			order.setProfitSub(StringUtil.nullToDoubleFormat(totalSubProfit));			//分享利润
			order.setProfitTop(StringUtil.nullToDoubleFormat(totalTopProfit));			//上级供应店铺利润
			order.setProductType(1);                 					        
			order.setOrderItemsList(orderItemsList);									

			// 设置是否实付金额
			order.setPayAmount(StringUtil.nullToDoubleFormat(payAmount));                //实际支付现金金额
			//订单绑定优惠券
			order.setIsUserCoupon(isUserCoupon);  
			//优惠金额
			order.setPreferentialAmount(StringUtil.nullToDoubleFormat(preferentialAmount));       	
			//优惠券ID
			if(isUserCoupon){
				order.setUserCouponId(userCouponId);                  	
			}

			// 物流方式收货人信息
			order.setConsignee(userAddress.getName()); 					// 收货人
			order.setConsigneePhone(userAddress.getMobile()); 			// 收货人电话
			order.setProvinceId(userAddress.getProvinceId()); 			// 省ID
			order.setCityId(userAddress.getCityId()); 					// 市ID
			order.setAreaId(userAddress.getAreaId()); 					// 区ID
			order.setIdentityName(userAddress.getRealName()); 			// 买家身份证真实姓名
			order.setIdentityNo(StringUtil.null2Str(userAddress.getIdentityNo()).toUpperCase());// 买家身份证
			order.setIdentityFront(userAddress.getIdentityFront()); 	// 买家身份证-正面
			order.setIdentityBack(userAddress.getIdentityBack()); 		// 买家身份证-反面
			order.setAddress(userAddress.getAddress()); 				// 收货地址

			//购物车结算
			List<Long> userCartIdList = new ArrayList<Long> ();
			if(StringUtil.compareObject(BuyPostType.POST_BUY_CART_TYPE, postType)){
				userCartIdList = StringUtil.stringToLongArray(cartIds);
			}

			// 下单前订单各个金额参数校验
			MsgModel<Void> emgModel = OrderUtil.checkOrderAmountEffective(order);
			if(!StringUtil.nullToBoolean(emgModel.getIsSucc())) {
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(emgModel.getMessage()));
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// 订单保存
			order.setIsInvitationAgent(false);
			order.setCreateTime(DateUtil.getCurrentDate());
			order.setUpdateTime(order.getCreateTime());
			order = this.orderManager.saveOrder(order, childerOrderList, userCartIdList);

			try{
				// 清除redis缓存
				this.userCartListByUserIdCacheManager.removeSession(userInfo.getUserId());
				this.orderListByUserIdCacheManager.removeSession(userInfo.getUserId());
			}catch(Exception e){
				e.printStackTrace();
			}

			resultMap.put("orderId", order.getOrderId());
			resultMap.put("orderNo", order.getOrderNo());
			resultMap.put("amount", StringUtil.nullToDoubleFormatStr(order.getPayAmount()));
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "下单成功");			
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, this.getText("错误,下单失败"));
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * 取消订单
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
				resultMap.put(PortalConstants.MSG, "订单不存在");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			if(!StringUtil.compareObject(order.getUserId(), userInfo.getUserId())) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "非自己下单不能操作");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			if(!StringUtil.compareObject(order.getStatus(), OrderStatus.NEW_ORDER_STATUS)){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单类型错误，不能取消");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// 订单关闭
			OrderUtil.orderCloseStatus(order, userInfo.getUserId(), "用户手动取消订单", null, null);
			
			resultMap.put("status", Constants.OrderStatus.CANCEL_ORDER_STATUS);
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "取消付款成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put(PortalConstants.MSG, "请求失败");
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * 删除订单
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

			// 订单状态支持删除功能
			List<Integer> statusList = new ArrayList<Integer>();
			statusList.add(Constants.OrderStatus.OVER_ORDER_STATUS);
			statusList.add(Constants.OrderStatus.CANCEL_ORDER_STATUS);

			Order order = orderManager.get(orderId);
			if(order == null || order.getOrderId() == null){
				//订单不存在
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单不存在");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if(!statusList.contains(order.getStatus())){
				//订单状态不匹配
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单类型错误，不能取消");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			if(!StringUtil.compareObject(order.getUserId(), userInfo.getUserId())) {
				//订单权限操作错误
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "非自己下单不能操作");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 隐藏订单
			this.orderManager.deleteOrder(orderId);

			try{
				// 清除缓存信息
				this.orderByIdCacheManager.removeSession(orderId);
				this.orderListByUserIdCacheManager.removeSession(userInfo.getUserId());
				this.orderListByStoreIdCacheManager.removeSession(order.getStoreId());
			}catch(Exception e){
				e.printStackTrace();
			}

			resultMap.put("status", Constants.OrderStatus.CANCEL_ORDER_STATUS);
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "删除成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "请求失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
	
	/**
	 * 确认收货
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/queryTake")
	public @ResponseBody Map<String, Object> queryTake(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));  

		try{
			// 检查店铺是否有效
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			// 检查订单是否有效
			Order order = orderManager.get(orderId);
			if(order == null || order.getOrderId() == null){
				//订单不存在
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单不存在");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if(!StringUtil.compareObject(StringUtil.nullToInteger(order.getStatus()), Constants.OrderStatus.DELIVER_ORDER_STATUS)){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单类型错误，不能取消");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			if(!StringUtil.compareObject(order.getUserId(), userInfo.getUserId())) {
				//订单权限操作错误
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "非自己下单不能操作");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			this.orderManager.updateOrderCompleteStatus(Constants.OrderStatus.OVER_ORDER_STATUS, orderId);
			try{
				// 更新缓存
				this.orderByIdCacheManager.removeSession(orderId);
				this.orderListByUserIdCacheManager.removeSession(order.getUserId());
				this.orderListByStoreIdCacheManager.removeSession(order.getStoreId());
			}catch(Exception e){
				e.printStackTrace();
			}

			resultMap.put("status", Constants.OrderStatus.CANCEL_ORDER_STATUS);
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "确认收货成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "请求失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
}