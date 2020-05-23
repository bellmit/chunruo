package com.chunruo.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductSpecModel;

@Repository("productSpecModelRepository")
public interface ProductSpecModelRepository extends GenericRepository<ProductSpecModel, Long> {

	@Query("from ProductSpecModel order by sort")
	public List<ProductSpecModel> getProductSpecModelListBySort();
	
	@Query("from ProductSpecModel where name =:name")
	public List<ProductSpecModel> getProductSpecModelListByName(@Param("name")String name);
}
