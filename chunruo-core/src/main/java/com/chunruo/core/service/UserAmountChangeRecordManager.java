package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserAmountChangeRecord;

public interface UserAmountChangeRecordManager extends GenericManager<UserAmountChangeRecord, Long>{

	public List<UserAmountChangeRecord> getUserAmountChangeRecordByObjectId(Long objectId, Integer type);
}
