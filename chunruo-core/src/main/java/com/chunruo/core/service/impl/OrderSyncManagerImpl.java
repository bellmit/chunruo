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
		// ?????????????????????
		List<Integer> suppertUpdateStatusList = new ArrayList<Integer> ();
		suppertUpdateStatusList.add(OrderStatus.DELIVER_ORDER_STATUS);	//?????????
		suppertUpdateStatusList.add(OrderStatus.OVER_ORDER_STATUS);		//????????? 
					
		// ????????????????????????
		Order order = this.orderRepository.getOrderByOrderNo(orderSync.getOrderNumber());
		if(order == null || order.getOrderId() == null){
			return;
		}else if(StringUtil.compareObject(orderSync.getOrderStatus(), OrderSync.ORDER_SYNC_STATUS_CANCEL)){
			if(suppertUpdateStatusList.contains(order.getStatus())){
				// ???????????????????????????
				return;
			}
		}

		// ?????????????????????????????????????????????????????????
		if(StringUtil.nullToBoolean(order.getIsSyncExpress())
				&& StringUtil.nullToBoolean(order.getIsIntercept())
				&& StringUtil.compareObject(orderSync.getOrderStatus(), OrderSync.ORDER_SYNC_STATUS_CANCEL)){
			// ????????????????????????????????????
			// ?????????????????????????????????????????????
			return;
		}
				
		List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory> ();
		if(StringUtil.compareObject(orderSync.getOrderStatus(), OrderSync.ORDER_SYNC_STATUS_SUCCESS)){
			// ??????????????????????????????????????????(??????????????????)
			OrderPackage orderPackage = new OrderPackage ();
			orderPackage.setCreateTime(DateUtil.getCurrentDate());
			List<OrderPackage> orderPackageList = this.orderPackageManager.getOrderPackageListByOrderId(order.getOrderId());
			if(orderPackageList != null && orderPackageList.size() > 0){
				orderPackage = orderPackageList.get(0);
			}
			
			orderPackage.setOrderId(order.getOrderId());
			orderPackage.setExpressCode(orderSync.getLogisticCode());		// ??????????????????
			orderPackage.setExpressCompany(orderSync.getLogisticName());	// ??????????????????
			orderPackage.setExpressNo(orderSync.getExpressNumber());		// ????????????
			orderPackage.setIsHandler(orderSync.getIsHandler());			//??????????????????????????????
			orderPackage.setUpdateTime(DateUtil.getCurrentDate());
			
			// ??????????????????100??????
			if(Constants.EXPRESS_CODE_MAP.containsKey(orderSync.getLogisticName().toLowerCase())){
				ExpressCode expressCode = Constants.EXPRESS_CODE_MAP.get(orderSync.getLogisticName().toLowerCase());
				orderPackage.setExpressCode(expressCode.getCompanyCode());
				orderPackage.setExpressCompany(expressCode.getCompanyName());
			}
			
			// ??????????????????????????????????????????
			boolean isExchangeExpress = false; 
			if(!suppertUpdateStatusList.contains(StringUtil.nullToInteger(order.getStatus()))){
				// ?????????????????????????????????????????????
				order.setStatus(OrderStatus.DELIVER_ORDER_STATUS);
				order.setSentTime(DateUtil.getCurrentDate());
				isExchangeExpress = true;
			}
			
			// ERP????????????
			order.setIsSyncExpress(true);
			order.setIsIntercept(false);
			order.setUpdateTime(DateUtil.getCurrentDate());
			this.orderRepository.save(order);
			this.orderPackageManager.save(orderPackage);
			
			//????????????????????????
			OrderHistory orderHistory = new OrderHistory ();
			orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
			orderHistory.setOrderId(order.getOrderId());
			orderHistory.setName("??????????????????");
			orderHistory.setMessage(String.format("??????????????????%s,?????????????????????", orderSync.getExpressNumber()));
			orderHistory.setCreateTime(DateUtil.getCurrentDate());
			orderHistoryList.add(orderHistory);
			

			//???????????????????????????
			UserSaleRecord userSaleRecord = this.userSaleRecordManager.getUserSaleRecordByOrderId(StringUtil.nullToLong(order.getOrderId()));
            if(userSaleRecord != null && userSaleRecord.getRecordId() != null) {
            	userSaleRecord.setOrderStatus(OrderStatus.DELIVER_ORDER_STATUS);
            	userSaleRecord.setOrderSentTime(DateUtil.getCurrentDate());
            	userSaleRecord.setUpdateTime(DateUtil.getCurrentDate());
            	this.userSaleRecordManager.save(userSaleRecord);
            }
            
            try {
            	 lock.lock();
            	  //????????????????????????????????????
                 OrderSyncManagerImpl.checkUserSaleAmount(StringUtil.nullToLong(order.getStoreId()), false);
            }catch(Exception e) {
            	e.printStackTrace();
            }finally {
            	lock.unlock();
            }
          
            Order realOrder = order;
			//??????????????????
			List<OrderItems> orderItemsList = this.orderItemsManager.getOrderItemsListByOrderId(order.getOrderId());
			// ???????????????????????????????????????????????????
			if(StringUtil.nullToBoolean(isExchangeExpress) && StringUtil.nullToBoolean(order.getIsSubOrder())){
				Order parentOrder = this.orderRepository.findOne(order.getParentOrderId());
				if(parentOrder != null && parentOrder.getOrderId() != null) {
					orderItemsList = this.orderItemsManager.getOrderSubItemsListByOrderId(StringUtil.nullToLong(parentOrder.getOrderId()), order.getOrderId());
					realOrder = parentOrder;
				}
				
				//????????????????????????
				OrderHistory xorderHistory = new OrderHistory ();
				xorderHistory.setUserId(Constants.ADMINSTARTOR_ID);
				xorderHistory.setOrderId(order.getParentOrderId());
				xorderHistory.setName("??????????????????");
				xorderHistory.setMessage(String.format("??????????????????%s,?????????????????????", orderSync.getExpressNumber()));
				xorderHistory.setCreateTime(DateUtil.getCurrentDate());
				orderHistoryList.add(xorderHistory);
				// ?????????????????????????????????
				this.orderRepository.updateParentOrderStatus(parentOrder.getOrderId(), OrderStatus.DELIVER_ORDER_STATUS);
			}
			this.orderHistoryManager.batchInsert(orderHistoryList, orderHistoryList.size());
		}else{
			// ERP????????????
			order.setIsSyncExpress(true);
			order.setIsIntercept(true);    					// ?????????????????????
			order.setUpdateTime(DateUtil.getCurrentDate());
			this.orderRepository.save(order);

			//????????????????????????
			OrderHistory orderHistory = new OrderHistory ();
			orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
			orderHistory.setOrderId(order.getOrderId());
			orderHistory.setName("????????????");
			orderHistory.setMessage(String.format("??????%s??????ERP??????", order.getOrderNo()));
			orderHistory.setCreateTime(DateUtil.getCurrentDate());
			orderHistoryList.add(orderHistory);	

			// ???????????????????????????????????????????????????
			if(StringUtil.nullToBoolean(order.getIsSubOrder())){
				//????????????????????????
				OrderHistory pOrderHistory = new OrderHistory ();
				pOrderHistory.setUserId(Constants.ADMINSTARTOR_ID);
				pOrderHistory.setOrderId(order.getOrderId());
				pOrderHistory.setName("????????????");
				pOrderHistory.setMessage(String.format("?????????%s??????ERP??????", order.getOrderNo()));
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
					//??????????????????
					OrderHistory orderHistory = new OrderHistory ();
					orderHistory.setUserId(userId);
					orderHistory.setOrderId(order.getOrderId());
					orderHistory.setName("??????????????????");
					orderHistory.setMessage(String.format("??????%s????????????", order.getOrderNo()));
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
		
		//????????????????????????
		OrderHistory orderHistory = new OrderHistory ();
		orderHistory.setUserId(Constants.ADMINSTARTOR_ID);
		orderHistory.setOrderId(orderPackage.getOrderId());
		orderHistory.setName("??????????????????");
		orderHistory.setMessage(String.format("??????????????????%s,???????????????????????????", orderSync.getExpressNumber()));
		orderHistory.setCreateTime(DateUtil.getCurrentDate());
		this.orderHistoryManager.save(orderHistory);
	}
	
	/**
	 * ?????????????????????????????????
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
//				// v2???v3????????????
//				UserSaleStandard userSaleStandard = standardList.get(0);
//				if (userSaleStandard != null && userSaleStandard.getId() != null
//						&& StringUtil.nullToDouble(userSaleStandard.getV2SaleAmount()) > 0
//						&& StringUtil.nullToDouble(userSaleStandard.getV3SaleAmount()) > 0) {
//					Double totalSaleAmount = new Double(0);
//					List<UserSaleRecord> userSaleRecordList = userSaleRecordManager.getUserSaleRecordListByUserId(userId);
//					if (userSaleRecordList != null && userSaleRecordList.size() > 0) {
//						List<Integer> monthStatusList = new ArrayList<Integer>();
//						monthStatusList.add(OrderStatus.DELIVER_ORDER_STATUS); // ?????????
//						monthStatusList.add(OrderStatus.OVER_ORDER_STATUS); // ?????????
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
//								//??????????????????????????????????????????????????????
//								totalSaleAmount += StringUtil.nullToDoubleFormat(record.getSaleAmount());
//							}
//						}
//					}
//
//					// ???????????????????????????????????????
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
//							//????????????????????????
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
