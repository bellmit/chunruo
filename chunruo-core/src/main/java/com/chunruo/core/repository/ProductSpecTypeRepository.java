package com.chunruo.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductSpecType;

@Repository("productSpecTypeRepository")
public interface ProductSpecTypeRepository extends GenericRepository<ProductSpecType, Long> {

	@Query("from ProductSpecType where productId =:productId order by sort")
	public List<ProductSpecType> getProductSpecTypeListByProductId(@Param("productId") Long productId);
}
