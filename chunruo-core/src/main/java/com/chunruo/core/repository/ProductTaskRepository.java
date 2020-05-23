package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductTask;

@Repository("productTaskRepository")
public interface ProductTaskRepository extends GenericRepository<ProductTask, Long> {

	@Query("from ProductTask where isEnable=:isEnable and endTime > now() order by taskId desc")
	List<ProductTask> getProductTaskListByIsEnable(@Param("isEnable")Boolean isEnable);

	@Query("from ProductTask where updateTime > :updateTime")
	List<ProductTask> getProductTaskListByUpdateTime(@Param("updateTime")Date updateTime);

	@Query("from ProductTask where productId = :productId")
	List<ProductTask> getProductTaskListByProductId(@Param("productId")Long productId);
}
