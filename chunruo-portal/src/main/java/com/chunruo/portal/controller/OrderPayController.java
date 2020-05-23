package com.chunruo.portal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.Constants.PaymentType;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.OrderPaymentRecord;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.OrderPaymentRecordManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.interceptor.LoginInterceptor;
import com.chunruo.portal.util.OrderUtil;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.ProductCheckUtil;
import com.chunruo.portal.util.ProductUtil;
import com.chunruo.portal.util.RequestUtil;
import com.chunruo.portal.util.WeiXinPayUtil;

@Controller
@RequestMapping("/api/pay/")
public class OrderPayController extends BaseController{
	protected final static Log log = LogFactory.getLog(OrderPayController.class);
	public static ThreadLocal<Long> ORDER_PAY_LOCAL = new ThreadLocal<Long>();
	@Autowired
	private OrderPaymentRecordManager orderPaymentRecordManager;

	/**
	 * 检查订单支付类型
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/getOrderPaymentType")
	public @ResponseBody Map<String, Object> getOrderPaymentType(final HttpServletRequest request, final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));
		try{
			// 检查支付订单状态
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			MsgModel<Order> msgModel = OrderPayController.checkPaymentOrderStatus(userInfo, orderId);
			if(!StringUtil.nullToBoolean(msgModel.getIsSucc())) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, msgModel.getMessage());
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 获取订单信息
			Order order = msgModel.getData();

			resultMap.put("orderId", order.getOrderId());
			resultMap.put("orderNo", order.getOrderNo());
			resultMap.put("amount", StringUtil.nullToDoubleFormatStr(order.getPayAmount()));
			resultMap.put("isNeedCheckPayment", StringUtil.booleanToInt(order.getIsNeedCheckPayment()));
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "获取支付类型成功");			
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, this.getText("获取支付类型失败"));
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * 订单支付支付
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/orderPay")
	public @ResponseBody Map<String, Object> orderPay(final HttpServletRequest request ,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));

		try{
			// 检查支付订单状态
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			MsgModel<Order> osgModel = OrderPayController.checkPaymentOrderStatus(userInfo, orderId);
			if(!StringUtil.nullToBoolean(osgModel.getIsSucc())) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, osgModel.getMessage());
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 获取订单信息
			String body = "订单支付";
			Order order = osgModel.getData();
			String outTradeNo = order.getOrderNo();
			String clientIp = RequestUtil.getClientIp(request);
			List<OrderItems> orderItemsList = order.getOrderItemsList();

			// 是否微信请求
			Integer paymentType = 0;
			Long weChatConfigId = null;
			String paymentRequestData = "";
			WeChatAppConfig weChatAppConfig = null;
			int orderTotal = WeiXinPayUtil.orderAmountToBranch(order.getPayAmount());
			String notifyURL = RequestUtil.getRequestURL(request) + "/clt/order/wxpayNotify.msp";
			MsgModel<Map<String, String>> xmsgModel = new MsgModel<Map<String, String>>();

			// 微信支付
			String openId = StringUtil.null2Str(userInfo.getOpenId());

			String tradeType = null;
			// 检查是否微信PC端扫描支付
			Long productId = StringUtil.nullToLong(orderItemsList.get(0).getProductId());
			weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(Constants.MINI_PROGRAM_WECHAT_CONFIG_ID);
			xmsgModel = WeiXinPayUtil.getWeixinH5PayInfo(weChatAppConfig, outTradeNo, openId, notifyURL, orderTotal, body, clientIp, tradeType, productId);
			if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())) {
				// 二次请求生成支付单信息
				xmsgModel = WeiXinPayUtil.getWeixinH5PayInfo(weChatAppConfig, outTradeNo, openId, notifyURL, orderTotal, body, clientIp, tradeType, productId);
				if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())) {
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, xmsgModel.getMessage());
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
			}

			resultMap.put("orderPayInfo", xmsgModel.getData());
			paymentRequestData = StringUtil.null2Str(xmsgModel.getPaymentBody());
			paymentType = PaymentType.PAYMENT_TYPE_WECHAT;
			weChatConfigId = weChatAppConfig.getConfigId();
			
			log.info(String.format("[orderPayInfo=%s]", resultMap.get("orderPayInfo").toString()));

			//记录客户端调用支付方式
			if(!StringUtil.compareObject(paymentType, PaymentType.PAYMENT_TYPE_FRIEND)) {
				OrderPaymentRecord record = new OrderPaymentRecord();
				record.setOrderId(orderId);
				record.setOrderNo(order.getOrderNo());
				record.setPaymentType(paymentType);
				record.setWeChatConfigId(weChatConfigId);
				record.setIsPaymentSucc(false);
				record.setIsFriendPay(false); // 非分享朋友代付
				record.setRequestData(StringUtil.null2Str(paymentRequestData));
				record.setCreateTime(DateUtil.getCurrentDate());
				record.setUpdateTime(record.getCreateTime());
				record.setSyncNumber(0);
				this.orderPaymentRecordManager.saveOrderPaymentRecord(record);
			}

			resultMap.put("orderId", orderId);
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "拉取支付信息成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			//手动清楚当前线程副本数据（tomcat线程池中由于线程复用，线程本地数据不会马上清除）
			OrderPayController.ORDER_PAY_LOCAL.remove();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, this.getText("获取支付失败"));
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	
	
	/**
	 * 获取其他支付类型
	 * @param userInfo
	 * @param order
	 * @return
	 */
	public static MsgModel<Order> checkPaymentOrderStatus(UserInfo userInfo, Long orderId) {
		return OrderPayController.checkPaymentOrderStatus(userInfo, orderId, false);
	}

