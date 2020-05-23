package com.chunruo.core.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductShareRecord;

@Repository("productShareRecordRepository")
public interface ProductShareRecordRepository extends GenericRepository<ProductShareRecord, Long> {

	
	@Query("from ProductShareRecord where token=:token")
	public ProductShareRecord getProductShareRecordByToken(@Param("token")String token);

}
