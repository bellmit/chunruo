package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderHistory;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.UserAmountChangeRecord;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserProfitRecord;
import com.chunruo.core.repository.UserProfitRecordRepository;
import com.chunruo.core.service.OrderHistoryManager;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.RefundManager;
import com.chunruo.core.service.UserAmountChangeRecordManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserInviteRecordManager;
import com.chunruo.core.service.UserProfitRecordManager;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.vo.ProductOrderVo;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("userProfitRecordManager")
public class UserProfitRecordManagerImpl extends GenericManagerImpl<UserProfitRecord, Long> implements UserProfitRecordManager {
	private Lock lock = new ReentrantLock();
	private UserProfitRecordRepository userProfitRecordRepository;
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private RefundManager refundManager;
	@Autowired
	private OrderHistoryManager orderHistoryManager;
	@Autowired
	private UserInviteRecordManager userInviteRecordManager;
	@Autowired
	private UserAmountChangeRecordManager userAmountChangeRecordManager;
	
	@Autowired
	public UserProfitRecordManagerImpl(UserProfitRecordRepository userProfitRecordRepository) {
		super(userProfitRecordRepository);
		this.userProfitRecordRepository = userProfitRecordRepository;
	}

	@Override
	public List<UserProfitRecord> getUserProfitRecordList(Long userId) {
		return this.userProfitRecordRepository.getUserProfitRecordList(userId);
	}

	@Override
	public List<UserProfitRecord> getUserProfitRecordByOrderNo(String orderNo) {
		return this.userProfitRecordRepository.getUserProfitRecordByOrderNo(orderNo);
	}

	@Override
	public List<UserProfitRecord> getUserProfitRecordByOrderId(Long orderId) {
		return this.userProfitRecordRepository.getUserProfitRecordByOrderId(orderId);
	}
	
	@Override
	public List<UserProfitRecord> getUserProfitRecordByStatus(int status) {
		return this.userProfitRecordRepository.getUserProfitRecordByStatus(status);
	}
	
	@Override
	public void updateUserProfitRecordStatusByOrderId(Long orderId, Integer status) {
		this.userProfitRecordRepository.updateUserProfitRecordStatusByOrderId(orderId, status, DateUtil.getCurrentDate());
	}
	
	@Override
	public void updateUserProfitRecordStatusByOrderIdList(List<Long> orderIdList, Integer status){
		this.userProfitRecordRepository.updateUserProfitRecordStatusByOrderIdList(orderIdList, status, DateUtil.getCurrentDate());
	}
	
	@Override
	public List<UserProfitRecord> getUserProfitRecordListByUpdateTime(Date updateTime) {
		return this.userProfitRecordRepository.getUserProfitRecordListByUpdateTime(updateTime);
	}

	@Override
	public List<UserProfitRecord> getUserProfitRecordListByFromUserId(Long fromUserId) {
		return this.userProfitRecordRepository.getUserProfitRecordListByFromUserId(fromUserId);
	}
	
	@Override
	public Double countUserProfitTotalIncomeByUserId(Long userId){
		Double totalInCome = 0.0D;
		String sql = "select sum(income) from jkd_user_profit_record where user_id = ? and status = ? and type in (?,?)";
		List<Object[]> objectList = this.querySql(sql, new Object[]{userId, UserProfitRecord.DISTRIBUTION_STATUS_SUCC, UserProfitRecord.DISTRIBUTION_TYPE_FX, UserProfitRecord.DISTRIBUTION_TYPE_VIP});
		if(objectList != null && objectList.size() > 0){
			totalInCome = StringUtil.nullToDoubleFormat(objectList.get(0));
		}
		return totalInCome;
	}
	
