package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductSeckill;

@Repository("productSeckillRepository")
public interface ProductSeckillRepository extends GenericRepository<ProductSeckill, Long> {

	@Query("from ProductSeckill where status =:status and (isDelete =false or isDelete is null) order by startTime desc")
	public List<ProductSeckill> getProductSeckillListByStatus(@Param("status")boolean status);
	
	@Query("from ProductSeckill where updateTime >:updateTime")
	public List<ProductSeckill> getProductSeckillByUpdateTime(@Param("updateTime")Date updateTime);
}
