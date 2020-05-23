package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ExpressCode;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderHistory;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.OrderPackage;
import com.chunruo.core.model.OrderSync;
import com.chunruo.core.model.UserSaleRecord;
import com.chunruo.core.repository.OrderRepository;
import com.chunruo.core.repository.OrderSyncRepository;
import com.chunruo.core.service.OrderHistoryManager;
import com.chunruo.core.service.OrderItemsManager;
import com.chunruo.core.service.OrderPackageManager;
import com.chunruo.core.service.OrderSyncManager;
import com.chunruo.core.service.UserSaleRecordManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("orderSyncManager")
public class OrderSyncManagerImpl extends GenericManagerImpl<OrderSync, Long> implements OrderSyncManager {
	private Lock lock = new  ReentrantLock();
	private OrderSyncRepository orderSyncRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderPackageManager orderPackageManager;
	@Autowired
	private OrderHistoryManager orderHistoryManager;
	@Autowired
	private OrderItemsManager orderItemsManager;
	@Autowired
	private UserSaleRecordManager userSaleRecordManager;
	
	@Autowired
	public OrderSyncManagerImpl(OrderSyncRepository orderSyncRepository) {
		super(orderSyncRepository);
		this.orderSyncRepository = orderSyncRepository;
	}
	
	@Override
	public List<OrderSync> getOrderSyncListByOrderNumber(String orderNumber) {
		return this.orderSyncRepository.getOrderSyncListByOrderNumber(orderNumber);
	}
	
	@Override
	public List<OrderSync> getOrderSyncListByOrderNumber(String orderNumber, Boolean isHandler){
		return this.orderSyncRepository.getOrderSyncListByOrderNumber(orderNumber, isHandler);
	}
	
	@Override
	public List<OrderSync> getOrderSyncListByOrderNumberList(List<String> orderNumberList){
		return this.orderSyncRepository.getOrderSyncListByOrderNumberList(orderNumberList);
	}
	
	@Override
	public List<OrderSync> updateOrderSyncStatusByLoadFunction() {
		String uniqueString = StringUtil.null2Str(UUID.randomUUID().toString());
		String batchNumber = this.orderSyncRepository.executeSqlFunction("{?=call loadOrderSyncStatusRecordList_Fnc(?)}", new Object[]{uniqueString});
		log.debug("updateOrderSyncStatusByLoadFunction=======>>> " + StringUtil.null2Str(uniqueString));
		if(StringUtil.compareObject(uniqueString, batchNumber)){
			return this.orderSyncRepository.getOrderSyncListByBatchNumber(batchNumber);
		}
		return null;
	}
	
	@Override
	public void saveOrUpdateOrderSyncStatus(OrderSync orderSync) {
		orderSync = this.save(orderSync);
		this.updateOrderSyncStatus(orderSync);
	}

