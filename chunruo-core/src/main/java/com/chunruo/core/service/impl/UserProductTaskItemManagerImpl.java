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

import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.ProductTask;
import com.chunruo.core.model.UserAmountChangeRecord;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserProductTaskItem;
import com.chunruo.core.model.UserProductTaskRecord;
import com.chunruo.core.repository.UserProductTaskItemRepository;
import com.chunruo.core.service.OrderItemsManager;
import com.chunruo.core.service.ProductTaskManager;
import com.chunruo.core.service.UserAmountChangeRecordManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserProductTaskItemManager;
import com.chunruo.core.service.UserProductTaskRecordManager;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Component("userProductTaskItemManager")
public class UserProductTaskItemManagerImpl extends GenericManagerImpl<UserProductTaskItem, Long> implements UserProductTaskItemManager{
	private UserProductTaskItemRepository userProductTaskItemRepository;
	private Lock lock = new ReentrantLock();
	@Autowired
	private OrderItemsManager orderItemsManager;
	@Autowired
	private ProductTaskManager productTaskManager;
	@Autowired
	private  UserInfoManager userInfoManager;
	@Autowired
	private UserAmountChangeRecordManager userAmountChangeRecordManager;
	@Autowired
	private UserProductTaskRecordManager userProductTaskRecordManager;

	@Autowired
	public UserProductTaskItemManagerImpl(UserProductTaskItemRepository userProductTaskItemRepository) {
		super(userProductTaskItemRepository);
		this.userProductTaskItemRepository = userProductTaskItemRepository;
	}

