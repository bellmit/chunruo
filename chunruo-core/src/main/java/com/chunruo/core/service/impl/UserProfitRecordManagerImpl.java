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
		
		Long topUserId = 0L; 			// 上线用户ID
		Long storeUserId = 0L;          // 分享店铺ID
		Double profitTop = 0.0D; 		// 上级利润
		Double profitSub = 0.0D;        // 分享利润
		List<UserProfitRecord> userProfitRecordList = new ArrayList<UserProfitRecord> ();
		
		// 支持上下级利润分成用户等级
		List<Integer> supperShareAgentLevelList = new ArrayList<Integer> ();
		supperShareAgentLevelList.add(UserLevel.USER_LEVEL_DEALER);	//经销商
		supperShareAgentLevelList.add(UserLevel.USER_LEVEL_AGENT);	//总代
		supperShareAgentLevelList.add(UserLevel.USER_LEVEL_V2);	    //V2
		supperShareAgentLevelList.add(UserLevel.USER_LEVEL_V3);	    //V3
		// 支持上级返利等级用户
		UserInfo topUserInfo = this.userInfoManager.get(order.getTopUserId());
		if(topUserInfo != null
				&& StringUtil.nullToBoolean(topUserInfo.getIsAgent()) 
				&& supperShareAgentLevelList.contains(StringUtil.nullToInteger(topUserInfo.getLevel()))){
			topUserId = StringUtil.nullToLong(topUserInfo.getUserId());
		}
		
		//支持分享返利等级店铺用户
		if(StringUtil.nullToBoolean(order.getIsShareBuy())) {
			UserInfo storeUserInfo = this.userInfoManager.get(StringUtil.nullToLong(order.getStoreId()));
			if(storeUserInfo != null 
					&& storeUserInfo.getUserId() != null
					&& StringUtil.nullToBoolean(topUserInfo.getIsAgent()) 
					&& supperShareAgentLevelList.contains(StringUtil.nullToInteger(topUserInfo.getLevel()))) {
				storeUserId = StringUtil.nullToLong(storeUserInfo.getUserId());
			}
		}
		
		// 重新计算子订单的利润分成
		List<Order> orderList = this.orderManager.getByIdList(childOrderIdList);
		if(orderList != null && orderList.size() > 0){
			for(Order childOrder : orderList){
				if(StringUtil.compareObject(childOrder.getParentOrderId(), order.getOrderId())){
					profitTop = StringUtil.nullToDoubleFormat(profitTop + childOrder.getProfitTop());
					profitSub = StringUtil.nullToDoubleFormat(profitSub + childOrder.getProfitSub());
				}
			}
		}
		
		// 上级店铺分销利润
		List<UserProfitRecord> topStoreRecordList = this.userProfitRecordRepository.getUserProfitRecordByOrderId(order.getOrderId(), topUserId);
		if(topStoreRecordList != null && topStoreRecordList.size() > 0){
			UserProfitRecord topStoreRecord = topStoreRecordList.get(0);
			topStoreRecord.setIncome(profitTop);
			topStoreRecord.setUpdateTime(DateUtil.getCurrentDate());
			userProfitRecordList.add(topStoreRecord);
		}
		
		// 分享店铺分享利润
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
			Double profitTop = 0.0D; 									
			
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
		// 加锁
		lock.lock();
		try{
			Order order = this.orderManager.get(recordId);
			// 普通检查订单状态
			if(!StringUtil.nullToBoolean(order.getIsInvitationAgent())){
				if(!StringUtil.compareObject(order.getStatus(), OrderStatus.OVER_ORDER_STATUS) 
						|| StringUtil.nullToBoolean(order.getIsCheck())){
					// 非结算状态订单
					return;
				}
				
				// 检查订单是否有退款退货处理中任务
				List<Refund> refundList = this.refundManager.getRefundListByOrderId(order.getOrderId(), true);
				if(!CollectionUtils.isEmpty(refundList)){
					List<Integer> unRefundStatusList = new ArrayList<Integer> ();
					unRefundStatusList.add(Refund.REFUND_STATUS_COMPLETED);	//退款完成
					unRefundStatusList.add(Refund.REFUND_STATUS_REFUSE);	//审核被拒
					unRefundStatusList.add(Refund.REFUND_STATUS_TIMEOUT);	//申请已超时
					for(Refund refund : refundList){
						Date beforeDate = DateUtil.getDateBeforeByDay(DateUtil.getCurrentDate(), 40);
						if(refund.getCreateTime().compareTo(beforeDate) > 0 && !unRefundStatusList.contains(refund.getRefundStatus())){
							// 退款退货处理中任务直接终止结算
							return;
						}
					}
				}
			}
			
			// 订单结算列表项
			List<UserProfitRecord> pushUserProfitRecordList = new ArrayList<UserProfitRecord> ();
			List<UserProfitRecord> UserProfitRecordList = this.getUserProfitRecordByOrderId(order.getOrderId());
			if(!CollectionUtils.isEmpty(UserProfitRecordList)){
				List<Object[]> profitRecordList = new ArrayList<Object[]> ();
				List<UserAmountChangeRecord> changeRecordList = new ArrayList<UserAmountChangeRecord> ();
				for(UserProfitRecord record : UserProfitRecordList){
					Double income = StringUtil.nullToDouble(record.getIncome());
					if(income.compareTo(0.01) >= 0 && StringUtil.compareObject(UserProfitRecord.DISTRIBUTION_STATUS_INIT, record.getStatus())){
						// 查询结算记录表是否为空
						List<UserAmountChangeRecord> recordList = this.userAmountChangeRecordManager.getUserAmountChangeRecordByObjectId(record.getRecordId(), UserAmountChangeRecord.AMOUNT_CHANGE_CHECK);
						if(CollectionUtils.isEmpty(recordList)){
							UserInfo userInfo = this.userInfoManager.get(record.getUserId());
							if(userInfo == null
									|| userInfo.getUserId() == null
									|| !StringUtil.nullToBoolean(userInfo.getIsAgent()) 
									|| !StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)){
								continue;
							}
							
							// 待结算利润
							profitRecordList.add(new Object[]{income, income, userInfo.getUserId()});
							pushUserProfitRecordList.add(record);
							
							//店铺金额变动记录
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
				
				// 批量结算店铺交易利润
				if(!CollectionUtils.isEmpty(profitRecordList)){
					this.userAmountChangeRecordManager.batchInsert(changeRecordList, changeRecordList.size());
					this.batchUpdate("update jkd_user_info set balance=truncate(ifnull(balance,0) + ?, 2), income=truncate(ifnull(income,0) + ?, 2), update_time=now() where user_id=?", profitRecordList);
				}
				
				// 店铺累计销售金额批量更新
				this.userProfitRecordRepository.updateUserProfitRecordStatusByOrderId(order.getOrderId(), UserProfitRecord.DISTRIBUTION_STATUS_SUCC, DateUtil.getCurrentDate());
			}
			
			//更改订单为已结算
			this.orderManager.updateOrderCheckById(true, order.getOrderId());
			OrderHistory orderHistory = this.orderHistoryManager.createOrderHistoryBean(order.getOrderId(), "订单结算", "订单结算成功");
			this.orderHistoryManager.save(orderHistory);
			
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			// 释放锁
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
			
			// 支持上级返利等级用户
			Long topUserId =0L;
			Double profitTop = new Double(0);
			UserInfo topUserInfo = this.userInfoManager.get(order.getTopUserId());
			if(topUserInfo != null
					&& StringUtil.nullToBoolean(topUserInfo.getIsAgent()) 
					&& StringUtil.compareObject(topUserInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)){
				topUserId = StringUtil.nullToLong(topUserInfo.getUserId());
				profitTop = order.getProfitTop();
			}
			
			// 插入上级用户分销利润
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
			
			// 插入购买会员礼包记录
			this.userInviteRecordManager.insertInviteRecordByOrder(order);
			// 立即利润返现操作
			this.orderCheckUpdateRecord(order.getOrderId());
			
			//用户上级绑定
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

//	@Override
//	public List<Object[]> getUserProfitRecordListByBdUserIdList(List<Long> bdUserIdList) {
//		StringBuilder strBulSql = new StringBuilder();
//		strBulSql.append("select cc.user_id,cc.nick_name,cc.mobile,bb.pay_time,aa.income,bb.status order_status, ");
//		strBulSql.append("case when aa.mtype = 2 and dd.refund_type in(1,2) then dd.sub_profit when aa.mtype = 2 and dd.refund_type = 3 then bb.profit_sub else dd.top_profit end as refund_profit, ");
//		strBulSql.append("aa.update_time ,case when aa.mtype = 2 then bb.profit_sub  else bb.profit_top end as order_profit,bb.order_id,aa.bd_user_id,aa.status profit_status,aa.record_id ");
//		strBulSql.append("from ");
//		strBulSql.append("( select jupr.*,zz.bd_user_id from jkd_user_profit_record jupr inner join ");
//		strBulSql.append("( select jbui.user_id,jbui.bd_user_id from jkd_bd_user_invite jbui where jbui.bd_user_id in(%s) ) zz on jupr.from_user_id = zz.user_id  where jupr.type = 5 ) aa ");
//		strBulSql.append("left join ");
//		strBulSql.append("jkd_order bb ON aa.order_id = bb.order_id ");
//		strBulSql.append("left join jkd_user_info cc ");
//		strBulSql.append("on aa.from_user_id = cc.user_id ");
//		strBulSql.append(" left join ");
//		strBulSql.append("(select jr.order_id,sum(joi.profit) sub_profit,sum(joi.top_profit) top_profit,jr.refund_type from jkd_refund jr ");
//		strBulSql.append("left join ");
//		strBulSql.append("jkd_order_items joi on joi.item_id = jr.order_item_id  where jr.refund_status = 5 ");
//		strBulSql.append(" group by jr.order_id ");
//		strBulSql.append(" ) dd on aa.order_id = dd.order_id order by aa.record_id desc");
//		log.info(strBulSql.toString());
//		return this.querySql(String.format(strBulSql.toString(), StringUtil.longListToStr(bdUserIdList)));
//	
//	}
	
//	@Override
//	public List<Object[]> getUserProfitRecordListByBdUserIdList(List<Long> bdUserIdList) {
//		StringBuilder strBulSql = new StringBuilder();
//		strBulSql.append("SELECT * FROM jkd_bd_user_profit_remote_rebate cc WHERE cc.bd_user_id IN(%s) ");
//		strBulSql.append("UNION ");
//		strBulSql.append("SELECT * FROM jkd_bd_user_profit_recent_rebate bb WHERE bb.bd_user_id IN(%s) ");
//		strBulSql.append("UNION ");
//		strBulSql.append("SELECT * FROM ");
//		strBulSql.append("(SELECT aa.record_id ,cc.user_id,cc.nick_name,cc.mobile,bb.pay_time,aa.income,bb.status AS order_status,");
//		strBulSql.append("CASE WHEN dd.refund_type = 3 THEN bb.profit_top ELSE dd.top_profit END AS refund_profit,");
//		strBulSql.append("aa.update_time AS complate_time,bb.profit_top  AS order_profit,");
//		strBulSql.append("bb.order_id,aa.bd_user_id,aa.status AS profit_status FROM ");
//		strBulSql.append("(SELECT jupr.*,zz.bd_user_id FROM (SELECT vv.* FROM jkd_user_profit_record  vv WHERE  ");
//		strBulSql.append("vv.type = 5  AND vv.mtype = 3 AND vv.create_time >= DATE_FORMAT(DATE_ADD(NOW(),INTERVAL - 1 DAY),'%%Y-%%m-%%d 23:30:00') GROUP BY vv.from_user_id,vv.order_id) jupr ");
//		strBulSql.append("INNER JOIN ");
//		strBulSql.append("(SELECT jbui.user_id,jbui.bd_user_id FROM jkd_bd_user_invite jbui WHERE jbui.bd_user_id IN( ");
//		strBulSql.append("%s ) ) zz ");
//		strBulSql.append("ON jupr.from_user_id = zz.user_id ) aa ");
//		strBulSql.append("INNER JOIN ");
//		strBulSql.append("jkd_order bb ON aa.order_id = bb.order_id ");
//		strBulSql.append("LEFT JOIN ");
//		strBulSql.append("jkd_user_info cc ON aa.from_user_id = cc.user_id ");
//		strBulSql.append(" LEFT JOIN ");
//		strBulSql.append("(SELECT jr.order_id,SUM(joi.profit) sub_profit,SUM(joi.top_profit) top_profit,jr.refund_type FROM jkd_refund jr LEFT JOIN ");
//		strBulSql.append("jkd_order_items joi ON joi.item_id = jr.order_item_id  WHERE jr.refund_status = 5  GROUP BY jr.order_id  ) dd ON aa.order_id = dd.order_id ");
//		strBulSql.append("ORDER BY aa.record_id DESC");
//		strBulSql.append(" ) gg");
//		log.info(strBulSql.toString());
//		return this.querySql(String.format(strBulSql.toString(), StringUtil.longListToStr(bdUserIdList), StringUtil.longListToStr(bdUserIdList), StringUtil.longListToStr(bdUserIdList)));
//	}
	
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
		
		// 检查时间是否有效
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

