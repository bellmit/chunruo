package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.chunruo.cache.portal.impl.OrderByIdCacheManager;
import com.chunruo.cache.portal.impl.OrderListByStoreIdCacheManager;
import com.chunruo.cache.portal.impl.OrderListByUserIdCacheManager;
import com.chunruo.cache.portal.impl.OrderLockStockByProductIdCacheManager;
import com.chunruo.cache.portal.impl.RefundByOrderItemIdCacheManager;
import com.chunruo.cache.portal.impl.UserCouponListByUserIdCacheManager;
import com.chunruo.cache.portal.impl.UserProfitByUserIdCacheManager;
import com.chunruo.cache.portal.impl.UserSaleRecordListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.UserCoupon;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.CouponManager;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.IKUtil;
import com.chunruo.core.util.ListPageUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.ListPageVo;
import com.chunruo.core.vo.MsgModel;
public class OrderUtil {
	protected final static Log log = LogFactory.getLog(OrderUtil.class);
	public final static int CHECK_ACCOUNT_ORDER_TAG = 1;	// tag查询用户可用余额
	public final static int CHECK_ACCOUNT_ORDER_CREATE = 2;	// 创建订单校验提交余额是否有效
	public final static int CHECK_ACCOUNT_ORDER_QUERY = 3;	// 已创建订单检查余额是否有效
	
