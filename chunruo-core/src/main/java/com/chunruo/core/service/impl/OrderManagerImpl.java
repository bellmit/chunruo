package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.Constants.UnDeliverStatus;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderHistory;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.OrderPaymentRecord;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.RefundRequestRecord;
import com.chunruo.core.model.UserCoupon;
import com.chunruo.core.model.UserProfitRecord;
import com.chunruo.core.repository.OrderHistoryRepository;
import com.chunruo.core.repository.OrderItemsRepository;
import com.chunruo.core.repository.OrderRepository;
import com.chunruo.core.repository.UserCartRepository;
import com.chunruo.core.service.IdentificationManager;
import com.chunruo.core.service.OrderLockStockManager;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.OrderPaymentRecordManager;
import com.chunruo.core.service.RefundManager;
import com.chunruo.core.service.RefundRequestRecordManager;
import com.chunruo.core.service.UserCouponManager;
import com.chunruo.core.service.UserProductTaskItemManager;
import com.chunruo.core.service.UserProfitRecordManager;
import com.chunruo.core.service.UserSaleRecordManager;
import com.chunruo.core.vo.ChilderOrderVo;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("orderManager")
public class OrderManagerImpl extends GenericManagerImpl<Order, Long> implements OrderManager {
	public final static int SPLIT_SINGLE_CHILDER_ORDER_SIZE = 2;
	private Lock lock = new ReentrantLock();
	private OrderRepository orderRepository;
	@Autowired
	private OrderItemsRepository orderItemsRepository;
	@Autowired
	private UserCartRepository userCartRepository;
	@Autowired
	private UserCouponManager userCouponManager;
	@Autowired
	private OrderHistoryRepository orderHistoryRepository;
	@Autowired
	private UserProfitRecordManager userProfitRecordManager;
	@Autowired
	private OrderLockStockManager orderLockStockManager;
	@Autowired
	private RefundManager refundManager;
	@Autowired
	private UserSaleRecordManager userSaleRecordManager;
	@Autowired
	private RefundRequestRecordManager refundRequestRecordManager;
	@Autowired
	private OrderPaymentRecordManager orderPaymentRecordManager;
	@Autowired
	private IdentificationManager identificationManager;
	@Autowired
	private UserProductTaskItemManager userProductTaskItemManager;

	@Autowired
	public OrderManagerImpl(OrderRepository orderRepository) {
		super(orderRepository);
		this.orderRepository = orderRepository;
	}

	@Override
	public List<Order> getOrderListByUserIdList(List<Long> userIdList) {
		if (userIdList != null && userIdList.size() > 0) {
			return this.orderRepository.getOrderListByUserIdList(userIdList);
		}
		return null;
	}

	@Override
	public List<Order> getOrderListByOrderNoList(List<String> orderNoList) {
		if (orderNoList != null && orderNoList.size() > 0) {
			return this.orderRepository.getOrderListByOrderNoList(orderNoList);
		}
		return null;
	}

	@Override
	public List<Order> getWaitCheckOrders(Integer status, Date complateTime, boolean isSubOrder, boolean isCheck) {
		return this.orderRepository.getCheckOrders(status, complateTime, isSubOrder, isCheck);
	}

	@Override
	public List<Order> getOrderSubListByParentOrderId(Long parentOrderId) {
		List<Order> subOrderList = this.orderRepository.getOrderSubListByParentOrderId(parentOrderId);
		if (subOrderList != null && subOrderList.size() > 0) {
			for (Order subOrder : subOrderList) {
				List<OrderItems> orderItemsList = this.orderItemsRepository.getOrderSubItemsListByOrderId(parentOrderId, subOrder.getOrderId());
				if (orderItemsList != null && orderItemsList.size() > 0) {
					subOrder.setOrderItemsList(orderItemsList);
				}
			}
		}
		return subOrderList;
	}

	@Override
	public Order getOrderByOrderId(Long orderId) {
		Order order = this.get(orderId);
		if (order != null && order.getOrderId() != null) {
			List<OrderItems> orderItemsList = new ArrayList<OrderItems> ();
			if (StringUtil.nullToBoolean(order.getIsSubOrder())) {
				// ?????????????????????????????????
				orderItemsList = this.orderItemsRepository.getOrderSubItemsListByOrderId(order.getParentOrderId(), order.getOrderId());
			} else {
				orderItemsList = this.orderItemsRepository.getOrderItemsListByOrderId(order.getOrderId());
			}
			order.setOrderItemsList(orderItemsList);
		}
		return order;
	}

	@Transactional
	@Override
	public Order saveOrder(Order order) {
		List<OrderItems> orderItemsList = order.getOrderItemsList();
		order = this.save(order);

		if (orderItemsList != null && orderItemsList.size() > 0) {
			for (OrderItems orderItems : orderItemsList) {
				orderItems.setOrderId(order.getOrderId());
			}

			orderItemsList = this.orderItemsRepository.batchInsert(orderItemsList, orderItemsList.size());
			if (orderItemsList != null && orderItemsList.size() > 0) {
				order.setOrderItemsList(orderItemsList);
			}
		}
		return order;
	}

	@Override
	public void updateOrderCheckById(boolean isCheck, Long orderId) {
		orderRepository.updateOrderCheckById(isCheck, orderId);
	}