	@Override
	public void updateOrderSyncStatus(OrderSync orderSync) {
		// 已发货订单状态
		List<Integer> suppertUpdateStatusList = new ArrayList<Integer> ();
		suppertUpdateStatusList.add(OrderStatus.DELIVER_ORDER_STATUS);	//已发货
		suppertUpdateStatusList.add(OrderStatus.OVER_ORDER_STATUS);		//已完成 
					
		// 检查订单是否存在
		Order order = this.orderRepository.getOrderByOrderNo(orderSync.getOrderNumber());
		if(order == null || order.getOrderId() == null){
			return;
		}else if(StringUtil.compareObject(orderSync.getOrderStatus(), OrderSync.ORDER_SYNC_STATUS_CANCEL)){
			if(suppertUpdateStatusList.contains(order.getStatus())){
				// 不支持订单状态修改
				return;
			}
		}

		// 检查当前已同步订单状态是否支持更新操作
		if(StringUtil.nullToBoolean(order.getIsSyncExpress())
				&& StringUtil.nullToBoolean(order.getIsIntercept())
				&& StringUtil.compareObject(orderSync.getOrderStatus(), OrderSync.ORDER_SYNC_STATUS_CANCEL)){
			// 当前订单状态已经取消状态
			// 订单状态还是拦截状态，则不处理
			return;
		}
				
		List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory> ();
		if(StringUtil.compareObject(orderSync.getOrderStatus(), OrderSync.ORDER_SYNC_STATUS_SUCCESS)){
			// 检查当前物流信息是否变更操作(订单物流唯一)
			OrderPackage orderPackage = new OrderPackage ();
			orderPackage.setCreateTime(DateUtil.getCurrentDate());
			List<OrderPackage> orderPackageList = this.orderPackageManager.getOrderPackageListByOrderId(order.getOrderId());
			if(orderPackageList != null && orderPackageList.size() > 0){
				orderPackage = orderPackageList.get(0);
			}
			
			orderPackage.setOrderId(order.getOrderId());
			orderPackage.setExpressCode(orderSync.getLogisticCode());		// 承运公司编码
			orderPackage.setExpressCompany(orderSync.getLogisticName());	// 承运公司名称
			orderPackage.setExpressNo(orderSync.getExpressNumber());		// 物流编号
			orderPackage.setIsHandler(orderSync.getIsHandler());			//是否手动导入快递信息
			orderPackage.setUpdateTime(DateUtil.getCurrentDate());
			
			// 自动匹配快递100信息
			if(Constants.EXPRESS_CODE_MAP.containsKey(orderSync.getLogisticName().toLowerCase())){
				ExpressCode expressCode = Constants.EXPRESS_CODE_MAP.get(orderSync.getLogisticName().toLowerCase());
				orderPackage.setExpressCode(expressCode.getCompanyCode());
				orderPackage.setExpressCompany(expressCode.getCompanyName());
			}
			
			// 检查订单是否同步物流信息状态
			boolean isExchangeExpress = false; 
			if(!suppertUpdateStatusList.contains(StringUtil.nullToInteger(order.getStatus()))){
				// 未同步设置订单状态为已发货状态
				order.setStatus(OrderStatus.DELIVER_ORDER_STATUS);
				order.setSentTime(DateUtil.getCurrentDate());
				isExchangeExpress = true;
			}
			
			// ERP发货成功
			order.setIsSyncExpress(true);
			order.setIsIntercept(false);
			order.setUpdateTime(DateUtil.getCurrentDate());
			this.orderRepository.save(order);
			this.orderPackageManager.save(orderPackage);
			
			//订单同步快递信息
			OrderHistory orderHistory = new OrderHistory ();
			orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
			orderHistory.setOrderId(order.getOrderId());
			orderHistory.setName("快递信息同步");
			orderHistory.setMessage(String.format("同步快递单号%s,订单已发货成功", orderSync.getExpressNumber()));
			orderHistory.setCreateTime(DateUtil.getCurrentDate());
			orderHistoryList.add(orderHistory);
			

			//维护用户销售额记录
			UserSaleRecord userSaleRecord = this.userSaleRecordManager.getUserSaleRecordByOrderId(StringUtil.nullToLong(order.getOrderId()));
            if(userSaleRecord != null && userSaleRecord.getRecordId() != null) {
            	userSaleRecord.setOrderStatus(OrderStatus.DELIVER_ORDER_STATUS);
            	userSaleRecord.setOrderSentTime(DateUtil.getCurrentDate());
            	userSaleRecord.setUpdateTime(DateUtil.getCurrentDate());
            	this.userSaleRecordManager.save(userSaleRecord);
            }
            
            try {
            	 lock.lock();
            	  //检查用户是否达到升级标准
                 OrderSyncManagerImpl.checkUserSaleAmount(StringUtil.nullToLong(order.getStoreId()), false);
            }catch(Exception e) {
            	e.printStackTrace();
            }finally {
            	lock.unlock();
            }
          
            Order realOrder = order;
			//推送订单信息
			List<OrderItems> orderItemsList = this.orderItemsManager.getOrderItemsListByOrderId(order.getOrderId());
			// 检查是否为子订单状态更新母订单状态
			if(StringUtil.nullToBoolean(isExchangeExpress) && StringUtil.nullToBoolean(order.getIsSubOrder())){
				Order parentOrder = this.orderRepository.findOne(order.getParentOrderId());
				if(parentOrder != null && parentOrder.getOrderId() != null) {
					orderItemsList = this.orderItemsManager.getOrderSubItemsListByOrderId(StringUtil.nullToLong(parentOrder.getOrderId()), order.getOrderId());
					realOrder = parentOrder;
				}
				
				//订单同步快递信息
				OrderHistory xorderHistory = new OrderHistory ();
				xorderHistory.setUserId(Constants.ADMINSTARTOR_ID);
				xorderHistory.setOrderId(order.getParentOrderId());
				xorderHistory.setName("快递信息同步");
				xorderHistory.setMessage(String.format("同步快递单号%s,订单已发货成功", orderSync.getExpressNumber()));
				xorderHistory.setCreateTime(DateUtil.getCurrentDate());
				orderHistoryList.add(xorderHistory);
				// 设置主订单为已发货状态
				this.orderRepository.updateParentOrderStatus(parentOrder.getOrderId(), OrderStatus.DELIVER_ORDER_STATUS);
			}
			this.orderHistoryManager.batchInsert(orderHistoryList, orderHistoryList.size());
		}else{
			// ERP订单取消
			order.setIsSyncExpress(true);
			order.setIsIntercept(true);    					// 设置为拦击状态
			order.setUpdateTime(DateUtil.getCurrentDate());
			this.orderRepository.save(order);

			//订单同步快递信息
			OrderHistory orderHistory = new OrderHistory ();
			orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
			orderHistory.setOrderId(order.getOrderId());
			orderHistory.setName("订单拦截");
			orderHistory.setMessage(String.format("订单%s已被ERP取消", order.getOrderNo()));
			orderHistory.setCreateTime(DateUtil.getCurrentDate());
			orderHistoryList.add(orderHistory);	

			// 检查是否为子订单状态更新母订单状态
			if(StringUtil.nullToBoolean(order.getIsSubOrder())){
				//订单同步快递信息
				OrderHistory pOrderHistory = new OrderHistory ();
				pOrderHistory.setUserId(Constants.ADMINSTARTOR_ID);
				pOrderHistory.setOrderId(order.getOrderId());
				pOrderHistory.setName("订单拦截");
				pOrderHistory.setMessage(String.format("子订单%s已被ERP取消", order.getOrderNo()));
				pOrderHistory.setCreateTime(DateUtil.getCurrentDate());
				orderHistoryList.add(pOrderHistory);
			}
			this.orderHistoryManager.batchInsert(orderHistoryList, orderHistoryList.size());
		}
		this.orderSyncRepository.updateOrderSyncStatusById(orderSync.getRecordId(), true, DateUtil.getCurrentDate());
	}

