package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductSpec;

@Repository("productSpecRepository")
public interface ProductSpecRepository extends GenericRepository<ProductSpec, Long> {

	@Query("from ProductSpec where productId =:productId order by sort")
	public List<ProductSpec> getProductSpecListByProductId(@Param("productId") Long productId);
	
	@Query("from ProductSpec where productId in (:productIdList) order by sort")
	public List<ProductSpec> getProductSpecListByProductIdList(@Param("productIdList") List<Long> productIdList);

	@Query("from ProductSpec where updateTime >:updateTime")
	public List<ProductSpec> getProductSpecByUpdateTime(@Param("updateTime")Date updateTime);

	@Modifying
	@Query("update ProductSpec set seckillTotalStock=:seckillTotalStock,updateTime=now() where productSpecId=:productSpecId")
	public void updateProductSeckillTotalNumber(@Param("productSpecId")Long productSpecId, @Param("seckillTotalStock")Integer seckillTotalStock);
}
