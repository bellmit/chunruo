package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductCollection;

@Repository("productCollectionRepository")
public interface ProductCollectionRepository extends GenericRepository<ProductCollection, Long> {
	
	@Query("from ProductCollection where userId=:userId and status =:status order by updateTime desc")
	public List<ProductCollection> getProductCollectionListByUserId(@Param("userId")Long userId, @Param("status")boolean status);
	
	@Query("from ProductCollection where updateTime >:updateTime")
	public List<ProductCollection> getProductCollectionListByUpdateTime(@Param("updateTime") Date updateTime);

	@Query("from ProductCollection where userId=:userId and productId=:productId")
	public List<ProductCollection> getProductCollectionByProductId(@Param("productId")Long productId, @Param("userId")Long userId);
}
