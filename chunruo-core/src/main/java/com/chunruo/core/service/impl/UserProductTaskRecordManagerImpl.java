package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserProductTaskRecord;
import com.chunruo.core.repository.UserProductTaskRecordRepository;
import com.chunruo.core.service.UserProductTaskRecordManager;
import com.chunruo.core.util.StringUtil;

@Component("userProductTaskRecordManager")
public class UserProductTaskRecordManagerImpl extends GenericManagerImpl<UserProductTaskRecord, Long> implements UserProductTaskRecordManager{
	private UserProductTaskRecordRepository userProductTaskRecordRepository;

	@Autowired
	public UserProductTaskRecordManagerImpl(UserProductTaskRecordRepository userProductTaskRecordRepository) {
		super(userProductTaskRecordRepository);
		this.userProductTaskRecordRepository = userProductTaskRecordRepository;
	}

	@Override
	public List<UserProductTaskRecord> getUserProductTaskRecordListByUserId(Long userId) {
		return this.userProductTaskRecordRepository.getUserProductTaskRecordListByUserId(userId);
	}

	@Override
	public List<UserProductTaskRecord> getUserProductTaskRecordListByUpdateTime(Date updateTime) {
		return this.userProductTaskRecordRepository.getUserProductTaskRecordListByUpdateTime(updateTime);
	}

	@Override
	public UserProductTaskRecord getUserProductTaskRecordById(Long userId, Long taskId) {
		return this.userProductTaskRecordRepository.getUserProductTaskRecordById(userId,taskId);
	}
	
	@Override
	public List<UserProductTaskRecord> getUserProductTaskRecordById(Long userId, List<Long> taskIdList){
		return this.userProductTaskRecordRepository.getUserProductTaskRecordById(userId, taskIdList);
	}

	@Override
	public void updateUserProductTaskStatusByLoadFunction() {
		this.userProductTaskRecordRepository.executeSqlFunction("{?=call updateUserProductTaskStatus_Fnc()}");
		log.debug("updateUserProductTaskStatusByLoadFunction======= ");
	}

	@Override
	public Double countUserTotalRewardByUserId(Long userId) {
		Double totalReward = 0.0D;
		String sql = "select sum(total_reward) from jkd_user_product_task_record where user_id = ? and status = ?";
		List<Object[]> objectList = this.querySql(sql, new Object[]{userId,UserProductTaskRecord.USER_PRODUCT_TASK_STATUS_AWARDED});
		if(objectList != null && objectList.size() > 0){
			totalReward = StringUtil.nullToDoubleFormat(objectList.get(0));
		}
		return totalReward;
	}

	@Override
	public List<UserProductTaskRecord> getUserProductTaskListByTaskIdList(List<Long> taskIdList) {
		return this.userProductTaskRecordRepository.getUserProductTaskListByTaskIdList(taskIdList);
	}
}
