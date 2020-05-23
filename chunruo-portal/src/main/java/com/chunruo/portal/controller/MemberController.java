package com.chunruo.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.cache.portal.impl.OrderListByStoreIdCacheManager;
import com.chunruo.cache.portal.impl.OrderListByUserIdCacheManager;
import com.chunruo.cache.portal.impl.UserInfoByIdCacheManager;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.Constants.PayWay;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.interceptor.LoginInterceptor;
import com.chunruo.portal.util.OrderUtil;
import com.chunruo.portal.util.PortalUtil;

@Controller
@RequestMapping("/api/member/")
public class MemberController  extends BaseController{
	
	
	private final static Double DEFAULT_VIP_AMOUNT = 288D;
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private UserInfoByIdCacheManager userInfoByIdCacheManager;
	@Autowired
	private OrderListByUserIdCacheManager orderListByUserIdCacheManager;
	@Autowired
	private OrderListByStoreIdCacheManager orderListByStoreIdCacheManager;
	
	/**
	 * 购买会员
	 * @param request
	 * @param response
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/purchaseMember")
	public @ResponseBody Map<String, Object> purchaseMember(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long topUserId = StringUtil.nullToLong(request.getParameter("topUserId"));			//分享人id
		Integer payWay = StringUtil.nullToInteger(request.getParameter("payWay"));          //支付方式：0：现金支付 1：账户余额支付
		Double payAccountAmount = StringUtil.nullToDoubleFormat(request.getParameter("payAccountAmount"));//账户余额支付金额
		
		try {
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			
			
			if(StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
				resultMap.put(PortalConstants.MSG, "你已是VIP，请勿再次购买");
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			
			//分享人返利
			Double topProfit = 0D;
			UserInfo topUserInfo = this.userInfoByIdCacheManager.getSession(topUserId);
			if(topUserInfo != null && topUserInfo.getUserId() != null
					&& StringUtil.compareObject(topUserInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
				topProfit = DoubleUtil.divide(DEFAULT_VIP_AMOUNT, 2d);
			}
		
			Double payAmount = DEFAULT_VIP_AMOUNT;
			//支付金额
			Double realPayAccountAmount = new Double(0);
		    if(StringUtil.compareObject(payWay, PayWay.PAY_WAY_ACCOUNT)) {
		    	// 检查订单账户余额支付
				MsgModel<Double> asgModel = OrderUtil.checkOrderUserAccount(userInfo.getUserId(), payAmount, payAccountAmount, OrderUtil.CHECK_ACCOUNT_ORDER_CREATE, null);
				if(!StringUtil.nullToBoolean(asgModel.getIsSucc())) {
					resultMap.put(PortalConstants.MSG, asgModel.getMessage());
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;	
				}
				
				// 重新计算使用账号余额和实际订单实付金额
				payAmount = asgModel.getData();
				realPayAccountAmount = asgModel.getPreferentialAmount();
		    }
			
			List<OrderItems> orderItemsList = new ArrayList<OrderItems> ();
			OrderItems orderItems = new OrderItems ();
			orderItems.setSort(0);													//子订单排序
			orderItems.setProductId(-1L); 			                            //批发市场ID
			orderItems.setProductName("VIP商品"); 			                       //商品名称
//			orderItems.setProductCode(productCode);                                 //商品编码
//			orderItems.setProductSku(productSku);                                   //商品SKU
//			orderItems.setWareHouseId(warehouseId);  					            //所属仓库ID
			orderItems.setQuantity(1);								                //订单数量	
			orderItems.setPrice(payAmount);										    //商品单价格
			orderItems.setProfit(0.0D); 											//上级供应店铺利润
			orderItems.setTopProfit(topProfit); 									//上级返利
			orderItems.setPriceWholesale(payAmount);				                    //分销市场价格
			orderItems.setPriceCost(payAmount);						                //供货商成本价
			orderItems.setTax(0.0D);							                    //增值税
			orderItems.setAmount(payAmount); 								            //商品总金额（不含税费）
			orderItems.setDiscountAmount(payAmount);				                    //折后商品总金额（不含税费）
			orderItems.setIsSpceProduct(false);			                            //是否规格商品
			orderItems.setIsMoreSpecProduct(false);	                                //是否多规格商品
			orderItems.setIsSeckillProduct(false);			                        //是否秒杀商品
			orderItems.setCreateTime(DateUtil.getCurrentDate());
			orderItems.setUpdateTime(orderItems.getCreateTime());
			orderItemsList.add(orderItems);
			
			// 订单信息
			Order order = new Order ();
			order.setUserId(userInfo.getUserId());						//买家用户ID
			order.setTopUserId(topUserId);					            //上线店铺ID
			order.setStoreId(userInfo.getUserId());                     //店铺ID
			order.setLevel(StringUtil.nullToInteger(userInfo.getLevel()));//用户等级
			order.setIsShareBuy(false);                                 //是否分享购买
			order.setIsUserCoupon(false);								//是否使用优惠券
			order.setOrderNo(CoreInitUtil.getRandomNo());     			//订单号date('YmdHis') . mt_rand(100000, 999999)
			order.setIsPushErp(false);									//ERP订单状态
			order.setStatus(OrderStatus.NEW_ORDER_STATUS);  			//订单状态(1:未支付;2:未发货;3:已发货;4:已完成;5:已取消;6:退款中;7:买家确认收货)
			order.setProductAmount(payAmount);					        //商品金额（不含邮费，不含税费）
			order.setPostage(StringUtil.nullToDouble(0));    			//邮费
			order.setTax(0.0D); 					                    //增值税
			order.setOrderAmount(payAmount); 					        //订单金额（含邮费,含税费）
			order.setPayAmount(payAmount); 					            //订单金额（含邮费，含税费）
			order.setProductNumber(1);					                //商品总件数
			order.setIsMyselfStore(true);                               //是否自己店铺订单
			order.setIsDelete(false);									//订单是否隐藏		
			order.setIsIntercept(false); 								//是否拦截
			order.setIsInvitationAgent(true); 							//升级代理或代理续费订单
			order.setIsSeckillProduct(false);  							//是否秒杀商品订单
			order.setProfitTop(StringUtil.nullToDoubleFormat(topProfit));//上级返利
			order.setMemberUserLevel(UserLevel.USER_LEVEL_DEALER);
		    // 设置是否账户余额支付
 			order.setPayAccountAmount(StringUtil.nullToDoubleFormat(realPayAccountAmount));//账户余额支付金额
 			order.setIsUseAccount(StringUtil.nullToBoolean(payWay));
		    order.setOrderItemsList(orderItemsList);				     //订单商品列表
		
	
			// 下单前订单各个金额参数校验
			MsgModel<Void> emgModel = OrderUtil.checkOrderAmountEffective(order);
			if(!StringUtil.nullToBoolean(emgModel.getIsSucc())) {
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(emgModel.getMessage()));
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// 订单保存
			order.setIsFriendPayment(false);
			order.setIsInvitationAgent(true);
			order.setIsNoStoreBuyAgent(false);
			order.setIsNeedCheckPayment(false);
			order.setIsSplitSingle(false);
			order.setCreateTime(DateUtil.getCurrentDate());
			order.setUpdateTime(order.getCreateTime());
			order = this.orderManager.saveOrder(order, null, null);
	
			try{
				// 清除redis缓存
				this.orderListByUserIdCacheManager.removeSession(userInfo.getUserId());
				this.orderListByStoreIdCacheManager.removeSession(userInfo.getUserId());
			}catch(Exception e){
				e.printStackTrace();
			}
			
			resultMap.put("orderId", order.getOrderId());
			resultMap.put("orderNo", order.getOrderNo());
			resultMap.put("amount", StringUtil.nullToDoubleFormatStr(payAmount));
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "下单成功");			
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
					
		}catch(Exception e) {
			e.printStackTrace();
		}
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, this.getText("下单失败"));
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
}
