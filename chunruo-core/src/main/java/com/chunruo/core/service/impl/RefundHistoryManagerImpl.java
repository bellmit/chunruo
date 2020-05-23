package com.chunruo.core.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.RefundHistory;
import com.chunruo.core.repository.RefundHistoryRepository;
import com.chunruo.core.service.RefundHistoryManager;

@Transactional
@Component("refundHistoryManager")
public class RefundHistoryManagerImpl extends GenericManagerImpl<RefundHistory, Long> implements RefundHistoryManager {
	private RefundHistoryRepository refundHistoryRepository;

	@Autowired
	public RefundHistoryManagerImpl(RefundHistoryRepository refundHistoryRepository) {
		super(refundHistoryRepository);
		this.refundHistoryRepository = refundHistoryRepository;
	}

	@Override
	public List<RefundHistory> getRefundHistoryListByRefundId(Long refundId) {
		return this.refundHistoryRepository.getRefundHistoryListByRefundId(refundId);
	}
}
