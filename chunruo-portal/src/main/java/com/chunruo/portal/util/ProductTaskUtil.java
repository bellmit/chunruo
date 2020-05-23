package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.chunruo.cache.portal.impl.ProductTaskListCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.ProductTask;
import com.chunruo.core.model.UserProductTaskItem;
import com.chunruo.core.model.UserProductTaskRecord;
import com.chunruo.core.service.UserProductTaskItemManager;
import com.chunruo.core.service.UserProductTaskRecordManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;

/**
 * 商品任务
 * @author Administrator
 *
 */
public class ProductTaskUtil {
	
	/**
	 * 获取商品对应的任务
	 * @param productId
	 * @return
	 */
	public static MsgModel<ProductTask> getProductTaskByProductId(Long productId){
		MsgModel<ProductTask> msgModel = new MsgModel<ProductTask>();
		try {
			ProductTaskListCacheManager productTaskListCacheManager = Constants.ctx.getBean(ProductTaskListCacheManager.class);
	
			//商品任务列表
			Map<String,ProductTask> productTaskMap = productTaskListCacheManager.getSession();
            if(productTaskMap != null && productTaskMap.size() > 0) {
            	for(Map.Entry<String,ProductTask> entry : productTaskMap.entrySet()) {
            		ProductTask productTask = entry.getValue();
            		//检查当前任务是否已开始
				    if(checkProductTaskIsStart(productTask) == 1
				    		&& StringUtil.compareObject(productTask.getProductId(), productId)) {
				            msgModel.setIsSucc(true);
				        	msgModel.setData(productTask);
				        	return msgModel;
				    }
            	}
            }
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	public static int checkProductTaskIsStart(ProductTask productTask) {
		//检查当前任务是否正在进行中
		int status = 0;  //默认已结束
		try {
			Long currentTime = DateUtil.getCurrentTime();
			Date beginTime = productTask.getBeginTime();
			Date endTime = productTask.getEndTime();
		    if(beginTime != null 
		    		&& endTime != null
		    		&& endTime.getTime() >= currentTime
		    		&& StringUtil.nullToBoolean(productTask.getIsEnable())) {
		    	if(endTime.getTime() > currentTime
			    		&& beginTime.getTime() < currentTime) {
		    		status = 1;  //进行中
		    	}else if(beginTime.getTime() < currentTime) {
		    		status = 2;  //未开始
		    	}
		    }
		}catch(Exception e) {
			e.printStackTrace();
		}
	    return status;
	}
	
	
	/**
	 * 检查用户商品任务完成情况
	 * @param userProductTaskMap
	 * @param productTask
	 * @return
	 */
	public static MsgModel<ProductTask> checkUserProductTask(Map<String, UserProductTaskRecord> userProductTaskMap,ProductTask productTask){
		MsgModel<ProductTask> msgModel = new MsgModel<ProductTask>();
		try {
			Long currentTime = DateUtil.getCurrentTime();
			Date beginTime = productTask.getBeginTime();
			Date endTime = productTask.getEndTime();
		    if(beginTime == null || endTime == null
		    		|| endTime.getTime() < currentTime
		    		|| !StringUtil.nullToBoolean(productTask.getIsEnable())) {
		    	//活动时间已结束，不显示
		    	msgModel.setIsSucc(false);
		    	msgModel.setMessage("该任务已结束");
		    	return msgModel;
		    }
		    
		    
		    Integer taskNumber = StringUtil.nullToInteger(productTask.getTaskNumber());            //任务数量
		    Integer reward = StringUtil.nullToInteger(productTask.getReward());                    //每组返利
		    Integer maxGroupNumber = StringUtil.nullToInteger(productTask.getMaxGroupNumber());    //最大组数
		    
		    //商品任务基础信息
		    Integer taskStatus = ProductTask.PRODUCT_TASK_STATUS_NOTSTART;
		    String  buttonTitle = "立即卖货赢奖励"; 
		    Integer headNumber = 0;
		    Integer tailNumber = taskNumber;
		    Integer salesNumber = 0;
		    Integer targetReward = reward;
		    String rewardTag = "";
		    String purchaseIntroduce = "";
		    Boolean isCompleted = false;
		    
		    if(beginTime.getTime() > currentTime) {
		    	//任务即将开始
		    	buttonTitle = String.format("%s 开始", DateUtil.formatDate(DateUtil.DATE_FORMAT_MONTH_DAY_MINUTE , beginTime));
		    	rewardTag = String.format("每卖%s件可得%s元奖励", taskNumber,reward);
		    }else if(endTime.getTime() > currentTime) {
		    	//任务进行中
		    	taskStatus = ProductTask.PRODUCT_TASK_STATUS_START;
//	    		buttonTitle = "立即卖货赢奖励";
	    		int nextNumber = taskNumber;
	    		rewardTag = String.format("再卖%s件可得%s元奖励", nextNumber,reward);
	    		
	    		if(userProductTaskMap != null && userProductTaskMap.size() > 0) {
	    			UserProductTaskRecord userProductTaskRecord = userProductTaskMap.get(StringUtil.null2Str(productTask.getTaskId()));
		    		if(userProductTaskRecord != null && userProductTaskRecord.getRecordId() != null) {
		    			//已购商品总数量
		    			salesNumber= StringUtil.nullToInteger(userProductTaskRecord.getTotalNumber());
		    		    
		    			if(salesNumber > 0) {
		    				//已购组数
			    			Integer groupNumber = salesNumber / taskNumber;
			    			if(StringUtil.nullToInteger(userProductTaskRecord.getGroupNumber()) != groupNumber) {
			    				//组数不想等，重新查找数据库
			    				MsgModel<UserProductTaskRecord> xsgModel = ProductTaskUtil.checkUserProductTaskItem(userProductTaskRecord,productTask);
			    			    if(!StringUtil.nullToBoolean(xsgModel.getIsSucc())) {
			    			    	msgModel.setIsSucc(false);
			    			    	msgModel.setMessage("该商品任务信息错误");
			    			    	return msgModel;
			    			    }
			    			    userProductTaskRecord = xsgModel.getData();
			    			    groupNumber = StringUtil.nullToInteger(userProductTaskRecord.getGroupNumber());
			    			    salesNumber = StringUtil.nullToInteger(userProductTaskRecord.getTotalNumber());
			    			}
		    				nextNumber = ((groupNumber+1) * taskNumber) - salesNumber;     //离达到下一组得数量
		    				int obtainReward = groupNumber * reward;                       //已获得总奖励
		    				int nextReward = (groupNumber+1) * reward;                     //达到下一组得总奖励
		    				headNumber = groupNumber * taskNumber;                         //进度条首节点
		    				tailNumber = (groupNumber + 1) * taskNumber;                   //进度条尾节点
		    				
		    				if(groupNumber >= maxGroupNumber) {
		    					//所有组数全部完成(任务已完成)
		    					tailNumber = headNumber;
		    					headNumber = headNumber - taskNumber;
		    					targetReward = obtainReward;
		    					salesNumber = tailNumber;
		    					buttonTitle = "任务已完成";
		    					rewardTag = "恭喜您已成功获取所有奖励";
		    					purchaseIntroduce = String.format("已销%s组获得全额奖励(元):%s", maxGroupNumber,obtainReward);
		    					isCompleted = true;
		    				}else {
		    					//任务未完成
		    					targetReward = nextReward;
		    					rewardTag = String.format("再卖%s件可得%s元奖励", nextNumber,reward);
		    					if(groupNumber > 0) {
			    					purchaseIntroduce = String.format("已销%s组获得奖励(元):%s", groupNumber,obtainReward);
		    					}
		    				}
		    				
		    				productTask.setObtainReward(obtainReward);
		    				productTask.setGroupNumber(groupNumber);
		    			}
		    		}
	    		}
	    		productTask.setNextNumber(nextNumber);
		    }
		    
		    //购买过但全部退款了
		    productTask.setIsCompleted(isCompleted);
			productTask.setTaskStatus(taskStatus);
	    	productTask.setButtonTitle(buttonTitle);
	        productTask.setHeadNumber(headNumber);
	        productTask.setTailNumber(tailNumber);
	        productTask.setSalesNumber(salesNumber);
	        productTask.setTargetReward(targetReward);
	        productTask.setRewardTag(rewardTag);
            productTask.setPurchaseIntroduce(purchaseIntroduce);
	        String rewardNotes = String.format("销量每满1组（1组%s件） 即可获得%s元奖励", taskNumber,reward);
		    productTask.setRewardNotes(rewardNotes);
		    productTask.setTaskRule(String.format("%s,最高上限%s组可获得全额%s元奖励", rewardNotes,maxGroupNumber,maxGroupNumber * reward));
		   
		    msgModel.setIsSucc(true);
		    msgModel.setData(productTask);
		    return msgModel;
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("服务器错误");
		return msgModel;
	}
	
	/**
	 * 计算该用户完成此任务商品数量
	 * @param userProductTaskRecord
	 * @param productTask
	 * @return
	 */
	public static MsgModel<UserProductTaskRecord> checkUserProductTaskItem(UserProductTaskRecord userProductTaskRecord,ProductTask productTask) {
		MsgModel<UserProductTaskRecord> msgModel = new MsgModel<UserProductTaskRecord>();
		try {
			UserProductTaskRecordManager userProductTaskRecordManager = Constants.ctx.getBean(UserProductTaskRecordManager.class);
			UserProductTaskItemManager userProductTaskItemManager = Constants.ctx.getBean(UserProductTaskItemManager.class);
			List<UserProductTaskItem> userProductTaskItemList = userProductTaskItemManager.getUserProductTaskItemListById(productTask.getTaskId(), userProductTaskRecord.getUserId());
			if(userProductTaskItemList != null && userProductTaskItemList.size() > 0) {
				int totalNumber = 0;
	        	List<Integer> statusList = new ArrayList<Integer>();
	        	statusList.add(UserProductTaskItem.USER_PRODUCT_TASK_STATUS_WAITTING); //结算中
	        	statusList.add(UserProductTaskItem.USER_PRODUCT_TASK_STATUS_SUCC);     //成功
	        	for(UserProductTaskItem item : userProductTaskItemList) {
	        		if(statusList.contains(StringUtil.nullToInteger(item.getStatus()))) {
	        			totalNumber += StringUtil.nullToInteger(item.getQuantity());
	        		}
	        	}
	        	//计算组数和返利
		    	int groupNumber = getGroupNumber(totalNumber,productTask.getTaskNumber(),productTask.getMaxGroupNumber());
		    	int totalReward = groupNumber * productTask.getReward();
		    	if(groupNumber >= 1) {
		    		userProductTaskRecord.setStatus(UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_WAIT);
		    	}
		    	userProductTaskRecord.setGroupNumber(groupNumber);
		    	userProductTaskRecord.setTotalNumber(totalNumber);
		    	userProductTaskRecord.setTotalReward(totalReward);
		    	userProductTaskRecord.setUpdateTime(DateUtil.getCurrentDate());
		    	userProductTaskRecordManager.save(userProductTaskRecord);
		    	msgModel.setIsSucc(true);
		    	msgModel.setData(userProductTaskRecord);
		    	return msgModel;
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("服务器错误");
		return msgModel;
	}
	
	/**
	 * 获取最大组数
	 * @param totalNumber
	 * @param taskNumber
	 * @param maxGropuNumber
	 * @return
	 */
	private static Integer getGroupNumber(Integer totalNumber, Integer taskNumber,Integer maxGropuNumber) {
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
}
