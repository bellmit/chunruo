package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.RefundHistory;
import com.chunruo.core.model.RefundReason;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.OrderItemsManager;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.RefundHistoryManager;
import com.chunruo.core.service.RefundManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.User;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/refund/")
public class RefundController extends BaseController {
	private Lock lock = new ReentrantLock();
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private OrderManager orderManager;
	@Autowired
	OrderItemsManager orderItemsManager;
	@Autowired
	private RefundManager refundManager;
	@Autowired
	private RefundHistoryManager refundHistoryManager;

	/**
	 * 退款退货列表查询
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		String record = request.getParameter("status");
		List<Integer> statusList = StringUtil.stringToIntegerArray(record);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<Refund> refundList = new ArrayList<>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), Refund.class);

			if (statusList != null && statusList.size() > 0) {
				paramMap.put("refundStatus", statusList);
			}
			List<Integer> typeList = new ArrayList<Integer>();
			typeList.add(Refund.REFUND_TYPE_GOODS);
			typeList.add(Refund.REFUND_TYPE_MONEY);
			typeList.add(Refund.REFUND_TYPE_PART);
			paramMap.put("refundType", typeList);
			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				//店铺名称过滤做特殊处理,转换成ID过滤
				if(filtersMap.containsKey("storeName")){
					List<Long> userIdList = new ArrayList<>();
					Map<String,Object> storeMap = new HashMap<>();
					storeMap.put("storeName", filtersMap.remove("storeName"));
					List<UserInfo> userInfoList = this.userInfoManager.getHqlPages(storeMap);
					if(userInfoList != null && userInfoList.size() > 0){
						for(UserInfo userInfo : userInfoList){
							userIdList.add(userInfo.getUserId());
						}
					}
					
					if(userIdList != null && userIdList.size() > 0){
						filtersMap.put("userId", userIdList);
					}else{
						resultMap.put("data", null);
						resultMap.put("totalCount", 0);
						resultMap.put("filters", filtersMap);
						return resultMap;
					}
					
				}
				
				//订单号过滤做特殊处理,转换成ID过滤
				if(filtersMap.containsKey("orderNo")){
					List<Long> orderIdList = new ArrayList<Long>();
					List<Order> orderList = this.orderManager.getOrderListByLikeParentOrderNo(StringUtil.nullToString(filtersMap.get("orderNo")));
					filtersMap.remove("orderNo");
					if(orderList != null && orderList.size() > 0){
						for(Order order : orderList) {
							orderIdList.add(order.getOrderId());
						}
						filtersMap.put("orderId", orderIdList);
					}else{
						resultMap.put("data", null);
						resultMap.put("totalCount", 0);
						resultMap.put("filters", filtersMap);
						return resultMap;
					}
					
				}
				
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
				
			}

			count = this.refundManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				refundList = this.refundManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if(refundList != null && refundList.size() > 0){
					for(Refund refund : refundList){
						// 店铺信息
						Order order = this.orderManager.get(refund.getOrderId());
						if (order != null && order.getOrderId() != null) {
							refund.setOrderNo(order.getOrderNo());
						}
						
						UserInfo userInfo = this.userInfoManager.get(refund.getStoreId());
						if (userInfo != null && userInfo.getUserId() != null) {
							refund.setStoreName(userInfo.getStoreName());
							refund.setUserMobile(userInfo.getMobile());
						}
						
						// 退款退货理由
						RefundReason reason = Constants.REFUND_REASON_MAP.get(refund.getReasonId());
						if (reason != null && reason.getReasonId() != null){
							refund.setReason(reason.getReason());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", refundList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;

	}

	/**
	 * 退款订单详情
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getRefundById")
	public @ResponseBody Map<String, Object> getOrderById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long refundId = StringUtil.nullToLong(request.getParameter("refundId"));
		Refund refund = null;
		List<RefundHistory> historyList = new ArrayList<RefundHistory> ();
		List<OrderItems> orderItemList = new ArrayList<>();
		try{
			refund = this.refundManager.get(refundId);
			if(refund != null && refund.getRefundId() != null){
				//支付方式
				Order order = orderManager.get(refund.getOrderId());
				if(order != null && order.getOrderId() != null){
					refund.setPaymentType(null == order.getPaymentType() ? 0 : order.getPaymentType());
					refund.setOrderNo(order.getOrderNo());
				}
				
				// 店铺信息
				UserInfo userInfo = this.userInfoManager.get(refund.getUserId());
				if (userInfo != null && userInfo.getUserId() != null) {
					refund.setStoreName(userInfo.getStoreName());
				}
				
				// 退款退货理由
				RefundReason reason = Constants.REFUND_REASON_MAP.get(refund.getReasonId());
				if (reason != null && reason.getReasonId() != null){
					refund.setReason(reason.getReason());
				}
				
				// 退款订单项
				OrderItems orderItems = this.orderItemsManager.get(refund.getOrderItemId());
				if(orderItems != null && orderItems.getItemId() != null){
					if(StringUtil.nullToBoolean(orderItems.getIsGroupProduct())){
						//组合商品
						orderItemList = this.orderItemsManager.getOrderItemsListByQroupUniqueBatch(order.getOrderId(), orderItems.getGroupUniqueBatch());
					}else{
						// 普通商品
						orderItemList.add(orderItems);
					}
				}
				// 操作历史记录
				historyList = this.refundHistoryManager.getRefundHistoryListByRefundId(refundId);;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put("success", true);
		resultMap.put("refund", refund);
		resultMap.put("orderItem", orderItemList);
		resultMap.put("history", historyList);
		return resultMap;
	}

	/**
	 * 退款退货审核
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/checkRefund")
	public @ResponseBody Map<String, Object> checkRefund(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long refundId = StringUtil.nullToLong(request.getParameter("refundId"));
		Boolean result = StringUtil.nullToBoolean(request.getParameter("result"));
		String reason = StringUtil.nullToString(request.getParameter("reason"));
		Double amount = StringUtil.nullToDouble(request.getParameter("refundAmount"));
		String address = StringUtil.nullToString(request.getParameter("address"));
		String remarks = StringUtil.nullToString(request.getParameter("remarks"));
		Refund refund = null;
		
		// 加锁
		lock.lock();
		try{
			// 支持操作状态
			List<Integer> supperStatusList = new ArrayList<Integer> ();
			supperStatusList.add(Refund.REFUND_STATUS_WAIT);
			supperStatusList.add(Refund.REFUND_STATUS_RECEIPT);
			
			
			refund = this.refundManager.get(refundId);
			if(refund == null || refund.getRefundId() == null){
				resultMap.put("success", false);
				resultMap.put("msg", "错误,退款退货记录不存在");
				return resultMap;
			}else if(!supperStatusList.contains(refund.getRefundStatus())){
				resultMap.put("success", false);
				resultMap.put("msg", "错误,退款退货记录非审核状态");
				return resultMap;
			}else if(!StringUtil.nullToBoolean(result) && StringUtil.isNullStr(reason)){
				resultMap.put("success", false);
				resultMap.put("msg", "拒绝退款申请，必须填写原因");
				return resultMap;
			}
			
			refund.setRemarks(remarks);
			User adminUser = this.getCurrentUser(request);
			
			resultMap.put("msg", "操作成功");
			Order order = this.orderManager.get(StringUtil.nullToLong(refund.getOrderId()));
			if(order == null || order.getOrderId() == null) {
				resultMap.put("success", false);
				resultMap.put("msg", "订单信息未找到");
				return resultMap;
			}
						
			this.refundManager.checkRefund(refund, result, reason, address, amount, adminUser.getUserId(), adminUser.getUsername());
			resultMap.put("success", true);
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			// 释放锁
			lock.unlock();
		}
		
		resultMap.put("success", true);
		resultMap.put("msg", "错误,操作失败");
		return resultMap;
	}

	/**
	 * 退货商品平台收货确认
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/refundReceipt")
	public @ResponseBody Map<String, Object> refundReceipt(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = request.getParameter("idListGridJson");

		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("ajax.no.record"));
			return resultMap;
		}
	
		try{
			// 检查退货列表数据是否有效
			List<Refund> refundList = this.refundManager.getByIdList(idList);
			if (refundList == null || refundList.size() <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
			
			boolean isExistError = false;
			StringBuffer errorBuffer = new StringBuffer();
			for (Refund refund : refundList) {
				if (!StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_RECEIPT)) {
					isExistError = true;
					errorBuffer.append(String.format("<br/>%s非平台收货状态", refund.getRefundNumber()));
				}
			}
			
			// 检查是否存在错误
			if(isExistError){
				resultMap.put("success", false);
				resultMap.put("message", String.format("错误,以下退货商品操作失败%s", errorBuffer.toString()));
				return resultMap;
			}
			
			User adminUser = this.getCurrentUser(request);
			this.refundManager.refundReceipt(idList, adminUser.getUserId(), adminUser.getUsername());
			
			resultMap.put("success", true);
			resultMap.put("msg", "操作成功");
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put("success", true);
		resultMap.put("msg", "错误,操作失败");
		return resultMap;
	}
	
	
	
	/**
	 * 退款备注
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/editRemarks")
	public @ResponseBody Map<String, Object> editRemarks(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long refundId = StringUtil.nullToLong(request.getParameter("refundId"));
		String remarks = StringUtil.null2Str(request.getParameter("remarks"));

		
		try {
			if(StringUtil.isNull(remarks)) {
				resultMap.put("success", false);
				resultMap.put("error", true);
				resultMap.put("message", getText("备注不能为空"));
				return resultMap;
			}
			Refund refund = this.refundManager.get(refundId);
			if(refund != null && refund.getRefundId() != null ) {
				refund.setRemarks(remarks);
				refund.setUpdateTime(DateUtil.getCurrentDate());
				this.refundManager.save(refund);
				
				resultMap.put("success", true);
				resultMap.put("error", false);
				resultMap.put("message", getText("save.success"));
				return resultMap;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", false);
		resultMap.put("error", true);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}
	
	
	/**
	 * 退款原因备注
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/editRemarkReason")
	public @ResponseBody Map<String, Object> editRemarkReason(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long refundId = StringUtil.nullToLong(request.getParameter("refundId"));
		Integer remarkReasonId = StringUtil.nullToInteger(request.getParameter("remarkReasonId"));

		
		try {
			if(StringUtil.compareObject(remarkReasonId, 0)) {
				resultMap.put("success", false);
				resultMap.put("error", true);
				resultMap.put("message", getText("请选择退款原因"));
				return resultMap;
			}
			Refund refund = this.refundManager.get(refundId);
			if(refund != null && refund.getRefundId() != null ) {
				refund.setRemarkReasonId(remarkReasonId);
				refund.setUpdateTime(DateUtil.getCurrentDate());
				this.refundManager.save(refund);
				
				resultMap.put("success", true);
				resultMap.put("error", false);
				resultMap.put("message", getText("save.success"));
				return resultMap;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", false);
		resultMap.put("error", true);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}
}