	@Transactional
	@Override
	public List<Long> batchInsertTaskItem(Order order) {
		List<Long> userIdList = new ArrayList<Long>();
		StringBuffer sqlBuffer = new StringBuffer ();
		sqlBuffer.append("select aa.product_id,aa.task_id,bb.quantity,bb.item_id,aa.task_number,aa.reward,aa.max_group_number,bb.product_spec_id from ");
		sqlBuffer.append("(select jpt.product_id,max(jpt.task_id) task_id,jpt.task_number,jpt.reward,jpt.max_group_number from jkd_product_task jpt where jpt.is_enable = true ");
		sqlBuffer.append("and '" + DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, order.getPayTime()) +"' between jpt.begin_time and jpt.end_time group by jpt.product_id ) aa ");
		sqlBuffer.append("inner join ");
		sqlBuffer.append("(select joi.product_id product_id,joi.product_spec_id,joi.quantity,joi.item_id from jkd_order_items joi where joi.order_id = ? and (joi.is_group_product = false or joi.is_group_product is null)  ");
		sqlBuffer.append("union ");
		sqlBuffer.append("select joip.group_product_id product_id,joip.product_spec_id,joip.quantity,joip.item_id from jkd_order_items joip where joip.order_id = ? ");
		sqlBuffer.append("and joip.is_group_product = true and joip.is_main_group_item = true ");
		sqlBuffer.append(") bb ");
		sqlBuffer.append("on aa.product_id = bb.product_id ");
		List<Object[]> list = this.querySql(sqlBuffer.toString(), new Object[]{ order.getOrderId(), order.getOrderId()});
		if(list != null && list.size() > 0) {
			Set<Long> taskIdSet = new HashSet<Long> ();
			Date currentDate = DateUtil.getCurrentDate();
			Long userId = StringUtil.nullToLong(order.getStoreId());
			List<UserProductTaskItem> taskItemList = new ArrayList<UserProductTaskItem>();
			for(Object[] object : list) {
				//??????????????????????????????
				UserProductTaskItem taskItem = new UserProductTaskItem();
				taskItem.setProductId(StringUtil.nullToLong(object[0]));
				taskItem.setTaskId(StringUtil.nullToLong(object[1]));
				taskItem.setQuantity(StringUtil.nullToInteger(object[2]));
				taskItem.setOrderItemId(StringUtil.nullToLong(object[3]));
				taskItem.setTaskNumber(StringUtil.nullToInteger(object[4]));
				taskItem.setReward(StringUtil.nullToInteger(object[5]));
				taskItem.setMaxGroupNumber(StringUtil.nullToInteger(object[6]));
				taskItem.setProductSpecId(StringUtil.nullToLong(object[7]));
				taskItem.setOrderId(StringUtil.nullToLong(order.getOrderId()));
				taskItem.setStatus(UserProductTaskItem.USER_PRODUCT_TASK_STATUS_WAITTING);
				taskItem.setUserId(userId);
				taskItem.setCreateTime(currentDate);
				taskItem.setUpdateTime(currentDate);
				taskIdSet.add(taskItem.getTaskId());
				taskItemList.add(taskItem);
			}
			
			// ????????????????????????
			Map<Long, UserProductTaskRecord> taskRecordMap = new HashMap<Long, UserProductTaskRecord> ();
			List<UserProductTaskRecord> recordList = this.userProductTaskRecordManager.getUserProductTaskRecordById(userId, StringUtil.longSetToList(taskIdSet));
			if(recordList != null && recordList.size() > 0){
				for(UserProductTaskRecord taskRecord: recordList){
					// ???????????????????????????????????????taskId??????(????????????????????????????????????)
					MsgModel<Integer> msgModel = this.getProductTotalNumber(userId, taskRecord.getTaskId(), false);
					taskRecord.setTotalNumber(StringUtil.nullToInteger(msgModel.getData()));
					taskRecordMap.put(taskRecord.getTaskId(), taskRecord);
				}
			}
			
			// ???????????????????????????????????????????????????
			for(UserProductTaskItem taskItem : taskItemList){
				if(taskRecordMap.containsKey(taskItem.getTaskId())){
					// ??????????????????????????????
					UserProductTaskRecord taskRecord = taskRecordMap.get(taskItem.getTaskId());
					//??????????????????????????????????????????????????????????????????????????????????????????
			    	int totalNumber = taskRecord.getTotalNumber() + taskItem.getQuantity();
			    	//?????????????????????
			    	int groupNumber = getGroupNumber(totalNumber, taskItem.getTaskNumber(), taskItem.getMaxGroupNumber());
			    	int totalReward = groupNumber * taskItem.getReward();
			    	
			    	if(groupNumber >= 1) {
			    		//?????????
			    		taskRecord.setStatus(UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_WAIT);
			    	}
			        taskRecord.setTotalNumber(totalNumber);
			        taskRecord.setTotalReward(totalReward);
			        taskRecord.setGroupNumber(groupNumber);
			        taskRecord.setUpdateTime(currentDate);
				}else{
					// ??????????????????
					UserProductTaskRecord taskRecord = new UserProductTaskRecord(); 
					//?????????????????????
			    	int groupNumber = getGroupNumber(taskItem.getQuantity(), taskItem.getTaskNumber(), taskItem.getMaxGroupNumber());
			    	int totalReward = groupNumber * taskItem.getReward();
			    	
			    	taskRecord.setTaskId(taskItem.getTaskId());
			    	taskRecord.setTotalNumber(taskItem.getQuantity());
			    	taskRecord.setUserId(taskItem.getUserId());
			    	if(groupNumber >= 1) {
			    		//?????????
			    		taskRecord.setStatus(UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_WAIT);
			    		taskRecord.setGroupNumber(groupNumber);
				    	taskRecord.setTotalReward(totalReward);
			    	}else {
			    		//?????????
			    		taskRecord.setStatus(UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_UNAWARDED);
			    		taskRecord.setGroupNumber(0);
				    	taskRecord.setTotalReward(0);
			    	}
			    	taskRecord.setCreateTime(currentDate);
			    	taskRecord.setUpdateTime(currentDate);
			    	taskRecordMap.put(taskRecord.getTaskId(), taskRecord);
				}
			}
			
			// ???map???????????????list
			List<UserProductTaskRecord> taskRecordList = new ArrayList<UserProductTaskRecord>();
			if(taskRecordMap != null && taskRecordMap.size() > 0){
				for(UserProductTaskRecord taskRecord :  taskRecordMap.values()){
					taskRecordList.add(taskRecord);
				}
			}
			
			userIdList.add(userId);
			this.batchInsert(taskItemList, taskItemList.size());
			this.userProductTaskRecordManager.batchInsert(taskRecordList, taskRecordList.size());
		}
		return userIdList;
	}
	
	/**
	 * ??????????????????
	 * @param totalNumber
	 * @param taskNumber
	 * @param maxGropuNumber
	 * @return
	 */
	private Integer getGroupNumber(Integer totalNumber, Integer taskNumber,Integer maxGropuNumber) {
		try {
			int groupNumber = totalNumber / taskNumber;
			if (groupNumber > maxGropuNumber) {
				groupNumber = maxGropuNumber;
			}
			return groupNumber;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public List<UserProductTaskItem> getUserProductTaskItemListById(Long taskId, Long userId) {
		return this.userProductTaskItemRepository.getUserProductTaskItemListById(taskId, userId);
	}

	@Transactional
	@Override
	public void failUserProductTaskItemByOrder(Order order) {
		//?????????????????????????????????
		List<UserProductTaskItem> userProductTaskItemList = this.userProductTaskItemRepository.getUserProductTaskItemListByOrderId(StringUtil.nullToLong(order.getOrderId()));
	    if(userProductTaskItemList != null && userProductTaskItemList.size() > 0) {
	    	Set<Long> taskIdSet = new HashSet<Long>();
	    	for(UserProductTaskItem item : userProductTaskItemList) {
	    		// ??????????????????item??????
	    		item.setStatus(UserProductTaskItem.USER_PRODUCT_TASK_STATUS_FAIL);
	    		item.setUpdateTime(DateUtil.getCurrentDate());
	    		taskIdSet.add(StringUtil.nullToLong(item.getTaskId()));
	    	}
	    	this.batchInsert(userProductTaskItemList, userProductTaskItemList.size());
	    	
	    	//?????????????????????????????????
	    	if(taskIdSet != null && taskIdSet.size() > 0) {
	    		List<Object[]> recordList = new ArrayList<Object[]> ();
	    		for(Long taskId : taskIdSet) {
	    			recordList.addAll(getUserProductTaskRecordData(StringUtil.nullToLong(order.getStoreId()), taskId, false));
	    		}
	    		this.userProductTaskRecordManager.batchUpdate("update jkd_user_product_task_record set total_number = ?,group_number = ?,total_reward = ?,status = ?, update_time=now() where record_id = ?", recordList);
	    	}
	    }
	}
	
	@Transactional
	@Override
	public void failUserProductTaskItemByOrderItem(OrderItems orderItems) {
		UserProductTaskItem userProductTaskItem = this.userProductTaskItemRepository.getUserProductTaskItemListByOrderItemId(orderItems.getItemId());
		if(userProductTaskItem != null 
				&& userProductTaskItem.getItemId() != null
				&& userProductTaskItem.getUserId() != null
				&& userProductTaskItem.getTaskId() != null) {
			// ??????????????????item??????
			userProductTaskItem.setStatus(UserProductTaskItem.USER_PRODUCT_TASK_STATUS_FAIL);
			userProductTaskItem.setUpdateTime(DateUtil.getCurrentDate());
			this.save(userProductTaskItem);
			List<Object[]> recordList = getUserProductTaskRecordData(userProductTaskItem.getUserId(), userProductTaskItem.getTaskId(), false);
    		this.userProductTaskRecordManager.batchUpdate("update jkd_user_product_task_record set total_number = ?,group_number = ?,total_reward = ?,status = ?,update_time=now() where record_id = ?", recordList);
		}
	}
	
	/**
	 * ??????????????????????????????
	 * @param userId
	 * @param taskId
	 * @param isOnlySucc  ??????????????????????????????
	 * @return
	 */
	@Transactional
	@Override
	public MsgModel<Integer> getProductTotalNumber(Long userId, Long taskId, boolean isOnlySucc) {
		MsgModel<Integer> msgModel = new MsgModel<Integer>();
		boolean isHaveWait = false;   //?????????????????????
		int totalNumber = 0;
		List<UserProductTaskItem> userProductTaskItemList = this.getUserProductTaskItemListById(taskId, userId);
        if(userProductTaskItemList != null && userProductTaskItemList.size() > 0) {
        	// ??????????????????
        	List<Integer> statusList = new ArrayList<Integer>();
        	statusList.add(UserProductTaskItem.USER_PRODUCT_TASK_STATUS_SUCC);     //??????
        	if(!StringUtil.nullToBoolean(isOnlySucc)) {
            	statusList.add(UserProductTaskItem.USER_PRODUCT_TASK_STATUS_WAITTING); //?????????
        	}
        	
        	for(UserProductTaskItem item : userProductTaskItemList) {
        		log.info("???????????????:"+item.getStatus());
        		if(statusList.contains(StringUtil.nullToInteger(item.getStatus()))) {
        			totalNumber += StringUtil.nullToInteger(item.getQuantity());
        		}
        		if(StringUtil.compareObject(UserProductTaskItem.USER_PRODUCT_TASK_STATUS_WAITTING , StringUtil.nullToInteger(item.getStatus()))) {
        			isHaveWait = true;
        		}
        	}
        }
        msgModel.setIsSucc(isHaveWait); 
        msgModel.setData(totalNumber);
        return msgModel;
	}
	
	/**
	 * ???????????????????????????
	 * @param userId
	 * @param taskId
	 * @param productTask
	 * @param isOnlySucc ???????????????????????????
	 * @return
	 */
	@Transactional
	@Override
	public List<Object[]> getUserProductTaskRecordData(Long userId, Long taskId, boolean isOnlySucc){
		List<Object[]> recordList = new ArrayList<Object[]> ();
		ProductTask productTask = this.productTaskManager.get(taskId);
		if(productTask != null && productTask.getTaskId() != null){
			UserProductTaskRecord userProductTaskRecord = this.userProductTaskRecordManager.getUserProductTaskRecordById(userId, taskId);
			if(userProductTaskRecord != null && userProductTaskRecord.getRecordId() != null) {
				MsgModel<Integer> msgModel = this.getProductTotalNumber(userId, userProductTaskRecord.getTaskId(), isOnlySucc);
				Integer totalNumber = StringUtil.nullToInteger(msgModel.getData());
				//?????????????????????
		    	int groupNumber = this.getGroupNumber(totalNumber, StringUtil.nullToInteger(productTask.getTaskNumber()), StringUtil.nullToInteger(productTask.getMaxGroupNumber()));
		    	int totalReward = groupNumber * StringUtil.nullToInteger(productTask.getReward());
		    	if(StringUtil.nullToBoolean(isOnlySucc)) {
		    		//????????????????????????
		    		this.checkIsSuccTaskRecord(groupNumber, totalReward, msgModel.getIsSucc(), productTask, userProductTaskRecord);
		    	}else{
		    		// ????????????????????????????????????
		    		Integer status = UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_UNAWARDED;
			    	if(groupNumber >= 1) {
			    		status = UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_WAIT;
			    	}
			    	recordList.add(new Object[] {totalNumber, groupNumber, totalReward, status, userProductTaskRecord.getRecordId()});
		    	}
			}
		}
		return recordList;
	}
	
	/**
	 * ???????????????????????????
	 * @param groupNumber
	 * @param totalReward
	 * @param isHaveWait
	 * @param productTask
	 * @param userProductTaskRecord
	 */
	public void checkIsSuccTaskRecord(Integer groupNumber, Integer totalReward, boolean isHaveWait, ProductTask productTask, UserProductTaskRecord userProductTaskRecord) {
		lock.lock();
		try {
			
			if(StringUtil.compareObject(userProductTaskRecord.getStatus(),UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_AWARDED)) {
				return;
			}
			boolean isAwarded = false;
			if(groupNumber >= StringUtil.nullToInteger(productTask.getMaxGroupNumber())) {
				log.info("?????????????????????=====");
				//??????????????????????????????
				userProductTaskRecord.setStatus(UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_AWARDED);
				userProductTaskRecord.setTotalReward(totalReward);
				userProductTaskRecord.setGroupNumber(groupNumber);
				userProductTaskRecord.setUpdateTime(DateUtil.getCurrentDate());
				userProductTaskRecord = this.userProductTaskRecordManager.save(userProductTaskRecord);
				
				//??????????????????
				isAwarded = true;
			}else {
				//????????????????????????????????????
				if(!StringUtil.nullToBoolean(productTask.getIsEnable())
						|| productTask.getEndTime().getTime() <= DateUtil.getCurrentTime()) {
					//????????????
					if(!StringUtil.nullToBoolean(isHaveWait)) {
						log.info("????????????????????????");
						//???????????????????????????????????????????????????????????????
						if(groupNumber >= 1) {
							//?????????????????????
							userProductTaskRecord.setStatus(UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_AWARDED);
			    			userProductTaskRecord.setTotalReward(totalReward);
			    			userProductTaskRecord.setGroupNumber(groupNumber);
			    			
			    			//??????????????????
			    			isAwarded = true;
						}else {
							//?????????????????????
							userProductTaskRecord.setStatus(UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_UNAWARDED);
			    			userProductTaskRecord.setTotalReward(0);
			    			userProductTaskRecord.setGroupNumber(0);
						}
		    			userProductTaskRecord.setUpdateTime(DateUtil.getCurrentDate());
		    			userProductTaskRecord = this.userProductTaskRecordManager.save(userProductTaskRecord);
					}
				}
			}
			
			//??????????????????????????????????????????
			if(StringUtil.nullToBoolean(isAwarded)) {
				log.info("?????????===="+userProductTaskRecord.getTotalReward());
				UserInfo userInfo = this.userInfoManager.get(userProductTaskRecord.getUserId());
				if(userInfo != null && userInfo.getUserId() != null) {
					List<UserAmountChangeRecord> recordList = this.userAmountChangeRecordManager.getUserAmountChangeRecordByObjectId(userProductTaskRecord.getRecordId(), UserAmountChangeRecord.AMOUNT_CHANGE_TASK_PRODUCT);
                    if(recordList == null || recordList.size() <= 0) {
                    	//????????????????????????
    					Double income = StringUtil.nullToDouble(userProductTaskRecord.getTotalReward());
    					Double balance = StringUtil.nullToDouble(userInfo.getBalance());
    					UserAmountChangeRecord changeRecord = new UserAmountChangeRecord();
    					changeRecord.setUserId(userInfo.getUserId());
    					changeRecord.setObjectId(userProductTaskRecord.getRecordId());
    					changeRecord.setType(UserAmountChangeRecord.AMOUNT_CHANGE_TASK_PRODUCT);
    					changeRecord.setBeforeAmount(balance);
    					changeRecord.setChangeAmount(income);
    					changeRecord.setAfterAmount(balance + income);
    					changeRecord.setCreateTime(DateUtil.getCurrentDate());
    					changeRecord.setUpdateTime(changeRecord.getCreateTime());
    					// ????????????????????????
    					this.userAmountChangeRecordManager.save(changeRecord);
    					this.executeSql("update jkd_user_info set balance=truncate(ifnull(balance,0) + ?, 2), income=truncate(ifnull(income,0) + ?, 2), update_time=now() where user_id=?", new Object[]{income, income, userInfo.getUserId()});
                    }	
				}
			}
		}finally{
			lock.unlock();
		}
	}
	
	@Transactional
	@Override
	public void succUserProductTaskItemByOrder(Order order) {
		if(!StringUtil.compareObject(StringUtil.nullToInteger(order.getStatus()), OrderStatus.OVER_ORDER_STATUS)) {
			return;
		}
		List<OrderItems> orderItemsList = this.orderItemsManager.getOrderItemsListByOrderId(StringUtil.nullToLong(order.getOrderId()));
		checkUserProductTask(orderItemsList);
	}
	
	/**
	 * ??????????????????????????????
	 * @param orderItemsList
	 */
	private void checkUserProductTask(List<OrderItems> orderItemsList) {
		if(orderItemsList != null && orderItemsList.size() > 0) {
			List<Long> orderItemIdList = new ArrayList<Long>();
			for(OrderItems orderItems : orderItemsList) {
				orderItemIdList.add(StringUtil.nullToLong(orderItems.getItemId()));
			}
			
			//????????????????????????????????????????????????????????????????????????????????????
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select jupti.item_id,jupti.order_item_id,jupti.task_id,jupti.user_id from jkd_order_items joi,jkd_user_product_task_item jupti,jkd_refund jr ");
			sqlBuffer.append("where joi.item_id in("+StringUtil.longListToStr(orderItemIdList)+") ");
			sqlBuffer.append("and joi.item_id = jr.order_item_id and jupti.order_item_id = joi.item_id and jr.refund_status = 5 and jr.refund_type in(1,2,3) and jupti.status <> 2");
		    List<Object[]> objectList = this.querySql(sqlBuffer.toString());
		    if(objectList != null && objectList.size() > 0) {
		    	List<Long> itemIdList = new ArrayList<Long>();
		    	for(Object[] object : objectList) {
		    		itemIdList.add(StringUtil.nullToLong(object[0]));
		    	}
		    	//???????????????????????????????????????
		    	if(itemIdList != null && itemIdList.size() > 0) {
		    		this.userProductTaskItemRepository.updateUserProductTaskItemByItemIdList(UserProductTaskItem.USER_PRODUCT_TASK_STATUS_FAIL, itemIdList);
		    	}
		    }
		    
		    //????????????????????????
		    Map<Long, Long> taskUserIdMap = new HashMap<Long, Long>();
		    List<UserProductTaskItem> userProductTaskItemList = this.getUserProductTaskItemListByOrderItemIdList(orderItemIdList);
		    if(userProductTaskItemList != null && userProductTaskItemList.size() > 0) {
		    	List<UserProductTaskItem> changeList = new ArrayList<UserProductTaskItem>();
		    	for(UserProductTaskItem item : userProductTaskItemList) {
		    		log.info("???????????????:"+item.getStatus());
		    		if(StringUtil.compareObject(UserProductTaskItem.USER_PRODUCT_TASK_STATUS_WAITTING, StringUtil.nullToInteger(item.getStatus()))) {
		    			//??????????????????
		    			item.setStatus(UserProductTaskItem.USER_PRODUCT_TASK_STATUS_SUCC);
		    			item.setUpdateTime(DateUtil.getCurrentDate());
		    			changeList.add(item);
		    		}
		    		taskUserIdMap.put(StringUtil.nullToLong(item.getTaskId()), StringUtil.nullToLong(item.getUserId()));
		    	}
		    	this.batchInsert(changeList, changeList.size());
		    }
		    
		    if(taskUserIdMap != null && taskUserIdMap.size() > 0) {
		    	for(Map.Entry<Long, Long> entry : taskUserIdMap.entrySet()) {
		    		//??????????????????????????????
		    		this.getUserProductTaskRecordData(entry.getValue(), entry.getKey(), true);
		    	}
		    }  
		}
	}

	@Override
	public List<UserProductTaskItem> getUserProductTaskItemListByOrderItemIdList(List<Long> orderItemIdList) {
		return this.userProductTaskItemRepository.getUserProductTaskItemListByOrderItemIdList(orderItemIdList);
	}

	@Override
	public List<UserProductTaskItem> getUserProductTaskItemListByUserId(Long userId) {
		return this.userProductTaskItemRepository.getUserProductTaskItemListByUserId(userId);
	}

	@Override
	public List<UserProductTaskItem> getUserProductTaskItemListByUpdateTime(Date updateTime) {
		return this.userProductTaskItemRepository.getUserProductTaskItemListByUpdateTime(updateTime);
	}
}
