package com.chunruo.core.service;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.HotSaleRecord;

public interface HotSaleRecordManager extends GenericManager<HotSaleRecord, Long>{

	public void updateHotSaleRecordByLoadFunction();
}