	/**
	 * 获取其他支付类型
	 * @param userInfo
	 * @param orderId
	 * @param isIgnoreUserInfo
	 * @return
	 */
	public static MsgModel<Order> checkPaymentOrderStatus(UserInfo userInfo, Long orderId, boolean isIgnoreUserInfo) {
		MsgModel<Order> msgModel = new MsgModel<Order> ();
		try {
			OrderManager orderManager = Constants.ctx.getBean(OrderManager.class);
			UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);
			
			Order order = orderManager.getOrderByOrderId(orderId);
			if(order == null || order.getOrderId() == null){
				//订单不存在
				msgModel.setIsSucc(false);
				msgModel.setMessage("订单不存在");
				return msgModel;
			}
			
			// 无论成功与否都返回订单详情
			msgModel.setData(order);
			
			// 非忽略传参用户信息,需要根据订单找用户信息
			if(StringUtil.nullToBoolean(isIgnoreUserInfo)) {
				userInfo = userInfoManager.get(order.getUserId());
			}

			// 检查用户权限和订单状态
			if(!StringUtil.compareObject(order.getUserId(), userInfo.getUserId())){
				//订单权限操作错误
				msgModel.setIsSucc(false);
				msgModel.setMessage("订单权限操作错误");
				return msgModel;
			}else if(!StringUtil.compareObject(order.getStatus(), OrderStatus.NEW_ORDER_STATUS)){
				//订单状态错误
				msgModel.setIsSucc(false);
				msgModel.setMessage("订单状态错误");
				return msgModel;
			}

			// 检查订单商品信息是否有效
			List<OrderItems> orderItemsList = order.getOrderItemsList();
			if(orderItemsList == null || orderItemsList.size() <= 0){
				//订单商品信息不存在
				msgModel.setIsSucc(false);
				msgModel.setMessage("订单商品信息不存在");
				return msgModel;
			}

			//标记当前线程付款orderId
			OrderPayController.ORDER_PAY_LOCAL.set(orderId);  

			// 检查订单的每个商品信息是否有效
			Double totalPayAmount = StringUtil.nullToDouble(DoubleUtil.add(StringUtil.nullToDouble(order.getPostage()), StringUtil.nullToDouble(order.getPostageTax())));
			for(OrderItems orderItems : orderItemsList){
				//折后商品总金额
				totalPayAmount = DoubleUtil.add(totalPayAmount, orderItems.getDiscountAmount());

				// 已下单未支付,只需要判断批发市场是否有货
				Long productId = orderItems.getProductId();
				Long productSpecId = orderItems.getProductSpecId();
				int number = orderItems.getQuantity();

				if(!StringUtil.nullToBoolean(order.getIsInvitationAgent())){
					// 非邀请商品商品检查库存信息
					if(StringUtil.nullToBoolean(orderItems.getIsGroupProduct())){
						// 检查组合商品本身是否已下架
						MsgModel<Product> xsgModel = ProductUtil.getProductByUserLevel(orderItems.getGroupProductId(), userInfo, true);
						if(!StringUtil.nullToBoolean(xsgModel.getIsSucc())){
							msgModel.setIsSucc(false);
							msgModel.setMessage(xsgModel.getMessage());
							return msgModel;
						}

						// 检查组合商品每项子商品是否有效
						MsgModel<Product> csgModel = ProductCheckUtil.checkProduct(productId, productSpecId, null, number, userInfo, true);
						if(!StringUtil.nullToBoolean(csgModel.getIsSucc())){
							msgModel.setIsSucc(false);
							msgModel.setMessage(csgModel.getMessage());
							return msgModel;
						}
					}else{
						MsgModel<Product> csgModel = ProductCheckUtil.checkProduct(productId, productSpecId, null, number, userInfo);
						if(!StringUtil.nullToBoolean(csgModel.getIsSucc())){
							msgModel.setIsSucc(false);
							msgModel.setMessage(csgModel.getMessage());
							return msgModel;
						}
					}
				}
			}

			// 检查支付金额和商品折后总金额相加是否相等
			Double payAmount = StringUtil.nullToDoubleFormat(order.getPayAmount());
			if(StringUtil.nullToBoolean(order.getIsUseAccount())) {
				Double payAccountAmount = StringUtil.nullToDoubleFormat(order.getPayAccountAmount());
				payAmount = DoubleUtil.add(payAmount, payAccountAmount);
				
				// 检查订单账户余额支付
				MsgModel<Double> asgModel = OrderUtil.checkOrderUserAccount(userInfo.getUserId(), null, payAccountAmount, OrderUtil.CHECK_ACCOUNT_ORDER_QUERY, order.getOrderId());
				if(!StringUtil.nullToBoolean(asgModel.getIsSucc())) {
					// 账户余额误差3分内
					msgModel.setIsSucc(false);
					msgModel.setMessage(asgModel.getMessage());
					return msgModel;
				}
			}

			if(!StringUtil.doubleTowValueBetween(totalPayAmount, payAmount, Double.valueOf(1))){
				//订单支付金额异常
				msgModel.setIsSucc(false);
				msgModel.setMessage("订单支付金额异常");
				return msgModel;
			}

			// 检查数据订单列表商品是否有效购买
			if(!StringUtil.nullToBoolean(order.getIsInvitationAgent())) {
				MsgModel<Integer> csgModel = ProductUtil.checkOrderItems(orderItemsList);
				if(!StringUtil.nullToBoolean(csgModel.getIsSucc())){
					msgModel.setIsSucc(false);
					msgModel.setMessage(csgModel.getMessage());
					return msgModel;
				}
			}

			msgModel.setIsSucc(true);
			return msgModel;
		}catch(Exception e) {
			e.printStackTrace();

			msgModel.setMessage("订单支付状态检查异常");
			msgModel.setIsSucc(false);
			return msgModel;
		}finally {
			//手动清楚当前线程副本数据（tomcat线程池中由于线程复用，线程本地数据不会马上清除）
			OrderPayController.ORDER_PAY_LOCAL.remove();
		}
	}

	
}
