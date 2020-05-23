package com.chunruo.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductGroup;

@Repository("productGroupRepository")
public interface ProductGroupRepository extends GenericRepository<ProductGroup, Long> {
	
	@Query("from ProductGroup where productGroupId =:productGroupId")
	public List<ProductGroup> getProductGroupListByProductGroupId(@Param("productGroupId")Long productGroupId);
	
	@Query("from ProductGroup where productGroupId =:productGroupId and productId =:productId")
	public List<ProductGroup> getProductGroupListByProductGroupId(@Param("productGroupId")Long productGroupId, @Param("productId")Long productId);
}
