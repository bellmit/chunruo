package com.chunruo.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.RefundReason;
import com.chunruo.core.repository.RefundReasonRepository;
import com.chunruo.core.service.RefundReasonManager;

@Transactional
@Component("refundReasonManager")
public class RefundReasonManagerImpl extends GenericManagerImpl<RefundReason, Long> implements RefundReasonManager {
	private RefundReasonRepository refundReasonRepository;

	@Autowired
	public RefundReasonManagerImpl(RefundReasonRepository refundReasonRepository) {
		super(refundReasonRepository);
		this.refundReasonRepository = refundReasonRepository;
	}
}