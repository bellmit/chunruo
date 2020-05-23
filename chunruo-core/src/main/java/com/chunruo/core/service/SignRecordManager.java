package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.SignRecord;
import com.chunruo.core.model.UserInfo;

public interface SignRecordManager extends GenericManager<SignRecord, Long>{

	List<SignRecord> getSignRecordListByUserId(Long userId);
	
	void saveSignRecord(UserInfo userInfo,Integer isShare);

	List<SignRecord> getSignRecordListByUpdateTime(Date date);

}