	@Override
	public void batchUpdateProfitRecords(Order order, List<Long> childOrderIdList){
		if(childOrderIdList == null || childOrderIdList.size() <= 0){
			return;
		}
		
		Long topUserId = 0L; 			// ????????????ID
		Long storeUserId = 0L;          // ????????????ID
		Double profitTop = 0.0D; 		// ????????????
		Double profitSub = 0.0D;        // ????????????
		List<UserProfitRecord> userProfitRecordList = new ArrayList<UserProfitRecord> ();
		
		// ???????????????????????????????????????
		List<Integer> supperShareAgentLevelList = new ArrayList<Integer> ();
		supperShareAgentLevelList.add(UserLevel.USER_LEVEL_DEALER);	//?????????
		supperShareAgentLevelList.add(UserLevel.USER_LEVEL_AGENT);	//??????
		supperShareAgentLevelList.add(UserLevel.USER_LEVEL_V2);	    //V2
		supperShareAgentLevelList.add(UserLevel.USER_LEVEL_V3);	    //V3
		// ??????????????????????????????
		UserInfo topUserInfo = this.userInfoManager.get(order.getTopUserId());
		if(topUserInfo != null
				&& StringUtil.nullToBoolean(topUserInfo.getIsAgent()) 
				&& supperShareAgentLevelList.contains(StringUtil.nullToInteger(topUserInfo.getLevel()))){
			topUserId = StringUtil.nullToLong(topUserInfo.getUserId());
		}
		
		//????????????????????????????????????
		if(StringUtil.nullToBoolean(order.getIsShareBuy())) {
			UserInfo storeUserInfo = this.userInfoManager.get(StringUtil.nullToLong(order.getStoreId()));
			if(storeUserInfo != null 
					&& storeUserInfo.getUserId() != null
					&& StringUtil.nullToBoolean(topUserInfo.getIsAgent()) 
					&& supperShareAgentLevelList.contains(StringUtil.nullToInteger(topUserInfo.getLevel()))) {
				storeUserId = StringUtil.nullToLong(storeUserInfo.getUserId());
			}
		}
		
		// ????????????????????????????????????
		List<Order> orderList = this.orderManager.getByIdList(childOrderIdList);
		if(orderList != null && orderList.size() > 0){
			for(Order childOrder : orderList){
				if(StringUtil.compareObject(childOrder.getParentOrderId(), order.getOrderId())){
					profitTop = StringUtil.nullToDoubleFormat(profitTop + childOrder.getProfitTop());
					profitSub = StringUtil.nullToDoubleFormat(profitSub + childOrder.getProfitSub());
				}
			}
		}
		
		// ????????????????????????
		List<UserProfitRecord> topStoreRecordList = this.userProfitRecordRepository.getUserProfitRecordByOrderId(order.getOrderId(), topUserId);
		if(topStoreRecordList != null && topStoreRecordList.size() > 0){
			UserProfitRecord topStoreRecord = topStoreRecordList.get(0);
			topStoreRecord.setIncome(profitTop);
			topStoreRecord.setUpdateTime(DateUtil.getCurrentDate());
			userProfitRecordList.add(topStoreRecord);
		}
		
		// ????????????????????????
		List<UserProfitRecord> shareStoreRecordList = this.userProfitRecordRepository.getUserProfitRecordByOrderId(order.getOrderId(), storeUserId);
		if(shareStoreRecordList != null && shareStoreRecordList.size() > 0){
			UserProfitRecord shareStoreRecord = shareStoreRecordList.get(0);
			shareStoreRecord.setIncome(profitSub);
			shareStoreRecord.setUpdateTime(DateUtil.getCurrentDate());
			userProfitRecordList.add(shareStoreRecord);
		}
		this.userProfitRecordRepository.batchInsert(userProfitRecordList, userProfitRecordList.size());
	}
	
