package com.chunruo.core.repository;

import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ExpressCode;

@Repository("expressCodeRepository")
public interface ExpressCodeRepository extends GenericRepository<ExpressCode, Long> {
}