	@Transactional
	@Override
	public Order saveOrder(Order order, List<ChilderOrderVo> childerOrderList, List<Long> userCartIdList) {
		if (userCartIdList != null && userCartIdList.size() > 0) {
			this.userCartRepository.deleteByIdList(userCartIdList);
		}


		// ??????ERP??????????????????
		order.setIsSyncExpress(false);
		order.setSyncTime(DateUtil.getCurrentDate());
		order.setSyncNumber(0);
		order.setIsRequestPushCustoms(false);           // ????????????????????????
		order.setIsPushErp(false); 						// ??????????????????ERP
		order.setIsPaymentSucc(false); 					// ?????????
		order.setIsIntercept(false); 					// ????????????
		order.setIsDelete(false); 						// ????????????
		order.setIsCheck(false); 						// ????????????(1:?????????;2:?????????)
		order.setIsSubOrder(false); 					// ???????????????(??????)
		order.setCancelMethod(0); 						// ??????????????????(0:??????;1:??????????????????;2:??????????????????;3:??????????????????)
		order.setStatus(OrderStatus.NEW_ORDER_STATUS); 	// ????????????(1:?????????;2:?????????;3:?????????;4:?????????;5:?????????;6:?????????;7:??????????????????)
		order = this.saveOrder(order);

		// ????????????
		if (StringUtil.nullToBoolean(order.getIsSplitSingle()) 
				&& order.getOrderItemsList() != null
				&& order.getOrderItemsList().size() > 0) {
			// ?????????????????????????????????
			Map<Long, List<OrderItems>> wareHouseIdItemsListMap = new HashMap<Long, List<OrderItems>>();
			for (OrderItems orderItems : order.getOrderItemsList()) {
				if (wareHouseIdItemsListMap.containsKey(orderItems.getWareHouseId())) {
					wareHouseIdItemsListMap.get(orderItems.getWareHouseId()).add(orderItems);
				} else {
					List<OrderItems> orderItemsList = new ArrayList<OrderItems>();
					orderItemsList.add(orderItems);
					wareHouseIdItemsListMap.put(orderItems.getWareHouseId(), orderItemsList);
				}
			}

			int index = 1;
			Date currentDate = DateUtil.getCurrentDate();
			List<Order> childerList = new ArrayList<Order>();
			for (ChilderOrderVo childerOrderVo : childerOrderList) {
				Order childerOrder = new Order();
				childerOrder.setIsNeedCheckPayment(false);
				childerOrder.setLoginType(order.getLoginType());	            						//??????????????????
				childerOrder.setLevel(order.getLevel());                     	 						//??????????????????
				childerOrder.setParentOrderId(order.getOrderId()); 										// ????????????ID
				childerOrder.setOrderNo(order.getOrderNo() + "0" + index); 								// ?????????
				childerOrder.setStoreId(order.getStoreId());                                            // ??????id
				childerOrder.setUserId(order.getUserId()); 												// ????????????ID
				childerOrder.setPostage(StringUtil.nullToDoubleFormat(childerOrderVo.getPostage())); 	// ??????
				childerOrder.setTax(StringUtil.nullToDoubleFormat(childerOrderVo.getTax())); 			// ?????????
				childerOrder.setTotalRealSellPrice(StringUtil.nullToDoubleFormat(childerOrderVo.getRealSellPrice()));//???????????????
				childerOrder.setProductAmount(StringUtil.nullToDoubleFormat(childerOrderVo.getProductAmount())); 	// ??????????????????????????????
				childerOrder.setOrderAmount(StringUtil.nullToDoubleFormat(childerOrderVo.getOrderAmount())); 		// ???????????????????????????
				childerOrder.setPayAmount(StringUtil.nullToDoubleFormat(childerOrderVo.getPayAmount())); 			// ??????????????????
				childerOrder.setPaymentType(order.getPaymentType()); 						// ????????????(0:????????????;1:???????????????,2:????????????)
				childerOrder.setProductNumber(childerOrderVo.getTotalNumber()); 				// ???????????????
				childerOrder.setBuyerMessage(order.getBuyerMessage()); 						// ????????????
				childerOrder.setRemarks(order.getRemarks()); 								// ??????
				childerOrder.setIsShareBuy(StringUtil.nullToBoolean(order.getIsShareBuy()));//??????????????????
				childerOrder.setIsMyselfStore(StringUtil.nullToBoolean(order.getIsMyselfStore()));//????????????????????????
				childerOrder.setCancelMethod(0); 											// ??????????????????(0:??????;1:??????????????????;2:??????????????????;3:??????????????????)
				childerOrder.setIsRequestPushCustoms(false);          	 					// ????????????????????????
				childerOrder.setIsSplitSingle(false); 										// ????????????
				childerOrder.setIsSubOrder(true); 											// ???????????????(??????)
				childerOrder.setIsCheck(false); 											// ????????????(1:?????????;2:?????????)
				childerOrder.setIsPaymentSucc(false); 										// ?????????
				childerOrder.setIsDelete(false); 											// ????????????
				childerOrder.setIsIntercept(false); 										// ????????????
				childerOrder.setIsSeckillProduct(order.getIsSeckillProduct()); 				// ??????????????????
				childerOrder.setIsInvitationAgent(order.getIsInvitationAgent()); 			// ??????????????????
				childerOrder.setIsNoStoreBuyAgent(order.getIsNoStoreBuyAgent()); 			// ??????????????????????????????
				childerOrder.setStatus(OrderStatus.NEW_ORDER_STATUS); 						// ????????????(1:?????????;2:?????????;3:?????????;4:?????????;5:?????????;6:?????????;7:??????????????????)


				// ??????????????????
				childerOrder.setTopUserId(order.getTopUserId());											// ????????????ID
				childerOrder.setProfitTop(StringUtil.nullToDoubleFormat(childerOrderVo.getTopProfit())); 	// ??????????????????
				childerOrder.setProductType(childerOrderVo.getProductType()); 								// ????????????
				childerOrder.setWareHouseId(childerOrderVo.getWareHouseId()); 								// ????????????ID

				// ???????????????
				childerOrder.setIsUserCoupon(order.getIsUserCoupon());  		 				//?????????????????????
				// ?????????????????????,???????????????????????????????????????
				if(StringUtil.nullToBoolean(order.getIsUserCoupon())){
					Double totalDiscountAmount = 0.0D;
					Double totalPreferentialAmount = 0.0D;
					Double totalRealSellPrice = 0.0D;
					List<OrderItems> itemsList = wareHouseIdItemsListMap.get(childerOrderVo.getWareHouseId());
					for (OrderItems orderItems : itemsList) {
						totalDiscountAmount = DoubleUtil.add(totalDiscountAmount, StringUtil.nullToDouble(orderItems.getDiscountAmount()));
						totalPreferentialAmount = DoubleUtil.add(totalPreferentialAmount, StringUtil.nullToDouble(orderItems.getPreferentialAmount()));
						totalRealSellPrice = DoubleUtil.add(StringUtil.nullToDouble(orderItems.getRealSellPrice()), totalRealSellPrice);
					}

					// ???????????????????????????????????????
					childerOrder.setUserCouponId(order.getUserCouponId());              		//?????????ID
					childerOrder.setPreferentialAmount(StringUtil.nullToDoubleFormat(totalPreferentialAmount));
					childerOrder.setPayAmount(StringUtil.nullToDoubleFormat(DoubleUtil.add(totalDiscountAmount,  childerOrder.getPostage())));
					childerOrder.setTotalRealSellPrice(StringUtil.nullToDoubleFormat(totalRealSellPrice));
				}

				// ???????????????????????????
				childerOrder.setBuyWayType(order.getBuyWayType()); 			// ????????????(0:????????????;1:????????????)
				childerOrder.setConsignee(order.getConsignee()); 			// ?????????
				childerOrder.setConsigneePhone(order.getConsigneePhone()); 	// ???????????????
				childerOrder.setProvinceId(order.getProvinceId()); 			// ???ID
				childerOrder.setCityId(order.getCityId()); 					// ???ID
				childerOrder.setAreaId(order.getAreaId()); 					// ???ID
				childerOrder.setIdentityName(order.getIdentityName()); 		// ???????????????????????????
				childerOrder.setIdentityNo(order.getIdentityNo()); 			// ???????????????
				childerOrder.setIdentityFront(order.getIdentityFront()); 	// ???????????????-??????
				childerOrder.setIdentityBack(order.getIdentityBack()); 		// ???????????????-??????
				childerOrder.setAddress(order.getAddress()); 				// ????????????
				childerOrder.setCreateTime(currentDate); 					// ????????????
				childerOrder.setUpdateTime(currentDate); 					// ????????????
				childerList.add(childerOrder);
				index++;
			}

			// ?????????????????????
			childerList = this.orderRepository.batchInsert(childerList, childerList.size());
			if (childerList != null && childerList.size() > 0) {
				List<OrderItems> orderItemsList = new ArrayList<OrderItems>();
				for (Order childerOrder : childerList) {
					if (wareHouseIdItemsListMap.containsKey(childerOrder.getWareHouseId())) {
						List<OrderItems> itemsList = wareHouseIdItemsListMap.get(childerOrder.getWareHouseId());
						for (OrderItems orderItems : itemsList) {
							orderItems.setSubOrderId(childerOrder.getOrderId());
							orderItemsList.add(orderItems);
						}
					}
				}
				this.orderItemsRepository.batchInsert(orderItemsList, orderItemsList.size());
			}
		}

		// ?????????????????????
		if(StringUtil.nullToBoolean(order.getIsUserCoupon())){
			//????????????????????????????????????
			UserCoupon userCoupon = this.userCouponManager.get(order.getUserCouponId());
			if(userCoupon != null && userCoupon.getUserCouponId() != null){
				userCoupon.setCouponStatus(UserCoupon.USER_COUPON_STATUS_OCCUPIED);
				userCoupon.setUpdateTime( new Date());
				this.userCouponManager.update(userCoupon);
			}
		}
		return order;
	}

