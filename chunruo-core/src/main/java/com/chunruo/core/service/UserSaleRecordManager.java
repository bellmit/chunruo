package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.UserSaleRecord;

public interface UserSaleRecordManager extends GenericManager<UserSaleRecord, Long>{

	public List<UserSaleRecord> getUserSaleRecordListByUserId(Long userId);
	
	public UserSaleRecord getUserSaleRecordByOrderId(Long orderId);

	public List<UserSaleRecord> getUserSaleRecordListByUpdateTime(Date updateTime);

	public void saveUserSaleRecordByOrder(Order order);

	public void updateUserSaleRecord(Order order,Boolean isReduce,Boolean isHaveRefund);
	
	public void updateUserSaleRecord(Order order,Boolean isReduce,OrderItems orderItems);
	
	public void updateUserSaleRecordByStatus(List<Long> orderIdList,Integer status);
	
	public List<UserSaleRecord> getUserSaleRecordListByOrderIdList(List<Long> orderIdList);
	
	public List<Object[]> getUserSaleRecordListByUserIdList(List<Long> userIdList);

	public void updateUserLevelByFunction();

	public List<Object[]> countMonthSaleAmountByUserId(Long userId);
}
