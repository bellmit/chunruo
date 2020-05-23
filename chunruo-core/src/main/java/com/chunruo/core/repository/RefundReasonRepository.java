package com.chunruo.core.repository;

import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.RefundReason;

@Repository("refundReasonRepository")
public interface RefundReasonRepository extends GenericRepository<RefundReason, Long>{

}
