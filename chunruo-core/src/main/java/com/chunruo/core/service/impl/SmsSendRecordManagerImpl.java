package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.SmsSendRecord;
import com.chunruo.core.repository.SmsSendRecordRepository;
import com.chunruo.core.service.SmsSendRecordManager;

@Transactional
@Component("smsSendRecordManager")
public class SmsSendRecordManagerImpl extends GenericManagerImpl<SmsSendRecord, Long> implements SmsSendRecordManager{
	private SmsSendRecordRepository smsSendRecordRepository;

	@Autowired
	public SmsSendRecordManagerImpl(SmsSendRecordRepository smsSendRecordRepository) {
		super(smsSendRecordRepository);
		this.smsSendRecordRepository = smsSendRecordRepository;
	}
}
