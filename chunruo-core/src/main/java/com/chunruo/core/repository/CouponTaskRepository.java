package com.chunruo.core.repository;

import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.CouponTask;

@Repository("couponTaskRepository")
public interface CouponTaskRepository extends GenericRepository<CouponTask, Long> {

}
