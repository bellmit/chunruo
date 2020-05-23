package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.RefundHistory;

public interface RefundHistoryManager extends GenericManager<RefundHistory, Long>{

	public List<RefundHistory> getRefundHistoryListByRefundId(Long refundId);
}
