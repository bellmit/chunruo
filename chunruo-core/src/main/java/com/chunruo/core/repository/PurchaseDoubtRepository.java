package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.PurchaseDoubt;

@Repository("purchaseDoubtRepository")
public interface PurchaseDoubtRepository extends GenericRepository<PurchaseDoubt, Long> {

	@Query("from PurchaseDoubt where updateTime >:updateTime")
	List<PurchaseDoubt> getPurchaseDoubtListByUpdateTime(@Param("updateTime") Date updateTime);

}
