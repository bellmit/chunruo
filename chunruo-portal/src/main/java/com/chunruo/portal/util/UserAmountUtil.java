package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.chunruo.cache.portal.impl.ProductTaskListCacheManager;
import com.chunruo.cache.portal.impl.UserProductTaskRecordListByUserIdCacheManager;
import com.chunruo.cache.portal.impl.UserProfitByUserIdCacheManager;
import com.chunruo.cache.portal.impl.UserRechargeByUserIdCacheManager;
import com.chunruo.cache.portal.impl.UserSaleRecordListByUserIdCacheManager;
import com.chunruo.cache.portal.impl.UserWithdrawalListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.model.ProductTask;
import com.chunruo.core.model.UserProductTaskRecord;
import com.chunruo.core.model.UserProfitRecord;
import com.chunruo.core.model.UserRecharge;
import com.chunruo.core.model.UserSaleRecord;
import com.chunruo.core.model.UserSaleStandard;
import com.chunruo.core.model.UserWithdrawal;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;

public class UserAmountUtil {
	
	/**
	 * 获取店铺收益收益情况
	 * @param storeId	店铺id
	 * @return
	 */
	public static Map<String, Double> getGroupbalance(Long userId) {
		Double balance = 0.0D;
		Double unbalance = 0.0D;
		Double groupBalance = 0.0D;
		Double groupUnbalance = 0.0D;
		UserProfitByUserIdCacheManager userProfitByUserIdCacheManager = Constants.ctx.getBean(UserProfitByUserIdCacheManager.class);
		UserWithdrawalListByUserIdCacheManager userWithdrawalListByUserIdCacheManager = Constants.ctx.getBean(UserWithdrawalListByUserIdCacheManager.class);
		UserRechargeByUserIdCacheManager userRechargeByUserIdCacheManager = Constants.ctx.getBean(UserRechargeByUserIdCacheManager.class);
		UserSaleRecordListByUserIdCacheManager userSaleRecordListByUserIdCacheManager = Constants.ctx.getBean(UserSaleRecordListByUserIdCacheManager.class);

		//已提现金额（申请中，银行中，提现成功）
		Double withdrawalAmount  = 0D;
		Map<String, UserWithdrawal> userWithdrawalMap = userWithdrawalListByUserIdCacheManager.getSession(userId);
		if(userWithdrawalMap != null && userWithdrawalMap.size() > 0){
			for(Entry<String, UserWithdrawal> map : userWithdrawalMap.entrySet()){
				UserWithdrawal userWithdrawal = map.getValue();
				withdrawalAmount = withdrawalAmount + userWithdrawal.getAmount();
			}
		}
		
		// 交易中的金额
		Map<String, Double> balanceMap = new HashMap<String, Double>();
		Map<String, UserProfitRecord> userProfitIdMap = userProfitByUserIdCacheManager.getSession(userId);
		if(userProfitIdMap != null && userProfitIdMap.size() > 0){
			for(Entry<String, UserProfitRecord> entry : userProfitIdMap.entrySet()){
				//如果收益不是上线分成的则跳过
				if(entry.getValue() == null || entry.getValue().getRecordId() == null ){
					continue;
				}
				
				UserProfitRecord record = entry.getValue();
				if(StringUtil.compareObject(record.getStatus(), UserProfitRecord.DISTRIBUTION_STATUS_INIT)){
					unbalance = unbalance + StringUtil.nullToDouble(record.getIncome());
				}else if (StringUtil.compareObject(record.getStatus(), UserProfitRecord.DISTRIBUTION_STATUS_SUCC)){
					balance = balance + StringUtil.nullToDouble(record.getIncome());
				}
			}
		}
		
		Double totalReward = new Double(0);    //已结算奖励
		Double unReward = new Double(0);       //待结算奖励
		//用户奖励
		List<UserRecharge> userRechargeList = userRechargeByUserIdCacheManager.getSession(userId);
		if(userRechargeList != null && userRechargeList.size() > 0) {
			for (UserRecharge userRecharge : userRechargeList) {
				if (!StringUtil.compareObject(userRecharge.getStatus(), UserRecharge.USER_RECHARGE_SUCC)) {
					continue;
				}
				totalReward += userRecharge.getAmount();
			}
		}
		
		//任务奖励
		UserProductTaskRecordListByUserIdCacheManager userProductTaskRecordListByUserIdCacheManager = Constants.ctx.getBean(UserProductTaskRecordListByUserIdCacheManager.class);
		ProductTaskListCacheManager productTaskListCacheManager = Constants.ctx.getBean(ProductTaskListCacheManager.class);
		Map<String, ProductTask> productTaskMap = productTaskListCacheManager.getSession();
		Map<String, UserProductTaskRecord> userProductTaskRecordMap = userProductTaskRecordListByUserIdCacheManager.getSession(userId);
		if(userProductTaskRecordMap != null && userProductTaskRecordMap.size() > 0) {
			for(Map.Entry<String, UserProductTaskRecord> entry : userProductTaskRecordMap.entrySet()) {
				UserProductTaskRecord record = entry.getValue();
				ProductTask productTask = productTaskMap.get(StringUtil.null2Str(record.getTaskId()));
			    if(productTask == null || productTask.getTaskId() == null) {
			    	continue;
			    }
				if(StringUtil.compareObject(record.getStatus(), UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_AWARDED)) {
					totalReward += record.getTotalReward();
				}else if(StringUtil.compareObject(record.getStatus(), UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_WAIT)) {
					unReward += record.getTotalReward();
				}
			}
		}
		
		
		UserSaleStandard userSaleStandard = Constants.USER_SALE_STANDARD;

		Double daySaleAmount = 0.0D;           //今日销售额
		Double realMonthSaleAmount = 0.0D;     //实际本月销售额
		Double virtualMonthSaleAmount = 0.0D;  //虚拟本月销售额
	    Double lastMonthSaleAmount = 0.0D;     //上月销售额
	    Double totalSaleAmount = 0.0D;         //总销售额
		//用户销售额统计
		List<UserSaleRecord> userSaleRecordList = userSaleRecordListByUserIdCacheManager.getSession(userId);
		if(userSaleRecordList != null && userSaleRecordList.size() > 0) {
			//销售额统计订单状态
			List<Integer> dayStatusList = new ArrayList<Integer>();
			dayStatusList.add(OrderStatus.UN_DELIVER_ORDER_STATUS); //未发货
			dayStatusList.add(OrderStatus.DELIVER_ORDER_STATUS);    //已发货
			dayStatusList.add(OrderStatus.OVER_ORDER_STATUS);       //已完成
			//月销售额统计订单状态
			List<Integer> monthStatusList = new ArrayList<Integer>();
			monthStatusList.add(OrderStatus.DELIVER_ORDER_STATUS);    //已发货
			monthStatusList.add(OrderStatus.OVER_ORDER_STATUS);       //已完成
			//今日日期
			String currentDate = DateUtil.getFormatDate(DateUtil.getCurrentDate());
			//本月日期
			String currentMonth = DateUtil.formatDate(DateUtil.DATE_YYYY_MM_PATTERN, DateUtil.getCurrentDate());
			//上月日期
			String lastMonth = DateUtil.formatDate(DateUtil.DATE_YYYY_MM_PATTERN, DateUtil.getMonthBeforeByDay(DateUtil.getCurrentDate(), 1));
			for(UserSaleRecord record : userSaleRecordList) {
				if(record.getOrderPayTime() == null 
						|| userSaleStandard.getCreateTime() == null
						|| !dayStatusList.contains(record.getOrderStatus())) {
					continue;
				}
			    
				String dayDate = DateUtil.getFormatDate(record.getOrderPayTime());
				Double saleAmount = StringUtil.nullToDouble(record.getSaleAmount());
				Double refundAmount = StringUtil.nullToDouble(record.getRefundAmount());
				Double realRefundAmount = StringUtil.nullToDouble(record.getRealRefundAmount());
				//实际销售额 = 总销售额 - 真正退款销售额
				//虚拟销售额 = 总销售额 - 总退款金额
				Double realCurrentOrderAmount = DoubleUtil.sub(saleAmount, refundAmount);       //虚拟（实时计算）
				Double realSaleAmount = DoubleUtil.sub(saleAmount, realRefundAmount);           //实际（真正退款计算）

				//3.4版本计算规则
				if(record.getOrderPayTime().compareTo(userSaleStandard.getCreateTime()) >= 0) {
					//今日销售额
					if(StringUtil.compareObject(currentDate, dayDate)) {
						//今日销售额
						daySaleAmount = DoubleUtil.add(daySaleAmount, saleAmount);
					}
					String monthDate = DateUtil.formatDate(DateUtil.DATE_YYYY_MM_PATTERN, record.getOrderPayTime());
					if(StringUtil.compareObject(currentMonth, monthDate)) {
						//本月
						realMonthSaleAmount = DoubleUtil.add(realMonthSaleAmount, saleAmount);
						virtualMonthSaleAmount = DoubleUtil.add(virtualMonthSaleAmount, saleAmount);
					}else if(StringUtil.compareObject(lastMonth, monthDate)) {
						//上月
						lastMonthSaleAmount = DoubleUtil.add(lastMonthSaleAmount, saleAmount);
					}
				}else {
					if(StringUtil.compareObject(currentDate, dayDate)) {
						//今日销售额
						daySaleAmount = DoubleUtil.add(daySaleAmount, realCurrentOrderAmount);
					}
					
					if(monthStatusList.contains(StringUtil.nullToInteger(record.getOrderStatus()))
							&& record.getOrderSentTime() != null) {
						String monthDate = DateUtil.formatDate(DateUtil.DATE_YYYY_MM_PATTERN, record.getOrderSentTime());
						//月销售额
						if(StringUtil.compareObject(currentMonth, monthDate)) {
							//实际本月
							realMonthSaleAmount = DoubleUtil.add(realMonthSaleAmount, realSaleAmount);
							virtualMonthSaleAmount = DoubleUtil.add(virtualMonthSaleAmount, realCurrentOrderAmount);
						}else if(StringUtil.compareObject(lastMonth, monthDate)) {
							//上月
							lastMonthSaleAmount = DoubleUtil.add(lastMonthSaleAmount, saleAmount);

						}
					}else if(StringUtil.compareObject(OrderStatus.UN_DELIVER_ORDER_STATUS, record.getOrderStatus())
							|| record.getOrderSentTime() == null) {
						//未发货的虚拟月销售额
						virtualMonthSaleAmount = DoubleUtil.add(virtualMonthSaleAmount, realCurrentOrderAmount);
					}
				}
				
				if(!StringUtil.compareObject(record.getOrderStatus(), OrderStatus.CANCEL_ORDER_STATUS)) {
					totalSaleAmount  = DoubleUtil.add(totalSaleAmount, saleAmount);
				}
			}
		}
		
		balanceMap.put("totalSaleAmount", totalSaleAmount);
		balanceMap.put("daySaleAmount", daySaleAmount);
		balanceMap.put("realMonthSaleAmount", realMonthSaleAmount);
		balanceMap.put("virtualMonthSaleAmount", virtualMonthSaleAmount);
		balanceMap.put("lastMonthSaleAmount", lastMonthSaleAmount);
		balanceMap.put("brokerage", StringUtil.nullToDoubleFormat(balance + groupBalance + totalReward));
		balanceMap.put("reward", totalReward);
		balanceMap.put("unReward", unReward);
		balanceMap.put("balance", StringUtil.nullToDoubleFormat(balance));
		balanceMap.put("unbalance", StringUtil.nullToDoubleFormat(unbalance));
		balanceMap.put("groupUnbalance", StringUtil.nullToDoubleFormat(groupUnbalance));
		balanceMap.put("groupBalance", StringUtil.nullToDoubleFormat(groupBalance));
		balanceMap.put("withdrawalAmount", StringUtil.nullToDoubleFormat(withdrawalAmount));
		return balanceMap;
	}
}
