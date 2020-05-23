package com.chunruo.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.cache.portal.impl.RefundByOrderItemIdCacheManager;
import com.chunruo.cache.portal.impl.RefundListByStoreIdCacheManager;
import com.chunruo.cache.portal.impl.RefundListByUserIdCacheManager;
import com.chunruo.cache.portal.impl.UserSaleRecordListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.OrderItemsManager;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.RefundManager;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.interceptor.LoginInterceptor;
import com.chunruo.portal.util.PortalUtil;

@Controller
@RequestMapping("/api/refund/")
public class RefundController extends BaseController {
	private Lock lock = new ReentrantLock();
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private OrderItemsManager orderItemsManager;
	@Autowired
	private RefundManager refundManager;
	@Autowired
	private ProductManager productManager;
	@Autowired
	private RefundListByUserIdCacheManager refundListByUserIdCacheManager;
	@Autowired
	private RefundListByStoreIdCacheManager refundListByStoreIdCacheManager;
	@Autowired
	private RefundByOrderItemIdCacheManager refundByOrderItemIdCacheManager;
	@Autowired
	private UserSaleRecordListByUserIdCacheManager userSaleRecordListByUserIdCacheManager;
	
	/**
	 * 申请退款退货
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value = LoginInterceptor.LOGIN, contType = LoginInterceptor.CONT_JOSN_TYPE)
	@RequestMapping(value = "/applyRefund")
	public @ResponseBody Map<String, Object> refund(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<>();
		Long reasonId = StringUtil.nullToLong(request.getParameter("reasonId"));
		Integer refundType = StringUtil.nullToInteger(request.getParameter("refundType"));
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));
		Long itemId = StringUtil.nullToLong(request.getParameter("itemId"));
		Double refundAmount = StringUtil.nullToDouble(request.getParameter("amount")); 			// 退款金额
		String refundExplain = StringUtil.nullToString(request.getParameter("refundExplain")); 	// 退款说明
		String image1 = StringUtil.nullToString(request.getParameter("image1"));
		String image2 = StringUtil.nullToString(request.getParameter("image2"));
		String image3 = StringUtil.nullToString(request.getParameter("image3"));

		// 加锁
		lock.lock();
		try {
			// 退货退款类型
			List<Integer> refundTypeList = new ArrayList<Integer>();
			refundTypeList.add(Refund.REFUND_TYPE_MONEY); // 退款
			refundTypeList.add(Refund.REFUND_TYPE_GOODS); // 退货退款
			refundTypeList.add(Refund.REFUND_TYPE_PART);  //部分退款
						
			// 验证登录
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if (userInfo == null || userInfo.getUserId() == null) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_NOLOGIN);
				resultMap.put(PortalConstants.MSG, "用户未登录");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if (!refundTypeList.contains(refundType)) {
				// 检查退货退款类型
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "请选择退货类型");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if (!Constants.REFUND_REASON_MAP.containsKey(reasonId)) {
				// 检查退款退货原因是否有效
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "请选择申请退货原因");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 检查订单是否有效
			Order order = this.orderManager.get(orderId);
			if (order == null || order.getOrderId() == null) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单不存在");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 订单支持退货的状态
			List<Integer> orderRefundStatusList = new ArrayList<Integer>();
			orderRefundStatusList.add(OrderStatus.UN_DELIVER_ORDER_STATUS);
			orderRefundStatusList.add(OrderStatus.DELIVER_ORDER_STATUS);
			orderRefundStatusList.add(OrderStatus.OVER_ORDER_STATUS);

			// 检查order状态是否支持退款退货
			if (!orderRefundStatusList.contains(order.getStatus())) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单当前不支持退款/退货操作");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			} else if (StringUtil.compareObject(order.getStatus(), OrderStatus.OVER_ORDER_STATUS)) {
				// 已结算订单不能退款退货
				if (StringUtil.nullToBoolean(order.getIsCheck())) {
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "订单已超过退款期限");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
			}
			
			// 退款状态
			List<Integer> refundStatusList = new ArrayList<>();
			refundStatusList.add(Refund.REFUND_STATUS_WAIT);
			refundStatusList.add(Refund.REFUND_STATUS_SUCCESS);
			refundStatusList.add(Refund.REFUND_STATUS_RECEIPT);
			
			// 检查订单商品是否有效
			OrderItems orderItems = this.orderItemsManager.get(itemId);
			if (orderItems == null || orderItems.getItemId() == null) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单商品已下架或不存在");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// 组合商品规整
			OrderItems realOrderItems = orderItems;
			
			//格式化金额，防止出现精度问题
			Double postage = StringUtil.nullToDouble(order.getPostage());
			Double amount = StringUtil.nullToDouble(realOrderItems.getAmount());
			Double tax = StringUtil.nullToDouble(realOrderItems.getTax());
			Double postageTax = StringUtil.nullToDouble(order.getPostageTax());
			Double totalTax = DoubleUtil.add(tax, postageTax);
			Double preferentialAmount = StringUtil.nullToDouble(realOrderItems.getPreferentialAmount());
			Double totalAmount = StringUtil.nullToDouble(DoubleUtil.sub(amount, preferentialAmount));
			Double realRefundAmount = StringUtil.nullToDoubleFormat(DoubleUtil.add(DoubleUtil.add(totalAmount, postage),totalTax));

			// 验证商品退款价格是否有效
			Refund checkRefund = this.refundByOrderItemIdCacheManager.getSession(itemId);
			if (checkRefund != null && refundStatusList.contains(checkRefund.getRefundStatus())) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "此商品已申请退款/退货，请勿重复申请");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			} else if (refundAmount.compareTo(realRefundAmount) > 0 ) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "请正确填写退款金额");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 检查退款退货申请是否能提交
			List<Refund> rufundList = this.refundManager.getRefundListByOrderItemId(realOrderItems.getItemId(), true);
			if (rufundList != null && rufundList.size() > 0) {
				for(Refund xRefund : rufundList){
					// 退款退货已存在记录,如果不是审核拒绝不能重现申请
					if (!StringUtil.compareObject(xRefund.getRefundStatus(), Refund.REFUND_STATUS_REFUSE) 
							&& !StringUtil.compareObject(xRefund.getRefundStatus(), Refund.REFUND_STATUS_TIMEOUT)) {
						resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
						resultMap.put(PortalConstants.MSG, "申请记录处理中,请勿重复提交");
						resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
						return resultMap;
					}
				}
			}

			// 检查商品是否存在
			Product product = this.productManager.get(realOrderItems.getProductId());
			if(product == null || product.getProductId() == null){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "申请退货商品已下架或不存在，请确认后重试");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			if(StringUtil.nullToBoolean(product.getIsFresh())) {
				int refundExpressTime = StringUtil.nullToInteger(Constants.conf.getProperty("chunruo.fresh.refund.expire.date"));
				if (System.currentTimeMillis() - order.getPayTime().getTime() >= refundExpressTime * 24 * 60 * 60 * 1000) {
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "生鲜类商品超过三天不支持退款");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
			}
			
			Refund refund = new Refund();
			refund.setRefundNumber("T" + CoreInitUtil.getRandomNo());
			refund.setOrderId(order.getOrderId()); 
			refund.setOrderItemId(realOrderItems.getItemId()); 
			refund.setProductId(realOrderItems.getProductId()); 
			refund.setProductPrice(StringUtil.nullToDoubleFormatStr(realOrderItems.getPrice())); 
			refund.setProductName(realOrderItems.getProductName()); 
			refund.setTotalAmount(StringUtil.nullToDoubleFormatStr(realOrderItems.getAmount())); 
			refund.setRefundType(refundType); 
			refund.setRefundCount(realOrderItems.getQuantity()); 
			refund.setRefundAmount(StringUtil.nullToDoubleFormatStr(refundAmount)); 
			refund.setReasonId(reasonId); 
			refund.setUserId(order.getUserId()); 
			refund.setStoreId(order.getStoreId());
			refund.setRefundStatus(Refund.REFUND_STATUS_WAIT);
			refund.setRefundExplain(refundExplain); 
			refund.setIsReceive(false); 
			refund.setIsGroupProduct(StringUtil.nullToBoolean(realOrderItems.getIsGroupProduct()));
			refund.setGroupUniqueBatch(StringUtil.null2Str(realOrderItems.getGroupUniqueBatch()));
			refund.setCreateTime(DateUtil.getCurrentDate()); 
			refund.setUpdateTime(refund.getCreateTime()); 

			refund.setImage1(image1);
			refund.setImage2(image2);
			refund.setImage3(image3);
			refund = this.refundManager.saveRefund(refund);

			try {
				// 删除缓存
				this.refundListByUserIdCacheManager.removeSession(refund.getUserId());
				this.refundListByStoreIdCacheManager.removeSession(order.getStoreId());
				this.refundByOrderItemIdCacheManager.removeSession(refund.getOrderItemId());
				this.userSaleRecordListByUserIdCacheManager.removeSession(order.getStoreId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "退款申请提交成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			// 释放锁
			lock.unlock();
		}

		resultMap.put(PortalConstants.MSG, "系统异常,请稍后重试");
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * 退货物流信息
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value = LoginInterceptor.LOGIN, contType = LoginInterceptor.CONT_JOSN_TYPE)
	@RequestMapping(value = "/saveExpressNumber")
	public @ResponseBody Map<String, Object> saveExpressNumber(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long refundId = StringUtil.nullToLong(request.getParameter("refundId"));
		String expressNumber = StringUtil.nullToString(request.getParameter("expressNumber"));
		String expressCompany = StringUtil.nullToString(request.getParameter("expressCompany"));
		String expressImage1 = StringUtil.nullToString(request.getParameter("expressImage1"));
		String expressImage2 = StringUtil.nullToString(request.getParameter("expressImage2"));
		String expressImage3 = StringUtil.nullToString(request.getParameter("expressImage3"));
		String expressExplain = StringUtil.nullToString(request.getParameter("expressExplain"));
	
		try {
			// 检查验证是否登录
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if (userInfo == null || userInfo.getUserId() == null) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_NOLOGIN);
				resultMap.put(PortalConstants.MSG, "用户未登录");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if (StringUtil.isNullStr(expressNumber)) {
				// 物流单号不能为空
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "物流单号不能为空");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			} else if (StringUtil.isNullStr(expressCompany)) {
				// 物流公司不能为空
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "物流公司不能为空");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 检查退货点到是否有效
			Refund refund = this.refundManager.get(refundId);
			if (refund == null || refund.getReasonId() == null) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "退款信息不存在");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if(!StringUtil.compareObject(refund.getStoreId(), userInfo.getUserId())) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "提交失败,请联系店铺处理");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			int refundExpressTime = StringUtil.nullToInteger(Constants.conf.getProperty("chunruo.refund.expire.date"));
			if (System.currentTimeMillis() - refund.getUpdateTime().getTime() >= refundExpressTime * 24 * 60 * 60 * 1000) {
				// 超时，更新退款单状态
				refund.setRefundStatus(Refund.REFUND_STATUS_TIMEOUT);
				refund.setUpdateTime(DateUtil.getCurrentDate());
				this.refundManager.update(refund);
				
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "填写物流信息操作已超时");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			refund.setIsReceive(false);
			refund.setIsSendPackage(true); 							// 回填退货物流信息
			refund.setRefundStatus(Refund.REFUND_STATUS_RECEIPT);	// 平台收货
			refund.setExpressNumber(expressNumber);
			refund.setExpressCompany(expressCompany);
			refund.setExpressImage1(expressImage1);
			refund.setExpressImage2(expressImage2);
			refund.setExpressImage3(expressImage3);
			refund.setExpressExplain(expressExplain);
			refund.setUpdateTime(DateUtil.getCurrentDate());
			refund = this.refundManager.update(refund);

			try {
				// 删除缓存
				this.refundByOrderItemIdCacheManager.removeSession(refund.getOrderItemId());
				this.refundListByUserIdCacheManager.removeSession(refund.getUserId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			resultMap.put(PortalConstants.MSG, "提交成功");
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "系统错误，请稍后重试");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
}