	@Override
	public Order getOrderByOrderNo(String orderNo) {
		Order order = this.orderRepository.getOrderByOrderNo(orderNo);
		if (order != null && order.getOrderId() != null) {
			List<OrderItems> orderItemsList = this.orderItemsRepository.getOrderItemsListByOrderId(order.getOrderId());
			if (orderItemsList != null && orderItemsList.size() > 0) {
				order.setOrderItemsList(orderItemsList);
			}
		}
		return order;
	}

	@Override
	public List<OrderItems> getOrderItemsListByOrderIdList(List<Long> orderIdList) {
		if (orderIdList != null && orderIdList.size() > 0) {
			return this.orderItemsRepository.getOrderItemsListByOrderIdList(orderIdList);
		}
		return null;
	}

	@Override
	public void updateOrderCompleteStatus(Integer status, Long orderId) {
		// ???????????????
		OrderHistory orderHistory = new OrderHistory();
		orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
		orderHistory.setOrderId(orderId);
		orderHistory.setName("???????????????");
		orderHistory.setMessage(orderHistory.getName());
		orderHistory.setCreateTime(DateUtil.getCurrentDate());

		this.orderRepository.updateOrderCompleteStatus(orderId, status);
		this.orderHistoryRepository.save(orderHistory);
		// ????????????
		this.userProfitRecordManager.orderCheckUpdateRecord(orderId);
	}

