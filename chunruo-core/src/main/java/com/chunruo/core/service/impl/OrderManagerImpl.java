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
import com.chunruo.core.util.PortalUtil;
import com.chunruo.core.vo.ChilderOrderVo;
import com.chunruo.core.vo.MsgModel;
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
				// 子订单查询订单商品列表
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

		// 检查订单是否需要推送海关支付
		boolean isDirectPushErp = false;
		boolean isPushCustoms = false;
		MsgModel<ProductWarehouse> warehouseModel = PortalUtil.checkProductWarehouse(order.getWareHouseId());
		if(StringUtil.nullToBoolean(warehouseModel.getIsSucc())){
			// 非推送支付信息订单可以直接推送ERP
			ProductWarehouse warehouse = warehouseModel.getData();
			isDirectPushErp = StringUtil.nullToBoolean(warehouse.getIsDirectPushErp());
			isPushCustoms = StringUtil.nullToBoolean(warehouse.getIsPushCustoms());
		}

		// 推送ERP同步记录信息
		order.setIsSyncExpress(false);
		order.setIsDirectPushErp(isDirectPushErp);
		order.setIsPushCustoms(isPushCustoms);
		order.setSyncTime(DateUtil.getCurrentDate());
		order.setSyncNumber(0);
		order.setIsRequestPushCustoms(false);           // 是否请求支付报关
		order.setIsPushErp(false); 						// 订单是否推送ERP
		order.setIsPaymentSucc(false); 					// 未支付
		order.setIsIntercept(false); 					// 是否拦截
		order.setIsDelete(false); 						// 是否删除
		order.setIsCheck(false); 						// 是否对账(1:未对账;2:已对账)
		order.setIsSubOrder(false); 					// 是否子订单(拆单)
		order.setCancelMethod(0); 						// 订单取消方式(0:默认;1:过期自动取消;2:卖家手动取消;3:买家手动取消)
		order.setStatus(OrderStatus.NEW_ORDER_STATUS); 	// 订单状态(1:未支付;2:未发货;3:已发货;4:已完成;5:已取消;6:退款中;7:买家确认收货)
		order = this.saveOrder(order);

		// 拆单订单
		if (StringUtil.nullToBoolean(order.getIsSplitSingle()) 
				&& order.getOrderItemsList() != null
				&& order.getOrderItemsList().size() > 0) {
			// 按仓库归类订单商品列表
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
				childerOrder.setLoginType(order.getLoginType());	            						//用户登录类型
				childerOrder.setLevel(order.getLevel());                     	 						//下单用户等级
				childerOrder.setParentOrderId(order.getOrderId()); 										// 子单父类ID
				childerOrder.setOrderNo(order.getOrderNo() + "0" + index); 								// 订单号
				childerOrder.setStoreId(order.getStoreId());                                            // 店铺id
				childerOrder.setUserId(order.getUserId()); 												// 买家用户ID
				childerOrder.setPostage(StringUtil.nullToDoubleFormat(childerOrderVo.getPostage())); 	// 邮费
				childerOrder.setTax(StringUtil.nullToDoubleFormat(childerOrderVo.getTax())); 			// 增值税
				childerOrder.setTotalRealSellPrice(StringUtil.nullToDoubleFormat(childerOrderVo.getRealSellPrice()));//商品拿货价
				childerOrder.setProductAmount(StringUtil.nullToDoubleFormat(childerOrderVo.getProductAmount())); 	// 商品金额（不含邮费）
				childerOrder.setOrderAmount(StringUtil.nullToDoubleFormat(childerOrderVo.getOrderAmount())); 		// 订单金额（含邮费）
				childerOrder.setPayAmount(StringUtil.nullToDoubleFormat(childerOrderVo.getPayAmount())); 			// 实际付款金额
				childerOrder.setPaymentType(order.getPaymentType()); 						// 支付方式(0:微信支付;1:支付宝支付,2:易宝支付)
				childerOrder.setProductNumber(childerOrderVo.getTotalNumber()); 				// 商品总件数
				childerOrder.setBuyerMessage(order.getBuyerMessage()); 						// 买家留言
				childerOrder.setRemarks(order.getRemarks()); 								// 备注
				childerOrder.setIsShareBuy(StringUtil.nullToBoolean(order.getIsShareBuy()));//是否分享订单
				childerOrder.setIsMyselfStore(StringUtil.nullToBoolean(order.getIsMyselfStore()));//是否自己店铺下单
				childerOrder.setCancelMethod(0); 											// 订单取消方式(0:默认;1:过期自动取消;2:卖家手动取消;3:买家手动取消)
				childerOrder.setIsRequestPushCustoms(false);          	 					// 是否请求支付报关
				childerOrder.setIsSplitSingle(false); 										// 是否拆单
				childerOrder.setIsSubOrder(true); 											// 是否子订单(拆单)
				childerOrder.setIsCheck(false); 											// 是否对账(1:未对账;2:已对账)
				childerOrder.setIsPaymentSucc(false); 										// 未支付
				childerOrder.setIsDelete(false); 											// 是否删除
				childerOrder.setIsIntercept(false); 										// 是否拦截
				childerOrder.setIsSeckillProduct(order.getIsSeckillProduct()); 				// 是否秒杀商品
				childerOrder.setIsInvitationAgent(order.getIsInvitationAgent()); 			// 是否邀请订单
				childerOrder.setIsNoStoreBuyAgent(order.getIsNoStoreBuyAgent()); 			// 是否普通用户购买代理
				childerOrder.setStatus(OrderStatus.NEW_ORDER_STATUS); 						// 订单状态(1:未支付;2:未发货;3:已发货;4:已完成;5:已取消;6:退款中;7:买家确认收货)

				// 检查订单是否需要推送海关支付
				boolean isSubDirectPushErp = false;
				MsgModel<ProductWarehouse> subWarehouseModel = PortalUtil.checkProductWarehouse(childerOrderVo.getWareHouseId());
				if(StringUtil.nullToBoolean(subWarehouseModel.getIsSucc())){
					// 非推送支付信息订单可以直接推送ERP
					ProductWarehouse warehouse = subWarehouseModel.getData();
					isSubDirectPushErp = StringUtil.nullToBoolean(warehouse.getIsDirectPushErp());

					// 需要推送海关仓库(拆单子订单直接推送ERP)
					if(StringUtil.nullToBoolean(warehouse.getIsPushCustoms())) {
						isSubDirectPushErp = true;
					}
				}

				// 推送ERP同步记录信息
				childerOrder.setIsSyncExpress(false); 						// 是已同步快递信息
				childerOrder.setIsPushErp(false); 							// 订单是否推送ERP
				childerOrder.setIsDirectPushErp(isSubDirectPushErp); 		// 是否可以直接同步ERP(子订单直推ERP)
				childerOrder.setIsPushCustoms(false); 						// 是否需要推送海关支付信息(子订单不走支付报关)
				childerOrder.setSyncTime(order.getSyncTime());
				childerOrder.setSyncNumber(0);

				// 上级店铺利润
				childerOrder.setTopUserId(order.getTopUserId());											// 上线店铺ID
				childerOrder.setProfitTop(StringUtil.nullToDoubleFormat(childerOrderVo.getTopProfit())); 	// 上级店铺利润
				childerOrder.setProductType(childerOrderVo.getProductType()); 								// 商品类型
				childerOrder.setWareHouseId(childerOrderVo.getWareHouseId()); 								// 所属仓库ID

				// 优惠劵信息
				childerOrder.setIsUserCoupon(order.getIsUserCoupon());  		 				//是否使用优惠券
				// 如果使用优惠券,重新计算折后商品总价和让利
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

					// 使用优惠券让利商品支付金额
					childerOrder.setUserCouponId(order.getUserCouponId());              		//优惠券ID
					childerOrder.setPreferentialAmount(StringUtil.nullToDoubleFormat(totalPreferentialAmount));
					childerOrder.setPayAmount(StringUtil.nullToDoubleFormat(DoubleUtil.add(totalDiscountAmount,  childerOrder.getPostage())));
					childerOrder.setTotalRealSellPrice(StringUtil.nullToDoubleFormat(totalRealSellPrice));
				}

				// 物流方式收货人信息
				childerOrder.setBuyWayType(order.getBuyWayType()); 			// 物流方式(0:快递发货;1:上门自提)
				childerOrder.setConsignee(order.getConsignee()); 			// 收货人
				childerOrder.setConsigneePhone(order.getConsigneePhone()); 	// 收货人电话
				childerOrder.setProvinceId(order.getProvinceId()); 			// 省ID
				childerOrder.setCityId(order.getCityId()); 					// 市ID
				childerOrder.setAreaId(order.getAreaId()); 					// 区ID
				childerOrder.setIdentityName(order.getIdentityName()); 		// 买家身份证真实姓名
				childerOrder.setIdentityNo(order.getIdentityNo()); 			// 买家身份证
				childerOrder.setIdentityFront(order.getIdentityFront()); 	// 买家身份证-正面
				childerOrder.setIdentityBack(order.getIdentityBack()); 		// 买家身份证-反面
				childerOrder.setAddress(order.getAddress()); 				// 收货地址
				childerOrder.setCreateTime(currentDate); 					// 创建时间
				childerOrder.setUpdateTime(currentDate); 					// 更新时间
				childerList.add(childerOrder);
				index++;
			}

			// 更新子订单记录
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

		// 更新优惠券状态
		if(StringUtil.nullToBoolean(order.getIsUserCoupon())){
			//更新优惠券状态（被占用）
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
		// 订单已完成
		OrderHistory orderHistory = new OrderHistory();
		orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
		orderHistory.setOrderId(orderId);
		orderHistory.setName("订单已完成");
		orderHistory.setMessage(orderHistory.getName());
		orderHistory.setCreateTime(DateUtil.getCurrentDate());

		this.orderRepository.updateOrderCompleteStatus(orderId, status);
		this.orderHistoryRepository.save(orderHistory);
		// 利润结算
		this.userProfitRecordManager.orderCheckUpdateRecord(orderId);
	}

	@Override
	public void updateOrderCompleteStatus(Integer status, List<Long> orderIdList) {
		if (orderIdList != null && orderIdList.size() > 0) {
			List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory>();
			for (Long orderId : orderIdList) {
				// 订单已完成
				OrderHistory orderHistory = new OrderHistory();
				orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
				orderHistory.setOrderId(orderId);
				orderHistory.setName("订单已完成");
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
			// 更新重新计算利润
			this.userProfitRecordManager.batchUpdateProfitRecords(order, orderIdList);

			// 更新操作记录已发货
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
		// 保存同步记录
		Order order = this.get(orderId);
		if (isSuccess) {
			order.setErrorMsg("");
			order.setIsPushErp(isSuccess);
			order.setUpdateTime(DateUtil.getCurrentDate());
			order = this.save(order);

			// 推送ERP成功
			OrderHistory orderHistory = new OrderHistory();
			orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
			orderHistory.setOrderId(order.getOrderId());
			orderHistory.setCreateTime(DateUtil.getCurrentDate());
			orderHistory.setName("推送ERP成功");
			orderHistory.setMessage(String.format("订单%s推送ERP成功", order.getOrderNo()));
			this.orderHistoryRepository.save(orderHistory);
		} else {
			// 推送ERP错误信息
			order.setErrorMsg(errorMsg);
			order.setUpdateTime(DateUtil.getCurrentDate());
			order = this.save(order);

			// 推送ERP失败
			OrderHistory orderHistory = new OrderHistory();
			orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
			orderHistory.setOrderId(order.getOrderId());
			orderHistory.setCreateTime(DateUtil.getCurrentDate());
			orderHistory.setName("推送ERP失败");
			orderHistory.setMessage(StringUtil.getStringByAppointLen(errorMsg, 490));
			this.orderHistoryRepository.save(orderHistory);

			//检查身份证是否超额(跨境商品)
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
		// 加锁
		lock.lock();
		try{
			log.info("=================="+orderId);
			Map<String, List<Long>> listMap = new HashMap<String, List<Long>>();
			List<Integer> orderStatusList = new ArrayList<Integer>();
			orderStatusList.add(OrderStatus.NEW_ORDER_STATUS);       //未支付
			orderStatusList.add(OrderStatus.CANCEL_ORDER_STATUS);    //已关闭
			Order order = this.getOrderByOrderId(orderId);
			if (order != null 
					&& order.getOrderId() != null
					&& orderStatusList.contains(StringUtil.nullToInteger(order.getStatus()))
					&& !StringUtil.nullToBoolean(order.getIsPaymentSucc())) {
			
				log.info("-------------------"+orderId);

				// 主订单状态更新
				List<Order> orderList = new ArrayList<Order>();
				order.setPaymentType(paymentType); 						// 支付方式 0:微信支付;1:支付宝支付
				order.setWeChatConfigId(weChatConfigId); 				// 微信支付编号，区别微信支付、公众号支付
				order.setTradeNo(transactionId); 						// 支付机构流水号
				order.setIsPaymentSucc(true); 							// 支付成功
				if(StringUtil.nullToBoolean(order.getIsInvitationAgent())) {
					order.setStatus(OrderStatus.OVER_ORDER_STATUS); 		// 不必发货的地址进入完成状态
				}else {
					order.setStatus(OrderStatus.UN_DELIVER_ORDER_STATUS); 	// 订单状态进入待发货
				}

				order.setUnDeliverStatus(UnDeliverStatus.UN_DELIVER_STATUS);//待发货订单
				order.setPayTime(DateUtil.getCurrentDate()); 			// 支付时间
				order.setUpdateTime(DateUtil.getCurrentDate());
				orderList.add(order);

				// 检查订单是否有子订单
				if (StringUtil.nullToBoolean(order.getIsSplitSingle())) {
					List<Order> childOrderList = this.getOrderSubListByParentOrderId(order.getOrderId());
					if (childOrderList != null && childOrderList.size() > 0) {
						for (Order childOrder : childOrderList) {
							childOrder.setPaymentType(order.getPaymentType()); // 支付方式 0:微信支付;1:支付宝支付
							childOrder.setWeChatConfigId(order.getWeChatConfigId()); // 微信支付编号，区别微信支付、公众号支付
							childOrder.setTradeNo(order.getTradeNo()); // 支付机构流水号
							childOrder.setIsPaymentSucc(order.getIsPaymentSucc()); // 支付成功
							childOrder.setStatus(order.getStatus()); // 订单状态进入待发货
							order.setUnDeliverStatus(UnDeliverStatus.UN_DELIVER_STATUS);//待发货订单
							childOrder.setPayTime(order.getPayTime()); // 支付时间
							childOrder.setUpdateTime(order.getUpdateTime()); // 更新时间
							orderList.add(childOrder);
						}
					}
				}

				// 订单支付成功保存支付回调记录
				OrderPaymentRecord record = this.orderPaymentRecordManager.getByOrderIdAndPaymentType(orderId,order.getOrderNo(), paymentType, weChatConfigId);
				if(record != null && record.getRecordId() != null){
					record.setIsPaymentSucc(true);
					record.setResponseData(responseData);
					record.setUpdateTime(DateUtil.getCurrentDate());

					// 订单支付人信息补充
					order.setPaymentRecordId(record.getRecordId());

					//删除订单对应的支付方式记录
					this.orderPaymentRecordManager.save(record);
					this.orderPaymentRecordManager.deleteOtherByOrderId(orderId);
				}

				// 批量更新订单支付状态
				this.batchInsert(orderList, orderList.size());

				// 订单存在,且订单状态是为支付成功状态进行分成
				if(StringUtil.nullToBoolean(order.getIsInvitationAgent())) {
					//会员礼包订单
					this.userProfitRecordManager.insertProfitRecordsByIsInvitationAgent(order);
				}else {
					//订单下单上线级返利
					listMap = this.userProfitRecordManager.batchInsertProfitRecords(order);

				}
			}
			return listMap;
		}catch(Exception e) {
			log.info("*************************");
			throw e;
		} finally {
			// 释放锁
			lock.unlock();     
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	@Override
	public boolean userAccountPaymentErrorClose(Long orderId, String transactionId, Integer paymentType,
			Long weChatConfigId, String responseData) {
		// 加锁
		lock.lock();
		try{
			List<Integer> orderStatusList = new ArrayList<Integer>();
			orderStatusList.add(OrderStatus.NEW_ORDER_STATUS);       //未支付
			orderStatusList.add(OrderStatus.CANCEL_ORDER_STATUS);    //已关闭
			Order order = this.getOrderByOrderId(orderId);
			if (order != null 
					&& order.getOrderId() != null
					&& orderStatusList.contains(StringUtil.nullToInteger(order.getStatus()))
					&& !StringUtil.nullToBoolean(order.getIsSubOrder())
					&& !StringUtil.nullToBoolean(order.getIsPaymentSucc())) {
				// 主订单状态更新
				List<Order> orderList = new ArrayList<Order>();
				order.setPaymentType(paymentType); 						// 支付方式 0:微信支付;1:支付宝支付
				order.setWeChatConfigId(weChatConfigId); 				// 微信支付编号，区别微信支付、公众号支付
				order.setTradeNo(transactionId); 						// 支付机构流水号
				order.setIsPaymentSucc(true); 							// 支付成功
				order.setStatus(OrderStatus.CANCEL_ORDER_STATUS); 		// 订单关闭
				order.setUnDeliverStatus(UnDeliverStatus.UN_DELIVER_STATUS);//待发货订单
				order.setPayTime(DateUtil.getCurrentDate()); 			// 支付时间
				order.setUpdateTime(DateUtil.getCurrentDate());
				orderList.add(order);

				// 检查订单是否有子订单
				if (StringUtil.nullToBoolean(order.getIsSplitSingle())) {
					List<Order> childOrderList = this.getOrderSubListByParentOrderId(order.getOrderId());
					if (childOrderList != null && childOrderList.size() > 0) {
						for (Order childOrder : childOrderList) {
							childOrder.setPaymentType(order.getPaymentType()); // 支付方式 0:微信支付;1:支付宝支付
							childOrder.setWeChatConfigId(order.getWeChatConfigId()); // 微信支付编号，区别微信支付、公众号支付
							childOrder.setTradeNo(order.getTradeNo()); // 支付机构流水号
							childOrder.setIsPaymentSucc(order.getIsPaymentSucc()); // 支付成功
							childOrder.setStatus(order.getStatus()); // 订单状态进入待发货
							order.setUnDeliverStatus(UnDeliverStatus.UN_DELIVER_STATUS);//待发货订单
							childOrder.setPayTime(order.getPayTime()); // 支付时间
							childOrder.setUpdateTime(order.getUpdateTime()); // 更新时间
							orderList.add(childOrder);
						}
					}
				}

				// 订单支付成功保存支付回调记录
				OrderPaymentRecord record = this.orderPaymentRecordManager.getByOrderIdAndPaymentType(orderId,order.getOrderNo(), paymentType, weChatConfigId);
				if(record != null && record.getRecordId() != null){
					record.setIsPaymentSucc(true);
					record.setResponseData(responseData);
					record.setUpdateTime(DateUtil.getCurrentDate());

					// 订单支付人信息补充
					order.setPaymentRecordId(record.getRecordId());

					//删除订单对应的支付方式记录
					this.orderPaymentRecordManager.save(record);
					this.orderPaymentRecordManager.deleteOtherByOrderId(orderId);
				}

				// 批量更新订单支付状态
				this.batchInsert(orderList, orderList.size());
				return true;
			}
		} finally {
			// 释放锁
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

				// 订单关闭
				List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory>();
				for (Long orderId : realOrderIdList) {
					OrderHistory orderHistory = new OrderHistory();
					orderHistory.setUserId(userId);
					orderHistory.setOrderId(orderId);
					orderHistory.setCreateTime(DateUtil.getCurrentDate());
					orderHistory.setName("订单关闭");
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

				// 订单关闭
				List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory>();
				for (Long orderId : realOrderIdList) {
					OrderHistory orderHistory = new OrderHistory();
					orderHistory.setUserId(userId);
					orderHistory.setOrderId(orderId);
					orderHistory.setCreateTime(DateUtil.getCurrentDate());
					orderHistory.setName("订单关闭");
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
					// 订单还原
					order.setIsIntercept(false);
					order.setUpdateTime(DateUtil.getCurrentDate());

					OrderHistory orderHistory = new OrderHistory();
					orderHistory.setUserId(userId);
					orderHistory.setOrderId(order.getOrderId());
					orderHistory.setCreateTime(DateUtil.getCurrentDate());
					orderHistory.setName("订单还原");
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

					// 直邮订单待出库
					OrderHistory orderHistory = new OrderHistory();
					orderHistory.setUserId(userId);
					orderHistory.setOrderId(order.getOrderId());
					orderHistory.setName("直邮待出库");
					orderHistory.setMessage(String.format("订单%s直邮待出库", order.getOrderNo()));
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

		// 检查是否为秒杀商品并且为待付款状态
		boolean isSeckillProduct = false;
		if((StringUtil.nullToBoolean(order.getIsSeckillProduct())
				|| StringUtil.nullToBoolean(order.getIsLevelLimitProduct())) && StringUtil.compareObject(order.getStatus(), OrderStatus.NEW_ORDER_STATUS)){
			isSeckillProduct = true;
		}

		// 订单状态关闭历史记录
		OrderHistory orderHistory = new OrderHistory();
		orderHistory.setUserId(userId);
		orderHistory.setOrderId(orderId);
		orderHistory.setCreateTime(DateUtil.getCurrentDate());
		orderHistory.setName("订单关闭");
		orderHistory.setMessage(StringUtil.getStringByAppointLen(message, 500));

		// 订单关闭
		order.setIsNeedCheckPayment(false);
		order.setCancelReasonId(StringUtil.nullToLong(reasonId));
		order.setStatus(OrderStatus.CANCEL_ORDER_STATUS);
		order.setCancelTime(DateUtil.getCurrentDate());
		order.setUpdateTime(order.getCancelTime());
		this.save(order);

		//检查是否拆单
		if(StringUtil.nullToBoolean(order.getIsSplitSingle())) {
			this.orderRepository.updateSubOrderStatusByParentOrderId(StringUtil.nullToLong(order.getOrderId()),OrderStatus.CANCEL_ORDER_STATUS);
		}

		if(refund != null) {
			//未发货订单一小时内直接退款
			this.refundManager.save(refund);
			//维护用户销售记录表
			this.userSaleRecordManager.updateUserSaleRecord(order, true, true);
			//任务商品检查
			this.userProductTaskItemManager.failUserProductTaskItemByOrder(order);

			//删除用户退款请求记录
			RefundRequestRecord record = this.refundRequestRecordManager.getRefundRequestRecordByOrderNoAndRefundNumber(order.getOrderNo(),refund.getRefundNumber());
			if(record != null && record.getRecordId() != null) {
				this.refundRequestRecordManager.remove(record);
			}
		}

		// 更新秒杀锁定库存
		List<Long> lockStockProductIdList = new ArrayList<Long> ();
		if(isSeckillProduct){
			lockStockProductIdList = this.orderLockStockManager.closeOrderLockStockListByOrderId(order.getOrderId());
		}

		this.userProfitRecordManager.updateUserProfitRecordStatusByOrderId(order.getOrderId(), UserProfitRecord.DISTRIBUTION_STATUS_RETURN,0D);
		this.orderHistoryRepository.save(orderHistory);

		// 是否使用优惠券
		if(StringUtil.nullToBoolean(order.getIsUserCoupon()) && !StringUtil.nullToBoolean(order.getIsPaymentSucc())){
			// 优惠券还原
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
					// 订单还原
					order.setIsIntercept(false);
					order.setUnDeliverStatus(UnDeliverStatus.UN_DELIVER_RESTORE);
					order.setUpdateTime(DateUtil.getCurrentDate());

					OrderHistory orderHistory = new OrderHistory();
					orderHistory.setUserId(userId);
					orderHistory.setOrderId(order.getOrderId());
					orderHistory.setCreateTime(DateUtil.getCurrentDate());
					orderHistory.setName("订单还原");
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
			// 订单还原
			order.setIsIntercept(false);
			order.setUnDeliverStatus(UnDeliverStatus.UN_DELIVER_RESTORE);
			order.setUpdateTime(DateUtil.getCurrentDate());

			//维护用户销售额记录
			this.userSaleRecordManager.updateUserSaleRecord(order,false,false);

			OrderHistory orderHistory = new OrderHistory();
			orderHistory.setUserId(userId);
			orderHistory.setOrderId(order.getOrderId());
			orderHistory.setCreateTime(DateUtil.getCurrentDate());
			orderHistory.setName("订单还原");
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
			// 订单取消
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