	/**
	 * 下单前订单各个金额参数校验
	 * @param order
	 * @return
	 */
	public static MsgModel<Void> checkOrderAmountEffective(Order order){
		MsgModel<Void> msgModel = new MsgModel<Void> ();
		try {
			Double orderAmount = StringUtil.nullToDoubleFormat(order.getOrderAmount());
			Double payAmount = StringUtil.nullToDoubleFormat(order.getPayAmount());
			Double preferentialAmount = StringUtil.nullToDoubleFormat(order.getPreferentialAmount());
			Double payAccountAmount = StringUtil.nullToDoubleFormat(order.getPayAccountAmount());
			
			// 检查金额不能为负数
			if(StringUtil.nullToDouble(payAmount).compareTo(0.0d) < 0) {
				msgModel.setIsSucc(false);
				msgModel.setMessage("下单实付金额计算错误");
				return msgModel;
			}else if(StringUtil.nullToDouble(preferentialAmount).compareTo(0.0d) < 0) {
				msgModel.setIsSucc(false);
				msgModel.setMessage("下单优惠券金额计算错误");
				return msgModel;
			}else if(StringUtil.nullToDouble(payAccountAmount).compareTo(0.0d) < 0) {
				msgModel.setIsSucc(false);
				msgModel.setMessage("下单账号金额计算错误");
				return msgModel;
			}
			
			// 订单金额与其他规则让利总和比较范围1元内正常
			Double totalAmount = DoubleUtil.add(payAmount, DoubleUtil.add(preferentialAmount, payAccountAmount));
			if(StringUtil.doubleTowValueBetween(totalAmount, orderAmount, Double.valueOf(1.0))) {
				msgModel.setIsSucc(true);
				return msgModel;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		msgModel.setMessage("下单金额计算错误");
		return msgModel;
	}

	/**
	 * 检查订单账户余额支付
	 * @param userId
	 * @param payAmount
	 * @param payAccountAmount
	 * @param isCreateOrder
	 * @param orderId
	 * @return
	 */
	public static MsgModel<Double> checkOrderUserAccount(Long userId, Double payAmount, Double payAccountAmount, int checkType, Long orderId){
		MsgModel<Double> msgModel = new MsgModel<Double> ();
		try {
			OrderManager orderManager = Constants.ctx.getBean(OrderManager.class);
			UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);

			// 检查用户是否有效
			UserInfo userInfo = userInfoManager.get(userId);
			if(userInfo == null || userInfo.getUserId() == null) {
				msgModel.setIsSucc(false);
				msgModel.setMessage("订单用户信息不存在错误");
				return msgModel;
			}
			
			// 如果是新创建订单则需要查询所有已锁定余额
			if(!StringUtil.compareObject(checkType, OrderUtil.CHECK_ACCOUNT_ORDER_QUERY)) {
				orderId = null;
			}else{
				// 检查订单是否存在
				Order order = orderManager.get(orderId);
				if(order == null || order.getOrderId() == null) {
					msgModel.setIsSucc(false);
					msgModel.setMessage("账号余额支付订单不存在");
					return msgModel;
				}else if(StringUtil.nullToBoolean(order.getIsPaymentSucc()) 
						&& StringUtil.compareObject(order.getStatus(), OrderStatus.CANCEL_ORDER_STATUS)) {
					// 订单已支付且已关闭,说明余额使用错误并发起退款
					msgModel.setIsSucc(false);
					msgModel.setIsExpire(true);
					msgModel.setMessage("订单已关闭");
					return msgModel;
				}
			}

			// 订单可用账号余额
			msgModel.setData(StringUtil.nullToDoubleFormat(payAmount));
			msgModel.setAccountAmount(StringUtil.nullToDoubleFormat(0));
			msgModel.setPreferentialAmount(StringUtil.nullToDoubleFormat(0));
			msgModel.setIsSucc(true);
			return msgModel;
		}catch(Exception e) {
			e.printStackTrace();

			msgModel.setIsSucc(false);
			msgModel.setMessage("账户余额校验失败,请联系客服");
			return msgModel;
		}
	}

	/**
	 * 订单关闭
	 * @param order
	 */
	public static void orderCloseStatus(Order order, Long userId, String message, Refund refund, Long reasonId){
		OrderManager orderManager = Constants.ctx.getBean(OrderManager.class);
		OrderByIdCacheManager orderByIdCacheManager = Constants.ctx.getBean(OrderByIdCacheManager.class);
		OrderListByUserIdCacheManager orderListByUserIdCacheManager = Constants.ctx.getBean(OrderListByUserIdCacheManager.class);
		OrderListByStoreIdCacheManager orderListByStoreIdCacheManager = Constants.ctx.getBean(OrderListByStoreIdCacheManager.class);
		UserProfitByUserIdCacheManager userProfitByUserIdCacheManager = Constants.ctx.getBean(UserProfitByUserIdCacheManager.class);
		OrderLockStockByProductIdCacheManager orderLockStockByProductIdCacheManager = Constants.ctx.getBean(OrderLockStockByProductIdCacheManager.class);
		UserSaleRecordListByUserIdCacheManager userSaleRecordListByUserIdCacheManager = Constants.ctx.getBean(UserSaleRecordListByUserIdCacheManager.class);
		
		try{
			List<Long> lockStockProductIdList = orderManager.updateOrderCloseStatus(order.getOrderId(), message, userId, refund, reasonId);
			try{
				// 检查是否秒杀订单
				if((StringUtil.nullToBoolean(order.getIsSeckillProduct())
						|| StringUtil.nullToBoolean(order.getIsLevelLimitProduct()))
						&& lockStockProductIdList != null
						&& lockStockProductIdList.size() > 0){
					for(Long lockStockProductId : lockStockProductIdList){
						try{
							// 秒杀锁库存更新
							orderLockStockByProductIdCacheManager.removeSession(lockStockProductId);
						}catch(Exception e){
							continue;
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try{
				// 更新缓存
				orderByIdCacheManager.removeSession(order.getOrderId());
				
				if(StringUtil.nullToBoolean(order.getIsPaymentSucc())) {
					// 更新店铺销售记录
					userSaleRecordListByUserIdCacheManager.removeSession(order.getStoreId());
					// 上级店铺返利信息
					if(StringUtil.nullToDouble(order.getProfitTop()).compareTo(0.0d) > 0) {
						userProfitByUserIdCacheManager.removeSession(order.getTopUserId());
					}
					// 分享订单需要更新店铺返利信息
					if(StringUtil.nullToBoolean(order.getIsShareBuy())) {
						// 更新分享店铺利润
						if(StringUtil.nullToDouble(order.getProfitSub()).compareTo(0.0d) > 0) {
							userProfitByUserIdCacheManager.removeSession(order.getStoreId());
						}
					}
					
				}
				
				orderListByUserIdCacheManager.removeSession(order.getUserId());
				orderListByStoreIdCacheManager.removeSession(order.getStoreId());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取订单对象
	 * @param productId
	 * @return
	 */
	public static MsgModel<Order> getOrderByOrderId(Long orderId){
		MsgModel<Order> msgModel = new MsgModel<Order> ();
		try{
			OrderManager orderManager = Constants.ctx.getBean(OrderManager.class);
			OrderByIdCacheManager orderByIdCacheManager = Constants.ctx.getBean(OrderByIdCacheManager.class);
			OrderListByUserIdCacheManager orderListByUserIdCacheManager = Constants.ctx.getBean(OrderListByUserIdCacheManager.class);
			OrderListByStoreIdCacheManager orderListByStoreIdCacheManager = Constants.ctx.getBean(OrderListByStoreIdCacheManager.class);
			OrderLockStockByProductIdCacheManager orderLockStockByProductIdCacheManager = Constants.ctx.getBean(OrderLockStockByProductIdCacheManager.class);
			Order order = orderByIdCacheManager.getSession(orderId);

			if (order != null 
					&& order.getOrderId() != null
					&& !StringUtil.nullToBoolean(order.getIsDelete())) {
				//赠品不显示
				List<OrderItems> itemsList = new ArrayList<OrderItems>();
				List<OrderItems> orderItemsList = order.getOrderItemsList();
				if(orderItemsList != null && !orderItemsList.isEmpty()) {
					StringBuffer strBuffer = new StringBuffer();
					for(OrderItems orderItems : orderItemsList) {
						if(!StringUtil.nullToBoolean(orderItems.getIsGiftProduct())) {
							strBuffer.append(orderItems.getProductName() + "|");
							itemsList.add(orderItems);
						}
					}
					order.setOrderItemsList(itemsList);
					order.setProductNames(strBuffer.toString());
				}

				// 待支付订单检查是否超值
				if (StringUtil.compareObject(order.getStatus(), OrderStatus.NEW_ORDER_STATUS)) {
					// 秒杀订单、等级限购订单
					if(StringUtil.nullToBoolean(order.getIsSeckillProduct())
							|| StringUtil.nullToBoolean(order.getIsLevelLimitProduct())){
						// 秒杀、等级限购订单支付到计时
						Integer seckillPaymentTime = StringUtil.nullToInteger(Constants.conf.getProperty("jkd.order.seckill.paymentTime"));
						Date endPaymentDate = DateUtil.getDateMinuteAfter(order.getCreateTime(), seckillPaymentTime);
						order.setEndPaymentTime(endPaymentDate.getTime());
					}else{
						// 普通订单支付到计时
						Integer commonPaymentTime = StringUtil.nullToInteger(Constants.conf.getProperty("chunruo.order.notpayment.paymentTime"));
						Date endPaymentDate = DateUtil.getDateMinuteAfter(order.getCreateTime(), commonPaymentTime);
						order.setEndPaymentTime(endPaymentDate.getTime());
					}

					// 检查时间是否已超时
					Long currentTimeMillis = System.currentTimeMillis();
					if(order.getEndPaymentTime() < currentTimeMillis){
						try{
							// 订单成功
							order.setStatus(OrderStatus.CANCEL_ORDER_STATUS);
							List<Long> lockStockProductIdList = orderManager.updateOrderCloseStatus(order.getOrderId(), "支付超时取消订单成功", order.getUserId(), null, null);
							try{
								// 检查是否秒杀订单
								if(StringUtil.nullToBoolean(order.getIsSeckillProduct())
										&& lockStockProductIdList != null
										&& lockStockProductIdList.size() > 0){
									for(Long lockStockProductId : lockStockProductIdList){
										try{
											orderLockStockByProductIdCacheManager.removeSession(lockStockProductId);
										}catch(Exception e){
											continue;
										}
									}
								}
							}catch(Exception e){
								e.printStackTrace();
							}

							// 清除缓存信息
							orderByIdCacheManager.removeSession(order.getOrderId());
							orderListByUserIdCacheManager.removeSession(order.getUserId());
							orderListByStoreIdCacheManager.removeSession(order.getStoreId());
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}

				// 组合商品清除缓存
				if (StringUtil.nullToBoolean(order.getIsGroupProduct())) {
					try {
						orderByIdCacheManager.removeSession(order.getOrderId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				msgModel.setData(order);
				msgModel.setIsSucc(true);
				return msgModel;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		msgModel.setMessage("查询订单不存在");
		msgModel.setIsSucc(false);
		return msgModel;
	}

	/**
	 * 订单历史记录
	 * @param orderIdMap
	 * @param status
	 * @param pageidx
	 * @param pagesize
	 * @param lastId
	 * @return
	 */
	public static ListPageVo<List<Order>> getOrderListPageVO(List<Order> orderList, Integer status, Integer pageidx, Integer pagesize, Long lastId, final UserInfo userInfo,String keyword) {
		ListPageVo<List<Order>> listPageVo = new ListPageVo<List<Order>> ();
		try{
			List<Long> orderIdList = new ArrayList<Long>();
			final RefundByOrderItemIdCacheManager refundByOrderItemIdCacheManager = Constants.ctx.getBean(RefundByOrderItemIdCacheManager.class);

			// 排序
			if (orderList != null && orderList.size() > 0) {
				List<Integer> orderStatusList = new ArrayList<Integer>();
				orderStatusList.add(OrderStatus.UN_DELIVER_ORDER_STATUS);  	//未发货
				orderStatusList.add(OrderStatus.DELIVER_ORDER_STATUS);  	//已发货
				orderStatusList.add(OrderStatus.OVER_ORDER_STATUS);     	//已完成
				for (Order order : orderList) {
					if (!StringUtil.compareObject(status, "0") && !StringUtil.compareObject(status, order.getStatus())
							|| StringUtil.nullToBoolean(order.getIsInvitationAgent())) {
						// 订单自动按状态分类
						continue;
					}

					// 关键字匹配
					if (!StringUtil.isNull(keyword)) {
						boolean isMathcKeyword = false;
						//搜索词
						List<String> keywordList = IKUtil.getKeywordList(keyword);
						//检查收货人，号码是否匹配
						if(StringUtil.compareObject(order.getConsigneePhone(), keyword)
								|| StringUtil.compareObject(order.getOrderNo(), keyword)
								|| StringUtil.null2Str(order.getConsignee()).contains(keyword)) {
							isMathcKeyword = true;
						}

						//检查商品名称是否匹配搜索词
						if(StringUtil.null2Str(order.getProductNames()).toLowerCase().contains(keyword)) {
							isMathcKeyword = true;
						}else if(keywordList != null && keywordList.size() > 0) {
							for(String word : keywordList) {
								if(StringUtil.null2Str(order.getProductNames()).toLowerCase().contains(word)) {
									isMathcKeyword = true;
									break;
								}
							}
						}

						if(!isMathcKeyword) {
							//该订单未匹配到搜索关键字
							continue;
						}
					}
					orderIdList.add(order.getOrderId());
				}
			}

			/**
			 * 自动List分页工具
			 */
			ListPageUtil<Order> pageUtil = new ListPageUtil<Order>() {
				@Override
				public Order addObject(Long objectId) {
					MsgModel<Order> msgModel = OrderUtil.getOrderByOrderId(objectId);
					if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
						Order order = msgModel.getData();
						if(order != null && order.getOrderId() != null) {
							//老订单实付金额赋值
							if(StringUtil.nullToDouble(order.getPayAmount()).compareTo(0.0D) == 0){
								order.setPayAmount(StringUtil.nullToDouble(order.getOrderAmount()) - StringUtil.nullToDouble(order.getPreferentialAmount()));
							}

							// 校验订单商品明细
							if(order.getOrderItemsList() != null && order.getOrderItemsList().size() > 0) {
								Integer totalNumber = 0;
								for (OrderItems orderItem : order.getOrderItemsList()) {
									orderItem.setIsSoldout(Constants.NO);
									Integer quantity = StringUtil.nullToInteger(orderItem.getQuantity());
									totalNumber = totalNumber + quantity;

									// 已下单未支付,需要判断批发市场是否有货
									if (StringUtil.compareObject(order.getStatus(), OrderStatus.NEW_ORDER_STATUS)) {
										// 非秒杀秒杀订单
										if(!StringUtil.nullToBoolean(order.getIsSeckillProduct())){
											// 订单待支付的情况下,显示商品是否有库存
											MsgModel<Product> xsgModel = ProductUtil.getProductByUserLevel(orderItem.getProductId(), userInfo, true);
											if(!StringUtil.nullToBoolean(xsgModel.getIsSucc())){
												// 判断商品状态不是上架状态,购物车统一任务是售罄状态
												orderItem.setIsSoldout(Constants.YES);
											}
										}
									}

									// 检查订单是否有退款退货记录
									Refund refund = refundByOrderItemIdCacheManager.getSession(orderItem.getItemId());
									if (refund != null && refund.getRefundId() != null) {
										orderItem.setRefundStatus(refund.getRefundStatus());
										orderItem.setRefundType(refund.getRefundType());
									}
								}
								order.setTotalNumber(totalNumber);
							}
						}
						return order;
					}
					return null;
				}
			};

			/**
			 * 返回自动分页结果
			 */
			listPageVo = pageUtil.getPageList(orderIdList, lastId, pageidx, pagesize);
		}catch(Exception e){
			e.printStackTrace();
		}
		return listPageVo;
	}

	/**
	 * 获取优惠券列表
	 * @param userCoupon
	 * @param productList
	 * @param totalAmount
	 * @return
	 */
	public static MsgModel<Coupon> checkUserCoupon(UserCoupon userCoupon, List<Product> productList, Double totalAmount){
		MsgModel<Coupon> msgModel = new MsgModel<Coupon> ();
		try{
			CouponManager couponManager = Constants.ctx.getBean(CouponManager.class);

			// 检查优惠券类型是否有效
			Coupon coupon = couponManager.get(userCoupon.getCouponId());
			if(coupon == null || coupon.getCouponId() == null){
				msgModel.setIsSucc(false);
				msgModel.setMessage("优惠券类型不存在");
				return msgModel;
			}else if(!StringUtil.nullToBoolean(userCoupon.getIsRechargeProductCoupon()) && StringUtil.nullToDoubleFormat(totalAmount) <= StringUtil.nullToDoubleFormat(coupon.getGiveAmount())){
				msgModel.setIsSucc(false);
				msgModel.setMessage("优惠券使用错误，订单金额必须大于优惠券金额");
				return msgModel;
			}

			//修改coupon为游离态
			couponManager.detach(coupon);

			// 优惠券金额
			msgModel.setData(coupon);

			boolean isRechargeProductCoupon = false;
			// 是否全场通用优惠券
			if(StringUtil.compareObject(StringUtil.nullToInteger(coupon.getAttribute()), Coupon.COUPON_ATTRIBUTE_ALL)){
				List<Long> productIdList = new ArrayList<Long> ();
				for(Product product : productList){
					productIdList.add(product.getProductId());
				}

				msgModel.setIsSucc(true);
				msgModel.setProductIdList(productIdList);
				return msgModel;
			}else if(StringUtil.compareObject(StringUtil.nullToInteger(coupon.getAttribute()), Coupon.COUPON_ATTRIBUTE_CATEGORY)){
				// 品类优惠券
				if(productList != null 
						&& productList.size() > 0
						&& StringUtil.compareObject(userCoupon.getCouponStatus(), UserCoupon.USER_COUPON_STATUS_NOT_USED)) {
					//优惠券属性详情，（一般绑定品类或商品，[1,2,3,4]以数组形式存储）
					List<Long> categoryIdList = StringUtil.stringToLongArray(coupon.getAttributeContent());
					if(categoryIdList != null && categoryIdList.size() > 0){
						// 品类优惠券
						Double categoryAmount = new Double(0);
						List<Long> productIdList = new ArrayList<Long> ();
						for(Product product : productList){
							if(StringUtil.compareObject(StringUtil.nullToInteger(coupon.getUseRangeType()), Coupon.USER_RANGE_TYPE_PRODUCT)) {
								//优惠券指定范围内的商品需满足优惠券条件
								if(categoryIdList.retainAll(product.getCategoryIdList())){
									productIdList.add(product.getProductId());
									categoryAmount = DoubleUtil.add(categoryAmount, DoubleUtil.mul(product.getPaymentPrice(), StringUtil.nullToDouble(product.getPaymentBuyNumber())));

								}
							}else {
								productIdList.add(product.getProductId());
								categoryAmount = DoubleUtil.add(categoryAmount, DoubleUtil.mul(product.getPaymentPrice(), StringUtil.nullToDouble(product.getPaymentBuyNumber())));
							}
						}

						//品类商品总金额大于等于满减金额，且大于优惠券满金额
						if(categoryAmount.compareTo(StringUtil.nullToDouble(coupon.getGiveAmount())) == 1
								&& categoryAmount.compareTo(StringUtil.nullToDouble(coupon.getFullAmount())) >= 0){
							msgModel.setIsSucc(true);
							msgModel.setProductIdList(productIdList);
							return msgModel;
						}
					}
				}

				msgModel.setIsSucc(false);
				msgModel.setMessage("品类优惠券不能使用");
				return msgModel;
			}else if(StringUtil.compareObject(StringUtil.nullToInteger(coupon.getAttribute()), Coupon.COUPON_ATTRIBUTE_PRODUCT)){
				// 商品优惠券
				if(productList != null 
						&& productList.size() > 0
						&& StringUtil.compareObject(userCoupon.getCouponStatus(), UserCoupon.USER_COUPON_STATUS_NOT_USED)) {
					//优惠券属性详情，（一般绑定品类或商品，[1,2,3,4]以数组形式存储）
					List<Long> productIdList = StringUtil.stringToLongArray(coupon.getAttributeContent());
					if(productIdList != null && productIdList.size() > 0){
						Collections.sort(productList,new Comparator<Product>() {
							@Override
							public int compare(Product o1, Product o2) {
								Double price1 = StringUtil.nullToDouble(o1.getPaymentPrice());
								Double price2 = StringUtil.nullToDouble(o2.getPaymentPrice());
								return price1.compareTo(price2);
							}
						});
						// 商品优惠券
						Double productAmount = new Double(0);
						List<Long> realProductIdList = new ArrayList<Long> ();
						for(Product product : productList){
							if(productIdList.contains(StringUtil.nullToLong(product.getProductId()))
									&& StringUtil.nullToBoolean(userCoupon.getIsRechargeProductCoupon())){
								//赠品券
								isRechargeProductCoupon = true;
								realProductIdList.add(product.getProductId());
								Double preferentialAmount = StringUtil.nullToDouble(product.getPaymentPrice());
								MsgModel<Double> taxModel = ProductUtil.getProductTax(preferentialAmount, product.getProductType(), product.getIsFreeTax());
								if(StringUtil.nullToBoolean(taxModel.getIsSucc())) {
									preferentialAmount = DoubleUtil.add(preferentialAmount, StringUtil.nullToDouble(taxModel.getData()));
								}
								coupon.setGiveAmount(preferentialAmount);
								coupon.setFullAmount(preferentialAmount);
								break;
							}
							if(StringUtil.compareObject(StringUtil.nullToInteger(coupon.getUseRangeType()), Coupon.USER_RANGE_TYPE_PRODUCT)) {
								//优惠券指定范围内的商品需满足优惠券条件
								if(productIdList.contains(StringUtil.nullToLong(product.getProductId()))){
									realProductIdList.add(product.getProductId());
									productAmount = DoubleUtil.add(productAmount, DoubleUtil.mul(product.getPaymentPrice(), StringUtil.nullToDouble(product.getPaymentBuyNumber())));
								}
							}else {
								realProductIdList.add(product.getProductId());
								productAmount = DoubleUtil.add(productAmount, DoubleUtil.mul(product.getPaymentPrice(), StringUtil.nullToDouble(product.getPaymentBuyNumber())));
							}
						}

						//商品总金额大于等于满减金额，且大于优惠券满金额
						if( (!isRechargeProductCoupon && productAmount.compareTo(StringUtil.nullToDouble(coupon.getGiveAmount())) == 1
								&& productAmount.compareTo(StringUtil.nullToDouble(coupon.getFullAmount())) >= 0)
								|| isRechargeProductCoupon){
							msgModel.setIsSucc(true);
							msgModel.setProductIdList(realProductIdList);
							return msgModel;
						}
					}
				}

				msgModel.setIsSucc(false);
				msgModel.setMessage("商品优惠券不能使用");
				return msgModel;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("优惠券不能使用");
		return msgModel;
	}

	/**
	 * 获取优惠券列表
	 * @param productCategroyIdAmountMap     订单分类Id列表
	 * @param productIdProductAmountMap   商品Id列表
	 * @param userId          用户ID
	 * @param totalAmount     订单商品总金额（不包含运费）
	 * @return
	 */
	public static Map<String, List<UserCoupon>> getUserCoupon(Map<Long,Double> productCategroyIdAmountMap, Map<Long,Double> productIdProductAmountMap, Long userId, Double totalAmount, boolean hasSeckillProduct){
		Map<String, List<UserCoupon>> resultMap = new HashMap<String, List<UserCoupon>> ();
		UserCouponListByUserIdCacheManager userCouponListByUserIdCacheManager = Constants.ctx.getBean(UserCouponListByUserIdCacheManager.class);
		try{
			//可使用优惠券列表
			List<UserCoupon> availableCouponList = new ArrayList<UserCoupon> ();
			//不可使用优惠券列表
			List<UserCoupon> unavailableCouponList = new ArrayList<UserCoupon> ();

			// 获取用户所有优惠券
			List<UserCoupon> userCouponAllList = userCouponListByUserIdCacheManager.getSession(userId);
			List<UserCoupon> userCouponList = new ArrayList<UserCoupon>();
			if(userCouponAllList != null && userCouponAllList.size() > 0) {
				for(UserCoupon userCoupon : userCouponAllList) {
					//得到所有未使用的优惠券
					if(StringUtil.compareObject(userCoupon.getCouponStatus(), UserCoupon.USER_COUPON_STATUS_NOT_USED)) {
						if(!StringUtil.nullToBoolean(userCoupon.getIsRechargeProductCoupon())
								&& (hasSeckillProduct)) {
							unavailableCouponList.add(userCoupon);
							continue;
						}
						userCouponList.add(userCoupon);
					}
				}
			}

			// 所有未使用的优惠券列表
			if(userCouponList != null && userCouponList.size() > 0){
				for(UserCoupon userCoupon : userCouponList){
					// 找出未使用的优惠券
					if(StringUtil.compareObject(userCoupon.getCouponStatus(), UserCoupon.USER_COUPON_STATUS_NOT_USED)){
						Coupon coupon = userCoupon.getCoupon();
						//格式化优惠券有效期
						userCoupon.setEffectiveTimeFormat(userCoupon.getReceiveTime().replaceAll("-", ".") + "-" + userCoupon.getEffectiveTime().replaceAll("-", "."));

						//订单商品总金额大于等于满减金额，且大于优惠券满金额
						if((totalAmount.compareTo(StringUtil.nullToDouble(coupon.getGiveAmount())) == 1
								&& totalAmount.compareTo(StringUtil.nullToDouble(coupon.getFullAmount())) >= 0
								) || StringUtil.nullToBoolean(userCoupon.getIsRechargeProductCoupon())){
							// 检查优惠券类型
							if(StringUtil.compareObject(coupon.getAttribute(), Coupon.COUPON_ATTRIBUTE_ALL)){
								//	全场通用优惠券
								availableCouponList.add(userCoupon);
							}else{
								//获取可用品类或商品Id列表
								List<Long> contentIdList = null;
								try{
									//优惠券属性详情，（一般绑定品类或商品，[1,2,3,4]以数组形式存储）
									if(!StringUtil.isNull(coupon.getAttributeContent())){
										contentIdList = StringUtil.stringToLongArray(coupon.getAttributeContent());
									}
									if(contentIdList == null || contentIdList.size() == 0){
										continue;
									}

									if(StringUtil.compareObject(coupon.getAttribute(), Coupon.COUPON_ATTRIBUTE_CATEGORY)){
										// 品类优惠券
										Double categoryAmount = new Double(0);
										Double categoryProductAmount = new Double(0);
										Double categoryOrderAmount = new Double(0);
										for(Map.Entry<Long, Double> entry : productCategroyIdAmountMap.entrySet()) {
											//可用分类优惠券
											categoryOrderAmount += StringUtil.nullToDouble(entry.getValue());
											if(contentIdList.contains(entry.getKey())) {
												categoryProductAmount = DoubleUtil.add(categoryProductAmount, StringUtil.nullToDouble(entry.getValue()));
											}
										}

										if(StringUtil.compareObject(Coupon.USER_RANGE_TYPE_PRODUCT, coupon.getUseRangeType())) {
											//所购商品在优惠券范围内的商品总价格
											categoryAmount = categoryProductAmount;
										}else if(categoryProductAmount.compareTo(0.0D) > 0){
											categoryAmount = categoryOrderAmount;
										}
										//flag 默认不可用标签
										boolean canUse = false;
										if(!StringUtil.nullToBoolean(userCoupon.getIsRechargeProductCoupon())
												&& (categoryAmount.compareTo(StringUtil.nullToDouble(coupon.getFullAmount())) >= 0)) {
											//如果购买的分类商品总金额超过优惠券满金额，才能使用此优惠券
											availableCouponList.add(userCoupon);
											canUse = true;
										}

										//不可用优惠券
										if(!canUse){
											unavailableCouponList.add(userCoupon);
										}
									}else {
										// 商品优惠券
										Double productAmount = new Double(0);
										Double productAllAmount = new Double(0);
										Double productOrderAmount = new Double(0);
										boolean isRechargeProductCoupon = false;
										for(Map.Entry<Long, Double> entry : productIdProductAmountMap.entrySet()) {
											//可用商品优惠券
											productOrderAmount += StringUtil.nullToDouble(entry.getValue());
											if(contentIdList.contains(entry.getKey())) {
												if(StringUtil.nullToBoolean(userCoupon.getIsRechargeProductCoupon())
														&& StringUtil.compareObject(userCoupon.getProductId(), entry.getKey())) {
													isRechargeProductCoupon = true;
												}
												productAllAmount = DoubleUtil.add(productAllAmount, StringUtil.nullToDouble(entry.getValue()));
											}
										}

										if(StringUtil.compareObject(Coupon.USER_RANGE_TYPE_PRODUCT, coupon.getUseRangeType())) {
											//所购商品在优惠券范围内的商品总价格
											productAmount = productAllAmount;
										}else if(productAllAmount.compareTo(0.0D) > 0){
											productAmount = productOrderAmount;
										}
										//flag 默认不可用标签
										boolean canUse = false;
										if((!isRechargeProductCoupon
												&& !StringUtil.nullToBoolean(userCoupon.getIsRechargeProductCoupon())
												&& productAmount.compareTo(StringUtil.nullToDouble(coupon.getFullAmount())) >= 0)
												|| isRechargeProductCoupon) {
											//如果购买的商品总金额超过优惠券满金额，才能使用此优惠券(此优惠券的contentId即productId才有效)
											availableCouponList.add(userCoupon);
											canUse = true;
										}

										//不可用优惠券
										if(!canUse){
											unavailableCouponList.add(userCoupon);
										}
									}
								}catch(Exception e){
									e.printStackTrace();
									continue;
								}
							}
						}else{
							//金额不满不可用优惠券
							unavailableCouponList.add(userCoupon);
						}
					}
				}
			}

			Collections.sort(availableCouponList, new Comparator<UserCoupon>() {
				public int compare(UserCoupon e1, UserCoupon e2) {
					int sort = StringUtil.booleanToInt(e1.getIsRechargeProductCoupon()).compareTo(StringUtil.booleanToInt(e2.getIsRechargeProductCoupon()));
					Double giveMoney1 = StringUtil.nullToDouble(e1.getCoupon().getGiveAmount());
					Double giveMoney2 = StringUtil.nullToDouble(e2.getCoupon().getGiveAmount());

					String effectiveTime1 = StringUtil.null2Str(e1.getEffectiveTime());
					String effectiveTime2 = StringUtil.null2Str(e2.getEffectiveTime());

					if(sort != 0) {
						return -sort;
					}
					if(giveMoney1.compareTo(giveMoney2) == 0) {
						return effectiveTime1.compareTo(effectiveTime2);
					}else {
						return -giveMoney1.compareTo(giveMoney2);
					}
				}
			});

			resultMap.put("unavailableCouponList", unavailableCouponList);
			resultMap.put("availableCouponList", availableCouponList);
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultMap;
	}
}