	@Override
	public List<OrderSync> saveOrderSyncOutLibrary(List<OrderSync> orderSyncList, Long userId) {
		if(orderSyncList != null && orderSyncList.size() > 0){
			List<String> orderNoList = new ArrayList<String> ();
			for(OrderSync orderSync : orderSyncList){
				orderNoList.add(orderSync.getOrderNumber());
			}
			
			List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory> ();
			List<Order> orderList = this.orderRepository.getOrderListByOrderNoList(orderNoList);
			if(orderList != null && orderList.size() > 0){
				for(Order order : orderList){
					//订单手动出库
					OrderHistory orderHistory = new OrderHistory ();
					orderHistory.setUserId(userId);
					orderHistory.setOrderId(order.getOrderId());
					orderHistory.setName("订单手动出库");
					orderHistory.setMessage(String.format("订单%s手动出库", order.getOrderNo()));
					orderHistory.setCreateTime(DateUtil.getCurrentDate());
					orderHistoryList.add(orderHistory);	
				}
			}
			
			orderSyncList = this.batchInsert(orderSyncList, orderSyncList.size());
			this.orderHistoryManager.batchInsert(orderHistoryList, orderHistoryList.size());
			for(final OrderSync orderSync : orderSyncList){
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						try{
							updateOrderSyncStatus(orderSync);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
		}
		return orderSyncList;
	}

	@Override
	public void updateOrderSyncExpress(OrderSync orderSync, OrderPackage orderPackage) {
		this.save(orderSync);
		this.orderPackageManager.save(orderPackage);
		
		//订单同步快递信息
		OrderHistory orderHistory = new OrderHistory ();
		orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
		orderHistory.setOrderId(orderPackage.getOrderId());
		orderHistory.setName("快递信息同步");
		orderHistory.setMessage(String.format("修改快递单号%s,快递单号已成功修改", orderSync.getExpressNumber()));
		orderHistory.setCreateTime(DateUtil.getCurrentDate());
		this.orderHistoryManager.save(orderHistory);
	}
	
	/**
	 * 检查用户销售额是否达标
	 */
	public static void checkUserSaleAmount(Long userId,boolean isbBuyPackage) {
//		try {
//			UserTeamManager userTeamManager = Constants.ctx.getBean(UserTeamManager.class);
//			UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);
//			UserSaleStandardManager userSaleStandardManager = Constants.ctx.getBean(UserSaleStandardManager.class);
//			UserSaleRecordManager userSaleRecordManager = Constants.ctx.getBean(UserSaleRecordManager.class);
//			
//			List<UserSaleStandard> standardList = userSaleStandardManager.getAll();
//			if (standardList != null && standardList.size() > 0) {
//				// v2、v3升级标准
//				UserSaleStandard userSaleStandard = standardList.get(0);
//				if (userSaleStandard != null && userSaleStandard.getId() != null
//						&& StringUtil.nullToDouble(userSaleStandard.getV2SaleAmount()) > 0
//						&& StringUtil.nullToDouble(userSaleStandard.getV3SaleAmount()) > 0) {
//					Double totalSaleAmount = new Double(0);
//					List<UserSaleRecord> userSaleRecordList = userSaleRecordManager.getUserSaleRecordListByUserId(userId);
//					if (userSaleRecordList != null && userSaleRecordList.size() > 0) {
//						List<Integer> monthStatusList = new ArrayList<Integer>();
//						monthStatusList.add(OrderStatus.DELIVER_ORDER_STATUS); // 已发货
//						monthStatusList.add(OrderStatus.OVER_ORDER_STATUS); // 已完成
//						String currentMonth = DateUtil.formatDate(DateUtil.DATE_YYYY_MM_PATTERN,DateUtil.getCurrentDate());
//						for (UserSaleRecord record : userSaleRecordList) {
//
//							if (!monthStatusList.contains(record.getOrderStatus())
//									|| record.getOrderSentTime() == null) {
//								continue;
//							}
//
//							String month = DateUtil.formatDate(DateUtil.DATE_YYYY_MM_PATTERN,record.getOrderSentTime());
//							if (StringUtil.compareObject(currentMonth, month)) {
//								//按照实际付款的计算，退款中的照样计算
//								totalSaleAmount += StringUtil.nullToDoubleFormat(record.getSaleAmount());
//							}
//						}
//					}
//
//					// 检查用户是否满足销售额标准
//					UserInfo userInfo = userInfoManager.get(userId);
//					if (userInfo != null && userInfo.getUserId() != null) {
//						boolean isChangLevel = false;
//						Integer level = StringUtil.nullToInteger(userInfo.getLevel());
//						List<Integer> v1LevelList = new ArrayList<Integer>();
//						v1LevelList.add(UserLevel.USER_LEVEL_DEALER);
//						v1LevelList.add(UserLevel.USER_LEVEL_AGENT);
//						if (StringUtil.compareObject(UserLevel.USER_LEVEL_BUYERS, level) && isbBuyPackage) {
//							if (totalSaleAmount.compareTo(userSaleStandard.getV3SaleAmount()) >= 0) {
//								isChangLevel = true;
//								userInfo.setLevel(UserLevel.USER_LEVEL_V3);
//							} else if (totalSaleAmount.compareTo(userSaleStandard.getV2SaleAmount()) >= 0) {
//								isChangLevel = true;
//								userInfo.setLevel(UserLevel.USER_LEVEL_V2);
//							}
//						} else if (v1LevelList.contains(level)) {
//							if (totalSaleAmount.compareTo(userSaleStandard.getV3SaleAmount()) >= 0) {
//								isChangLevel = true;
//								userInfo.setLevel(UserLevel.USER_LEVEL_V3);
//							} else if (totalSaleAmount.compareTo(userSaleStandard.getV2SaleAmount()) >= 0) {
//								isChangLevel = true;
//								userInfo.setLevel(UserLevel.USER_LEVEL_V2);
//							}
//						} else if (StringUtil.compareObject(level, UserLevel.USER_LEVEL_V2)) {
//							if (totalSaleAmount.compareTo(userSaleStandard.getV3SaleAmount()) >= 0) {
//								isChangLevel = true;
//								userInfo.setLevel(UserLevel.USER_LEVEL_V3);
//							}
//						}
//
//						if (isChangLevel) {
//							userInfo.setUpdateTime(DateUtil.getCurrentDate());
//							userInfo= userInfoManager.save(userInfo);
//							//维护用户团队信息
//							userTeamManager.changeUserTeamRecord(userInfo);
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
}
