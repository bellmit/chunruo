package com.chunruo.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.UserProfitRecord;

public interface UserProfitRecordManager extends GenericManager<UserProfitRecord, Long> {

	public Map<String, List<Long>> batchInsertProfitRecords(Order order);
	
	public void batchUpdateProfitRecords(Order order, List<Long> childOrderIdList);
	
	public void orderCheckUpdateRecord(Long recordId);
	
	/**
	 * 根据店铺ID查找记录
	 * @param storeId
	 * @return
	 */
	public List<UserProfitRecord> getUserProfitRecordList(Long userId);

	public List<UserProfitRecord> getUserProfitRecordByOrderId(Long orderId);

	public List<UserProfitRecord> getUserProfitRecordByOrderNo(String orderNo);

	public List<UserProfitRecord> getUserProfitRecordByStatus(int status);
	
	public void updateUserProfitRecordStatusByOrderId(Long orderId, Integer status);
	
	public void updateUserProfitRecordStatusByOrderId(Long orderId, Integer status,Double income);
	
	public void updateUserProfitRecordStatusByOrderIdList(List<Long> orderIdList, Integer status);

	public List<UserProfitRecord> getUserProfitRecordListByUpdateTime(Date date);
	
	public List<UserProfitRecord> getUserProfitRecordListByFromUserId(Long fromUserId);
	
	public Double countUserProfitTotalIncomeByUserId(Long userId);

	public List<UserProfitRecord> getUserProfitRecordListByCurrentMonth();

	public void insertProfitRecordsByIsInvitationAgent(Order order);

	public List<Object[]> getUserProfitRecordListByCondition(List<Long> bdUserIdList,Integer status,String keyword,String beginTime ,String endTime);

	public List<UserProfitRecord> getUserProfitRecordListByFromUserIdList(List<Long> userIdList);

	public List<Object[]> getUserProfitRecordToExcel(List<Long> extendUserList, String beginTime, String endTime);
}
