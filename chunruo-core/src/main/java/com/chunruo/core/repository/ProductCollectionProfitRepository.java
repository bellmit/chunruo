package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductCollectionProfit;

@Repository("productCollectionProfitRepository")
public interface ProductCollectionProfitRepository extends GenericRepository<ProductCollectionProfit, Long> {
	
	@Query("from ProductCollectionProfit where userId=:userId and productId =:productId ")
	public List<ProductCollectionProfit> getCollectionProfitListByUserIdAndProductId(@Param("userId")Long userId, @Param("productId")Long productId);
	
	@Query("from ProductCollectionProfit where updateTime >:updateTime")
	public List<ProductCollectionProfit> getCollectionProfitListByUpdateTime(@Param("updateTime") Date updateTime);

	@Query("from ProductCollectionProfit where userId =:userId")
	public List<ProductCollectionProfit> getProductCollectionProfitListByUserId(@Param("userId")Long userId);
}
