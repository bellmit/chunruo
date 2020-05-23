package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.HotSaleRecord;
import com.chunruo.core.repository.HotSaleRecordRepository;
import com.chunruo.core.service.HotSaleRecordManager;

@Transactional
@Component("hotSaleRecordManager")
public class HotSaleRecordManagerImpl extends GenericManagerImpl<HotSaleRecord, Long> implements HotSaleRecordManager {
	private HotSaleRecordRepository hotSaleRecordRepository;
	
	@Autowired
	public HotSaleRecordManagerImpl(HotSaleRecordRepository hotSaleRecordRepository) {
		super(hotSaleRecordRepository);
		this.hotSaleRecordRepository = hotSaleRecordRepository;
	}

	@Override
	public void updateHotSaleRecordByLoadFunction() {
		this.hotSaleRecordRepository.executeSqlFunction("{?=call loadProductHotSaleRecordList_Fnc()}");
		log.debug("updateHotSaleRecordByLoadFunction======= ");
	}
}