	@Override
	public Map<String, List<Long>> batchInsertProfitRecords(Order order) {
		lock.lock();
		try {
			StringBuffer sqlBuffer = new StringBuffer ();
			sqlBuffer.append("select i.price_cost,i.quantity,w.stock_number,w.seckill_sales_number, ");
			sqlBuffer.append("w.is_soldout,w.product_id,i.product_spec_id,i.is_spce_product,i.is_seckill_product,w.sales_number,i.is_gift_product,w.max_limit_number limitMaxNumber ");
			sqlBuffer.append("from jkd_order_items i, jkd_product w ");
			sqlBuffer.append("where i.order_id = ? and i.is_spce_product = 0 ");
			sqlBuffer.append("and i.product_id = w.product_id  ");
			sqlBuffer.append("UNION ");
			sqlBuffer.append("select s.price_cost,i.quantity,s.stock_number,s.seckill_sales_number, ");
			sqlBuffer.append("w.is_soldout,w.product_id,i.product_spec_id,i.is_spce_product,i.is_seckill_product,s.sales_number,i.is_gift_product,0 limitMaxNumber ");
			sqlBuffer.append("from jkd_order_items i, jkd_product_spec s, jkd_product w ");
			sqlBuffer.append("where i.order_id = ? and i.is_spce_product = 1 ");
			sqlBuffer.append("and s.product_spec_id = i.product_spec_id ");
			sqlBuffer.append("and s.product_id = w.product_id ");
			
			
			List<Long> productIdList = new ArrayList<Long>();
			List<ProductOrderVo> productOrderList = new ArrayList<ProductOrderVo> ();
			List<Object[]> list = this.querySql(sqlBuffer.toString(), new Object[]{order.getOrderId(), order.getOrderId()});
			if(list != null && list.size() > 0){
				for(Object[] obj : list){
					Double priceCost = StringUtil.nullToDoubleFormat(obj[0]);
					Integer quantity = StringUtil.nullToInteger(obj[1]);
					Integer stockNumber = StringUtil.nullToInteger(obj[2]);
					Integer seckillSalesNumber = StringUtil.nullToInteger(obj[3]);
					Boolean isSoldout = StringUtil.nullToBoolean(obj[4]);
					Long productId = StringUtil.nullToLong(obj[5]);
					Long productSpecId = StringUtil.nullToLong(obj[6]);
					Boolean isSpceProduct = StringUtil.nullToBoolean(obj[7]);
					Boolean isSeckillProduct = StringUtil.nullToBoolean(obj[8]);
					Integer salesNumber = StringUtil.nullToInteger(obj[9]);
					Boolean isGiftProduct = StringUtil.nullToBoolean(obj[10]);
					Integer limitMaxNumber = StringUtil.nullToInteger(obj[11]);

					Double totalPriceCost = StringUtil.nullToDoubleFormat(priceCost * quantity);
					
					ProductOrderVo productOrder = new ProductOrderVo();
					productOrder.setPriceCost(priceCost);
					productOrder.setQuantity(quantity);
					productOrder.setStockNumber(stockNumber);
					productOrder.setSeckillSalesNumber(seckillSalesNumber);
					productOrder.setIsSoldout(isSoldout);
					productOrder.setProductId(productId);
					productOrder.setTotalPriceCost(totalPriceCost);
					productOrder.setIsSpceProduct(isSpceProduct);
					productOrder.setProductSpecId(productSpecId);
					productOrder.setIsSeckillProduct(isSeckillProduct);
					productOrder.setSalesNumber(salesNumber);
					productOrder.setIsGiftProduct(isGiftProduct);
					productOrder.setLimitMaxNumber(limitMaxNumber);
					productOrderList.add(productOrder);
					productIdList.add(productId);
				}
			}
			
			List<UserProfitRecord> userProfitRecordList = new ArrayList<UserProfitRecord>();
			Long orderId = order.getOrderId();							
			String orderNo = order.getOrderNo();						
			Double orderAmount = order.getOrderAmount(); 				
			Integer profitType = UserProfitRecord.DISTRIBUTION_TYPE_FX; 
			Long fromUserId = order.getUserId();						
			Long topUserId = 0L; 										
			Long storeId = 0L;            
			Long shareUserId = 0L;
			Double profitTop = 0.0D; 									
			Double profitSub = 0.0D;                       
			
			// ??????????????????????????????
			UserInfo topUserInfo = this.userInfoManager.get(order.getTopUserId());
			if(topUserInfo != null
					&& StringUtil.nullToBoolean(topUserInfo.getIsAgent()) 
					&& StringUtil.compareObject(topUserInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)){
				topUserId = StringUtil.nullToLong(topUserInfo.getUserId());
				profitTop = StringUtil.nullToDoubleFormat(order.getProfitTop());
			}
			
			//????????????
			UserInfo shareUserInfo = this.userInfoManager.get(order.getShareUserId());
            if(shareUserInfo != null
            		&& shareUserInfo.getUserId() != null
            		 && StringUtil.nullToBoolean(shareUserInfo.getIsAgent())) {
            	shareUserId = StringUtil.nullToLong(shareUserInfo.getUserId());
            	profitSub = StringUtil.nullToDoubleFormat(order.getProfitSub());
            }
			
			
			Map<String, List<Long>> resultMap = new HashMap<String, List<Long>> ();
			if(StringUtil.nullToBoolean(order.getIsInvitationAgent())){
				profitType = UserProfitRecord.DISTRIBUTION_TYPE_VIP;
				
				MsgModel<Map<String, List<Long>>> msgModel = this.userInviteRecordManager.insertInviteRecordByOrder(order);
				if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
					resultMap.putAll(msgModel.getData());
				}
			}
			
			if(profitTop.compareTo(0.0D) > 0) {
				UserProfitRecord topUserProfitRecord = new UserProfitRecord();
				topUserProfitRecord.setUserId(topUserId);
				if(StringUtil.nullToBoolean(order.getIsShareBuy())) {
					topUserProfitRecord.setFromUserId(storeId);
				}else {
					topUserProfitRecord.setFromUserId(fromUserId);
				}
				topUserProfitRecord.setOrderId(orderId);
				topUserProfitRecord.setOrderNo(orderNo);
				topUserProfitRecord.setIncome(profitTop);
				topUserProfitRecord.setMtype(UserProfitRecord.DISTRIBUTION_MTYPE_TOP);
				topUserProfitRecord.setStatus(UserProfitRecord.DISTRIBUTION_STATUS_INIT);
				topUserProfitRecord.setType(profitType);
				topUserProfitRecord.setCreateTime(DateUtil.getCurrentDate());
				topUserProfitRecord.setUpdateTime(topUserProfitRecord.getCreateTime());
				userProfitRecordList.add(topUserProfitRecord);
			}
			
			if( profitSub.compareTo(0.0D) > 0) {
				UserProfitRecord shareUserProfitRecord = new UserProfitRecord();
				shareUserProfitRecord.setUserId(shareUserId);
				shareUserProfitRecord.setFromUserId(fromUserId);
				shareUserProfitRecord.setOrderId(orderId);
				shareUserProfitRecord.setOrderNo(orderNo);
				shareUserProfitRecord.setIncome(profitSub);
				shareUserProfitRecord.setMtype(UserProfitRecord.DISTRIBUTION_MTYPE_DOWN);
				shareUserProfitRecord.setStatus(UserProfitRecord.DISTRIBUTION_STATUS_INIT);
				shareUserProfitRecord.setType(UserProfitRecord.DISTRIBUTION_TYPE_FX);
				shareUserProfitRecord.setCreateTime(DateUtil.getCurrentDate());
				shareUserProfitRecord.setUpdateTime(shareUserProfitRecord.getCreateTime());	
				userProfitRecordList.add(shareUserProfitRecord);
			}
			
		
			
			List<Object[]> productSplList = new ArrayList<Object[]> ();
			List<Object[]> productSpecSplList = new ArrayList<Object[]> ();

			if(productOrderList != null && productOrderList.size() > 0){
				Set<Long> higherProductIdList = new HashSet<Long> ();
				for(ProductOrderVo productOrder : productOrderList){
					Boolean isSoldout = productOrder.getIsSoldout();
					if(StringUtil.nullToBoolean(productOrder.getIsSpceProduct())){
						Integer seckillSalesNumber = StringUtil.nullToInteger(productOrder.getSeckillSalesNumber());
						if(StringUtil.nullToBoolean(productOrder.getIsSeckillProduct())){
							seckillSalesNumber += productOrder.getQuantity();
							if(seckillSalesNumber <= 0){
								seckillSalesNumber = 0;
							}
						}
						
						Integer stockNumber = productOrder.getStockNumber() - productOrder.getQuantity();
						if(stockNumber <= 0){
							stockNumber = 0;
						}
						
						Integer salesNumber = StringUtil.nullToInteger(productOrder.getSalesNumber()) + productOrder.getQuantity();
						
						productSpecSplList.add(new Object[]{stockNumber, seckillSalesNumber, salesNumber,productOrder.getProductSpecId()});
						String logs = "productSpec[orderId=%s,isSeckill=%s,srcStockNumber=%s,quantity=%s,stockNumber=%s,seckillSalesNumber=%s,productId=%s]";
						log.debug(String.format(logs, order.getOrderId(), StringUtil.nullToBoolean(productOrder.getIsSeckillProduct()), productOrder.getStockNumber(), productOrder.getQuantity(), stockNumber, seckillSalesNumber, productOrder.getProductId()));
						
						if(higherProductIdList.contains(productOrder.getProductId())){
							higherProductIdList.add(productOrder.getProductId());
							productSplList.add(new Object[]{0, 0, isSoldout,salesNumber, productOrder.getProductId()});
						}
					}else{
						Integer seckillSalesNumber = StringUtil.nullToInteger(productOrder.getSeckillSalesNumber());
						if(StringUtil.nullToBoolean(productOrder.getIsSeckillProduct())){
							seckillSalesNumber += productOrder.getQuantity();
							if(seckillSalesNumber <= 0){
								seckillSalesNumber = 0;
							}
						}
						
						Integer stockNumber = productOrder.getStockNumber() - productOrder.getQuantity();
						if(stockNumber <= 0){
							stockNumber = 0;
							isSoldout = true;
						}
						
						Integer salesNumber = StringUtil.nullToInteger(productOrder.getSalesNumber()) + productOrder.getQuantity();
                        Integer maxLimitNumber = StringUtil.nullToInteger(productOrder.getLimitMaxNumber()) + productOrder.getQuantity();
						productSplList.add(new Object[]{stockNumber, seckillSalesNumber, isSoldout,salesNumber, maxLimitNumber,productOrder.getProductId()});
						String logs = "productSpec[orderId=%s,isSeckill=%s,srcStockNumber=%s,quantity=%s,stockNumber=%s,seckillSalesNumber=%s,isSoldout=%s,productId=%s]";
						log.debug(String.format(logs, order.getOrderId(), StringUtil.nullToBoolean(productOrder.getIsSeckillProduct()), productOrder.getStockNumber(), productOrder.getQuantity(), stockNumber, seckillSalesNumber, isSoldout, productOrder.getProductId()));
				
					}
				}
			}
			
			this.batchInsert(userProfitRecordList, userProfitRecordList.size());
			this.executeSql("update jkd_user_info set sales=truncate(ifnull(sales, 0) + ?, 2), update_time=now() where user_id=?", new Object[]{orderAmount, order.getStoreId()});
			this.batchUpdate("update jkd_product set stock_number=?, seckill_sales_number=?, is_soldout=?,sales_number=?,max_limit_number=?, update_time=now() where product_id=?", productSplList);
			this.batchUpdate("update jkd_product_spec set stock_number=?, seckill_sales_number=?,sales_number=?, update_time=now() where product_spec_id=?", productSpecSplList);

			if(StringUtil.nullToBoolean(order.getIsInvitationAgent())){
				this.orderCheckUpdateRecord(order.getOrderId());
			}
			
			resultMap.put("productIdList", productIdList);
			return resultMap;
		}finally {
			lock.unlock();
		}
	}
	
	@Override
	public void orderCheckUpdateRecord(Long recordId){
		// ??????
		lock.lock();
		try{
			Order order = this.orderManager.get(recordId);
			// ????????????????????????
			if(!StringUtil.nullToBoolean(order.getIsInvitationAgent())){
				if(!StringUtil.compareObject(order.getStatus(), OrderStatus.OVER_ORDER_STATUS) 
						|| StringUtil.nullToBoolean(order.getIsCheck())){
					// ?????????????????????
					return;
				}
				
				// ????????????????????????????????????????????????
				List<Refund> refundList = this.refundManager.getRefundListByOrderId(order.getOrderId(), true);
				if(!CollectionUtils.isEmpty(refundList)){
					List<Integer> unRefundStatusList = new ArrayList<Integer> ();
					unRefundStatusList.add(Refund.REFUND_STATUS_COMPLETED);	//????????????
					unRefundStatusList.add(Refund.REFUND_STATUS_REFUSE);	//????????????
					unRefundStatusList.add(Refund.REFUND_STATUS_TIMEOUT);	//???????????????
					for(Refund refund : refundList){
						Date beforeDate = DateUtil.getDateBeforeByDay(DateUtil.getCurrentDate(), 40);
						if(refund.getCreateTime().compareTo(beforeDate) > 0 && !unRefundStatusList.contains(refund.getRefundStatus())){
							// ?????????????????????????????????????????????
							return;
						}
					}
				}
			}
			
			// ?????????????????????
			List<UserProfitRecord> pushUserProfitRecordList = new ArrayList<UserProfitRecord> ();
			List<UserProfitRecord> UserProfitRecordList = this.getUserProfitRecordByOrderId(order.getOrderId());
			if(!CollectionUtils.isEmpty(UserProfitRecordList)){
				List<Object[]> profitRecordList = new ArrayList<Object[]> ();
				List<UserAmountChangeRecord> changeRecordList = new ArrayList<UserAmountChangeRecord> ();
				for(UserProfitRecord record : UserProfitRecordList){
					Double income = StringUtil.nullToDouble(record.getIncome());
					if(income.compareTo(0.01) >= 0 && StringUtil.compareObject(UserProfitRecord.DISTRIBUTION_STATUS_INIT, record.getStatus())){
						// ?????????????????????????????????
						List<UserAmountChangeRecord> recordList = this.userAmountChangeRecordManager.getUserAmountChangeRecordByObjectId(record.getRecordId(), UserAmountChangeRecord.AMOUNT_CHANGE_CHECK);
						if(CollectionUtils.isEmpty(recordList)){
							UserInfo userInfo = this.userInfoManager.get(record.getUserId());
							if(userInfo == null
									|| userInfo.getUserId() == null
									|| !StringUtil.nullToBoolean(userInfo.getIsAgent()) 
									|| !StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)){
								continue;
							}
							
							// ???????????????
							profitRecordList.add(new Object[]{income, income, userInfo.getUserId()});
							pushUserProfitRecordList.add(record);
							
							//????????????????????????
							Double balance = StringUtil.nullToDouble(userInfo.getBalance());
							UserAmountChangeRecord changeRecord = new UserAmountChangeRecord();
							changeRecord.setUserId(userInfo.getUserId());
							changeRecord.setObjectId(record.getRecordId());
							changeRecord.setType(UserAmountChangeRecord.AMOUNT_CHANGE_CHECK);
							changeRecord.setBeforeAmount(balance);
							changeRecord.setChangeAmount(income);
							changeRecord.setAfterAmount(balance + income);
							changeRecord.setCreateTime(DateUtil.getCurrentDate());
							changeRecord.setUpdateTime(changeRecord.getCreateTime());
							changeRecordList.add(changeRecord);
						}
					}
				}
				
				// ??????????????????????????????
				if(!CollectionUtils.isEmpty(profitRecordList)){
					this.userAmountChangeRecordManager.batchInsert(changeRecordList, changeRecordList.size());
					this.batchUpdate("update jkd_user_info set balance=truncate(ifnull(balance,0) + ?, 2), income=truncate(ifnull(income,0) + ?, 2), update_time=now() where user_id=?", profitRecordList);
				}
				
				// ????????????????????????????????????
				this.userProfitRecordRepository.updateUserProfitRecordStatusByOrderId(order.getOrderId(), UserProfitRecord.DISTRIBUTION_STATUS_SUCC, DateUtil.getCurrentDate());
			}
			
			//????????????????????????
			this.orderManager.updateOrderCheckById(true, order.getOrderId());
			OrderHistory orderHistory = this.orderHistoryManager.createOrderHistoryBean(order.getOrderId(), "????????????", "??????????????????");
			this.orderHistoryManager.save(orderHistory);
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			// ?????????
			lock.unlock();     
		}
	}

	@Override
	public void updateUserProfitRecordStatusByOrderId(Long orderId, Integer status, Double income) {
		this.userProfitRecordRepository.updateUserProfitRecordStatusByOrderId(orderId, status, income,DateUtil.getCurrentDate());
	}

	@Override
	public List<UserProfitRecord> getUserProfitRecordListByCurrentMonth() {
		return this.userProfitRecordRepository.getUserProfitRecordListByCurrentMonth();
	}

	@Override
	public void insertProfitRecordsByIsInvitationAgent(Order order) {
		lock.lock();
		try {
			
			// ??????????????????????????????
			Long topUserId =0L;
			Double profitTop = new Double(0);
			UserInfo topUserInfo = this.userInfoManager.get(order.getTopUserId());
			if(topUserInfo != null
					&& StringUtil.nullToBoolean(topUserInfo.getIsAgent()) 
					&& StringUtil.compareObject(topUserInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)){
				topUserId = StringUtil.nullToLong(topUserInfo.getUserId());
				profitTop = order.getProfitTop();
			}
			
			// ??????????????????????????????
			UserProfitRecord topUserProfitRecord = new UserProfitRecord();
			topUserProfitRecord.setUserId(topUserId);
			topUserProfitRecord.setFromUserId(StringUtil.nullToLong(order.getUserId()));
			topUserProfitRecord.setOrderId(StringUtil.nullToLong(order.getOrderId()));
			topUserProfitRecord.setOrderNo(StringUtil.null2Str(order.getOrderNo()));
			topUserProfitRecord.setIncome(StringUtil.nullToDoubleFormat(profitTop));
			topUserProfitRecord.setMtype(UserProfitRecord.DISTRIBUTION_MTYPE_TOP);
			topUserProfitRecord.setStatus(UserProfitRecord.DISTRIBUTION_STATUS_INIT);
			topUserProfitRecord.setType(UserProfitRecord.DISTRIBUTION_TYPE_VIP);
			topUserProfitRecord.setCreateTime(DateUtil.getCurrentDate());
			topUserProfitRecord.setUpdateTime(topUserProfitRecord.getCreateTime());
			this.save(topUserProfitRecord);
			
			// ??????????????????????????????
			this.userInviteRecordManager.insertInviteRecordByOrder(order);
			// ????????????????????????
			this.orderCheckUpdateRecord(order.getOrderId());
			
			//??????????????????
			UserInfo userInfo = this.userInfoManager.get(StringUtil.nullToLong(order.getUserId()));
			if(userInfo != null && userInfo.getUserId() != null) {
				userInfo.setTopUserId(topUserId);
				userInfo.setUpdateTime(DateUtil.getCurrentDate());
				this.userInfoManager.save(userInfo);
			}
		}finally {
			lock.unlock();
		}
	}
	@Override
	public List<Object[]> getUserProfitRecordListByCondition(List<Long> bdUserIdList,Integer status,String keyword,String beginTime ,String endTime) {
	    if(bdUserIdList == null || bdUserIdList.size() <= 0 ) {
	    	return null;
	    }
		StringBuilder strBulSql = new StringBuilder();
		strBulSql.append("select jo.order_id,jo.profit_top,jo.status order_status,jo.create_time,cc.status profit_status,cc.income,cc.update_time complate_time,bb.bd_user_id,");
		strBulSql.append("jui.user_id,jui.nick_name,jui.mobile,cc.record_id  from ");
		strBulSql.append("jkd_order jo ");
		strBulSql.append("inner join ");
		strBulSql.append("(select jbui.user_id,jbui.bd_user_id from jkd_bd_user_invite jbui WHERE jbui.bd_user_id IN(%s) and jbui.type = 1  and (jbui.is_dock is null or jbui.is_dock = 0) ");
		strBulSql.append(") bb on jo.store_id = bb.user_id ");
		strBulSql.append("inner join ");
		strBulSql.append("jkd_user_profit_record  cc ");
		strBulSql.append("on (cc.from_user_id,cc.order_id) = (jo.store_id,jo.order_id) ");
		strBulSql.append("inner join ");
		strBulSql.append("jkd_user_info jui ");
		strBulSql.append("on jo.store_id = jui.user_id ");
		
		strBulSql.append("where cc.type = 5  and cc.mtype = 3 ");
		if(StringUtil.compareObject(status, UserProfitRecord.DISTRIBUTION_STATUS_SUCC)) {
			strBulSql.append("and cc.status in(2,3) ");
		}else {
			strBulSql.append("and cc.status = 1 ");
		}
		
		// ????????????????????????
		if(DateUtil.isEffectiveTime(DateUtil.dateFormat, StringUtil.null2Str(beginTime))
				&& DateUtil.isEffectiveTime(DateUtil.dateFormat, StringUtil.null2Str(endTime))){
			if(StringUtil.compareObject(status, UserProfitRecord.DISTRIBUTION_STATUS_INIT)) {
	            strBulSql.append("and jo.create_time between '"+beginTime+"' and '"+endTime+"' ");
			}else if(StringUtil.compareObject(status, UserProfitRecord.DISTRIBUTION_STATUS_SUCC)) {
				strBulSql.append("and cc.update_time between '"+beginTime+"' and '"+endTime+"' ");
			}
		}
		strBulSql.append(" group by jo.order_id,jo.store_id order by cc.update_time desc");
		log.info(strBulSql.toString());
		return this.querySql(String.format(strBulSql.toString(),StringUtil.longListToStr(bdUserIdList),status));
	}

	@Override
	public List<UserProfitRecord> getUserProfitRecordListByFromUserIdList(List<Long> userIdList) {
		return this.userProfitRecordRepository.getUserProfitRecordListByFromUserIdList(userIdList);
	}

	@Override
	public List<Object[]> getUserProfitRecordToExcel(List<Long> bdUserIdList, String beginTime, String endTime) {
		if(bdUserIdList == null || bdUserIdList.size() <= 0) {
			return null;
		}
		StringBuilder strBulSql = new StringBuilder();
		strBulSql.append("select jupr.from_user_id,sum(jupr.income),jbui.bd_user_id from jkd_user_profit_record jupr,jkd_bd_user_invite jbui ");
		strBulSql.append("where jupr.from_user_id = jbui.user_id and  jupr.status = 3 and jupr.type=5 and jupr.mtype=3 and jbui.bd_user_id in(%s) and (jbui.is_dock is null or jbui.is_dock = 0) ");
		strBulSql.append("and jupr.update_time between '%s' and '%s' ");
		strBulSql.append("group by jbui.bd_user_id ");
		return this.querySql(String.format(strBulSql.toString(), StringUtil.longListToStr(bdUserIdList),beginTime,endTime));
	}
}

