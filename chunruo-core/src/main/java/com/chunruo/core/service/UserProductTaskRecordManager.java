package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserProductTaskRecord;

public interface UserProductTaskRecordManager extends GenericManager<UserProductTaskRecord, Long> {

	List<UserProductTaskRecord> getUserProductTaskRecordListByUserId(Long userId);

	List<UserProductTaskRecord> getUserProductTaskRecordListByUpdateTime(Date updateTime);

	UserProductTaskRecord getUserProductTaskRecordById(Long userId, Long taskId);
	
	List<UserProductTaskRecord> getUserProductTaskRecordById(Long userId, List<Long> taskIdList);

	void updateUserProductTaskStatusByLoadFunction();

	Double countUserTotalRewardByUserId(Long userId);

	List<UserProductTaskRecord> getUserProductTaskListByTaskIdList(List<Long> taskIdList);
	
}
