package com.chunruo.core.repository;

import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserSaleStandard;

@Repository("userSaleStandardRepository")
public interface UserSaleStandardRepository extends GenericRepository<UserSaleStandard, Long> {

}
