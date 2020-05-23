package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.UserProductTaskItem;
import com.chunruo.core.vo.MsgModel;

public interface UserProductTaskItemManager extends GenericManager<UserProductTaskItem, Long> {

	public List<Long> batchInsertTaskItem(Order order);

	public List<UserProductTaskItem> getUserProductTaskItemListById(Long taskId, Long userId);
    
	public void failUserProductTaskItemByOrder(Order order);
    
	public void failUserProductTaskItemByOrderItem(OrderItems orderItems);
	
	public MsgModel<Integer> getProductTotalNumber(Long userId, Long taskId, boolean isOnlySucc);
	
	public List<Object[]> getUserProductTaskRecordData(Long userId, Long taskId, boolean isOnlySucc);
	
	public void succUserProductTaskItemByOrder(Order order);
    
    public List<UserProductTaskItem> getUserProductTaskItemListByOrderItemIdList(List<Long> orderItemIdList);

	public List<UserProductTaskItem> getUserProductTaskItemListByUserId(Long userId);

	public List<UserProductTaskItem> getUserProductTaskItemListByUpdateTime(Date updateTime);
}
