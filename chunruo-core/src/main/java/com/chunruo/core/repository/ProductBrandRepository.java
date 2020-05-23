package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductBrand;

@Repository("productBrandRepository")
public interface ProductBrandRepository extends GenericRepository<ProductBrand, Long>{
	
	@Query("from ProductBrand where isHot =:isHot")
	public List<ProductBrand> getBrandListByIsHot(@Param("isHot") Boolean isHot);
	
	@Query("from ProductBrand where updateTime >:updateTime")
	public List<ProductBrand> getBrandListByUpdateTime(@Param("updateTime")Date updateTime);
	
}
