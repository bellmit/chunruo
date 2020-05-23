package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductPromot;

@Repository("productPromotRepository")
public interface ProductPromotRepository extends GenericRepository<ProductPromot, Long> {
	
	@Query("from ProductPromot where status =:status and isDelete =:false")
	public List<ProductPromot> getProductPromotListByStatus(@Param("status")Boolean status);

	@Query("from ProductPromot where updateTime >:updateTime")
	public List<ProductPromot> getProductPromotByUpdateTime(@Param("updateTime")Date updateTime);
}