	@Override
	public void updateOrderCompleteStatus(Integer status, List<Long> orderIdList) {
		if (orderIdList != null && orderIdList.size() > 0) {
			List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory>();
			for (Long orderId : orderIdList) {
				// ???????????????
				OrderHistory orderHistory = new OrderHistory();
				orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
				orderHistory.setOrderId(orderId);
				orderHistory.setName("???????????????");
				orderHistory.setMessage(orderHistory.getName());
				orderHistory.setCreateTime(DateUtil.getCurrentDate());
				orderHistoryList.add(orderHistory);
			}

			this.orderRepository.updateOrderCompleteStatus(orderIdList, status);
			this.orderHistoryRepository.batchInsert(orderHistoryList, orderHistoryList.size());
		}
	}

	@Override
	public void updateOrderOthrerCompleteStatus(Integer status, List<Long> orderIdList, Long parentOrderId) {
		Order order = this.get(parentOrderId);
		if (order != null 
				&& order.getOrderId() != null 
				&& StringUtil.nullToBoolean(order.getIsSplitSingle())
				&& orderIdList != null 
				&& orderIdList.size() > 0) {
			// ????????????????????????
			this.userProfitRecordManager.batchUpdateProfitRecords(order, orderIdList);

			// ???????????????????????????
			orderIdList.add(parentOrderId);
			this.updateOrderCompleteStatus(status, orderIdList);
		}
	}

	@Override
	public List<Order> getOrderListByUserId(Long userId, int limit) {
		return this.orderRepository.getOrderListByUserId(userId, limit);
	}

	@Override
	public List<Order> getOrderListByShareUserId(Long shareUserId) {
		return this.orderRepository.getOrderListByShareUserId(shareUserId);
	}

	@Override
	public List<Order> getOrderListAfterCreateTime(Integer status, boolean isSubOrder, Date createTime, Date endDate) {
		return this.orderRepository.getOrderListAfterCreateTime(status, isSubOrder, createTime, endDate);
	}

	@Override
	public void deleteOrder(Long orderId) {
		this.orderRepository.deleteOrder(true, orderId);
	}

	@Override
	public List<Order> getOrderListByStatus(Integer status, Boolean isSubOrder) {
		if (isSubOrder != null) {
			return this.orderRepository.getOrderListByStatus(status, isSubOrder);
		} else {
			return this.orderRepository.getOrderListByStatus(status);
		}
	}

	@Override
	public List<Order> updateOrderPushErpRecordByLoadFunction() {
		String uniqueString = StringUtil.null2Str(UUID.randomUUID().toString());
		String batchNumber = this.orderRepository.executeSqlFunction("{?=call loadPushErpRecordList_Fnc(?)}", new Object[] { uniqueString });
		log.debug("updateOrderPushErpRecordByLoadFunction=======>>> " + StringUtil.null2Str(uniqueString));
		if (StringUtil.compareObject(uniqueString, batchNumber)) {
			return this.orderRepository.getOrderListByBatchNumber(batchNumber);
		}
		return null;
	}

	@Override
	public int updateOrderUnPaymentStatusClose() {
		String updateNumber = this.orderRepository.executeSqlFunction("{?=call updateOrderUnPaymentStatusClose_Fnc()}");
		return StringUtil.nullToInteger(updateNumber);
	}

	@Override
	public void updateOrderPaymentTradeNo(Long orderId, String tradeNo, Integer status, Integer paymentType, Long weChatConfigId) {
		this.orderRepository.updateOrderPaymentTradeNo(orderId, tradeNo, status, paymentType, weChatConfigId);
	}

	@Override
	public void updateOrderPushErpStatus(Long orderId, boolean isSuccess, String errorMsg) {
		// ??????????????????
		Order order = this.get(orderId);
		if (isSuccess) {
			order.setErrorMsg("");
			order.setIsPushErp(isSuccess);
			order.setUpdateTime(DateUtil.getCurrentDate());
			order = this.save(order);

			// ??????ERP??????
			OrderHistory orderHistory = new OrderHistory();
			orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
			orderHistory.setOrderId(order.getOrderId());
			orderHistory.setCreateTime(DateUtil.getCurrentDate());
			orderHistory.setName("??????ERP??????");
			orderHistory.setMessage(String.format("??????%s??????ERP??????", order.getOrderNo()));
			this.orderHistoryRepository.save(orderHistory);
		} else {
			// ??????ERP????????????
			order.setErrorMsg(errorMsg);
			order.setUpdateTime(DateUtil.getCurrentDate());
			order = this.save(order);

			// ??????ERP??????
			OrderHistory orderHistory = new OrderHistory();
			orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
			orderHistory.setOrderId(order.getOrderId());
			orderHistory.setCreateTime(DateUtil.getCurrentDate());
			orderHistory.setName("??????ERP??????");
			orderHistory.setMessage(StringUtil.getStringByAppointLen(errorMsg, 490));
			this.orderHistoryRepository.save(orderHistory);

			//???????????????????????????(????????????)
			if(StringUtil.null2Str(order.getErrorMsg()).contains(Constants.CROSS_PRODUCT_LIMIT_MSG)) {
				ProductWarehouse productWarehouse = Constants.PRODUCT_WAREHOUSE_MAP.get(StringUtil.nullToLong(order.getWareHouseId()));
				if(productWarehouse != null && productWarehouse.getWarehouseId() != null
						&& StringUtil.compareObject(StringUtil.nullToInteger(productWarehouse.getProductType()), GoodsType.GOODS_TYPE_CROSS)) {
					this.identificationManager.updateIdentificationByStatus(true,StringUtil.null2Str(order.getIdentityNo()));
				}
			}
		}
	}

	@Override
	public List<Order> getOrderStatusListBeforeSyncTime(Integer status, Date beforeDate) {
		return this.orderRepository.getOrderStatusListBeforeSyncTime(status, beforeDate);
	}

	@Override
	public List<Order> getOrderListByUpdateTime(Date updateTime) {
		return this.orderRepository.getOrderListByUpdateTime(updateTime);
	}

	@Override
	public List<Order> getOrderListByTime(Date beginDate, Date endDate, List<Long> storeIdList){
		if(storeIdList != null && storeIdList.size() > 0){
			return this.orderRepository.getOrderListByTime(beginDate, endDate, storeIdList);
		}else{
			return this.orderRepository.getOrderListByTime(beginDate, endDate);
		}
	}

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public Map<String, List<Long>> updateOrderPaymentSuccStatus(Long orderId, String transactionId, Integer paymentType, Long weChatConfigId, String responseData) throws Exception {
		// ??????
		lock.lock();
		try{
			Map<String, List<Long>> listMap = new HashMap<String, List<Long>>();
			List<Integer> orderStatusList = new ArrayList<Integer>();
			orderStatusList.add(OrderStatus.NEW_ORDER_STATUS);       //?????????
			orderStatusList.add(OrderStatus.CANCEL_ORDER_STATUS);    //?????????
			Order order = this.getOrderByOrderId(orderId);
			if (order != null 
					&& order.getOrderId() != null
					&& orderStatusList.contains(StringUtil.nullToInteger(order.getStatus()))
					&& !StringUtil.nullToBoolean(order.getIsPaymentSucc())) {

				List<Order> orderList = new ArrayList<Order>();
				order.setPaymentType(paymentType); 						
				order.setWeChatConfigId(weChatConfigId); 				
				order.setTradeNo(transactionId); 						
				order.setIsPaymentSucc(true); 	
				order.setStatus(OrderStatus.UN_DELIVER_ORDER_STATUS); 	
				if(StringUtil.nullToBoolean(order.getIsInvitationAgent())) {
					order.setStatus(OrderStatus.OVER_ORDER_STATUS); 		
				}

				order.setPayTime(DateUtil.getCurrentDate()); 			
				order.setUpdateTime(DateUtil.getCurrentDate());
				orderList.add(order);


				OrderPaymentRecord record = this.orderPaymentRecordManager.getByOrderIdAndPaymentType(orderId,order.getOrderNo(), paymentType, weChatConfigId);
				if(record != null && record.getRecordId() != null){
					record.setIsPaymentSucc(true);
					record.setResponseData(responseData);
					record.setUpdateTime(DateUtil.getCurrentDate());

					order.setPaymentRecordId(record.getRecordId());

					this.orderPaymentRecordManager.save(record);
					this.orderPaymentRecordManager.deleteOtherByOrderId(orderId);
				}

				this.batchInsert(orderList, orderList.size());

				if(StringUtil.nullToBoolean(order.getIsInvitationAgent())) {
					this.userProfitRecordManager.insertProfitRecordsByIsInvitationAgent(order);
				}else {
					listMap = this.userProfitRecordManager.batchInsertProfitRecords(order);

				}
			}
			return listMap;
		}catch(Exception e) {
			throw e;
		} finally {
			// ?????????
			lock.unlock();     
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	@Override
	public boolean userAccountPaymentErrorClose(Long orderId, String transactionId, Integer paymentType,
			Long weChatConfigId, String responseData) {
		// ??????
		lock.lock();
		try{
			List<Integer> orderStatusList = new ArrayList<Integer>();
			orderStatusList.add(OrderStatus.NEW_ORDER_STATUS);       //?????????
			orderStatusList.add(OrderStatus.CANCEL_ORDER_STATUS);    //?????????
			Order order = this.getOrderByOrderId(orderId);
			if (order != null 
					&& order.getOrderId() != null
					&& orderStatusList.contains(StringUtil.nullToInteger(order.getStatus()))
					&& !StringUtil.nullToBoolean(order.getIsSubOrder())
					&& !StringUtil.nullToBoolean(order.getIsPaymentSucc())) {
				// ?????????????????????
				List<Order> orderList = new ArrayList<Order>();
				order.setPaymentType(paymentType); 						// ???????????? 0:????????????;1:???????????????
				order.setWeChatConfigId(weChatConfigId); 				// ?????????????????????????????????????????????????????????
				order.setTradeNo(transactionId); 						// ?????????????????????
				order.setIsPaymentSucc(true); 							// ????????????
				order.setStatus(OrderStatus.CANCEL_ORDER_STATUS); 		// ????????????
				order.setUnDeliverStatus(UnDeliverStatus.UN_DELIVER_STATUS);//???????????????
				order.setPayTime(DateUtil.getCurrentDate()); 			// ????????????
				order.setUpdateTime(DateUtil.getCurrentDate());
				orderList.add(order);

				// ??????????????????????????????
				if (StringUtil.nullToBoolean(order.getIsSplitSingle())) {
					List<Order> childOrderList = this.getOrderSubListByParentOrderId(order.getOrderId());
					if (childOrderList != null && childOrderList.size() > 0) {
						for (Order childOrder : childOrderList) {
							childOrder.setPaymentType(order.getPaymentType()); // ???????????? 0:????????????;1:???????????????
							childOrder.setWeChatConfigId(order.getWeChatConfigId()); // ?????????????????????????????????????????????????????????
							childOrder.setTradeNo(order.getTradeNo()); // ?????????????????????
							childOrder.setIsPaymentSucc(order.getIsPaymentSucc()); // ????????????
							childOrder.setStatus(order.getStatus()); // ???????????????????????????
							order.setUnDeliverStatus(UnDeliverStatus.UN_DELIVER_STATUS);//???????????????
							childOrder.setPayTime(order.getPayTime()); // ????????????
							childOrder.setUpdateTime(order.getUpdateTime()); // ????????????
							orderList.add(childOrder);
						}
					}
				}

				// ??????????????????????????????????????????
				OrderPaymentRecord record = this.orderPaymentRecordManager.getByOrderIdAndPaymentType(orderId,order.getOrderNo(), paymentType, weChatConfigId);
				if(record != null && record.getRecordId() != null){
					record.setIsPaymentSucc(true);
					record.setResponseData(responseData);
					record.setUpdateTime(DateUtil.getCurrentDate());

					// ???????????????????????????
					order.setPaymentRecordId(record.getRecordId());

					//???????????????????????????????????????
					this.orderPaymentRecordManager.save(record);
					this.orderPaymentRecordManager.deleteOtherByOrderId(orderId);
				}

				// ??????????????????????????????
				this.batchInsert(orderList, orderList.size());
				return true;
			}
		} finally {
			// ?????????
			lock.unlock();     
		}
		return false;
	}

	@Override
	public void updateOrderCloseStatus(List<Long> orderIdList, String message, Long userId) {
		if (orderIdList != null && orderIdList.size() > 0) {
			List<Order> orderList = this.orderRepository.getByIdList(orderIdList);
			if (orderList != null && orderList.size() > 0) {
				List<Long> realOrderIdList = new ArrayList<Long>();
				List<String> orderNumberList = new ArrayList<String>();
				for (Order order : orderList) {
					realOrderIdList.add(order.getOrderId());
					orderNumberList.add(order.getOrderNo());
				}

				// ????????????
				List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory>();
				for (Long orderId : realOrderIdList) {
					OrderHistory orderHistory = new OrderHistory();
					orderHistory.setUserId(userId);
					orderHistory.setOrderId(orderId);
					orderHistory.setCreateTime(DateUtil.getCurrentDate());
					orderHistory.setName("????????????");
					orderHistory.setMessage(StringUtil.getStringByAppointLen(message, 500));
					orderHistoryList.add(orderHistory);
				}

				this.userSaleRecordManager.updateUserSaleRecordByStatus(realOrderIdList, OrderStatus.CANCEL_ORDER_STATUS);
				this.userProfitRecordManager.updateUserProfitRecordStatusByOrderIdList(realOrderIdList, UserProfitRecord.DISTRIBUTION_STATUS_RETURN);
				this.orderRepository.updateOrderStatus(realOrderIdList, OrderStatus.CANCEL_ORDER_STATUS);
				this.orderHistoryRepository.batchInsert(orderHistoryList, orderHistoryList.size());
			}
		}
	}

	@Override
	public void updateSubOrderCloseStatus(List<Long> subOrderIdList, String message, Long userId) {
		if (subOrderIdList != null && subOrderIdList.size() > 0) {
			List<Order> orderList = this.orderRepository.getByIdList(subOrderIdList);
			if (orderList != null && orderList.size() > 0) {
				List<Long> realOrderIdList = new ArrayList<Long>();
				for (Order order : orderList) {
					realOrderIdList.add(order.getOrderId());
				}

				// ????????????
				List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory>();
				for (Long orderId : realOrderIdList) {
					OrderHistory orderHistory = new OrderHistory();
					orderHistory.setUserId(userId);
					orderHistory.setOrderId(orderId);
					orderHistory.setCreateTime(DateUtil.getCurrentDate());
					orderHistory.setName("????????????");
					orderHistory.setMessage(StringUtil.getStringByAppointLen(message, 500));
					orderHistoryList.add(orderHistory);
				}

				this.userSaleRecordManager.updateUserSaleRecordByStatus(realOrderIdList, OrderStatus.CANCEL_ORDER_STATUS);
				this.orderRepository.updateOrderStatus(realOrderIdList, OrderStatus.CANCEL_ORDER_STATUS);
				this.orderHistoryRepository.batchInsert(orderHistoryList, orderHistoryList.size());
			}
		}
	}

	@Override
	public void updateOrderReduction(List<Long> orderIdList, String message, Long userId) {
		if (orderIdList != null && orderIdList.size() > 0) {
			List<Order> orderList = this.getByIdList(orderIdList);
			if (orderList != null && orderList.size() > 0) {
				List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory>();
				for (Order order : orderList) {
					// ????????????
					order.setIsIntercept(false);
					order.setUpdateTime(DateUtil.getCurrentDate());

					OrderHistory orderHistory = new OrderHistory();
					orderHistory.setUserId(userId);
					orderHistory.setOrderId(order.getOrderId());
					orderHistory.setCreateTime(DateUtil.getCurrentDate());
					orderHistory.setName("????????????");
					orderHistory.setMessage(StringUtil.getStringByAppointLen(message, 500));
					orderHistoryList.add(orderHistory);
				}
				this.orderRepository.batchInsert(orderList, orderList.size());
				this.orderHistoryRepository.batchInsert(orderHistoryList, orderHistoryList.size());
			}
		}
	}

	@Override
	public void updateDirectMailWaitLibraryStatus(List<Long> orderIdList, Long userId) {
		if (orderIdList != null && orderIdList.size() > 0) {
			List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory>();
			List<Order> orderList = this.getByIdList(orderIdList);
			if (orderList != null && orderList.size() > 0) {
				for (Order order : orderList) {
					order.setIsPushErp(true);
					order.setSentTime(DateUtil.getCurrentDate());
					order.setUpdateTime(order.getSentTime());

					// ?????????????????????
					OrderHistory orderHistory = new OrderHistory();
					orderHistory.setUserId(userId);
					orderHistory.setOrderId(order.getOrderId());
					orderHistory.setName("???????????????");
					orderHistory.setMessage(String.format("??????%s???????????????", order.getOrderNo()));
					orderHistory.setCreateTime(DateUtil.getCurrentDate());
					orderHistoryList.add(orderHistory);
				}
			}
			this.batchInsert(orderList, orderList.size());
			this.orderHistoryRepository.batchInsert(orderHistoryList, orderHistoryList.size());
		}
	}

	@Transactional
	@Override
	public List<Long> updateOrderCloseStatus(Long orderId, String message, Long userId, Refund refund, Long reasonId)  throws Exception{
		Order order = this.get(orderId);

		// ???????????????????????????????????????????????????
		boolean isSeckillProduct = false;
		if((StringUtil.nullToBoolean(order.getIsSeckillProduct())
				|| StringUtil.nullToBoolean(order.getIsLevelLimitProduct())) && StringUtil.compareObject(order.getStatus(), OrderStatus.NEW_ORDER_STATUS)){
			isSeckillProduct = true;
		}

		// ??????????????????????????????
		OrderHistory orderHistory = new OrderHistory();
		orderHistory.setUserId(userId);
		orderHistory.setOrderId(orderId);
		orderHistory.setCreateTime(DateUtil.getCurrentDate());
		orderHistory.setName("????????????");
		orderHistory.setMessage(StringUtil.getStringByAppointLen(message, 500));

		// ????????????
		order.setIsNeedCheckPayment(false);
		order.setCancelReasonId(StringUtil.nullToLong(reasonId));
		order.setStatus(OrderStatus.CANCEL_ORDER_STATUS);
		order.setCancelTime(DateUtil.getCurrentDate());
		order.setUpdateTime(order.getCancelTime());
		this.save(order);

		//??????????????????
		if(StringUtil.nullToBoolean(order.getIsSplitSingle())) {
			this.orderRepository.updateSubOrderStatusByParentOrderId(StringUtil.nullToLong(order.getOrderId()),OrderStatus.CANCEL_ORDER_STATUS);
		}

		if(refund != null) {
			//???????????????????????????????????????
			this.refundManager.save(refund);
			//???????????????????????????
			this.userSaleRecordManager.updateUserSaleRecord(order, true, true);
			//??????????????????
			this.userProductTaskItemManager.failUserProductTaskItemByOrder(order);

			//??????????????????????????????
			RefundRequestRecord record = this.refundRequestRecordManager.getRefundRequestRecordByOrderNoAndRefundNumber(order.getOrderNo(),refund.getRefundNumber());
			if(record != null && record.getRecordId() != null) {
				this.refundRequestRecordManager.remove(record);
			}
		}

		// ????????????????????????
		List<Long> lockStockProductIdList = new ArrayList<Long> ();
		if(isSeckillProduct){
			lockStockProductIdList = this.orderLockStockManager.closeOrderLockStockListByOrderId(order.getOrderId());
		}

		this.userProfitRecordManager.updateUserProfitRecordStatusByOrderId(order.getOrderId(), UserProfitRecord.DISTRIBUTION_STATUS_RETURN,0D);
		this.orderHistoryRepository.save(orderHistory);

		// ?????????????????????
		if(StringUtil.nullToBoolean(order.getIsUserCoupon()) && !StringUtil.nullToBoolean(order.getIsPaymentSucc())){
			// ???????????????
			UserCoupon userCoupon = this.userCouponManager.get(order.getUserCouponId());
			if(userCoupon != null && userCoupon.getUserCouponId() != null){
				userCoupon.setCouponStatus(UserCoupon.USER_COUPON_STATUS_NOT_USED);
				userCoupon.setUpdateTime(new Date());
				this.userCouponManager.update(userCoupon);
			}
		}
		return lockStockProductIdList;
	}

	@Override
	public List<Order> getAbmormalOrderList() {
		return this.orderRepository.getAbmormalOrderList();
	}

	@Override
	public List<Order> getOrderListByCreateTime(Integer status, Date beginDate, Date endDate, Long storeId) {
		return this.orderRepository.getOrderListByCreateTime(status, beginDate, endDate, storeId);
	}

	@Override
	public List<Order> getCouponOrderListByUserCouponId(Long couponId) {
		List<Long> userCouponIdList = new ArrayList<Long>();  
		List<UserCoupon> userCouponList = this.userCouponManager.getUserCouponListByCouponId(couponId);
		if(userCouponList != null && userCouponList.size() >0) {
			for(UserCoupon userCoupon :userCouponList) {
				userCouponIdList.add(userCoupon.getUserCouponId());
			}
		}
		return this.orderRepository.getCouponOrderListByUserCouponId(userCouponIdList);
	}

	@Override
	public List<Order> getBecameVipOrderList(Long userId,Date createTime) {
		return this.orderRepository.getBecameVipOrderList(userId,createTime);
	}

	@Override
	public List<Order> getOrderListByLikeParentOrderNo(String orderNo) {
		return this.orderRepository.getOrderListByLikeParentOrderNo(orderNo);
	}

	@Override
	public void updateOrderRestore(List<Long> orderIdList, String message, Long userId) {
		if (orderIdList != null && orderIdList.size() > 0) {
			List<Order> orderList = this.getByIdList(orderIdList);
			if (orderList != null && orderList.size() > 0) {
				List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory>();
				for (Order order : orderList) {
					// ????????????
					order.setIsIntercept(false);
					order.setUnDeliverStatus(UnDeliverStatus.UN_DELIVER_RESTORE);
					order.setUpdateTime(DateUtil.getCurrentDate());

					OrderHistory orderHistory = new OrderHistory();
					orderHistory.setUserId(userId);
					orderHistory.setOrderId(order.getOrderId());
					orderHistory.setCreateTime(DateUtil.getCurrentDate());
					orderHistory.setName("????????????");
					orderHistory.setMessage(StringUtil.getStringByAppointLen(message, 500));
					orderHistoryList.add(orderHistory);
				}
				this.orderRepository.batchInsert(orderList, orderList.size());
				this.orderHistoryRepository.batchInsert(orderHistoryList, orderHistoryList.size());
			}
		}
	}


	@Override
	public void updateOrderRestore(Long orderId, String message, Long userId) {
		Order order = this.get(orderId);
		if(order != null && order.getOrderId() != null) {
			// ????????????
			order.setIsIntercept(false);
			order.setUnDeliverStatus(UnDeliverStatus.UN_DELIVER_RESTORE);
			order.setUpdateTime(DateUtil.getCurrentDate());

			//???????????????????????????
			this.userSaleRecordManager.updateUserSaleRecord(order,false,false);

			OrderHistory orderHistory = new OrderHistory();
			orderHistory.setUserId(userId);
			orderHistory.setOrderId(order.getOrderId());
			orderHistory.setCreateTime(DateUtil.getCurrentDate());
			orderHistory.setName("????????????");
			orderHistory.setMessage(StringUtil.getStringByAppointLen(message, 500));

			this.save(order);
			this.orderHistoryRepository.save(orderHistory);
		}
	}


	@Override
	public void updateUnDeliverOrderStatus(Order order, String message) {
		if(order != null && order.getOrderId() != null) {
			this.save(order);
			if(StringUtil.compareObject(UnDeliverStatus.UN_DELIVER_CANCELING, StringUtil.nullToInteger(order.getUnDeliverStatus()))) {
				this.userSaleRecordManager.updateUserSaleRecord(order, true, false);
			}
			// ????????????
			OrderHistory orderHistory = new OrderHistory();
			orderHistory.setUserId(order.getUserId());
			orderHistory.setOrderId(order.getOrderId());
			orderHistory.setName(message);
			orderHistory.setMessage(orderHistory.getName());
			orderHistory.setCreateTime(DateUtil.getCurrentDate());

			this.orderHistoryRepository.save(orderHistory);
		}
	}

	@Override
	public List<Order> getOrderListByStoreId(Long storeId, int limit) {
		return this.orderRepository.getOrderListByStoreId(storeId, limit);
	}

	@Override
	public int countByIdentityNo(String identityNo, List<Integer> productTypeList) {
		if(productTypeList != null && productTypeList.size() > 0) {
			StringBuffer sqlBuffer = new StringBuffer("select count(order_id) from jkd_order ");
			sqlBuffer.append("where identity_no=? and is_sub_order=0 and is_payment_succ=1 and product_type in(?)  ");
			sqlBuffer.append("and DATE_FORMAT(create_time, '%Y-%m-%d') = DATE_FORMAT(NOW(), '%Y-%m-%d') ");
			long count = this.countSql(sqlBuffer.toString(), new Object[] {identityNo, StringUtil.intArrayToString(productTypeList)});
			return StringUtil.nullToInteger(count);
		}
		return 0;
	}

	@Override
	public List<Object[]> getOrderDetailByUserId(Long userId) {
		StringBuilder strBulSql = new StringBuilder();
		strBulSql.append("select jp.product_id,jp.category_id,jp.brand_id from jkd_order_items joi,jkd_product jp ");
		strBulSql.append("where joi.order_id in( ");
		strBulSql.append("select order_id from jkd_order where is_payment_succ = 1 and user_id = %s ");
		strBulSql.append(") AND joi.product_id = jp.product_id ");
		return this.querySql(String.format(strBulSql.toString(), userId));
	}
}
